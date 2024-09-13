package com.dct.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * Amazon S3云存储工具类   留着
 * 
 * @author John 2014-04-21
 *
 */
@Component
@Slf4j
public class S3Util {
	
	/**
	 * 上传的目录
	 */
	private static final String UPLOAD_PATH = "ContentManage";

	@Autowired(required = true)
	private AmazonS3 s3Client;
	

    /** 存储段(桶名) */
    @Value("${s3.bucketName}")
    private String bucket;



	
	/**
	 * 上传到S3服务器
	 * 
	 * @param otherPath
	 * 				参考FileType枚举，上传的内容属于什么类型，key的组成部分
	 * @param files
	 * 				上传的文件集合<br>
	 * 
	 * s3 key:与map的key意义相同，唯一对应一个对象。组成：fileType + map.key<br>
	 * 例如：key的名字为images/example.jpg,则访问该图片地址为http://justdownit.s3.amazonaws.com/images/example.jpg<br>
	 *       key的名字为doc/example,访问该对象地址为http://justdownit.s3.amazonaws.com/doc/example
	 */
	public void uploadToS3(String otherPath, Map<String, File> files) {
		if (files != null && files.size() > 0) {
			File file = null;
			Iterator<Map.Entry<String,File>> entryIterator = files.entrySet().iterator();
			while(entryIterator.hasNext()){
				Map.Entry<String,File> entry = entryIterator.next();
				file = entry.getValue();
				String key = entry.getKey();
				String s3Path = otherPath + key;
				// 上传到S3服务器，同时赋予该Object一个public read权限，以便通过URL直接访问
				s3Client.putObject(new PutObjectRequest(bucket,s3Path , file).withCannedAcl(CannedAccessControlList.PublicRead));
			}
		}
	}
	

	

	
	/**
	 * 根据key获取对象
	 * 
	 * @param key
	 * @return
	 */
	public S3Object getS3ObjectByKey(String key) {
		return s3Client.getObject(new GetObjectRequest(bucket, key));
	}
	
	public void deleteS3ObjectByKey(String key){
		s3Client.deleteObject(new DeleteObjectRequest(bucket, key));
	}



}