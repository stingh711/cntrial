package com.imedis.cntrial.util;

import java.util.*;
public class ParamAnalysis {
	@SuppressWarnings("unchecked")
    public static String getSignOfParams(Map params,String key) {
        String sign = Md5Encrypt.md5(getContent(params, key));
        return sign;

    }
    
    @SuppressWarnings("unchecked")
	private static String getContent(Map params, String privateKey) {
        List keys = new ArrayList(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            String value = params.get(key).toString();

            if (i == keys.size() - 1) {
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr + privateKey;
    }
}
