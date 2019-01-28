package com.quanwei.network.core.util;

import com.quanwei.network.core.Enum.ErrorCodeEnum;
import com.quanwei.network.core.exception.ParamException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数处理工具类
 */
public class ParamUtil {

    /**
     * 获取请求参数数据
     * @param request
     * @return
     */
    public static Map getMapData(HttpServletRequest request) {
        Map<String, Object> map = (Map<String, Object>) request.getSession().getAttribute("dataMap");
        /*if (map == null || map.isEmpty()) {
            throw new ParamException(ErrorCodeEnum.PARAM_ERROR);
        }*/
        return map == null ? new HashMap() {} : map;
    }

    /**
     * 获取string类型参数值
     * @param dataMap 参数数据
     * @param key 参数名
     * @return
     */
    public static String getString(Map<String, Object> dataMap, String key) {
        if (dataMap.containsKey(key)) {
            Object value = dataMap.get(key);
            return (String) value;
        }
        return null;
    }

    /**
     * 获取必传string类型参数值
     * @param dataMap 参数数组
     * @param key 参数名
     * @return
     */
    public static String getRequiredString(Map<String, Object> dataMap, String key) {
        checkContainsKey(dataMap, key);
        Object value = dataMap.get(key);
//        checkStringType(value, key);
        return (String) value;
    }

    /**
     * 获取int类型参数值
     * @param dataMap 参数数组
     * @param key 参数名
     * @return
     */
    public static Integer getInteger(Map<String, Object> dataMap, String key) {
        if (dataMap.containsKey(key)) {
            Object value = dataMap.get(key);
            return changeToInteger(value, key);
        }
        return null;
    }

    /**
     * 获取必传int类型参数值
     * @param dataMap 参数数组
     * @param key 参数名
     * @return
     */
    public static Integer getRequiredInteger(Map<String, Object> dataMap, String key) {
        checkContainsKey(dataMap, key);
        Object value = dataMap.get(key);
//        checkNumberType(value, key);
        return changeToInteger(value, key);
    }

    /**
     * 获取Long类型参数值
     * @param dataMap 参数数组
     * @param key 参数名
     * @return
     */
    public static Long getLong(Map<String, Object> dataMap, String key) {
        if (dataMap.containsKey(key)) {
            Object value = dataMap.get(key);
//            checkNumberType(value, key);
            return changeToLong(value, key);
        }
        return null;
    }

    /**
     * 获取必传Long类型参数值
     * @param dataMap 参数数组
     * @param key 参数名
     * @return
     */
    public static Long getRequiredLong(Map<String, Object> dataMap, String key) {
        checkContainsKey(dataMap, key);
        Object value = dataMap.get(key);
        return changeToLong(value, key);
    }

    /**
     * 获取布尔类型参数值
     * @param dataMap 参数数组
     * @param key 参数名
     * @return
     */
    public static Boolean getBoolean(Map<String, Object> dataMap, String key) {
        if (dataMap.containsKey(key)) {
            Object value = dataMap.get(key);
//            checkBooleanType(value, key);
            return (Boolean) value;
        }
        return null;
    }

    /**
     * 获取必传布尔类型参数值
     * @param dataMap 参数数组
     * @param key 参数名
     * @return
     */
    public static Boolean getRequiredBoolean(Map<String, Object> dataMap, String key) {
        checkContainsKey(dataMap, key);
        Object value = dataMap.get(key);
//        checkBooleanType(value, key);
        return (Boolean) value;
    }

    /**
     * 获取参数值
     * @param request
     * @param dataMap 参数数组
     * @param key 参数名
     * @return
     */
    private static Object getValue(HttpServletRequest request, Map<String, Object> dataMap, String key) {
        if (dataMap == null || dataMap.isEmpty()) {
            dataMap = (Map<String, Object>) request.getSession().getAttribute("dataMap");
        }
        if (dataMap != null && dataMap.size() > 0) {
            return dataMap.get(key);
        }
        return null;
    }

    /**
     * 检测参数是否存在
     * @param dataMap 参数数组
     * @param key 参数名
     */
    public static void checkContainsKey(Map<String, Object> dataMap, String key) {
        if (!dataMap.containsKey(key)) {
            throw new ParamException(ErrorCodeEnum.PARAM_EMPTY.getKey(), getEmptyMsg(key));
        }
    }

    /**
     * 校验String类型参数
     * @param value 参数值
     * @param key 参数名
     */
    private static void checkStringType(Object value, String key) {
        if (value != null && !(value instanceof String)) {
            throw new ParamException(ErrorCodeEnum.PARAM_TYPE_ERROR.getKey(), ErrorCodeEnum.PARAM_TYPE_ERROR.getValue()+"，key：" + key);
        }
    }

    /**
     * 校验数值类型参数
     * @param value 参数值
     * @param key 参数名
     */
    private static void checkNumberType(Object value, String key) {
        if (value != null && !(value instanceof Number)) {
            throw new ParamException(ErrorCodeEnum.PARAM_TYPE_ERROR.getKey(), getWrongTypeMsg(key));
        }
    }

    /**
     * 校验布尔值类型参数
     * @param value 参数值
     * @param key 参数名
     */
    private static void checkBooleanType(Object value, String key) {
        if (value != null && !(value instanceof Boolean)) {
            throw new ParamException(ErrorCodeEnum.PARAM_TYPE_ERROR.getKey(), getWrongTypeMsg(key));
        }
    }


    /**
     * 转换数据类型 to Integer
     * @param value 参数值
     * @param key 参数名
     * @return
     */
    private static Integer changeToInteger(Object value, String key) {
        try {
            return Integer.parseInt((String) value);
        } catch (IllegalArgumentException e) {
            throw new ParamException(ErrorCodeEnum.PARAM_TYPE_ERROR.getKey(), ErrorCodeEnum.PARAM_TYPE_ERROR.getValue()+"，key：" + key);
        }
    }

    /**
     * 转换数据类型 to Long
     * @param value
     * @param key
     * @return
     */
    private static Long changeToLong(Object value, String key) {
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (IllegalArgumentException e) {
            throw new ParamException(ErrorCodeEnum.PARAM_TYPE_ERROR.getKey(), ErrorCodeEnum.PARAM_TYPE_ERROR.getValue()+"，key：" + key);
        }
    }

    /**
     * 获取空参数响应信息
     * @param key 参数名
     * @return
     */
    private static String getEmptyMsg(String key) {
        return ErrorCodeEnum.PARAM_EMPTY.getValue() + ",key：" + key;
    }

    /**
     * 获取参数类型错误响应信息
     * @param key 参数名
     * @return
     */
    private static String getWrongTypeMsg(String key) {
        return ErrorCodeEnum.PARAM_TYPE_ERROR.getValue() + ",key：" + key;
    }

}
