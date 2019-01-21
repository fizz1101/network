package com.quanwei.network.core.Enum;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 枚举操作工具类
 */
public class EnumUtil {

    /**
     * 通过key值获取对应的value
     * @param key
     * @param enumT
     * @return
     */
    public static <T> Object getEnumValueByKey(Object key, Class<T> enumT, String... methodNames) {
        if (!enumT.isEnum()) { //不是枚举则返回""
            return "";
        }
        T[] enums = enumT.getEnumConstants();  //获取枚举的所有枚举属性，似乎这几句也没啥用，一般既然用枚举，就一定会添加枚举属性
        if (enums == null || enums.length <= 0) {
            return "";
        }
        int count = methodNames.length;
        String keyMathod = "getKey";    //默认获取枚举key方法，与接口方法一致
        String valueMathod = "getValue"; //默认获取枚举value方法
        if (count >= 1 && !"".equals(methodNames[0])) {
            keyMathod = methodNames[0];
        }
        if (count == 2 && !"".equals(methodNames[1])) {
            valueMathod = methodNames[1];
        }
        for (int i = 0, len = enums.length; i < len; i++) {
            T t = enums[i];
            try {
                Object resultKey = getMethodValue(keyMathod, t);//获取枚举对象key
                if (resultKey.toString().equals(key+"")) {
                    Object resultValue = getMethodValue(valueMathod, t); //存在则返回对应值
                    return resultValue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 通过枚举key或者自定义值及方法获取枚举属性值
     * @param key
     * @param enumT
     * @return enum key
     */
    public static <T> String getEnumNameByKey(Object key, Class<T> enumT, String... methodNames) {
        if (!enumT.isEnum()) {
            return "";
        }
        T[] enums = enumT.getEnumConstants();
        if (enums == null || enums.length <= 0) {
            return "";
        }
        int count = methodNames.length;
        String keyMathod = "getKey"; //默认方法
        if (count >= 1 && !"".equals(methodNames[0])) { //独立方法
            keyMathod = methodNames[0];
        }
        for (int i = 0, len = enums.length; i < len; i++) {
            T tobj = enums[i];
            try {
                Object resultKey = getMethodValue(keyMathod, tobj);
                if (resultKey != null && resultKey.toString().equals(key + "")) { //存在则返回对应值
                    return tobj + "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    /**
     * 枚举转map结合value作为map的key,description作为map的value
     * @param enumT
     * @return enum mapcolloction
     */
    public static <T> Map<String, String> EnumToMap(Class<T> enumT, String... methodNames) {
        Map<String, String> enummap = new LinkedHashMap<>();
        if (!enumT.isEnum()) {
            return enummap;
        }
        T[] enums = enumT.getEnumConstants();
        if (enums == null || enums.length <= 0) {
            return enummap;
        }
        int count = methodNames.length;
        String keyMathod = "getKey"; //默认接口value方法
        String valueMathod = "getValue";//默认接口description方法
        if (count >= 1 && !"".equals(methodNames[0])) {
            //扩展方法
            keyMathod = methodNames[0];
        }
        if (count == 2 && !"".equals(methodNames[1])) {
            valueMathod = methodNames[1];
        }
        for (int i = 0, len = enums.length; i < len; i++) {
            T tobj = enums[i];
            try {
                Object resultKey = getMethodValue(keyMathod, tobj); //获取key值
                if ("".equals(resultKey)) {
                    continue;
                }
                Object resultValue = getMethodValue(valueMathod, tobj); //获取value值
                if ("".equals(resultValue)) {
                    //如果描述不存在获取属性值
                    resultValue = tobj;
                }
                enummap.put(resultKey + "", resultValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return enummap;
    }

    /**
     * 根据反射，通过方法名称获取方法值，忽略大小写的
     * @param methodName
     * @param obj
     * @param args
     * @return return value
     */
    private static <T> Object getMethodValue(String methodName, T obj, Object... args) {
        Object resut = "";
        // boolean isHas = false;
        try {
            /********************************* start *****************************************/
            Method[] methods = obj.getClass().getMethods();
            //获取方法数组，这里只要共有的方法
            if (methods.length <= 0) {
                return resut;
            }
            // String methodstr=Arrays.toString(obj.getClass().getMethods());
            // if(methodstr.indexOf(methodName)<0){ //只能粗略判断如果同时存在 getValue和getValues可能判断错误
            // return resut;
            // }
            // List<Method> methodNamelist= Arrays.asList(methods); //这样似乎还要循环取出名称
            Method method = null;
            for (int i = 0, len = methods.length; i < len; i++) {
                if (methods[i].getName().equalsIgnoreCase(methodName)) {//忽略大小写取方法
                    // isHas = true;
                    methodName = methods[i].getName(); //如果存在，则取出正确的方法名称
                    method = methods[i];
                    break;
                }
            }
            // if(!isHas){
            // return resut;
            // }
            // /*************************** end ***********************************************/
            // Method method = obj.getClass().getDeclaredMethod(methodName); ///确定方法
            if (method == null) {
                return resut;
            }
            resut = method.invoke(obj, args); //方法执行
            if (resut == null) {
                resut = "";
            }
            return resut; //返回结果
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resut;
    }

    public static void main(String[] args) {
        /*Class clazz = SmsGetwayEnum.class;
        Map<Object, String> map = EnumToMap(clazz);
        System.out.println("获取枚举的map集合----------："+map);

        System.out.println(getEnumNameByKey(1, SmsGetwayEnum.class));*/

        /*Object desc = getEnumDescByValue("1", ThirdEnumGroup.ThirdServerEnum.class);
        Object value = getEnumDescByValue("通用", ThirdEnumGroup.ThirdServerEnum.class, "getDescription", "getValue");
        Object key = getEnumKeyByValue("1", ThirdEnumGroup.ThirdServerEnum.class);
        System.out.println(desc.toString());
        System.out.println(value.toString());
        System.out.println(key.toString());*/
    }

}
