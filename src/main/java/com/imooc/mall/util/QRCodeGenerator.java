package com.imooc.mall.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * 描述： 生成二维码工具
 */
public class QRCodeGenerator {
    /**
     * 类定义：QRCodeGenerator类包含一个静态方法generatorQRCodeImage，用于生成二维码图像。
     * generatorQRCodeImage方法：这个方法接受四个参数：要编码的文本、二维码的宽度和高度、二维码图像
     * 在方法中，先创建了一个 QRCodeWriter 对象，用于将字符串编码为二维码图像。
     * 然后，通过 QRCodeWriter.encode() 方法将文本内容编码为 QRCode 的 BitMatrix 矩阵。
     * 接着，创建一个 Path 对象，用于指定二维码图片的保存路径。
     * 最后，通过MatrixToImageWriter.writeToPath()方法将BitMatrix矩阵写入到指定路径的PNG文件中。
     */
    public  static void generatorQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    // 调试方法： 调用generatorQRCodeImage方法，生成一个包含文本"Hello World"的二维码图像，并将其保存在一个PNG文件中。
    public  static void main(String[] args) {
        try {
            generatorQRCodeImage("Hello World", 350, 350, "/Users/chenzh12/Desktop/Learn/imooc-mall-prepare-static/QRTesr.png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
