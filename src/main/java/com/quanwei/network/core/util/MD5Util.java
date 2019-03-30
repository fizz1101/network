package com.quanwei.network.core.util;

import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 */
public class MD5Util {

    /**
     * md5加密(32位小写)
     * @param plainText 源字符串
     * @return
     */
    public static String getMd5Str(String plainText) {
        return md5(plainText);
    }

    /**
     * md5加密(32位大写)
     * @param plainText 源字符串
     * @return
     */
    public static String getMd5StrUpper(String plainText) {
        String str = md5(plainText);
        return str.toUpperCase();
    }

    /**
     * md5加密(16位小写)
     * @param plainText 源字符串
     * @return
     */
    public static String getMd5Str16(String plainText) {
        String str = md5(plainText);
        str = str.substring(8, 24);
        return str;
    }

    /**
     * md5加密(16位大写)
     * @param plainText 源字符串
     * @return
     */
    public static String getMd5StrUpper16(String plainText) {
        String str = md5(plainText);
        str = str.substring(8, 24);
        return str.toUpperCase();
    }

    /**
     * md5加密方法
     * @param plainText 源字符串
     * @return
     */
    private static String md5(String plainText) {
        String str = "";
        try {
            if (!StringUtils.isEmpty(plainText)) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(plainText.getBytes());
                byte b[] = md.digest();
                int i;
                StringBuffer buf = new StringBuffer("");
                for (int offset = 0; offset < b.length; offset++) {
                    i = b[offset];
                    if (i < 0)
                        i += 256;
                    if (i < 16)
                        buf.append("0");
                    buf.append(Integer.toHexString(i));
                }
                str = buf.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return str;
    }

}
