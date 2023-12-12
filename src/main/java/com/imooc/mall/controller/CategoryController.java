package com.imooc.mall.controller;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 描述： 目录Controller
 */
@Controller
public class CategoryController {

    @Autowired
    UserService userService;
    @Autowired
    CategoryService categoryService;

    /**
     * 添加分类（后台）
     * @param session  对用户进行校验,是否登录，以及是不是管理员
     * @param addCategoryReq 封装参数,统一传入
     * @return
     */
    @PostMapping("admin/category/add")   // 定义URL
    @ResponseBody
    public ApiRestResponse addCategory(HttpSession session,@RequestBody AddCategoryReq addCategoryReq){
        // 入参校验
        if (addCategoryReq.getName() == null || addCategoryReq.getType() == null || addCategoryReq.getParentId() == null || addCategoryReq.getOrderNum() == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.PARA_NOT_NULL);
        }
        // 获取当前用户，并转成User对象
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {   // 当前对象为空，说明没登录
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        // 校验管理员
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            // 是管理员执行添加分类操作
            categoryService.add(addCategoryReq);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }

}
