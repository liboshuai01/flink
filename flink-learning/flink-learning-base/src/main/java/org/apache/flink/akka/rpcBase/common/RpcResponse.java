package org.apache.flink.akka.rpcBase.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {

    private Objects result;
    private Throwable ex;
    private int status;

}
