package org.apache.flink.streaming.examples.lbs;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.flink.util.StringUtils;

public class WordCount {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.socketTextStream("127.0.0.1", 8888)
                .filter(s -> !StringUtils.isNullOrWhitespaceOnly(s))
                .flatMap((String s, Collector<Tuple2<String, Integer>> out) -> {
                    for (String string : s.split(" ")) {
                        out.collect(Tuple2.of(string, 1));
                    }
                }).returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(s -> s.f0)
                .sum(1)
                .print();
        env.execute();
    }
}
