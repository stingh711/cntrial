package com.imedis.cntrial.util;

public class Constants {
	/** The name of the ResourceBundle used in this application */
    public static final String BUNDLE_KEY = "ApplicationResources";
    
	/** 
     * Session scope attribute that holds the locale set by the user. By setting this key
     * to the same one that Struts uses, we get synchronization in Struts w/o having
     * to do extra work or have two session-level variables.
     */ 
    public static final String      PREFERRED_LOCALE_KEY = "org.apache.struts.action.LOCALE";
    
    public static final String		IMAGE_PATH = "/uploadfiles/images";
    public static final String		THUMBNAILS_PATH = "/uploadfiles/thumbnails";
    public static final String		SAVE_PATH = "/usr/local/conf/struts/";
    public static final String      IMAGE_UPLOAD_PATH = "/uploadfiles";//保存上传文件的目录，相对于Web应用程序的根路径
  
}
