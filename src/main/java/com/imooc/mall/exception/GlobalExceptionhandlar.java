package com.imooc.mall.exception;

import com.imooc.mall.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 描述： 处理统一异常的handlar
 */
@ControllerAdvice  // @ControllerAdvice注解，用于标识一个类是一个控制器异常处理类。此处GlobalExceptionHandler类，它提供了全局异常处理逻辑
/**
 * 当Spring容器启动时，它会扫描所有带有@ControllerAdvice注解的类，并将它们注册为bean。然后，当控制器抛出异常时，Spring会将异常传递给异常处理类，以便进行异常处理。
 * @ControllerAdvice 注解只能标注接口或类，不能标注方法。因此，如果你的异常处理类需要提供多个异常处理方法，可以将其封装在一个接口中，并在类中实现该接口。然后，使用 @ControllerAdvice 注解标注接口的实现类。
 */
public class GlobalExceptionhandlar {

    private final Logger log= LoggerFactory.getLogger(GlobalExceptionhandlar.class);
    // @ExceptionHandler 注解指定了要处理的异常类型，这里是Exception类。当控制器抛出任何异常时，都会被handleException方法捕获到。
    // @ExceptionHandler 注解只能标注方法，不能标注类或接口
    @ExceptionHandler(Exception.class)
    @ResponseBody   // 返回Json
    public Object handlarException(Exception e) {
        log.error("Default Exception:", e);
        return ApiRestResponse.error(ImoocMallExceptionEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(ImoocMallException.class)
    @ResponseBody   // 返回Json
    public Object handlarImoocMallException(ImoocMallException e) {
        log.error("ImoocMallException:", e);
        return ApiRestResponse.error(e.getCode(),e.getMessage());
    }

}
