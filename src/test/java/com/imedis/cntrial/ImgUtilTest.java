package com.imedis.cntrial;

import com.imedis.cntrial.util.ImgUtil;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-4-10
 * Time: 下午2:28
 * To change this template use File | Settings | File Templates.
 */
public class ImgUtilTest {
    @Test
    public void testToJpg() throws Exception {
        ImgUtil.toJPG("e://mybatis-logo.png", "e://mybatis-logo.jpg", 100);
    }
}
