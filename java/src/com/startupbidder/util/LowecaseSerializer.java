package com.startupbidder.util;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 * Jackson's JSON lowercase serializer.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class LowecaseSerializer extends JsonSerializer<String>{

	@Override
	public void serialize(String value, JsonGenerator jgen,	SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeString(value.toLowerCase());
	}

}
