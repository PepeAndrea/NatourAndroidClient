package com.exam.natour.Model.PathDetailResponse;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class PathDetail {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("difficulty")
    @Expose
    private String difficulty;
    @SerializedName("disability")
    @Expose
    private Integer disability;
    @SerializedName("length")
    @Expose
    private Double length;
    @SerializedName("duration")
    @Expose
    private Long duration;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("coordinates")
    @Expose
    private List<Coordinate> coordinates = null;
    @SerializedName("interest_points")
    @Expose
    private List<InterestPoint> interestPoints = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getDisability() {
        return disability;
    }

    public void setDisability(Integer disability) {
        this.disability = disability;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public List<InterestPoint> getInterestPoints() {
        return interestPoints;
    }

    public void setInterestPoints(List<InterestPoint> interestPoints) {
        this.interestPoints = interestPoints;
    }

    public void calculateLength(){

        Double totalDistance = 0.0;
        double cosAng,ang,dist;

        for(int i = 1; i < this.getCoordinates().size(); i++) {
            totalDistance += this.twoPointDistance(
                    Double.valueOf(this.getCoordinates().get(i-1).getLatitude()),
                    Double.valueOf(this.getCoordinates().get(i-1).getLongitude()),
                    Double.valueOf(this.getCoordinates().get(i).getLatitude()),
                    Double.valueOf(this.getCoordinates().get(i).getLongitude()));
        }

        Log.i("Distanza", "calculateLength:"+totalDistance);
        this.setLength(((double) totalDistance.intValue())/1000);

    }

    private Double twoPointDistance(Double lat1,Double lng1,Double lat2,Double lng2){
        Double radius = 6371e3; // metres
        Double p1 = lat1 * Math.PI/180; // φ, λ in radians
        Double p2 = lat2 * Math.PI/180;
        Double dLat = (lat2-lat1) * Math.PI/180;
        Double dLng = (lng2-lng1) * Math.PI/180;

        Double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(p1) * Math.cos(p2) *
                                Math.sin(dLng/2) * Math.sin(dLng/2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        Double d = radius * c;
        return d;
    }

}
