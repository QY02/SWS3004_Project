package org.nus.cloud.orderRecordService.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.nus.cloud.orderRecordService.utils.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result serviceException(HttpServletResponse response, ServiceException e) {
        return Result.error(response, e.getCode(), e.getMessage(), e.getCauseObject());
    }

}