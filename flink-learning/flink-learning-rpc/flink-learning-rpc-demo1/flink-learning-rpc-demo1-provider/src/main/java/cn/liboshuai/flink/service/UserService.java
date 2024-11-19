package cn.liboshuai.flink.service;

import cn.liboshuai.flink.pojo.User;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class UserService {

    List<User> userList = new ArrayList<>();

    /**
     * 模拟数据库，进行数据加载
     */
    public UserService() {
        userList.add(
                User.builder().name("lbs02").age(22).address("北京").phone("100100100102").build()
        );
        userList.add(
                User.builder().name("lbs01").age(21).address("上海").phone("100100100101").build()
        );
        userList.add(
                User.builder().name("lbs03").age(23).address("广州").phone("100100100103").build()
        );
        userList.add(
                User.builder().name("lbs04").age(24).address("深圳").phone("100100100104").build()
        );
    }

    /**
     * 根据姓名和年龄查询对应用户信息（模拟查询数据库的操作）
     */
    public User findUserByNameAndAge(String name, int age) {
        List<User> collect = userList.stream()
                .filter(user -> Objects.equals(user.getName(), name)
                        && Objects.equals(user.getAge(), age))
                .collect(Collectors.toList());
        return collect.get(0);
    }
}
