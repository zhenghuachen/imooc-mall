package com.imooc.mall.model.dao;

import com.imooc.mall.model.pojo.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
    // 通过名字查询Cataory，返回category实体
    Category selectByName(String name);

    List<Category> selectList();

    // 通过parentid 查询分类列表
    List<Category> selectCategoriesByParentId(Integer parentId);
}