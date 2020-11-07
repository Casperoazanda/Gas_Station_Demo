package cn.sheep.eagle.visual.dao.impl;

import cn.sheep.eagle.visual.dao.RedisDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * author: old sheep
 * qq: 64341393
 * Created 2018/12/16
 */
@Component
public class RedisDAOImpl implements RedisDAO {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Map<Object, Object> selectBy(String day) {
        return redisTemplate.opsForHash().entries(day);
    }

    @Override
    public Object selectBy(String day, String field) {
        return redisTemplate.opsForHash().get(day, field);
    }
}
