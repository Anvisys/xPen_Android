package net.anvisys.xpen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.xpen.Common.APP_CONST;
import net.anvisys.xpen.Common.ImageServer;
import net.anvisys.xpen.Common.Session;
import net.anvisys.xpen.Object.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {


    ImageView profileImage;
    TextView txtMobile,txtName,txtRole,txtEmail,txtLocation,txtOrg,editOrg,editEmail,txtPasswordMessage,txtEditProfile;
    EditText editName,editLocation,editMobile;
    Profile myProfile;
    Bitmap newBitmap;
    Button btnImageUpload,btnUpdateProfile;
    View txtProfile, editProfile;
    ProgressBar progressBar,ImgProgressBar;
    static final int REQUEST_IMAGE_GET = 1;
    static final int REQUEST_IMAGE_CROP = 2;
    String strImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        try {
            Toolbar toolbar =  findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Profile");
            actionBar.show();
            progressBar =  findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            ImgProgressBar = findViewById(R.id.ImgProgressBar);
            ImgProgressBar.setVisibility(View.GONE);
            btnImageUpload = findViewById(R.id.btnImageUpdate);
            profileImage = findViewById(R.id.profile_image);
            txtMobile = findViewById(R.id.txtmobile);
            txtName =  findViewById(R.id.txtName);
            txtOrg = findViewById(R.id.txtOrganization);
            txtEmail = findViewById(R.id.txtEmail);
            txtLocation =  findViewById(R.id.txtLocation);
            txtEditProfile = findViewById(R.id.txtEditProfile);
            txtEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileEdit();
                }
            });

            txtProfile = findViewById(R.id.ShowProfileContent);
            editProfile = findViewById(R.id.EditProfileContent);
            txtRole = findViewById(R.id.txtRole);
            myProfile = Session.GetUser(getApplicationContext());
            txtMobile.setText(myProfile.MOB_NUMBER);
            txtName.setText(myProfile.NAME);
            txtRole.setText(myProfile.Role);
            txtOrg.setText(myProfile.OrganizationName);
            txtEmail.setText(myProfile.E_MAIL);
            txtLocation.setText(myProfile.Location);
            Bitmap bmp = ImageServer.GetImageBitmapFromExternal(myProfile.UserID, getApplicationContext());
            profileImage.setImageBitmap(bmp);
        }
        catch (Exception ex)
        {

        }
    }

    public void ProfileEdit()
    {
        if (txtProfile.getVisibility()==View.VISIBLE) {
            txtProfile.setVisibility(View.GONE);
        }
        if(editProfile.getVisibility() == View.GONE)
        {
            editOrg = findViewById(R.id.editOrganization);
            editOrg.setText(myProfile.OrganizationName);
            editEmail = findViewById(R.id.editEmail);
            editEmail.setText(myProfile.E_MAIL);
            editMobile = findViewById(R.id.editMobile);
            editMobile.setText(myProfile.MOB_NUMBER);
            editName = findViewById(R.id.editName);
            editName.setText(myProfile.NAME);
            editLocation = findViewById(R.id.editLocation);
            btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
            editProfile.setVisibility(View.VISIBLE);
            myProfile = Session.GetUser(this);
            Bitmap bmp = ImageServer.GetImageBitmapFromExternal(myProfile.UserID, this);
            profileImage.setImageBitmap(bmp);
        }
    }

    public void UpdateProfile(View v)
    {
        String newName = editName.getText().toString();
        String newMobile = editMobile.getText().toString();
        String newLocation = editLocation.getText().toString();
        SaveProfileData(newName,newMobile,newLocation);
    }

    private void SaveProfileData(final String newName, final String newMobile, final String newLocation)
    {
        progressBar.setVisibility(View.VISIBLE);
        String url = APP_CONST.APP_SERVER_URL  + "api/User/Edit";
        String reqBody = "{\"UserEmail\":\""+ myProfile.E_MAIL + "\",\"UserMobile\":\""+ newMobile + "\",\"UserName\":\""+ newName + "\",\"Location\":\"" + newLocation + "\",\"UserID\":\""+ myProfile.UserID + "\"}";
        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (jObj.getString("Response").matches("OK")) {
                            myProfile.NAME = newName;
                            myProfile.MOB_NUMBER = newMobile;
                            myProfile.Location=newLocation;
                            Session.AddUser(getApplicationContext(), myProfile);
                            editProfile.setVisibility(View.GONE);
                            txtProfile.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Updated Successfully.",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                        }


                    }
                    catch (JSONException jEx)
                    {


                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    String message = error.toString();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Post could not be submitted : Try Again",
                            Toast.LENGTH_LONG).show();

                }
            });


            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);

            //*******************************************************************************************************
        }
        catch (JSONException js)
        {
            progressBar.setVisibility(View.GONE);
        }

        finally {

        }

    }
    public void EditImage(View view)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_IMAGE_GET) {

                if (data != null) {
                    Uri uri = data.getData();
                    InputStream image_stream = getContentResolver().openInputStream(uri);
                    byte[] imgByte= ImageServer.getBytes(image_stream);
                    ImageServer.SaveFileToExternal(imgByte,"crop.jpg",getApplicationContext());
                    File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File myDir = new File(root + "/xPen/crop.jpg");
                    myDir.mkdirs();
                    Uri contentUri = Uri.fromFile(myDir);
                    ImageCropFunction(contentUri);
                }
            } else if (requestCode == REQUEST_IMAGE_CROP) {

                if (data != null) {

                    Bundle bundle = data.getExtras();
                    if(bundle!= null) {
                        newBitmap = bundle.getParcelable("data");
                        profileImage.setImageBitmap(newBitmap);
                        btnImageUpload.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Uri cropUri =  data.getData();
                        InputStream image_stream = getContentResolver().openInputStream(cropUri);
                        newBitmap= BitmapFactory.decodeStream(image_stream);
                        profileImage.setImageBitmap(newBitmap);
                        btnImageUpload.setVisibility(View.VISIBLE);
                    }
                    strImage = ImageServer.getStringFromBitmap(newBitmap);
                    profileImage.invalidate();
                }
            }
        }
        catch (Exception ex)
        {
            int a=1;
        }
    }
    public void ImageCropFunction(Uri uri) {

        // Image Crop Code
        try {
            Intent CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri, "image/*");
            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 100);
            CropIntent.putExtra("outputY", 100);
            CropIntent.putExtra("aspectX", 1);
            CropIntent.putExtra("aspectY", 1);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);
            // CropIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            // CropIntent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(CropIntent, REQUEST_IMAGE_CROP);
        }
        catch (Exception e)
        {
            int a =1;
        }
    }
    public void Image_Update(View v)
    {
        btnImageUpload.setVisibility(View.INVISIBLE);

        ImgProgressBar.setVisibility(View.VISIBLE);
        String url = APP_CONST.APP_SERVER_URL+ "/api/Image";
        String reqBody = "{\"UserID\":\""+ myProfile.UserID  + "\",\"ImageString\":\""+ strImage + "\"}";
        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    ImgProgressBar.setVisibility(View.GONE);
                    try {
                        String Response = jObj.getString("Response");

                        if(Response.matches("OK"))
                        {
                            ImageServer.SaveStringAsBitmap(strImage,myProfile.UserID,getApplicationContext());

                        }
                        else if(Response.matches("Fail"))
                        {
                            btnImageUpload.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"Failed to Upload Image", Toast.LENGTH_LONG).show();

                        }
                    }
                    catch (JSONException jex)
                    {}


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    String message = error.toString();

                    ImgProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Image Upload failed : Try Later", Toast.LENGTH_LONG).show();

                }
            });


            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);

            //*******************************************************************************************************
        }
        catch (JSONException js)
        {
            btnImageUpload.setVisibility(View.VISIBLE);
            ImgProgressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Image Upload failed : Try Later", Toast.LENGTH_LONG).show();
        }

        finally {

        }
    }


}
