package cn.sheep.eagle

import cn.sheep.eagle.config.ConfigHandler
import cn.sheep.eagle.constant.EagleEnum
import cn.sheep.eagle.offset.OffsetHandler
import cn.sheep.eagle.utils.{CaculateTime, EagleKpi, Jpools}
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}

/** 项目的启动类(主类)
  * author: old sheep
  * QQ: 64341393 
  * Created 2018/12/15
  */
object EagleAppcatition {

	def main(args: Array[String]): Unit = {
		val sparkConf = new SparkConf()
			.setMaster("local[*]")
			.setAppName("实时数据分析-Eagle")
			.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
			.set("spark.streaming.stopGracefullyOnShutdown", "true")
			.set("spark.streaming.kafka.maxRatePerPartition", "10000")
		val ssc = new StreamingContext(sparkConf, Seconds(1))

		// 从kafka获取数据
		val stream = if (!ConfigHandler.autoCommit) { // 配置文件中如果设置了不自定提交偏移量
			KafkaUtils.createDirectStream(ssc,
				LocationStrategies.PreferConsistent,
				ConsumerStrategies.Subscribe[String, String](
					ConfigHandler.topics,
					ConfigHandler.kafkaParams,
					// 从数据库中获取偏移量
					OffsetHandler.obtainOffset4MySQL()
				)
			)
		} else { // 如果设置了自动提交，就没有必要去数据库中查询了
			KafkaUtils.createDirectStream(ssc,
				LocationStrategies.PreferConsistent,
				ConsumerStrategies.Subscribe[String, String](ConfigHandler.topics, ConfigHandler.kafkaParams)
			)
		}

		stream.foreachRDD(rdd => {
			// 获取偏移量
			val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

			// 业务逻辑的计算
			val filtered: RDD[JSONObject] = rdd.map(crd => JSON.parseObject(crd.value()))
				.filter(_.getString(EagleEnum.SERVICE.toString).equals(EagleEnum.RCNR.toString))

			/* 整理数据 */
			val markBaseData = EagleKpi.markBaseData(filtered)

			/* 计算关键的业务kpi */
			EagleKpi.cacuGeneralDay(markBaseData)
			EagleKpi.cacuGeneralHour(markBaseData)
			EagleKpi.cacuProvince(markBaseData)
			EagleKpi.cacuPerMinute(markBaseData)


			if (!ConfigHandler.autoCommit) {
				// 存储当前批次的偏移量到MySQL
				OffsetHandler.saveOffset2MySQL(offsetRanges)
			}/* else {
				// 自定提交便宜到kafka => __consumer_offsets
				stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
			}*/

			// 释放一下缓存
			markBaseData.unpersist()
		})

		ssc.start()
		ssc.awaitTermination()
	}


}
