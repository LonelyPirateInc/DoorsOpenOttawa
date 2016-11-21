package com.algonquincollege.anto0084.doorsopenottawa.parsers;

import com.algonquincollege.anto0084.doorsopenottawa.model.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Parse a JSON object for a Building.
 *
 * @author anto0084@AlgonquinCollege.com Anton Antonenko
 *
 *
 */
public class BuildingJSONParser {

    public static List<Building> parseFeed(String content) {

        try {
            JSONObject jsonResponse = new JSONObject(content);
            JSONArray buildingArray = jsonResponse.getJSONArray("buildings");
            List<Building> buildingList = new ArrayList<>();

            for (int i = 0; i < buildingArray.length(); i++) {

                JSONObject obj = buildingArray.getJSONObject(i);
                Building building = new Building();

                building.setBuildingId( obj.getInt("buildingId"));
                building.setName( obj.getString("name"));
                building.setImage( obj.getString("image"));
                building.setAddress( obj.getString("address"));
                building.setDescription( obj.getString("description") );
                building.setHours( obj.getJSONArray("open_hours") );

                buildingList.add(building);
            }

            return buildingList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
