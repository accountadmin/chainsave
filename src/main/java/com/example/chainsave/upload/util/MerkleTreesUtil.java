package com.example.chainsave.upload.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author larry.xiang
 */
public class MerkleTreesUtil {

    // 默克根
    String merkleRoot;

    // 交易集合
    List<String> txLst;

    /**
     * 初始化
     *
     * @param txList 交易集合
     */
    public MerkleTreesUtil(List<String> txList) {
        this.txLst = txList;
        merkleRoot = "";
    }


    /**
     * 获取Node Hash List
     *
     * @return
     */
    private List<String> getNodeHashList(List<String> tempTxList) {

        List<String> newTxList = new ArrayList<String>();
        int index = 0;
        while (index < tempTxList.size()) {
            // left
            String left = tempTxList.get(index);
            index++;
            // right
            String right = "";
            if (index != tempTxList.size()) {
                right = tempTxList.get(index);
            }
            // 两两加密
            String sha2HexValue = FileSHA256Util.getSHA256StrJava(left + right);
            // 双重hash
            sha2HexValue = FileSHA256Util.getSHA256StrJava(sha2HexValue);
            newTxList.add(sha2HexValue);
            index++;
        }
        return newTxList;
    }


    /**
     * 构造默克树，设置默克根
     */
    public void merkle_tree() {
        List<String> tempTxList = new ArrayList<String>();
        for (int i = 0; i < this.txLst.size(); i++) {
            tempTxList.add(this.txLst.get(i));
        }
        List<String> newTxList = getNodeHashList(tempTxList);
        //一直循环直到只剩下一个hash值
        while (newTxList.size() != 1) {
            newTxList = getNodeHashList(newTxList);
        }

        this.merkleRoot = newTxList.get(0);
    }


    /**
     * 获取默克根
     *
     * @return
     */
    public String getMerkleRoot() {
        return this.merkleRoot;
    }
}