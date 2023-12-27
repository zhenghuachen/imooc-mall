package com.imooc.mall.model.dao;

import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.query.ProductlistQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    // 通过名字查询商品是否存在
    Product selectByName(String name);

    // 批量上下架
    int batchUpdateSellStatus(@Param("ids") Integer[] ids, @Param("sellStatus") Integer sellStatus);

    List<Product> selectListForAdmin();

    // 前台查找数据库
    List<Product> selectList(@Param("query") ProductlistQuery query);
}