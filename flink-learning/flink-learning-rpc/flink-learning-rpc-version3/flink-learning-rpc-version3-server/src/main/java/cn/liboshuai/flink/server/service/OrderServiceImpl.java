package cn.liboshuai.flink.server.service;

import cn.liboshuai.flink.common.pojo.Order;
import cn.liboshuai.flink.common.service.OrderService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class OrderServiceImpl implements OrderService {
    List<Order> orders = new ArrayList<>();

    public OrderServiceImpl() {
        orders.add(Order
                .builder().no("D000001").amount(new BigDecimal(100)).build());
        orders.add(Order
                .builder().no("D000002").amount(new BigDecimal(200)).build());
        orders.add(Order
                .builder().no("D000003").amount(new BigDecimal(300)).build());
        orders.add(Order
                .builder().no("D000004").amount(new BigDecimal(400)).build());
        orders.add(Order
                .builder().no("D000005").amount(new BigDecimal(500)).build());
    }

    /**
     * 根据姓名和年龄查询用户信息
     */
    public Order findOrderByNo(String no) {
        return orders
                .stream()
                .filter(order -> Objects.equals(no, order.getNo()))
                .collect(
                        Collectors.toList()).get(0);
    }
}
