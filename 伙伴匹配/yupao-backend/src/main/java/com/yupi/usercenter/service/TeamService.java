package com.yupi.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.dto.TeamQuery;
import com.yupi.usercenter.model.domain.request.TeamUpdateRequest;
import com.yupi.usercenter.model.domain.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author xuao
* @description 针对表【team】的数据库操作Service
* @createDate 2024-09-08 16:07:15
*/
public interface TeamService extends IService<Team> {

    boolean addTeam(Team team, User loginUser);

    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    boolean updateTeamById(TeamUpdateRequest team, HttpServletRequest request);

    boolean joinTeam(Long teamId, String password,HttpServletRequest request);

    /**
     * 退出队伍
     * @param teamId
     * @param loginUser
     * @return
     */
    boolean exitTeam(Long teamId, User loginUser);

    boolean deleteTeam(Long teamId,HttpServletRequest request);

    public void testA(String param);

    public void testB(String param);

    List<Team> listJoinTeam(Long id);

    List<Team> listCreateTeam(Long id);
}
