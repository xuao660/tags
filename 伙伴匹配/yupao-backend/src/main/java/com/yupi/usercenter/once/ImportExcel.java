package com.yupi.usercenter.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ListUtils;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @Author xuao
 * @Date 2024/7/28 10:59
 * @Description:示例
 */
public class ImportExcel {
    /**
     * 最简单的读
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link ExcelUserData}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link UserDataListener}
     * <p>
     * 3. 直接读即可
     */
    private static final Gson gson = new Gson();

    public static void main(String[] args){
        synchronousRead();
    }
    public static void simpleRead() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName ="D:\\YuPi\\伙伴匹配\\yupao-backend\\src\\main\\resources\\testFile.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, ExcelUserData.class, new UserDataListener()).sheet().doRead();


    }
    public static void synchronousRead() {
        String fileName ="D:\\YuPi\\伙伴匹配\\yupao-backend\\src\\main\\resources\\testFile.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<ExcelUserData> list = EasyExcel.read(fileName).head(ExcelUserData.class).sheet().doReadSync();
        for (ExcelUserData data : list) {
            System.out.println("解析到一条数据:"+gson.toJson(data));
        }
        // 这里 也可以不指定class，返回一个list，然后读取第一个sheet 同步读取会自动finish
        List<Map<Integer, String>> listMap = EasyExcel.read(fileName).sheet().doReadSync();
        for (Map<Integer, String> data : listMap) {
            // 返回每条数据的键值对 表示所在的列 和所在列的值
            System.out.println("解析到一条数据:"+gson.toJson(data));
        }
    }
}
