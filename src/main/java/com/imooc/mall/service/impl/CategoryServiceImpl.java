package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.CategoryMapper;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    @Override
    public void  update(Category updateCategory) {
        if (updateCategory.getName() != null) {   // 更新数据name不为空
            Category categoryOld = categoryMapper.selectByName(updateCategory.getName());//数据库原有数据
            if (categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
            }
        }
        int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public  void  delete(Integer id) {
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        // 查不到记录，无法删除，删除失败
        if (categoryOld == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public PageInfo listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        // 实现分页功能
        /**
         * PageHelper是Spring MVC中实现分页功能的工具类。它提供了一系列的方法，用于配置分页参数、分页查询和分页信息处理等。
         * startPage()方法接收三个参数：当前页码、每页显示的记录数以及排序字段。它返回一个PageInfo对象，
         * 表示当前页的分页信息。
         * PageInfo对象包含了分页数据、总记录数、每页显示的记录数等信息。
         * 它可以通过getList()方法获取分页数据，
         * 通过getTotal()方法获取总记录数，
         * 通过getPageNum()方法获取当前页码，
         * 通过getPageSize()方法获取每页显示的记录数。
         */
        PageHelper.startPage(pageNum, pageSize, "type, order_num");//orderBy是配置排序
        List<Category> categoryList = categoryMapper.selectList();// 返回category的列表
        PageInfo pageInfo = new PageInfo(categoryList);//查询结果categoryList封装为PageInfo对象的方法
        return  pageInfo;
    }
}
