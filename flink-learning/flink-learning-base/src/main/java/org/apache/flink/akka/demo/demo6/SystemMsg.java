package org.apache.flink.akka.demo.demo6;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMsg implements Serializable {
    private String msg;
}
