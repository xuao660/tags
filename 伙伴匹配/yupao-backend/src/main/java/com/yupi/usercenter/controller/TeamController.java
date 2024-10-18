package com.yupi.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.usercenter.common.BaseResponse;
import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.common.ResultUtils;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.dto.TeamQuery;
import com.yupi.usercenter.model.domain.request.TeamAddRequest;
import com.yupi.usercenter.model.domain.request.TeamIdRequest;
import com.yupi.usercenter.model.domain.request.TeamUpdateRequest;
import com.yupi.usercenter.model.domain.vo.TeamUserVO;
import com.yupi.usercenter.service.TeamService;
import com.yupi.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author xuao
 * @Date 2024/9/8 18:39
 * @Description:
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

    @Autowired
    TeamService teamService;
    @Autowired
    UserService userService;

    @PostMapping("add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        //判空
        if(teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        1、用户是否登录，未登录跳转到登录页面
        User loginUser = userService.getLoginUser(request);
//        User loginUser = (User) session.getAttribute(UserConstant.USER_LOGIN_STATE);
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team,teamAddRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean save = teamService.addTeam(team, loginUser);

        //返回内容
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"插入失败");
        }
        //系统自动会写id到实体
        return ResultUtils.success(team.getId());
    }
    @PostMapping("delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id){
        //判断id是否正常
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //删除队伍
        boolean b = teamService.removeById(id);
        //返回内容
        if(!b){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        //系统自动会写id到实体
        return ResultUtils.success(true);
    }

    @PostMapping("join")
    public BaseResponse<Boolean> joinTeam(Long teamId, String password,HttpServletRequest request){
//        2、校验参数是否为空，参数是否有效
        if(teamId == null || teamId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = teamService.joinTeam(teamId,password,request);
        if(!b){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        //系统自动会写id到实体
        return ResultUtils.success(true);
    }
    @PostMapping("update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest team, HttpServletRequest request){
        //判断参数是否正常
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //删除队伍
//        boolean b = teamService.updateById(team);
        boolean b = teamService.updateTeamById(team,request);

        //返回内容
        if(!b){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"修改失败");
        }
        //系统自动会写id到实体
        return ResultUtils.success(true);
    }
    @GetMapping("get")
    public BaseResponse<Team> getTeamById(@RequestBody long id){
        //判断参数是否正常
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取队伍
        Team team = teamService.getById(id);
        //返回内容
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"查询失败");
        }
        return ResultUtils.success(team);
    }

    @PostMapping("list")
    public BaseResponse<List<TeamUserVO>> list(@RequestBody TeamQuery teamQuery,HttpServletRequest request){
        //判断参数是否正常
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVO> list = teamService.listTeams(teamQuery,isAdmin);
        //返回内容
        if(CollectionUtils.isEmpty(list) ){
            throw new BusinessException(ErrorCode.NULL_ERROR,"查询数据为空");
        }
        return ResultUtils.success(list);
    }
    @PostMapping("drop")
    public BaseResponse<Boolean> dropTeam(@RequestBody TeamIdRequest team,HttpServletRequest request){
        if(team == null || team.getTeamId() == null ||team.getTeamId() < 1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = team.getTeamId();
        boolean b = teamService.deleteTeam(teamId,request);
        if(!b){
            throw new BusinessException(ErrorCode.NULL_ERROR,"删除队伍失败");

        }
        return ResultUtils.success(true);
    }

    @PostMapping("exit")
    public BaseResponse<Boolean> exitTeam(@RequestBody TeamIdRequest team, HttpServletRequest request){
//        1、校验参数是否为空
        if(team == null || team.getTeamId() == null ||team.getTeamId() < 1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = team.getTeamId();
//        2、获取当前用户
        User loginUser = userService.getLoginUser(request);
        boolean b = teamService.exitTeam(teamId,loginUser);
        //返回内容
        if(!b){
            throw new BusinessException(ErrorCode.NULL_ERROR,"退出队伍失败");
        }
        return ResultUtils.success(true);
    }
    @GetMapping("page")
    public BaseResponse<List<Team>> page(@RequestBody TeamQuery teamQuery){
        //判断参数是否正常
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        //将TeamQuery中的参数放到Team中，list方法的返回值才是Team集合
        try {
            BeanUtils.copyProperties(team,teamQuery);
        } catch (Exception e) {
            log.info(e.toString());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //构建查询条件qw
        QueryWrapper<Team> queryWrapper= new QueryWrapper<>(team);
        Page<Team> teamPage = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        List<Team> list = teamService.page(teamPage,queryWrapper).getRecords();
        //返回内容
        if(CollectionUtils.isEmpty(list) ){
            throw new BusinessException(ErrorCode.NULL_ERROR,"查询数据为空");
        }
        return ResultUtils.success(list);
    }
}
