package com.example.manny.myapplication;

import java.util.List;

public class Details {
    Result result;
    String status;

    public Details(Result result, String status) {
        this.result = result;
        this.status = status;
    }

    public class Result {
        String formatted_address;
        String formatted_phone_number;
        String place_id;
        String name;
        float rating;
        String url;
        String website;
        String vicinity;
        int utc_offset;
        int price_level;
        List<Reviews> reviews;
        Geometry geometry;
        String icon;

        public Result(String formatted_address, String formatted_phone_number, String place_id, String name, float rating, String url, String website, String vicinity, int utc_offset, int price_level, List<Reviews> reviews, Geometry geometry, String icon) {
            this.formatted_address = formatted_address;
            this.formatted_phone_number = formatted_phone_number;
            this.place_id = place_id;
            this.name = name;
            this.rating = rating;
            this.url = url;
            this.website = website;
            this.vicinity = vicinity;
            this.utc_offset = utc_offset;
            this.price_level = price_level;
            this.reviews = reviews;
            this.geometry = geometry;
            this.icon = icon;
        }

        public class Reviews {
            String author_name;
            String author_url;
            String profile_photo_url;
            float rating;
            String text;
            long time;


            public Reviews(String author_name, String author_url, String profile_photo_url, float rating, String text, long time) {
                this.author_name = author_name;
                this.author_url = author_url;
                this.profile_photo_url = profile_photo_url;
                this.rating = rating;
                this.text = text;
                this.time = time;
            }
        }

        public class Geometry {
            Location location;

            public Geometry(Location location) {
                this.location = location;
            }

            public class Location {
                double lat;
                double lng;

                public Location(double lat, double lng) {
                    this.lat = lat;
                    this.lng = lng;
                }
            }
        }

    }
}
