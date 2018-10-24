package com.casic.datadriver.controller.score;

import com.casic.datadriver.controller.AbstractController;
import com.casic.datadriver.model.coin.DdScoreOutflow;
import com.casic.datadriver.service.score.DdScoreOutflowService;
import com.hotent.core.annotion.Action;
import com.hotent.core.web.query.QueryFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: hollykunge
 * @Description: 积分消费记录
 * @Date: 创建于 2018/9/21
 */

@Controller
@RequestMapping("/datadriver/outflow/")
public class ScoreOutflowController extends AbstractController {

    @Resource
    private DdScoreOutflowService ddScoreOutflowService;

    /**
     * @throws Exception e
     */
    @RequestMapping("list")
    @Action(description = "积分消耗列表")
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<DdScoreOutflow> consumeList = ddScoreOutflowService.getAll(
                new QueryFilter(request,"scoreOutflowItem"));
        return this.getAutoView().addObject("scoreOutflowList",consumeList);
    }
}