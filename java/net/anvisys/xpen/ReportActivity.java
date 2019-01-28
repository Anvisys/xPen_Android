package net.anvisys.xpen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import net.anvisys.xpen.Common.ImageServer;
import net.anvisys.xpen.Common.Utility;
import net.anvisys.xpen.Object.ActivityData;
import net.anvisys.xpen.Object.Expense;
import net.anvisys.xpen.Object.ProjectData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    List<Expense> expenseList = new ArrayList<>();
    ListView expenseListView;
    ExpenseAdapter expenseAdapter;
    String url="",selectedFilterType;
    ProgressBar progressBar;
    Button btnSubmit,projectstatus;
    ActivityData currentActivity;
    TextView txtError,txtRetry;
    TextView txtName,txtExpense,txtReceived,txtStatus,txtProjectname,txtManager;
    int TotalExpense=0, TotalReceived=0;
    String CurrentStatus="";
    NumberFormat currFormat;
    ProjectData currentProject;
    ImageView txtManagerProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("ExpenseReport");
        actionBar.show();

        txtName = findViewById(R.id.txtName);
        txtExpense = findViewById(R.id.txtExpense);
        txtReceived = findViewById(R.id.txtReceived);
        txtStatus = findViewById(R.id.txtStatus);
        txtProjectname = findViewById(R.id.txtProjectname);
        txtManager = findViewById(R.id.txtManager);
        txtManagerProfile= findViewById(R.id.txtManagerProfile);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitActivity();
            }
        });


        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));


        txtError = findViewById(R.id.txtError);
        txtError.setVisibility(View.GONE);
        txtRetry=findViewById(R.id.txtRetry);
        txtRetry.setVisibility(View.GONE);
        try {
            Intent intent = getIntent();
            url = intent.getStringExtra("url");
            selectedFilterType=  intent.getStringExtra("Type");
            if(selectedFilterType.matches("Activity"))
            {
                currentActivity = intent.getParcelableExtra("Activity");
                txtName.setText(currentActivity.ActivityName+",  ");
                txtProjectname.setText(currentActivity.ProjectName);
                txtManager.setText(currentActivity.ApproverName);

                //txtManagerProfile.setImageBitmap(currentActivity.UserImage);

                if(currentActivity.ActivityStatus.matches("Added"))
                {
                    currentProject = intent.getParcelableExtra("Project");
                    txtName.setText(currentProject.ProjectName+",  ");
                    txtProjectname.setText(currentProject.ClientName);
                    txtManager.setText(currentProject.Approver);
                }


            }
            else if (selectedFilterType.matches("Project"))
            {
                currentProject = intent.getParcelableExtra("Project");
                txtName.setText(currentProject.ProjectName+",  ");
                txtProjectname.setText(currentProject.ClientName);
                txtManager.setText(currentProject.Approver);
            }
            expenseListView = findViewById(R.id.expenseListView);
            expenseAdapter = new ExpenseAdapter(getApplicationContext(), 0, expenseList);
            expenseListView.setAdapter(expenseAdapter);
            expenseAdapter.notifyDataSetChanged();
            GetData(url);
            //btnSubmit.setVisibility(View.VISIBLE);
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            if(selectedFilterType.matches("Activity")) {
                if (currentActivity.ActivityStatus.matches("Added"))
                {
                    inflater.inflate(R.menu.menu_activity, menu);

                }
            }
        }
      catch (Exception ex){}

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuSubmit) {

            SubmitActivity();

            return true;
        }
        else
        {
            return false;
        }

    }


    public void Retry(View v)
    {
        if(!url.matches(""))
        {
            txtError.setVisibility(View.GONE);
            txtRetry.setVisibility(View.GONE);
            expenseListView.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
            GetData(url);
        }
        else
        {
            ReportActivity.this.finish();
        }

    }

    public void GetData(String qureyURL)
    {
        try {
           TotalExpense=0; TotalReceived=0;
            CurrentStatus="";
            progressBar.setVisibility(View.VISIBLE);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, qureyURL, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray json = response.getJSONArray("$values");
                        int x = json.length();
                        if(x==0)
                        {
                            txtError.setVisibility(View.VISIBLE);
                            txtError.setText("No Data to display");
                        }
                        for(int i = 0; i < x; i++){
                            try {
                                Expense expense = new Expense();
                                JSONObject jObj = json.getJSONObject(i);
                                expense.Status = jObj.getString("Status");
                                expense.expense = jObj.getInt("ExpenseAmount");
                                TotalExpense = TotalExpense + expense.expense;
                                expense.receive = jObj.getInt("ReceiveAmount");
                                TotalReceived = TotalReceived + expense.receive;

                                expense.ExpenseDate = jObj.getString("ExpenseDate");
                                expense.Remarks = jObj.getString("ExpenseDescription");
                                if (selectedFilterType.matches("Personal")) {
                                    expense.expense_item = jObj.getString("ExpenseType");
                                    expense.ActivityName = "Personal";

                                } else {
                                    expense.expense_item = jObj.getString("ItemName");
                                    expense.ActivityName = jObj.getString("ActivityName");
                                }
                                expenseList.add(expense);
                                CurrentStatus = expense.Status;
                            }

                            catch (Exception ex)
                            {
                                int a=1;
                                a++;
                            }
                        }


                        txtExpense.setText("Expenses\n"+currFormat.format(TotalExpense));
                        txtReceived.setText("Received\n"+currFormat.format(TotalReceived));
                        txtStatus.setText(CurrentStatus);
                        expenseAdapter.notifyDataSetChanged();
                        if (CurrentStatus.matches("Submitted")){
                            btnSubmit.setVisibility(View.GONE);
                        }

                    } catch (JSONException jEx) {
                        int a=1;
                        a++;
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText("Error Parsing Data");
                    }



                }



            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText("Error Occured");
                    txtRetry.setVisibility(View.VISIBLE);
                    expenseListView.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.GONE);
                }


            });


            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
            //*******************************************************************************************************
        } catch (Exception js) {
            progressBar.setVisibility(View.GONE);
            txtError.setVisibility(View.VISIBLE);
            txtError.setText("Error Occured");
            txtRetry.setVisibility(View.VISIBLE);
            expenseListView.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.GONE);

        } finally {

        }
    }


    private class ExpenseAdapter extends ArrayAdapter<Expense>
    {
        LayoutInflater inflat;
        TableViewHolder holder;
        public ExpenseAdapter(@NonNull Context context, int resource, @NonNull List<Expense> objects) {
            super(context, resource, objects);
            inflat = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return expenseList.size();
        }

        @Nullable
        @Override
        public Expense getItem(int position) {
            return expenseList.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            try{
                if(convertView == null)
                {
                    convertView = inflat.inflate(R.layout.row_item_expense, null);
                    holder = new TableViewHolder();
                    holder.tblField1 =  convertView.findViewById(R.id.expenseName);
                    holder.tblField2 = convertView.findViewById(R.id.expenseAmount);
                    holder.tblField3 =  convertView.findViewById(R.id.txtRemarks);
                    holder.tblField4 =  convertView.findViewById(R.id.txtDate);
                    holder.txtActivity = convertView.findViewById(R.id.txtActivity);
                    convertView.setTag(holder);
                }


                holder=(TableViewHolder)convertView.getTag();
                Expense act = getItem(position);

                holder.tblField1.setText(" "+act.expense_item);
                holder.tblField2.setText(currFormat.format(act.expense));
                holder.tblField3.setText(" " + act.Remarks);
                holder.tblField4.setText(Utility.ChangeToDateTimeDisplayFormat("  "+act.ExpenseDate));
                holder.txtActivity.setText("  "+act.ActivityName);
            }
            catch (Exception ex)
            {
                int a=1;

            }

            return convertView;
        }
    }

    private class TableViewHolder
    {
        TextView tblField1,tblField2,tblField3,tblField4,txtActivity;
    }
    private void ShowSnackBar(String message)
    {
        Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void SubmitActivity()
    {
        progressBar.setVisibility(View.VISIBLE);

        String reqBody = "{\"ActivityID\":\"" + currentActivity.ActivityID + "\",\"ActivityName\":\"" + currentActivity.ActivityName + "\",\"ProjectID\":\"" + currentActivity.ProjectID + "\",\"ItemName\":\"" + "StatusUpdate" + "\",\"ExpenseAmount\":\"" + 0 + "\",\"ReceiveAmount\":" + 0 +
                ",\"ExpenseDescription\":\"" + "Submitted" + "\",\"ExpenseDate\":\"" + Utility.GetCurrentDateTimeUTC() +"\",\"SelectedRow\":\"" + 0 + "\",\"Status\":\"" + "Submitted" +  "\"}";

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
                            ReportActivity.this.finish();
                            btnSubmit.setVisibility(View.GONE);
                        }
                        else
                        {

                        }
                        invalidateOptionsMenu();

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





}
