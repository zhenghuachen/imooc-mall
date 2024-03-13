package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.common.ValidList;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.UpdateProductReq;
import com.imooc.mall.service.ProductService;
import com.imooc.mall.service.UploadService;
import io.swagger.annotations.ApiOperation;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

/**
 * 描述： 后台商品管理Controller
 */
@RestController
@Validated
public class ProductAdminController {
    @Autowired
    ProductService productService;

    @Autowired
    private UploadService uploadService;
    /**
     * 商品添加
     * @param addProductReq
     * @return
     */
    @PostMapping("admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq) {
        productService.add(addProductReq);
        return  ApiRestResponse.success();
    }

    // 为了在图片地址中保存URL，传入HttpServletRequest
    @PostMapping("admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) {
        // 获取上传文件原始名称
        /**
         * getOriginalFilename()是java.io.File类的一个方法，用于获取上传文件的原文件名。
         * 它返回一个字符串，表示上传文件的原文件名。
         * 在处理文件上传请求时，通常使用getOriginalFilename()方法获取上传文件的原始文件名，
         * 以便将其用作新文件名的的一部分，或者作为文件的唯一标识符。
         * 需要注意的是，getOriginalFilename()返回的文件名不包括文件扩展名，因为文件扩展名
         * 通常用于区分文件类型。因此，在处理文件上传请求时，通常需要将文件扩展名与文件名一起使
         * 用，以便正确地保存和处理文件。
         */
        String result = uploadService.uploadFile(file);
        return ApiRestResponse.success(result);
        /**
         * httpServletRequest.getRequestURL()是Java Servlet API中的一个方法，用于获取客户端
         * 请求的URL。返回一个字符串，表示客户端请求的完整URL，包括协议、主机名、端口号（如果有的话）
         * 和请求路径。
         * 在处理HTTP请求时，通常使用httpServletRequest.getRequestURL()来获取请求的URL。
         * 例如，在处理文件上传请求时，可以使用此方法获取上传文件的URL，以便将其保存到服务器上。
         * 需要注意的是，上述方法返回的URL不包括查询参数（如?param1=value1&param2=value2），
         * 因为这些参数通常用于传递请求参数，而不是用于表示请求的资源。
         */
//        try {
//
//            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL()+""))+"/images/"+newFileName);
//        } catch (URISyntaxException e) {
//            return ApiRestResponse.error(ImoocMallExceptionEnum.UPLOAD_FAILED);
//        }
    }

    @ApiOperation("后台更新商品")
    @PostMapping("/admin/product/update")
    public  ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除商品")
    @PostMapping("/admin/product/delete")
    public  ApiRestResponse deleteProduct(@RequestParam Integer id) {
        productService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台批量上下架接口")
    @PostMapping("/admin/product/batchUpdateSellStatus")
    public  ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids, @RequestParam Integer sellStatus) {
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }
    @ApiOperation("后台商品列表")
    @PostMapping("/admin/product/list")
    public  ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("后台批量上传商品接口")
    @PostMapping("/admin/upload/product")   // 上传文件的参数写法如下
    public  ApiRestResponse uploadProduct(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        String newFileName = uploadService.getNewFileName(multipartFile);
        // 创建文件
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR); // new File的参数是上传的位置
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        uploadService.createFile(multipartFile, fileDirectory, destFile);
        productService.addProductByExcel(destFile);
        return ApiRestResponse.success();
    }

    // 为了在图片地址中保存URL，传入HttpServletRequest
    @PostMapping("admin/upload/image")
    public ApiRestResponse uploadImage(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) throws IOException {
        String result = uploadService.uploadImage(file);
        return ApiRestResponse.success(result);
//        try {
//            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL()+""))+"/images/"+newFileName);
//        } catch (URISyntaxException e) {
//            return ApiRestResponse.error(ImoocMallExceptionEnum.UPLOAD_FAILED);
//        }
    }


    @ApiOperation("后台批量更新商品")
    @PostMapping("/admin/product/batchUpdate")
    public ApiRestResponse batchUpdateProduct(@Valid @RequestBody List<UpdateProductReq> updateProductReqList) {
        for (int i = 0; i < updateProductReqList.size() ; i++) {
            UpdateProductReq updateProductReq = updateProductReqList.get(i);
            // 方法一： 手动校验
            if (updateProductReq.getPrice() < 1) {
                throw new ImoocMallException(ImoocMallExceptionEnum.PRICE_TO_LOW);
            }
            if (updateProductReq.getStock() > 10000) {
                throw new ImoocMallException(ImoocMallExceptionEnum.STOCK_TO_MANY);
            }
            Product product = new Product();
            BeanUtils.copyProperties(updateProductReq, product);
            productService.update(product);
        }

        return ApiRestResponse.success();
    }
    @ApiOperation("后台批量更新商品,validList验证")
    @PostMapping("/admin/product/batchUpdate2")
    public ApiRestResponse batchUpdateProduct2(@Valid @RequestBody ValidList<UpdateProductReq> updateProductReqList) {
        for (int i = 0; i < updateProductReqList.size() ; i++) {
            UpdateProductReq updateProductReq = updateProductReqList.get(i);
            // 方法二： 自定义列表
            Product product = new Product();
            BeanUtils.copyProperties(updateProductReq, product);
            productService.update(product);
        }

        return ApiRestResponse.success();
    }

    @ApiOperation("后台批量更新商品,@Validated验证")
    @PostMapping("/admin/product/batchUpdate3")
    public ApiRestResponse batchUpdateProduct3(@Valid @RequestBody List<UpdateProductReq> updateProductReqList) {
        for (int i = 0; i < updateProductReqList.size() ; i++) {
            UpdateProductReq updateProductReq = updateProductReqList.get(i);
            // 方法三： @validated
            Product product = new Product();
            BeanUtils.copyProperties(updateProductReq, product);
            productService.update(product);
        }
        return ApiRestResponse.success();
    }
}
