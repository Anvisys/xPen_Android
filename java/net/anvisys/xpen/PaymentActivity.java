package net.anvisys.xpen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.LinkedHashMap;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    TextView txtActivityName,txtEmployeeName;
    EditText editAmount,editRemarks;
    Spinner spinnerAccounts;
    LinkedHashMap<String, Integer> Accounts = new LinkedHashMap<>();
    List<String> accString = new ArrayList<>();
    Profile myProfile;
    ActivityData activity;
    String selectedAccount;
    int accID;
    ProgressBar progressBar;
    OvalImageView imageView;
    NumberFormat currFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.show();

        actionBar.setTitle("Pay to User");
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        myProfile = Session.GetUser(getApplicationContext());
        spinnerAccounts =  findViewById(R.id.spinnerAccounts);

        txtActivityName = findViewById(R.id.txtActivityName);
        txtEmployeeName = findViewById(R.id.txtEmployeeName);
        imageView = findViewById(R.id.imageView);
        editAmount = findViewById(R.id.editAmount);
        editRemarks = findViewById(R.id.editRemarks);

        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));
        Intent intent = getIntent();

        activity = intent.getParcelableExtra("Activity");
        txtActivityName.setText(activity.ActivityName);
        txtEmployeeName.setText(activity.Employee);

        editAmount.setText(currFormat.format(activity.Expenses-activity.Advance));
        DataAccess da = new DataAccess(getApplicationContext());
        da.open();
        String img = da.GetImage(activity.EmployeeID);
        da.close();
        if (img.matches("")|| img.matches("null") || img == null) {


        }
        else
        {
            Bitmap bmp = ImageServer.getBitmapFromString(img, getApplicationContext());
            imageView.setImageBitmap(bmp);
        }

        GetAccounts();

    }

    private void SetSpinner()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item ,accString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccounts.setAdapter(adapter);



        spinnerAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAccount = spinnerAccounts.getSelectedItem().toString();
                accID = Accounts.get(selectedAccount);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    public void Pay(View v)
    {
        int amount=0;
        if(editAmount.getText().toString().matches("")|| editRemarks.getText().toString().matches(""))
        {
            ShowSnackBar("Empty field");
            return;
        }
        else
        {
            amount = Integer.parseInt(editAmount.getText().toString());
            progressBar.setVisibility(View.VISIBLE);
            /*
            String reqBody = "{\"TransName\":\"" + activity.ActivityName + "\",\"AccID\":\"" + accID + "\",\"Deposit\":\"" + 0 + "\",\"Withdraw\":" + amount +
                ",\"TransactionID\":\"" +0 + "\",\"InvoiceID\":\"" + 0 +"\",\"TransactionRemarks\":\"" + editRemarks.getText().toString() + "\",\"ProjectID\":\"" + activity.ProjectID+
                "\",\"ActivityID\":\"" + activity.ActivityID+ "\",\"OrgID\":\"" + myProfile.OrgID +"\",\"TransType\":\"" + "Expense" +"\",\"TransactionDate\":\"" + Utility.GetCurrentDateTimeUTC()+ "\"}";
            */
            String paymentData = "{\"ProjectID\":\"" + activity.ProjectID + "\",\"ActivityID\":\"" + activity.ActivityID+ "\",\"ExpenseID\":\"" + 0+  "\",\"OrgID\":\"" + myProfile.OrgID +
                    "\",\"PaymentAmount\":\"" + amount + "\",\"AccID\":\"" + accID + "\",\"PaymentName\":\"" + activity.ActivityName + "\",\"Status\":\"" + "Paid" +
                    "\",\"Payment_Remarks\":\"" + editRemarks.getText().toString() + "\",\"PaymentDate\":\"" + Utility.GetCurrentDateTimeUTC()+ "\"}";


            String url = APP_CONST.APP_SERVER_URL + "api/ExpenseItem/AddPayment";

            try {
                JSONObject jsRequest = new JSONObject(paymentData);
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
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("ActivityID",activity.ActivityID);
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }
                            else if(Response.matches("Fail"))
                            {
                                ShowSnackBar("Failed to Update");
                            }

                        } catch (JSONException jEx) {
                            ShowSnackBar("Parsing Error");
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        ShowSnackBar("Network Error");

                    }
                });


                RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
                jsArrayRequest.setRetryPolicy(rPolicy);
                queue.add(jsArrayRequest);


                //*******************************************************************************************************
            } catch (JSONException js) {
                progressBar.setVisibility(View.GONE);
                ShowSnackBar("Network Error");
            } finally {

            }
        }
    }

    private  void GetAccounts()
    {

        String url = APP_CONST.APP_SERVER_URL + "api/Account/Organization/" + myProfile.OrgID;

        try {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray json = response.getJSONArray("$values");
                        int x = json.length();
                        for(int i = 0; i < x; i++){

                            JSONObject jObj = json.getJSONObject(i);
                            int AccID = (jObj.getInt("AccID"));
                            String AccountName = (jObj.getString("AccountName"));
                            accString.add(AccountName);
                            Accounts.put(AccountName,AccID);

                        }
                        SetSpinner();
                    } catch (JSONException jEx) {
                        int a=1;
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
            //*******************************************************************************************************
        } catch (Exception js) {

        } finally {

        }
    }

    private void ShowSnackBar(String message)
    {
        Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
