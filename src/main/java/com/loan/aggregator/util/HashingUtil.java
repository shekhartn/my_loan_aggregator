package com.loan.aggregator.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.amazonaws.util.Base64;
import com.loan.aggregator.model.Consumer;

public class HashingUtil {
	public static String encrypt(Consumer consumer,String passwordToHash) {
		String value=consumer.getEmail()+" "+String.valueOf(consumer.getConsumerId());
		byte[] salt=value.getBytes();
		String securePassword=getSHA256SecurePassword(passwordToHash,salt);
		return securePassword;
	}

	private static String getSHA256SecurePassword(String passwordToHash, byte[] salt) {
		String generatedPassword=null;
		try {
			MessageDigest md=MessageDigest.getInstance("SHA-256");
			md.update(salt);
			byte[] bytes=md.digest(passwordToHash.getBytes());
			generatedPassword=Base64.encodeAsString(bytes);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error in generating hashing:::"+e);
		}
		
		return generatedPassword;
	}
	
}
