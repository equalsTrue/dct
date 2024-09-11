package com.dct.utils;/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/10 17:21
 */

import com.dct.constant.enums.NumberEnum;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/10 17:21 
 */
public class FileUtil {

    /**
     * multipartFileToFile.
     *
     * @param multipartFile
     * @return
     */
    public static File multipartFileToFile(MultipartFile multipartFile) {
        File file = null;
        //判断是否为null
        if (multipartFile == null) {
            return file;
        }
        InputStream ins = null;
        OutputStream os = null;
        try {
            ins = multipartFile.getInputStream();
            file = new File(multipartFile.getOriginalFilename());
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[NumberEnum.EIGHT_ONE_NINE_TWO.getNum()];
            while ((bytesRead = ins.read(buffer, 0, NumberEnum.EIGHT_ONE_NINE_TWO.getNum())) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }


    public static void deleteFile(File excelFile) {
        if (excelFile.exists()) {
            excelFile.delete();
        }
    }
}
