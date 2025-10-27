package com.example.demo.tool;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 媒体资源处理工具类（修正版）
 */
@Component
public class MediaResourceProcessor {

    // 匹配文件名格式的正则表达式：歌手名 - 歌曲名 (其他信息) [标识]
    private static final Pattern FILENAME_PATTERN = Pattern.compile("^(.+?)\\s+-\\s+(.+?)\\s*(\\(.+?\\)\\s*)*\\[.+?]$");

    /**
     * 处理指定资源文件夹中的媒体文件
     */
    public List<Map<String, Object>> processMediaFiles(String resourceDir) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:" + resourceDir + "/*.*");

        // 按基础文件名（不含扩展名）分组
        Map<String, List<Resource>> fileGroups = new HashMap<>();
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename == null)
                continue;

            String baseName = StringUtils.stripFilenameExtension(filename);
            fileGroups.computeIfAbsent(baseName, k -> new ArrayList<>()).add(resource);
        }

        // 处理webp转jpg
        Set<String> convertedWebpFiles = convertWebpToJpg(fileGroups, resourceDir);

        // 构建返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        int id = 1;

        for (Map.Entry<String, List<Resource>> entry : fileGroups.entrySet()) {
            String baseName = entry.getKey();
            List<Resource> groupResources = entry.getValue();

            // 解析歌手名和歌名
            Matcher matcher = FILENAME_PATTERN.matcher(baseName);
            String artist = "";
            String title = "";
            if (matcher.matches()) {
                artist = matcher.group(1).trim();
                title = matcher.group(2).trim();
            } else {
                title = baseName; // 匹配失败时用完整文件名作为标题
            }

            // 查找音频文件和封面文件
            String audioPath = null;
            String coverPath = null;

            // 遍历资源时生成路径（重点：只写 "res/"，不包含 "static/"）
            for (Resource resource : groupResources) {
                String filename = resource.getFilename();
                if (filename == null)
                    continue;

                String extension = StringUtils.getFilenameExtension(filename).toLowerCase();
                if ("mp3".equals(extension)) {
                    // 正确：固定 "res/" + 文件名，和 ResourceHandler("/res/**") 匹配
                    audioPath = "res/" + filename;
                } else if ("jpg".equals(extension)) {
                    coverPath = "res/" + filename;
                }
            }

            // WebP转换后的路径也一样
            if (coverPath == null && convertedWebpFiles.contains(baseName)) {
                coverPath = "res/" + baseName + ".jpg";
            }

            // 添加有效音频信息
            if (audioPath != null) {
                Map<String, Object> mediaInfo = new HashMap<>();
                mediaInfo.put("id", id++);
                mediaInfo.put("title", title);
                mediaInfo.put("artist", artist);
                mediaInfo.put("url", audioPath);
                mediaInfo.put("cover", coverPath != null ? coverPath : "");

                result.add(mediaInfo);
            }
        }

        return result;
    }

    /**
     * 将webp文件转换为jpg文件（修正资源目录判断逻辑）
     */
    private Set<String> convertWebpToJpg(Map<String, List<Resource>> fileGroups, String resourceDir)
            throws IOException {
        Set<String> convertedFiles = new HashSet<>();
        File targetDir = null;
        boolean isDirectoryWritable = false;

        // 尝试获取资源目录的文件系统路径（仅在开发环境/非JAR包运行时有效）
        try {
            // 先尝试通过ClassPathResource获取目录
            Resource classPathResource = new ClassPathResource(resourceDir);
            // 只有文件系统资源才能转换为File，JAR包内资源会抛异常
            targetDir = classPathResource.getFile();
            // 判断是否为目录且可写
            isDirectoryWritable = targetDir.isDirectory() && targetDir.canWrite();
        } catch (IOException e) {
            // 尝试从项目源码目录获取（适用于开发环境）
            String projectResourcePath = System.getProperty("user.dir") + "/src/main/resources/" + resourceDir;
            targetDir = new File(projectResourcePath);
            if (targetDir.exists() && targetDir.isDirectory() && targetDir.canWrite()) {
                isDirectoryWritable = true;
            } else {
                System.err.println("资源目录不可写或不存在，跳过WebP转换: " + e.getMessage());
                return convertedFiles;
            }
        }

        // 处理webp文件转换
        for (List<Resource> resources : fileGroups.values()) {
            for (Iterator<Resource> iterator = resources.iterator(); iterator.hasNext();) {
                Resource resource = iterator.next();
                String filename = resource.getFilename();
                if (filename == null)
                    continue;

                String extension = StringUtils.getFilenameExtension(filename).toLowerCase();
                if ("webp".equals(extension)) {
                    String baseName = StringUtils.stripFilenameExtension(filename);
                    String jpgFilename = baseName + ".jpg";
                    File jpgFile = new File(targetDir, jpgFilename);

                    // 检查jpg文件是否已存在
                    if (jpgFile.exists()) {
                        System.out.println("JPG文件已存在，跳过转换: " + jpgFilename);
                        iterator.remove();
                        resources.add(new FileSystemResource(jpgFile));
                        convertedFiles.add(baseName);
                        continue;
                    }

                    // 读取webp文件并转换为jpg
                    try (InputStream inputStream = resource.getInputStream()) {
                        BufferedImage image = ImageIO.read(inputStream);
                        if (image != null) {
                            ImageIO.write(image, "jpg", jpgFile);
                            System.out.println("已转换: " + filename + " -> " + jpgFilename);

                            // 替换列表中的webp资源为jpg资源
                            iterator.remove();
                            resources.add(new FileSystemResource(jpgFile));
                            convertedFiles.add(baseName);
                        } else {
                            System.err.println("无法读取WebP文件: " + filename);
                        }
                    } catch (IOException e) {
                        System.err.println("转换WebP文件失败 " + filename + ": " + e.getMessage());
                    }
                }
            }
        }

        return convertedFiles;
    }
}