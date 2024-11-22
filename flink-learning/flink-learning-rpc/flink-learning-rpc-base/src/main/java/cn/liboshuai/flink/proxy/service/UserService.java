package cn.liboshuai.flink.proxy.service;


import cn.liboshuai.flink.base.proxy.pojo.User;

public interface UserService {

    User findUserByNameAndAge(String name, int age);
}
