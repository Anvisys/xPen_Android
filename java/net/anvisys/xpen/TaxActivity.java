package net.anvisys.xpen;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import net.anvisys.xpen.Common.Utility;
import net.anvisys.xpen.Object.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Currency;

public class TaxActivity extends AppCompatActivity {

    Profile myProfile;
    ProgressBar tdsProgressBar, gstProgressBar;
    TextView tdsMonth,tdsPayable,tdsPaid,previousTDS,currentTDS,penalty;
    TextView gstMonth,payableGST,paidGST,previousGST,currentGST,penaltyGST;
    TextView tdsError,gstError;
    NumberFormat currFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.show();
        actionBar.setTitle("Tax Baseline");

        myProfile = Session.GetUser(this);

        tdsProgressBar = findViewById(R.id.tdsProgressBar);
        tdsProgressBar.setVisibility(View.GONE);
        tdsMonth = findViewById(R.id.tdsMonth);
        tdsPayable = findViewById(R.id.tdsPayable);
        tdsPaid = findViewById(R.id.tdsPaid);
        previousTDS = findViewById(R.id.previousTDS);
        currentTDS = findViewById(R.id.currentTDS);
        penalty = findViewById(R.id.penalty);

        gstProgressBar = findViewById(R.id.gstProgressBar);
        gstProgressBar.setVisibility(View.GONE);
        gstMonth = findViewById(R.id.gstMonth);
        payableGST = findViewById(R.id.payableGST);
        paidGST = findViewById(R.id.paidGST);
        previousGST = findViewById(R.id.previousGST);
        currentGST = findViewById(R.id.currentGST);
        penaltyGST = findViewById(R.id.penaltyGST);

        tdsError = findViewById(R.id.tdsError);
        tdsError.setVisibility(View.GONE);
        gstError = findViewById(R.id.gstError);
        gstError.setVisibility(View.GONE);
        GetTDS();
        GetGST();
        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));
    }



    private void GetTDS()
    {
        try {
            gstProgressBar.setVisibility(View.VISIBLE);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            String  url = APP_CONST.APP_SERVER_URL + "api/Tax/TDS/" + myProfile.OrgID;

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    gstProgressBar.setVisibility(View.GONE);

                    try {
                        String remarks = jObj.getString("TransactionRemarks");
                        if(remarks.matches("NoData"))
                        {
                            tdsError.setVisibility(View.VISIBLE);
                            tdsError.setText("No TDS Data");
                        }
                        else {
                            currFormat = NumberFormat.getCurrencyInstance();
                            currFormat.setCurrency(Currency.getInstance("INR"));

                            int tds_deducted = jObj.getInt("TDSDeducted");
                            int tds_payable = jObj.getInt("TDSPayable");
                            int tds_previous = jObj.getInt("PreviousTDS");
                            int tds_penalty = jObj.getInt("Penalty");
                            String tax_month = jObj.getString("TaxMonth");
                            int tds_paid = jObj.getInt("TDS_Paid");
                           // String entry_date = jObj.getString("EntryDate");
                           // String trans_date = jObj.getString("TransactionDate");
                            tdsMonth.setText(Utility.ChangeToDateOnlyDisplayFormat(tax_month));
                            tdsPayable.setText(currFormat.format(tds_payable));
                            tdsPaid.setText(currFormat.format(tds_paid));
                            previousTDS.setText(currFormat.format(tds_previous));
                            currentTDS.setText(currFormat.format(tds_deducted));
                            penalty.setText(currFormat.format(tds_penalty));
                        }
                    }

                    catch (JSONException jEx) {
                        int a=1;
                        a++;
                    }
                    catch (Exception ex)
                    {
                        int a=1;
                        a++;
                    }


                }



            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    gstProgressBar.setVisibility(View.GONE);
                    tdsError.setVisibility(View.VISIBLE);
                    tdsError.setText("Error Retreiving TDS data");
                }


            });


            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
            //*******************************************************************************************************
        } catch (Exception js) {
            gstProgressBar.setVisibility(View.GONE);
            tdsError.setVisibility(View.VISIBLE);
            tdsError.setText("Error Retreiving TDS data");
        } finally {

        }

    }

    private void GetGST()
    {
        try {
            gstProgressBar.setVisibility(View.VISIBLE);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            String  url = APP_CONST.APP_SERVER_URL + "api/Tax/GST/" + myProfile.OrgID;

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    gstProgressBar.setVisibility(View.GONE);
                    try {
                        String remarks = jObj.getString("TransactionRemarks");
                        if(remarks.matches("NoData"))
                        {
                            gstError.setVisibility(View.VISIBLE);
                            gstError.setText("No GST Data");
                        }
                        else {
                            int gst_received = jObj.getInt("GSTReceived");
                            int gst_input = jObj.getInt("GSTInput");
                            int previous_due = jObj.getInt("PreviousGSTDues");
                            int penalty = jObj.getInt("Penalty");
                            int gst_Payable = jObj.getInt("GSTPayable");
                            String tax_Month = jObj.getString("TaxMonth");
                            int gst_paid = jObj.getInt("GST_Paid");
                            String update_date = jObj.getString("UpdateDate");

                            String transaction_date = jObj.getString("TransactionDate");
                            gstMonth.setText(Utility.ChangeToDateOnlyDisplayFormat(tax_Month));
                            payableGST.setText(Integer.toString(gst_Payable));
                            paidGST.setText(Integer.toString(gst_paid));
                            previousGST.setText(Integer.toString(previous_due));
                            currentGST.setText(Integer.toString(gst_received));
                            penaltyGST.setText(Integer.toString(penalty));
                        }
                    }

                    catch (JSONException jEx) {
                        int a=1;
                        a++;
                    }
                    catch (Exception ex)
                    {
                        int a=1;
                        a++;
                    }


                }



            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    gstProgressBar.setVisibility(View.GONE);
                    gstError.setVisibility(View.VISIBLE);
                    gstError.setText("Error Retreiving GST Data");
                }


            });


            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
            //*******************************************************************************************************
        } catch (Exception js) {
            gstProgressBar.setVisibility(View.GONE);
            gstError.setVisibility(View.VISIBLE);
            gstError.setText("Error Retreiving GST Data");
        } finally {

        }
    }
}
