package com.startupbidder.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.gc.android.market.api.MarketSession.Callback;
import com.gc.android.market.api.model.Market.GetImageResponse;
import com.gc.android.market.api.model.Market.ResponseContext;

public class GetImageResponseCallback implements Callback<GetImageResponse> {
	private static final Logger log = Logger.getLogger(ImageHelper.class.getName());
	
	private byte[] result;
	private String mimeType;
	private String imageId;
	
	public GetImageResponseCallback (String imageId) {
		this.imageId = imageId;
	}
	
	@Override
	public void onResult(ResponseContext context, GetImageResponse response) {
		try {
			result = response.getImageData().toByteArray();
		} catch (Exception ex) {
			log.log(Level.WARNING, "Error reading byte array for image data", ex);
			return;
		}
		try {
			mimeType = ImageHelper.checkMagicNumber(result);
		} catch (ImageHelper.ImageFormatException e) {
			mimeType = "image/jpeg";
		}
		log.info("Loaded image ");
	}

	public byte[] getResult() {
		return result;
	}

	public void setResult(byte[] result) {
		this.result = result;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getImageId() {
		return imageId;
	}
}