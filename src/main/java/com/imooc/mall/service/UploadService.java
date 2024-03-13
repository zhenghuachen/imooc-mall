package com.imooc.mall.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 描述： 上传服务
 */
public interface UploadService {


    void createFile(@RequestParam("file") MultipartFile file, File fileDirectory, File destFile);

    String uploadFile(MultipartFile file);

    String getNewFileName(MultipartFile multipartFile);

    String uploadImage(MultipartFile file) throws IOException;
}
