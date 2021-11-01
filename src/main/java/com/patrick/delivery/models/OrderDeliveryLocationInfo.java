package com.patrick.delivery.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author patrick on 6/25/20
 * @project sprintel-delivery
 */
public class OrderDeliveryLocationInfo {

    @JsonProperty("address_components")

    private List<AddressComponent> addressComponents = null;
    @JsonProperty("adr_address")

    private String adrAddress;
    @JsonProperty("formatted_address")

    private String formattedAddress;
    @JsonProperty("geometry")

    private Geometry geometry;
    @JsonProperty("icon")

    private String icon;
    @JsonProperty("id")

    private String id;
    @JsonProperty("name")

    private String name;
    @JsonProperty("place_id")

    private String placeId;
    @JsonProperty("reference")

    private String reference;
    @JsonProperty("scope")

    private String scope;
    @JsonProperty("types")

    private List<String> types = null;
    @JsonProperty("url")

    private String url;
    @JsonProperty("utc_offset")

    private Integer utcOffset;
    @JsonProperty("vicinity")

    private String vicinity;
    @JsonProperty("html_attributions")

    private List<Object> htmlAttributions = null;
    @JsonProperty("utc_offset_minutes")
    private Integer utcOffsetMinutes;

    @JsonProperty("photos")
    private Object photos;

    public static class Geometry {

        @JsonProperty("location")
        private Location location;

        @JsonProperty("viewport")
        @JsonIgnore
        private Viewport viewport;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Viewport getViewport() {
            return viewport;
        }

        public void setViewport(Viewport viewport) {
            this.viewport = viewport;
        }
    }

    public static class Location {
        @JsonProperty("lat")
        private Double lat;
        @JsonProperty("lng")
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }
    }

    public static class AddressComponent {
        @JsonProperty("long_name")
        private String longName;
        @JsonProperty("short_name")
        private String shortName;
        @JsonProperty("types")
        private List<String> types = null;

        public String getLongName() {
            return longName;
        }

        public void setLongName(String longName) {
            this.longName = longName;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }
    }

    public static class Viewport {

        @JsonProperty("south")
        private Double south;
        @JsonProperty("west")

        private Double west;
        @JsonProperty("north")

        private Double north;
        @JsonProperty("east")

        private Double east;

        public Double getSouth() {
            return south;
        }

        public void setSouth(Double south) {
            this.south = south;
        }

        public Double getWest() {
            return west;
        }

        public void setWest(Double west) {
            this.west = west;
        }

        public Double getNorth() {
            return north;
        }

        public void setNorth(Double north) {
            this.north = north;
        }

        public Double getEast() {
            return east;
        }

        public void setEast(Double east) {
            this.east = east;
        }
    }

    public List<AddressComponent> getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(List<AddressComponent> addressComponents) {
        this.addressComponents = addressComponents;
    }

    public String getAdrAddress() {
        return adrAddress;
    }

    public void setAdrAddress(String adrAddress) {
        this.adrAddress = adrAddress;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(Integer utcOffset) {
        this.utcOffset = utcOffset;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public Integer getUtcOffsetMinutes() {
        return utcOffsetMinutes;
    }

    public void setUtcOffsetMinutes(Integer utcOffsetMinutes) {
        this.utcOffsetMinutes = utcOffsetMinutes;
    }

    public Object getPhotos() {
        return photos;
    }

    public void setPhotos(Object photos) {
        this.photos = photos;
    }

    @Override
    public String toString() {
        return "LocationData{" +
                "address_components=" + addressComponents +
                ", adrAddress='" + adrAddress + '\'' +
                ", formattedAddress='" + formattedAddress + '\'' +
                ", geometry=" + geometry +
                ", icon='" + icon + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", placeId='" + placeId + '\'' +
                ", reference='" + reference + '\'' +
                ", scope='" + scope + '\'' +
                ", types=" + types +
                ", url='" + url + '\'' +
                ", utcOffset=" + utcOffset +
                ", vicinity='" + vicinity + '\'' +
                ", htmlAttributions=" + htmlAttributions +
                ", utcOffsetMinutes=" + utcOffsetMinutes +
                '}';
    }
}
