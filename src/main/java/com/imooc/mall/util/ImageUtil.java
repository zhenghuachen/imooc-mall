package com.imooc.mall.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.poi.hpsf.Thumbnail;

import java.io.IOException;

/**
 * 图片工具类
 */
public class ImageUtil {
    public static void main(String[] args) throws IOException {
        String path = "/Users/chenzh12/Desktop/";  // 桌面
        // 切割
        Thumbnails.of(path + "caomei.jpg").sourceRegion(Positions.BOTTOM_RIGHT, 200,200).size(200,200).toFile(path+"crop.jpg");
        // 缩放: 分为按比例缩放scale和按长宽缩放size
        Thumbnails.of(path + "caomei.jpg").scale(0.7).toFile(path+"scale1.jpg");
        Thumbnails.of(path + "caomei.jpg").scale(1.5).toFile(path+"scale2.jpg");
        Thumbnails.of(path + "caomei.jpg").size(500, 500).keepAspectRatio(false).toFile(path+"size1.jpg");
        // keepAspectRatio(true)等比缩放
        Thumbnails.of(path + "caomei.jpg").size(500,500).keepAspectRatio(true).toFile(path+"size2.jpg");
        // 旋转
        Thumbnails.of(path + "caomei.jpg").size(500,500).keepAspectRatio(true).rotate(90).toFile(path+"size2.jpg");
    }
}
