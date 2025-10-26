package com.example.demo.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文件返回处理器组件
 * 用于处理歌曲文件相关的资源信息提取与封装，如获取歌曲封面、音频路径、歌手名、歌曲名等
 */
@Component
public class FileReturnProcess {

    // 注入文件名读取工具类，用于读取指定路径下的文件名列表
    @Autowired
    private FileNameReader fileNameReader;

    /**
     * 分割原始字符串以提取歌手名和歌曲名
     * 假设原始字符串格式为 "歌手名 - 歌曲名 [其他信息]"
     * @param original 原始字符串（包含歌手和歌曲信息）
     * @return 包含歌手名和歌曲名的列表，索引0为歌手名，索引1为歌曲名
     */
    public List<String> spiltString(String original) {
        // 存储字符串中空格的索引（当前逻辑中未实际使用，可能为预留处理）
        List<Integer> spaceIndices = new ArrayList<>();
        for (int i = 0; i < original.length(); i++) {
            if (original.charAt(i) == ' ') {
                spaceIndices.add(i);
            }
        }

        List<String> res = new ArrayList<>();
        // 查找" - "的索引，作为歌手名的结束位置
        int sonerEnd = original.indexOf(" - ");
        // 查找" ["的索引，作为歌曲名的结束位置
        int nameEnd = original.indexOf(" [");

        // 截取歌手名（从开头到" - "的位置）
        String songerName = original.substring(0, sonerEnd);
        // 截取歌曲名（从" - "之后到" ["之前）
        String songName = original.substring(sonerEnd + 3, nameEnd);
        res.add(songerName);
        res.add(songName);
        return res;
    }

    /**
     * 根据文件名获取对应的歌曲图片路径（假设图片与文件同名，后缀为.jpg）
     * @param fileName 原始文件名（如xxx.mp3）
     * @return 对应的图片路径（如xxx.jpg）
     */
    private String getSongImage(String fileName) {
        // 查找文件扩展名的起始位置（.的索引）
        int ind = fileName.indexOf("].");
        // 截取文件名前缀（不含扩展名），拼接.jpg作为图片路径
        String s = fileName.substring(0, ind+1);
        return s + ".jpg";
    }

    /**
     * 根据文件名获取对应的歌曲音频路径（假设音频文件后缀为.mp3）
     * @param fileName 原始文件名（如xxx其他后缀）
     * @return 对应的音频路径（如xxx.mp3）
     */
    private String getSongAudio(String fileName) {
        // 查找文件扩展名的起始位置（.的索引）
        int ind = fileName.indexOf("].");
        // 截取文件名前缀（不含扩展名），拼接.mp3作为音频路径
        String s = fileName.substring(0, ind+1);
        return s + ".mp3";
    }

    /**
     * 获取指定路径下所有歌曲的资源信息列表
     * 包括歌曲ID、封面路径、音频路径、标题、艺术家等信息
     * @param path 歌曲文件所在的路径
     * @return 包含所有歌曲资源信息的Map列表，每个Map对应一首歌曲的信息
     */
    public List<Map<String, Object>> getAllSongsResource(String path) {
        // 读取指定路径下的所有文件名
        List<String> filenames = fileNameReader.getFileNames(path);
        // 用于存储最终的歌曲资源信息列表
        List<Map<String, Object>> resMap = new ArrayList<>();
        // 资源文件的基础路径前缀
        String pre = "/res/";
        // 对文件名列表进行排序
        Collections.sort(filenames);

        // 遍历文件名列表（步长为2，可能假设文件成对存在，如一个音频一个图片）
        for (int i = 0; i < filenames.size(); i += 2) {
            // 歌曲ID（当前固定为1，实际应根据业务需求自增或从文件中提取）
            int id = 1;
            // 存储单首歌曲的资源信息
            Map<String, Object> item = new HashMap<>();
            // 解析文件名获取歌手名和歌曲名
            List<String> songInfo = spiltString(filenames.get(i));
            
            // 封装歌曲信息到Map中
            item.put("id", id);
            item.put("cover", pre + getSongImage(filenames.get(i))); // 封面图片完整路径
            item.put("url", pre + getSongAudio(filenames.get(i)));  // 音频文件完整路径
            item.put("title", songInfo.get(1));                     // 歌曲标题
            item.put("artist", songInfo.get(0));                    // 歌手名
            
            // 将当前歌曲信息添加到结果列表
            resMap.add(item);
        }
        return resMap;
    }
}   