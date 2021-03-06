package com.startupbidder.util;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import com.startupbidder.datamodel.Notification;

/**
 * Jackson's JSON lowercase serializer.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class NotificationTypeSerializer extends JsonSerializer<List<Notification.Type>>{

	@Override
	public void serialize(List<Notification.Type> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		String str = value.toString();
		if (!StringUtils.isEmpty(str)) {
			jgen.writeString(str.substring(1, str.length() - 1));
		}
	}

}
