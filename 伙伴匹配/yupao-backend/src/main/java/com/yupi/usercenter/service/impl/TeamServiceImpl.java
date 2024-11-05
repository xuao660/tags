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
import com.yupi.usercenter.model.domain.request.TeamUpdateRequest;
import com.yupi.usercenter.model.domain.vo.TeamUserVO;
import com.yupi.usercenter.model.domain.vo.UserVO;
import com.yupi.usercenter.service.TeamService;
import com.yupi.usercenter.service.UserService;
import com.yupi.usercenter.service.UserteamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
//    @Resource
//    TeamServiceImpl teamServiceImpl;
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
        userteam.setJoinTime(LocalDateTime.now());
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
        if(list == null || list.size() == 0){
            return teams;
        }
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

    @Override
    public boolean updateTeamById(TeamUpdateRequest teamRequest, HttpServletRequest request) {
        //改进点 ，封装一个专门用来修改的实体TeamUpdateRequest

        //先判断id是否存在
        Long id = teamRequest.getId();
        if(id == null || id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        1、首先判断队伍是否存在
        Team oldTeam = this.getById(id);
        if(oldTeam == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
//        2、当前用户是否有修改队伍的权限，只有管理员和队长能修改队伍
        boolean isAdmin = userService.isAdmin(request);
        User loginUser = userService.getLoginUser(request);
        boolean isCaptain = oldTeam.getCaptainId().equals(loginUser.getId());
        //既不是管理员，也不是队长
        if(!isAdmin && !isCaptain){
            throw new BusinessException(ErrorCode.NO_AUTH,"无权限");
        }
//        3、超过一定人数（3人）之后，不可修改
        //获取要修改的队伍人数
        QueryWrapper<Userteam> qw = new QueryWrapper<>();
        qw.eq("teamId",oldTeam.getId());
        long count = userteamService.count(qw);
        if(count > 3){
            throw new BusinessException(ErrorCode.NO_AUTH,"队伍人数已超过三人");
        }
//        4、哪些队伍信息可以修改？ 队伍标题、队伍描述、最大人数、过期时间、状态、队伍密码
        Team newTeam = new Team();
        try {
            BeanUtils.copyProperties(newTeam,teamRequest);
        } catch (Exception e) {
            log.info("修改队伍，拷贝队伍属性出现异常！");
            e.printStackTrace();
        }
        Integer status = newTeam.getStatus();
        String password = newTeam.getPassword();
        TeamStatusEnum enumBystatus = TeamStatusEnum.getEnumBystatus(status);
        if(enumBystatus == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        if(TeamStatusEnum.ENCRYPT.equals(enumBystatus) && StringUtils.isBlank(password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(TeamStatusEnum.PUBLIC.equals(enumBystatus) || TeamStatusEnum.PRIVATE.equals(enumBystatus) ){
            newTeam.setPassword("");
        }

        boolean b = this.updateById(newTeam);
        return b;
    }

    @Override
    public boolean joinTeam(Long teamId, String password,HttpServletRequest request) {
//        3、根据队伍ID获取信息
        Team team = this.getById(teamId);
//        4、校验队伍是否过期、是否私密、
        TeamStatusEnum enumBystatus = TeamStatusEnum.getEnumBystatus(team.getStatus());
        if(enumBystatus != null && enumBystatus.equals(TeamStatusEnum.PRIVATE)){
            throw new BusinessException(ErrorCode.NO_AUTH,"加入队伍失败");
        }
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.NO_AUTH,"加入队伍失败");

        }
//        6、是否加密，校验密码
        if(enumBystatus.equals(TeamStatusEnum.ENCRYPT)){
            if(StringUtils.isBlank(password)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加入队伍失败");
            }
            String teamPasword = team.getPassword();
            if(!teamPasword.equals(password)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加入队伍失败");

            }
        }
//        5、是否超过了最大人数、
        Integer maxNum = team.getMaxNum();
        QueryWrapper<Userteam> queryWrapper = new QueryWrapper<Userteam>().eq("teamId", teamId);
        long count = userteamService.count(queryWrapper);
        if(maxNum<=count){
            throw new BusinessException(ErrorCode.NO_AUTH,"加入队伍失败");

        }

//        7、当前用户是否已加入过该队伍
        User loginUser = userService.getLoginUser(request);

        QueryWrapper<Userteam> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("teamId",teamId).eq("userId",loginUser.getId());
        long count1 = userteamService.count(queryWrapper1);
        if(count1 == 1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"已加入此队伍");

        }
//        补充点：用户最多加入十个队伍
        QueryWrapper<Userteam> queryWrapper2 = new QueryWrapper<>();
        queryWrapper1.eq("userId",loginUser.getId());
        long count2 = userteamService.count(queryWrapper1);
        if(count2 >= 10){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"加入队伍失败");
        }
//        补充点：新增用户-队伍关联信息
        Userteam userteam = new Userteam();
        userteam.setTeamId(teamId);
        userteam.setUserId(loginUser.getId());
        userteam.setJoinTime(LocalDateTime.now());
        boolean save = userteamService.save(userteam);
        return save;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean exitTeam(Long teamId, User loginUser) {
//        3、获取队伍，判断队伍是否存在、队伍是否过期
        Team team = this.getById(teamId);
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未获取到队伍信息");
        }
        Date expireTime = team.getExpireTime();
        if(expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍已过期");
        }
//        4、查询用户是否在队伍中，isDelete = 0
        QueryWrapper<Userteam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId).eq("userId",loginUser.getId());
        Userteam userteam = userteamService.getOne(queryWrapper);
        if(userteam == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"退出队伍失败");
        }
//        5、修改teamuser，退出队伍
        boolean b = userteamService.removeById(userteam);
//        6、是不是队长，不是队长结束
        if(!loginUser.getId().equals(team.getCaptainId())){
            return b;
        }
//        7、是队长退出， 队长退出方法（队伍id）
        boolean c = captainExit(team);
        return c;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void testA(String param)  {
        Team team = new Team();
        team.setTeamName("A事务小队");
        team.setPassword("123");
        team.setDescription("");

        save(team);
        System.out.println(this);
        this.testB(param);

        if("A".equals(param)){
            throw new RuntimeException("手动抛出异常A");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void testB(String param)  {
        Team team = new Team();
        team.setTeamName("B事务小队");
        team.setPassword("123");
        team.setDescription("");
        save(team);
        //com.yupi.usercenter.service.impl.TeamServiceImpl@46d69ca4
        System.out.println(this);
        if("B".equals(param)){
                throw new RuntimeException("手动抛出异常B");

        }

    }

    @Override
    public List<Team> listJoinTeam(Long id) {
        List<Team> list = teamMapper.listJoinTeam(id);
        return list;
    }

    @Override
    public List<Team> listCreateTeam(Long id) {
        List<Team> list = teamMapper.listCreateTeam(id);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(Long teamId,HttpServletRequest request) {
        if(teamId == null || teamId < 1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        2、获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        //是否为管理员
        boolean admin = userService.isAdmin(request);
//        3、获取当前队伍信息信息，是否为空
        Team team = this.getById(teamId);
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        //判断是不是队长
        boolean isCaptain = loginUser.getId().equals(team.getCaptainId()) ? true : false;
//        4、如果不是管理员或者当前队伍队长，删除失败
        if(!admin && !isCaptain){
            throw new BusinessException(ErrorCode.NO_AUTH,"无权限");
        }
//        5、移除当前队伍所有的队伍用户信息
        QueryWrapper<Userteam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        boolean remove = userteamService.remove(queryWrapper);

//        6、删除队伍
        boolean b = this.removeById(teamId);

        return b;
    }

    private boolean captainExit(Team team) {

        if(team == null ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未获取到队伍信息");
        }
        Long teamId = team.getId();
        if(teamId == null || teamId < 1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未获取到队伍信息");
        }

//        7、查询本队伍中加入最早的人
        QueryWrapper<Userteam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId).eq("isDelete",0).orderByAsc("joinTime");
        Userteam userteam = userteamService.getOne(queryWrapper);

//        8、存在，修改队伍表，队长id字段
        if(userteam != null && userteam.getUserId() != null){
            team.setCaptainId(userteam.getUserId());
            return this.updateById(team);
        }
//        9、不存在，解散队伍

        return this.removeById(team);
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




