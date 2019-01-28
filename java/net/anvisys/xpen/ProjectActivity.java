package net.anvisys.xpen;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.xpen.Common.APP_CONST;
import net.anvisys.xpen.Common.Session;
import net.anvisys.xpen.Object.ActivityData;
import net.anvisys.xpen.Object.Profile;
import net.anvisys.xpen.Object.ProjectData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ProjectActivity extends AppCompatActivity {

    private ListView listViewProject;
    private ProjectAdapter prjAdapter;
    ProgressBar progressBar;
    List<ProjectData> projectList = new ArrayList<>();
    boolean isResult = false;
    TextView txtError,txtRetry;
    NumberFormat currFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Projects");
        actionBar.show();
        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        try {
            Intent parent = getIntent();
            if (parent != null) {
                isResult = parent.getBooleanExtra("isResult", false);
            }
        }
        catch (Exception ex)
        {

        }

        listViewProject = findViewById(R.id.listViewProject);
        prjAdapter = new ProjectAdapter(getApplicationContext(),0,projectList);
        listViewProject.setAdapter(prjAdapter);
        listViewProject.setVisibility(View.GONE);
        txtError = findViewById(R.id.txtError);
        txtError.setVisibility(View.GONE);
        txtRetry=findViewById(R.id.txtRetry);
        txtRetry.setVisibility(View.GONE);

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent statement = new Intent(ProjectActivity.this, AddProjectActivity.class);
                startActivity(statement);
            }
        });


        listViewProject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProjectData prj = (ProjectData) listViewProject.getItemAtPosition(position);

                if(isResult)
                {
                    if(prj.Status.matches("Closed"))
                    {
                        Snackbar.make(getCurrentFocus(),"", Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                    else {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("Project", prj);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }
                else {


                }


            }
        });

        GetProjects();
    }

    public void Retry(View view)
    {
        GetProjects();


    }

    private void GetProjects()
    {
        Profile myProfile = Session.GetUser(getApplicationContext());
        progressBar.setVisibility(View.VISIBLE);
        int RequestID=1;
        String url = APP_CONST.APP_SERVER_URL + "api/Project/Organization/" + myProfile.OrgID +"/Status/Open";

        try {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray json = response.getJSONArray("$values");
                        int x = json.length();
                        for(int i = 0; i < x; i++){
                            ProjectData prj = new ProjectData();
                            JSONObject jObj = json.getJSONObject(i);
                            prj.ProjectID = (jObj.getInt("ProjectID"));
                            prj.ProjectName = (jObj.getString("ProjectName"));
                            prj.ClientName =(jObj.getString("ClientName"));
                            prj.ProjectNumber =(jObj.getString("ProjectNumber"));
                            prj.WorkCompletion =(jObj.getInt("WorkCompletion"));
                            prj.ProjectDescription =(jObj.getString("ProjectDescription"));
                            prj.Approver =(jObj.getString("CreatedByName"));
                            prj.Status = (jObj.getString("Status"));
                            prj.ExpenseAmount = (jObj.getInt("Spent"));
                            prj.ReceiveAmount = (jObj.getInt("Received"));
                            prj.ApproverID = (jObj.getInt("CreatedBy"));
                            projectList.add(prj);
                        }
                        Session.SetProjectSyncTime(getApplicationContext());
                        prjAdapter.notifyDataSetChanged();
                        listViewProject.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        txtError.setVisibility(View.GONE);
                        txtRetry.setVisibility(View.GONE);

                    } catch (JSONException jEx) {
                        progressBar.setVisibility(View.GONE);
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText("No Data");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listViewProject.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText("Error Occured");
                    txtRetry.setVisibility(View.VISIBLE);
                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
            //*******************************************************************************************************
        } catch (Exception js) {
            listViewProject.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            txtError.setVisibility(View.VISIBLE);
            txtError.setText("Error Occured");
            txtRetry.setVisibility(View.VISIBLE);
        } finally {

        }
    }


    private class ActivityViewHolder
    {
        TextView txtManager,txtExpenseAmount,txtReceiveAmount,txtEmployee,txtStatus,txtItemName,txtOwnerName;
        View viewHeader;
    }


    private class ProjectAdapter extends ArrayAdapter<ProjectData>
    {
        LayoutInflater inflat;
        ActivityViewHolder holder;
        public ProjectAdapter(@NonNull Context context, int resource, @NonNull List<ProjectData> objects) {
            super(context, resource, objects);
            inflat = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return projectList.size();
        }

        @Nullable
        @Override
        public ProjectData getItem(int position) {
            return projectList.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            try{
                if(convertView == null)
                {
                    convertView = inflat.inflate(R.layout.row_item_activity_project, null);
                    holder = new ActivityViewHolder();
                    holder.txtExpenseAmount = convertView.findViewById(R.id.txtExpenseAmount);
                    holder.txtReceiveAmount = convertView.findViewById(R.id.txtReceiveAmount);
                    holder.txtStatus =  convertView.findViewById(R.id.txtStatus);
                    holder.txtItemName =  convertView.findViewById(R.id.txtItemName);
                    holder.txtOwnerName =  convertView.findViewById(R.id.txtOwnerName);
                    holder.txtManager =  convertView.findViewById(R.id.txtManager);
                    holder.viewHeader = convertView.findViewById(R.id.viewHeader);
                    convertView.setTag(holder);
                }

                holder=(ActivityViewHolder)convertView.getTag();
                ProjectData prj = getItem(position);


                holder.txtItemName.setText(prj.ProjectName + ", ");
                holder.txtOwnerName.setText(prj.ClientName);
                holder.txtManager.setText(prj.Approver);
                holder.txtExpenseAmount.setText("Spent:\n "+currFormat.format(prj.ExpenseAmount));
                holder.txtReceiveAmount.setText("Received:\n "+currFormat.format(prj.ReceiveAmount));
                holder.txtEmployee.setText("Manager: "+ prj.Approver);
               // holder.txtStatus.setText(prj.Status);
                if(prj.Status.matches("Closed") || prj.Status.matches("Complete"))
                {
                    holder.txtStatus.setText(prj.Status);
                    holder.viewHeader.setBackgroundColor(Color.MAGENTA);
                }else {
                    holder.txtStatus.setText(prj.Status);
                }


            }
            catch (Exception ex)
            {
                int a=1;
            }
            return convertView;
        }
    }


}
