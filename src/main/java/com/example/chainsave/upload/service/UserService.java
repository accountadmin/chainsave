package com.example.chainsave.upload.service;

import com.example.chainsave.upload.dao.LoginTicketMapper;
import com.example.chainsave.upload.dao.UserMapper;
import com.example.chainsave.upload.entity.LoginTicket;
import com.example.chainsave.upload.entity.User;
import com.example.chainsave.common.util.ChainSaveConstant;
import com.example.chainsave.common.util.ChainSaveUtil;
import com.example.chainsave.common.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements ChainSaveConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    public User findUserById(int id){
        return  userMapper.selectById(id);
    }
    public Map<String,Object> register(User user){
        Map<String,Object> map=new HashMap<>();
        //空值处理
        if(user==null){
            throw new IllegalArgumentException("参数不为空！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("errorMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("errorMsg","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("errorMsg","邮箱不能为空！");
            return map;
        }
        //验证账号
        User u=userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("errorMsg","该账号已存在！");
            return  map;
        }
        u=userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("errorMsg","该邮箱已被注册！");
            return  map;
        }
        //注册用户
        user.setSalt(ChainSaveUtil.generateUUID().substring(0,5));
        user.setPassword(ChainSaveUtil.md5(user.getPassword()+user.getSalt()));
//        user.setType(0);
        user.setStatus(0);
        user.setCreateTime(new Date());
        //注册时传入的user没有userId,调用了insert之后有了userId
        //mybatis自动获取生成的id并进行回填
        //在配置文件mybatis.configuration.useGeneratedKeys=true处进行了配置
        //在相应sql语句中设置keyProperty="id"设置类的增长主键
        userMapper.insertUser(user);

        return map;
    }

    public Map<String,Object> login(String username,String password,long expiredSeconds){
        Map<String,Object> map=new HashMap<>();
        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("errorMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("errorMsg","密码不能为空！");
            return map;
        }
        //验证账号
        User user=userMapper.selectByName(username);
        if(user==null){
            map.put("errorMsg","该帐号不存在！");
            return map;
        }
        //验证状态
//        if(user.getStatus()==0){
//            map.put("usernameMsg","该帐号未激活！");
//            return map;
//        }
        //验证密码
        password=ChainSaveUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("errorMsg","密码不正确！");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(ChainSaveUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }
    public Map<String,Object> sendVerifyCode(String email){
        Map<String,Object> map=new HashMap<>();
        //空值处理
        if(StringUtils.isBlank(email)){
            map.put("errorMsg","邮箱不能为空！");
            return map;
        }
        //验证邮箱
        User user=userMapper.selectByEmail(email);
        if(user==null){
            map.put("errorMsg","邮箱不存在！");
            return map;
        }
        //发送验证码
        String verifyCode=ChainSaveUtil.generateUUID().substring(0,6);
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        context.setVariable("verifyCode",verifyCode);
        String content=templateEngine.process("/mail/forget",context);
        mailClient.sendMail(user.getEmail(),"找回密码",content);
        map.put("verifyCode",verifyCode);
        return map;
    }
    //重置密码
    public Map<String,Object> resetPassword(String email,String password){
        Map<String,Object> map=new HashMap<>();
        //空值处理
        if(StringUtils.isBlank(email)){
            map.put("errorMsg","邮箱不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("errorMsg","密码不能为空！");
            return map;
        }
        //验证邮箱
        User user=userMapper.selectByEmail(email);
        if(user==null){
            map.put("errorMsg","邮箱不存在！");
            return map;
        }
        //重置密码
        password=ChainSaveUtil.md5(password+user.getSalt());
        userMapper.updatePassword(user.getId(),password);
        return map;
    }
    //修改密码
    public Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(oldPassword)) {
            map.put("errorMsg", "原密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("errorMsg", "新密码不能为空!");
            return map;
        }

        // 验证原始密码
        User user = userMapper.selectById(userId);
        oldPassword = ChainSaveUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("errorMsg", "原密码输入有误!");
            return map;
        }

        // 更新密码
        newPassword = ChainSaveUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(userId, newPassword);

        return map;
    }
}
