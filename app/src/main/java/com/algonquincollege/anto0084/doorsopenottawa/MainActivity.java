package com.algonquincollege.anto0084.doorsopenottawa;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.sax.StartElementListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;


import com.algonquincollege.anto0084.doorsopenottawa.model.Building;
import com.algonquincollege.anto0084.doorsopenottawa.parsers.BuildingJSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * Displaying web service data in a ListActivity.
 *
 * @author anto0084@AlgonquinCollege.com Anton Antonenko
 * @see {BuildingAdapter}
 * @see {res.layout.item_building.xml}
 */


public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    // URL to my RESTful API Service hosted on my Bluemix account.
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";

    private ProgressBar pb;
    private List<MyTask> tasks;

    private List<Building> buildingList;


    ListView lv;
    SearchView sv;
    ArrayAdapter<Building> adapter;

    //
//    ListView mListView;
//    SwipeRefreshLayout mSwipeRefreshLayout;
//    Adapter mAdapter;
    SwipeRefreshLayout mySwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);








        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);


        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    public static final String LOG_TAG = "";

                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                        mySwipeRefreshLayout.setRefreshing(true);

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
//                        myUpdateOperation();
                        requestData(REST_URI);

                        mySwipeRefreshLayout.setRefreshing(false);

                    }
                }
        );


        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);


        tasks = new ArrayList<>();


        if (isOnline()) {
            requestData(REST_URI);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }

        //TODO: single selection && register this ListActivity as the event handler

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

//
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ((BuildingAdapter) getListAdapter()).getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    ((BuildingAdapter) getListAdapter()).getFilter().filter(newText);
                return false;
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.about_dialog:
                DialogFragment newFragment = new AboutDialog();
                newFragment.show(getFragmentManager(), "Test");
                break;

            case R.id.add_building:
                Intent intent = new Intent(getApplicationContext(), NewBuildingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.action_sort_name_asc:
                Collections.sort(buildingList, new Comparator<Building>() {
                    @Override
                    public int compare(Building lhs, Building rhs) {
                        Log.i("Buildings", "Sorting buildings by name (a-z)");
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                break;

            case R.id.action_sort_name_dsc:
                Collections.sort(buildingList, Collections.reverseOrder(new Comparator<Building>() {
                    @Override
                    public int compare(Building lhs, Building rhs) {
                        Log.i("Buildings", "Sorting buildings by name (z-a)");
                        return lhs.getName().compareTo(rhs.getName());
                    }
                }));
                break;
            case R.id.search:
                handleIntent(getIntent());
                    break;
            default:
                return false;
        }

        ((BuildingAdapter) getListView().getAdapter()).notifyDataSetChanged();
        item.setChecked(true);
        return false;
    }


    private void handleIntent(Intent intent) {


        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            Log.i("QUERY", query + "");
            doMySearch(query);

        }

    }


    private void doMySearch(String query) {
        Toast.makeText(getApplicationContext(),"HERE     " + query, Toast.LENGTH_SHORT);
    }

    private void requestData(String uri) {
        RequestPackage getPackage = new RequestPackage();
        getPackage.setMethod(HttpMethod.GET);
        getPackage.setUri(uri);
        MyTask task = new MyTask();
        task.execute(getPackage);
    }

    protected void updateDisplay() {
        //Use PlanetAdapter to display data
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


        Toast.makeText(getApplicationContext(), "VIEW " + view, Toast.LENGTH_SHORT);
        Log.i("VIEW ", view +"");


        Building theSelectedBuilding = buildingList.get(position);

        Intent intent = new Intent(getApplicationContext(), EditBuildingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("building_id",  theSelectedBuilding.getBuildingId());
        startActivity(intent);

        return true;
    }

    // TODO : implement the only method of the OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Building theSelectedBuilding = buildingList.get(position);

        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("building_name", theSelectedBuilding.getName());
        intent.putExtra("building_description", theSelectedBuilding.getDescription());
        intent.putExtra("building_address", theSelectedBuilding.getAddress());
        intent.putExtra("building_hours", theSelectedBuilding.getDate());
        intent.putExtra("building_id",  theSelectedBuilding.getBuildingId());
        intent.putExtra("building_image", theSelectedBuilding.getImage());
        startActivity(intent);

    }




    private class MyTask extends AsyncTask<RequestPackage, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(RequestPackage... params) {

            String content = HttpManager.getData(params[0], "anto0084", "password");
            buildingList = BuildingJSONParser.parseFeed(content);

            return buildingList;
        }

        @Override
        protected void onPostExecute(List<Building> result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            buildingList = result;
            updateDisplay();

//            activiteSearch();
        }
    }
//
//    private void activiteSearch() {
//
//
//        lv = (ListView) findViewById( android.R.id.list );
//        sv = (SearchView) findViewById( R.id.search );
//
//        adapter = new ArrayAdapter<>( this, R.layout.activity_main, buildingList);
//        lv.setAdapter(adapter);
//
//        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String text) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String text) {
//                adapter.getFilter().filter(text);
//
//                return false;
//            }
//        });
//    }


}
