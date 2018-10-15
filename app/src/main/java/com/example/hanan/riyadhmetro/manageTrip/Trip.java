package com.example.hanan.riyadhmetro.manageTrip;

import android.widget.TextView;

public class Trip {

    private int gate;
    private int bookedSeats;
    private int availableSeats;

    private String leavingPlace;
    private String arrivingPlace;
    private String tripCode;

    private String tripDate;
    private String arrivingTime;
    private String leavingTime;

    public Trip() {
    }

    public int getGate() {
        return gate;
    }

    public int getBookedSeats() {
        return bookedSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public String getLeavingPlace() {
        return leavingPlace;
    }

    public String getArrivingPlace() {
        return arrivingPlace;
    }

    public String getTripCode() {
        return tripCode;
    }

    public String getTripDate() {
        return tripDate;
    }

    public String getArrivingTime() {
        return arrivingTime;
    }

    public String getLeavingTime() {
        return leavingTime;
    }
}
