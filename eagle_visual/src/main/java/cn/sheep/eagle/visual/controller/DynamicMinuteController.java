package cn.sheep.eagle.visual.controller;

import cn.sheep.eagle.visual.pojo.MinuteVo;
import cn.sheep.eagle.visual.service.IMinuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

/**
 * author: old sheep
 * qq: 64341393
 * Created 2018/12/16
 */
@RestController
public class DynamicMinuteController {

    @Autowired
    private IMinuteService minuteService;

    @GetMapping("showminute/{day}")
    public Object showMinuteData(@PathVariable String day) {

        Calendar calendar = Calendar.getInstance();
        String hour = calendar.get(Calendar.HOUR_OF_DAY) >= 10 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : "0" + calendar.get(Calendar.HOUR_OF_DAY);
        String minutes = calendar.get(Calendar.MINUTE) >= 10 ? calendar.get(Calendar.MINUTE) + "" : "0" + calendar.get(Calendar.MINUTE);

        // 查询数据
        MinuteVo minuteVo = minuteService.findCurrentDataBy(day, hour + minutes);
        return minuteVo;
    }

}
