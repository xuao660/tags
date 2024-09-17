package com.yupi.usercenter.model.domain.request;

import lombok.Data;

/**
 * @Author xuao
 * @Date 2024/9/8 21:30
 * @Description:
 */

@Data
public class PageRequest {


    /**
     * 页面条数
     */
    int pageSize;
    /**
     * 页码
     */
    int pageNum;
}
