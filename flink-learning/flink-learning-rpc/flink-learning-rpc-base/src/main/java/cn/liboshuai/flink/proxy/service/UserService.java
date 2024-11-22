package cn.liboshuai.flink.proxy.service;


import cn.liboshuai.flink.proxy.pojo.User;

public interface UserService {

    User findUserByNameAndAge(String name, int age);
}
