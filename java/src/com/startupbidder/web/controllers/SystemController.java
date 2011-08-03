package com.startupbidder.web.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.startupbidder.vo.SystemPropertyVO;
import com.startupbidder.web.HttpHeaders;
import com.startupbidder.web.HttpHeadersImpl;
import com.startupbidder.web.ModelDrivenController;
import com.startupbidder.web.ServiceFacade;

public class SystemController extends ModelDrivenController {
	private Object model;

	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			if("set-property".equalsIgnoreCase(getCommand(1))) {
				return setProperty(request);
			}
		}
		return null;
	}

	private HttpHeaders setProperty(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("set-property");
		
		String name = request.getParameter("name");
		String value = request.getParameter("value");
		
		SystemPropertyVO property = new SystemPropertyVO();
		property.setName(name);
		property.setValue(value);
		model = ServiceFacade.instance().setSystemProperty(getLoggedInUser(), property);

		name = request.getParameter("name.1");
		if (!StringUtils.isEmpty(name)) {
			value = request.getParameter("value.1");
			property = new SystemPropertyVO();
			property.setName(name);
			property.setValue(value);
			ServiceFacade.instance().setSystemProperty(getLoggedInUser(), property);
		}

		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
