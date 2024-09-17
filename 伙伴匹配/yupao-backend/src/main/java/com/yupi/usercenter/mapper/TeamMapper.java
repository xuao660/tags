package com.yupi.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.domain.vo.TeamUserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author xuao
* @description 针对表【team】的数据库操作Mapper
* @createDate 2024-09-08 16:07:15
* @Entity generator.domain.Team
*/
public interface TeamMapper extends BaseMapper<Team> {

    IPage<TeamUserVO> selectListByTeams(@Param("teams") List<Team> list, Page<TeamUserVO> teamUserPage);
}




