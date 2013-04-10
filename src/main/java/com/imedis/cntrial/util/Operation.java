/**  
 * 客户端操作枚举类
 * @title Operation.java
 * @package com.bama.utils 
 * @author zhaochunjiao  
 * @create 2013-3-5 上午09:38:39
 * @version V1.0  
 */
package com.imedis.cntrial.util;

/**
 * 客户端操作枚举类
 * @author zhaochunjiao
 * @create 2013-3-5 上午09:38:39
 * @version 1.0
 */
public enum Operation {
	/*新房*/
	LISTNEWHOUSESFORSEARCH,//列出新房
	GETNEWHOUSEFORANDROID,//获得新房详情
	LISTNEWHOUSEPICSFORANDROID,//列出新房图片
	LISTNEWHOUSEALBUMSFORANDROID,//列出新房相册
	LISTALBUMPICSFORANDROID,//列出相册图片
	
	/*会员优惠*/
	LISTDISCOUNTSFORCLIENT,//列出会员优惠券
	VIEWDISCOUNT,//获取会员优惠详情
	LISTDISCOUNTCATEGORYS,//列出所有会员优惠类型
	LISTDISCOUNTSFORSEARCH,//会员优惠搜索
	
	LISTNEWCONSULTANTS,//列出置业顾问
	LISTADBYMODULE, //列出广告
	
	/*门店*/
	LISTNEWSHOPSFORANDROID,
}
