package cn.sheep.eagle.utils

import cn.sheep.eagle.config.ConfigHandler
import redis.clients.jedis.{JedisPool, JedisPoolConfig}

/**
  * author: old sheep
  * QQ: 64341393 
  * Created 2018/12/15
  */
object Jpools {

	private val jedisPoolConfig = new JedisPoolConfig()
	jedisPoolConfig.setMaxTotal(2000)
	jedisPoolConfig.setMaxIdle(200)
	jedisPoolConfig.setMinIdle(100)
	jedisPoolConfig.setTestOnBorrow(true)
	jedisPoolConfig.setTestOnReturn(true)
	jedisPoolConfig.setTestOnCreate(true)
	// TODO 度娘一下

	private val jedisPool = new JedisPool(jedisPoolConfig, ConfigHandler.redisHost, ConfigHandler.redisPort)

	/**
	  * 对外提供一个从池子中获取连接的方法
	  * @return
	  */
	def getJedis = jedisPool.getResource

}
