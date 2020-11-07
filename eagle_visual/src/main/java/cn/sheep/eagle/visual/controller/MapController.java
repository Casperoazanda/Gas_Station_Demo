package cn.sheep.eagle.visual.controller;

import cn.sheep.eagle.visual.service.IMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** 全国充值数据分布的可视化
 * author: old sheep
 * qq: 64341393
 * Created 2018/12/16
 */
@RestController
public class MapController {

    @Autowired
    private IMapService mapService;

    @GetMapping("/showMap/{day}") //RESTful
    public Object show(@PathVariable String day) {
        // 查询redis的数据并返回
        return mapService.findMapDataBy(day);
    }


}
