package com.example.chainsave.fabric.service;

import com.alibaba.fastjson.JSON;
import com.example.chainsave.fabric.model.PartitionMetadata;
import org.hyperledger.fabric.gateway.*;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author WaleGarrett
 * @Date 2022/3/9 21:08
 */
@Service
public class FileMetadataService {
    //getting connected to the gateway
    public static Gateway connect() throws Exception{
        // Load a file system based wallet for managing identities.
        Path walletPath = Paths.get("src","main","resources","wallet");
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);
        // load a CCP
        Path networkConfigPath = Paths.get("src","main","resources","connection.json");

        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(false);
        return builder.connect();
    }

    /**
     * 文件元数据上链
     * @return
     * @throws Exception
     */
    public static String createFileMetadata() throws Exception {

        PartitionMetadata partitionMetadata1 = PartitionMetadata.builder()
                .partitionHash("0x1f3f3f")
                .partitionAddress("127.9.90.9")
                .partitionType(0)
                .build();
        PartitionMetadata partitionMetadata2 = PartitionMetadata.builder()
                .partitionHash("0x2f4f4f")
                .partitionAddress("128.9.90.0")
                .partitionType(0)
                .build();
        PartitionMetadata[][] partitionMetadatas = {{partitionMetadata1},{partitionMetadata2}};

        // 分片IDs
        int[][] allPartitionIds = new int[2][1];
        int partitionNum = 2;
        int duplicateNum = 1;
        for(int i=0; i<partitionNum; i++){
            for(int j=0; j<duplicateNum; j++){
                // 创建该文件所属的所有分片元数据
                PartitionMetadata partitionMetadata = PartitionMetadataService.createPartitionMetadata(
                        partitionMetadatas[i][j].getPartitionHash(), partitionMetadatas[i][j].getPartitionAddress(),
                        partitionMetadatas[i][j].getPartitionType());
                allPartitionIds[i][j] = partitionMetadata.getPartitionMetadataId();
            }
        }

        // 上传文件的用户，根据实际进行设置
        int userId = 2;
        String fileHash = "0x445552222";
        String fileMd5 = "xxxxxxxxx";
        String fileName = "测试文件";
        String fileDescription = "描述。。。";


        // connect to the network and invoke the smart contract
        Gateway gateway = connect();
        // get the network and contract
        Network network = gateway.getNetwork("mychannel");
        Contract contract = network.getContract("testStore");

        byte[] result;
        // 查询使用evaluateTransaction，修改和添加使用submitTransaction
        result = contract.submitTransaction("CreateFileMetadata", String.valueOf(userId), fileHash, fileMd5, fileName,
                fileDescription, String.valueOf(partitionNum), String.valueOf(duplicateNum),
                JSON.toJSONString(allPartitionIds));
        return new String(result);
    }

    /**
     * 根据文件元数据Id查询文件元数据
     * @param fileMetadataId
     * @return
     * @throws Exception
     */
    public static String queryFileMetadataById(int fileMetadataId) throws Exception {
        // connect to the network and invoke the smart contract
        Gateway gateway = connect();
        // get the network and contract
        Network network = gateway.getNetwork("mychannel");
        Contract contract = network.getContract("testStore");

        byte[] result;
        // 查询使用evaluateTransaction，修改和添加使用submitTransaction
        result = contract.evaluateTransaction("QueryFileMetadataByFileMetaId", String.valueOf(fileMetadataId));
        return new String(result);
    }
}
