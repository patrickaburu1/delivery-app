package com.patrick.delivery.locationEntities;

/**
 * @author patrick on 8/18/20
 * @project testmongo
 */
public class Location {
    private String type = "Point";
    private double[] coordinates;

    public Location() {
        this.type = "Point";
    }

    public Location(double[] coordinates) {
        this();
        this.coordinates = coordinates;
    }

    public Location(double lat, double lng) {
        this(new double[]{lng, lat});
    }

    public double getLat() {
        if (coordinates == null || coordinates.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Coordinates have not been set");
        }
        // Remember in Mongo we use lng, lat
        return this.coordinates[1];
    }

    public double getLng() {
        if (coordinates == null || coordinates.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Coordinates have not been set");
        }
        // Remember in Mongo we use lng, lat
        return this.coordinates[0];
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }


}
