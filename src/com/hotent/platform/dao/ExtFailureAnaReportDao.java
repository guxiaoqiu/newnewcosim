package com.hotent.platform.dao;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;

import org.springframework.stereotype.Repository;
import com.hotent.core.bpm.model.ProcessCmd;
import com.hotent.core.util.UniqueIdUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Repository
public class ExtFailureAnaReportDao
{
	@Resource
	protected JdbcTemplate jdbcTemplate;
	
	/**
	 * 保存 故障分析报告 表单
	 * @param cmd
	 * @throws Exception
	 */
	public void add(ProcessCmd cmd) throws Exception
	{
		Map map= cmd.getFormDataMap();		
		Long id=UniqueIdUtil.genId();
		map.put("id", id);
		
		//这里冗余F_GUZHANGBIANHAO（故障报告编号也是流程主键）进来		
		String sql = "insert into w_gzfxbg(id,F_BIANHAO,F_GUZHANGBIANHAO,F_GZJIANMINGCHENG,F_GZJIANXINGHAO,F_FENXISHUOMING,F_GUZHANGFENLEI,F_ZERENBUMEN,F_ZERENBUMENID,F_CUOSHIJIANYI) "
				   + " values(:id,:bianhao,:guzhangbianhao,:gzjianmingcheng,:gzjianxinghao,:fenxishuoming,:guzhangfenlei,:zerenbumen,:zerenbumenID,:cuoshijianyi)";
		NamedParameterJdbcTemplate template=new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
		
		template.update(sql, map);
	}
	
	/**
	 * 获取 故障分析报告 数据
	 * @param cmd
	 * @return
	 */
	public Map getById(ProcessCmd cmd){
		try{
			//这里通过F_GUZHANGBIANHAO（流程主键）查询记录
			String sql="select a.* from w_gzfxbg a where F_GUZHANGBIANHAO="+cmd.getBusinessKey();
			Map map=jdbcTemplate.queryForMap(sql);
			return map;
		}
		catch(Exception ex){
			Map map=new HashMap();
			map.put("guzhangbianhao", cmd.getBusinessKey());
			return map;
		}
		
	}
	
	/**
	 * 更新 故障分析报告 数据
	 * @param cmd
	 * @throws Exception
	 */
	public void updById(ProcessCmd cmd) throws Exception
	{
//		Map map= cmd.getFormDataMap();
//		//通过F_GUZHANGBIANHAO（流程主键）更新数据
//		String sql="update w_gzfxbg set F_BIANHAO=:bianhao,F_BIANHAORIQI=:bianhaoriqi,F_GZJIANMINGCHENG=:gzjianmingcheng,F_GZJIANXINGHAO=:gzjianxinghao,F_FENXISHUOMING=:fenxishuoming,F_GUZHANGFENLEI=:guzhangfenlei,F_ZERENBUMEN=:zerenbumen,F_ZERENBUMENID=:zerenbumenID,F_CUOSHIJIANYI=:cuoshijianyi where F_GUZHANGBIANHAO=:guzhangbianhao";
//		NamedParameterJdbcTemplate template=new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
//		template.update(sql, map);
	}
}
