package com.casic.datadriver.dao.score;

import com.casic.datadriver.model.coin.DdGoldenCoin;
import com.hotent.core.db.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: hollykunge
 * @Description:
 * @Date: 创建于 2018/9/27
 */

@Repository
public class DdGoldenCoinDao extends BaseDao<DdGoldenCoin> {

    @Override
    public Class getEntityClass() {
        return DdGoldenCoin.class;
    }

    /**
     * 增加使用add
     * 删除使用delById
     * 更新使用update
     * 查询使用getById, getAll
     */

    /**
     * 根据用户id获取所有币种
     */
    public List<DdGoldenCoin> getPersonal(Long uid) {
        return this.getBySqlKey("getPersonal", uid);
    }
}