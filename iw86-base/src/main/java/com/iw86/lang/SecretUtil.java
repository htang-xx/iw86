package com.iw86.lang;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;

import com.iw86.base.Constant;


/**
 * 加解密处理
 * @author tanghuang
 */
public class SecretUtil {
	private final static String DESMODE = "DES/CBC/PKCS5Padding";
	private final static String MD5 = "MD5";
	private final static String RSA = "RSA";
	private final static char C1 = 'a';
	private final static char C2 = 'z';
	private final static char C3 = 'A';
	private final static char C4 = 'Z';
	
	/**
	 * md5加密
	 * @param args
	 * @return
	 */
	public static String md5(String... args) {
		try {
			MessageDigest md5 = MessageDigest.getInstance(MD5);
			for (String arg : args) {
				md5.update(arg.getBytes(Constant.UTF_8));
			}
			byte[] bytes = md5.digest();
			StringBuilder md5StrBuff = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				if (Integer.toHexString(0xFF & bytes[i]).length() == 1)
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & bytes[i]));
				else
					md5StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
			}
			return md5StrBuff.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * base64移位加密
	 * @param str
	 * @param m 移位的模值，为0时表示不根据模移位
	 * @param n 移位的值，m为0时有效
	 * @return
	 */
	public static String enMove(String str, int m, int n){
		try {
			str = new String(Base64.encodeBase64(str.getBytes()));
			char[] chars = str.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				if (chars[i] >= C3 && chars[i] <= C4) {
					if(m!=0) chars[i] += i % m;
					else chars[i] += n;
					if (chars[i] > C4) {
						chars[i] = (char) (chars[i] - C4 + C3 - 1);
					} else if (chars[i] < C3) {
						chars[i] = (char) (C4 - (C3 - chars[i]) + 1);
					}
				} else if (chars[i] >= C1 && chars[i] <= C2) {
					if(m!=0) chars[i] += i % m;
					else chars[i] += n;
					if (chars[i] > C2) {
						chars[i] = (char) (chars[i] - C2 + C1 - 1);
					} else if (chars[i] < C1) {
						chars[i] = (char) (C2 - (C1 - chars[i]) + 1);
					}
				}
			}
			return new String(chars);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * base64移位解密
	 * @param str
	 * @param m 移位的模值，为0时表示不根据模移位
	 * @param n 移位的值，m为0时有效
	 * @return
	 */
	public static String deMove(String str, int m, int n) {
		try {
			char[] chars = str.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				if (chars[i] >= C3 && chars[i] <= C4) {
					if(m!=0) chars[i] -= i % m;
					else chars[i] -= n;
					if (chars[i] > C4) {
						chars[i] = (char) (chars[i] - C4 + C3 - 1);
					} else if (chars[i] < C3) {
						chars[i] = (char) (C4 - (C3 - chars[i]) + 1);
					}
				} else if (chars[i] >= C1 && chars[i] <= C2) {
					if(m!=0) chars[i] -= i % m;
					else chars[i] -= n;
					if (chars[i] > C2) {
						chars[i] = (char) (chars[i] - C2 + C1 - 1);
					} else if (chars[i] < C1) {
						chars[i] = (char) (C2 - (C1 - chars[i]) + 1);
					}
				}
			}
			return new String(Base64.decodeBase64(new String(chars).getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/** 
     * 从字符串中加载公钥
     * @param publicKeyStr 公钥数据字符串 
     * @throws Exception 加载公钥时产生的异常 
     */  
    public static RSAPublicKey rsaPublicKey(String publicKeyStr) throws Exception{  
        try {
        	byte[] buffer = Base64.decodeBase64(new String(publicKeyStr).getBytes());
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {  
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {  
            throw new Exception("公钥数据为空");
        }  
    }  
  
    public static RSAPrivateKey rsaPrivateKey(String privateKeyStr) throws Exception{  
        try {
        	byte[] buffer = Base64.decodeBase64(new String(privateKeyStr).getBytes());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {  
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {  
            throw new Exception("私钥数据为空");
        }  
    }  
  
    /** 
     * 加密过程 
     * @param key 密钥 
     * @param plainText 明文数据 
     * @return 
     * @throws Exception 加密过程中的异常信息 
     */  
    public static String rsaEncrypt(Key key, String plainText) throws Exception{  
        if(key==null) throw new Exception("加密密钥为空, 请设置");
        
        Cipher cipher= null;
        try {
            cipher= Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] output = cipher.doFinal(plainText.getBytes());
            return StringUtil.bytesToHexStr(output);
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();
            return null;
        }catch (InvalidKeyException e) {  
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {  
            throw new Exception("明文数据已损坏");
        }  
    }
  
    /** 
     * 解密过程 
     * @param key 密钥 
     * @param cipherCode 密文数据 
     * @return 明文 
     * @throws Exception 解密过程中的异常信息 
     */  
    public static String rsaDecrypt(Key key, String cipherCode) throws Exception{  
        if (key== null){  
            throw new Exception("解密密钥为空, 请设置");
        }  
        Cipher cipher= null;
        try {  
            cipher= Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] output = cipher.doFinal(StringUtil.hexStrToBytes(cipherCode));
            return new String(output);
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();
            return null;
        }catch (InvalidKeyException e) {  
            throw new Exception("解密密钥非法,请检查");
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {  
            throw new Exception("密文数据已损坏");
        }         
    }

	/**
     * 密文加密
     * @param password
     * @param key
     * @return
     */
    public final static String desEncrypt(String password,String key){
        try {
            return StringUtil.bytesToHexStr(encryptDES(password.getBytes(Constant.UTF_8), key.getBytes()));
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 密文解密
     * @param data
     * @param key
     * @return
     */
    public final static String desDecrypt(String data,String key){
        try {
            return new String(decryptDES(StringUtil.hexStrToBytes(data),key.getBytes()));
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    
	/**
	 * 加密
	 * @param src 数据源
	 * @param key 密钥，长度必须是8的倍数
	 * @return	  返回加密后的数据
	 * @throws Exception
	 */
	private static byte[] encryptDES(byte[] src, byte[] key)
		throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DESMODE);
		
		IvParameterSpec ivparam = new IvParameterSpec(key);
		
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, ivparam, sr);
		// 现在，获取数据并加密
		// 正式执行加密操作
		return cipher.doFinal(src);
	}
	
	/**
	 * 解密
	 * @param src	数据源
	 * @param key	密钥，长度必须是8的倍数
	 * @return		返回解密后的原始数据
	 * @throws Exception
	 */
	private static byte[] decryptDES(byte[] src, byte[] key)
		throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DESMODE);
		
		IvParameterSpec ivparam = new IvParameterSpec(key);
		
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, ivparam, sr);
		// 现在，获取数据并解密
		// 正式执行解密操作
		return cipher.doFinal(src);
	}
	
}