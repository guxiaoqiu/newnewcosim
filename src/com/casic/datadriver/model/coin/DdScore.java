package com.casic.datadriver.model.coin;

import com.hotent.core.model.BaseModel;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

public class DdScore extends BaseModel {

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 积分总量
     */
    private Integer scoreTotal;

    /**
     * 创建时间
     */
    private String crtTime;

    /**
     * 更新时间
     */
    private String udpTime;

    /**
     * 积分类型
     */
    private String scoreType;

    /**
     * 积分产生动作
     */
    private String scoreAction;
    /**
     * 组织名称
     */
    private String orgName;
    /**
     * 组织ID
     */
    private Long orgId;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户id
     *
     * @return userId - 用户id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    /**
     * 获取积分总量
     *
     * @return score_total - 积分总量
     */
    public Integer getScoreTotal() {
        return scoreTotal;
    }

    /**
     * 设置积分总量
     *
     * @param scoreTotal 积分总量
     */
    public void setScoreTotal(Integer scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    /**
     * 获取创建时间
     *
     * @return crt_time - 创建时间
     */
    public String getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建时间
     *
     * @param crtTime 创建时间
     */
    public void setCrtTime(String crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * 获取更新时间
     *
     * @return udp_time - 更新时间
     */
    public String getUdpTime() {
        return udpTime;
    }

    /**
     * 设置更新时间
     *
     * @param udpTime 更新时间
     */
    public void setUdpTime(String udpTime) {
        this.udpTime = udpTime;
    }

    /**
     * 获取积分类型
     *
     * @return score_type - 积分类型
     */
    public String getScoreType() {
        return scoreType;
    }

    /**
     * 设置积分类型
     *
     * @param scoreType 积分类型
     */
    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    /**
     * 获取积分产生动作
     *
     * @return score_action - 积分产生动作
     */
    public String getScoreAction() {
        return scoreAction;
    }

    /**
     * 设置积分产生动作
     *
     * @param scoreAction 积分产生动作
     */
    public void setScoreAction(String scoreAction) {
        this.scoreAction = scoreAction;
    }

    @Override
    public String toString() {
        return "DdScore{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", scoreTotal=" + scoreTotal +
                ", crtTime='" + crtTime + '\'' +
                ", udpTime='" + udpTime + '\'' +
                ", scoreType='" + scoreType + '\'' +
                ", scoreAction='" + scoreAction + '\'' +
                ", orgName='" + orgName + '\'' +
                ", orgId=" + orgId +
                '}';
    }
}