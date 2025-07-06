package org.max.cms.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.max.cms.user.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}