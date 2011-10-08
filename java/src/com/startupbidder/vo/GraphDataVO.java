package com.startupbidder.vo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.startupbidder.util.DateSerializer;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class GraphDataVO implements Serializable {
	
	@JsonProperty("type")
	private String type;
	@JsonProperty("label")
	private String label;
	@JsonProperty("xaxis")
	private String xAxis;
	@JsonProperty("yaxis")
	private String yAxis;
	@JsonProperty("values")
	private int[] values;
	@JsonProperty("created")
	@JsonSerialize(using=DateSerializer.class)
	private Date created;

	public GraphDataVO(String graphType) {
		this.type = graphType;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getxAxis() {
		return xAxis;
	}
	public void setxAxis(String xAxis) {
		this.xAxis = xAxis;
	}
	public String getyAxis() {
		return yAxis;
	}
	public void setyAxis(String yAxis) {
		this.yAxis = yAxis;
	}
	public int[] getValues() {
		return values;
	}
	public void setValues(int[] values) {
		this.values = values;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	@Override
	public String toString() {
		return "GraphDataVO [type=" + type + ", label=" + label + ", xAxis="
				+ xAxis + ", yAxis=" + yAxis + ", values="
				+ Arrays.toString(values) + ", created=" + created + "]";
	}
}
