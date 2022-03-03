package com.example.chainsave;

import com.example.chainsave.upload.dao.LoginTicketMapper;
import com.example.chainsave.upload.dao.UserFileMapper;
import com.example.chainsave.upload.dao.UserMapper;
import com.example.chainsave.upload.entity.LoginTicket;
import com.example.chainsave.upload.entity.User;
import com.example.chainsave.upload.entity.UserFile;
import com.example.chainsave.common.util.ChainSaveUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes=ChainsaveApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private UserFileMapper userFileMapper;
    @Test
    public void testSelectUser(){
        User user=userMapper.selectById(101);
        System.out.println(user);
        user=userMapper.selectByName("liubei");
        System.out.println(user);
        user=userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user= new User();
        user.setUsername("test2");
        user.setSalt("abc");
        user.setPassword(ChainSaveUtil.md5("test2"+user.getSalt()));
        user.setEmail("test2@zju.edu.cn");
        user.setCreateTime(new Date());
        int rows=userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdate(){
        int rows=userMapper.updateStatus(1,1);
        System.out.println(rows);
        rows=userMapper.updatePassword(1,"hello");
        System.out.println(rows);

    }

//    @Test
//    public void testSelectPosts(){
//        List<DiscussPost> list=discussPostMapper.selectDiscussPosts(149,0,10);
//        for(DiscussPost post : list){
//            System.out.println(post);
//        }
//        int rows=discussPostMapper.selectDiscussPostRows(149);
//        System.out.println(rows);
//    }
    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(1);
        loginTicket.setTicket("ddd");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }
    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket=loginTicketMapper.selectByTicket("ddd");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("ddd",1);
        loginTicket=loginTicketMapper.selectByTicket("ddd");
        System.out.println(loginTicket);
    }
    @Test
    public void testInsertUserFile(){
        UserFile userFile=new UserFile();
        userFile.setUserId(1);
        userFile.setFileName("bbb");
        userFile.setFileHash("bbb");
        userFile.setFileDetail("test");
        userFile.setStatus(0);
        userFile.setCreateTime(new Date());
        userFileMapper.insertUserFile(userFile);
    }
    @Test
    public void testSelectUserFiles(){
        List<UserFile> list=userFileMapper.selectUserFiles(1,0,5);
        for(UserFile file:list){
            System.out.println(file.toString());
        }
    }
}
