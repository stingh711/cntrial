package com.imedis.cntrial.util;

import java.math.BigDecimal;

public class FloatUtil {

	/***
	 * 
	 * @param price
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static int FloatForInt(float price,float code)throws Exception{
		BigDecimal pricebd = new BigDecimal(Float.toString(price));
		BigDecimal b2 = new BigDecimal(Float.toString(code));
		int priceInt = pricebd.multiply(b2).intValue();
		return priceInt;
	} 
	
	/***
	 * 
	 *@param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商 
	 * @return
	 */
	public static float IntForFloat(float v1,float v2,int scale){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).floatValue();
    } 
	
	/**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static float round(float v,int scale){
        BigDecimal b = new BigDecimal(Float.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).floatValue();
    } 
}
