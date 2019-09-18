package com.zhuli.ascoltate.server.util.exception;

import java.util.List;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Strings;

import lombok.Getter;

@Getter
public class RequestException extends RuntimeException {
    private static final long serialVersionUID = 6019192737095202166L;

    private RequestErrorCode code;

    /**
     * <p>填充 code.errorMsgKey 中的占位符 </p>
     */
    private List<Object> msgArgs;

    /**
     * <p>自定义具体的异常信息, 比如指定 PRN 不存在，errorDetail 中就指明不存在的 PRN</p>
     * <p>
     * <p>如果是系统错误，则将堆栈返回给前端</p>
     */
    private String errorDetail = "";

    /**
     * <p>原始错误信息, 日志中追踪堆栈信息</p>
     */
    private Throwable originThrowable;

    public RequestException(RequestErrorCode code) {
        this.code = code;
    }

    public RequestException(RequestErrorCode code, List<Object> args) {
        this(code, args, null);
    }

    public RequestException(RequestErrorCode code, String errorDetail) {
       this(code, null, errorDetail);
    }

    public RequestException(RequestErrorCode code, List<Object> args, String errorDetail) {
        this.code = code;
        this.msgArgs = args;
        this.errorDetail = errorDetail;
    }

    /**
     * <p>一般为无法辨别的系统异常</p>
     * @param code
     * @param errorDetail
     * @param originThrowable
     */
    public RequestException(RequestErrorCode code, String errorDetail, Throwable originThrowable) {
        this.code = code;
        this.errorDetail = errorDetail;
        this.originThrowable = originThrowable;
    }

    public RequestException(RequestErrorCode code, Throwable originThrowable) {
        this.code = code;
        this.originThrowable = originThrowable;
    }

    /**
     * 返回给前端的 response.msg
     * @return
     */
    public String convertResponseMsg() {
        if (this.code != null) {
            if (RequestErrorCode.UNKNOWN_ERROR.equals(this.code)) {
                // 系统异常, msg 中返回原始堆栈 msg 消息
                return String.format("%s: %s", this.code.getErrorMsgKey(), this.originThrowable == null ? "" : this.originThrowable.getMessage());
            }
            if (RequestErrorCode.DATABASE_UNIQUE_ERROR.equals(this.code)) {
                return String.format("%s 详情: %s", this.code.getErrorMsgKey(), this.getErrorDetail());
            }
            if (CollectionUtils.isEmpty(this.msgArgs)) {
                // 不含占位符
                return this.code.getErrorMsgKey();
            } else {
                // 含占位符
                FormattingTuple ft = MessageFormatter
                        .arrayFormat(this.code.getErrorMsgKey(), this.msgArgs.toArray());
                return ft.getMessage();
            }
        }
        // 兼容老的
        return super.getMessage();
    }

    /**
     * 代码中 catch exception 需要获取详细的 message
     * @return
     */
    @Override
    public String getMessage() {
        if (this.code != null) {
            if (RequestErrorCode.UNKNOWN_ERROR.equals(this.code)) {
                // 系统异常, msg 中返回原始堆栈 msg 消息
                return String.format("%s: %s", this.code.getErrorMsgKey(), this.originThrowable == null ? "" : this.originThrowable.getMessage());
            }
            String message = this.code.getErrorMsgKey();
            if (!CollectionUtils.isEmpty(this.msgArgs)) {
                // 有占位符, 匹配占位符
                FormattingTuple ft = MessageFormatter
                        .arrayFormat(this.code.getErrorMsgKey(), this.msgArgs.toArray());
                message = ft.getMessage();
            }
            if (!Strings.isNullOrEmpty(this.errorDetail)) {
                // 补充详情
                message = String.format("%s: %s", message, this.errorDetail);
            }
            return message;
        }
        // 兼容老的
        return super.getMessage();
    }

    public String getErrorDetail() {
        if (Strings.isNullOrEmpty(this.errorDetail)) {
            if (this.originThrowable != null) {
                return this.originThrowable.getMessage();
            }
        }
        return this.errorDetail;
    }
}
