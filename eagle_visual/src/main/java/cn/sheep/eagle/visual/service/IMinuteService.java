package cn.sheep.eagle.visual.service;

import cn.sheep.eagle.visual.pojo.MinuteVo;

/**
 * author: old sheep
 * qq: 64341393
 * Created 2018/12/16
 */
public interface IMinuteService {

    /**
     * 查询当前分钟的数据
     * @param minute
     * @return
     */
    MinuteVo findCurrentDataBy(String day, String minute);
}
