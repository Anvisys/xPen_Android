package net.anvisys.xpen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import net.anvisys.xpen.Common.DataAccess;
import net.anvisys.xpen.Common.ImageServer;
import net.anvisys.xpen.Common.Session;
import net.anvisys.xpen.Common.Utility;
import net.anvisys.xpen.Object.ActivityData;
import net.anvisys.xpen.Object.OvalImageView;
import net.anvisys.xpen.Object.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

public class ApprovalActivity extends AppCompatActivity {

    ListView listViewApproval;
    TaskAdapter taskAdapter;
    Profile myProfile;
    ProgressBar progressBar;
    List<Integer> inProcessList = new ArrayList<>();
    HashMap<Integer,ActivityData> approvalData= new HashMap<>();
    ActivityData selectedActivity;
    TextView txtNoData,txtRetry,txtTotalValue,txtCount;
    String url;
    Spinner spinnerStatus;
    ImageView imgSearch;
    EditText txtName;
    List<String> statusString = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.show();
        actionBar.setTitle("Task");

        txtNoData = findViewById(R.id.txtNoData);
        txtNoData.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        myProfile = Session.GetUser(getApplicationContext());
        listViewApproval = findViewById(R.id.listViewApproval);
        taskAdapter = new TaskAdapter(this,0, approvalData);
        listViewApproval.setAdapter(taskAdapter);
        txtRetry= findViewById(R.id.txtRetry);
        txtRetry.setVisibility(View.GONE);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        txtTotalValue =findViewById(R.id.txtTotalValue);
        txtCount =findViewById(R.id.txtCount);
        imgSearch = findViewById(R.id.imgSearch);
        txtName = findViewById(R.id.txtName);

        final String[] status = new String[]{"Approved", "Submitted", "All"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item ,status);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
        spinnerStatus.setSelection(0);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                txtCount.setText(spinnerStatus.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        if(myProfile.Role.matches("Admin"))
        {
            url = APP_CONST.APP_SERVER_URL + "api/Activity/Organization/" + myProfile.OrgID  + "/Status/" + "Open";
        }
        else if(myProfile.Role.matches("Manager"))
        {
            url = APP_CONST.APP_SERVER_URL + "api/Activity/Organization/" + myProfile.OrgID +"/Approver/" + myProfile.UserID + "/Status/" + "Open";
        }
        else
        {
            url = APP_CONST.APP_SERVER_URL + "api/Activity/Organization/" + myProfile.OrgID +"/Employee/" + myProfile.UserID + "/Status/" + "Open";
        }
        GetReviewData(url);
    }


    public void Retry(View view)
    {
        txtNoData.setVisibility(View.GONE);
        txtRetry.setVisibility(View.GONE);
        GetReviewData(url);

    }


    private void  GetReviewData(String queryURL)
    {
        progressBar.setVisibility(View.VISIBLE);

        try {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, queryURL, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray json = response.getJSONArray("$values");
                        int x = json.length();
                        if(x==0)
                        {
                            txtNoData.setVisibility(View.VISIBLE);
                            txtNoData.setText("No Data for approval");
                        }
                        for(int i = 0; i < x; i++){
                            ActivityData pa = new ActivityData();
                            JSONObject jObj = json.getJSONObject(i);
                            pa.ActivityID = (jObj.getInt("ActivityID"));
                            pa.EmployeeID = (jObj.getInt("EmployeeID"));
                            pa.ProjectID = jObj.getInt("ProjectID");
                            pa.ActivityName = (jObj.getString("ActivityName"));
                            pa.ActivityDescription = (jObj.getString("ActivityDescription"));

                            pa.Employee = (jObj.getString("Employee"));
                            pa.ProjectName =(jObj.getString("ProjectName"));
                            pa.Expenses  = jObj.getInt("Expenses") ;
                            pa.Advance =(jObj.getInt("Advance"));
                            pa.Balance =(jObj.getInt("Balance"));
                            pa.UpdatedOn =(jObj.getString("PaidDate"));
                            // pa.Advance =0;//(jObj.getInt("Advance"));
                            pa.ActivityStatus  = (jObj.getString("ActivityStatus")) ;
                            pa.ApproverName =(jObj.getString("ApproverName"));
                            if(!pa.ActivityStatus.matches("Paid"))
                                approvalData.put(pa.ActivityID,pa);
                        }
                        taskAdapter.notifyDataSetChanged();

                    } catch (JSONException jEx) {
                        // progressBar.setVisibility(View.GONE);
                        txtNoData.setVisibility(View.VISIBLE);
                        txtNoData.setText("Error Parsing Data");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                    txtNoData.setText("Error retreiving Data");
                    txtRetry.setVisibility(View.VISIBLE);
                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
            //*******************************************************************************************************
        } catch (Exception js) {
            progressBar.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
            txtNoData.setText("Netwrok Error");
            txtRetry.setVisibility(View.VISIBLE);
        } finally {

        }
    }

    private class TaskAdapter extends ArrayAdapter<ActivityData>
    {
        LayoutInflater inflat;
        NewViewHolder holder;
        public TaskAdapter(@NonNull Context context, int resource, @NonNull HashMap<Integer,ActivityData> objects) {
            super(context, resource);
            inflat = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return approvalData.size();
        }

        @Nullable
        @Override
        public ActivityData getItem(int position) {
            return (ActivityData)approvalData.values().toArray()[position];
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            try{
                if(convertView == null)
                {
                    convertView = inflat.inflate(R.layout.row_item_approval, null);
                    holder = new NewViewHolder();
                    holder.txtUser =  convertView.findViewById(R.id.txtUser);
                    holder.txtActivity =  convertView.findViewById(R.id.txtActivity);
                    holder.txtExpense =  convertView.findViewById(R.id.txtExpense);
                    holder.txtAdvance =  convertView.findViewById(R.id.txtAdvance);
                    holder.txtTotalExpense = convertView.findViewById(R.id.txtTotalExpense);
                    holder.txtNetBalance = convertView.findViewById(R.id.txtNetBalance);
                    holder.txtProjectName = convertView.findViewById(R.id.txtProjectName);
                    holder.txtStatus =  convertView.findViewById(R.id.txtStatus);
                    holder.imageUser =  convertView.findViewById(R.id.imageUser);
                    holder.imgAction =convertView.findViewById(R.id.imgAction);
                    convertView.setTag(holder);
                }


                NumberFormat format = NumberFormat.getCurrencyInstance();
                format.setCurrency(Currency.getInstance("INR"));

                holder=(NewViewHolder)convertView.getTag();
                ActivityData act = getItem(position);
                holder.txtUser.setText(act.Employee);
                holder.txtExpense.setText(format.format(act.Expenses));
                holder.txtStatus.setText(act.ActivityStatus);
                holder.txtActivity.setText(act.ActivityName + ", ");
                holder.txtProjectName.setText(act.ProjectName);
               // holder.txtDateTime.setText(Utility.ChangeToDateTimeDisplayFormat(act.UpdatedOn));

                holder.txtAdvance.setText(format.format(act.Advance));
                holder.txtTotalExpense.setText(format.format(act.Expenses));
                holder.txtNetBalance.setText(format.format(act.Expenses-act.Advance));

               // holder.txtApprover.setText("Manager: "+act.ApproverName);


                holder.imgAction.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        selectedActivity = getItem(position);
                        v.post(new Runnable() {
                            @Override
                            public void run() {
                                ShowPopupMenu(v);
                            }
                        });


                    }
                });

                DataAccess da = new DataAccess(getApplicationContext());
                da.open();
                String img = da.GetImage(act.EmployeeID);
                da.close();
                if (img.matches("")|| img.matches("null") || img == null) {

                    if (!inProcessList.contains(act.EmployeeID)) {
                        inProcessList.add(act.EmployeeID);
                        GetImages(act.EmployeeID);
                    }
                }
                else
                {
                    Bitmap bmp = ImageServer.getBitmapFromString(img, getApplicationContext());
                    holder.imageUser.setImageBitmap(bmp);
                }

            }
            catch (Exception ex)
            {
                int a=1;

            }

            return convertView;
        }
    }

    private void ShowPopupMenu(View view)
    {
        PopupMenu popup = new PopupMenu(ApprovalActivity.this,view,10);
        //Inflating the Popup using xml file
        if(selectedActivity.ActivityStatus.matches("Submitted")) {
            popup.getMenuInflater().inflate(R.menu.menu_approval_popup, popup.getMenu());
        }
        else if(selectedActivity.ActivityStatus.matches("Approved"))
        {
            popup.getMenuInflater().inflate(R.menu.menu_pay_popup, popup.getMenu());
        }


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.approve:
                        // Remove the item from the adapter
                        UpdateDialog("Approve");
                        return true;
                    case R.id.reject:
                        // Remove the item from the adapter
                        UpdateDialog("Reject");
                        return true;
                    case R.id.pay:
                        Intent payIntent = new Intent(ApprovalActivity.this, PaymentActivity.class);
                        payIntent.putExtra("Activity", selectedActivity);
                        startActivityForResult(payIntent,APP_CONST.REQUEST_PAY_ACTIVITY);
                        return true;
                }
                return false;
            }
        });


        popup.show();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == APP_CONST.REQUEST_PAY_ACTIVITY)
        {
            if(data!=null)
            {
                int id = data.getIntExtra("ActivityID",0);
                approvalData.get(id).ActivityStatus="Paid";
                taskAdapter.notifyDataSetChanged();
            }

        }

    }


    private class NewViewHolder
    {
        TextView txtUser,txtDateTime,txtActivity,txtExpense,txtAdvance,txtApprover,txtStatus,txtTotalExpense,txtNetBalance,txtProjectName;
        OvalImageView imageUser;
        ImageView imgAction;
        int Activity_Id;
    }




    private void UpdateDialog(final String action)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ApprovalActivity.this);
        dialog.setTitle("Update:");

        dialog.setMessage(action + " Activity: " + selectedActivity.ActivityName);


        dialog.setCancelable(false);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                return;
            }
        });

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String status="";
                    if(action.matches("Approve"))
                    {
                        status = "Approved";
                    }
                    else if(action.matches("Reject"))
                    {
                        status = "Rejected";
                    }
                    UpdateStatus(status);
                }
                catch (Exception ex)
                {

                }
            }
        });
        dialog.show();

    }

    private void UpdateStatus(final String status)
    {
        progressBar.setVisibility(View.VISIBLE);
        String reqBody = "{\"ActivityID\":\"" + selectedActivity.ActivityID + "\",\"ItemName\":\"" + "StatusUpdate" + "\",\"ExpenseAmount\":\"" + 0 + "\",\"ReceiveAmount\":" + 0 +
                ",\"ExpenseDescription\":\"" +status + "\",\"ExpenseDate\":\"" + Utility.GetCurrentDateTimeUTC() +"\",\"SelectedRow\":\"" + 0 + "\",\"Status\":\"" + "Added" +  "\"}";

        String url = APP_CONST.APP_SERVER_URL + "api/ExpenseItem/AddItem";

        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        String Response = jObj.getString("Response");
                        if(Response.matches("OK"))
                        {
                            approvalData.get(selectedActivity.ActivityID).ActivityStatus = status;
                            taskAdapter.notifyDataSetChanged();
                        }
                        else if(Response.matches("Fail"))
                        {

                        }


                    } catch (JSONException jEx) {

                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    if(error.networkResponse == null)
                    {

                    }


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


    private void GetImages(final int User_Id)
    {
        String url = APP_CONST.APP_SERVER_URL + "api/Image/" + User_Id ;
        JSONObject jsRequest=null;
        //-------------------------------------------------------------------------------------------------
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObject) {

                try{

                    DataAccess da = new DataAccess(getApplicationContext());
                    da.open();
                    int id = jObject.getInt("UserID");
                    String strImg = jObject.getString("ImageByte");
                    da.insertImage(id,strImg);
                    da.close();
                    //  ActivityData activity =   APP_VARIABLES.approvalData.get(User_Id);
                    // activity.UserImage = strImg;
                    inProcessList.remove(User_Id);
                    taskAdapter.notifyDataSetChanged();
                }
                catch (JSONException e)
                {
                    int a=1;
                }

                catch (Exception ex)
                {
                    int b =8;
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
        jsArrayRequest.setRetryPolicy(rPolicy);
        queue.add(jsArrayRequest);
    }
}
