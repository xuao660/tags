package com.yupi.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.gson.Gson;
import com.yupi.usercenter.common.BaseResponse;
import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.common.ResultUtils;
import com.yupi.usercenter.contant.UserConstant;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.request.UserLoginRequest;
import com.yupi.usercenter.model.domain.request.UserRegisterRequest;
import com.yupi.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yupi.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.yupi.usercenter.contant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final Gson gson = new Gson();
    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        //xuao123456
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }


    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }


    @GetMapping("/recommand")
    public BaseResponse<List<User>> recommand(HttpServletRequest request,long pageNum , long pageSize) {
        String userId = userService.getLoginUser(request).getId().toString();
        String key = "yupao:user:recommend:"+userId;
        String recommends = redisTemplate.opsForValue().get(key);
        if(StringUtils.isNotBlank(recommends)){
            List<User> list = gson.fromJson(recommends, List.class);
            return ResultUtils.success(list);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        List<User> userList = userService.list(queryWrapper);
        //后端分页查询 pageNum --页码  pageSize --页数据量
        IPage<User> page = new Page<>(pageNum, pageSize);
        IPage<User> userList = userService.page(page, queryWrapper);
        //关键信息脱敏
        List<User> list = userList.getRecords().stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        String json = gson.toJson(list);
        try {
            redisTemplate.opsForValue().set(key,json,30l, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.info("redis set error",e.getMessage());
        }
        return ResultUtils.success(list);
    }
    @GetMapping("searchUserByTags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList) {
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUserByTags(tagNameList);

        return ResultUtils.success(userList);
    }
    /**
     *  管理员和普通用户修改用户信息的接口是一样的
     */
    @PostMapping("update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request){

        //1、校验参数是否为空
        if(user == null || user.getId() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2、校验权限，获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        //2.1 管理员可以修改任意用户信息
        //2.2 普通用户只能修改当前用户信息
        if(userService.isAdmin(request) || loginUser.getId().equals(user.getId())){
            //3、修改用户信息
            int i = userService.updateUser(user);
            return ResultUtils.success(i);

        }
        return ResultUtils.error(ErrorCode.NO_AUTH);
    }

    /**
     * 获取匹配用户
     * @param request
     * @return
     */
    @GetMapping("findUsers")
    public BaseResponse<List<User>> findUsers(@RequestParam int num , HttpServletRequest request){
        if(num<0 || num >20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"匹配用户数不能超过二十");
        }
        User loginUser = userService.getLoginUser(request);
        List<User> users = userService.findUsers(loginUser,num);
        return ResultUtils.success(users);
    }

}
