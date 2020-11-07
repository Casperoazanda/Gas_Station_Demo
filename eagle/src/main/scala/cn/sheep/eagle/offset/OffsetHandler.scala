package cn.sheep.eagle.offset

import cn.sheep.eagle.config.ConfigHandler
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange
import scalikejdbc.{DB, SQL}
import scalikejdbc.config._

/** 偏移量的操作维护
  * author: old sheep
  * QQ: 64341393
  * Created 2018/12/15
  */
object OffsetHandler {
	// 加载配置文件
	DBs.setup()

	/**
	  * 将偏移量存储到MySQL
	  */
	def saveOffset2MySQL(offsetRanges: Array[OffsetRange]) = {
		DB.localTx { implicit session =>
			val parameters: Seq[Seq[Any]] = offsetRanges.map(or => Seq(
				or.topic,
				or.partition,
				or.untilOffset,
				ConfigHandler.groupId)).toSeq
			SQL("replace into doit_streaming_02 values(?,?,?,?)").batch(parameters: _*).apply()
		}
	}

	/**
	  * 从数据库中获取偏移量信息并封装成
	  * Map[TopicPartition, Long]
	  */
	def obtainOffset4MySQL() = {
		DB.readOnly{ implicit session =>
			SQL("select * from doit_streaming_02 where groupId=? and topic=?")
    			.bind(ConfigHandler.groupId, ConfigHandler.topics.head)
    			.map(rs => (
					new TopicPartition(rs.string("topic"), rs.int("partitionId")),
					rs.long("untilOffset")
				)).list().apply()
		}.toMap
	}

}
