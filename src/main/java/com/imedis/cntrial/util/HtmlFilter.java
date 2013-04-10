package com.imedis.cntrial.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlFilter {

	/**
	 * 服务器端过滤HTML标签样式
	 * by smm,此方法有待进改进
	 */
	public static void main(String[] args) {
		// /** 去除随笔内容的样式等给ContentQuote */
		String ss = "<div class=\"ddddd\">1233333333</div><!-- dsadsadsadsadsa  -->";//�?��过滤样式的内�?
		
		String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";//去除script代码
		String regEx_scriptOfIe = "<[\\s]*?SCRIPT[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?SCRIPT[\\s]*?>";//IE�?
		String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //定义style的正则表达式{�?style[^>]*?>[\\s\\S]*?<\\/style> } 
		String regEx_styleOfIe = "<[\\s]*?STYLE[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?STYLE[\\s]*?>";
		
		//匹配<!-- XXX --> 
		String pat3 = "<!--([^><]*?)-->";
		
		
		String 	 cutContent =ss.replaceAll(regEx_script, "").replaceAll(regEx_style, "");
		String 	 cutContent2 = cutContent.replaceAll(regEx_scriptOfIe, "").replaceAll(regEx_styleOfIe, "");
		
		String   pat   =   "(<(a|A)\\s+([^>h]|h(?!ref\\b))*href=\")([^\"]*)(http://www.bama.com)([^\"]*)(\"[^>]*>)";   
		String   pat1   =   "((onclick|style|id|class|ondblclick|onchange|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup)++=\"([^\"]*\"))|((onclick|style|id|class|ondblclick|onchange|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup)++=([^\"|\\s|>]*))";
		String   pat2   =  "(<((input|textarea|select|iframe|INPUT|TEXTAREA|SELECT|IFRAME)++)\\s*([^>|<]*>))"; //([^\"]*)
		
		String	editContentHtml = cutContent2.replaceAll(pat,"").replaceAll(pat1,"").replaceAll(pat2,"").replaceAll(pat3,"");
		System.out.println(editContentHtml);

	}
	
//	/**
//
//    *
//
//    * 基本功能：替换指定的标签
//
//    * @author linshutao
//
//    * @param str
//
//    * @param beforeTag   要替换的标签
//
//    * @param tagAttrib   要替换的标签属性值
//
//    * @param startTag    新标签开始标记
//
//    * @param endTag   新标签结束标记
//
//    * @return String
//
//    */
//
//  public   static String replaceHtmlTag(String str, String beforeTag,
//
//     String tagAttrib, String startTag, String endTag) {
//
//  //  String regxpForTag = "<//s*" + beforeTag + "//s+([^>]*)//s*>" ;
//
//     String regxpForTag = "<//s*" + beforeTag + "//s+([^>]*)//s*" ;
//
//     String regxpForTagAttrib = tagAttrib + "=//s*/"([^/"]+)/"";
//
//     //Pattern.CASE_INSENSITIVE 忽略大小写
//
//     Pattern patternForTag = Pattern.compile (regxpForTag,Pattern. CASE_INSENSITIVE );
//
//     Pattern patternForAttrib = Pattern.compile (regxpForTagAttrib,Pattern. CASE_INSENSITIVE );   
//
//     Matcher matcherForTag = patternForTag.matcher(str);
//
//     StringBuffer sb = new StringBuffer ();
//
//     boolean result = matcherForTag.find();
//
//     // 循环找出每个 img 标签
//
//     while (result) {
//
//         StringBuffer sbreplace = new StringBuffer( "<img " );
//
//         Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag
//
//                .group(1));
//
//         if (matcherForAttrib.find()) {
//
//            matcherForAttrib.appendReplacement(sbreplace, startTag
//
//                   + matcherForAttrib.group(1) + endTag);
//
//         }
//
//         matcherForAttrib.appendTail(sbreplace);
//
//         matcherForTag.appendReplacement(sb, sbreplace.toString());
//
//         result = matcherForTag.find();
//
//     }
//
//     matcherForTag.appendTail(sb);         
//
//     return sb.toString();
//
//  } 

}
