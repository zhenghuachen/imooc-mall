package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.ProductMapper;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.query.ProductlistQuery;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.ProductListReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryService categoryService;
    @Override
    public void add(AddProductReq addProductReq) {
        Product product = new Product();
        // 用于将一个对象的所有属性值复制到另一个对象
        BeanUtils.copyProperties(addProductReq, product);
        // 检测重名情况
        Product productOld = productMapper.selectByName(addProductReq.getName());
        if (productOld != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        // 校验插入是否成功
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void  update(Product updateProduct) {
        Product productOld = productMapper.selectByName(updateProduct.getName());
        // 同名且不同id，不能继续修改
        if (productOld != null && !productOld.getId().equals(updateProduct.getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        // 校验插入是否成功
        int count = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public  void  delete(Integer id) {
        Product productOld = productMapper.selectByPrimaryKey(id);
        // 查不到记录，无法删除，删除失败
        if (productOld == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        productMapper.batchUpdateSellStatus(ids, sellStatus);
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        /**
         * PageHelper分页查询商品列表
         * PageHelper.startPage(pageNum, pageSize);
         * 表示开始分页查询，其中pageNum表示当前页码，pageSize表示每页显示的记录数。
         * List<Product> products = productMapper.selectListForAdmin();
         * 表示将查询结果存储到products列表中。
         * PageInfo pageInfo = new PageInfo(products);
         * 表示创建一个PageInfo对象，将products列表作为参数传递，表示当前页的商品信息。
         * 将查询结果封装为PageInfo对象返回。
         */
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }

    @Override
    public Product datail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }

    @Override
    public PageInfo list(ProductListReq productListReq) {
        // 构建query对象
        ProductlistQuery productlistQuery = new ProductlistQuery();
        // 搜索处理
        /**
         * StringUtils是Apache Commons Lang库中的一个类，提供了许多字符串
         * 处理的方法，如检查字符串是否为空、转换字符串大小写、格式化日期等。
         * StringUtils.isEmpty()用于检查一个字符串是否为空字符串(即长度为0)。
         * StringBuilder()是Java中String类的构造方法之一，用于创建一个可修改
         * 的字符串对象。
         * StringBuilder对象的主要优点是它是一个可修改的字符串对象，可以在运行
         * 时动态地添加或删除字符。
         */
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {
            // keyword左右各添加一个%号
            String keyword = new StringBuilder().append("%").append(productListReq.getKeyword()).append("%").toString();
            productlistQuery.setKeyword(keyword);
        }
        // 目录处理：如果查某个目录下的商品，不仅是需要查出该目录下的，还要把所有子目录的所有商品都查出来，所以要拿到一个目录id的List；
        if (productListReq.getCategoryId() != null) {// 传了categoryId
            // 拿到以productListReq.getCategoryId()为根节点，所有目录VO的List
            List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(productListReq.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOList, categoryIds);
            productlistQuery.setCategoryIds(categoryIds);
        }
        // 排序处理, 枚举限制前端传的字段符合要求
        // 获取排序字段
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }
        List<Product> productList = productMapper.selectList(productlistQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return  pageInfo;
    }
    // 获取所有目录Id（包括子目录），平铺于列表中
    private void getCategoryIds(List<CategoryVO> categoryVOList, ArrayList<Integer> categoryIds) {
        for (int i = 0; i < categoryVOList.size(); i++) {
            CategoryVO categoryVO = categoryVOList.get(i);
            if (categoryVO != null) {  // 自身含有Id
                categoryIds.add(categoryVO.getId());
                // 递归
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }

}
