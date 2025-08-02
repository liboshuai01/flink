package org.apache.flink.streaming.examples.lbs.ext;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataEvent implements LbsEvent{

    private int f1;
    private int f2;
    private int f3;
}
