package com.xiaotian.framework.util;

import it.sauronsoftware.base64.Base64;

import com.xiaotian.frameworkxt.util.UtilRSAEncrypt;

public class UtilSecurity extends com.xiaotian.frameworkxt.util.UtilSecurity {

	// RSA编码,非标准RSA(数据块+随机数封装模式)
	public String encryptUTF8Base64RSAECBPackage(String modulus, String publicExponent, String encodeData) {
		try {
			return getUtilRSA(modulus).encryptByPublicKey(UtilRSAEncrypt.RSA_ALGORITHM_ECB_PACKAGE, Base64.encode(encodeData, "UTF-8"), getUtilRSA(modulus).initPublicKey(publicExponent));
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// RSA解码,非标准RSA
	public String decryptUTF8RSAECBPackageBase64(String modulus, String privateExponent, String decodeData) {
		try {
			return Base64.decode(getUtilRSA(modulus).decryptByPrivateKey(UtilRSAEncrypt.RSA_ALGORITHM_ECB_PACKAGE, decodeData, getUtilRSA(modulus).initPrivateKey(privateExponent)), "UTF-8");
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
