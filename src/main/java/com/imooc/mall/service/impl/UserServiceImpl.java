package com.imooc.mall.service.impl;

import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.UserMapper;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        user.setPassword(password);
        // insertSelective会对不为空的方法进行处理; count返回值为修改成功的数值条数
        int count =userMapper.insertSelective(user);
        // 处理插入失败异常
        if (count==0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }
}
