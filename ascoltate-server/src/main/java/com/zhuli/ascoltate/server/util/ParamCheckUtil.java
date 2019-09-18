package com.zhuli.ascoltate.server.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.zhuli.ascoltate.server.util.exception.RequestErrorCode;
import com.zhuli.ascoltate.server.util.exception.RequestException;

import java.util.List;

public class ParamCheckUtil {
    public static String ERROR_MSG_EXPRESSION_TRUE = "%s not valid!";
    public static String ERROR_MSG_STRING_NOT_NULL = "%s is null or empty!";
    public static String ERROR_MSG_OBJECT_NOT_NULL = "%s is null!";
    public static String ERROR_MSG_VALUE_NOT_NULL_NEGATIVE = "%s is null or negative!";
    public static String ERROR_MSG_LIST_NOT_NULL = "%s list null or empty!";
    public static String ERROR_MSG_LIST_SIZE_NOT_EQUAL = "%s list size not equal %s!";

    public static void checkTrue(boolean expression, String arg) {
        checkTrue(expression, arg, 400);
    }

    public static void checkTrue(boolean expression, String arg, int status) {
        if (!expression) {
            throw new RequestException(RequestErrorCode.BAD_PARAMS, Lists.newArrayList(arg),
                    String.format(ERROR_MSG_EXPRESSION_TRUE, arg));
        }
    }

    public static void checkObjectNotNull(Object item, String arg) {
        checkObjectNotNull(item, arg, 400);
    }

    public static void checkObjectNotNull(Object item, String arg, int status) {
        checkObjectNotNull(item, arg, status, true);
    }

    public static void checkObjectNotNull(Object item, String arg, Boolean needFormatErrorMsg) {
        checkObjectNotNull(item, arg, 400, needFormatErrorMsg);
    }

    public static void checkObjectNotNull(Object item, String arg, int status, Boolean needFormatErrorMsg) {
        if (item == null) {
            String msg = needFormatErrorMsg ? String.format(ERROR_MSG_OBJECT_NOT_NULL, arg) : arg;
            throw new RequestException(RequestErrorCode.PARAMS_NULL, Lists.newArrayList(arg), msg);
        }
    }

    public static void checkStringNotNullOrEmpty(String item, String arg) {
        checkStringNotNullOrEmpty(item, arg, 400);
    }

    public static void checkStringNotNullOrEmpty(String item, String arg, int status) {
        if (Strings.isNullOrEmpty(item)) {
            throw new RequestException(RequestErrorCode.PARAMS_NULL, Lists.newArrayList(arg),
                    String.format(ERROR_MSG_STRING_NOT_NULL, arg));
        }
    }

    public static void checkListNotNullOrEmpty(List list, String arg) {
        checkListNotNullOrEmpty(list, arg, 400);
    }

    public static void checkListEqualLength(List list, Integer length, String arg) {
        checkObjectNotNull(list, "list can't be null");
        if (list.size() != length) {
            throw new RequestException(RequestErrorCode.PARAMS_NULL, Lists.newArrayList(arg),
                    String.format(ERROR_MSG_LIST_SIZE_NOT_EQUAL, arg, length));
        }
    }

    public static void checkListNotNullOrEmpty(List list, String arg, int status) {
        if (list == null || list.isEmpty()) {
            throw new RequestException(RequestErrorCode.PARAMS_NULL, Lists.newArrayList(arg),
                    String.format(ERROR_MSG_LIST_NOT_NULL, arg));
        }
    }

    public static void checkValueNotNullOrNegative(Integer value, String arg) {
        checkValueNotNullOrNegative(value, arg, 400);
    }

    public static void checkValueNotNullOrNegative(Integer value, String arg, int status) {
        if (value == null || value < 0) {
            throw new RequestException(RequestErrorCode.PARAMS_NULL_OR_NEGATIVE, Lists.newArrayList(arg),
                    String.format(ERROR_MSG_VALUE_NOT_NULL_NEGATIVE, arg));
        }
    }

    public static void checkValueNotNullOrNegative(Long value, String arg) {
        checkValueNotNullOrNegative(value, arg, 400);
    }

    public static void checkValueNotNullOrNegative(Long value, String arg, int status) {
        if (value == null || value < 0) {
            throw new RequestException(RequestErrorCode.PARAMS_NULL_OR_NEGATIVE,
                    String.format(ERROR_MSG_VALUE_NOT_NULL_NEGATIVE, arg));
        }
    }
}
