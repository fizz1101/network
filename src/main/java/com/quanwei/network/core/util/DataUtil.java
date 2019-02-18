package com.quanwei.network.core.util;

import java.util.*;

public class DataUtil {

    private static Map<String, Object> dataMap = new HashMap<>();

    static {
        loadDataJsonFile();
    }

    /**
     * 加载资源文件
     */
    private static void loadDataJsonFile() {
        ClassLoader classLoader = getClassLoader();
        List<Map<String, Object>> userList = JacksonUtil.buildNormalBinder().toObject(
                classLoader.getResourceAsStream("data/user.json"), List.class);
        dataMap.put("user", userList);
    }

    /**
     * 获取classLoader
     * @return
     */
    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = DataUtil.class.getClassLoader();
        }
        return classLoader;
    }

    /**
     * 获取用户
     * @param username 用户名
     * @return
     */
    public static Map<String, Object> getUser(String username) {
        Map<String, Object> user = new HashMap<>();
        List<Map<String, Object>> userList = (List<Map<String, Object>>) dataMap.get("user");
        for (Map<String, Object> cell : userList) {
            if (username.equalsIgnoreCase((String) cell.get("user"))) {
                user = cell;
                break;
            }
        }
        return user;
    }

}
