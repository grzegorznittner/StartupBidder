package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class MonitorListVO extends BaseResultVO {
	@JsonProperty("monitors")
	private List<MonitorVO> monitors;
	@JsonProperty("monitors_props")
	private ListPropertiesVO monitorsProperties;
	@JsonProperty("profile")
	private UserVO user;
	public List<MonitorVO> getMonitors() {
		return monitors;
	}
	public void setMonitors(List<MonitorVO> monitors) {
		this.monitors = monitors;
	}
	public ListPropertiesVO getMonitorsProperties() {
		return monitorsProperties;
	}
	public void setMonitorsProperties(ListPropertiesVO monitorsProperties) {
		this.monitorsProperties = monitorsProperties;
	}
	public UserVO getUser() {
		return user;
	}
	public void setUser(UserVO user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "MonitorListVO [monitors=" + monitors + ", monitorsProperties="
				+ monitorsProperties + ", user=" + user + "]";
	}
}
