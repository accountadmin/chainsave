package com.example.chainsave.upload.service;

import com.example.chainsave.upload.dao.UserFileMapper;
import com.example.chainsave.upload.entity.UserFile;
import com.example.chainsave.common.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class UserFileService {
    @Autowired
    private UserFileMapper userFileMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    public List<UserFile> findUserFiles(int userId,int offset,int limit){
        return userFileMapper.selectUserFiles(userId,offset,limit);
    }
    public int findUserFileRows(int userId){
        return userFileMapper.selectUserFileRows(userId);
    }
    public int addUserFile(UserFile userFile){
        if(userFile==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //转移HTML标记
        userFile.setFileName(HtmlUtils.htmlEscape(userFile.getFileName()));
        userFile.setFileDetail(HtmlUtils.htmlEscape(userFile.getFileDetail()));
        //过滤敏感词
        userFile.setFileName(sensitiveFilter.filter(userFile.getFileName()));
        userFile.setFileDetail(sensitiveFilter.filter(userFile.getFileDetail()));
        return userFileMapper.insertUserFile(userFile);
    }
    public UserFile findUserFileById(int id){
        return userFileMapper.selectUserFileById(id);
    }
}
