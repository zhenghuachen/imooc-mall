package com.imooc.mall.util;

import org.apache.poi.ss.usermodel.Cell;

/**
 * 描述： 处理excel
 */
public class ExcelUtil {
    // 返回Object， 类型不确定
    public static  Object getCellValue (Cell cell) {
        switch (cell.getCellTypeEnum()) {   // 获取单元格类型
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
        }
        return null;
    }
}
