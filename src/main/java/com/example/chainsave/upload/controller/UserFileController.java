package com.example.chainsave.upload.controller;

import com.example.chainsave.upload.entity.UserFile;
import com.example.chainsave.upload.entity.Page;
import com.example.chainsave.upload.entity.User;
import com.example.chainsave.upload.service.UserFileService;
import com.example.chainsave.upload.service.UserService;
import com.example.chainsave.common.util.ChainSaveUtil;
import com.example.chainsave.common.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class UserFileController {
    @Autowired
    private UserFileService userFileService;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model。
        //所以，在thymeleaf中可以直接访问Page对象中的数据。
        page.setRows(userFileService.findUserFileRows(0));
        page.setPath("/index");
        List<UserFile> list=userFileService.findUserFiles(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> userFiles=new ArrayList<>();
        if(list!=null){
            for(UserFile file:list){
                Map<String,Object> map=new HashMap<>();
                map.put("file",file);
                User user=userService.findUserById(file.getUserId());
                map.put("user",user);
                userFiles.add(map);
            }
        }
        model.addAttribute("userFiles",userFiles);
        return "/index";
    }
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addUserFile(String fileName,String fileDetail){
        User user=hostHolder.getUser();
        if(user==null){
            return ChainSaveUtil.getJSONString(403,"您未登录！");
        }
        UserFile userFile=new UserFile();
        userFile.setFileName(fileName);
        userFile.setFileDetail(fileDetail);
        userFile.setCreateTime(new Date());
        userFile.setUserId(user.getId());
        userFileService.addUserFile(userFile);

        //报错的情况，将来统一处理
        return ChainSaveUtil.getJSONString(0,"发布成功！");
    }
    @RequestMapping(path = "/detail/{userFileId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("userFileId") int userFileId, Model model){
        //文件
        UserFile file=userFileService.findUserFileById(userFileId);
        model.addAttribute("file",file);
        //所有者
        User user=userService.findUserById(file.getUserId());
        model.addAttribute("user",user);

        return "site/discuss-detail";
    }
}
