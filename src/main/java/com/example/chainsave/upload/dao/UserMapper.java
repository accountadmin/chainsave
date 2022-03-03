package com.example.chainsave.upload.dao;

import com.example.chainsave.upload.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(int id);
    // username?
    User selectByName(String name);
    User selectByEmail(String email);
    int insertUser(User user);
    int updateStatus(int id,int status);
    int updatePassword(int id,String password);
}
