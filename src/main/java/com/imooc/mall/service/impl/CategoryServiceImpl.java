package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.CategoryMapper;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
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

    @Override
    @Cacheable(value = "listCategoryForCustomer")
    // @Cacheable注解，用于实现缓存，标记一个方法的结果应该被缓存
    // 返回CategoryVO 列表
    public List<CategoryVO> listCategoryForCustomer(Integer parentId) {
        ArrayList<CategoryVO> categoryVOList = new ArrayList<>();
        // 额外的数据处理，通过新写的方法解决
        recursivelyFindCategories(categoryVOList, parentId);  //一级目录父目录为0
        return categoryVOList;
    }
    private void recursivelyFindCategories(List<CategoryVO> categoryVOList, Integer parentId) {
        // 递归获取所有子类别，并组合成为一个"目录树"
        // 递归停止条件：到达最后一层(无子目录),查找的categoryList为空,会跳过if判断，当前递归结束
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        /**
         * CollectionUtils.isEmpty()是Apache Commons Lang库中的一个方法，用于检查一个集合是否为空。
         * CollectionUtils.isEmpty()方法接收一个参数，即需要检查的集合对象。它返回一个布尔值，表示集合是否为空。
         * 在Java中，我们可以使用Collection接口及其实现类（如List、Set、Map等）来表示集合。如果一个集合
         * 没有任何元素，那么它就被视为空集合。
         */
        if (!CollectionUtils.isEmpty(categoryList)) {   //ifn 会自动生成if Null判断
            for (int i = 0; i < categoryList.size(); i++) {
                Category category = categoryList.get(i);
                CategoryVO categoryVO = new CategoryVO();
                /**
                 * BeanUtils.copyProperties()方法接收两个参数，分别是源对象和目标对象。
                 * 它将源对象的属性值复制到目标对象中，覆盖目标对象中已存在的属性值。
                 */
                BeanUtils.copyProperties(category, categoryVO);
                categoryVOList.add(categoryVO);   // 完成最外层categoryVO
                // 递归获取子目录:传入当前categoryVO的childCategory,以及当前categoryVO的ID作为parenId
                recursivelyFindCategories(categoryVO.getChildCategory(), categoryVO.getId());
            }
        }
    }
}
