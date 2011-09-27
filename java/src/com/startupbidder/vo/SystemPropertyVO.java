package com.startupbidder.vo;

import com.startupbidder.dto.SystemPropertyDTO;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
public class SystemPropertyVO extends SystemPropertyDTO {
	public SystemPropertyVO() {	
	}
	
	public SystemPropertyVO (SystemPropertyDTO property) {
		setAuthor(property.getAuthor());
		setCreated(property.getCreated());
		setKey(property.getKey());
		setName(property.getName());
		setValue(property.getValue());
	}
}
