package com.imooc.mall.common;

import com.imooc.mall.exception.ImoocMallExceptionEnum;

/**
 * 描述： 通用返回对象
 */
public class ApiRestResponse<T> {
    // 属性status、msg、data
    private Integer status;

    private String msg;

    private  T data;   // 表示返回数据data是一个泛型

    private static final int OK_CODE=10000;

    private static final String OK_MSG = "SUCCESS";

    // 弹出生成构造函数快捷键：control + enter; 可以通过右键找到generate，其右侧显示的是快捷键
    public ApiRestResponse(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public ApiRestResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }
    // 默认构造函数不需要任何信息，也就是默认的信息，意味着成功了
    public ApiRestResponse() {
        this(OK_CODE,OK_MSG);
    }

    public static <T> ApiRestResponse<T> success() {
        return new ApiRestResponse<>();
    }

    public  static <T> ApiRestResponse<T> success(T result) {
        ApiRestResponse<T> response = new ApiRestResponse<>();
        response.setData(result);
        return  response;
    }

    public static  <T> ApiRestResponse<T> error(Integer code, String msg) {
        return new ApiRestResponse<>(code,msg);
    }

    // 传入枚举类, 新建枚举类可以将错误统一管理
    public static  <T> ApiRestResponse<T> error(ImoocMallExceptionEnum ex) {
        return new ApiRestResponse<>(ex.getCode(), ex.getMsg());
    }

    @Override
    public String toString() {
        return "ApiRestResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
