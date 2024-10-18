package com.dct.utils;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dct.common.constant.consist.MainConstant;
import com.google.common.net.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * spring mvc MultipartFile 工具类 进行文件上传   留着
 * 
 * @author Joe
 * 
 */
@Component
@Slf4j
public class SpringMvcFileUpLoad {
	

	@Value("${s3.bucketName}")
	private String bucketName;
	
	@Autowired
	private S3Util s3Util;
	
	/** 取S3服务器的地址 // 10M 10485760; 20M
						// 20971520; 50M
						// 52428800; 100M
						// 104857600
	 */
	private final static long MAX_UPLOAD_SIZE = 104857600;
	
	public final static String CODE_TEXT = "code";
	public final static String MESSAGE_TEXT = "message";
	public final static String CODE_BAD_REQUEST = "400";

	/**
	 * 主要是上传图片，音频文件，视频文件，上传后名字修改为一个uuid.后缀名
	 * 
	 * @param file  需要上传的文件对象
	 * @param s3Path
	 * @return
	 */
	public String fileUpload(MultipartFile file, String fileName, String s3Path) {
		// 先把文件从客户端传到服务器
		try {
			String prefix = fileName.substring(fileName.lastIndexOf("."));
			File tempFile = File.createTempFile(UUID.randomUUID().toString(),prefix);
			file.transferTo(tempFile);
			// 再次调用S3上传的方法，把服务器上的文件传至S3服务器上
			Map<String, File> map = new HashMap<String, File>(1);
			map.put(fileName, tempFile);
			s3Util.uploadToS3(s3Path, map);
			deleteFile(tempFile);
			String s3Url = getS3UrlHead().trim() + "product/"  + fileName;
			return s3Url;
		}catch (Exception e){
			log.error("SUBMIT TO S3 ERROR:{}",e.getMessage());
		}
		return "";

	}


	@Async
	public void uploadFilesToS3(Map<String, List<File>> completeS3Result) {
		try {
			List<File> creatorFileList = completeS3Result.get("creator");
			List<File> pidFileList = completeS3Result.get("pid");
			submitToS3(creatorFileList,"creator");
			submitToS3(pidFileList,"pid");
		}catch (Exception e){
			log.error("SUBMIT S3 ERROR:{}",e.getStackTrace());

		}

	}


	/**
	 * 上传到S3.
	 * @param fileList
	 */
	public void submitToS3(List<File> fileList, String type) {
		if (fileList != null && fileList.size() > 0) {
			fileList.stream().forEach(a->{
				File file = a;
				String s3Path = type + "/" + a.getName();
				ObjectMetadata md = new ObjectMetadata();
				md.setContentType(MediaType.JSON_UTF_8.toString());
				// 上传到S3服务器，同时赋予该Object一个public read权限，以便通过URL直接访问
				try {
					s3Util.submitFile(s3Path,file);
				}catch (Exception e){
					log.error("SUBMIT S3 ERROR:{}",e.getStackTrace());
				}

			});

		}
	}


	public void deleteFile(File excelFile) {
		if (excelFile.exists()) {
			excelFile.delete();
		}
	}


	/**
	 * 文件上传至服务器上
	 *
	 * @param filePath 传入服务器的目录
	 * @param file spring的文件上传工具类
	 * @return
	 */
	public static File upLoad(String filePath, MultipartFile file,String fileName) {
		// 取文件名
		File targetFile = null;
		if(null != fileName){
			targetFile = new File("", fileName);
			if (!targetFile.exists()) {
				 targetFile.mkdirs();
			}
			// 上传文件
			try {
				file.transferTo(targetFile);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}

		return targetFile;
	}

	/**
	 * 文件上传控制
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String validateFile(MultipartFile file, String fileType) {
		if (file == null) {
			return "上传文件是空！";
		}
		String fileName = file.getOriginalFilename();
		String type = file.getContentType();
		long size = file.getSize();
		if (size > MAX_UPLOAD_SIZE) {
			return fileName + "文件大于100M请选择其他工具！";
		}
		if (!type.startsWith(fileType)) {
			return fileName + "请检查上传文件的格式！";
		}

		return "SUCCESS";
	}

	/**
	 * 删除filePath对应的文件
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static boolean deleteFile(String filePath) {
		boolean success = Boolean.FALSE;
		File f = new File(filePath);
		if (f.exists()) {
		boolean delete =f.delete();
		if(delete){
		log.info("delete file success");
		}

			success = Boolean.TRUE;
		}
		return success;
	}
	
	public static Map<String, String> getBadRequestMap(String errorMessage) {
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_BAD_REQUEST);
		map.put(MESSAGE_TEXT, errorMessage);
		return map;
	}
	private String getS3UrlHead(){
		return "https://dct-gmv.s3.ap-southeast-1.amazonaws.com/";
	}

}
