package com.yupi.usercenter.once;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
public class ExcelUserData {
    /**
     * 强制读取第三个 这里不建议 index 和 name 同时用，要么一个对象只用index，要么一个对象只用name去匹配，用名字去匹配，这里需要注意，如果名字重复，会导致只有一个字段读取到数据
     */
    @ExcelProperty("成员编号")
    private Long plantCode;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;
    @Override
    public String toString() {
        return "DataModel{columnWithSpaces='" + plantCode + '\'' + username+'}';
    }

}