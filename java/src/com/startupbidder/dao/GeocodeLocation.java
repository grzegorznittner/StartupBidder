package com.startupbidder.dao;

import java.util.Random;

import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Id;

/**
* Created by IntelliJ IDEA.
* User: minlenmay
* Date: 4/29/12
* Time: 11:45 PM
* To change this template use File | Settings | File Templates.
*/
public class GeocodeLocation {
    @Id public String source_address;
    @Unindexed public String address;
    @Unindexed public String city;
    @Unindexed public String state;
    @Unindexed public String country;
    @Unindexed public Double latitude;
    @Unindexed public Double longitude;
    public GeocodeLocation() {
        this.source_address = null;
        this.address = "Threadneedle St, London EC2R, UKd";
        this.city = "London";
        this.state = null;
        this.country = "United Kingdom";
        this.latitude = 51.51406;
        this.longitude = -0.08839;
    }
    public GeocodeLocation(String source_address, String address, String city, String state, String country, Double latitude, Double longitude) {
        this.source_address = source_address;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public void randomize(double scaleFactor) {
        latitude = (latitude != null ? latitude : 51.51406) + scaleFactor * (new Random().nextDouble() - 0.5);
        longitude = (longitude != null ? longitude : -0.08839) + scaleFactor * (new Random().nextDouble() - 0.5);
    }
}
