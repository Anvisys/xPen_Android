package net.anvisys.xpen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import net.anvisys.xpen.Common.APP_VARIABLES;
import net.anvisys.xpen.Common.Session;
import net.anvisys.xpen.Common.Utility;
import net.anvisys.xpen.Object.OvalImageView;
import net.anvisys.xpen.Object.Profile;
import net.anvisys.xpen.Object.ProjectData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView selectProjectName,txtClientName,txtApproverName;
    EditText txtActivityName,txtActivityDescription;
    Button btnAddActivity;
    int SelectedProjectID=0;
    String ActivityName="",ActivityDescription="";
    OvalImageView imgManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("New Activity");
        actionBar.show();

        selectProjectName = findViewById(R.id.selectProjectName);
        txtClientName = findViewById(R.id.txtClientName);
        txtApproverName = findViewById(R.id.txtApproverName);
        txtActivityName = findViewById(R.id.txtActivityName);
        txtActivityDescription = findViewById(R.id.txtActivityDescription);
        btnAddActivity = findViewById(R.id.btnAddActivity);
        imgManager = findViewById(R.id.imgManager);

        Intent prjIntent = getIntent();
        ProjectData prj = prjIntent.getParcelableExtra("Project");
        txtClientName.setText("Client: " +prj.ClientName);
        SelectedProjectID = prjIntent.getIntExtra("ProjectID",0) ;
        String name = prjIntent.getStringExtra("ProjectName");
        selectProjectName.setText("Project "+ prj.ProjectName);
        txtApproverName.setText("Manager: " + prj.Approver);

        //ImageView iv=findViewById(R.id.txtApproverName);
        // TextView txtApproverName = (TextView)findViewById(R.id.imgManager);

        Bitmap bm= Bitmap.createBitmap(txtApproverName.getWidth(), txtApproverName.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        //c.drawText(initials,x,y,paint);
        txtApproverName.draw(c);
        imgManager.setImageBitmap(bm);
    }

    public void AddActivity(View v)
    {
        ActivityName = txtActivityName.getText().toString();
        ActivityDescription = txtActivityDescription.getText().toString();
        if(ActivityName.matches("")|| ActivityDescription.matches(""))
        {
            Toast.makeText(this,"Fields are empty", Toast.LENGTH_LONG);
        }
        else if(!APP_VARIABLES.NETWORK_STATUS)
        {
            Toast.makeText(this,"No Network, Activity can not be created.", Toast.LENGTH_LONG);
        }
        else
        {
            AddData(getApplicationContext());
        }
    }
    private void AddData(final Context context)
    {
        String date =  Utility.GetCurrentDateTimeUTC();
        Profile myProfile = Session.GetUser(getApplicationContext());
        progressBar.setVisibility(View.VISIBLE);

        int RequestID=1;

        String reqBody = "{\"ActivityName\":\""+ ActivityName +"\",\"EmployeeID\":\""+ myProfile.UserID+"\",\"ProjectID\":\""+ SelectedProjectID+
                "\",\"CreatedBy\":\""+ myProfile.UserID+ "\",\"ActivityDescription\":\""+ ActivityDescription +"\",\"CreationDate\":\""+ date+
                "\",\"ExpenseAmount\":"+ 0+ ",\"ActivityStatus\":\"Initiated\",\"OrgID\":"+ myProfile.OrgID + "}";

        String url = APP_CONST.APP_SERVER_URL + "api/Activity/CreateActivity";

        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    try {
                        String Response = jObj.getString("Response");
                        if(Response.matches("OK"))
                        {

                            setResult(APP_CONST.REQUEST_ACTIVITY_CODE);
                            AddActivity.this.finish();
                        }
                        else
                        {
                            Snackbar.make(getCurrentFocus(), "Failed to Update", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                        progressBar.setVisibility(View.GONE);

                    } catch (JSONException jEx) {
                        progressBar.setVisibility(View.GONE);
                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse == null)
                    {
                        Snackbar.make(getCurrentFocus(), "Network Error, Try Again", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    progressBar.setVisibility(View.GONE);

                }
            });


            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);


            //*******************************************************************************************************
        } catch (JSONException js) {
            progressBar.setVisibility(View.GONE);
        } finally {

        }
    }

}
