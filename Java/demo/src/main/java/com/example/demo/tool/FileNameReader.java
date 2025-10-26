package com.example.demo.tool;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileNameReader {

    private final ResourceLoader resourceLoader;

    public FileNameReader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public static boolean renameFile(List<String> oldFilePath) {
        for (int i = 0; i < oldFilePath.size(); i++) {
            File oldFile = new File(oldFilePath.get(i));
            int proInd = oldFile.getName().indexOf("].");
            String nxt = oldFile.getName().substring(proInd + 1, oldFile.getName().length());
            int preInd = oldFile.getName().indexOf(" [");
            String pre = oldFile.getName().substring(0, preInd);
            if (!oldFile.exists()) {
                System.out.println("原文件不存在");
                return false;
            }
            String parentPath = oldFile.getParent();
            String newFileName = pre + nxt;
            File newFile = new File(parentPath + File.separator + newFileName);
            if(! oldFile.renameTo(newFile))
            {
                return false;
            }
           
        }
        return true;
    }

    public List<String> getFileNames(String directoryPath) {
        List<String> fileNames = new ArrayList<>();
        // 1. 利用Spring的资源加载器获取目录资源
        Resource directoryResource = resourceLoader.getResource("classpath:" + directoryPath);

        // 2. 校验路径是否存在
        if (!directoryResource.exists()) {
            throw new IllegalArgumentException("目录不存在：" + directoryPath);
        }

        File directory;
        try {
            directory = directoryResource.getFile();
        } catch (IOException e) {
            throw new RuntimeException("无法访问资源（可能是jar包内资源或非文件系统资源）：" + directoryPath, e);
        }

        // 3. 校验路径是否为目录
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("路径不是目录：" + directoryPath);
        }

        // 5. 遍历目录下的所有文件，提取文件名

        File[] list = directory.listFiles();
        if (list != null) {
            for (File i : list) {
                fileNames.add(i.getName());
            }
        } else {
            System.out.println("没有文件");

        }

        return fileNames;
    }

}
