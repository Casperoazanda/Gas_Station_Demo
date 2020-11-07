package cn.sheep.eagle.visual.service.impl;

import cn.sheep.eagle.visual.dao.RedisDAO;
import cn.sheep.eagle.visual.pojo.MinuteVo;
import cn.sheep.eagle.visual.service.IMinuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: old sheep
 * qq: 64341393
 * Created 2018/12/16
 */
@Service
public class MinuteServiceImpl implements IMinuteService {

    @Autowired
    private RedisDAO redisDAO;

    @Override
    public MinuteVo findCurrentDataBy(String day, String minute) {
        Integer cnt = Integer.parseInt(redisDAO.selectBy(day, "s-" + minute).toString());
        Double money = Double.parseDouble(redisDAO.selectBy(day, "m-" + minute).toString());
        return new MinuteVo(cnt, money);
    }
}
