package com.algonquincollege.anto0084.doorsopenottawa;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.algonquincollege.anto0084.doorsopenottawa.model.Building;

/**
 * Created by rayantonenko on 2016-12-13.
 */

public class EditBuildingActivity extends FragmentActivity{


    public String building_address;
    public String building_description;
    public Button SubmitBuilding;
    public Integer building_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_building_activity);



        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            building_id = bundle.getInt("building_id");
        }



        SubmitBuilding = (Button) findViewById(R.id.submit_new_building);

        SubmitBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Building editBuilding = new Building();

                editBuilding.setAddress(((EditText) findViewById(R.id.edit_building_address)).getText().toString());
                editBuilding.setDescription(((EditText) findViewById(R.id.edit_building_description)).getText().toString());

                RequestPackage pkg = new RequestPackage();
                pkg.setMethod(HttpMethod.PUT);

                pkg.setUri("http://doors-open-ottawa-hurdleg.mybluemix.net/buildings/" + building_id);
                pkg.setParam("address", editBuilding.getAddress());
                pkg.setParam("description", editBuilding.getDescription());

                EditBuildingActivity.DoTask postTask = new EditBuildingActivity.DoTask();
                postTask.execute(pkg);
            }
        });

    }

    private class DoTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
//            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            String content = HttpManager.getData(params[0], "anto0084", "password");
            return content;
        }


        @Override
        protected void onPostExecute(String result) {

//            pb.setVisibility(View.INVISIBLE);


            if (result == null) {
                Toast.makeText(EditBuildingActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            } else {

                Log.i("RESULT", result);

            }
        }

    }
}
