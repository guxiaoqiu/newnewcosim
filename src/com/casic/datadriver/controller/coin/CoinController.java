package com.casic.datadriver.controller.coin;

import com.alibaba.fastjson.JSON;
import com.casic.datadriver.model.coin.AddScoreModel;
import com.casic.datadriver.model.coin.RankModel;
import com.casic.datadriver.model.data.OrderDataRelation;
import com.casic.datadriver.model.data.PrivateData;
import com.casic.datadriver.model.project.Project;
import com.casic.datadriver.model.task.TaskInfo;
import com.casic.datadriver.service.coin.CoinService;
import com.casic.datadriver.service.data.OrderDataRelationService;
import com.casic.datadriver.service.data.PrivateDataService;
import com.casic.datadriver.service.project.ProjectService;
import com.casic.datadriver.service.task.TaskInfoService;
import com.hotent.core.annotion.Action;
import com.hotent.core.web.controller.GenericController;
import com.hotent.core.web.ResultMessage;
import com.hotent.platform.auth.ISysUser;
import com.hotent.platform.service.system.SysUserService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 主要是积分部分的对外接口，可以提供丰富的积分和协同币展示方式
 *
 * @Author: hollykunge
 * @Date: 创建于 2018/9/20
 */
@Controller
@RequestMapping("/coin/")
public class CoinController extends GenericController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private PrivateDataService privateDataService;

    @Autowired
    private OrderDataRelationService orderDataRelationService;

    /**
     * 赚取积分接口，参数不能改
     *
     * @param uid          身份证号
     * @param sourceScore  分数
     * @param sourceType   一级类型
     * @param sourceDetail 二级类型
     * @param updTime      更新时间
     * @throws Exception the exception
     */
    @RequestMapping("add")
    @ResponseBody
    public void add(String uid, String sourceScore, String sourceType, String sourceDetail, String updTime, Long resourceId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String resultMsg = null;
        try {
            //TODO：传整个model
            AddScoreModel addScoreModel = new AddScoreModel();
            addScoreModel.setAccount(uid);
            addScoreModel.setSourceScore(sourceScore);
            addScoreModel.setSourceType(sourceType);
            addScoreModel.setSourceDetail(sourceDetail);
            addScoreModel.setUpdTime(new Date());
            addScoreModel.setResourceId(resourceId);
            resultMsg = coinService.addScore(addScoreModel);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result", resultMsg);
            //解决跨域
            String callback = request.getParameter("callback");
            response.getWriter().write(callback + "(" + jsonObject + ")");
        } catch (Exception e) {
            writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
        }
    }

    /**
     * 测试接口
     */
    @RequestMapping("test")
    @ResponseBody
    public void testGetUserInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        if (request.getHeader("clientip") != null) {
            jsonObject.put("clientip", request.getHeader("clientip"));
        }
        if (request.getHeader("username") != null) {
            jsonObject.put("username", request.getHeader("username"));
        }
        if (request.getHeader("password") != null) {
            jsonObject.put("password", request.getHeader("password"));
        }

        jsonObject.put("test", "test");
        try {
            // 解决跨域
            String callback = request.getParameter("callback");
            if (callback == null) {
                callback = "success";
            }
            response.getWriter().write("{" + callback + ":" + jsonObject + "}");
        } catch (Exception e) {
            writeResultMessage(response.getWriter(), "获取用户信息" + e.getMessage(), ResultMessage.Fail);
        }
    }

    /**
     * 获取个人所有详情
     *
     * @param response 响应
     * @throws Exception the exception
     */
    @RequestMapping("personalScore")
    @ResponseBody
    public void personalScore(String account, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONArray jsonR = null;
        try {
            Map<String, Integer> personalMap = coinService.getPersonal(account);
            jsonR = JSONArray.fromObject(personalMap);
            //解决跨域
            String callback = request.getParameter("callback");
            response.getWriter().write(callback + "(" + jsonR.toString() + ")");
        } catch (Exception e) {
            writeResultMessage(response.getWriter(), null + "," + e.getMessage(), ResultMessage.Fail);
        }
    }

    /**
     * @param response 响应
     * @throws Exception the exception
     */
    @RequestMapping("rank")
    @ResponseBody
    public void getRank(String sourceType, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONArray jsonR = null;
        try {
            List<RankModel> itemList = coinService.getRank(sourceType);
            jsonR = JSONArray.fromObject(itemList);
            //解决跨域
            String callback = request.getParameter("callback");
            response.getWriter().write(callback + "(" + jsonR.toString() + ")");
        } catch (Exception e) {
            writeResultMessage(response.getWriter(), null + "," + e.getMessage(), ResultMessage.Fail);
        }
    }

    /**
     * @param response 年度报告接口：项目数
     * @throws Exception the exception
     */
    @RequestMapping("task")
    @ResponseBody
    public void taskanddata(String uid, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONArray jsonR = null;
        try {
            ISysUser sysUser = sysUserService.getByAccount(uid);
            Long userId = sysUser.getUserId();
            if (userId != null && userId != 0) {
                List<Project> projectList = projectService.queryProjectBasicInfoList(userId);
                List<TaskInfo> taskListR = taskInfoService.getListByResponceId(userId);
                List<PrivateData> privateDataList = privateDataService.getDataByUserId(userId);
                Integer pubNum = 0;
                for (TaskInfo taskInfo : taskListR) {
                    List<OrderDataRelation> orderDataRelations = orderDataRelationService.getOrderDataRelationList(taskInfo.getDdTaskId());
                    pubNum = +orderDataRelations.size();
                }

                Integer projectNum = projectList.size();
                Integer taskNum = taskListR.size();
                Integer dataNum = privateDataList.size();

                HashMap<String, Object> temp = new HashMap<String, Object>();
                temp.put("projectNum", projectNum);
                temp.put("taskNum", taskNum);
                temp.put("dataNum", dataNum);
                temp.put("pubNum", pubNum);
                String jsonString = JSON.toJSONString(temp);
                response.getWriter().write(jsonString);
            }
        } catch (Exception e) {
            writeResultMessage(response.getWriter(), null + "," + e.getMessage(), ResultMessage.Fail);
        }
    }

}