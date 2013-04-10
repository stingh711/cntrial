package com.imedis.cntrial.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.ImageIcon;

import com.imedis.cntrial.util.exception.BaseException;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImgUtil {
	private static final int BUFFER_SIZE = 16 * 1024;
	private static String savePath;
	private static String imagePathBase;
	
	private static String imageRootPath = Constants.SAVE_PATH; 
	private String prePath = "titlepicture"; 
	private static String imagePathHead = "images";
	private static String thumbnailsPathHead = "thumbnails"; 
	private String temporaryPathHead = "temporary"; 
	private static int imageMaxSize = 800; 
	private static int thumbnailsMaxSize = 150; 

	private static String ERROR_IMAGENOTEXIST = "图片文件不存在";
	private static String ERROR_IMAGENOTSUPPORT = "图片类型不支持";
	
	public static String IMAGE_TYPE_JPG = "jpg";
	public static String IMAGE_TYPE_GIF = "gif";
	public static String IMAGE_TYPE_BMP = "bmp";
	public static String IMAGE_TYPE_PNG = "png";
	public static String IMAGE_TYPE_SWF = "swf";
	
	public static final int IMAGE_CODEC_UNKNOWN = 0;
	public static final int IMAGE_CODEC_JPG = 1;
	public static final int IMAGE_CODEC_GIF = 2;
	public static final int IMAGE_CODEC_BMP = 3;
	public static final int IMAGE_CODEC_PNG = 4;
	public static final int IMAGE_CODEC_SWF = 5;
	
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
	
	/**
	 * 上传图片
	 * @param images
	 * @param imagesFileNames
	 * @param imgSavePath：图片的保存路径是WebContent/uploadfiles目录下
	 * @param height：大图尺寸
	 * @param thumbnailsHeight：小图尺寸
	 * @param isWatermark:是否打水印
	 * @return
	 * @throws Exception
	 * @author zhaochunjiao
	 * @create 2013-1-16 下午08:44:59
	 */
	public static String[] uploadImages(File[] images, String[] imagesFileNames, String imgSavePath,
			int height ,int thumbnailsHeight ,boolean isWatermark) throws Exception{
		String[] imageUrls = new String[images.length];
		for (int i = 0; i < images.length; i++) {
			Random random = new Random();
			//本地临时图片路径
			String uploadPathAndName = getSavePath() + System.currentTimeMillis() + i + random.nextInt(10000) + imagesFileNames[i];
			/* sturts的临时图片拷贝到本地 */
			fileCopy(images[i], new File(uploadPathAndName));
			//调整图片大小，并保存到本地目录
			imageUrls[i] = dealImage(uploadPathAndName, height, thumbnailsHeight, imgSavePath);
		}
		
		return imageUrls;
	}
	
	/**
	 * 调整图片大小，并保存到本地目录
	 * @param fileName：本地临时文件路径
	 * @param height
	 * @param thumbnailsHeight
	 * @param imgSavePath：图片的保存路径是WebContent/uploadfiles目录下 
	 * @return
	 * @throws Exception
	 * @author zhaochunjiao
	 * @create 2013-1-18 上午10:49:38
	 */
	private static String dealImage(String fileName, int height, int thumbnailsHeight, String imgSavePath) throws Exception{
		File file = new File(fileName);
		
		//如果临时文件不存在，则抛出异常
		if (file.exists() == false) {
			throw new BaseException(ERROR_IMAGENOTEXIST);
		}
		
		//获得文件后缀
		String extName = getImageType(file);
		if (extName == null) {
			throw new Exception(ERROR_IMAGENOTSUPPORT);
		}
		int codec = getCodec(extName);
		if (codec == ImgUtil.IMAGE_CODEC_UNKNOWN) {
			throw new BaseException(ERROR_IMAGENOTSUPPORT);
		}
		
		//图片路径（如：/02/12/021243456）
		String newFileName = generateImagePath();
		String imagePath = newFileName + "." + extName;//图片路径 （如：/02/12/021243456.jpg）
		
		//大图片路径 （如：/usr/local/conf/struts/image/02/12/021243456.jpg）
		String newImageName = imageRootPath + imagePathHead + imagePath;
		
		//小图片路径 （如：/usr/local/conf/struts/thumbnails/02/12/021243456.jpg）
		String thumbnailImageName = imageRootPath + thumbnailsPathHead + imagePath;
		File fileNewImage = new File(newImageName);
		File fileNewThumbnail = new File(thumbnailImageName);
		
		//如果不是swf格式的文件，那么调整图片大小
		if (!extName.equals(ImgUtil.IMAGE_TYPE_SWF)) {
			int imageMaxSizeTemp;
			int thumbnailsMaxSizeTemp;
			if (height > 0) {
				imageMaxSizeTemp = height;
			} else {
				imageMaxSizeTemp = imageMaxSize;
			}
			if (thumbnailsHeight > 0) {
				thumbnailsMaxSizeTemp = thumbnailsHeight;
			} else {
				thumbnailsMaxSizeTemp = thumbnailsMaxSize;
			}

			ImgUtil.resizeForWidth(file, fileNewImage, imageMaxSizeTemp, 0.9f);
			ImgUtil.resizeForWidth(fileNewImage, fileNewThumbnail, thumbnailsMaxSizeTemp, 0.9f);

			file.delete();
		} else { // flash
			
		}

		if (!extName.equals(ImgUtil.IMAGE_TYPE_SWF)) {
			File thumbnailsFile = new File(thumbnailImageName);
			FileInputStream fis = new FileInputStream(thumbnailsFile);
			byte[] thumbnailsContent = new byte[(int) thumbnailsFile.length()];
			fis.read(thumbnailsContent, 0, (int) thumbnailsFile.length());
			
			//图片文件copy到本地
			//imagePath:（如：/02/12/021243456.jpg）
			//newImageName:（如：/usr/local/conf/struts/image/02/12/021243456.jpg）
			//imgSavePath:(如：)
			ImgUtil.stockImage(imagePath, newImageName, thumbnailsContent, imgSavePath+"/");
			
			//imageFileManager.stockImage(imagePath, newImageName, thumbnailsContent);
			fis.close();
			fis = null;
			thumbnailsFile = null;
			thumbnailsContent = null;
			fileNewThumbnail.delete();
			fileNewImage.delete();
		} else { // flash
			
		}
		
		return imagePath;
	}
	
	//imagePath:（如：/02/12/021243456.jpg）
	//filename:（如：/usr/local/conf/struts/image/02/12/021243456.jpg）
	private static void stockImage(String imagePath, String filename, byte[] thumbnailsContent, String imgSavePath) throws Exception {
		if ((filename == null) || (filename.length() == 0)) {
			throw new Exception("ImgUtil.stockImage: filename is null or length is 0");
		}
		File srcFile = new File(filename);
		if (!srcFile.exists()) {
			throw new Exception("ImgUtil.stockImage: file[" + filename + "] not exist");
		}

		stockImage2(imgSavePath, imagePath, filename, null, thumbnailsContent);
	}
	
	//imagePath:（如：/02/12/021243456.jpg）
	//filename:（如：/usr/local/conf/struts/image/02/12/021243456.jpg）
	private static void stockImage2(String imgSavePath, String imagePath, String filename, byte[] imageContent, byte[] thumbnailsContent) throws Exception {
		String dstImageFilename = imgSavePath + imagePathHead + imagePath;
		String dstTthumbnailsFilename = imgSavePath + thumbnailsPathHead + imagePath;

		File dstImageFile = new File(dstImageFilename);
		if (dstImageFile.exists()) {
			dstImageFile.delete();
		} else {
			assertDirectory(dstImageFilename);
		}

		File dstTthumbnailsFile = new File(dstTthumbnailsFilename);
		if (dstTthumbnailsFile.exists()) {
			dstTthumbnailsFile.delete();
		} else {
			assertDirectory(dstTthumbnailsFilename);
		}

		if (filename != null) {
			copyFile2(filename, dstImageFilename);
		} else {
			OutputStream imageOs = new FileOutputStream(dstImageFile);
			imageOs.write(imageContent);
			imageOs.close();
		}

		if (thumbnailsContent != null) {
			OutputStream thumbnailsOs = new FileOutputStream(dstTthumbnailsFile);
			thumbnailsOs.write(thumbnailsContent);
			thumbnailsOs.close();
		}
	}
	
	private static void assertDirectory(String path) throws Exception {
		int n = path.lastIndexOf('/');
		if (n > 0) {
			File directory = new File(path.substring(0, n));
			if (!directory.exists()) {
				boolean b = directory.mkdirs();
				if (!b) {
					throw new Exception("创建目录[" + path + "]失败");
				}
			}
		}
	}
	
	private static int getCodec(String extName) {
		int codec = ImgUtil.IMAGE_CODEC_UNKNOWN;
		if (extName.toLowerCase().equals(ImgUtil.IMAGE_TYPE_JPG)) {
			codec = ImgUtil.IMAGE_CODEC_JPG;
		} else if (extName.toLowerCase().equals(ImgUtil.IMAGE_TYPE_GIF)) {
			codec = ImgUtil.IMAGE_CODEC_GIF;
		} else if (extName.toLowerCase().equals(ImgUtil.IMAGE_TYPE_BMP)) {
			codec = ImgUtil.IMAGE_CODEC_BMP;
		} else if (extName.toLowerCase().equals(ImgUtil.IMAGE_TYPE_PNG)) {
			codec = ImgUtil.IMAGE_CODEC_PNG;
		} else if (extName.toLowerCase().equals(ImgUtil.IMAGE_TYPE_SWF)) {
			codec = ImgUtil.IMAGE_CODEC_PNG;
		} else {
			codec = ImgUtil.IMAGE_CODEC_UNKNOWN;
		}
		return codec;
	}
	
	/**
	 * 生成图片路径
	 * @return
	 * @author zhaochunjiao
	 * @create 2013-1-16 下午08:55:39
	 */
	private static String generateImagePath() {
		/*
		 * ȡ��ǰ΢����Ϊ}��Ŀ¼����ļ��� �õ���ֵΪ14λ���� ȡ��9��10λ���һ��Ŀ¼����11��12λ��ڶ���Ŀ¼ ���ֵ��Ϊ�ļ���
		 * System.nanoTime()���ȿ��ܻ�仯 ���ؽ������"/12/80/11932505128086"
		 */
		String fileName = Long.toHexString(System.nanoTime());
		int nameLength = fileName.length();
		// ���õ��ĳ���С��6�������һ�ν�}�ν��l��4
		if (nameLength < 6) {
			fileName = fileName + Long.toHexString(System.nanoTime());
			nameLength = fileName.length();
		}
		String firstFolder = fileName.substring(nameLength - 6, nameLength - 4);
		String secondFolder = fileName.substring(nameLength - 4, nameLength - 2);
		String twoFolders = "/" + firstFolder + "/" + secondFolder;
		// �����༶�ļ���
		ImgUtil.createFolder(imageRootPath + imagePathHead + twoFolders);
		ImgUtil.createFolder(imageRootPath + thumbnailsPathHead + twoFolders);
		return twoFolders + "/" + fileName;
	}

//	/*
//	 * 
//	 * 使用例子：【 String[] productpics = UploadImagesUtil.uploadImages(productpicture, productpictureFileName, creatorId,
//	 * ACT.product.getModelId(), ACT.product.getDealEntityType(), ACT.product.getModelId(), 440, 183, false, imageManager);】
//	 * 文件的上传 执行图片文件的上传功能，上传过程如下： 1）新建Image对象
//	 * 2）调用imageManager.addImage()方法，通过该方法，将产生图片的url 具体的细节在代码的注释中 参数1：文件 参数2：文件名
//	 * 3)isWatermark == ture ;表示打水印
//	 */
//	public static String[] uploadImages(File[] images, String[] imagesFileName,long creatorId ,short refEntityId, short refEntityType,
//			short refModule, int height ,int thumbnailsHeight ,boolean isWatermark) throws Exception {
//		if (images == null || imagesFileName == null)
//			return null;
//		String[] imageUrls = new String[images.length];
//		/* 新建图片对象，并调用imageManager的addImage()方法 */
//		for (int i = 0; i < images.length; i++) {
//			if (images[i] == null) {
//				imageUrls[i] = "";
//			}else {
//				/* 图片库功能，管理图片的上传 */
//				Image image = new Image(); // 新建Image对象
//
//				/* 设置Image对象的属性，属性主要为页面传递。由于目前为例子，所以属性设置为固定值 */
//				image.setHeight(height);
//				image.setThumbnailsHeight(thumbnailsHeight);
//
//				// 图片的创建者信息
//				image.setCreatorId(creatorId); // 创建者Id
//				image.setRefEntityId(refEntityId);
//				image.setRefEntityType(refEntityType);
//				image.setRefModule(refModule);
//				image.setCreatorType(Image.IMAGE_CREATORTYPE_OPERATOR);
//				/* uploadPathAndName为目标文件路径,即将图片上传到本地应用的某个文件夹内，名字为上传时所用的名字 */
//				// String uploadPathAndName = getSavePath() + imagesFileName[i];
//				Random random = new Random();
//				String uploadPathAndName = getSavePath()
//						+ System.currentTimeMillis() + i + random.nextInt(10000)
//						+ imagesFileName[i];
//				/* sturts的临时文件拷贝到本地 */
//				fileCopy(images[i], new File(uploadPathAndName));
//
//				/*
//				 * 调用图片库imageManager的addImage方法，上传图片并生成图片url
//				 * addImage需2个参数：第一个为上面生成的Image对象，第二个参数为本地图片的路径
//				 */
//				String imgUrl = imageManager.addImage(image, uploadPathAndName);
//				imageUrls[i] = imgUrl;
//				
//				/*给图片打水印*/
//				if(isWatermark){
//					imageManager.watermarkImage(imgUrl);
//				}
//			}
//		}
//		return imageUrls;
//	}
	
//	/***
//	 * 把关联图片copy到本地制定目录
//	 * 
//	 * @param images
//	 * @param imagesFileName
//	 * @return
//	 * @throws Exception
//	 */
//	@SuppressWarnings("unused")
//	private static String[] copyImages(File[] images, String[] imagesFileName)
//			throws Exception {
//		if (images == null || imagesFileName == null)
//			return null;
//		String[] imageUrls = new String[imagesFileName.length];
//		/* 新建图片对象，并调用imageManager的addImage()方法 */
//		for (int i = 0; i < imagesFileName.length; i++) {
//			/* 图片库功能，管理图片的上传 */
//			Image image = new Image(); // 新建Image对象
//
//			Random random = new Random();
//			/* uploadPathAndName为目标文件路径,即将图片上传到本地应用的某个文件夹内，名字为上传时所用的名字 */
//			String uploadPathAndName = getSavePath()
//					+ System.currentTimeMillis() + i + random.nextInt(10000)
//					+ imagesFileName[i];
//
//			/* sturts的临时文件拷贝到本地 */
//			fileCopy(images[i], new File(uploadPathAndName));
//			imageUrls[i] = uploadPathAndName;
//		}
//		return imageUrls;
//	}
	
//	/***
//	 * 
//	 * @param viewContent
//	 * @param title
//	 * @param localURLall == getRequest().getSession().getServletContext().getRealPath("/")
//	 * @param creatorId
//	 * @param refEntityId
//	 * @param refEntityType
//	 * @param refModule
//	 * @param height
//	 * @param thumbnailsHeight
//	 * @param isWatermark == ture ;表示打水印
//	 * @return
//	 * @throws Exception
//	 */
//	public static String loadImg(String viewContent, String localURLall, long creatorId ,short refEntityId, short refEntityType,
//			short refModule, int height ,int thumbnailsHeight ,boolean isWatermark, ImageManager imageManager) throws Exception {
//		String url;
//		boolean flage = false;
//		String postfix = null;
//		String uploadPathAndName = savePath;
//		String regEx_span1 = "<[\\s]*?span[^>]*?>";
//		String regEx_span2 = "<[\\s]*?\\/[\\s]*?span[\\s]*?>";
//		String str_span = viewContent.replaceAll(regEx_span1, "").replaceAll(
//				regEx_span2, "");
//		Pattern p = Pattern.compile("<img");
//		String[] str = p.split(str_span);
//		for (int j = 1; j < str.length; j++) {
//			/* 主要针对QQ的反盗链 */
//			Pattern p1 = Pattern.compile("src=");
//			String[] src = p1.split(str[j]);
//			char rex = '"';
//			Pattern p2 = Pattern.compile(String.valueOf(rex));
//			String[] imgsrc = p2.split(src[1]);
//			url = imgsrc[1];
//			String imagePath = "";
//
//			/* 判断是否来自本网站 */
//
//			Pattern p3 = Pattern.compile(imagePathBase);
//			String[] islocalhost = p3.split(url);
//			if (islocalhost.length == 1) {
//				postfix = url.substring(url.lastIndexOf(".") + 1, url.length())
//						.toLowerCase();
//				// 可下载的图片类型gif|png|jpg|jpeg|bmp
//				if (postfix.equals("jpg") || postfix.equals("jpeg")
//						|| postfix.equals("gif") || postfix.equals("png")
//						|| postfix.equals("bmp")) {
//					
////					Pattern p4 = Pattern.compile("backend");
////					String[] localURLs = p4.split(localURLall);
////					String localURL = localURLs[0];
//					String replaceurl = url.substring(1, url.length());
//					replaceurl = replaceurl.replace("%20", " ");
//					String newurl = "/var/www/html/backend/" + replaceurl;
//					Random random = new Random();
//					String filename = uploadPathAndName
//							+ System.currentTimeMillis() + j
//							+ random.nextInt(10000) + ".jpg";
//					try {
//						// 建立相关的字节输入流
//						FileInputStream fr = new FileInputStream(newurl);
//
//						FileOutputStream fw = new FileOutputStream(filename);
//						byte buffer[] = new byte[1]; // 声明一个byte型的数组，数组的大小是512个字节
//						while (fr.read(buffer) != -1) {
//							fw.write(buffer);
//						}
//						fw.close();
//						fr.close();
//
//						// 上传图片
//						Image image = new Image();
//						image.setHeight(height);
//						image.setThumbnailsHeight(thumbnailsHeight);
//						image.setCreatorId(creatorId); // 创建者Id
//						image.setRefEntityId(refEntityId);
//						image.setRefEntityType(refEntityType);
//						image.setRefModule(refModule);
//						image.setCreatorType(Image.IMAGE_CREATORTYPE_OPERATOR);
//						imagePath = imageManager.addImage(image, filename);
//						/*给图片打水印*/
//						if(isWatermark){
//							imageManager.watermarkImage(imagePath);
//						}
//						flage = false;
//					} catch (Exception e) {
//						flage = true;
//					}
//				}
//				/* 替换原有地址 */
//				if (flage == false) {
//					String oldChar = url;
//					String newChar = imagePathBase + imagePath;
//					viewContent = str_span.replace(oldChar, newChar);
//				}
//			}
//			str_span = viewContent;
//		}
//
//		return viewContent;
//	}	
	
	
	/**
	 * �½�Ŀ¼�����Դ����༶Ŀ¼
	 * 
	 * @param folderPath:
	 *            Ŀ¼·��
	 */
	public static void createFolder(String folderPath) {
		String[] splitedFolder = folderPath.split("/");
		StringBuilder path = new StringBuilder();
		for (int i = 0; i < splitedFolder.length; i++) {
			if (i > 0) {
				path.append("/");
			}
			path.append(splitedFolder[i]);
			File filePath = new File(path.toString());
			if (!filePath.exists())
				filePath.mkdir();
		}
	}
	
	
//	private Image copyImage(Image image, Image imageBean) {
//		imageBean.setRefModule(image.getRefModule());
//		imageBean.setRefEntityType(image.getRefEntityType());
//		imageBean.setRefEntityId(image.getRefEntityId());
//		imageBean.setCreatorId(image.getCreatorId());
//		imageBean.setCreatorType(image.getCreatorType());
//		imageBean.setFlag(image.getFlag());
//		return imageBean;
//	}

	/**
	 * ��СͼƬ�����浽ָ��Ŀ¼����Ҫ��Java������ڴ����һЩ������ᱨ�ڴ治�����
	 * 
	 * @throws Exception
	 */
	public static void resize(File oldImage, File newImage, int maxSize, float quality)
			throws Exception {
		if (quality < 0 || quality > 1) {
			quality = 0.9f;
		}

		String imagePath = oldImage.getCanonicalPath();
		/* ���ͼƬ�ߺͿ� */
		BufferedImage biImage = ImageIO.read(oldImage);
		int imageWidth = biImage.getWidth();
		int imageHeight = biImage.getHeight();

		if (imageWidth > maxSize || imageHeight > maxSize) {
			/* ���ͼƬ����JPG��ʽ������ת��ΪJPG��ʽ */
			String extName = getImageType(oldImage);
			if (!extName.equals(IMAGE_TYPE_JPG)) {
				String tmpFileName = imagePath + "." + extName;
				File fileTemp = new File(tmpFileName);
				oldImage.renameTo(fileTemp);
				String jpgFileName = imagePath + "." + IMAGE_TYPE_JPG;
				File fileJpg = new File(jpgFileName);
				if (fileJpg.exists() == false) {
					try {
						toJPG(tmpFileName, jpgFileName, 100);
					} catch(Exception e) {
						fileTemp.delete();
						fileJpg.delete();
						throw new BaseException(e.getMessage());
					}
				}
				fileTemp.delete();
				fileJpg.renameTo(oldImage);
			}

			ImageIcon imageIcon = new ImageIcon(imagePath);
			Image image = imageIcon.getImage();

			/* ����ͼƬ�ߺͿ� */
			int newWidth = 1;
			int newHeight = 1;
			if (imageWidth > imageHeight) {
				newWidth = maxSize;
				newHeight = (maxSize * imageHeight) / imageWidth;
			} else {
				newWidth = (maxSize * imageWidth) / imageHeight;
				newHeight = maxSize;
			}
			/* �����С���ͼƬ�߻��С��1�������ٸ���ֵΪ1 */
			if (newWidth < 1)
				newWidth = 1;
			if (newHeight < 1)
				newHeight = 1;
			Image resizedImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

			Image tempImage = new ImageIcon(resizedImage).getImage();
			BufferedImage bufferedImage = new BufferedImage(tempImage.getWidth(null), tempImage
					.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics graphics = bufferedImage.createGraphics();

			/* Clear background and paint the image. */
			graphics.setColor(Color.white);
			graphics.fillRect(0, 0, tempImage.getWidth(null), tempImage.getHeight(null));
			graphics.drawImage(tempImage, 0, 0, null);
			graphics.dispose();

			FileOutputStream fo = new FileOutputStream(newImage);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fo);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufferedImage);
			param.setQuality(quality, true);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(bufferedImage);
			fo.close();
		} else {
			/* ���ͼƬ�ߴ�С����С�����ߴ磬����ͼƬ */
			if (!oldImage.equals(newImage)) {
				copyFile(oldImage, newImage);
			}
		}
	}
	
	/**
	 * 变换图像大小
	 * @param oldImage: 原图
	 * @param newImage：新图
	 * @param maxWidth:最大宽度
	 * @param quality:画质
	 * @throws Exception
	 * @author zhaochunjiao
	 */
	public static void resizeForWidth(File oldImage, File newImage, int maxWidth, float quality)
			throws Exception {
		if (quality < 0 || quality > 1) {
			quality = 0.9f;
		}

		String imagePath = oldImage.getCanonicalPath();
		/* 读取原图 */
		BufferedImage biImage = ImageIO.read(oldImage);
		int imageWidth = biImage.getWidth();
		int imageHeight = biImage.getHeight();
		
		if (imageWidth > maxWidth) {
			/* 判断图片是否为JPG格式，如果不是，则转换为JPG格式 */
			String extName = getImageType(oldImage);
			if (!extName.equals(IMAGE_TYPE_JPG)) {
				String tmpFileName = imagePath + "." + extName;
				File fileTemp = new File(tmpFileName);
				oldImage.renameTo(fileTemp);
				String jpgFileName = imagePath + "." + IMAGE_TYPE_JPG;
				File fileJpg = new File(jpgFileName);
				if (fileJpg.exists() == false) {
					try {
						toJPG(tmpFileName, jpgFileName, 100);
					} catch(Exception e) {
						fileTemp.delete();
						fileJpg.delete();
						throw new BaseException(e.getMessage());
					}
				}
				fileTemp.delete();
				fileJpg.renameTo(oldImage);
			}
			
			ImageIcon imageIcon = new ImageIcon(imagePath);
			Image image = imageIcon.getImage();

			/*设置新图片的宽度 */
			int	newWidth = maxWidth;  
			int	newHeight = (maxWidth * imageHeight) / imageWidth;
//			if (imageWidth > imageHeight) {
//				newWidth = maxWidth;
//				newHeight = (maxWidth * imageHeight) / imageWidth;
//			} else {
//				newWidth = (maxWidth * imageWidth) / imageHeight;
//				newHeight = maxWidth;
//			}
			
			Image resizedImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

			Image tempImage = new ImageIcon(resizedImage).getImage();
			BufferedImage bufferedImage = new BufferedImage(tempImage.getWidth(null), tempImage
					.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics graphics = bufferedImage.createGraphics();

			/* Clear background and paint the image. */
			graphics.setColor(Color.white);
			graphics.fillRect(0, 0, tempImage.getWidth(null), tempImage.getHeight(null));
			graphics.drawImage(tempImage, 0, 0, null);
			graphics.dispose();

			FileOutputStream fo = new FileOutputStream(newImage);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fo);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufferedImage);
			param.setQuality(quality, true);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(bufferedImage);
			fo.close();
		} else {
			if (!oldImage.equals(newImage)) {
				copyFile(oldImage, newImage);
			}
		}
	}

//	/**
//	 * �õ�ͼƬ��Ϣ
//	 * 
//	 * @param imageFile:
//	 *            ͼƬ��
//	 * @param imageFile:
//	 *            ����ͼ��
//	 * @return 
//	 * @throws Exception
//	 */
//	public static com.bama.imagerepository.Image getImageInfomation(File fileImage, File fileThumbnail) throws Exception {
//		com.bama.imagerepository.Image image = new com.bama.imagerepository.Image();
//		BufferedImage bufferImage;
//
//		/* �õ���ͼƬ��Ϣ */
//		bufferImage = ImageIO.read(fileImage);
//		image.setHeight(bufferImage.getHeight());
//		image.setWidth(bufferImage.getWidth());
//		image.setSize((int) (fileImage.length() / 1024));
//
//		/* �õ�����ͼ��Ϣ */
//		bufferImage = ImageIO.read(fileThumbnail);
//		image.setThumbnailsHeight(bufferImage.getHeight());
//		image.setThumbnailsWidth(bufferImage.getWidth());
//		image.setThumbnailsSize((int) (fileThumbnail.length() / 1024));
//
//		return image;
//	}

	/**
	 * ����ͼƬ
	 * 
	 * @param fileOld:
	 *            ͼƬԭ·��
	 * @param fileNew:
	 *            ͼƬ��·��
	 * @throws java.io.IOException
	 */
	public static void copyFile(File fileOld, File fileNew) throws Exception {
		if (fileOld.equals(fileNew)) return;

		int byteSum = 0;
		int byteRead = 0;
		if (fileOld.exists()) { 
			InputStream inStream = new FileInputStream(fileOld); // ����ԭ�ļ�
			FileOutputStream outStream = new FileOutputStream(fileNew);
			byte[] buffer = new byte[1024];
			while ((byteRead = inStream.read(buffer)) != -1) {
				byteSum += byteRead; // �ֽ��� �ļ���С
				outStream.write(buffer, 0, byteRead);
			}
			inStream.close();
			outStream.close();
		}
	}
	
	
	private static void copyFile2(String srcFilename, String dstFilename) throws Exception {
		File srcFile = new File(srcFilename);
		if (!srcFile.exists() || !srcFile.canRead()) {
			throw new Exception("ImgUtil.copyFile: file[" + srcFilename + "] 不存在或不可读");
		}

		assertDirectory(dstFilename);
		File dstFile = new File(dstFilename);
		if (dstFile.exists()) {
			if (!dstFile.isFile()) {
				throw new Exception("ImgUtil.copyFile: file[" + dstFilename + "] 不存在");
			}
			boolean b = dstFile.delete();
			if (!b) {
				throw new Exception("ImgUtil.copyFile: file[" + dstFilename + "] 不存在");
			}
		}

		FileInputStream is = null;
		FileOutputStream os = null;
		try {
			is = new FileInputStream(srcFilename);
			os = new FileOutputStream(dstFilename);
			int buffLength = 5 * 1024 * 1024;
			byte[] buff = new byte[buffLength];
			int readLength = 0;
			while (true) {
				readLength = is.read(buff, 0, buffLength);
				if (readLength == -1) {
					break;
				} else {
					os.write(buff, 0, readLength);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			is.close();
			os.close();
		}
	}

//	public static void toJPG(String source, String destination, int quality) throws Exception {
//		// Ҫ�� 0 < quality < 100������qualityֵ��Ϊ75
//		if (quality < 0 || quality > 100 || (quality + "") == null || (quality + "").equals("")) {
//			quality = 100;
//		}
//
//		JimiWriter writer;
//		ImageProducer image;
//		try {
//			JPGOptions options = new JPGOptions();
//			options.setQuality(quality);
//			image = Jimi.getImageProducer(source);
//
//			/* �ж��Ƿ�ͼƬ�������ĳЩͼƬ����Ϊ�����Jimi������޷���ȡ */
//			String imageMessage = image.toString();
//			if (imageMessage.indexOf("ErrorImageProducer") > -1) {
//				throw new BaseException("ͼƬ��ʽ����ȷ");
//			}
//
//			writer = Jimi.createJimiWriter(destination);
//			writer.setSource(image);
//			/* �����������ã��Ǳ�Ҫ */
//			writer.setOptions(options);
//			writer.putImage(destination);
//		} catch (Exception e) {
//			throw new BaseException("ͼƬ��ʽ����ȷ");
//		}
//	}

    public static void toJPG(String src, String dst, int quality) throws Exception {
        float q = 0;
        if (quality < 0 || quality > 100) {
            q = 1;
        } else {
            q = quality / 100;
        }

        ImageWriter writer = null;
        FileImageOutputStream output = null;
        try {
            BufferedImage bi = ImageIO.read(new File(src));
            writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(q);
            File file = new File(dst);
            output = new FileImageOutputStream(file);
            writer.setOutput(output);

            BufferedImage copy = new BufferedImage(bi.getWidth(null), bi.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = copy.createGraphics();
            g.drawImage(bi, 0, 0, copy.getWidth(), copy.getHeight(), Color.white, null);
            IIOImage image = new IIOImage(copy, null, null);
            writer.write(null, image, param);
        } catch (Exception e) {
            if (writer != null) {
                writer.dispose();
            }
            if (output != null) {
                output.close();
            }
        }
    }

	/**
	 * 获得文件后缀类型
	 * @param file:文件
	 * @throws java.io.IOException
	 * @return 后缀类型
	 */
	public static String getImageType(File file) throws IOException {
		byte[] content = null;
		String strFileExtendName = null;

		FileInputStream fin = new FileInputStream(file);
		content = new byte[6];
		fin.read(content);
		fin.close();
		fin = null;

		if ((content[0] == 71) && (content[1] == 73) && (content[2] == 70) && (content[3] == 56)
				&& ((content[4] == 55) || (content[4] == 57)) && (content[5] == 97)) {
			strFileExtendName = IMAGE_TYPE_GIF;
		} else if ((content[0] == -1) && (content[1] == -40) && (content[2] == -1)) {
			strFileExtendName = IMAGE_TYPE_JPG;
		} else if ((content[0] == 66) && (content[1] == 77)) {
			strFileExtendName = IMAGE_TYPE_BMP;
		} else if ((content[1] == 80) && (content[2] == 78) && (content[3] == 71)) {
			strFileExtendName = IMAGE_TYPE_PNG;
		} else if ((content[0] == 67) && (content[1] == 87) && (content[2] == 83)
				|| (content[0] == 70) && (content[1] == 87) && (content[2] == 83)) {
			strFileExtendName = IMAGE_TYPE_SWF;
		}

		return strFileExtendName;
	}

	/**
	 * ���ͼƬ
	 * 
	 * @param image
	 * @param extName
	 * @param rect:
	 *            ��õ�λ��
	 */
	public static void cutImage(File image, String extName, Rectangle rect)
			throws IOException {
		BufferedImage scrBuffer = ImageIO.read(image);
		BufferedImage desBuffer = scrBuffer.getSubimage(rect.x, rect.y, rect.width, rect.height);
		ImageIO.write(desBuffer, extName, image);
	}
	
	public static void mergeImages(String source1, String source2, String des, String type, int x, int y, int width, int height) throws Exception {
		BufferedImage img1 = ImageIO.read(new File(source1));
		BufferedImage img2 = ImageIO.read(new File(source2));
		Graphics g = img1.getGraphics();
		g.drawImage(img2, x, y, width, height, null);
		g.dispose();
		ImageIO.write(img1, type, new File(des));  
	}

	
	public static String getSavePath() {
		return savePath;
	}

	public static void setSavePath(String savePath) {
		ImgUtil.savePath = savePath;
	}

	public static String getImagePathBase() {
		return imagePathBase;
	}

	public static void setImagePathBase(String imagePathBase) {
		ImgUtil.imagePathBase = imagePathBase;
	}
}
