package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.vo.CategoryVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 描述： 商品Service
 */
public interface ProductService {

    void add(AddProductReq addProductReq);

    void  update(Product updateProduct);

    void  delete(Integer id);

    void batchUpdateSellStatus(@RequestParam Integer[] ids, @RequestParam Integer sellStatus);
}
