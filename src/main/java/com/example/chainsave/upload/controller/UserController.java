package com.example.chainsave.upload.controller;

import com.example.chainsave.common.annotation.LoginRequired;
import com.example.chainsave.common.model.Result;
import com.example.chainsave.upload.entity.User;
import com.example.chainsave.upload.service.UserService;
import com.example.chainsave.common.util.ChainSaveConstant;
import com.example.chainsave.common.util.ChainSaveUtil;
import com.example.chainsave.common.util.HostHolder;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@CrossOrigin
public class UserController implements ChainSaveConstant {
    private static final Logger logger= LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private HostHolder hostHolder;
    @RequestMapping(path = "/register",method = RequestMethod.POST)
    @ResponseBody
    public Result<String> register(@RequestBody User user){
        Map<String,Object> map=userService.register(user);
        if(map==null||map.isEmpty()){
            return new Result<String>().setMessage("添加用户成功");
        }else{
            return new Result<String>().setParameterErrorMsgInfo(map.get("errorMsg").toString());
        }
    }
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text=kaptchaProducer.createText();
        BufferedImage image=kaptchaProducer.createImage(text);
        //将验证码存入session
        session.setAttribute("kaptcha",text);
        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os=response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败："+e.getMessage());
        }
    }
    @RequestMapping(path = "/login",method = RequestMethod.POST)
    @ResponseBody
    public Result<String> login(String username, String password, boolean rememberMe,
                        HttpServletResponse response){
        //检查验证码
//        String kaptcha= (String) session.getAttribute("kaptcha");
//        if(StringUtils.isBlank(code)||StringUtils.isBlank(kaptcha)||!kaptcha.equalsIgnoreCase(code)){
//            return new Result<String>().setParameterErrorMsgInfo("验证码不正确");
//        }
        //检查账号,密码
        int expiredSeconds=rememberMe?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map=userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return new Result<String>().setMessage("登陆成功");
        }else{
            return new Result<String>().setParameterErrorMsgInfo(map.get("errorMsg").toString());
        }
    }
    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        return new Result<String>().setMessage("登出成功");
    }
    @RequestMapping(path = "/forget/code",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> sendVerifyCode(String email,HttpSession session){
        Map<String,Object> map=userService.sendVerifyCode(email);
        if(map.containsKey("verifyCode")){
            session.setAttribute("verifyCode",map.get("verifyCode"));
            return new Result<String>().setMessage("验证码发送成功");
        }else{
            return new Result<String>().setParameterErrorMsgInfo(map.get("errorMsg").toString());
        }
    }
    @RequestMapping(path = "/forget",method = RequestMethod.POST)
    @ResponseBody
    public Result<String>  forgetPassword(String email,String verifyCode,String password,HttpSession session){
        //检查验证码
        String code= (String) session.getAttribute("verifyCode");
        if(StringUtils.isBlank(verifyCode)||StringUtils.isBlank(code)||!code.equals(verifyCode)){
            return new Result<String>().setParameterErrorMsgInfo("验证码不正确");
        }
        Map<String,Object> map=userService.resetPassword(email,password);
        if(map==null||map.isEmpty()){
            return new Result<String>().setMessage("密码重置成功");
        }else{
            return new Result<String>().setParameterErrorMsgInfo(map.get("errorMsg").toString());
        }
    }
    // 修改密码
    @LoginRequired
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> updatePassword(String oldPassword, String newPassword) {
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword);
        if (map == null || map.isEmpty()) {
            return new Result<String>().setMessage("密码更新成功");
        } else {
            return new Result<String>().setParameterErrorMsgInfo(map.get("errorMsg").toString());
        }
    }

    //列出所有用户

    //删除用户

    //更改用户权限

    //用户更改自己的邮箱
    
}
