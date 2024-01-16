package com.imooc.mall.controller;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.filter.UserFilter;
import com.imooc.mall.model.vo.CartVO;
import com.imooc.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述： 购物车Controller
 */
@RestController
@RequestMapping("/cart")   // 统一添加购物车请求前缀
public class CartController {
    @Autowired
    CartService cartService;

    /**
     * 横向越权是指攻击者试图访问或修改其他用户的数据，而不是自己的数据。
     * 此处，为了确保用户只能访问和修改自己的购物车信息，而不是其他用户的购物车信息，需要在后端进行身份
     * 验证。具体而言，通过调用UserFilter.currentUser.getId()方法来获取当前登录用户的ID，并将其
     * 作为参数传递给购物车相关的服务方法。
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("购物车列表")
    public ApiRestResponse list () {
        // 内部获取用户ID，防止横向越权
        List<CartVO> cartList = cartService.list(UserFilter.currentUser.getId());
        return ApiRestResponse.success(cartList);
    }

    @PostMapping("/add")
    @ApiOperation("添加商品到购物车")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count) {
        //用户ID，商品ID，数量
        List<CartVO> cartVOList = cartService.add(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/update")
    @ApiOperation("更新购物车")
    public ApiRestResponse update(@RequestParam Integer productId, @RequestParam Integer count) {
        //用户ID，商品ID，数量
        List<CartVO> cartVOList = cartService.update(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/delete")
    @ApiOperation("删除购物车")
    public ApiRestResponse delete(@RequestParam Integer productId) {
        // 不能传入userID,cartID, 否则可以删除别人的购物车
        List<CartVO> cartVOList = cartService.delete(UserFilter.currentUser.getId(), productId);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/select")
    @ApiOperation("选择/不选择某商品")
    public ApiRestResponse select(@RequestParam Integer productId, @RequestParam Integer selected) {
        // 用户ID，商品ID，是否选中
        List<CartVO> cartVOList = cartService.selectOrNot(UserFilter.currentUser.getId(), productId, selected);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/selectAll")
    @ApiOperation("全选择/全不选择某商品")
    public ApiRestResponse selectAll(@RequestParam Integer selected) {
        //用户ID，是否选中
        List<CartVO> cartVOList = cartService.selectAllOrNot(UserFilter.currentUser.getId(), selected);
        return ApiRestResponse.success(cartVOList);
    }

}
