package com.xiaotian.frameworkxt.util;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;

/**
 * @version 1.0.0
 * @author Administrator
 * @name UtilRSAEncrypt
 * @description RAS 编码/解码器
 * @date 2015-5-7
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilRSAEncrypt {
	// 标准RSA
	public static final String RSA_ALGORITHM_STANDARD = "RSA";
	// ECB(Encryption block),PKC1Padding(Package V1.5的填充模式) 的 RSA(随机数填充包装模式,数据块+随机数封装模式)
	// 如果可以则采用,数据块的封装模式/填充数据包模式(随机数填充[解码时剔除随机数])
	public static final String RSA_ALGORITHM_ECB_PACKAGE = "RSA/ECB/PKCS1Padding";
	//
	public static final String MAP_KEY_PUBLICKEY = "publicKey";
	public static final String MAP_KEY_PRIVATEKEY = "privateKey";
	private String modulus; // 加密的模数/系数

	// 系统随机生成公钥和私钥
	public static HashMap<String, RSAKey> createRandomKeys() throws NoSuchAlgorithmException {
		HashMap<String, RSAKey> map = new HashMap<String, RSAKey>();
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA_ALGORITHM_STANDARD);
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		map.put(MAP_KEY_PUBLICKEY, publicKey);
		map.put(MAP_KEY_PRIVATEKEY, privateKey);
		return map;
	}

	// 构造器
	public UtilRSAEncrypt(String modules) {
		this.modulus = modules;
	}

	// 初始化公匙
	public RSAPublicKey initPublicKey(String exponent) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_STANDARD);
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(new BigInteger(modulus, 16), new BigInteger(exponent, 16));
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 初始化私匙
	public RSAPrivateKey initPrivateKey(String exponent) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_STANDARD);
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(new BigInteger(modulus, 16), new BigInteger(exponent, 16));
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 公钥编码
	public String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
		return encryptByPublicKey(RSA_ALGORITHM_STANDARD, data, publicKey);
	}

	public String encryptByPublicKey(String rsaAlogrithm, String data, RSAPublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance(rsaAlogrithm);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		// 模长  
		int key_len = publicKey.getModulus().bitLength() / 8;
		// 加密数据长度 <= 模长-11
		String[] datas = splitString(data, key_len - 11);
		StringBuffer mi = new StringBuffer();
		//如果明文长度大于模长-11则要分组加密  
		for (String s : datas) {
			mi.append(bcd2Str(cipher.doFinal(s.getBytes())));
		}
		return mi.toString();
	}

	// 私钥解码
	public String decryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
		return decryptByPrivateKey(RSA_ALGORITHM_STANDARD, data, privateKey);
	}

	public String decryptByPrivateKey(String rsaAlogrithm, String data, RSAPrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance(RSA_ALGORITHM_STANDARD);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		//模长  
		int key_len = privateKey.getModulus().bitLength() / 8;
		byte[] bytes = data.getBytes();
		byte[] bcd = asciiToBCD(bytes, bytes.length);
		//如果密文长度大于模长则要分组解密  
		StringBuffer ming = new StringBuffer();
		byte[][] arrays = splitArray(bcd, key_len);
		for (byte[] arr : arrays) {
			ming.append(new String(cipher.doFinal(arr)));
		}
		return ming.toString();
	}

	// 模数
	public String getModulus() {
		return modulus;
	}

	public void setModulus(String modulus) {
		this.modulus = modulus;
	}

	// ASCII码转BCD码
	private byte[] asciiToBCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = ascToBCD(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : ascToBCD(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}

	private byte ascToBCD(byte asc) {
		byte bcd;
		if ((asc >= '0') && (asc <= '9')) {
			bcd = (byte) (asc - '0');
		} else if ((asc >= 'A') && (asc <= 'F')) {
			bcd = (byte) (asc - 'A' + 10);
		} else if ((asc >= 'a') && (asc <= 'f')) {
			bcd = (byte) (asc - 'a' + 10);
		} else {
			bcd = (byte) (asc - 48);
		}
		return bcd;
	}

	// BCD转字符串
	private String bcd2Str(byte[] bytes) {
		char temp[] = new char[bytes.length * 2], val;
		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}

	// 拆分字符串
	private String[] splitString(String string, int len) {
		int x = string.length() / len;
		int y = string.length() % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		String[] strings = new String[x + z];
		String str = "";
		for (int i = 0; i < x + z; i++) {
			if (i == x + z - 1 && y != 0) {
				str = string.substring(i * len, i * len + y);
			} else {
				str = string.substring(i * len, i * len + len);
			}
			strings[i] = str;
		}
		return strings;
	}

	// 拆分数组
	private byte[][] splitArray(byte[] data, int len) {
		int x = data.length / len;
		int y = data.length % len;
		int z = 0;
		if (y != 0) z = 1;
		byte[][] arrays = new byte[x + z][];
		byte[] arr;
		for (int i = 0; i < x + z; i++) {
			arr = new byte[len];
			if (i == x + z - 1 && y != 0) {
				System.arraycopy(data, i * len, arr, 0, y);
			} else {
				System.arraycopy(data, i * len, arr, 0, len);
			}
			arrays[i] = arr;
		}
		return arrays;
	}
}
