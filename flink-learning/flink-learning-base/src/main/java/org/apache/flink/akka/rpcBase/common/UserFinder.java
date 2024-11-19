package org.apache.flink.akka.rpcBase.common;

public interface UserFinder {

    String getUserNameById(int id);

    int getAgeById(int id);
}
