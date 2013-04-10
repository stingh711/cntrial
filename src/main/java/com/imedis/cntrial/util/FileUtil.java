package com.imedis.cntrial.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.regex.Pattern;

public class FileUtil {
	private static final int BUFFER_SIZE = 16 * 1024;
	private static String savePath;
	private static String imagePathBase;
	static
	{
		savePath = Constants.SAVE_PATH;
		imagePathBase = Constants.IMAGE_PATH;
	}
	
	public static void fileCopy(File src, File dest) {
		InputStream in = null;
		OutputStream out = null;
		try{
			in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
			out = new BufferedOutputStream(new FileOutputStream(dest), BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE];
			int len = 0;
			while((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try{
					in.close();
				} catch(IOException ex){
					ex.printStackTrace();
				}
			}
			if(out != null) {
				try{
					out.close();
				} catch(IOException ex){
					ex.printStackTrace();
				}
			}
		}
	}
	
	/***
	 * 上传本地文件
	 * @param filepath
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String copyFile(File[] filepath, String[] fileName) throws Exception {
		if (filepath == null || fileName == null)
			return null;
			String imageUrls = null;
			Random random = new Random();
			String uploadPathAndName = getSavePath() + System.currentTimeMillis() + random.nextInt(10000)
					+ fileName[0];

			/* sturts的临时文件拷贝到本地 */
			fileCopy(filepath[0], new File(uploadPathAndName));
			imageUrls = uploadPathAndName;
		return imageUrls;
	}

	public static String getSavePath() {
		return savePath;
	}

	public static void setSavePath(String savePath) {
		FileUtil.savePath = savePath;
	}

	public static String getImagePathBase() {
		return imagePathBase;
	}

	public static void setImagePathBase(String imagePathBase) {
		FileUtil.imagePathBase = imagePathBase;
	}
}
