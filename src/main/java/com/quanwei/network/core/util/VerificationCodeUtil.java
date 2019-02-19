package com.quanwei.network.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

/**
 * 此类包含为验证码相关方法
 */
public class VerificationCodeUtil {
    public static Logger logger = LoggerFactory.getLogger(VerificationCodeUtil.class);
    //验证码内容
    private static StringBuilder accessCode = new StringBuilder();
    
    public static StringBuilder getAccessCode() {
        return accessCode;
    }
    
    //生成随机的验证码
    public static void produceIdentifyCodeImage() {
        String code[] = {"3", "4", "5", "z", "x", "c", "v", "b", "n", "m", "a", "s", "d", "f", "g"};
        String codeType[] = {"TimesRoman", "Courier", "Arial", "宋体", "仿宋", "微软雅黑", "Monospaced ", "TimesRoman", "Courier", "Arial"};
        
        int randomNum[] = getRandomNum(4);
        
        
        if (accessCode.length() != 0) {
            accessCode.replace(0, accessCode.length(), "");
        }
        
        BufferedImage bufferedImage = new BufferedImage(130, 60, BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        graphics2D.setColor(Color.white);
        graphics2D.fillRect(0, 0, 150, 70);
        for (int i = 0; i < 4; i++) {
            graphics2D.setFont(new Font(codeType[randomNum[i]], Font.BOLD, 30));
            graphics2D.setColor(new Color(i * 60, i * 45, i * 32));
            graphics2D.drawLine(randomNum[i] * 7, randomNum[i] * 15, randomNum[i] * 160, randomNum[i] * 130);
            graphics2D.drawString(code[randomNum[i]], 10 + i * 30, 30);
            accessCode.append(code[randomNum[i]]);
        }
        graphics2D.drawRect(0, 0, 149, 69);
        try {
            ImageIO.write(bufferedImage, "JPEG", new FileOutputStream(getPath()));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //生成随机数
    private static int[] getRandomNum(int num) {
        int RandomNums[] = new int[num];
        for (int i = 0; i < num; i++) {
            int randomNum = new Random().nextInt(10);
            RandomNums[i] = randomNum;
        }
        return RandomNums;
    }
    
    
    //生成验证码并获得bufferedimage
    public static byte[] getIdentifyCodeImage() {
        VerificationCodeUtil.produceIdentifyCodeImage();
        File file = new File(getPath());
        BufferedImage image = null;
        byte[] imageByte = null;
        try {
            image = ImageIO.read(file);
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", bo);
            bo.flush();
            imageByte = bo.toByteArray();
            bo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        boolean delete = file.delete();
//        if(!delete){
//            logger.error("验证码文件未删除");
//        }
        return imageByte;
    }
    
    
    //将BufferedImage写入response.getOutputStream()
    public void out(BufferedImage bufferedImage, OutputStream os) {
        try {
            ImageIO.write(bufferedImage, "JPEG", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public static String getPath(){
        String path = System.getProperties().getProperty("user.dir");
        StringBuffer filePath = new StringBuffer();
        filePath.append(path).append(File.separator).append(".verification.jpg");
        return filePath.toString();
    }
}
