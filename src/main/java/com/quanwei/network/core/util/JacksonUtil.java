package com.quanwei.network.core.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * json反序列化工具类
 */
public class JacksonUtil {

    private Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

    private ObjectMapper mapper;

    public JacksonUtil(Include inclusion) {
        mapper = new ObjectMapper();
        // 设置输出时包含属性的风格
        mapper.setSerializationInclusion(inclusion);
        //mapper.registerModule(new Hibernate4Module());
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        //mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 禁止使用int代表Enum的order()來反序列化Enum,非常危險
        //mapper.configure(Feature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        // 所有日期格式都统一为以下样式
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        //mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //解决不能直接转单引号的json串的方式
        //mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    /**
     * 创建输出全部属性到Json字符串的Binder.
     */
    public static JacksonUtil buildNormalBinder() {
        return new JacksonUtil(Include.ALWAYS);
    }

    /**
     * 创建只输出非空属性到Json字符串的Binder.
     */
    public static JacksonUtil buildNonNullBinder() {
        return new JacksonUtil(Include.NON_NULL);
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Binder.
     */
    public static JacksonUtil buildNonDefaultBinder() {
        return new JacksonUtil(Include.NON_DEFAULT);
    }

    /**
     * 如果JSON字符串为Null或"null"字符串,返回Null.
     * 如果JSON字符串为"[]",返回空集合.
     *
     * 如需读取集合如List/Map,且不是List<String>这种简单类型时使用如下语句:
     * List<MyBean> beanList = binder.getMapper().readValue(listString, new TypeReference<List<MyBean>>() {});
     */
    public <T> T toObject(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T toObject(String jsonString, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return mapper.readValue(jsonString,typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Exception",e);
        }
        return null;
    }
    /**
     * 如果对象为Null,返回"null".
     * 如果集合为空集合,返回"[]".
     */
    public String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Exception",e);
        }
        return null;
    }

    /**
     * json字符串转成ArrayList<Object>
     * @param jsonString
     * @param elementClasses
     * @return
     */
    public List<Object> JsontoList(String jsonString, Class<?> elementClasses) {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, elementClasses);
        List<Object> lst=null;
        try {
            lst = mapper.readValue(jsonString, javaType);
        } catch (JsonParseException e) {
            logger.error("Exception",e);
            e.printStackTrace();
        } catch (JsonMappingException e) {
            logger.error("Exception",e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("Exception",e);
            e.printStackTrace();
        }
        return lst;
    }

    /**
     * json文件流转成List<?>
     * @param jsonInputStream
     * @param elementClasses
     * @return
     */
    public List<?> JsontoList(InputStream jsonInputStream, Class<?> elementClasses) {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, elementClasses);
        List<?> lst=null;
        try {
            lst = mapper.readValue(jsonInputStream, javaType);
        } catch (JsonParseException e) {
            logger.error("Exception",e);
            e.printStackTrace();
        } catch (JsonMappingException e) {
            logger.error("Exception",e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("Exception",e);
            e.printStackTrace();
        }
        return lst;
    }

    /**
     * 获取JAVA类型
     * @param collectionClass
     * @param elementClasses
     * @return
     */
    public JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }


    /**
     * 取出Mapper做进一步的设置或使用其他序列化API.
     */
    public ObjectMapper getMapper() {
        return mapper;
    }


    public <T> T toObject(InputStream jsonInputStream, Class<T> clazz) {
        if(jsonInputStream!=null){
            try {
                return mapper.readValue(jsonInputStream, clazz);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

}
