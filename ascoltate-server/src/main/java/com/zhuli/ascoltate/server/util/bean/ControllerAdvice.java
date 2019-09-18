package com.zhuli.ascoltate.server.util.bean;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.zhuli.ascoltate.common.dto.response.Response;
import com.zhuli.ascoltate.server.util.exception.ExceptionUtil;
import com.zhuli.ascoltate.server.util.exception.RequestErrorCode;
import com.zhuli.ascoltate.server.util.exception.RequestException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {
    /**
     * <p>
     * 捕捉系统异常
     * </p>
     * 包括: 1. Spring MVC 中的框架异常
     * {@link org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler}
     * 2. 先知其它模块异常 {@link com._4paradigm.prophet.rest.exception.RestBaseException}
     * 2. 数据库异常 {@link ConstraintViolationException} 3. 其它异常 将其包装成统一
     * ProphetException
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Response> defaultExceptionHandler(HttpServletRequest request, Exception ex) {
        RequestException prophetException;
        // 此处的异常尽量在业务逻辑和上面的 Exception 的 if-else 中收敛
        String message = ex.getMessage();
        Throwable rootEx = ExceptionUtils.getRootCause(ex);
        if (rootEx != null) {
            message = rootEx.getMessage();
        }
        if (rootEx == null || (rootEx != null && !(rootEx instanceof SQLException))) {
            log.error("exception: {}", message, ex);
        }
        prophetException = new RequestException(RequestErrorCode.UNKNOWN_ERROR, message, ex);
        return wrap(request, prophetException);
    }

    private ResponseEntity<Response> wrap(HttpServletRequest request, RequestException prophetException) {
        ResponseEntity<Response> responseEntity = ExceptionUtil.exceptionResponse(request, prophetException);
        return responseEntity;
    }

    /**
     * <p>
     * 自定义系统异常
     * </p>
     */
    @ExceptionHandler(value = RequestException.class)
    public ResponseEntity<Response> exception(HttpServletRequest request, RequestException e) {
        return wrap(request, e);
    }
}
