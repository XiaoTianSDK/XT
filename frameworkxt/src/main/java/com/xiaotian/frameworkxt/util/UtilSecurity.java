package com.xiaotian.frameworkxt.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import it.sauronsoftware.base64.Base64;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilBase64
 * @description Base 64 转码器
 * @date 2014-1-23
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilSecurity {
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private UtilRC4Encrypt rc4;
	private UtilRSAEncrypt ras;

	public byte[] encodeByteToBase64Byte(byte[] byteData, int... start) {
		if (start.length > 0) {
			return Base64.encode(byteData, start[0]);
		} else {
			return Base64.encode(byteData);
		}
	}

	// RC4编码 明文的String字符串 -> Base64 -> RC4 String [编码]
	public String encryptUTF8Base64RC4(String RC4Key, String encodeData) {
		return getUtilRC4(RC4Key).encrypt(Base64.encode(encodeData, "UTF-8"));
	}

	// RC4解码 RC4的字符串 -> 解密 -> Base64 -> String [解码]
	public String decryptUTF8RC4Base64(String RC4Key, String decodeData) {
		return Base64.decode(getUtilRC4(RC4Key).decrypt(decodeData), "UTF-8");
	}

	// 标准RSA编码 明文的String(UTF8) -> Base 64 -> RSA [编码]
	public String encryptUTF8Base64RSA(String modulus, String publicExponent, String encodeData) {
		// 公匙编码[modulus:系数,publicExponent:公匙指数]
		try {
			return getUtilRSA(modulus).encryptByPublicKey(Base64.encode(encodeData, "UTF-8"), getUtilRSA(modulus).initPublicKey(publicExponent));
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 标准RSA解码 编码的String -> RSA -> Base 64 ->String(UTF8) [解码]
	public String decryptUTF8RSABase64(String modulus, String privateExponent, String decodeData) {
		// 私匙解码[modulus:系数,privateExponent:私匙指数]
		try {
			return Base64.decode(getUtilRSA(modulus).decryptByPrivateKey(decodeData, getUtilRSA(modulus).initPrivateKey(privateExponent)), "UTF-8");
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected UtilRC4Encrypt getUtilRC4(String key) {
		if (rc4 != null) return rc4;
		return rc4 = new UtilRC4Encrypt(key);
	}

	protected UtilRSAEncrypt getUtilRSA(String modulus) {
		if (ras != null && ras.getModulus().equals(modulus)) return ras;
		return ras = new UtilRSAEncrypt(modulus);
	}

	/********************************** Public Static Method **********************************/
	// String -> Base64 String
	public static String encodeUTF8ToBase64(String codeUTF8Data) {
		// 源为UTF-8码进行Base64编码
		return Base64.encode(codeUTF8Data, "UTF-8");
	}

	// Base64 String - > String
	public static String decodeBase64ToUTF8(String codeBase64Data) {
		// 源为Base64解码为UTF-8
		return Base64.decode(codeBase64Data, "UTF-8");
	}

	// 获取十六进制的MD5码
	public static String getMD5(String data) {
		return getMD5(data.getBytes());
	}

	// 获取十六进制的MD5码
	public static String getMD5(byte[] data) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(data);
			return toHexString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 获取十六进制的SHA码
	public static String getSHA(String data) {
		return getSHA(data.getBytes());
	}

	// 获取十六进制的SHA码
	public static String getSHA(byte[] data) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(data);
			return toHexString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toHexString(byte[] array) {
		StringBuilder sb = new StringBuilder(array.length * 2);
		for (int i = 0; i < array.length; i++) {
			sb.append(HEX_DIGITS[(array[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[array[i] & 0x0f]);
		}
		return sb.toString();
	}
}
