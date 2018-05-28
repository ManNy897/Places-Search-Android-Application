package com.example.manny.myapplication;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Results {

    private List<Result> results;
    private String next_page_token;

    public Results(List<Result> results){
        this.results = results;
    }

    public List<Result> getResults(int pageNum){
        int endIndex = 20*pageNum + 20;
        return new ArrayList<Result>(this.results.subList(20*pageNum, ((endIndex > this.results.size()) ? this.results.size() : endIndex)));
    }

    public List<Result> getResults(){
        return this.results;
    }

    public String getNextPage(){
        return this.next_page_token;
    }

    public void addToList(List<Result> newList){
        this.results.addAll(newList);
    }

    public int getSize(){
        return this.results.size();
    }


    public static class Result {
        @SerializedName("name")
        private String title;
        @SerializedName("vicinity")
        private String address;
        @SerializedName("icon")
        private String img;
        @SerializedName("place_id")
        private String id;

        public Result(String title, String address, String img, String id) {
            this.title = title;
            this.address = address;
            this.img = img;
            this.id = id;
        }

        public String getAddress() {
            return address;
        }

        public String getImg() {
            return img;
        }

        public String getTitle() {
            return title;
        }

        public String getId(){
            return id;
        }
    }
}
