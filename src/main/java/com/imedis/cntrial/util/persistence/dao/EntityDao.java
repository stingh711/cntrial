package com.imedis.cntrial.util.persistence.dao;

import com.imedis.cntrial.util.exception.BaseException;

import java.io.Serializable;
import java.util.List;

/**
 * ��Ե���Entity����Ĳ�����.����5�ھ���ORMʵ�ַ���.
 *h
 * @author calvin
 */
public interface EntityDao<T> {

	T get(Serializable id) throws BaseException;

	List<T> getAll();

	void insert(Object o);
	void update(Object o);
	
	void saveOrUpdate(Object o) ;
	void remove(Object o);

	void removeById(Serializable id);

	/**
	 * ��ȡEntity����������.
	 */
	String getIdName(Class clazz);
}
