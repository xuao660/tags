package com.yupi.usercenter.once;

import com.alibaba.excel.EasyExcel;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author xuao
 * @Date 2024/7/28 16:12
 * @Description:
 */
public class ImportUserExcel {

    private static final Gson gson = new Gson();

    public static void main(String[] args){
        synchronousRead();
//        if(StringUtils.isNotEmpty(" ")){
//            System.out.println("11");
//        }
    }
    //1、将excel中用户信息读取到内存中
    public static void synchronousRead() {
        String fileName ="D:\\YuPi\\伙伴匹配\\yupao-backend\\src\\main\\resources\\testFile.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<ExcelUserData> list = EasyExcel.read(fileName).head(ExcelUserData.class).sheet().doReadSync();
        for(ExcelUserData item : list){
            System.out.println(":"+item.getUsername());

        }
        System.out.println("数据量:"+list.size());
            Map<String, List<ExcelUserData>> listMap = list.stream().filter(item -> StringUtils.isNotEmpty(item.getUsername())).collect(Collectors.groupingBy(ExcelUserData::getUsername));
        System.out.println("分组后数据量:"+listMap.size());
        for(Map.Entry<String,List<ExcelUserData>> entry : listMap.entrySet()){
            List<ExcelUserData> list1 = entry.getValue();
            if(list1.size()>1){
                System.out.println("重复用户名："+entry.getKey());
            }
        }
    }
}
