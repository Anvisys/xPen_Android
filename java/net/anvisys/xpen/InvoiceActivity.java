package net.anvisys.xpen;

import android.content.Context;
import android.content.Intent;
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
import net.anvisys.xpen.Common.Utility;
import net.anvisys.xpen.Object.Profile;
import net.anvisys.xpen.Object.ProjectData;
import net.anvisys.xpen.Object.PurchaseData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {
    ProjectData selProject;
    ListView invoiceListView;
    ProgressBar progressBar;
    List<PurchaseData> invoiceList;
    InvoiceAdapter invoiceAdapter;
    Profile myProfile;
    TextView txtProjectName,totalInvValue,totalReceived,totalCost,totalGst,totalTds,noData;
    Integer TotalReceivable=0,TotalReceived=0,TotalGST=0, TotalTDS=0,TotalCost=0;
    NumberFormat currFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Invoices");
        actionBar.show();

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(InvoiceActivity.this, SellInvoiceActivity.class);
                addIntent.putExtra("Type","Sell");
                addIntent.putExtra("Project",selProject);
                startActivity(addIntent);
            }
        });
        txtProjectName = findViewById(R.id.txtProjectName);
        totalInvValue = findViewById(R.id.totalInvValue);
        totalReceived = findViewById(R.id.totalReceived);
        totalCost = findViewById(R.id.totalCost);
        totalGst = findViewById(R.id.totalGst);
        totalTds = findViewById(R.id.totalTds);
        noData = findViewById(R.id.noData);
        myProfile = Session.GetUser(getApplicationContext());
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));



        invoiceListView = findViewById(R.id.invoiceListView);

        Intent intent = getIntent();
        String type= intent.getStringExtra("Type");
        if(type.matches("Project")) {
            selProject = intent.getParcelableExtra("Project");
            invoiceList = new ArrayList<>();
            invoiceAdapter = new InvoiceAdapter(getApplicationContext(), 0, invoiceList);
            invoiceListView.setAdapter(invoiceAdapter);
            SetSummary();

            GetInvoices();
        }
    }
    private void GetInvoices()
    {
        try {
            TotalReceivable=0;
            TotalReceived=0;
            TotalCost=0;
            TotalGST=0;
            TotalTDS=0;
            progressBar.setVisibility(View.VISIBLE);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            String  url = APP_CONST.APP_SERVER_URL + "api/Invoice/Sell/Organization/" + myProfile.OrgID +"/Project/" + selProject.ProjectID;

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray json = response.getJSONArray("$values");
                        int x = json.length();
                        if (x==0)
                        {
                            noData.setVisibility(View.VISIBLE);
                            noData.setText("No Invoice for Selected Project");
                        }
                        for(int i = 0; i < x; i++){
                            try {
                                PurchaseData purchase = new PurchaseData();
                                JSONObject jObj = json.getJSONObject(i);
                                purchase.InvoiceNumber = jObj.getString("InvoiceNumber");
                                purchase.InvoiceDate = jObj.getString("InvoiceDate");
                                purchase.ServiceCost = jObj.getInt("ServiceCost");
                                purchase.CGST = jObj.getInt("CGST");
                                purchase.SGST = jObj.getInt("SGST");
                                purchase.IGST = jObj.getInt("IGST");
                                purchase.TDS = jObj.getInt("TDS");
                                purchase.PaidAmount = jObj.getInt("ReceivedAmount");
                                purchase.PaidDate = jObj.getString("ReceivedDate");
                                invoiceList.add(purchase);

                                TotalReceivable = TotalReceivable +  purchase.ServiceCost;
                                TotalReceived = TotalReceived +  purchase.PaidAmount;
                                TotalCost = TotalCost +purchase.ServiceCost + purchase.CGST +purchase.SGST;
                                TotalTDS = TotalTDS +  purchase.TDS;
                                TotalGST =  purchase.CGST + purchase.SGST + purchase.IGST;
                            }
                            catch (Exception ex)
                            {
                                int a=1;
                                a++;
                            }
                            invoiceAdapter.notifyDataSetChanged();

                            SetSummary();
                        }


                    } catch (JSONException jEx) {
                        int a=1;
                        a++;
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

    private void  SetSummary()
    {
        txtProjectName.setText(""+ selProject.ProjectName +", " + selProject.ClientName);
        totalInvValue.setText("Receivable:\n " +currFormat.format( TotalReceivable ));
        totalReceived.setText("Received:\n " + currFormat.format( TotalReceived));
        totalCost.setText(currFormat.format(TotalCost));
        totalGst.setText(currFormat.format( TotalGST));
        totalTds.setText(currFormat.format(TotalTDS));

    }

    private class InvoiceAdapter extends ArrayAdapter<PurchaseData>
    {
        LayoutInflater inflat;
        TableViewHolder holder;
        public InvoiceAdapter(@NonNull Context context, int resource, @NonNull List<PurchaseData> objects) {
            super(context, resource, objects);
            inflat = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return invoiceList.size();
        }

        @Nullable
        @Override
        public PurchaseData getItem(int position) {
            return invoiceList.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            try{
                if(convertView == null)
                {
                    convertView = inflat.inflate(R.layout.row_item_invoice, null);
                    holder = new TableViewHolder();
                    holder.txtday =  convertView.findViewById(R.id.txtday);
                    holder.txtmonth = convertView.findViewById(R.id.txtmonth);
                    holder.txtyear = convertView.findViewById(R.id.txtyear);
                    holder.txtInvValue = convertView.findViewById(R.id.txtInvValue);
                    holder.txtInvReceived =  convertView.findViewById(R.id.txtInvReceived);
                    holder.txtInvNumber =  convertView.findViewById(R.id.txtInvNumber);
                    holder.txtCost =  convertView.findViewById(R.id.txtCost);
                    holder.txtGst =  convertView.findViewById(R.id.txtGst);
                    holder.txtTds =  convertView.findViewById(R.id.txtTds);
                    holder.txtInvRemaining= convertView.findViewById(R.id.txtInvRemaining);
                    convertView.setTag(holder);
                }

                holder=(TableViewHolder)convertView.getTag();
                PurchaseData pur = getItem(position);

                holder.txtInvNumber.setText(""+ pur.InvoiceNumber);
                holder.txtCost.setText("Cost\n "+currFormat.format(pur.ServiceCost));
                holder.txtInvReceived.setText("Received:\n "+currFormat.format(pur.PaidAmount));
                holder.txtInvRemaining.setText("Remaining:\n "+currFormat.format(pur.ServiceCost+pur.IGST + pur.CGST + pur.SGST - pur.PaidAmount));
                holder.txtTds.setText("TDS\n "+ currFormat.format(pur.TDS));
                holder.txtGst.setText("GST\n "+currFormat.format(pur.IGST + pur.CGST + pur.SGST));
                holder.txtday.setText(""+Utility.GetDayOnly(pur.InvoiceDate));
                holder.txtmonth.setText(""+Utility.GetMonthOnly(pur.InvoiceDate));
                holder.txtyear.setText(""+Utility.GetYearOnly(pur.InvoiceDate));
                int balance = pur.ServiceCost+pur.IGST + pur.CGST + pur.SGST - pur.TDS - pur.PaidAmount;
                if(balance>10)
                {
                    convertView.setBackground(getDrawable(R.drawable.background_card_delay));
                }
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
        TextView txtInvValue, txtInvReceived,txtInvRemaining,txtInvNumber,txtCost,txtGst,txtTds,txtday,txtmonth,txtyear;
    }
    private void ShowSnackBar(String message)
    {
        Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
