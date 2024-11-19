package cn.liboshuai.flink.service;

import cn.liboshuai.flink.pojo.User;

// 定义用户服务接口
public interface UserService {
    // 根据姓名和年龄查找用户
    User findUserByNameAndAge(String name, int age);
}
