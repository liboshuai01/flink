package org.apache.flink.streaming.examples.lbs.ext;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressionEvent implements LbsEvent{

    // f1>f2?f1:f3
    // f1+f2-f3
    private String expression;
}
