package com.imooc.mall.service.impl;

import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.UserMapper;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import com.imooc.mall.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

/**
 * UserService实现类
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }

    @Override
    public void register(String userName, String password) throws ImoocMallException {
        // 查询用户名是否存在，不允许重名
        User result = userMapper.selectByName(userName);
        if(result != null) {
            // 每一层都有分工，此Service层不应该直接触碰最终返回的内容（Controller层）；采用抛出异常的方法,编写统一异常
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }

        // 写到数据库
        User user = new User();
        user.setUsername(userName);
        // 数据库存储的密码需要加密
        try {
            user.setPassword(MD5Utils.getMD5Str(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // user.setPassword(password);
        // insertSelective会对不为空的方法进行处理; count返回值为修改成功的数值条数
        int count =userMapper.insertSelective(user);
        // 处理插入失败异常
        if (count==0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String userName, String password) throws ImoocMallException {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str(password);   //获取数据库中存储的md5password,返回的是String
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        User user = userMapper.selectLogin(userName, md5Password);  //Mapper中新增查找密码的方法
        if (user == null) {  // user对象为空，说明用户名活密码错误查询不到
            throw  new ImoocMallException(ImoocMallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    @Override
    public void updateInformation(User user) throws ImoocMallException {
        // 更新个性签名
        int updateCount =  userMapper.updateByPrimaryKeySelective(user);
        if (updateCount>1) {  //只更新一条数据，不可大于1
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkAdminRole(User user) {
        // 1是普通用户，2是管理员
        return user.getRole().equals(2);
    }
}
