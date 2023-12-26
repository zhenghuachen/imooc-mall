package com.imooc.mall.controller;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.UpdateProductReq;
import com.imooc.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * 描述： 后台商品管理Controller
 */
@RestController
public class ProductAdminController {
    @Autowired
    ProductService productService;

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
        String fileName = file.getOriginalFilename();
        // 获取图片文件格式,后缀
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 生成文件名称UUID
        /**
         * UUID.randomUUID()是Java中java.util.UUID类的一个静态方法，用于生成一个随机的
         * UUID（通用唯一标识符）。它返回一个UUID对象，表示一个随机的全局唯一标识符。
         * 在Java中，通常使用UUID来表示全局唯一的标识符，如设备ID、用户ID、文件ID等。
         * UUID.randomUUID()方法生成的UUID是随机的，因此在分布式系统中通常具有唯一性。
         * 需要注意的是，UUID.randomUUID()生成的UUID是全局唯一的，但是它并不是一个加密哈希值，
         * 因此在某些情况下可能不是完全唯一的。如果需要完全唯一的标识符，可以考虑使用加密哈希值或
         * 其他更复杂的方法。
         */
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;
        // 创建文件
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {
                throw new ImoocMallException(ImoocMallExceptionEnum.MKDIR_FAILED);
            }
        }
        /**
         * file.transferTo(destFile)是Java中java.nio.file.Files类的一个静态方法，用于
         * 将一个文件从一个位置移动到另一个位置。它将文件file从当前位置移动到目标位置destFile，
         * 并返回一个boolean值，表示移动是否成功。
         * 在Java中，使用Files.copy()方法可以将文件从一个位置移动到另一个位置。例如，可以使用
         * Files.copy(file.toPath(), destFile.toPath(),
         * StandardCopyOption.REPLACE_EXISTING)将文件file移动到目标位置destFile。
         * 需要注意的是，transferTo()方法将文件移动到目标位置，而copy()方法将文件复制到目标位置。
         * 如果要将文件移动到目标位置，建议使用transferTo()方法，因为它可以更有效地处理大文件和
         * 并发访问。
         */
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * httpServletRequest.getRequestURL()是Java Servlet API中的一个方法，用于获取客户端
         * 请求的URL。返回一个字符串，表示客户端请求的完整URL，包括协议、主机名、端口号（如果有的话）
         * 和请求路径。
         * 在处理HTTP请求时，通常使用httpServletRequest.getRequestURL()来获取请求的URL。
         * 例如，在处理文件上传请求时，可以使用此方法获取上传文件的URL，以便将其保存到服务器上。
         * 需要注意的是，上述方法返回的URL不包括查询参数（如?param1=value1&param2=value2），
         * 因为这些参数通常用于传递请求参数，而不是用于表示请求的资源。
         */
        try {
            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL()+""))+"/images/"+newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.UPLOAD_FAILED);
        }
    }
    // 获取IP和端口号
    private URI getHost(URI uri) {
        URI effectiveURI;
        // 新建URI
        /**
         * URI对象表示URL的统一表示，它提供了对URL的访问和验证功能。effectiveURI对象表示一个有效的URL，
         * 它是从URI对象中提取出的部分。
         * 在这个代码中，首先使用URI.create(uriString)将原始URL字符串转换为URI对象。然后，通过设置
         * scheme、userInfo、host和port属性来填充effectiveURI对象。最后，返回填充后的effectiveURI对象。
         * 这个方法的目的是将原始URL字符串转换为有效的URL，以便在Java代码中使用effectiveURI对象提供的
         * 方法和功能，如验证URL、解析路径等。
         * 需要注意的是，effectiveURI对象可能不包含原始URL的所有信息，因此在处理数据时，需要确保使用完整
         * 的URL信息。
         */
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null,null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;   // http://127.0.0.1:8083
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
}
