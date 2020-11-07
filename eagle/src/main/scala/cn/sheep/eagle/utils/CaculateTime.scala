package cn.sheep.eagle.utils

import org.apache.commons.lang3.time.FastDateFormat

/**
  * author: old sheep
  * QQ: 64341393 
  * Created 2018/12/15
  */
object CaculateTime {

	/* 线程安全的 */
	private val dateFormat: FastDateFormat = FastDateFormat.getInstance("yyyyMMddHHmmssSSS")

	def apply(start: String, end: String) = {
		val startTime = dateFormat.parse(start).getTime
		val endTime = dateFormat.parse(end).getTime

		endTime - startTime
	}

}
