package com.example.chainsave.fabric.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.chainsave.fabric.model.PartitionMetadata;
import org.hyperledger.fabric.gateway.*;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author WaleGarrett
 * @Date 2022/3/9 21:09
 */
@Service
public class PartitionMetadataService {
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
     * 分片元数据上链
     * @param partitionHash
     * @param partitionAddress
     * @param partitionType
     * @return
     * @throws Exception
     */
    public static PartitionMetadata createPartitionMetadata(String partitionHash, String partitionAddress,
                                                            int partitionType) throws Exception {
        // connect to the network and invoke the smart contract
        Gateway gateway = connect();
        // get the network and contract
        Network network = gateway.getNetwork("mychannel");
        Contract contract = network.getContract("testStore");

        byte[] result;
        // 查询使用evaluateTransaction，修改和添加使用submitTransaction
        result = contract.submitTransaction("CreatePartitionMetadata", partitionHash, partitionAddress,
                String.valueOf(partitionType));

        String resJson = new String(result);
        JSONObject jsonObject = JSON.parseObject(resJson);
        if(jsonObject.getInteger("code") != 200){
            return PartitionMetadata.builder().build();
        }
        String partitionJson = jsonObject.get("data").toString();
        PartitionMetadata partitionMetadata = JSONObject.parseObject(partitionJson, PartitionMetadata.class);
        System.out.println(partitionMetadata.toString());
        return partitionMetadata;
    }
}
