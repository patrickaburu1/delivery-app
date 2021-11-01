package com.patrick.delivery.models;

/**
 * @author patrick on 9/8/20
 * @project sprintel-delivery
 */
public class TrackOrderRes {
    private Long orderNo;
    private String orderStatus;
    private RiderLocRes riderLocation;
    private DestinationLoc destinationLoc;

    public static class RiderLocRes{
        public RiderLocRes(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        private double lat,lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    public static class DestinationLoc{
        private double lat,lng;

        public DestinationLoc(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public RiderLocRes getRiderLocation() {
        return riderLocation;
    }

    public void setRiderLocation(RiderLocRes riderLocation) {
        this.riderLocation = riderLocation;
    }

    public DestinationLoc getDestinationLoc() {
        return destinationLoc;
    }

    public void setDestinationLoc(DestinationLoc destinationLoc) {
        this.destinationLoc = destinationLoc;
    }
}
