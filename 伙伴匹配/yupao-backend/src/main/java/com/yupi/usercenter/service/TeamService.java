package com.yupi.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.dto.TeamQuery;
import com.yupi.usercenter.model.domain.vo.TeamUserVO;

import java.util.List;

/**
* @author xuao
* @description 针对表【team】的数据库操作Service
* @createDate 2024-09-08 16:07:15
*/
public interface TeamService extends IService<Team> {

    boolean addTeam(Team team, User loginUser);

    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);
}
