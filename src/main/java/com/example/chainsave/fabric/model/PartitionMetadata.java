package com.example.chainsave.fabric.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author WaleGarrett
 * @Date 2022/2/24 20:29
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartitionMetadata {
    int partitionMetadataId;
    String partitionHash;
    String partitionAddress;
    int partitionType;
    int partitionStatus;
    String generateTime;
    String changeTime;
}
