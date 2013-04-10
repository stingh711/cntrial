package com.imedis.cntrial.util.persistence.dao.ibatis;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.imedis.cntrial.common.DataPage;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * IBatis Dao�ķ��ͻ���. <p/>
 * �̳���Spring��SqlMapClientDaoSupport,�ṩ��ҳ�������ɱ�ݲ�ѯ���������Է���ֵ���˷�������ת��.
 * 
 * @author suwei
 */
@SuppressWarnings("unchecked" )
public class IBatisGenericDao extends SqlSessionDaoSupport
{
    private static final Logger logger = LoggerFactory.getLogger(IBatisGenericDao.class);
	
	public static final String	POSTFIX_INSERT				= ".insert";
	
	public static final String	POSTFIX_UPDATE				= ".update";
	
	public static final String	POSTFIX_DELETE				= ".delete";
	
	public static final String	POSTFIX_DELETE_PRIAMARYKEY	= ".deleteByPrimaryKey";
	
	public static final String	POSTFIX_SELECT				= ".select";
	
	public static final String	POSTFIX_SELECTMAP			= ".selectByMap";
	
	public static final String	POSTFIX_SELECTSQL			= ".selectBySql";
	
	public static final String	POSTFIX_COUNT				= ".count";
	
	/**
	 * ���ID��ȡ����
	 */
	public <T> T get(Class<T> entityClass, Serializable id)
	{
		
        T o = (T) getSqlSession().selectOne(entityClass.getName() + POSTFIX_SELECT, id);
		return o;
	}
	
	/**
	 * ��ȡȫ������
	 */
	public <T> List<T> getAll(Class<T> entityClass)
	{
        return getSqlSession().selectList(entityClass.getName() + POSTFIX_SELECT);
	}
	
	/**
	 * �������
	 */
	public void insert(Object o)
	{
        getSqlSession().insert(o.getClass().getName() + POSTFIX_INSERT, o);
	}
	
	/**
	 * �������
	 */
	public void update(Object o)
	{
		getSqlSession().update(o.getClass().getName() + POSTFIX_UPDATE, o);
	}
	
	/**
	 * ɾ�����
	 */
	public void remove(Object o)
	{
		getSqlSession().delete(o.getClass().getName() + POSTFIX_DELETE, o);
	}
	
	/**
	 * ���IDɾ�����
	 */
	public <T> void removeById(Class<T> entityClass, Serializable id)
	{
		getSqlSession().delete(entityClass.getName() + POSTFIX_DELETE_PRIAMARYKEY, id);
	}
	
	/**
	 * map��ѯ.
	 * 
	 * @param map
	 *            ��������ԵĲ�ѯ
	 */
	public <T> List<T> find(Class<T> entityClass, Map map)
	{
		if(map == null)
			return getSqlSession().selectList(entityClass.getName() + POSTFIX_SELECT);
		else
		{
			map.put("findBy", "True");
			return getSqlSession().selectList(entityClass.getName() + POSTFIX_SELECTMAP, map);
		}
	}
	
	/**
	 * sql ��ѯ.
	 * 
	 * @param sql
	 *            ֱ��sql�����(��Ҫ��ֹע��ʽ����)
	 */
	public <T> List<T> find(Class<T> entityClass, String sql)
	{
		Assert.hasText(sql);
		if(StringUtils.isEmpty(sql))
			return this.getSqlSession().selectList(entityClass.getName() + POSTFIX_SELECT, null);
		else
			return this.getSqlSession().selectList(entityClass.getName() + POSTFIX_SELECTSQL, sql);
	}
	
	/**
	 * ��������������ֵ��ѯ����.
	 * 
	 * @return �������Ķ����б�
	 */
	public <T> List<T> findBy(Class<T> entityClass, String name, Object value)
	{
		Assert.hasText(name);
		Map map = new HashMap();
		map.put(name, value);
		return find(entityClass, map);
	}
	
	/**
	 * ��������������ֵ��ѯ����.
	 * 
	 * @return ��������Ψһ����
	 */
	public <T> T findUniqueBy(Class<T> entityClass, String name, Object value)
	{
		Assert.hasText(name);
		Map map = new HashMap();
		try
		{
			PropertyUtils.getProperty(entityClass.newInstance(), name);
			map.put(name, value);
			map.put("findUniqueBy", "True");
			return (T) getSqlSession().selectOne(entityClass.getName() + POSTFIX_SELECTMAP, map);
		}
		catch (Exception e)
		{
			logger.error("Error when propertie on entity," + e.getMessage(), e.getCause());
			return null;
		}
		
	}
	
	/**
	 * ��������������ֵ��Like AnyWhere��ʽ��ѯ����.
	 */
	public <T> List<T> findByLike(Class<T> entityClass, String name, String value)
	{
		Assert.hasText(name);
		Map map = new HashMap();
		map.put(name, value);
		map.put("findLikeBy", "True");
		return getSqlSession().selectList(entityClass.getName() + POSTFIX_SELECTMAP, map);
		
	}
	
	/**
	 * �ж϶���ĳЩ���Ե�ֵ����ݿ��в������ظ�
	 * 
	 * @param tableName
	 *            ��ݱ�����
	 * @param names
	 *            ��POJO�ﲻ���ظ��������б�,�Զ��ŷָ� ��"name,loginid,password" FIXME how about
	 *            in different schema?
	 */
	public boolean isNotUnique(Object entity, String tableName, String names)
	{
		try
		{
			String primarykey;
			Connection con = getSqlSession().getConnection();
			ResultSet dbMetaData = con.getMetaData().getPrimaryKeys(con.getCatalog(), null, tableName);
			dbMetaData.next();
			if(dbMetaData.getRow() > 0)
			{
				primarykey = dbMetaData.getString(4);
				if(names.indexOf(primarykey) > -1)
					return false;
			}
			else
			{
				return true;
			}
			
		}
		catch (SQLException e)
		{
			logger.error(e.getMessage(), e);
			return false;
		}
		return false;
	}
	
	/**
	 * ��ҳ��ѯ����ʹ��PaginatedList.
	 * 
	 * @param pageNo
	 *            ҳ��,��0��ʼ.
	 * @throws java.sql.SQLException
	 */
	public DataPage pagedQuery(String sqlName, HashMap hashMap,Integer pageNo, Integer pageSize) throws SQLException
	{
	
		if(pageNo == null || pageSize == null)
		{
			List list = getSqlSession().selectList(sqlName, hashMap);
			if(list == null || list.size() == 0)
			{
				return new DataPage();
			}
			else
			{
				return new DataPage(0, list.size(), list.size(), list);
			}
		}
		else
		{
			Assert.hasText(sqlName);
			Assert.isTrue(pageNo >= 1, "pageNo should start from 1");
			// Count��ѯ
			Integer totalCount = (Integer) getSqlSession().selectOne(sqlName + ".Count", hashMap);
			
			if(totalCount < 1)
			{
				return new DataPage();
			}
			
			// ʵ�ʲ�ѯ���ط�ҳ����
			int startIndex = DataPage.getStartOfPage(pageNo, pageSize);
			hashMap.put("startIndex", startIndex);
			hashMap.put("pageSize", pageSize);
			List list = getSqlSession().selectList(sqlName, hashMap);
			
			return new DataPage(startIndex, totalCount, pageSize, list);
		}
	}
	
	public String getMappedSQL(String sqlName)
	{
        return getMappedSQL(sqlName);
	}
	
	private static String removeOrders(String hql)
	{
		Assert.hasText(hql);
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find())
		{
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}
}
