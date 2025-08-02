package org.apache.flink.streaming.examples.lbs.ext;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartitionedEvent implements LbsEvent {

    private int partitionId;
    private LbsEvent event;
}
