package com.example.chainsave.upload.dao;

import com.example.chainsave.upload.entity.UserFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFileMapper {
    List<UserFile> selectUserFiles(int userId,int offset,int limit);

    //@Param注解用于给参数取别名，
    //如果只有一个参数，并且在<if>里使用，则必须加别名。
    int selectUserFileRows(@Param("userId") int userId);
    int insertUserFile(UserFile userFile);
    UserFile selectUserFileById(int id);
}

