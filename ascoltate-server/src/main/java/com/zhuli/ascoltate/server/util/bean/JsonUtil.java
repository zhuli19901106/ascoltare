package com.zhuli.ascoltate.server.util.bean;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.zhuli.ascoltate.server.util.exception.RequestErrorCode;
import com.zhuli.ascoltate.server.util.exception.RequestException;

@Component
public class JsonUtil {
    private ObjectMapper objectMapper;

    /**
     * spring自动注入的bean初始化了一些配置，这个对象用来做原始的转换功能。比如不要过滤null的字段。
     */
    private ObjectMapper nativeObjectMapper = new ObjectMapper();

    public JsonUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toNativeJSON(Object object) {
        try {
            return nativeObjectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RequestException(RequestErrorCode.JSON_ERROR, e);
        }
    }

    public String toJSON(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RequestException(RequestErrorCode.JSON_ERROR, e.getMessage(), e);
        }
    }

    public void toJSON(OutputStream out, Object obj) {
        try {
            objectMapper.writeValue(out, obj);
        } catch (IOException e) {
            throw new RequestException(RequestErrorCode.IO_ERROR, e);
        }
    }

    public <T> T toObject(String origin, Class<T> clazz) {
        if (Strings.isNullOrEmpty(origin) || clazz == null) {
            throw new RequestException(RequestErrorCode.NULL_OBJECT_ERROR, "origin content can't be empty and clazz can't be null");
        }
        try {
            return objectMapper.readValue(origin, clazz);
        } catch (IOException e) {
            throw new RequestException(RequestErrorCode.IO_ERROR, e);
        }
    }

    public <T> T convertObject(Object obj, Class<T> clazz) {
        T target = null;
        try {
            String str = objectMapper.writeValueAsString(obj);
            target = objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            throw new RequestException(RequestErrorCode.IO_ERROR, e);
        }
        return target;
    }
}
