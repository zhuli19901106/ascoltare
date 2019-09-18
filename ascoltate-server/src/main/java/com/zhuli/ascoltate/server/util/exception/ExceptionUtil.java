package com.zhuli.ascoltate.server.util.exception;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.zhuli.ascoltate.common.dto.response.Response;

@Slf4j
public class ExceptionUtil {
    public static final String HEADER_TRACING_V1 = "X-Prophet-Tracing-v1";

    public static ResponseEntity<Response> exceptionResponse(RequestException e) {
        return exceptionResponse(null, e);
    }

    public static ResponseEntity<Response> exceptionResponse(HttpServletRequest request, RequestException e) {
        String displayMsg = e.convertResponseMsg();
        Level logLevel = e.getCode().getErrorLevel();
        if (LogLevel.ERROR.name().equals(logLevel.name())) {
            log.error("error msg: {}, detail {}", displayMsg, e.getErrorDetail(), e);
        } else if (LogLevel.WARN.name().equals(logLevel.name())) {
            log.warn("error msg: {}, detail {}", displayMsg, e.getErrorDetail(), e);
        }
        Response response = new Response();
        response.setStatus(e.getCode().getErrorCode());
        response.setMsg(displayMsg);
        response.setDetail(e.getErrorDetail());
        response.setLevel(e.getCode().getErrorLevel());
        if (request != null) {
            response.setPath(request.getRequestURI());
            response.setTraceId(request.getHeader(HEADER_TRACING_V1));
        }
        if (e.getOriginThrowable() != null) {
            log.error("ProphetException origin throwable: ", e.getOriginThrowable());
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getCode().getHttpCode()));
    }

    public static String extractTraceId(HttpServletRequest request) {
        return request.getHeader(HEADER_TRACING_V1);
    }
}
