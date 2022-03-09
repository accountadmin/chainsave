package com.example.chainsave;

import com.example.chainsave.fabric.service.FileMetadataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author WaleGarrett
 * @Date 2022/3/9 21:15
 */
public class FabricSDKTest {
    @Autowired
    FileMetadataService fileMetadataService;

    @Test
    public void testCreateFileMetadata() throws Exception {
        System.out.println(FileMetadataService.createFileMetadata());
    }

    @Test
    public void testQueryFileMetadata() throws Exception {
        System.out.println(FileMetadataService.queryFileMetadataById(5));
    }
}
