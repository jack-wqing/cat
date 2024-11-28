package com.dianping.cat.test;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * 通过自由定义文件指针来随机访问文件
 *  只能读写文件，不能读写其它的id节点
 *  1、mode: 指定读取的模式: r, rw, rws: 对文件内容或元数据得更新都同步写入到基础存储设备], rwd:[对文件的内容同步写入到基础存储设备]
 *  2、RandomAccessFile对象包含一个记录指针，用以标识当前读写的位置；
 *      getFilePointer返回文件记录指针的当前位置
 *      seek(long pos): 将文件指针定位到pos为止
 */
public class RandomAccessFileTest {

    public static void main(String[] args) throws FileNotFoundException {

        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/qing/1.txt", "rw");


    }

}
