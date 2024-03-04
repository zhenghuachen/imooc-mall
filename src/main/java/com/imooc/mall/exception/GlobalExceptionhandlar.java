package com.imooc.mall.exception;

import com.imooc.mall.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    // 自定义异常
    @ExceptionHandler(ImoocMallException.class)
    @ResponseBody   // 返回Json
    public Object handlarImoocMallException(ImoocMallException e) {
        log.error("ImoocMallException:", e);
        return ApiRestResponse.error(e.getCode(),e.getMessage());
    }

    /**
     * 参数检验异常
     * MethodArgumentNotValidException是Spring框架中javax.validation包下的一个异常类。
     * 当使用@Valid注解对方法参数进行验证时，如果某个参数的值不符合预期，Spring会抛出MethodArgumentNotValidException异常。
     * 这个异常通常包含一个ConstraintViolation对象，其中包含了具体的错误信息。
     * @ExceptionHandler注解用于处理异常。当Spring Boot应用程序抛出异常时，这个注解会自动捕获异常并处理它。
     * 注解可以应用于类级别或方法级别。
     * 当应用于类级别时，它表示该类中的所有方法都会处理指定的异常类型。
     * 当应用于方法级别时，它只表示该方法会处理指定的异常类型。
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody   // 返回Json
    public ApiRestResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException:", e);
        /**
         * getBindingResult()是Spring框架中javax.validation包下的BindingResult对象的一个方法，
         * 它用于获取在验证过程中收集的验证结果。
         * BindingResult对象包含了所有被验证的参数的验证结果，以及所有验证错误的详细信息。通过调用getBindingResult()方法，
         * 我们可以获取到BindingResult对象，并从中获取验证结果和错误信息。
         */
        return handleBindingResult(e.getBindingResult());
    }

    // 当验证过程中出现错误时，这个方法会根据BindingResult对象中的错误信息构建一个ApiRestResponse对象，并返回这个对象。
    private ApiRestResponse handleBindingResult(BindingResult result) {
        // 将异常处理为对外暴露的提示
        List<String> list = new ArrayList<>();
        if (result.hasErrors()) {   // result.hasErrors() 输入.if，选择快捷键自动生成if判断语句
            List<ObjectError> allErrors =  result.getAllErrors();
            // 输入itli,会自动弹出快捷选项，自动生成遍历代码for (int i = 0; i < allErrors.size(); i++) { }
            // 选中for，鼠标悬停可选择enhance... 使用增强for循环
            for (ObjectError objectError : allErrors) {
                String message = objectError.getDefaultMessage();
                list.add(message);
            }
        }
        if (list.size() == 0) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiRestResponse.error(ImoocMallExceptionEnum.REQUEST_PARAM_ERROR.getCode(), list.toString());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ApiRestResponse handle(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        StringBuilder builder = new StringBuilder();
        for (ConstraintViolation<?> violation: violations) {
            builder.append(violation.getMessage());
            break;
        }
        return ApiRestResponse.error(ImoocMallExceptionEnum.REQUEST_PARAM_ERROR.getCode(), builder.toString());
    }

}
