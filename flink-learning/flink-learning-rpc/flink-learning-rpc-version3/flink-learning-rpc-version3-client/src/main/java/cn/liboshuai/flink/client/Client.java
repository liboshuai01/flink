package cn.liboshuai.flink.client;

import cn.liboshuai.flink.client.handler.RpcHandler;
import cn.liboshuai.flink.common.pojo.Order;
import cn.liboshuai.flink.common.pojo.User;
import cn.liboshuai.flink.common.service.OrderService;
import cn.liboshuai.flink.common.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {
    public static void main(String[] args) {
        UserService userService = new RpcHandler().getProxy(UserService.class);
        User user = userService.findUserByNameAndAge("lbs3", 23);
        log.info("user:{}", user);

        OrderService orderService = new RpcHandler().getProxy(OrderService.class);
        Order order = orderService.findOrderByNo("D000001");
        log.info("order:{}", order);
    }
}
