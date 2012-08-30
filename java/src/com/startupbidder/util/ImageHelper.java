package com.startupbidder.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class ImageHelper {
	private static final Logger log = Logger.getLogger(ImageHelper.class.getName());
	
	private final static byte[] JPEG = {(byte)0xff, (byte)0xd8 };
	private final static byte[] BMP = {(byte)0x42, (byte)0x4d };
	private final static byte[] GIF = {0x47, 0x49, 0x46, 0x38};
	private final static byte[] PNG = {(byte)0x89, 0x50, 0x4e, 0x47};
	private final static byte[] TIFF_II = {(byte)0x49, 0x49, 0x2a, 0x00};
	private final static byte[] TIFF_MM = {(byte)0x4d, 0x4d, 0x00, 0x2a};
	private final static byte[] WMF = {(byte)0x9a, (byte)0xc6, (byte)0xcd, (byte)0xd7};
	
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
		} else if (ArrayUtils.isEquals(premagicNumber, BMP)) {
			format = "image/bmp";
		} else if (ArrayUtils.isEquals(magicNumber, GIF)) {
			format = "image/gif";
		} else if (ArrayUtils.isEquals(magicNumber, PNG)) {
			format = "image/png";
		} else if (ArrayUtils.isEquals(magicNumber, TIFF_II)) {
			format = "image/tiff";
		} else if (ArrayUtils.isEquals(magicNumber, TIFF_MM)) {
			format = "image/tiff";
		} else if (ArrayUtils.isEquals(magicNumber, WMF)) {
			format = "image/wmf";
		} else {
			log.warning("Image not recognized as JPG, GIF or PNG. Magic number was: " + toHexString(magicNumber));
            throw new ImageFormatException("Image not recognized as JPG, GIF or PNG. Magic number was: " + toHexString(magicNumber));
		}
		return format;
	}
	
	public static String printStringAsHex(String text) {
		text = StringUtils.substring(text, 0, 32);
		StringBuffer buf = new StringBuffer();
		buf.append("Hex output:\n");
		for (int i = 0; i < text.length(); i++) {
			buf.append(text.charAt(i)).append("    ");
		}
		buf.append("\n");
		buf.append(toHexString(text.getBytes()));
		return buf.toString();
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
		propValue = StringUtils.lowerCase(propValue);
		if (propValue.endsWith(".gif")) {
			return "image/gif";
		} else if (propValue.endsWith(".jpg")) {
			return "image/jpeg";
		} else if (propValue.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (propValue.endsWith(".png")) {
			return "image/png";
		} else if (propValue.endsWith(".bmp")) {
			return "image/bmp";
		} else if (propValue.endsWith(".tif")) {
			return "image/tiff";
		} else if (propValue.endsWith(".tiff")) {
			return "image/tiff";
		} else if (propValue.endsWith(".wmf")) {
			return "image/wmf";
		} else if (propValue.endsWith(".doc")) {
			return "application/msword";
		} else if (propValue.endsWith(".docx")) {
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
	
	private static String fetchUrl(String url) {
		try {
			log.info("Fetching Google Plus avatar url from " + url);
			HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
			con.setInstanceFollowRedirects(false);
			IOUtils.toByteArray(con.getInputStream());
			String locationHeader = con.getHeaderField("Location");
			if (StringUtils.isNotEmpty(locationHeader)) {
				return locationHeader;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Error fetching avatar url from " + url, e);
			return null;
		}
	}

	public static String getGooglePlusAvatarUrl(String googleId, String email) {
		String avatarUrl = fetchUrl("http://profiles.google.com/s2/photos/profile/" + googleId);
		log.info("Fetched Google Plus avatar url: " + avatarUrl);
		if (avatarUrl == null && email != null) {
			String emailParts[] = StringUtils.split(email, "@");
			log.info("Trying to fetch avatar for google email address: " + emailParts[0]);
			avatarUrl = fetchUrl("http://profiles.google.com/s2/photos/profile/" + emailParts[0]);
			log.info("Fetched Google Plus avatar url: " + avatarUrl);
		}
		return avatarUrl;
	}
	
	public static String getFacebookAvatarUrl(String facebookId) {
		String avatarUrl = fetchUrl("http://graph.facebook.com/" + facebookId + "/picture");
		log.info("Fetched Facebook avatar url: " + avatarUrl);
		return avatarUrl;
	}
}
