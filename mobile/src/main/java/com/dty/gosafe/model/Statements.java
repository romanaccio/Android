/*
 * Copyright (c) 2016.
 * Created by David Mugisha during the DTY Program
 * Last Edited on 19/01/16 18:31
 */

package com.dty.gosafe.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Dasha on 13/01/2016.
 */
public class Statements {
    protected Date dateCreation;
    protected double latitude,longitude;
    protected int level,pulse;
    protected String trigger;

    public Statements(Date dateCreation, double latitude, double longitude, int level, int pulse, String trigger){
        this.dateCreation = dateCreation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.level = level;
        this.trigger = trigger;
        this.pulse = pulse;
    }

    public int getPulse() {

        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {

        this.level = level;
    }

    public String getTrigger() {

        return trigger;
    }

    public void setTrigger(String trigger) {

        this.trigger = trigger;
    }

    public Date getDateCreation(){

        return this.dateCreation;
    }
    public void setDateCreation(Date date){

        this.dateCreation = date;
    }
    public double getLatitude(){

        return this.latitude;
    }
    public void setLatitude(double latitude){

        this.latitude = latitude;
    }
    public double getLongitude(){

        return this.longitude;
    }
    public void setLongitude(double longitude){

        this.longitude = longitude;
    }

}
