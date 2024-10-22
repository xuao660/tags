package com.yupi.usercenter.model.domain.request;

import lombok.Data;

/**
 * @Author xuao
 * @Date 2024/10/19 21:36
 * @Description:
 */
@Data
public class TeamJoinRequest {
    Long teamId;
    String password;
}
