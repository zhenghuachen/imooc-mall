package com.imooc.mall.service;

import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.model.vo.CartVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述： 购物车Service
 */
@Service
public interface CartService {

    List<CartVO> add(Integer userId, Integer productId, Integer count);
}
