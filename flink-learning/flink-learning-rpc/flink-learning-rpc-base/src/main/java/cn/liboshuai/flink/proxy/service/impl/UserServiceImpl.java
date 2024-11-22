package cn.liboshuai.flink.proxy.service.impl;

import cn.liboshuai.flink.base.proxy.service.UserService;
import cn.liboshuai.flink.base.proxy.pojo.User;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class UserServiceImpl implements UserService {

    List<User> users = new ArrayList<User>();

    public UserServiceImpl() {
        users.add(User.builder().name("lbs1").age(21).address("北京").build());
        users.add(User.builder().name("lbs2").age(22).address("上海").build());
        users.add(User.builder().name("lbs3").age(23).address("广州").build());
        users.add(User.builder().name("lbs4").age(24).address("深圳").build());
        users.add(User.builder().name("lbs5").age(25).address("杭州").build());
    }

    /**
     * 根据姓名和年龄查询用户信息
     */
    public User findUserByNameAndAge(String name, int age) {
        return users
                .stream()
                .filter(user -> Objects.equals(name, user.getName()) && Objects.equals(
                        age,
                        user.getAge()))
                .collect(
                        Collectors.toList()).get(0);
    }
}
