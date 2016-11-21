package com.algonquincollege.anto0084.doorsopenottawa;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by rayantonenko on 2016-11-17.
 */

public class DetailsActivity extends FragmentActivity implements OnMapReadyCallback {

    public TextView building_name;
    public TextView building_description;
    public TextView building_address;
    public TextView building_hours;


    private GoogleMap mMap;
    private Geocoder mGeocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        getActionBar().setDisplayHomeAsUpEnabled(true);



        building_name = (TextView) findViewById(R.id.building_name );
        building_description = (TextView) findViewById( R.id.building_description );
        building_address = (TextView) findViewById( R.id.building_address );
        building_hours = (TextView) findViewById( R.id.building_hours );

        Bundle bundle = getIntent().getExtras();

        if(bundle != null )
        {
            building_name.setText( bundle.getString("building_name") );
            building_address.setText( bundle.getString("building_address") );
            building_description.setText( bundle.getString("building_description") );
            building_hours.setText( bundle.getString("building_hours") );
        }


        mGeocoder = new Geocoder( this , Locale.CANADA);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//        final EditText userLocation = (EditText) findViewById( R.id.userLocation );
//        userLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER ) {
////                    String ul = userLocation.getText().toString();
//                    DetailsActivity.this.pin( userLocation.getText().toString() );
//                    userLocation.getText().clear();
//                    return true;
//                } else {   return false; }
//
//            }
//        });



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        pin(building_address.getText().toString());

    }

    /** Locate and pin locationName to the map. */
    private void pin( String locationName ) {
        try {
            Address address = mGeocoder.getFromLocationName(locationName, 1).get(0);
            LatLng ll = new LatLng( address.getLatitude(), address.getLongitude() );
//            mMap.clear();
            mMap.addMarker( new MarkerOptions().position(ll).title(locationName) );



            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(ll,12));
            Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
