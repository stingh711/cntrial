package com.imedis.cntrial.util.exception;

public class BaseException extends RuntimeException {
	public final static long serialVersionUID = 12345678;
	
	public static final int SYSTEM_NORMALEXCEPTION = 100;
	//ϵͳ����
	public static final int SYSTEM_ERROR	= -1;
	//��ݲ�����
	public static final int DATA_NOTFOUND = -2;
	//����Ѵ���
	public static final int DATA_EXIST = -3 ;
	
	public static final int DUPLICATE_ADVERTISEMENTLOCATION_NAME = -8; //���λ�������Ψһ�� 
	public static final int NOT_ALLOWED_ADVERTISEMENT_TYPE = -9; //���ǹ��λ����Ͷ�ŵĹ������
	
	public static final int LARGE_THAN_MAX_SHELL_EVERYDAY = -11; //����ֵ�������������ֵ
	
	//���������
	public static final int ALBUM_FULL =-4 ;
	
	//��������
	public static final int ALBUM_NUM_FULL =-5 ;
	
	private boolean debug = true;
	private String trace;
	private int code = SYSTEM_ERROR;

	public BaseException() {
		super();
		init();
	}

	public BaseException(String s) {
		super(s);
		init();
	}
	public BaseException(int code, String s) {
		super(s);
		this.code = code;
		init();
	}
	
	public int getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return (debug?trace:"") + super.getMessage();	
	}
	
	private void init() {
		StackTraceElement traces[] = getStackTrace();
		String className = traces[0].getClassName();
		int n = className.lastIndexOf('.');
		if(n > 0) className = className.substring(n + 1);
		trace = className + "." + traces[0].getMethodName() + "[line: " + traces[0].getLineNumber() + "]: ";
	}
}

