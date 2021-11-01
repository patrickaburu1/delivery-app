package com.patrick.delivery.models;

import java.util.List;

public class InfoBipSmsReq {
   private String from;
   private String text;
   private List<Destinations> destinations;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Destinations> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Destinations> destinations) {
        this.destinations = destinations;
    }

    public static class Destinations{
      private   String to;

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }
    }
}
