package com.imedis.cntrial.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.regex.Pattern;


/**
 * String Utility Class This is used to encode passwords programmatically
 *
 * <p>
 * <a h
 * ref="StringUtil.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class StringUtil {
    //~ Static fields/initializers =============================================

    private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);
    //~ Methods ================================================================

    /**
     * Encode a string using algorithm specified in web.xml and return the
     * resulting encrypted password. If exception, the plain credentials
     * string is returned
     *
     * @param password Password or other credentials to use in authenticating
     *        this username
     * @param algorithm Algorithm used to do the digest
     *
     * @return encypted password based on the algorithm.
     */
    public static String encodePassword(String password, String algorithm) {
        byte[] unencodedPassword = password.getBytes();

        MessageDigest md = null;

        try {
            // first create an instance, given the provider
            md = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            logger.error("Exception: " + e);

            return password;
        }

        md.reset();

        // call the update method one or more times
        // (useful when you don't know the size of your data, eg. stream)
        md.update(unencodedPassword);

        // now calculate the hash
        byte[] encodedPassword = md.digest();

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < encodedPassword.length; i++) {
            if ((encodedPassword[i] & 0xff) < 0x10) {
                buf.append("0");
            }

            buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
        }

        return buf.toString();
    }

    /**
     * Encode a string using Base64 encoding. Used when storing passwords
     * as cookies.
     *
     * This is weak encoding in that anyone can use the decodeString
     * routine to reverse the encoding.
     *
     * @param str
     * @return String
     */
    public static String encodeString(String str)  {
//        sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
//        return encoder.encodeBuffer(str.getBytes()).trim();
    	return str;
    }

    /**
     * Decode a string using Base64 encoding.
     *
     * @param str
     * @return String
     */
    public static String decodeString(String str) {
//        sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
//        try {
//            return new String(dec.decodeBuffer(str));
//        } catch (IOException io) {
//        	throw new RuntimeException(io.getMessage(), io.getCause());
//        }
    	return str;
    }
    
    public static String replaceString(String str){
    	str = str.replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;").replaceAll("\'","&apos;").replaceAll("\"","&quot;");
    	return str;
    }
    
    public static String delHTML(String htmlStr){
    	String str = Pattern.compile("(<\\s*[a-z]+.*?/?>)|(</?\\s*[a-z]+.*?>)|(<\\!--.*?-->)|(<\\!--\\[.*?\\]>)|(<\\!\\[.*?\\]-->)|(<\\?.*?xml.*?>)", Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
    	return str;
    }
    
    /**
	 * 生成随机密码<br />
	 * 获取当前时间，进行MD5加密，取加密后的最后六位作为密码。
	 * @author zhaochunjiao
	 */
	public static String createRandomPassword()throws Exception{
		//1、获取当前时间
		long currentTime = System.currentTimeMillis();
		
		//2、MD5加密
		String encryptedStr = Md5Encrypt.md5(String.valueOf(currentTime));
		
		//3、取encryptedStr的最后六位
		int strLength = encryptedStr.length();
		String randomPassword = encryptedStr.substring(strLength-6, strLength);
		
		return randomPassword;
	}
}
