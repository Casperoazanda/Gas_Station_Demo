package cn.sheep.eagle.visual.service.impl;

import cn.sheep.eagle.visual.dao.RedisDAO;
import cn.sheep.eagle.visual.pojo.MapVo;
import cn.sheep.eagle.visual.service.IMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author: old sheep
 * qq: 64341393
 * Created 2018/12/16
 */
@Service
public class MapServiceImpl implements IMapService {

    @Autowired
    private RedisDAO redisDAO;

    @Override
    public List<MapVo> findMapDataBy(String day) {
        // 定义一个集合用来存放查到的数据
        List<MapVo> list = new ArrayList<>();

        // 调用dao层查询数据
        Map<Object, Object> map = redisDAO.selectBy(day);

        Set<Map.Entry<Object, Object>> entrySet = map.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            // 将查询到的数据封装并装到list中
            list.add(new MapVo(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString())));
        }

        return list;
    }
}
