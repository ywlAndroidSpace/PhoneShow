
package com.likebamboo.phoneshow.entities;

import java.io.Serializable;

public class City implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name = "";

    /**
     * 经度
     */
    private String lat = "";

    /**
     * 纬度
     */
    private String lon = "";

    public City() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "City [name=" + name + ", lat=" + lat + ", lon=" + lon + "]";
    }

}
