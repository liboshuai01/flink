package cn.liboshuai.flink.common.service;


import cn.liboshuai.flink.common.pojo.User;

public interface UserService {

    User findUserByNameAndAge(String name, int age);
}
