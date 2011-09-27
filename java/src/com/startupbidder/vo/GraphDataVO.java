package com.startupbidder.vo;

import java.util.Arrays;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class GraphDataVO {
	@JsonProperty("label")
	private String label;
	@JsonProperty("xaxis")
	private String xAxis;
	@JsonProperty("yaxis")
	private String yAxis;
	@JsonProperty("values")
	private int[] values;
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
	@Override
	public String toString() {
		return "GraphDataVO [label=" + label + ", xAxis=" + xAxis + ", yAxis="
				+ yAxis + ", values=" + Arrays.toString(values) + "]";
	}
}
