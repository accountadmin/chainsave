package com.example.chainsave.fabric.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author WaleGarrett
 * @Date 2022/2/24 20:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMetadata {
    int fileMetadataId;
    String fileHash;
    String fileName;
    String fileDescription;
    int partitionNum;
    int duplicateNum;
    int[][] allPartitionIds;
    int fileStatus;
    int userId;
    String generateTime;
    String changeTime;
    String fileMd5;
}
