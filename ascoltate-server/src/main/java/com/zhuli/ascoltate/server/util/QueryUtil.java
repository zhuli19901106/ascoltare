package com.zhuli.ascoltate.server.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Transient;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import com.zhuli.ascoltate.common.dto.response.Page;
import com.zhuli.ascoltate.server.util.exception.RequestErrorCode;
import com.zhuli.ascoltate.server.util.exception.RequestException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryUtil {
    public static <T> BooleanBuilder buildGeneralConditions(Class<T> clazz, Map<String, String> params) {
        // size filed conflict with page `size` param
        if (params.containsKey("size"))
            params.remove("size");

        String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clazz.getSimpleName());
        SimplePath<T> entity = Expressions.path(clazz, tableName);
        BooleanBuilder conditions = new BooleanBuilder();

        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

        for (Field field : fields) {
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            String fieldName = field.getName();
            if (!params.containsKey(fieldName) && !params.containsKey(fieldName + "Start")
                    && !params.containsKey(fieldName + "End") && !params.containsKey(fieldName + "EQ")
                    && !params.containsKey(fieldName + "StartGT") && !params.containsKey(fieldName + "EndLTE"))
                continue;
            String fieldValue = params.get(fieldName);
            String clazzName = field.getType().getSimpleName();

            Class superClazz = field.getType().getSuperclass();
            if (superClazz != null && superClazz == Enum.class) {
                clazzName = "Enum";
            }

            switch (clazzName) {
            case "Long":
                if (StringUtils.isEmpty(fieldValue))
                    continue;
                conditions.and(Expressions.path(Long.class, entity, fieldName).eq(Long.valueOf(fieldValue)));
                break;
            case "Integer":
                if (StringUtils.isEmpty(fieldValue))
                    continue;
                conditions.and(Expressions.path(Integer.class, entity, fieldName).eq(Integer.valueOf(fieldValue)));
                break;
            case "String":
                if (StringUtils.isEmpty(fieldValue))
                    continue;

                if (fieldValue.toLowerCase().equals("null")) {
                    conditions.and(Expressions.stringPath(entity, fieldName).isNull());
                } else if (fieldValue.equals("*") || fieldValue.equals("**")) {
                    conditions.and(Expressions.stringPath(entity, fieldName).matches(fieldValue.replace("*", ".*"))
                            .or(Expressions.stringPath(entity, fieldName).isNull()));
                } else if (fieldValue.contains("*")) {
                    conditions.and(Expressions.stringPath(entity, fieldName).matches(fieldValue.replace("*", ".*")));
                } else if (fieldValue.contains(",")) {
                    conditions.and(Expressions.stringPath(entity, fieldName).in(fieldValue.split(",")));
                } else {
                    conditions.and(Expressions.stringPath(entity, fieldName).eq(fieldValue));
                }
                break;
            case "Boolean":
                conditions.and(Expressions.booleanPath(entity, fieldName).eq(Boolean.valueOf(fieldValue)));
                break;
            case "Timestamp":
                // 以 long 的形式传递进来
                if (fieldValue != null) {
                    if ("NotNull".equalsIgnoreCase(fieldValue.toLowerCase())) {
                        conditions.and(Expressions.datePath(Timestamp.class, entity, fieldName).isNotNull());
                    }
                    if ("null".equalsIgnoreCase(fieldValue.toLowerCase())) {
                        conditions.and(Expressions.datePath(Timestamp.class, entity, fieldName).isNull());
                    }
                    if (fieldValue.contains(",")) {
                        String[] timeSlices = fieldValue.split(",");
                        List<Timestamp> timestamps = Lists.newArrayList();
                        for (String slice : timeSlices) {
                            timestamps.add(new Timestamp(Long.valueOf(slice)));
                        }
                        conditions.and(Expressions.datePath(Timestamp.class, entity, fieldName).in(timestamps));
                    }
                }
                if (params.containsKey(fieldName + "StartGT")) {
                    conditions.and(Expressions.datePath(Timestamp.class, entity, fieldName)
                            .gt(new Timestamp(Long.valueOf(params.get(fieldName + "StartGT")))));
                }
                if (params.containsKey(fieldName + "EndLTE")) {
                    conditions.and(Expressions.datePath(Timestamp.class, entity, fieldName)
                            .loe(new Timestamp(Long.valueOf(params.get(fieldName + "EndLTE")))));
                }
                if (params.containsKey(fieldName + "Start")) {
                    conditions.and(Expressions.datePath(Timestamp.class, entity, fieldName)
                            .goe(new Timestamp(Long.valueOf(params.get(fieldName + "Start")))));
                }
                if (params.containsKey(fieldName + "End")) {
                    conditions.and(Expressions.datePath(Timestamp.class, entity, fieldName)
                            .lt(new Timestamp(Long.valueOf(params.get(fieldName + "End")))));
                }
                if (params.containsKey(fieldName + "EQ")) {
                    conditions.and(Expressions.datePath(Timestamp.class, entity, fieldName)
                            .eq(new Timestamp(Long.valueOf(params.get(fieldName + "EQ")))));
                }
                break;
            case "Date":
                // 传递格式类似: 2018-10-11T13:33:51
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String dateStr = null;
                try {
                    if (params.containsKey(fieldName + "StartGT")) {
                        dateStr = params.get(fieldName + "StartGT");
                        conditions.and(Expressions.datePath(Date.class, entity, fieldName)
                                .gt(simpleDateFormat.parse(dateStr)));
                    }
                    if (params.containsKey(fieldName + "EndLTE")) {
                        dateStr = params.get(fieldName + "EndLTE");
                        conditions.and(Expressions.datePath(Date.class, entity, fieldName)
                                .loe(simpleDateFormat.parse(dateStr)));
                    }
                    if (params.containsKey(fieldName + "Start")) {
                        dateStr = params.get(fieldName + "Start");
                        conditions.and(Expressions.datePath(Date.class, entity, fieldName)
                                .goe(simpleDateFormat.parse(dateStr)));
                    }
                    if (params.containsKey(fieldName + "End")) {
                        dateStr = params.get(fieldName + "End");
                        conditions.and(Expressions.datePath(Date.class, entity, fieldName)
                                .lt(simpleDateFormat.parse(dateStr)));
                    }
                } catch (ParseException e) {
                    log.error("Date format parse error", e);
                    throw new RequestException(RequestErrorCode.DATE_FORMAT_ERROR, "error date" + dateStr);
                }
                break;
            case "Enum":
                Class type = field.getType();
                try {
                    Method valueOf = field.getType().getMethod("valueOf", String.class);
                    if (!StringUtils.isEmpty(fieldValue)) {
                        List<Object> collect = Stream.of(fieldValue.split(",")).map((String item) -> {
                            try {
                                return valueOf.invoke(null, item);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }).collect(Collectors.toList());
                        conditions.and(Expressions.enumPath(type, entity, fieldName).in(collect));
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return conditions;
    }

    public static <T> Page<List<T>> convertJpaPage(org.springframework.data.domain.Page<T> jpaPage) {
        Page<List<T>> page = new Page<>();
        page.setList(jpaPage.getContent());
        page.setTotal(jpaPage.getTotalElements());
        page.setLimit(jpaPage.getPageable().getPageSize());
        page.setOffset(jpaPage.getPageable().getOffset());
        page.setPage(jpaPage.getNumber() + 1);
        page.setSize(jpaPage.getSize());

        return page;
    }

    @SuppressWarnings("unchecked")
    public static <T> T mergeObjects(T first, T second) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = first.getClass();
        Field[] fields = clazz.getDeclaredFields();

        Field[] allFields = fields;
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null) {
            Field[] parentFields = superclass.getDeclaredFields();
            allFields = (Field[]) ArrayUtils.addAll(allFields, parentFields);
            superclass = superclass.getSuperclass();
        }
        Object returnValue = clazz.newInstance();
        for (Field field : allFields) {
            if (field.getName().equals("log")) {
                continue;
            }
            field.setAccessible(true);
            Object value1 = field.get(first);
            Object value2 = field.get(second);
            Object value = (value1 != null) ? value1 : value2;
            field.set(returnValue, value);
        }
        return (T) returnValue;
    }
}
