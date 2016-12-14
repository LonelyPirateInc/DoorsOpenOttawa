package com.algonquincollege.anto0084.doorsopenottawa;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.algonquincollege.anto0084.doorsopenottawa.model.Building;
import com.algonquincollege.anto0084.doorsopenottawa.parsers.BuildingJSONParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by rayantonenko on 2016-12-08.
 */

public class NewBuildingActivity extends FragmentActivity {

    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PICTURE = 2;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int PICK_IMAGE = 0;
    private static final int PICK_IMAGE_FROM_GALLERY = 1;
    public Uri photoURI;
    private final int CAMERA_RESULT = 1;
    private final String Tag = getClass().getName();


    private Uri u;
    private Uri imageToUploadUri;
    public File photoFile;
    String mCurrentPhotoPath;

    private String selectedImagePath;

    public String new_building_name;
    public String new_building_address;
    public byte[] new_building_image;
    public String new_building_description;
    public Button SubmitBuilding;
    public Button new_building_image_taken_button;
    public Button new_building_image_existing_button;
    public ImageView new_building_image_taken;
    public ImageView mImageView;
    public Building new_uploaded_building;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_building_activity);


        SubmitBuilding = (Button) findViewById(R.id.submit_new_building);
        new_building_image_taken_button = (Button) findViewById(R.id.new_building_image_taken_button);
        mImageView = (ImageView) findViewById(R.id.new_building_image_taken);
        new_building_image_existing_button = (Button) findViewById(R.id.new_building_image_exist_button);

        SubmitBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Building newBuilding = new Building();

                newBuilding.setName(((EditText) findViewById(R.id.new_building_name)).getText().toString());
                newBuilding.setAddress(((EditText) findViewById(R.id.new_building_address)).getText().toString());
//                newBuilding.setImage();
                newBuilding.setDescription(((EditText) findViewById(R.id.new_building_description)).getText().toString());

                RequestPackage pkg = new RequestPackage();
                pkg.setMethod(HttpMethod.POST);

                pkg.setUri(REST_URI);
                pkg.setParam("name", newBuilding.getName());
                pkg.setParam("address", newBuilding.getAddress());
                pkg.setParam("image", "abc.png");
                pkg.setParam("description", newBuilding.getDescription());

                DoTask postTask = new DoTask();
                postTask.execute(pkg);
            }
        });


        new_building_image_taken_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();

            }
        });


        new_building_image_existing_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("Error", String.valueOf(ex));
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_TAKE_PHOTO) {

                mImageView.setImageURI(photoURI);

            }

            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();


                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }


//                selectedImagePath = getPath(selectedImageUri);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    class DoTask extends AsyncTask<RequestPackage, String, String> {

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
                Toast.makeText(NewBuildingActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            } else {
                new UploadPictureTask().execute(result);

            }
        }

    }


    private void uploadMedia(String result) {
        try {

            new_uploaded_building = BuildingJSONParser.parseBuilding(result.toString());


            String charset = "UTF-8";
//                File uploadFile1 = new File("/sdcard/myvideo.mp4");
            String requestURL = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings/" + new_uploaded_building.getBuildingId() + "/image";

            MultipartUtility multipart = new MultipartUtility(requestURL, charset);

            Log.e("TAG", photoFile.getAbsolutePath());
            multipart.addFilePart("image", photoFile);

            List<String> response = multipart.finish();

            Log.v("rht", "SERVER REPLIED:");

            for (String line : response) {
                Log.v("rht", "Line : " + line);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class UploadPictureTask extends AsyncTask<String, String, String> {
        @Override

        protected String doInBackground(String... params) {
            uploadMedia(params[0]);
            return null;
        }
    }
}
