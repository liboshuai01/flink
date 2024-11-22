package cn.liboshuai.flink.common.service;


import cn.liboshuai.flink.common.pojo.Order;

public interface OrderService {

    Order findOrderByNo(String no);
}
