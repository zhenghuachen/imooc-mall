package com.imooc.mall.service.impl;

import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.CategoryMapper;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述： 目录分类Service实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper; //引入mapper,并再Mapper中增加查询重名的方法
    @Override
    public  void add(AddCategoryReq addCategoryReq) throws ImoocMallException {
        Category category = new Category();
        // BeanUtils.copyProperties()方法接收两个参数：第一个参数是要被复制属性的对象，第二个参数是要将属性值复制到的对象。
        // 该方法将遍历第一个对象的属性，并将其值复制到第二个对象的相应属性(字段类型和字段名相同的属性)中。
        BeanUtils.copyProperties(addCategoryReq, category);
        Category categoryOld = categoryMapper.selectByName(addCategoryReq.getName());  //存储表中是否有对应数据
        if (categoryOld != null) {  // 不允许重名
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = categoryMapper.insertSelective(category);  // 插入数据条数
        if(count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
        }
    }
}
