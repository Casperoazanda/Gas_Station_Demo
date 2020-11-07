package cn.sheep.eagle.utils

import cn.sheep.eagle.config.ConfigHandler
import cn.sheep.eagle.constant.EagleEnum
import com.alibaba.fastjson.JSONObject
import org.apache.spark.rdd.RDD

/**
  * author: old sheep
  * QQ: 64341393 
  * Created 2018/12/15
  */
object EagleKpi {
	/**
	  * 整理计算的基本数据 -- 整理一张宽表
	  * @param filtered
	  * @return
	  */
	def markBaseData(filtered: RDD[JSONObject]) = {
		// 统计全网的充值成功的订单量 reduceByKey => RDD[(K, V)]
		val markBaseData = filtered.map(obj => {
			val requestId = obj.getString("requestId")

			// 提取当前天
			val day = requestId.substring(0, 8)
			val hour = requestId.substring(8, 10)
			val minute = requestId.substring(10, 12)
			// 省份编码
			val provinceCode = obj.getString("provinceCode")

			// 充值发起的时间
			val startTime = requestId.substring(0, 17)

			// 关键业务指标
			val kpi = if (obj.getString("bussinessRst").equals("0000")) {
				val money = obj.getDouble("chargefee").toDouble
				// 获取充值的结束时间
				val endTime = obj.getString("receiveNotifyTime")
				(1, 1, money, CaculateTime(startTime, endTime))
			} else (1, 0, 0d, 0L)
			(day, hour, kpi, provinceCode, minute)
		}).cache()
		markBaseData
	}

	/**
	  * 每分钟的充值订单及金额统计
	  * @param markBaseData
	  */
	def cacuPerMinute(markBaseData: RDD[(String, String, (Int, Int, Double, Long), String, String)]) = {
		// 统计没分钟的充值成功订单及金额
		markBaseData.map(tp => ((tp._1, tp._4, tp._5), (tp._3._2, tp._3._3)))
			.reduceByKey((a, b) => (a._1 + b._1, a._2 + b._2))
			.foreachPartition(iter => {
				val jedis = Jpools.getJedis
				iter.foreach {
					case ((day, hour, minute), (succ, money)) => {
						jedis.hincrBy(EagleEnum.PROJECT.toString + ":" + day + "-m", "s-" + hour + minute, succ)
						jedis.hincrByFloat(EagleEnum.PROJECT.toString + ":" + day + "-m", "m-" + hour + minute, money)
					}
					case _ =>
				}
				jedis.close()
			})
	}

	/**
	  * 全国各省数据(充值成功的订单)分布情况统计
	  * @param markBaseData
	  */
	def cacuProvince(markBaseData: RDD[(String, String, (Int, Int, Double, Long), String, String)]) = {
		// 全国各省充值成功订单分布
		markBaseData.map(tp => ((tp._1, tp._4), tp._3._2)).reduceByKey(_ + _).foreachPartition(iter => {
			val jedis = Jpools.getJedis
			// 省份编码映射关系
			val pcn = ConfigHandler.provinceCodeName
			iter.foreach {
				case ((day, provinceCode), succ) => {
					jedis.hincrBy(EagleEnum.PROJECT.toString + ":" + day + "-p", pcn.get(provinceCode).toString, succ)
				}
				case _ =>
			}
			jedis.close()
		})
	}

	/**
	  * 业务概况--每小时的成功订单及成功率统计
	  * @param markBaseData
	  */
	def cacuGeneralHour(markBaseData: RDD[(String, String, (Int, Int, Double, Long), String, String)]) = {
		// 每小时的成功订单及成功率
		markBaseData.map(tp => ((tp._1, tp._2), (tp._3._1, tp._3._2))).reduceByKey((a, b) => {
			(a._1 + b._1, a._2 + b._2)
		}).foreachPartition(iter => {
			val jedis = Jpools.getJedis
			iter.foreach {
				case ((day, hour), (total, succ)) => {
					jedis.hincrBy(EagleEnum.PROJECT.toString + ":" + day, "total-" + hour, total)
					jedis.hincrBy(EagleEnum.PROJECT.toString + ":" + day, "succ-" + hour, succ)
				}
				case _ =>
			}
			jedis.close()
		})
	}

	/**
	  * 业务概况--每天的指标(总订单，成功订单，金额，平均时长)
	  * @param markBaseData
	  */
	def cacuGeneralDay(markBaseData: RDD[(String, String, (Int, Int, Double, Long), String, String)]) = {
		markBaseData.map(tp => (tp._1, tp._3)).reduceByKey((a, b) => {
			(a._1 + b._1, a._2 + b._2, a._3 + b._3, a._4 + b._4)
		}).foreachPartition(iter => {
			val jedis = Jpools.getJedis
			iter.foreach {
				case (day, (total, succ, money, time)) => {
					jedis.hincrBy(EagleEnum.PROJECT.toString + ":" + day, "total", total)
					jedis.hincrBy(EagleEnum.PROJECT.toString + ":" + day, "succ", succ)
					jedis.hincrByFloat(EagleEnum.PROJECT.toString + ":" + day, "money", money)
					jedis.hincrBy(EagleEnum.PROJECT.toString + ":" + day, "time", time)
				}
				case _ =>
			}
			jedis.close()
		})
	}
	
}
