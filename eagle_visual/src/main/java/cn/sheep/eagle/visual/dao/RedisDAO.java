package cn.sheep.eagle.visual.dao;

import java.util.Map;

/**
 * author: old sheep
 * qq: 64341393
 * Created 2018/12/16
 */
public interface RedisDAO {
    /**
     * 通过day查询今天的所有数据
     * @param day
     */
    Map<Object, Object> selectBy(String day);

    /**
     * 通过key和 指定的field进行查询
     * @param day
     * @param field
     * @return
     */
    Object selectBy(String day, String field);

}
