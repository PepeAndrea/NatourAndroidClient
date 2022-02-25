package com.exam.natour.Model;

import com.exam.natour.Model.PathDetailResponse.Coordinate;
import com.exam.natour.Model.PathDetailResponse.InterestPoint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class LiveRecordingData {

    private static LiveRecordingData instance;

    private List<Coordinate> coordinates;
    private List<InterestPoint> interestPoints;
    private Instant startTime,endTime;
    private boolean isManualRecording;


    public static LiveRecordingData getInstance(){
        if(instance == null){
            instance = new LiveRecordingData();
        }
        return instance;
    }

    public LiveRecordingData() {
    }

    public void setStartTime() {
        this.startTime = Instant.now();
    }

    public void setEndTime() {
        this.endTime = Instant.now();
    }

    public void addCoordinate(Coordinate coordinate){
        if (this.coordinates == null)
            this.coordinates = new ArrayList<>();
        this.coordinates.add(coordinate);
    }

    public void addInterestPoint(InterestPoint interestPoint){
        if (this.interestPoints == null)
            this.interestPoints = new ArrayList<>();
        this.interestPoints.add(interestPoint);
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public List<InterestPoint> getInterestPoints() {
        return interestPoints;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public boolean isManualRecording() {
        return isManualRecording;
    }

    public void setManualRecording(boolean manualRecording) {
        isManualRecording = manualRecording;
    }

    public void destroy(){
        coordinates = null;
        interestPoints = null;
        startTime = null;
        endTime = null;
        isManualRecording = false;
    }
}
