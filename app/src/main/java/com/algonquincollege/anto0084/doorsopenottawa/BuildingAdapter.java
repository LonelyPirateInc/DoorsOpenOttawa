package com.algonquincollege.anto0084.doorsopenottawa;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquincollege.anto0084.doorsopenottawa.model.Building;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Purpose: customize the Building cell for each building displayed in the ListActivity (i.e. MainActivity).
 * Usage:
 *   1) extend from class ArrayAdapter<YourModelClass>
 *   2) @override getView( ) :: decorate the list cell
 *
 * Based on the Adapter OO Design Pattern.
 *
 * @author anto0084@AlgonquinCollege.com Anton Antonenko
 *
 */

public class BuildingAdapter extends ArrayAdapter<Building> {

    private Context context;
    private List<Building> buildingList;

    // cache the binary image for each planet
    private LruCache<Integer , Bitmap> imageCache;

    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.buildingList = objects;



        // instantiate the imageCache
        final int maxMemory = ( int ) (Runtime.getRuntime().maxMemory() / 1024 );
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>( cacheSize );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_building, parent, false);
        view.setBackgroundResource(R.color.cellColor);

        //Display planet name in the TextView widget
        Building building = buildingList.get(position);
        TextView tv1 = (TextView) view.findViewById(R.id.textView1);
        TextView tv2 = (TextView) view.findViewById( R.id.textView2);
        tv1.setText(building.getName());
        tv2.setText(building.getAddress());
//        tv.append(building.);

        // Display planet photo in ImageView widget
        Bitmap bitmap = imageCache.get( building.getBuildingId() );
        if ( bitmap != null ) {
            Log.i( "BUILDINGS" , building.getName() + "\tbitmap in cache" );
            ImageView image = ( ImageView ) view.findViewById( R.id.imageView1 );
            image.setImageBitmap( building.getBitmap() );
//            view.setBackgroundResource( );

        } else {
            Log.i( "BUILDINGS" , building.getName() + "\tfetching bitmap using AsyncTask  ");
            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }


        return view;
    }

    // container for AsyncTask params
    private class BuildingAndView {
        public Building building;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<BuildingAndView, Void, BuildingAndView> {

        @Override
        protected BuildingAndView doInBackground(BuildingAndView... params) {
            BuildingAndView container = params[0];
            Building building = container.building;

            try {
                String imageUrl = MainActivity.IMAGES_BASE_URL + building.getImage();
                InputStream in = ( InputStream ) new URL( imageUrl ).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream( in );
                building.setBitmap( bitmap );
                in.close();
                container.bitmap = bitmap;
                return  container;
            } catch (Exception e) {
                System.err.println( "IMAGE: " + building.getName() );
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(BuildingAndView result) {
            try {
                ImageView image = ( ImageView ) result.view.findViewById( R.id.imageView1 );
                image.setImageBitmap( result.bitmap );
                result.building.setBitmap( result.bitmap );
                imageCache.put( result.building.getBuildingId(), result.bitmap );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
