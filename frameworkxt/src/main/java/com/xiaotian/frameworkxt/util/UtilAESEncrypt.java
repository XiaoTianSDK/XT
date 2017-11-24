package com.xiaotian.frameworkxt.util;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.xiaotian.frameworkxt.android.common.Mylog;

/**
 * @version 1.0.0
 * @author Administrator
 * @name UtilAESEncrypt
 * @description AES编码,解码
 * @date 2015-6-8
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilAESEncrypt {
	public static final String CHARSET_UTF8 = "UTF-8";
	// 高级加密标准（英语：Advanced Encryption Standard，缩写：AES），在密码学中又称Rijndael加密法
	// AES加密过程是在一个4×4的字节矩阵上运作，这个矩阵又称为“状态（state）”，其初值就是一个明文区块
	// AES的基本要求是，采用对称分组密码体制，密钥长度的最少支持为128、192、256，分组长度128位
	public static final String AES_ALGORITHM = "AES";
	// 标准 AES
	public static final String AES_ALGORITHM_STANDARD = "AES";
	// 如果可以则采用,采用V7版数据块的封装模式/填充数据包模式
	// [1.密钥必须是16位的,2.待加密内容的长度必须是16的倍数]
	public static final String AES_ALGORITHM_NOPACKAGING = "AES/ECB/NoPadding";
	// 如果可以则采用,采用V7版数据块的封装模式/填充数据包模式
	public static final String AES_ALGORITHM_ECB_PACKAGING_7 = "AES/ECB/PKCS7Padding";
	// 如果可以则采用,采用V5版数据块的封装模式/填充数据包模式
	public static final String AES_ALGORITHM_ECB_PACKAGING_5 = "AES/ECB/PKCS5Padding";
	// 如果可以则采用,采用V5版数据块的封装模式/填充数据包模式
	public static final String AES_ALGORITHM_CBC_PACKAGING_5 = "AES/CBC/PKCS5Padding";

	// 编码
	public String encrypt(String data, String key) {
		try {
			SecretKeySpec secretKey = new SecretKeySpec(toByteArray(key), AES_ALGORITHM);
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, AES_ALGORITHM);
			Cipher cipher = Cipher.getInstance(AES_ALGORITHM_STANDARD);
			cipher.init(Cipher.ENCRYPT_MODE, seckey);
			byte[] result = cipher.doFinal(data.getBytes(CHARSET_UTF8));
			return toHexString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 解码
	public String decrypt(String data, String key) {
		try {
			SecretKeySpec secretKey = new SecretKeySpec(toByteArray(key), AES_ALGORITHM);
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, AES_ALGORITHM);
			Cipher cipher = Cipher.getInstance(AES_ALGORITHM_STANDARD);
			cipher.init(Cipher.DECRYPT_MODE, seckey);
			byte[] result = cipher.doFinal(toByteArray(data));
			return new String(result, CHARSET_UTF8);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// CBC 随机数据打包
	public String encryptCBC(String data, String key, String algolithm) {
		try {
			/* Derive the key, given password and salt. */
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec("password".toCharArray(), toByteArray("01c5bb0260a135161879783bdd562c48a81bb2a7f2dcd0f6"), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			/* Encrypt the message. */
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			byte[] ciphertext = cipher.doFinal("Hello, World!".getBytes("UTF-8"));

			/* Decrypt the message, given derived key and initialization vector. */
			Cipher cipher1 = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher1.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			String plaintext = new String(cipher1.doFinal(ciphertext), "UTF-8");
			System.out.println(plaintext);

			return toHexString(ciphertext);
		} catch (Exception e) {
			throw new RuntimeException("encrypt fail!", e);
		}
	}

	public String decryptCBC(String data, String key, String algolithm) {
		try {
			SecretKeySpec secretKey = new SecretKeySpec(toByteArray(key), AES_ALGORITHM);
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, AES_ALGORITHM);
			Cipher cipher = Cipher.getInstance(algolithm, "BC");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, seckey);// 初始化
			byte[] result = cipher.doFinal(data.getBytes(CHARSET_UTF8));
			return toHexString(result); // 加密
		} catch (Exception e) {
			throw new RuntimeException("decrypt fail!", e);
		}
	}

	// 生成随机Key
	public String genarateRandomKey() {
		// 默认192
		KeyGenerator keygen = null;
		try {
			keygen = KeyGenerator.getInstance(AES_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("genarateRandomKey fail!", e);
		}
		SecureRandom random = new SecureRandom();
		keygen.init(random);
		Key key = keygen.generateKey();
		return toHexString(key.getEncoded());
	}

	public String genarateRandomKey(int keyLength) {
		// [密匙长度必须为: 128、192、256]
		KeyGenerator keygen = null;
		try {
			keygen = KeyGenerator.getInstance(AES_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("genarateRandomKey fail!", e);
		}
		SecureRandom random = new SecureRandom();
		keygen.init(keyLength, random);
		Key key = keygen.generateKey();
		return toHexString(key.getEncoded());
	}

	// 根据指定参数生成密钥Key
	public String genarateRandomKey(char[] password, byte[] salt, int iterations, int keyLength) {
		// [密匙长度必须为: 128、192、256]
		SecretKeyFactory secretKeyFactory;
		try {
			secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec keySpec = new PBEKeySpec(password, salt, iterations, keyLength);
			SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			return toHexString(secretKey.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void demoCBCPackage() {
		SecureRandom rng = new SecureRandom(); // 安全随机数
		byte[] aesKeyData = new byte[256 / Byte.SIZE]; // length/8 byte
		rng.nextBytes(aesKeyData); // 生成Key,密钥
		Mylog.info("demoCBCPackage 密钥: " + toHexString(aesKeyData));
		SecretKey aesKey = new SecretKeySpec(aesKeyData, "AES"); // 密钥
		Cipher aesCBCEn; // 编码运算器
		Cipher aesCBCDe; // 解码运算器
		try {
			aesCBCEn = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] iv = new byte[aesCBCEn.getBlockSize()];
			rng.nextBytes(iv); // 生成IV
			Mylog.info("demoCBCPackage 矢量参数标准: " + toHexString(iv));
			aesCBCEn.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv)); // 矢量参数标准
			//
			aesCBCDe = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCBCDe.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
			// 编码
			String data = "小明是个天才,才只有90厘米!";
			byte[] dataEn = aesCBCEn.doFinal(data.getBytes("UTF-8"));
			Mylog.info("demoCBCPackage encrypt: " + toHexString(dataEn));
			// 解码
			byte[] dataDe = aesCBCDe.doFinal(dataEn);
			Mylog.info("demoCBCPackage decrypt: " + new String(dataDe, "UTF-8"));

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void demoECBPackage() {
		try {
			String keyStr = "ca8bbc38fdb6ea1d4deea2ee057fbb1887a7a2840747199941ee7b4362b1dcea";
			String text = "我是一个广东人,不要欺负我读的书少!";

			SecretKeySpec key = new SecretKeySpec(toByteArray(keyStr), "AES"); // 密钥[标准]

			Cipher cipherEn = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
			Cipher cipherDe = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
			cipherEn.init(Cipher.ENCRYPT_MODE, key);
			cipherDe.init(Cipher.DECRYPT_MODE, key);
			// 编码
			byte[] textBytes = text.getBytes("UTF-8");
			byte[] cipherText = new byte[cipherEn.getOutputSize(textBytes.length)];
			int ctLength = cipherEn.update(textBytes, 0, textBytes.length, cipherText, 0);
			ctLength += cipherEn.doFinal(cipherText, ctLength);
			//
			Mylog.info(toHexString(textBytes));
			Mylog.info("内容长度:" + ctLength);

			// 解码
			byte[] plainText = new byte[cipherDe.getOutputSize(ctLength)];
			int ptLength = cipherDe.update(cipherText, 0, ctLength, plainText, 0);
			ptLength += cipherDe.doFinal(plainText, ptLength);
			//
			Mylog.info(new String(plainText, "UTF-8"));
			Mylog.info("内容长度:" + ptLength);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (ShortBufferException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	// byte[] -> String
	private String toHexString(byte[] byteArray) {
		if (byteArray == null || byteArray.length < 1) throw new IllegalArgumentException("this byteArray must not be null or empty");
		final StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if ((byteArray[i] & 0xff) < 0x10) {
				//0~F前面不零
				hexString.append("0");
			}
			hexString.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return hexString.toString().toLowerCase(Locale.CHINA);
	}

	// String -> byte[]
	private byte[] toByteArray(String hexString) {
		if ("".equals(hexString)) throw new IllegalArgumentException("this hexString must not be empty");
		hexString = hexString.toLowerCase(Locale.CHINA);
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {
			//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}
}
