package com.startupbidder.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import com.startupbidder.dto.NotificationDTO;
import com.startupbidder.dto.NotificationDTO.Type;

/**
 * Jackson's JSON lowercase serializer.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class NotificationTypeDeserializer extends JsonDeserializer<ArrayList<NotificationDTO.Type>>{

	@Override
	public ArrayList<Type> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken curr = jp.getCurrentToken();
        
        // Usually should just get string value:
        if (curr == JsonToken.VALUE_STRING) {
        	ArrayList<NotificationDTO.Type> list = new ArrayList<NotificationDTO.Type>();
        	// we expect comma separated list of enum names
            String values = jp.getText();
			StringTokenizer tokenizer = new StringTokenizer(values, ",");
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken().trim();
				list.add(NotificationDTO.Type.valueOf(token));
			}
            return list;
        }
        throw ctxt.mappingException(new ArrayList<NotificationDTO.Type>().getClass());
	}

}
