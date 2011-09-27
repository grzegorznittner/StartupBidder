package com.startupbidder.util;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Jackson's JSON date serializer.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class DateSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider)
		throws IOException,	JsonProcessingException {
		if (value == null) {
			jgen.writeString("");
		} else {
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
			jgen.writeString(fmt.print(value.getTime()));
		}
	}
}
