package com.zhuli.ascoltate.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import java.util.HashMap;

import lombok.Data;
import org.slf4j.event.Level;

@ApiModel
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    /**
     * <p>错误码<p/>
     */
    private String status;
    /**
     * <p>异常文案信息, 供前端展示</p>
     */
    private String msg;
    /**
     * <p>更详细的出错日志, TODO: 如果是系统错误则包含堆栈信息</p>
     */
    private String detail;
    /**
     * <p>异常等级 {@link Level info, error}</p>
     */
    private Level level;
    /**
     * <p>异常请求的 API 路径</p>
     */
    private String path;
    /**
     * <p>X-Prophet-Tracing-v1, 请求的 traceId 方便 debug</p>
     */
    private String traceId;

    /**
     * <p>api 实际数据</p>
     */
    private T data;

    public Response() {
    }

    public Response(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Response(String status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    /**
     * <p>api 调用正常时候的 response </p>
     */
    public Response(T data) {
        this.data = data;
        this.msg = "";
        // TODO: 0 or 200
        this.status = "0";
    }

    public Response(T data, HashMap httpResponseType) {
        this.setData(data);
        this.setStatus(httpResponseType.get("status").toString());
        this.setMsg(httpResponseType.get("msg").toString());
    }

    public Response(HashMap httpResponseType) {
        this.setStatus(httpResponseType.get("status").toString());
        this.setMsg(httpResponseType.get("msg").toString());
    }
}
