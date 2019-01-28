package net.anvisys.xpen;

import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import net.anvisys.xpen.Object.Profile;
import net.anvisys.xpen.Object.ProjectData;

import org.json.JSONException;
import org.json.JSONObject;

public class AddProjectActivity extends AppCompatActivity {


    EditText txtProjectName,txtProjectNumber,txtClientName,txtProjectValue,txtProjectDescription;
    ProgressBar progressBar;
    Button btnSubmit;
    Profile myProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("New Project");
        actionBar.show();

        txtProjectName = findViewById(R.id.txtProjectName);
        txtProjectNumber = findViewById(R.id.txtProjectNumber);
        txtClientName = findViewById(R.id.txtClientName);
        txtProjectValue = findViewById(R.id.txtProjectValue);
        txtProjectDescription = findViewById(R.id.txtProjectDescription);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        btnSubmit = findViewById(R.id.btnSubmit);
        myProfile = Session.GetUser(this);
    }


    public void SaveProject(View v)
    {
        ProjectData temp = new ProjectData();
        temp.ProjectName = txtProjectName.getText().toString();
        temp.ProjectNumber = txtProjectNumber.getText().toString();
        temp.ClientName = txtClientName.getText().toString();
        temp.ProjectDescription = txtProjectDescription.getText().toString();
        temp.ProjectValue = Integer.parseInt(txtProjectValue.getText().toString());

        if( temp.ProjectName.matches("")||temp.ProjectNumber.matches("")||
                temp.ClientName.matches("")||temp.ProjectDescription.matches(""))
        {
            Toast.makeText(this,"Fill All fields", Toast.LENGTH_LONG);
        }
        else
        {
            if(APP_VARIABLES.NETWORK_STATUS)
            {
                SaveProjectAtServer(temp);
            }
            else
            {
                Snackbar.make(getCurrentFocus(),"Connect to Network for Project Creation", BaseTransientBottomBar.LENGTH_LONG);
                temp.ProjectID =0;
                // SaveProjectInLocal(temp);
            }
        }
    }


    private void SaveProjectAtServer(final ProjectData prj)
    {
        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);
        int RequestID=1;


        try {
            String reqBody = "{\"ProjectNumber\":\""+ prj.ProjectNumber+ "\",\"ClientName\":\""+ prj.ClientName+"\",\"ProjectName\":\""+ prj.ProjectName +
                    "\",\"ProjectValue\":"+ prj.ProjectValue+",\"ProjectDescription\":\""+ prj.ProjectDescription +
                    "\",\"CreatedBy\":\""+ myProfile.UserID +"\",\"OrgID\":"+myProfile.OrgID+"}";

            String url = APP_CONST.APP_SERVER_URL + "api/Project/New";
            JSONObject jsonObject = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    try {

                        String Response = jObj.getString("Response");
                        if(Response.matches("OK"))
                        {
                            Snackbar.make(getCurrentFocus(), "Updated Successfully", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            AddProjectActivity.this.finish();
                        }
                        else
                        {
                            Snackbar.make(getCurrentFocus(), "Failed to Update", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);

                    } catch (JSONException jEx) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                    }
                    catch (Exception ex)
                    {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
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
                    btnSubmit.setEnabled(true);

                }


            });


            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);


            //*******************************************************************************************************
        } catch (JSONException js) {
            progressBar.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);
        } finally {

        }
    }
}
