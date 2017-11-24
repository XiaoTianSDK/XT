package com.xiaotian.frameworkxt.util;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilRC4Encrypt
 * @description RC4 编码/解码 器
 * @date 2013-12-26
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class UtilRC4Encrypt {
	public String key = null;

	public UtilRC4Encrypt(String key) {
		this.key = key;
	}

	// 编码
	public String encrypt(String data) {
		if (data == null || key == null) return null;
		char[] bytes = null;
		try {
			bytes = data.toCharArray();
		} catch (Exception e) {
			return null;
		}

		bytes = encryptEx(bytes);
		return ByteToHex(bytes);
		// return new String(bytes);
	}

	public char[] encryptEx(char[] data) {
		if (data == null || key == null) return null;
		char[] output = new char[data.length];
		long i = 0;
		long j = 0;
		char[] mBox = null;
		try {
			char[] bytes = key.toCharArray();
			mBox = getKey(bytes, 256);

			for (long offset = 0; offset < data.length; offset++) {
				i = (i + 1) % mBox.length;
				j = (j + mBox[(int) i]) % mBox.length;
				char temp = mBox[(int) i];
				mBox[(int) i] = mBox[(int) j];
				mBox[(int) j] = temp;
				char a = data[(int) offset];
				char b = mBox[(mBox[(int) i] + mBox[(int) j]) % mBox.length];
				output[(int) offset] = (char) (a ^ b);
			}

		} catch (Exception e) {
			return null;
		}
		return output;
	}

	// 解码
	public String decrypt(String data) {
		try {
			if (data == null || key == null) return null;
			char[] array = decryptEx(HexToByte(data));
			// char[] array=DecryptEx(data.toCharArray());
			return new String(array);
		} catch (Exception e) {
			return null;
		}
	}

	public char[] decryptEx(char[] data) {
		return encryptEx(data);
	}

	private char[] HexToByte(String szHex) {
		try {
			int iLen = szHex.length();
			if (iLen <= 0 || 0 != iLen % 2) {
				return null;
			}
			int dwCount = iLen / 2;
			int tmp1, tmp2;
			char[] pbBuffer = new char[dwCount];
			char tempByte;
			for (int i = 0; i < dwCount; i++) {
				tempByte = szHex.charAt(i * 2);
				tmp1 = tempByte - ((tempByte >= 'A') ? 'A' - 10 : (int) '0');
				if (tmp1 >= 16) return null;
				tempByte = szHex.charAt(i * 2 + 1);
				tmp2 = tempByte - ((tempByte >= 'A') ? 'A' - 10 : (int) '0');
				if (tmp2 >= 16) return null;
				pbBuffer[i] = (char) (tmp1 * 16 + tmp2);
			}
			return pbBuffer;
		} catch (Exception e) {
			return null;
		}
	}

	// Byte To HEX
	private String ByteToHex(char[] vByte) {
		try {
			if (vByte == null || vByte.length < 1) {
				return null;
			}
			StringBuilder sb = new StringBuilder(vByte.length * 2);
			for (int i = 0; i < vByte.length; i++) {
				if (vByte[i] < 0) {
					return null;
				}
				int k = vByte[i] / 16;
				sb.append((char) (k + ((k > 9) ? 'A' - 10 : '0')));
				k = vByte[i] % 16;
				sb.append((char) (k + ((k > 9) ? 'A' - 10 : '0')));
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	// 获取指定Key
	private char[] getKey(char[] key, int kLen) {
		char[] mBox = new char[kLen];

		for (int i = 0; i < kLen; i++) {
			mBox[i] = (char) i;
		}
		int j = 0;
		char temp;
		try {
			for (int i = 0; i < kLen; i++) {
				j = (j + mBox[i] + key[i % key.length]) % kLen;
				temp = mBox[i];
				mBox[i] = mBox[j];
				mBox[j] = temp;
			}
		} catch (Exception e) {
			return null;
		}
		return mBox;
	}
}
