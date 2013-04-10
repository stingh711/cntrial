/**  
 * 系统参数相关操作(DAO层) 
 * @title SystemOptionDaoIbatis.java
 * @package com.unimedsci.pdms.systemoption.dao.ibatis 
 * @author zcj  
 * @create 2012-7-18 下午09:05:16
 * @version V1.0  
 */
package com.imedis.cntrial.dao.systemoption.mybatis;

import com.imedis.cntrial.model.systemoption.SystemOption;
import com.imedis.cntrial.util.DateUtil;
import com.imedis.cntrial.util.persistence.dao.ibatis.IBatisEntityDao;

import java.util.HashMap;
import java.util.List;

/**
 * 系统参数相关操作(DAO层)
 * @author zcj
 * @create 2012-7-18 下午09:05:16
 * @version 1.0
 */
public class SystemOptionDaoIbatis extends IBatisEntityDao<SystemOption> {
	/**
	 * 批量更新系统参数 
	 * @param systemOptions：系统参数集合
	 * @param operatorId：操作人标识
	 * @param operatorName：操作人姓名
	 * @throws Exception
	 * @author zcj
	 * @create 2012-7-18 下午09:30:28
	 */
	@SuppressWarnings("unchecked")
	public void updateSystemOptions(List<SystemOption> systemOptions, int operatorId,
			String operatorName) throws Exception {
		// 开始批处理  
//        getSqlMapClient().startBatch();
  
        for (SystemOption systemOption: systemOptions) {  
        	HashMap map = new HashMap();
        	map.put("systemOptionId", systemOption.getSystemOptionId());
        	map.put("content", systemOption.getContent());
        	map.put("lastModifierId", operatorId);
        	map.put("lastModifierName", operatorName);
        	map.put("lastModified", DateUtil.getCurrentTime());
        	
        	//更新操作
        	getSqlSession().update("com.unimedsci.pdms.systemoption.model.SystemOption.updateSystemOption", map);
        }  
        
        // 执行批处理  
//        getSqlMapClient().executeBatch();
	}
	
	/**
	 * 根据英文标识，获得系统参数信息 
	 * @param enName：英文标识
	 * @return 系统参数对象
	 * @throws Exception
	 * @author zhaochunjiao
	 * @create 2012-7-19 下午01:47:37
	 */
	@SuppressWarnings("unchecked")
	public SystemOption selectSystemOptionByEnName(String enName) throws Exception {
		HashMap map = new HashMap();
    	map.put("enName", enName);
		return (SystemOption)getSqlSession().selectOne("com.unimedsci.pdms.systemoption.model.SystemOption.selectSystemOptionByEnName", map);
	}
	
	/**
	 * 列出系统参数
	 * @return List<SystemOption> 系统参数集合
	 * @throws Exception
	 * @author zhaochunjiao
	 * @create 2012-7-19 下午01:52:59
	 */
	@SuppressWarnings("unchecked")
	public List<SystemOption> selectSystemOptions() throws Exception {
		return getSqlSession().selectList("com.unimedsci.pdms.systemoption.model.SystemOption.selectSystemOptions");
	}
}
