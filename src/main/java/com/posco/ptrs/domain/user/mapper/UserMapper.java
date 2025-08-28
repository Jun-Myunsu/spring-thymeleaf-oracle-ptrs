package com.posco.ptrs.domain.user.mapper;

import com.posco.ptrs.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> findAll();
    User findById(@Param("id") Long id);
    User findByEmail(@Param("email") String email);
    int insert(User user);
    int update(User user);
    int deleteById(@Param("id") Long id);
    int count();
}