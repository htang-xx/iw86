/**
 * 
 */
package com.iw86.other;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.iw86.base.Constant;

/**
 * @author tanghuang
 *
 */
public class ZXing {

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	/**
     * 条形码编码
     * @param contents
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage enBarcode(String contents, int width, int height) {
        int codeWidth = 3 + // start guard
                (7 * 6) + // left bars
                5 + // middle guard
                (7 * 6) + // right bars
                3; // end guard
        codeWidth = Math.max(codeWidth, width);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.EAN_13, codeWidth, height, null);
            return toBufferedImage(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
 
    /**
     * 解析条形码
     * @param input
     * @return
     */
    public static String deBarcode(InputStream input) {
        BufferedImage image = null;
        Result result = null;
        try {
            image = ImageIO.read(input);
            if (image != null) {
	            LuminanceSource source = new BufferedImageLuminanceSource(image);
	            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	 
	            result = new MultiFormatReader().decode(bitmap, null);
	            return result.getText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
    /**
     * 生成二维码
     * @param contents
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage enQrcode(String contents, int width, int height) {
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        // 指定纠错等级(一般情况下L就行，如果要加logo的话，必须设为最高等级H)
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 指定编码格式
        hints.put(EncodeHintType.CHARACTER_SET, Constant.UTF_8);
        // 设置留白宽度
        hints.put(EncodeHintType.MARGIN, 1);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
            return toBufferedImage(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 添加logo至二维码
     * @param bufImg
     * @param logoFile
     */
    public static void addogo2Qrcode(BufferedImage bufImg, String logoFile){
    	try {
			//载入logo  
    		BufferedImage logoimg = ImageIO.read(new File(logoFile));
			int widthLogo = logoimg.getHeight();
			int heightLogo = logoimg.getHeight();
			int new_w = 0; 
	    	int new_h = 0;
			// 根据logo尺寸压缩比得到新图的尺寸  
	    	if (widthLogo > heightLogo) {
	    		new_w = bufImg.getWidth()/5;  
	    		new_h = (int) Math.round(heightLogo * ((float) new_w / widthLogo));  
	    	} else {
	    		new_h = bufImg.getHeight()/5;
	    		new_w = (int) Math.round(widthLogo * ((float) new_h / heightLogo));
	    	} 
			// 计算图片放置位置
			int x = (bufImg.getWidth() - new_w) / 2;
			int y = (bufImg.getHeight() - new_h) / 2;
			
			Graphics2D gs = bufImg.createGraphics();
			gs.drawImage(logoimg, x, y, new_w, new_h, null);
			gs.drawRoundRect(x, y, new_w, new_h, 15, 15);
			gs.setStroke(new BasicStroke(2));
			gs.setColor(Color.WHITE);
			gs.drawRect(x, y, new_w, new_h);
			gs.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
 
    /**
     * 解析二维码
     * @param input
     * @return
     */
    public static String deQrcode(InputStream input) {
        try {
        	BufferedImage image = ImageIO.read(input);
            if (image != null) {
	            LuminanceSource source = new BufferedImageLuminanceSource(image);
	            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            	
	            Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
	 
	            Result result = new MultiFormatReader().decode(bitmap, hints);
	            return result.getText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * @param matrix
     * @return
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
    	int width = matrix.getWidth();
    	int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
        	for (int y = 0; y < height; y++) {
        	  image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
        	}
        }
        return image;
	}
    
    /**
     * @param matrix
     * @param imgFile
     * @throws IOException
     */
    public static void writeToFile(BitMatrix matrix, File imgFile)throws IOException {
    	MatrixToImageWriter.writeToStream(matrix, "png", new FileOutputStream(imgFile));
    }
    
    /**
     * @param matrix
     * @param out
     * @throws IOException
     */
    public static void writeToFile(BitMatrix matrix, OutputStream out)throws IOException {
    	MatrixToImageWriter.writeToStream(matrix, "png", out);
    }
	
}
