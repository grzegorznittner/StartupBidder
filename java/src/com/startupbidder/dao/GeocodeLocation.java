package com.startupbidder.dao;

import com.googlecode.objectify.annotation.Unindexed;
import org.apache.commons.lang.math.RandomUtils;

import javax.persistence.Id;

/**
* Created by IntelliJ IDEA.
* User: minlenmay
* Date: 4/29/12
* Time: 11:45 PM
* To change this template use File | Settings | File Templates.
*/
public class GeocodeLocation {
    @Id
    public String address;
    @Unindexed
    public String city;
    @Unindexed public String state;
    @Unindexed public String country;
    @Unindexed public Double latitude;
    @Unindexed public Double longitude;
    public GeocodeLocation() {
        this.address = null;
        this.city = null;
        this.state = null;
        this.country = null;
        this.latitude = null;
        this.longitude = null;
    }
    public GeocodeLocation(String address, String city, String state, String country, Double latitude, Double longitude) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public void randomize(double scaleFactor) {
        latitude += scaleFactor * (RandomUtils.nextDouble() - 0.5);
        longitude += scaleFactor * (RandomUtils.nextDouble() - 0.5);
    }
}
