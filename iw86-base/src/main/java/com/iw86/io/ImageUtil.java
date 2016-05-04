/**
 * 
 */
package com.iw86.io;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 图片压缩工具类 提供的方法中可以设定生成的 缩略图片的大小尺寸、压缩尺寸的比例、图片的质量等<br>
 * 采用原生java实现，大型应用建议采用GraphicsMagick+IM4Java
 * @author tanghuang
 */
@SuppressWarnings("restriction")
public class ImageUtil {
	
	/** 
     * 图片文件读取 
     * @param srcImgPath 
     * @return 
     */  
	public static BufferedImage read(String srcImgPath) throws RuntimeException {
        BufferedImage srcImage = null;
        try {
            srcImage = ImageIO.read(new File(srcImgPath));
        } catch (IOException e) {
        	try {
	        	//解决ICC错误
	        	JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(new FileInputStream(srcImgPath));
	        	srcImage = decoder.decodeAsBufferedImage();
        	} catch (IOException e2) {
        		e.printStackTrace();
        		throw new RuntimeException("读取图片文件出错！", e2);
        	}
        }
        return srcImage;
    }
	
	/**
     * 将内存中一个图片写入目标文件(此方法的效率并不高，且不支持压缩质量)
     * @param outImgPath 目标文件，根据其后缀，来决定写入何种图片格式
     * @param bi 图片对象
     */
    public static void write(String outImgPath, BufferedImage bi) {
    	try{
    		ImageIO.write(bi, FileUtil.getTypePart(outImgPath), FileUtil.getFile(outImgPath));
    	} catch (Exception e) {
        	throw new RuntimeException(e);
        }
    }
    
    /** 
     * 将图片文件输出到指定的路径，并可设定压缩质量
     * @param outImgPath 
     * @param bufImg 
     * @param per
     */  
    public static void write(String outImgPath, BufferedImage bufImg, float per) {
        // 判断输出的文件夹路径是否存在，不存在则创建  
        File file = FileUtil.getFile(outImgPath, true);
        // 输出到文件流
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(bufImg);
            // 压缩质量
            jep.setQuality(per, true);
            encoder.encode(bufImg, jep);
            fos.close();
        } catch (Exception e) {
        	throw new RuntimeException(e);
        } finally {
        	if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {}
			}
        }
    }
  
    /** 
     * 将图片按照指定的图片尺寸、源图片质量压缩(默认质量为1) 
     * @param srcImgPath 源图片路径 
     * @param outImgPath 输出的压缩图片的路径 
     * @param new_w 压缩后的图片宽 
     * @param new_h 压缩后的图片高 
     */  
    public static void resize(String srcImgPath, String outImgPath, int new_w, int new_h) {
    	BufferedImage srcImg = read(srcImgPath);
    	write(outImgPath, resize(srcImg, new_w, new_h), 1F);
    } 
  
    /** 
     * 将图片按照指定的尺寸比例、源图片质量压缩(默认质量为1) 
     * @param srcImgPath 源图片路径 
     * @param outImgPath 输出的压缩图片的路径 
     * @param ratio 压缩后的图片尺寸比例 
     */  
    public static void resize(String srcImgPath, String outImgPath, float ratio) {
    	BufferedImage srcImg = read(srcImgPath);
    	write(outImgPath, resize(srcImg, ratio), 1F);
    }  
  
    /** 
     * 将图片按照指定长或者宽的最大值来压缩图片(默认质量为1)
     * @param srcImgPath 源图片路径 
     * @param outImgPath 输出的压缩图片的路径 
     * @param maxLength 长或者宽的最大值
     */  
    public static void resize(String srcImgPath, String outImgPath, int maxLength) {
    	BufferedImage srcImg = read(srcImgPath);
    	write(outImgPath, resize(srcImg, maxLength), 1F);
    }  
  
    /** 
     * 将图片按照指定的图片尺寸、图片质量压缩
     * @param srcImg 源图片
     * @param new_w 压缩后的图片宽 
     * @param new_h 压缩后的图片高
     */  
    public static BufferedImage resize(BufferedImage srcImg, int new_w, int new_h) {
        int old_w = srcImg.getWidth();
        // 得到源图宽  
        int old_h = srcImg.getHeight();
        // 得到源图长  
        // 根据原图的大小生成空白画布  
        BufferedImage tempImg = new BufferedImage(old_w, old_h, BufferedImage.TYPE_INT_RGB);
        // 在新的画布上生成原图的缩略图  
        Graphics2D g = tempImg.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, old_w, old_h);
        g.drawImage(srcImg, 0, 0, old_w, old_h, Color.white, null);
        g.dispose();
        BufferedImage newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
        newImg.getGraphics().drawImage(tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0, null);
        return newImg;
    }  
  
    /** 
     * 将图片按照指定的尺寸比例、图片质量压缩
     * @param srcImg 源图片路径
     * @param ratio 压缩后的图片尺寸比例
     */  
    public static BufferedImage resize(BufferedImage srcImg, float ratio) {
        int old_w = srcImg.getWidth();
        // 得到源图宽  
        int old_h = srcImg.getHeight();
        // 得到源图长  
        int new_w = 0;
        // 新图的宽  
        int new_h = 0;
        // 新图的长  
        BufferedImage tempImg = new BufferedImage(old_w, old_h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tempImg.createGraphics();
        g.setColor(Color.white);
        // 从原图上取颜色绘制新图
        g.fillRect(0, 0, old_w, old_h);
        g.drawImage(srcImg, 0, 0, old_w, old_h, Color.white, null);
        g.dispose();
        // 根据图片尺寸压缩比得到新图的尺寸
        new_w = (int) Math.round(old_w * ratio);
        new_h = (int) Math.round(old_h * ratio);
        BufferedImage newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
        newImg.getGraphics().drawImage(tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0, null);
        return newImg;
    }  
  
    /** 
     * <b>
     * 指定长或者宽的最大值来压缩图片
     * 推荐使用此方法 
     * </b>
     * @param srcImgPath 源图片路径 
     * @param maxLength 长或者宽的最大值
     */  
    public static BufferedImage resize(BufferedImage srcImg, int maxLength) {
        int old_w = srcImg.getWidth();
        // 得到源图宽  
        int old_h = srcImg.getHeight();
        // 高和宽都小于最大长度
        if(old_w < maxLength && old_h < maxLength) return srcImg;
        // 得到源图长  
        int new_w = 0;
        // 新图的宽  
        int new_h = 0;
        // 新图的长  
        BufferedImage tempImg = new BufferedImage(old_w, old_h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tempImg.createGraphics();
        g.setColor(Color.white);
        // 从原图上取颜色绘制新图  
        g.fillRect(0, 0, old_w, old_h);
        g.drawImage(srcImg, 0, 0, old_w, old_h, Color.white, null);
        g.dispose();
        // 根据图片尺寸压缩比得到新图的尺寸  
        if (old_w > old_h) {
            // 图片要缩放的比例  
            new_w = maxLength;
            new_h = (int) Math.round(old_h * ((float) maxLength / old_w));
        } else {
            new_w = (int) Math.round(old_w * ((float) maxLength / old_h));
            new_h = maxLength;
        }  
        BufferedImage newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
        newImg.getGraphics().drawImage(tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0, null);
        return newImg;
    }  
    
    /**
     * 将图片压缩成指定宽度，高度等比例缩放
     * @param srcImg
     * @param width
     */
    public static BufferedImage resizeFixedWidth(BufferedImage srcImg, int width) {
    	int old_w = srcImg.getWidth();
    	// 得到源图宽  
    	int old_h = srcImg.getHeight();
    	// 得到源图长  
    	int new_w = 0;
    	// 新图的宽  
    	int new_h = 0;
    	// 新图的长  
    	BufferedImage tempImg = new BufferedImage(old_w, old_h, BufferedImage.TYPE_INT_RGB);
    	Graphics2D g = tempImg.createGraphics();
    	g.setColor(Color.white);
    	// 从原图上取颜色绘制新图  
    	g.fillRect(0, 0, old_w, old_h);
    	g.drawImage(srcImg, 0, 0, old_w, old_h, Color.white, null);
    	g.dispose();
    	// 根据图片尺寸压缩比得到新图的尺寸  
    	if (old_w > old_h) {
    		// 图片要缩放的比例  
    		new_w = width;
    		new_h = (int) Math.round(old_h * ((float) width / old_w));
    	} else {
    		new_w = (int) Math.round(old_w * ((float) width / old_h));
    		new_h = width;
    	}  
    	BufferedImage newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
    	newImg.getGraphics().drawImage(tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0, null);
    	return newImg;
    } 
    
    /**
     * 自动缩放剪切一个图片，令其符合给定的尺寸
     * 如果图片太大，则将其缩小，如果图片太小，则将其放大，多余的部分被裁减
     * @param srcImg 图像对象
     * @param w 宽度
     * @param h 高度
     * @return 被转换后的图像
     */
    public static BufferedImage cut(BufferedImage srcImg, int w, int h) throws Exception{
        // 获得尺寸
        int oW = srcImg.getWidth();
        int oH = srcImg.getHeight();
        float oR = (float) oW / (float) oH;
        float nR = (float) w / (float) h;

        int nW, nH, x, y;
        if (oR > nR) { // 原图太宽，计算当原图与画布同高时，原图的等比宽度
            nW = (h * oW) / oH;
            nH = h;
            x = (w - nW) / 2;
            y = 0;
        } else if (oR < nR) {// 原图太长
            nW = w;
            nH = (w * oH) / oW;
            x = 0;
            y = (h - nH) / 2;
        } else { // 比例相同
            nW = w;
            nH = h;
            x = 0;
            y = 0;
        }
        // 创建图像
        BufferedImage re = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        re.createGraphics().drawImage(srcImg, x, y, nW, nH, Color.white, null);
        // 返回
        return re;
    }
    
    /**
     * 裁剪图片
     * @param srcImgPath
     * @param outImgPath
     * @param x
     * @param y
     * @param width
     * @param height
     * @throws Exception
     */
    public static void cut(String srcImgPath, String outImgPath, int x, int y, int width, int height) throws Exception {
    	BufferedImage old = read(srcImgPath);
        BufferedImage im = cut(old.getSubimage(x,y,width,height), width, height);
        write(outImgPath, im, 1F);
    }
	
	/**
	 * @param targetImg 目标文件
	 * @param pressImg 水印文件
	 * @param x
	 * @param y
	 */
	public static void press(String targetImg, String pressImg, int x, int y) {
        try {
            BufferedImage src = read(targetImg);
            int wideth = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();
            g.drawImage(src, 0, 0, wideth, height, null);
            
            BufferedImage src_biao = read(pressImg);
            int wideth_biao = src_biao.getWidth(null);
            int height_biao = src_biao.getHeight(null);
            g.drawImage(src_biao, x, y, wideth_biao, height_biao, null);
            g.dispose();
            
            //调用方法输出图片文件
            write(targetImg, image, 1F);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
     * 对一个图像进行旋转
     * @param srcImg 图像
     * @param degree 旋转角度, 90 为顺时针九十度， -90 为逆时针九十度
     * @return 旋转后得图像对象
     */
    public static BufferedImage rotate(BufferedImage srcImg, int degree) {
        int iw = srcImg.getWidth();// 原始图象的宽度
        int ih = srcImg.getHeight();// 原始图象的高度
        int w = 0;
        int h = 0;
        int x = 0;
        int y = 0;
        degree = degree % 360;
        if (degree < 0)
            degree = 360 + degree;// 将角度转换到0-360度之间
        double ang = degree * 0.0174532925;// 将角度转为弧度

        /**
         * 确定旋转后的图象的高度和宽度
         */
        if (degree == 180 || degree == 0 || degree == 360) {
            w = iw;
            h = ih;
        } else if (degree == 90 || degree == 270) {
            w = ih;
            h = iw;
        } else {
            int d = iw + ih;
            w = (int) (d * Math.abs(Math.cos(ang)));
            h = (int) (d * Math.abs(Math.sin(ang)));
        }

        x = (w / 2) - (iw / 2);// 确定原点坐标
        y = (h / 2) - (ih / 2);
        BufferedImage rotatedImage = new BufferedImage(w, h, srcImg.getType());
        Graphics2D gs = rotatedImage.createGraphics();
        gs.fillRect(0, 0, w, h);// 以给定颜色绘制旋转后图片的背景
        AffineTransform at = new AffineTransform();
        at.rotate(ang, w / 2, h / 2);// 旋转图象
        at.translate(x, y);
        AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        op.filter(srcImg, rotatedImage);
        srcImg = rotatedImage;
        return srcImg;
    }
    
}
