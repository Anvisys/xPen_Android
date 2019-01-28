package net.anvisys.xpen;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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
import net.anvisys.xpen.Object.ProjectData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SellInvoiceActivity extends AppCompatActivity {


    TextView clientName,projectName,editDate;
    ProjectData selProject;
    EditText editInvoiceNumber,editServiceCost,editTDS,editCGST,editSGST,editIGST;
    private android.app.DatePickerDialog DatePickerDialog;
    Calendar calSelDateTime;
    Profile myProfile;
    String strInvoiceDateTime;
    ProgressBar progressBar;
    String InvoiceType,url;
    RadioGroup  radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_invoice);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.show();

        clientName = findViewById(R.id.clientName);
        projectName = findViewById(R.id.projectName);

        myProfile = Session.GetUser(this);
        Intent intent = getIntent();
        selProject = intent.getParcelableExtra("Project");
        InvoiceType = intent.getStringExtra("Type");

        if(InvoiceType.matches("Sell"))
        {
            actionBar.setTitle("Add Sell Invoice");
            clientName.setText(""+selProject.ClientName);
            url = APP_CONST.APP_SERVER_URL + "api/Invoice/SellInvoice";
        }
        else if(InvoiceType.matches("Purchase"))
        {
            actionBar.setTitle("Purchase Invoice");
            clientName.setText("Project Number: "+selProject.ProjectNumber);
            url = APP_CONST.APP_SERVER_URL + "api/Invoice/PurchaseInvoice";
        }

        projectName.setText(""+selProject.ProjectName);

        editInvoiceNumber = findViewById(R.id.editInvoiceNumber);
        editServiceCost = findViewById(R.id.editServiceCost);
        editTDS = findViewById(R.id.editTDS);
        editCGST = findViewById(R.id.editCGST);
        editSGST = findViewById(R.id.editSGST);
        editIGST = findViewById(R.id.editIGST);

        editDate = findViewById(R.id.editDate);

        calSelDateTime = Calendar.getInstance(Locale.US);
        editDate.setText(Utility.ChangeToDateOnlyDisplayFormat(Utility.GetCurrentDateTimeLocal()));
        strInvoiceDateTime = Utility.GetCurrentDateTimeUTC();
    }

    public void Save(View v)
    {

        String invoice_number = editInvoiceNumber.getText().toString();
        String service_cost = editServiceCost.getText().toString();
        String tds = editTDS.getText().toString();
        String cgst = editCGST.getText().toString();
        String sgst = editSGST.getText().toString();
        String igst = editIGST.getText().toString();

        if(service_cost.matches(""))
        {
            ShowSnackBar("Service Cost is mandatory");
            return;
        }

        if(invoice_number.matches(""))
        {
            ShowSnackBar("Invoice Number is mandatory");
            return;
        }
        if(tds.matches(""))
        {
            tds ="0";
        }

        if(igst.matches(""))
        {
            igst ="0";
        }
        if(cgst.matches(""))
        {
            cgst ="0";
        }

        if(sgst.matches(""))
        {
            sgst ="0";
        }
        String Invoice = "{\"InvoiceNumber\":\"" + invoice_number + "\",\"OrgId\":\"" + myProfile.OrgID + "\",\"ProjectId\":\"" + selProject.ProjectID+ "\",\"ServiceCost\":" + service_cost+
                ",\"CGST\":\"" + cgst + "\",\"SGST\":\"" + sgst +"\",\"IGST\":\"" + igst + "\",\"TDS\":\""+ tds +"\",\"InvoiceDate\":\"" + strInvoiceDateTime +"\",\"InvoiceType\":\"" + 0+ "\"}";



        try {
            progressBar.setVisibility(View.VISIBLE);
            JSONObject jsRequest = new JSONObject(Invoice);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        String Response = jObj.getString("Response");
                        if(Response.matches("OK"))
                        {
                            SellInvoiceActivity.this.finish();
                        }
                        else if(Response.matches("Fail"))
                        {
                            ShowSnackBar("Error Creating Invoice, Try Again");
                        }


                    } catch (JSONException jEx) {

                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse == null)
                    {
                        progressBar.setVisibility(View.GONE);
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

    public void EditDate(View v)
    {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                //Calendar newDate = Calendar.getInstance();
                calSelDateTime.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
                TimeZone UTCtimeZone = TimeZone.getTimeZone("UTC");
                newDateFormat.setTimeZone(UTCtimeZone);
                strInvoiceDateTime = newDateFormat.format(calSelDateTime.getTime());

                SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());


                editDate.setText(Utility.ChangeToDateOnlyDisplayFormat(localDateFormat.format(calSelDateTime.getTime())));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        DatePickerDialog.show();

    }


    private void ShowSnackBar(String message)
    {
        Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
