package org.apache.flink.streaming.examples.lbs.ext;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

import org.apache.flink.runtime.operators.coordination.CoordinationRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressionChangeRequest implements CoordinationRequest {

    private String expression;
}
