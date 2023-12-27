package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 描述： 分类目录Service
 */
public interface CategoryService {
    void add(AddCategoryReq addCategoryReq) throws ImoocMallException;

    void  update(Category updateCategory);

    void  delete(Integer id);

    PageInfo listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize);

    List<CategoryVO> listCategoryForCustomer(Integer parentId);
}
