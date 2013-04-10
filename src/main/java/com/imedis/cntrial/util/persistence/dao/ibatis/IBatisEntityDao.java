package com.imedis.cntrial.util.persistence.dao.ibatis;



import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import com.imedis.cntrial.util.GenericsUtils;
import com.imedis.cntrial.util.persistence.dao.EntityDao;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * 负责为单个Entity 提供CRUD操作的IBatis DAO基类.
 * <p/>
 * 子类只要在类定义时指定所管理Entity的Class, 即拥有对单个Entity对象的CRUD操作.
 * <pre>
 * public class UserManagerIbatis extends IBatisEntityDao&lt;User&gt; {
 * }
 * </pre>
 *
 * @author suwei
 * @see IBatisGenericDao
 */
public class IBatisEntityDao<T> extends IBatisGenericDao implements EntityDao<T> {

	/**
	 * DAO所管理的Entity类型.
	 */
	protected Class<T> entityClass;

	protected String primaryKeyName;

	/**
	 * 在构造函数中将泛型T.class赋给entityClass.
	 */
	@SuppressWarnings("unchecked")
	public IBatisEntityDao() {
		entityClass = GenericsUtils.getSuperClassGenricType(getClass());
	}

	/**
	 * 根据属性名和属性值查询对象.
	 *
	 * @return 符合条件的对象列表
	 */
	public List<T> findBy(String name, Object value) {
		return findBy(getEntityClass(), name, value);
	}

	/**
	 * 根据属性名和属性值以Like AnyWhere方式查询对象.
	 */
	public List<T> findByLike(String name, String value) {
		return findByLike(getEntityClass(), name, value);
	}

	/**
	 * 根据属性名和属性值查询单个对象.
	 *
	 * @return 符合条件的唯一对象
	 */
	public T findUniqueBy(String name, Object value) {
		return findUniqueBy(getEntityClass(), name, value);
	}

	/**
	 * 根据ID获取对象.
	 */
	public T get(Serializable id) {
		return get(getEntityClass(), id);
	}

	/**
	 * 获取全部对象.
	 */
	public List<T> getAll() {
		return getAll(getEntityClass());
	}

	/**
	 * 取得entityClass.
	 * <p/>
	 * JDK1.4不支持泛型的子类可以抛开Class<T> entityClass,重载此函数达到相同效果。
	 */
	protected Class<T> getEntityClass() {
		return entityClass;
	}

	public String getIdName(Class clazz) {
		return "id";
	}

	public String getPrimaryKeyName() {
		if (StringUtils.isEmpty(primaryKeyName))
			primaryKeyName = "id";
		return primaryKeyName;
	}

	protected Object getPrimaryKeyValue(Object o) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		return PropertyUtils.getProperty(entityClass.newInstance(), getPrimaryKeyName());
	}

	

	/**
	 * 根据ID移除对象.
	 */
	public void removeById(Serializable id) {
		removeById(getEntityClass(), id);
	}

	/**
	 * 保存对象. 为了实现IEntityDao 我在内部使用了insert和upate 2个方法.
	 */
	public void saveOrUpdate(Object o) {
		Object primaryKey;
		try {
			primaryKey = getPrimaryKeyValue(o);
		} catch (Exception e) {
            primaryKey = null;
		}

		if (primaryKey == null)
			insert(o);
		else
			update(o);
	}

//    public void batchInsert( final String statementName, final List list) {
//        try {
//            if (list != null ) {
//                this.getSqlSession().execute( new SqlMapClientCallback() {
//                    public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
//                        executor.startBatch();
//                        for ( int i = 0, n = list.size(); i < n; i++) {
//                            executor.insert(statementName, list.get(i));
//                        }
//                        executor.executeBatch();
//                        return null ;
//                    }
//                });
//                this.getSqlSession().
//            }
//        } catch (Exception e) {
//            throw new ObjectRetrievalFailureException(entityClass, "batchInsert error: id [" + statementName + "], parameterObject [" + list + "].  Cause: " + e.getMessage());
//        }
//
//    }

	public void setPrimaryKeyName(String primaryKeyName) {
		this.primaryKeyName = primaryKeyName;
	}
}
