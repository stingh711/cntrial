package com.imedis.cntrial.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;


public class KeyGenerator
{
//	public static final String	url							= "c:/keyGenerator.properties";
	public static final String	url = "keyGenerator.properties";
	public static long			nodeId						= -1;
	protected static int		userId						= 0;
	protected static int		roleId						= 0;
	protected static int		logId						= 0;
	
	protected static long		MAX_ID_MASK					= 0xffffffffL;
	protected static long		MIN_ID_MASK					= 0;
	
	/* organization */
	protected static int organizationId =0;
	
	/* city */
	protected static int locationRegionId =0;
	protected static int cityId =0;
	protected static int provinceId =0;
	protected static int locationDistrictId =0;
	protected static int	locationRangeId	= 0;
	 
	/*systemoption*/
	protected static int systemOptionId = 0;
	
	protected static Connection	connection;
	
	
	static {
		InputStream inputStream = null;
		Properties property = new Properties();
		
//		File file = new File(url);
//		if(!file.exists())
//		{
//			KeyGenerator.nodeId = 1;
//		}
//		else
//		{
			try	{
				inputStream = KeyGenerator.class.getClassLoader().getResourceAsStream(url);
//				inputStream = new FileInputStream(url);
				property.load(inputStream);
			} catch (IOException e) {
				System.out.println(e.getMessage() + "  keyGenerator.properties 文件加载异常");
			} finally {
				try {
					inputStream.close();
				} catch (IOException e)	{
					e.printStackTrace();
				}
			}
			
			KeyGenerator.nodeId = Long.parseLong(property.getProperty("nodeId"));
			System.out.println("----------------------nodeId : -----------------------" + KeyGenerator.nodeId);
//		}
		
		String jdbcUrl = "";
		String jdbcUserName = "";
		String jdbcPassword = "";
		String jdbcDriverClassName = "";
		
		inputStream = KeyGenerator.class.getClassLoader().getResourceAsStream("jdbc.properties");
		property = new Properties();
		
		try	{
			property.load(inputStream);
			jdbcUrl = property.getProperty("jdbc.url");
			jdbcUserName = property.getProperty("jdbc.username");
			jdbcPassword = property.getProperty("jdbc.password");
			jdbcDriverClassName = property.getProperty("jdbc.driverClassName");
		} catch (IOException e) {
			System.out.println(e.getMessage() + "jdbc.properties 文件加载异常");
		} finally {
			try	{
				inputStream.close();
			} catch (IOException e)	{
				e.printStackTrace();
			}
		}
		
		try	{
			Class.forName(jdbcDriverClassName).newInstance();
			connection = java.sql.DriverManager.getConnection(jdbcUrl, jdbcUserName, jdbcPassword);
			KeyGenerator.init();
		} catch (Exception e) {
		} finally {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public synchronized static int getNextUserId()	{
		KeyGenerator.userId += 1;
		return KeyGenerator.userId;
	}
	public synchronized static int getNextLogId()	{
		KeyGenerator.logId += 1;
		return KeyGenerator.logId;
	}
	public synchronized static int getNextRoleId()	{
		KeyGenerator.roleId += 1;
		return KeyGenerator.roleId;
	}
	public synchronized static int getNextOrganizationId()	{
		KeyGenerator.organizationId += 1;
		return KeyGenerator.organizationId;
	}
	public synchronized static int getNextLocationRegionId()
	{
		KeyGenerator.locationRegionId+=1;
		return KeyGenerator.locationRegionId;
	}
	public synchronized static int getNextCityId()
	{
		KeyGenerator.cityId+=1;
		return KeyGenerator.cityId;
	}
	public synchronized static int getNextProvinceId()
	{
		KeyGenerator.provinceId+=1;
		return KeyGenerator.provinceId;
	}
	public synchronized static int getNextLocationDistrictId()
	{
		KeyGenerator.locationDistrictId+=1;
		return KeyGenerator.locationDistrictId;
	}
	public synchronized static int getNextLocationRangeId() {
		KeyGenerator.locationRangeId += 1;
		return KeyGenerator.locationRangeId;
	}
	public synchronized static int getNextSystemOptionId() {
		KeyGenerator.systemOptionId += 1;
		return KeyGenerator.systemOptionId;
	}
	
	public static void init()
	{	
		KeyGenerator.userId = getNextIntId("hf_user", "userId");
		KeyGenerator.roleId = getNextIntId("hf_role", "roleId");
		KeyGenerator.organizationId = getNextIntId("hf_organization", "organizationId");
		KeyGenerator.logId = getNextIntId("hf_log", "logId");
		KeyGenerator.locationRegionId = getNextIntId("hf_location_region","locationRegionId");
		KeyGenerator.cityId = getNextIntId("hf_city","cityId");
		KeyGenerator.provinceId = getNextIntId("hf_province","provinceId");
		KeyGenerator.locationDistrictId = getNextIntId("hf_location_district","locationDistrictId");
		KeyGenerator.locationRangeId  = getNextIntId("hf_location_range","locationRangeId");
		KeyGenerator.systemOptionId  = getNextIntId("hf_system_option","systemOptionId");
	}

	private synchronized static long getNextLongId(String tableName, String columnName) {
		long nodeIdMask = nodeId << 32;
		long maxId = MAX_ID_MASK | nodeIdMask;
		long minId = nodeIdMask;
		long id = nodeIdMask;
		if(nodeId > 0 && connection != null) {
			/*
			 * select max(memberId) as id from Member where memberId>minId and
			 * meberId< maxId
			 */
			String sql = "select max(" + columnName + ") as id  from " + tableName + " where " + columnName + ">" + minId + " and " + columnName + " < " + maxId;
			Statement statement = null;
			ResultSet resultSet = null;
			
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				while (resultSet.next()) {
					id = resultSet.getLong("id");
				}
			} catch (Exception e) {
			} finally {
				try {
					if(resultSet != null) {
						resultSet.close();
					}
					if(statement != null) {
						statement.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		if(id == 0)	{
			id = nodeIdMask;
		}
		System.out.println("------" + tableName + "-----:-----" + columnName + "-----:----" + id);
		return id;
	}
	
	private synchronized static int getNextIntId(String tableName, String columnName) {
		int id = -1;
		
		String sql = "select max(" + columnName + ") as id  from " + tableName;
		Statement statement = null;
		ResultSet resultSet = null;
		
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				id = resultSet.getInt("id");
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if(resultSet != null) {
					resultSet.close();
				}
				
				if(statement != null) {
					statement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return id;
	}
	
	public static void main(String[] args)
	{
		System.out.println();
	}


}