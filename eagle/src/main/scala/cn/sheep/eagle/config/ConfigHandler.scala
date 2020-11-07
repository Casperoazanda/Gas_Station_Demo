package cn.sheep.eagle.config

import java.util

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.JavaConverters._

/** 专门用来解析配置文件的
  * author: old sheep
  * QQ: 64341393 
  * Created 2018/12/15
  */
object ConfigHandler {

	// lazy 懒加载
	private lazy val applicationConf: Config = ConfigFactory.load()

	/* 解析配置文件中的主题 */
	private val _topics = applicationConf.getStringList("eagle.kafka.topics").asScala
	/* 解析brokers */
	private val _brokers = applicationConf.getString("eagle.kafka.brokers")
	/* 解析groupid */
	private val _groupId = applicationConf.getString("eagle.kafka.groupid")
	/* 解析是否自动提价参数 */
	private val _autoCommit = applicationConf.getBoolean("eagle.kafka.autocommit")

	/* 封装Kafka相关参数 */
	val kafkaParams = Map[String, Object](
		"bootstrap.servers" -> _brokers,
		"key.deserializer" -> classOf[StringDeserializer],
		"value.deserializer" -> classOf[StringDeserializer],
		"group.id" -> _groupId,
		"auto.offset.reset" -> "earliest",
		"enable.auto.commit" -> _autoCommit.asInstanceOf[java.lang.Boolean]
	)

	// 解析redis的配置
	private val _redis_host = applicationConf.getString("eagle.redis.host")
	private val _redis_port = applicationConf.getInt("eagle.redis.port")

	// 省份编码对照关系
	val provinceCodeName = applicationConf.getObject("pcode2pname").unwrapped()


	def topics = _topics
	def brokers = _brokers
	def groupId = _groupId
	def autoCommit = _autoCommit
	def redisHost = _redis_host
	def redisPort = _redis_port

}
