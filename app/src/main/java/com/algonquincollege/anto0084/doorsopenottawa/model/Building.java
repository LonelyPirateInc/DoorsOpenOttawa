package com.algonquincollege.anto0084.doorsopenottawa.model;

/**
 * Created by rayantonenko on 2016-11-08.
 */

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A building class.
 *
 * A Building has the following properties:
 *   buildingId
 *   name
 *   address
 *   image
 */

public class Building {


    private int buildingId;
    private String description;
    private String name;
    private String address;
    private String image;
    private Bitmap bitmap;
    private JSONArray open_hours;



    private String date;


    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address + " Ottawa, Ontario";
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getBitmap() { return bitmap; }
    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHours(JSONArray hours) {
        this.open_hours = hours;

         date = "";
        for (int i=0; i<open_hours.length(); i++){
            try {
                date += open_hours.getJSONObject(i).getString("date") + "\n";
            } catch (JSONException e) {

            }
        }
    }

    public JSONArray getHours() {
        return open_hours;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
