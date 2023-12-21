package com.imooc.mall.controller;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.request.UpdateCategoryReq;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 描述： 目录Controller
 */
// @Controller注解，用于标识一个类或接口作为一个控制器。
@Controller
public class CategoryController {

    @Autowired
    UserService userService;  // 包含检验管理员的方法
    @Autowired
    CategoryService categoryService;

    /**
     * 后台添加目录
     * @param session  对用户进行校验,是否登录，以及是不是管理员
     * @param addCategoryReq 封装参数,统一传入
     * @return
     */
    // ApiOperation是一个用于描述API操作的注解，它可以包含操作的描述、输入参数、输出参数等信息。在这个示例中，
    // 我们定义了一个ApiOperation注解，用于描述一个名为“后台添加目录”的操作。
    @ApiOperation("后台添加目录")
    @PostMapping("admin/category/add")   // 定义添加分类URL
    @ResponseBody
    // HttpSession将用来校验用户是否登录，以及是否有管理员权限；
    // @RequestBody注解表示将 HTTP 请求体中的数据绑定到方法的参数上。具体来说，当一个HTTP请求到达服务
    // 器时，服务器会将请求体中的数据读取出来，并将其转换为方法参数所需要的类型(通常为Java对象)，然后将
    // 其传递给方法进行处理。
    // @Valid是用于数据验证的注解。当对某个字段进行数据验证时，如果该字段不符合预期，@Valid注解会抛出一个异常，从而触发后续的逻辑。
    public ApiRestResponse addCategory(HttpSession session,@Valid  @RequestBody AddCategoryReq addCategoryReq){
        // 入参校验
//        if (addCategoryReq.getName() == null || addCategoryReq.getType() == null || addCategoryReq.getParentId() == null || addCategoryReq.getOrderNum() == null) {
//            return ApiRestResponse.error(ImoocMallExceptionEnum.PARA_NOT_NULL);
//        }
        // 通过Session获取当前用户，并转成User对象
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

    /**
     * 后台更新目录
     *
     */
    @PostMapping("admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(@Valid @RequestBody UpdateCategoryReq updateCategoryReq, HttpSession session ) {
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null){  // 检验是否登录
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        // 校验是否为管理员
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            // 是管理员，执行更新操作
            Category category = new Category();
            BeanUtils.copyProperties(updateCategoryReq, category);
            categoryService.update(category);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }
}
