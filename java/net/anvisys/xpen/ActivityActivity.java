package net.anvisys.xpen;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import net.anvisys.xpen.Common.DataAccess;
import net.anvisys.xpen.Common.Session;
import net.anvisys.xpen.Object.ActivityData;
import net.anvisys.xpen.Object.Profile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.LinkedHashMap;

public class ActivityActivity extends AppCompatActivity {


    ListView activityListView;
    ActivityAdapter myAdapter;
    ProgressBar progressBar;
    Profile myProfile;
    boolean isResult = false;
    NumberFormat currFormat;
    LinkedHashMap<Integer,ActivityData> prjActivityData = new LinkedHashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Activities");
        actionBar.show();

        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));


        myProfile = Session.GetUser(getApplicationContext());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(ActivityActivity.this, AddActivityActivity.class);
                startActivity(addIntent);
            }
        });



        activityListView = findViewById(R.id.activityListView);
        myAdapter = new ActivityAdapter(this,0,prjActivityData);
        activityListView.setAdapter(myAdapter);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        try {
            Intent intent = getIntent();
            if (intent != null) {
                isResult = intent.getBooleanExtra("isResult", false);
            }
        }
        catch (Exception ex)
        {

        }


        activityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityData act = (ActivityData) activityListView.getItemAtPosition(position);

                if(isResult)
                {
                    if(act.ActivityStatus.matches("Approved"))
                    {
                        Snackbar.make(getCurrentFocus(),"", Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                    else {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("Activity", act);
                        returnIntent.putExtra("ActivityID", act);
                        returnIntent.putExtra("ActivityName", act);
                        returnIntent.putExtra("ProjectName", act);
                        returnIntent.putExtra("Status", act);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }
                else {

                }


            }
        });

        GetActivityData();

    }
    private class ActivityAdapter extends ArrayAdapter<ActivityData>
    {
        LayoutInflater inflat;
        ActivityViewHolder holder;
        public ActivityAdapter(@NonNull Context context, int resource, @NonNull LinkedHashMap<Integer,ActivityData> objects) {
            super(context, resource);
            inflat = LayoutInflater.from(context);
        }



        @Override
        public int getCount() {
            return prjActivityData.size();
        }

        @Nullable
        @Override
        public ActivityData getItem(int position) {

            return (ActivityData) prjActivityData.values().toArray()[position];
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
                    holder.txtStatus = convertView.findViewById(R.id.txtStatus);
                    holder.txtItemName =  convertView.findViewById(R.id.txtItemName);
                    holder.txtOwnerName =  convertView.findViewById(R.id.txtOwnerName);
                    holder.txtManager =  convertView.findViewById(R.id.txtManager);
                    holder.viewHeader =  convertView.findViewById(R.id.viewHeader);
                    convertView.setTag(holder);
                }

                holder=(ActivityViewHolder)convertView.getTag();
                ActivityData act = getItem(position);
                holder.txtItemName.setText(act.ActivityName + ", ");
                holder.txtOwnerName.setText(act.ProjectName);
                holder.txtManager.setText("Manager: "+ act.ApproverName);

                holder.txtExpenseAmount.setText("Spent: "+currFormat.format(act.Expenses));
                holder.txtReceiveAmount.setText("Received: "+currFormat.format(act.Received));
                holder.txtStatus.setText(act.ActivityStatus);
                if(act.ActivityStatus.matches("Paid") || act.ActivityStatus.matches("Approved"))
                {
                    holder.viewHeader.setBackgroundColor(Color.rgb(255,191,68));
                }
                else if(act.ActivityStatus.matches("Submitted"))
                {
                    holder.viewHeader.setBackgroundColor(Color.rgb(68,132,255));
                }

            }
            catch (Exception ex)
            {
                int a=1;

            }

            return convertView;
        }
    }
    private class ActivityViewHolder
    {
        TextView txtManager,txtExpenseAmount,txtReceiveAmount,txtStatus,txtItemName,txtOwnerName;
        View viewHeader;
    }

    public void GetActivityData()
    {
        progressBar.setVisibility(View.VISIBLE);
        int RequestID=1;
        String url = APP_CONST.APP_SERVER_URL + "api/Activity/Organization/" + myProfile.OrgID +"/Employee/"+myProfile.UserID+"/time/" + Session.GetProjectSyncTime(getApplicationContext());

        try {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray json = response.getJSONArray("$values");
                        int x = json.length();
                        for(int i = 0; i < x; i++){
                            ActivityData pa = new ActivityData();
                            JSONObject jObj = json.getJSONObject(i);
                            pa.ActivityID = (jObj.getInt("ActivityID"));
                            pa.ActivityName = (jObj.getString("ActivityName"));
                            pa.ProjectName =(jObj.getString("ProjectName"));
                            pa.Expenses  = jObj.getInt("Expenses") ;
                            pa.Advance =0;//(jObj.getInt("Advance"));
                            pa.ActivityStatus  = (jObj.getString("ActivityStatus")) ;
                            pa.ApproverName =(jObj.getString("ApproverName"));
                            if(!pa.ActivityStatus.matches("Paid"))
                                prjActivityData.put(pa.ActivityID,pa);
                        }
                        myAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException jEx) {
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
            //*******************************************************************************************************
        } catch (Exception js) {
            progressBar.setVisibility(View.GONE);
        } finally {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.projects)
        {
            Intent projectIntent = new Intent(ActivityActivity.this, ProjectActivity.class);
            startActivity(projectIntent);
            return true;
        }
        else if(id== R.id.logOff)
        {
            LogOff();
            return true;
        }
        return true;
    }

    public void LogOff()
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityActivity.this);
        dialog.setTitle("LogOff");
        dialog.setMessage("Confirm");
        dialog.setCancelable(false);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                return;
            }
        });

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (Session.LogOff(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), "Successfully Un-Registered", Toast.LENGTH_LONG).show();
                        ActivityActivity.this.finish();
                        DataAccess da = new DataAccess(getApplicationContext());
                        da.open();
                        da.ClearAll();
                        da.close();

                        return;
                    }
                }
                catch (Exception ex)
                {

                }
            }
        });
        dialog.show();

    }

}
