package com.yupi.usercenter.model.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author xuao
 * @Date 2024/9/17 9:46
 * @Description:
 */
@Data
public class TeamUserVO implements Serializable {
    /**
     * id
     */
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
     * 最大人数
     */
    @Min(1)
    @Max(20)
    @NotBlank(message = "最大人数不能为空")
    private Integer maxNum;

    /**
     * 过期时间
     */
    @NotNull(message = "过期时间不能为空")
    private Date expireTime;



    /**
     * 创建人id
     */
    @NotNull(message = "创建人不能为空")
    private Long userID;

    /**
     * 0-公开 1-私密  2-加密
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 队长ID
     */
    @NotNull(message = "队长不能为空")
    private Long captainId;
    /**
     * 加入队伍的用户集合
     */
    List<UserVO> users;
}
