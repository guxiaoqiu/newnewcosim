package com.casic.datadriver.dao.score;

import com.casic.datadriver.model.coin.DdScore;
import com.hotent.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: hollykunge
 * @Description:
 * @Date: 创建于 2018/9/19
 */

@Repository
public class DdScoreDao extends BaseDao<DdScore> {
    @Override
    public Class getEntityClass() {return DdScore.class; }

    /**
     * 更新积分账户
     * @param ddScore the query filter
     */
    public void updateScore(DdScore ddScore) {
        this.update(ddScore);
    }
    /**
     * 插入
     */
    public void insert() {
    }
    /**
     * 删除
     */
    public void delete(){
    }
    /**
     * @param id id
     */
    public List<DdScore> getById(long id) {
        return this.getBySqlKey("getById", id);
    }
    /**
     * @param userId userId
     */
    public List<DdScore> getPersonal(long userId) {
        return this.getBySqlKey("getPersonal", userId);
    }

    public List<DdScore> getType(String sourceType) {
        return this.getBySqlKey("getType", sourceType);
    }

}
