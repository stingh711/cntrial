package com.imedis.cntrial.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.imedis.cntrial.util.exception.BaseException;

public class CheckCode {
	static Random r = new Random();
	static String ssource = "0123456789";
	static char[] src = ssource.toCharArray();
	
	/*
	 * ������������ַ���
	 * ����length:Ϊ�ַ�ĳ���
	 */
	public synchronized static String generateRandom(int length) {
		
		char[] buf = new char[length];
		int rand;
		for(int i = 0; i<length; i++) {
			rand = Math.abs(r.nextInt())%src.length;			
			buf[i] = src[rand];
		}
		String checkcode = new String(buf);
		return checkcode; 
	}
	
	//�ڸ�ķ�Χ�ڻ�ȡRGB��ɫ
	private static Color getColor(int fc, int bc) {
		Random random = new Random();
		if (fc>255) {
			fc = 255;
		}
		if (bc>255) {
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		
		return new Color(r, g, b);
	}
	
	public synchronized static BufferedImage drawImage(String checkcode) throws BaseException {
		try{ 
		     //�ַ������
		   Font CodeFont = new Font("Arial Black",Font.BOLD,17);
		   int iLength = checkcode.length();     //�õ���֤�볤��
		   int width=25*iLength, height=25;    //ͼ������߶�
		   int CharWidth = (int)(width-16)/iLength;  //�ַ����߿��
		   int CharHeight = 16;          //�ַ���ϱ߸߶�
		   
		   // ���ڴ��д���ͼ��
		   BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		   
		   // ��ȡͼ��������
		   Graphics g = image.getGraphics();
		   
		   //��������
		   Random random = new Random();
		   
		   // �趨����ɫ
		   g.setColor(getColor(200,240));
		   g.fillRect(0, 0, width, height);
		   
		   //�趨����
		   g.setFont(CodeFont);
		   
		   //�������ɫ�ı߿�
		   g.setColor(getColor(10,50));
		   g.drawRect(0,0,width-1,height-1);
		   
		   // ������155������ߣ�ʹͼ���е���֤�벻�ױ��������̽�⵽
		   g.setColor(getColor(160,200));
		   for (int i = 0; i<155; i++)
		   {
		      int x = random.nextInt(width);
		      int y = random.nextInt(height);
		      int xl = random.nextInt(12);
		      int yl = random.nextInt(12);
		      g.drawLine(x,y,x+xl,y+yl);
		   }
		   
		 
		   for (int i=0;i<iLength;i++)
		   {
		    String rand = checkcode.substring(i,i+1);
		    // ����֤����ʾ��ͼ����
		    g.setColor(new Color(20+random.nextInt(60),20+random.nextInt(120),20+random.nextInt(180)));
		    g.drawString(rand,CharWidth*i+14,CharHeight);
		   }
		   // ͼ����Ч
		   g.dispose();
		   return image;
		  }catch(Exception e){
			  throw new BaseException(e.getMessage());
		   }
	}

}

