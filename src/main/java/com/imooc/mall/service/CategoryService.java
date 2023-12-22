package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 描述： 分类目录Service
 */
public interface CategoryService {
    void add(AddCategoryReq addCategoryReq) throws ImoocMallException;

    void  update(Category updateCategory);

    void  delete(Integer id);

    PageInfo listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize);
}
