package com.yupi.usercenter.model.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yupi.usercenter.model.domain.request.PageRequest;
import lombok.Data;

/**
 * @Author xuao
 * @Date 2024/9/8 20:50
 * @Description:team查询请求参数
 */

@Data
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 关键词
     */
    private String stringText;

    /**
     * 标题
     */
    private String teamName;

    /**
     * 描述
     */
    private String description;
    /**
     * 0-公开 1-私密  2-加密
     */
    private Integer status;
    /**
     * 最大人数
     */
    private Integer maxNum;
}
