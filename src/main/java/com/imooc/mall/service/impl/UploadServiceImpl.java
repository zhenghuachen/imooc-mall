package com.imooc.mall.service.impl;

import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.service.UploadService;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 描述：上传服务实现类
 */
@Service
public class UploadServiceImpl implements UploadService {

    @Value("${file.upload.uri}")
    String uri;

    @Override
    public void createFile(@RequestParam("file") MultipartFile file, File fileDirectory, File destFile) {
        if (!fileDirectory.exists()) { //文件夹不存在，则创建fileDirectory.mkdir()
            if (!fileDirectory.mkdir()) {
                throw new ImoocMallException(ImoocMallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            // 将上传的文件内容保存到目标文件
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename(); //fileName: logo.png
        // 获取图片文件格式,后缀
        String suffixName = fileName.substring(fileName.lastIndexOf(".")); // suffixName: .png
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
        String newFileName = uuid.toString() + suffixName; // newfileName: b93fee5a-271c-4922-b57e-99b5c3413d82.png
        // 创建文件夹
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        createFile(file, fileDirectory, destFile);
        String address = uri;
        String result = "http://" +address + "/images/" + newFileName;
        return result;
    }

    @Override
    public String getNewFileName(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename(); // 获取文件原始名称
        String suffixName = fileName.substring(fileName.lastIndexOf(".")); // 获取后缀名
        // 生成uuid
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;  // 新文件名
        return newFileName;
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        // 获取上传文件原始名称
        String newFileName = getNewFileName(file);
        // 创建文件夹
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        createFile(file, fileDirectory, destFile);
        // 对目标文件进行缩略图处理，并添加水印
        Thumbnails.of(destFile) // 选择目标文件
                .size(Constant.IMAGE_SIZE, Constant.IMAGE_SIZE) // 设置缩略图大小
                .watermark(
                        Positions.BOTTOM_RIGHT, // 设置水印位置为右下角
                        ImageIO.read(new File(Constant.FILE_UPLOAD_DIR + Constant.WATER_MARK_JPG)), // 读取水印图片
                        Constant.IMAGE_OPACITY // 设置水印透明度
                )
                .toFile(new File(Constant.FILE_UPLOAD_DIR + newFileName)); // 输出处理后的文件到指定路径
        String address = uri;
        String result = "http://" +address + "/images/" + newFileName;
        return result;
    }
}
