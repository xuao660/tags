package com.yupi.usercenter.model.domain.request;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * @Author xuao
 * @Date 2024/9/21 20:54
 * @Description:
 */
@Data
public class TeamUpdateRequest {

    @NotNull
    private Long id;


    /**
     * 标题
     */
    @NotBlank(message = "队伍名称不能为空")
    @Size(min = 1,max = 20,message = "队伍名称的长度应该大于1小于20")
    private String teamName;

    /**
     * 描述
     */
    @NotNull(message = "队伍描述不能为空")
    @Size(min = 1,max = 512,message = "队伍名称的长度应该大于1小于512")
    private String description;


    /**
     * 过期时间
     */
    @NotNull(message = "过期时间不能为空")
    private Date expireTime;

    /**
     * 队伍密码
     */
    private String password;



    /**
     * 0-公开 1-私密  2-加密
     */
    @NotNull(message = "状态不能为空")
    private Integer status;



}
