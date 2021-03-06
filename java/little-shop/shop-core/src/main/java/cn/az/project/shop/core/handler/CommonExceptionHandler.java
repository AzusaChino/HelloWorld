package cn.az.project.shop.core.handler;

import cn.az.project.shop.core.domain.RestResponse;
import cn.az.project.shop.core.exception.CommonException;
import cn.az.project.shop.core.exception.LimitAccessException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.Set;

/**
 * The type Common exception handler.
 *
 * @author Liz
 * @date 2019/9/19
 */
@Slf4j
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class CommonExceptionHandler {
    /**
     * Handle exception rest response.
     *
     * @param e the e
     * @return the rest response
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse handleException(Exception e) {
        log.error("系统内部异常，异常信息：", e);
        return new RestResponse().code(HttpStatus.INTERNAL_SERVER_ERROR).message("系统内部异常");
    }

    /**
     * Handle params invalid exception rest response.
     *
     * @param e the e
     * @return the rest response
     */
    @ExceptionHandler(value = CommonException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse handleParamsInvalidException(CommonException e) {
        log.error("系统错误：{}", e.getMessage());
        return new RestResponse().code(HttpStatus.INTERNAL_SERVER_ERROR).message(e.getMessage());
    }

    /**
     * 统一处理请求参数校验(实体对象传参)
     *
     * @param e BindException
     * @return RestResponse rest response
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse validExceptionHandler(BindException e) {
        StringBuilder message = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors) {
            message.append(error.getField()).append(error.getDefaultMessage()).append(StringPool.COMMA);
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return new RestResponse().code(HttpStatus.BAD_REQUEST).message(message.toString());

    }

    /**
     * 统一处理请求参数校验(普通传参)
     *
     * @param e ConstraintViolationException
     * @return RestResponse rest response
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse handleConstraintViolationException(ConstraintViolationException e) {
        StringBuilder message = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            Path path = violation.getPropertyPath();
            String[] pathArr = StringUtils.splitByWholeSeparatorPreserveAllTokens(path.toString(), StringPool.DOT);
            message.append(pathArr[1]).append(violation.getMessage()).append(StringPool.COMMA);
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return new RestResponse().code(HttpStatus.BAD_GATEWAY).message(message.toString());
    }

    /**
     * Handle limit access exception rest response.
     *
     * @param e the e
     * @return the rest response
     */
    @ExceptionHandler(value = LimitAccessException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public RestResponse handleLimitAccessException(LimitAccessException e) {
        log.warn(e.getMessage());
        return new RestResponse().code(HttpStatus.TOO_MANY_REQUESTS).message(e.getMessage());
    }

    /**
     * Handle unauthorized exception.
     *
     * @param e the e
     */
    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public RestResponse handleUnauthorizedException(Exception e) {
        log.error("权限不足，{}", e.getMessage());
        return new RestResponse().code(HttpStatus.FORBIDDEN).message(e.getMessage());
    }
}
