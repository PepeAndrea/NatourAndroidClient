package com.exam.natour.Model.PathDetailResponse;

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
    @SerializedName("difficulty_id")
    @Expose
    private String difficultyId;
    @SerializedName("disability")
    @Expose
    private Integer disability;
    @SerializedName("length")
    @Expose
    private Integer length;
    @SerializedName("duration")
    @Expose
    private Double duration;
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

    public String getDifficultyId() {
        return difficultyId;
    }

    public void setDifficultyId(String difficultyId) {
        this.difficultyId = difficultyId;
    }

    public Integer getDisability() {
        return disability;
    }

    public void setDisability(Integer disability) {
        this.disability = disability;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
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

}