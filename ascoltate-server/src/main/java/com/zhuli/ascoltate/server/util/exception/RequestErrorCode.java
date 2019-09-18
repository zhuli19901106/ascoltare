package com.zhuli.ascoltate.server.util.exception;

import com.zhuli.ascoltate.server.util.constant.Constants;

import org.slf4j.event.Level;

import lombok.Getter;

@Getter
public enum RequestErrorCode {
    UNKNOWN_ERROR("0010001", "系统异常，请查看日志详情", Level.ERROR),

    IO_ERROR("0010027", "IO 异常", Level.ERROR),
    BAD_PARAMS("0010032", "参数错误", Level.ERROR, Constants.BAD_REQUEST_STATUS),
    PARAMS_NULL("0010033", "参数不能为空：{}", Level.ERROR, Constants.BAD_REQUEST_STATUS),
    PARAMS_NULL_OR_NEGATIVE("0010034", "参数不能为空且不能小于0：{}", Level.ERROR, Constants.BAD_REQUEST_STATUS),
    JSON_ERROR("0010041", "JSON 错误", Level.ERROR, Constants.BAD_REQUEST_STATUS),
    DATE_FORMAT_ERROR("0010043", "日期格式错误, 仅支持格式: yyyy-MM-dd'T'HH:mm:ss", Level.ERROR,
            Constants.BAD_REQUEST_STATUS),
    NULL_OBJECT_ERROR("0010044", "空指针异常", Level.ERROR),
    ELEMENT_NOT_FOUND("0010049", "元素 {} 主键 {} 不存在", Level.ERROR, Constants.NOT_FOUND_STATUS),
    ELEMENT_EXIST("0010068", "元素 {} 主键 {} 已存在", Level.ERROR, Constants.BAD_REQUEST_STATUS),

    DATABASE_TYPE_CONVERT_ERROR("0010154", "数据类型转换失败", Level.ERROR),
    UPDATE_META_MERGE_OBJECT_ERROR("0010155", "更新对象合并属性错误", Level.ERROR, 503),
    DATABASE_UNIQUE_ERROR("0010160", "数据库中存在重复主键", Level.ERROR, Constants.CONFLICT);

    /**
     * <p>错误状态码, kg-manager 以 001 开头</p>
     */
    private String errorCode;
    /**
     * <p>错误状态码对应 HTTP Code</p>
     */
    private Integer httpCode;
    /**
     * <p>错误对应文案 key （国际化）, 对应在配置文件中找到反馈给前端的文案 (msg)</p>
     */
    private String errorMsgKey;

    /**
     * <p>复用日志的 level (基本只会有 error, warn)</p>
     */
    private Level errorLevel;

    RequestErrorCode(String errorCode, String errorMsgKey, Level errorLevel, Integer httpCode) {
        this.errorCode = errorCode;
        this.errorMsgKey = errorMsgKey;
        this.errorLevel = errorLevel;
        this.httpCode = httpCode;
    }

    /**
     * <p>默认 HTTP 响应码 500</p>
     */
    RequestErrorCode(String errorCode, String errorMsgKey, Level errorLevel) {
        this(errorCode, errorMsgKey, errorLevel, Constants.INTERNAL_SERVER_ERROR);
    }
}
