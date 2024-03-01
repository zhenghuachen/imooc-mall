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
import com.imooc.mall.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

    /**
     * 从上传的Excel文件中读取商品信息并将商品逐个添加到数据库
     * 首先调用readProductsFromExcel方法从Excel文件中读取商品信息并将
     * 其存储在products列表中。
     * 然后,通过循环遍历products列表中的每个商品，对每个商品进行如下处理：
     * 首先通过商品名称查询数据库，如果数据库中已经存在同名商品，则抛出自定
     * 义异常ImoocMallException，错误类型为NAME_EXISTED。
     * 接着将商品信息插入到数据库中，如果插入失败，则同样抛出自定义异常
     * ImoocMallException，错误类型为CREATE_FAILED
     */
    @Override
    public void addProductByExcel(File destFile) throws IOException {
        // 从Excel文件中读取商品信息,并转换为商品列表
        List<Product> products = readProductsFromExcel(destFile);
        for (int i = 0; i < products.size(); i++) {
            Product product =  products.get(i);
            Product productOld = productMapper.selectByName(product.getName()); // 通过商品名称来查询数据库中是否已存在同名商品
            if (productOld != null) {  // 查询到同名商品，则抛出自定义异常
                throw  new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
            }
            int count = productMapper.insertSelective(product); // 不存在同名商品，则向数据库中插入当前商品信息，存储受影响的行数
            if (count == 0) {
                throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
            }
        }
    }

    /**
     * 私有方法,用于将输入的Excel文件转换为商品列表
     * 首先创建了一个空的商品列表listProducts，然后通过文件输入流FileInputStream从给定的Excel文件
     * 中获取输入流。
     * 接着使用XSSFWorkbook类来加载Excel文件，获取第一个工作表firstsheet，并通过迭代器逐行遍历工作表
     * 中的数据。对于每一行数据，再通过迭代器逐个单元格遍历，将单元格中的数据填充到一个 Product 对象中，
     * 最终将该对象添加到商品列表中。
     * 最后关闭工作簿和输入流，并返回填充好的商品列表。
     * @param excelFile
     * @return
     * @throws IOException
     */
    private List<Product> readProductsFromExcel(File excelFile) throws IOException {
        ArrayList<Product> listProducts = new ArrayList<>();  // 用于存储读取到的商品信息
        FileInputStream inputStream = new FileInputStream(excelFile);  // 文件输入流 inputStream，用于读取Excel文件中的数据

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream); // 使用XSSFWorkbook类加载输入流，创建了一个新版本的Excel工作簿workbook
        XSSFSheet firstsheet =  workbook.getSheetAt(0); // 获取第一个工作表,index从0还是1开始需要根据实际情况确认
        Iterator<Row> iterator = firstsheet.iterator();  // 通过工作表的迭代器创建一个行迭代器iterator，用于逐行遍历工作表中的数据
        while(iterator.hasNext()) {
            Row nextRow = iterator.next(); // 逐行遍历
            Iterator<Cell> cellIterator = nextRow.cellIterator();  // 逐个单元格遍历
            Product aProduct = new Product();  // 创建Product对象aProduct，用于存储当前行数据对应的商品信息
            // 读取单元格填充aProduct
            while (cellIterator.hasNext()) {
                Cell nextCell = cellIterator.next();
                int columnIndex = nextCell.getColumnIndex(); //获取单元格的索引，即列号
                switch (columnIndex) {
                    case 0:
                        aProduct.setName((String) ExcelUtil.getCellValue(nextCell)); // 针对不同的列号执行相应的操作，将单元格数据填充到aProduct对象的相应属性中
                        break;
                    case 1:
                        aProduct.setImage((String) ExcelUtil.getCellValue(nextCell));
                        break;
                    case 2:
                        aProduct.setDetail((String) ExcelUtil.getCellValue(nextCell));
                        break;
                    case 3:
                        Double cellValue = (Double) ExcelUtil.getCellValue(nextCell); // excel中数字类型默认为Double
                        aProduct.setCategoryId(cellValue.intValue());
                        break;
                    case 4:
                        cellValue = (Double) ExcelUtil.getCellValue(nextCell); // excel中数字类型默认为Double
                        aProduct.setPrice(cellValue.intValue());
                        break;
                    case 5:
                        cellValue = (Double) ExcelUtil.getCellValue(nextCell); // excel中数字类型默认为Double
                        aProduct.setStock(cellValue.intValue());
                        break;
                    case 6:
                        cellValue = (Double) ExcelUtil.getCellValue(nextCell); // excel中数字类型默认为Double
                        aProduct.setStatus(cellValue.intValue());
                        break;
                    default:
                        break;
                }
            }
            listProducts.add(aProduct); // 将填充好数据的aProduct对象添加到商品列表listProducts中
        }
        workbook.close();  // 关闭Excel工作簿
        inputStream.close();  // 关闭文件输入流
        return listProducts;
    }

}
