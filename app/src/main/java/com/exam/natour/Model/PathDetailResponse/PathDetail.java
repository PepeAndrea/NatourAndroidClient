package com.exam.natour.Model.PathDetailResponse;

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
    private String duration;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
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
            cosAng = (Math.cos(Double.valueOf(this.getCoordinates().get(i-1).getLatitude())) * Math.cos(Double.valueOf(this.getCoordinates().get(i).getLatitude())) * Math.cos(Double.valueOf(this.getCoordinates().get(i).getLongitude())-Double.valueOf(this.getCoordinates().get(i-1).getLongitude()))) + (Math.sin(Double.valueOf(this.getCoordinates().get(i-1).getLatitude())) * Math.sin(Double.valueOf(this.getCoordinates().get(i).getLatitude())));
            ang = Math.acos(cosAng);
            dist = ang * 6371f;
            totalDistance += dist;
        }

        this.setLength(Double.valueOf(new DecimalFormat("000.00").format(totalDistance).replace(",",".")));

    }

}
