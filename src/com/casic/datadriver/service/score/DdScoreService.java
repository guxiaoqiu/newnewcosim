package com.casic.datadriver.service.score;

import com.casic.datadriver.dao.score.DdScoreDao;
import com.casic.datadriver.model.coin.DdScore;
import com.casic.datadriver.model.coin.DdScoreInflow;
import com.casic.datadriver.model.coin.DdScoreOutflow;
import com.casic.datadriver.service.cache.Cache;
import com.hotent.core.db.IEntityDao;
import com.hotent.core.util.UniqueIdUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static com.casic.datadriver.manager.ScoreRegulation.CACHE_SCORE_PREFIX;

/**
 * @Author: hollykunge
 * @Date: 创建于 2018/9/20
 */

@Service
public class DdScoreService extends AbstractService<DdScore, Long> {

    private final Log logger = LogFactory.getLog(DdScoreService.class);

    private boolean isUseCache = false;

    @Resource
    private DdScoreDao ddScoreDao;

    @Resource
    private Cache cache;

    @Override
    protected IEntityDao<DdScore, Long> getEntityDao() {
        return this.ddScoreDao;
    }

    /**
     * 在程序启动时与其他缓存初始化一起调用
     */
    public void initScoreCache() {
        if (isUseCache) {
            List<DdScore> ddScoreList = ddScoreDao.getAll();
            for (DdScore ddScore : ddScoreList) {
                addToCache(ddScore);
            }
        }
    }

    /**
     * 为缓存增加一条
     */
    private void addToCache(DdScore ddScore) {
        Long userId = ddScore.getUserId();
        String type = ddScore.getScoreType();
        String cacheKey = CACHE_SCORE_PREFIX + userId + type;
        cache.add(cacheKey, ddScore);
    }

    /**
     * 通过一条输入或输出流水更新DdScore，默认在这之前inflow表和outflow表已经更新了
     *
     * @param ddScoreInflow  输入流水
     * @param ddScoreOutflow 输出流水
     */
    public Boolean updateScore(DdScoreInflow ddScoreInflow, DdScoreOutflow ddScoreOutflow) {
        DdScore ddScore = null;
        if (ddScoreInflow != null) {
            Long userId = ddScoreInflow.getUserId();
            String type = ddScoreInflow.getSourceType();
            String cacheKey = CACHE_SCORE_PREFIX + userId + type;
            if (isUseCache) {
                ddScore = (DdScore) cache.getByKey(cacheKey);
            } else {
                ddScore = ddScoreDao.getByUidAndType(userId, type);
            }
            //如果有就取出，没有就写数据库并写缓存
            if (ddScore != null) {
                //计算出
                Integer scoreTemp = ddScore.getScoreTotal() + ddScoreInflow.getSourceScore();
                ddScore.setUdpTime(ddScoreInflow.getUpdTime());
                ddScore.setScoreTotal(scoreTemp);
                //TODO:拿出来的对象操作后会自己写入缓存，新缓存框架是否可以？
                ddScoreDao.update(ddScore);
                if (isUseCache) {
                    logger.info("通过输入流水更新DdScore，更新缓存 " + cacheKey);
                } else {
                    logger.info("通过输入流水更新DdScore " + cacheKey);
                }
            } else {
                //生成积分统计对象
                DdScore ddScoreTemp = new DdScore();
                ddScoreTemp.setId(UniqueIdUtil.genId());
                ddScoreTemp.setUserId(userId);
                ddScoreTemp.setUserName(ddScoreInflow.getUserName());
                ddScoreTemp.setScoreTotal(ddScoreInflow.getSourceScore());
                ddScoreTemp.setScoreType(type);
                ddScoreTemp.setCrtTime(ddScoreInflow.getUpdTime());
                ddScoreTemp.setUdpTime(ddScoreInflow.getUpdTime());
                ddScoreTemp.setOrgId(ddScoreInflow.getOrgId());
                ddScoreTemp.setOrgName(ddScoreInflow.getOrgName());
                ddScoreDao.add(ddScoreTemp);
                if (isUseCache) {
                    cache.add(cacheKey, ddScoreTemp);
                    logger.info("通过输入流水添加DdScore，添加缓存 " + cacheKey);
                } else {
                    logger.info("通过输入流水添加DdScore " + cacheKey);
                }
            }
        }
        if (ddScoreOutflow != null) {
            Long userId = ddScoreOutflow.getUserId();
            String type = ddScoreOutflow.getSourceType();
            String cacheKey = CACHE_SCORE_PREFIX + userId + type;
            if (isUseCache) {
                ddScore = (DdScore) cache.getByKey(cacheKey);
            } else {
                ddScore = ddScoreDao.getByUidAndType(userId, type);
            }
            if (ddScore != null) {
                //计算出
                Integer scoreTemp = ddScore.getScoreTotal() - ddScoreOutflow.getExpendScore();
                if (scoreTemp < 0) {
                    logger.error("用户分数为负，输出流水失败 " + cacheKey);
                    return false;
                }
                ddScore.setUdpTime(ddScoreOutflow.getUdpTime());
                ddScore.setScoreTotal(scoreTemp);
                ddScoreDao.update(ddScore);
                logger.info("通过输出流水更新DdScore " + cacheKey);
            } else {
                logger.error("DdScore中没有该对象，输出流水失败 " + cacheKey);
                return false;
            }
        }
        return true;
    }

    /**
     * 重写增加
     */
    @Override
    public void add(DdScore ddScore) {
        super.add(ddScore);
        //多一个写缓存
        if (isUseCache) {
            addToCache(ddScore);
        }
    }

    /**
     * 重写删除
     */
    @Override
    public void delById(Long id) {
        //多一个删缓存
        if (isUseCache) {
            DdScore ddScore = getById(id);
            Long userId = ddScore.getUserId();
            String type = ddScore.getScoreType();
            String cacheKey = CACHE_SCORE_PREFIX + userId + type;
            cache.delByKey(cacheKey);
        }
        super.delById(id);
    }

    /**
     * 重写删一片
     */
    @Override
    public void delByIds(Long[] ids) {
        for (Long id : ids) {
            delById(id);
        }
    }

    /**
     * 删除某一类型所有的
     *
     * @param sourceType 一级类型
     */
    @SuppressWarnings("unchecked")
    public void delByType(String sourceType) {
        //删库
        ddScoreDao.delByType(sourceType);
        if (isUseCache) {
            //删缓存
            List<DdScore> ddScores = (List<DdScore>) cache.getByKeySection(CACHE_SCORE_PREFIX, sourceType);
            for (DdScore ddScore : ddScores) {
                Long userId = ddScore.getUserId();
                String cacheKey = CACHE_SCORE_PREFIX + userId + sourceType;
                cache.delByKey(cacheKey);
            }
        }
    }

    /**
     * 重写修改
     */
    @Override
    public void update(DdScore ddScore) {
        super.update(ddScore);
        //多一个更新缓存
        if (isUseCache) {
            addToCache(ddScore);
        }
    }

    /**
     * 重写查询，缓存中没有则在数据库查询
     * 这个方法尽量少用，因为通过id从缓存索引不高效
     */
    @Override
    @SuppressWarnings("unchecked")
    public DdScore getById(Long id) {
        if (isUseCache) {
            List<DdScore> ddScores = (List<DdScore>) cache.getByKeySection(
                    CACHE_SCORE_PREFIX, "");
            for (DdScore ddScore : ddScores) {
                if (ddScore.getId().equals(id)) {
                    return ddScore;
                }
            }
            logger.warn("DdScore缓存中没有该对象，查找失败 " + id);
        }
        return super.getById(id);
    }

    /**
     * 重写查询所有
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<DdScore> getAll() {
        if (isUseCache) {
            return (List<DdScore>) cache.getByKeySection(CACHE_SCORE_PREFIX, "");
        }
        return super.getAll();
    }

    /**
     * 查询个人
     *
     * @param userId 用户id
     * @return DdScore列表
     */
    @SuppressWarnings("unchecked")
    public List<DdScore> getPersonal(long userId) {
        if (isUseCache) {
            return (List<DdScore>) cache.getByKeySection(CACHE_SCORE_PREFIX, String.valueOf(userId));
        }
        return ddScoreDao.getPersonal(userId);
    }

    /**
     * 查询类型
     *
     * @param sourceType 一级类型
     */
    @SuppressWarnings("unchecked")
    private List<DdScore> getByType(String sourceType) {
        if (isUseCache) {
            return (List<DdScore>) cache.getByKeySection(CACHE_SCORE_PREFIX, sourceType);
        }
        return ddScoreDao.getByType(sourceType);
    }

    /**
     * 通过最低分限和积分类型获取积分列表
     *
     * @param least      最低分限
     * @param sourceType 一级类型
     * @return 符合条件的列表
     */
    List<DdScore> getScoresByLeastAndType(Integer least, String sourceType) {
        List<DdScore> ddScores = this.getByType(sourceType);
        Iterator<DdScore> it = ddScores.iterator();
        while (it.hasNext()) {
            DdScore x = it.next();
            if (x.getScoreTotal() < least) {
                it.remove();
            }
        }
        return ddScores;
    }

    /**
     * 通过排名区间和积分类型获取积分列表
     *
     * @param rank       名次
     * @param sourceType 一级类型
     * @return ddScoreList 排序完成
     */
    public List<DdScore> getScoresByRankAndType(Integer rank, String sourceType) {
        List<DdScore> ddScores = this.getByType(sourceType);
        Iterator<DdScore> it = ddScores.iterator();
        while (it.hasNext()) {
            DdScore x = it.next();
            if (0 == x.getScoreTotal()) {
                it.remove();
            }
        }
        //排序
        Collections.sort(ddScores, new Comparator<DdScore>() {
            @Override
            public int compare(DdScore ddScore1, DdScore ddScore2) {
                return ddScore2.getScoreTotal().compareTo(ddScore1.getScoreTotal());
            }
        });
        //根据排名数获取列表
        if (ddScores.size() > rank) {
            //符合条件的最后一名分数
            Integer base = ddScores.get(rank - 1).getScoreTotal();
            Iterator<DdScore> it2 = ddScores.iterator();
            while (it2.hasNext()) {
                DdScore x = it2.next();
                if (x.getScoreTotal() < base) {
                    it2.remove();
                }
            }
        }
        return ddScores;
    }
}