package com.casic.datadriver.controller.score;

import com.casic.datadriver.controller.AbstractController;
import com.casic.datadriver.model.coin.DdScoreInflow;
import com.casic.datadriver.service.score.DdScoreInflowService;

import com.hotent.core.annotion.Action;
import com.hotent.core.web.ResultMessage;
import com.hotent.core.web.query.QueryFilter;
import com.hotent.core.web.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * @Author: hollykunge
 * @Description: 积分赚取流水
 * @Date: 创建于 2018/9/21
 */

@Controller
@RequestMapping("/datadriver/inflow/")
public class ScoreInflowController extends AbstractController {

    @Autowired
    private DdScoreInflowService ddScoreInflowService;

    /**
     * 列表
     */
    @RequestMapping("list")
    @Action(description = "积分赚取列表")
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<DdScoreInflow> earnList = ddScoreInflowService.getAll(new QueryFilter(request, "scoreInflowItem"));
        return this.getAutoView().addObject("scoreInflowList", earnList);
    }

    /**
     * 流水列表批量删除
     */
    @RequestMapping("del")
    @Action(description = "流水列表删除")
    public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String preUrl = RequestUtil.getPrePage(request);
        ResultMessage message = null;
        try {
            Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
            ddScoreInflowService.delByIds(lAryId);
            message = new ResultMessage(ResultMessage.Success, "删除成功!");
        } catch (Exception ex) {
            message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
        }
        addMessage(message, request);
        response.sendRedirect(preUrl);
    }

    /**
     * 编辑个人流水
     */
    @RequestMapping("edit")
    @Action(description = "编辑个人流水")
    public ModelAndView edit(HttpServletRequest request) throws Exception {
        Long scoreInflowId = RequestUtil.getLong(request, "id");
        String returnUrl = RequestUtil.getPrePage(request);
        DdScoreInflow ddScoreInflow = ddScoreInflowService.getById(scoreInflowId);
        return getAutoView().addObject("bizDef", ddScoreInflow)
                .addObject("returnUrl", returnUrl);
    }

    /**
     * 提交编辑个人流水
     */
    @RequestMapping("save")
    @Action(description = "提交编辑个人流水")
    public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String resultMsg = null;

        Long scoreInflowId = RequestUtil.getLong(request, "id");
        Long scoreInflowUid = RequestUtil.getLong(request, "userId");
        Integer sourceScore = RequestUtil.getInt(request, "sourceScore");
        String sourceType = RequestUtil.getString(request, "sourceType");
        String sourceDetail = RequestUtil.getString(request, "sourceDetail");
        Long orgId = RequestUtil.getLong(request, "orgId");
        String orgName = RequestUtil.getString(request, "orgName");
        Long total = RequestUtil.getLong(request, "total");
        Long resourceId = RequestUtil.getLong(request, "resourceId");

        DdScoreInflow ddScoreInflow = new DdScoreInflow();

        ddScoreInflow.setId(scoreInflowId);
        ddScoreInflow.setUserId(scoreInflowUid);
        ddScoreInflow.setSourceScore(sourceScore);
        ddScoreInflow.setSourceType(sourceType);
        ddScoreInflow.setSourceDetail(sourceDetail);
        ddScoreInflow.setUpdTime(new Date());
        ddScoreInflow.setOrgId(orgId);
        ddScoreInflow.setOrgName(orgName);
        ddScoreInflow.setTotal(total);
        ddScoreInflow.setResourceId(resourceId);

        try {
            ddScoreInflowService.update(ddScoreInflow);
            resultMsg = getText("record.updated", "赚取的积分");
            writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);

        } catch (Exception e) {
            writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
        }

    }
}