package com.startupbidder.util;

import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;

public class ImageHelper {
	private static final Logger log = Logger.getLogger(ImageHelper.class.getName());
	
	private final static byte[] JPEG = {(byte)0xff, (byte)0xd8 };
	private final static byte[] GIF = {0x47, 0x49, 0x46, 0x38};
	private final static byte[] PNG = {(byte)0x89, 0x50, 0x4e, 0x47};
	
	@SuppressWarnings("serial")
	public static class ImageFormatException extends Exception {
		public ImageFormatException(String message) {
			super(message);
		}
	}

	public static String checkMagicNumber(byte[] logo) throws ImageFormatException {
		byte[] premagicNumber = ArrayUtils.subarray(logo, 0, 2);
		byte[] magicNumber = ArrayUtils.subarray(logo, 0, 4);
		String format = "";
		if (ArrayUtils.isEquals(premagicNumber, JPEG)) {
			format = "image/jpeg";
		} else if (ArrayUtils.isEquals(magicNumber, GIF)) {
			format = "image/gif";
		} else if (ArrayUtils.isEquals(magicNumber, PNG)) {
			format = "image/png";
		} else {
			log.warning("Image not recognized as JPG, GIF or PNG. Magic number was: " + toHexString(magicNumber));
            throw new ImageFormatException("Image not recognized as JPG, GIF or PNG. Magic number was: " + toHexString(magicNumber));
		}
		return format;
	}
	
	public static String toHexString(byte[] magicNumber) {
		StringBuffer buf = new StringBuffer();
		for (byte b : magicNumber) {
			buf.append("0x").append(Integer.toHexString((0xF0 & b) >>> 4)).append(Integer.toHexString(0x0F & b)).append(" ");
		}
		return buf.toString();
	}
	
	public static String toByteDefinition(byte[] magicNumber) {
		StringBuffer buf = new StringBuffer();
		buf.append("new byte[] {");
		int index = 0;
		for (byte b : magicNumber) {
			if ((index++) > 0) {
				buf.append(", ");
			}
			buf.append("(byte)0x").append(Integer.toHexString((0xF0 & b) >>> 4)).append(Integer.toHexString(0x0F & b)).append(" ");
		}
		buf.append("}");
		return buf.toString();
	}

	public static String getMimeTypeFromFileName(String propValue) {
		if (propValue.endsWith(".gif")) {
			return "image/gif";
		} else if (propValue.endsWith(".jpg")) {
			return "image/jpeg";
		} else if (propValue.endsWith(".png")) {
			return "image/png";
		} else if (propValue.endsWith(".doc")) {
			return "application/msword";
		} else if (propValue.endsWith(".ppt")) {
			return "application/vnd.ms-powerpoint";
		} else if (propValue.endsWith(".xls")) {
			return "application/vnd.ms-excel";
		} else if (propValue.endsWith(".pdf")) {
			return "application/pdf";
		}
		return "";
	}


}