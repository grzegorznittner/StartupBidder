package com.startupbidder.util;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.datanucleus.util.StringUtils;

import com.startupbidder.dto.NotificationDTO;

/**
 * Jackson's JSON lowercase serializer.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class NotificationTypeSerializer extends JsonSerializer<List<NotificationDTO.Type>>{

	@Override
	public void serialize(List<NotificationDTO.Type> value, JsonGenerator jgen,	SerializerProvider provider) throws IOException, JsonProcessingException {
		String str = value.toString();
		if (StringUtils.notEmpty(str)) {
			jgen.writeString(str.substring(1, str.length() - 1));
		}
	}

}
