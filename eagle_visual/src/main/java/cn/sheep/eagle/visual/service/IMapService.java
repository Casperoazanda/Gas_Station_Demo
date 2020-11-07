package cn.sheep.eagle.visual.service;

import cn.sheep.eagle.visual.pojo.MapVo;

import java.util.List;

/**
 * author: old sheep
 * qq: 64341393
 * Created 2018/12/16
 */
public interface IMapService {

    List<MapVo> findMapDataBy(String day);

}
