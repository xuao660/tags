package com.yupi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.contant.TeamStatusEnum;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.mapper.TeamMapper;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.Userteam;
import com.yupi.usercenter.model.domain.dto.TeamQuery;
import com.yupi.usercenter.model.domain.vo.TeamUserVO;
import com.yupi.usercenter.model.domain.vo.UserVO;
import com.yupi.usercenter.service.TeamService;
import com.yupi.usercenter.service.UserService;
import com.yupi.usercenter.service.UserteamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yupi.usercenter.contant.UserConstant.ADMIN_ROLE;

/**
* @author xuao
* @description 针对表【team】的数据库操作Service实现
* @createDate 2024-09-08 16:07:15
*/
@Slf4j
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @Resource
    TeamMapper teamMapper;
    @Resource
    UserteamService userteamService;
    @Resource
    UserService userService;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addTeam(Team team, User loginUser) {
        final Long  loginUserId = loginUser.getId();
        if(!loginUserId.equals(team.getUserID()) || !loginUserId.equals(team.getCaptainId())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
//        1、补充：用户最多创建五个队伍
        //todo 用户同时点击多下，可能创建多个队伍
        int count = getTeamCount(loginUser);
        if(count >= 5){
            throw new BusinessException(ErrorCode.NO_AUTH,"最多创建五个队伍");
        }
//        2、校验标题，标题不能是特殊字符，不能为空，最大字数不超过20
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(team.getTeamName());
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"标题不能是特殊字符");
        }
//        3、描述不能是特殊字符，不能为空，最大字数不能超过512
        Matcher matcherDescription = Pattern.compile(validPattern).matcher(team.getDescription());
        if (matcherDescription.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"描述不能是特殊字符");
        }
//        4、最大人数必须是整数，大于1，小于20
//        5、过期时间 大于当前时间
        Date expireTime = team.getExpireTime();
        LocalDateTime expireTimeLocalDateTime = expireTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime currentDate = LocalDateTime.now(); // 获取当前时间
        log.info("expireTime:",expireTime);
        log.info("currentDate:",currentDate);

        if(currentDate.isAfter(expireTimeLocalDateTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"过期时间需要大于当前时间");
        }

//        7、默认是创建人
//        8、状态：默认是公开
        TeamStatusEnum enumBystatus = TeamStatusEnum.getEnumBystatus(team.getStatus());
        if (enumBystatus == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
        //        6、如果是加密，必须创建密码六位密码 数字
        String teamPassword = team.getPassword();
        if(TeamStatusEnum.ENCRYPT.equals(enumBystatus)){
            if(StringUtils.isBlank(teamPassword) || teamPassword.length()!=6){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码为六位");
            }
        }
//        9、插入队伍信息到队伍表
        boolean save = save(team);
//        10、插入队伍-用户到队伍用户关系表
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"添加队伍失败");
        }
        Userteam userteam = new Userteam();
        userteam.setTeamId(team.getId());
        userteam.setUserId(team.getUserID());
        userteam.setJoinTime(new Date());
        userteamService.save(userteam);
        //创建队伍
        return save;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery,boolean isAdmin) {
//        Team team = new Team();

        //构建查询条件qw
        QueryWrapper<Team> queryWrapper= new QueryWrapper<>();

        Long id = teamQuery.getId();
        String stringText = teamQuery.getStringText();
        String teamName = teamQuery.getTeamName();
        String description = teamQuery.getDescription();
        Integer maxNum = teamQuery.getMaxNum();
        Integer status = teamQuery.getStatus();
        if(id != null && id>0){
            queryWrapper.eq("id",id);
        }
        if (StringUtils.isNotBlank(stringText)){
            queryWrapper.and(qw -> qw.like("teamName",stringText).or().like("description",stringText));
        }
        if (StringUtils.isNotBlank(teamName)) {
            queryWrapper.like("teamName",teamName);
        }
        if(StringUtils.isNotBlank(description)){
            queryWrapper.like("description",description);
        }
        if(maxNum != null && maxNum>1){
            queryWrapper.eq("maxNum",maxNum);
        }
        //todo 管理员才能查询到私密队伍
        TeamStatusEnum enumBystatus = TeamStatusEnum.getEnumBystatus(status);
        if(enumBystatus == null){
            enumBystatus = TeamStatusEnum.PUBLIC;
        }

        if(!isAdmin && enumBystatus.equals(TeamStatusEnum.PRIVATE)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        queryWrapper.eq("status",enumBystatus.getStatus());

        queryWrapper.and(qw->qw.gt("expireTime",new Date()).or().isNull("expireTime"));
        List<Team> list = this.list(queryWrapper);
        List<TeamUserVO> teams = new ArrayList<>();

        Page<TeamUserVO> teamUserPage = new Page<>(1,10);
        IPage<TeamUserVO> iPage= teamMapper.selectListByTeams(list,teamUserPage);
        teams = iPage.getRecords();
        //关联查询用户信息
//        for (Team team : list) {
//            List<UserVO> userVOList = new ArrayList<>();
//            TeamUserVO teamUserVO = new TeamUserVO();
//            //将队伍信息复制到teamUserVO
//            try {
//                BeanUtils.copyProperties(teamUserVO,team);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }d
//            //获取对应队伍的成员信息
//            userVOList = this.listUserVOByTeam(team.getId());
//            //将成员信息设置到teamUserVO
//            teamUserVO.setUsers(userVOList);
//            teams.add(teamUserVO);
//
//        }
        return teams;
    }

    /**
     * 根据队伍id查询已经脱敏的用户信息列表
     * @param id
     * @return
     */
    private List<UserVO> listUserVOByTeam(Long id) {
        QueryWrapper<Userteam> queryWrapper1= new QueryWrapper<>();
        queryWrapper1.eq("teamId",id);
        List<Userteam> userteams = userteamService.list(queryWrapper1);
        ArrayList<UserVO> userVOS = new ArrayList<>();
        for (Userteam userteam : userteams) {
            UserVO userVO = new UserVO();
            User user = userService.getById(userteam.getUserId());
            try {
                if(user != null){
                    BeanUtils.copyProperties(userVO,user);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            userVOS.add(userVO);
        }
        return userVOS;
    }

    /**
     * 获取用户创建的队伍个数
     * @param loginUser
     * @return
     */
    private int getTeamCount(User loginUser) {
        Long id = loginUser.getId();
        QueryWrapper<Team> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("captainId",id);
        queryWrapper.eq("isDelete",0);
        Integer count = Integer.parseInt(teamMapper.selectCount(queryWrapper).toString());
        return count;
    }
}




