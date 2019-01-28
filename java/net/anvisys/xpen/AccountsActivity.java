package net.anvisys.xpen;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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
import net.anvisys.xpen.Common.APP_VARIABLES;
import net.anvisys.xpen.Common.Session;
import net.anvisys.xpen.Common.Utility;
import net.anvisys.xpen.Object.Profile;
import net.anvisys.xpen.Object.TransactionData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class AccountsActivity extends AppCompatActivity {
    Spinner spinnerAccounts;
    Profile myProfile;
    LinkedHashMap<Integer, String> Accounts = new LinkedHashMap<>();
    List<String> accString = new ArrayList<>();
    List<TransactionData> transationDataList= new ArrayList<>();
    ListView listViewTransaction;
    String[] category= { "football","cricket" ,"baseball"};
    int selYear, selMonth;
    TextView transactionMonth;
    ProgressBar progressBar;
    TransactionAdapter transAdapter;
    private android.app.DatePickerDialog monthDialog;
    int TotalWithdraw=0, TotalDeposit=0, Balance=0;
    TextView txtWithdraw,txtDeposit,txtBalance;
    String strINR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.show();

        actionBar.setTitle("Transaction");

        txtWithdraw = findViewById(R.id.txtWithdraw);
        txtDeposit =  findViewById(R.id.txtDeposit);
        txtBalance = findViewById(R.id.txtBalance);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        Calendar date = Calendar.getInstance();
        selYear = date.get(Calendar.YEAR);
        selMonth = date.get(Calendar.MONTH);

        transactionMonth = findViewById(R.id.transactionMonth);
        transactionMonth.setText(Utility.ChangeToMonthDisplayFormat(Utility.GetCurrentDateTimeLocal()));

        listViewTransaction =findViewById(R.id.listViewTransaction);
        transAdapter = new TransactionAdapter(getApplicationContext(),0, transationDataList);
        listViewTransaction.setAdapter(transAdapter);
        myProfile = Session.GetUser(this);
        strINR = APP_VARIABLES.getCurrencySymbol("INR");
        spinnerAccounts = findViewById(R.id.spinnerAccounts);
        accString.add("ShowAll");
        Accounts.put(0,"ShowAll");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item ,accString);

        // ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),Accounts.values().toArray(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccounts.setAdapter(adapter);
        spinnerAccounts.setSelection(0);

        String url = APP_CONST.APP_SERVER_URL + "api/Transaction/Organization/" + myProfile.OrgID +"/Year/" + selYear + "/Month/" + selMonth;
        GetAccounts();
        GetTransaction(url);
    }


    public void FIndTransaction(View v)
    {
        String url = APP_CONST.APP_SERVER_URL + "api/Transaction/Organization/" + myProfile.OrgID +"/Year/" + selYear + "/Month/" + selMonth;
        GetTransaction(url);
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

                            Accounts.put(AccID,AccountName);

                        }

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

    private void GetTransaction(String queryURL)
    {
        progressBar.setVisibility(View.VISIBLE);

        try {
            TotalDeposit=0;
            TotalWithdraw=0;
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, queryURL, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray json = response.getJSONArray("$values");
                        int x = json.length();
                        transationDataList= new ArrayList<>();
                        for(int i = 0; i < x; i++) {
                            TransactionData trans = new TransactionData();
                            JSONObject jObj = json.getJSONObject(i);
                            trans.AccID = jObj.getInt("AccID");
                            trans.AccountName = jObj.getString("AccountName");
                            trans.TransactionDate = jObj.getString("TransactionDate");
                            trans.TransID = jObj.getInt("TransID");
                            trans.Deposit = jObj.getInt("Deposit");
                            trans.Withdraw = jObj.getInt("Withdraw");
                            trans.Balance = jObj.getInt("Balance");
                            trans.AccountBalance = (jObj.getInt("AccountBalance"));
                            trans.TransactionName = (jObj.getString("TransName"));
                            trans.TransactionRemarks = (jObj.getString("TransactionRemarks"));
                            trans.TransactionType = (jObj.getString("TransType"));
                            transationDataList.add(trans);
                            TotalDeposit = TotalDeposit + trans.Deposit;
                            TotalWithdraw = TotalWithdraw + trans.Withdraw;
                            Balance =trans.Balance;
                        }


                        NumberFormat format = NumberFormat.getCurrencyInstance();
                        format.setCurrency(Currency.getInstance("INR"));
                        //String USD = APP_VARIABLES.getCurrencySymbol("USD");
                        txtWithdraw.setText(format.format(TotalWithdraw));
                        txtDeposit.setText( format.format(TotalDeposit));



                       // txtDeposit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupees, 0, 0, 0);
                        txtBalance.setText(format.format(Balance));
                        transAdapter.notifyDataSetChanged();

                    } catch (JSONException jEx) {
                        // progressBar.setVisibility(View.GONE);
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



    private class TransactionAdapter extends ArrayAdapter<TransactionData>
    {
        LayoutInflater inflat;
        TableViewHolder holder;
        public TransactionAdapter(@NonNull Context context, int resource, @NonNull List<TransactionData> objects) {
            super(context, resource, objects);
            inflat = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return transationDataList.size();
        }

        @Nullable
        @Override
        public TransactionData getItem(int position) {
            return transationDataList.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            try{
                if(convertView == null)
                {
                    convertView = inflat.inflate(R.layout.row_item_account, null);
                    holder = new TableViewHolder();
                    holder.expenseName =  convertView.findViewById(R.id.expenseName);
                    holder.txtDate = convertView.findViewById(R.id.txtDate);
                    holder.expenseAmount =  convertView.findViewById(R.id.expenseAmount);
                    holder.txtRemarks =  convertView.findViewById(R.id.txtRemarks);
                    holder.txtAccountName =  convertView.findViewById(R.id.txtAccountName);

                    convertView.setTag(holder);
                }

                holder=(TableViewHolder)convertView.getTag();
                TransactionData trans = getItem(position);

                holder.txtDate.setText(Utility.ChangeToDateTimeDisplayFormat(trans.TransactionDate));
                holder.expenseName.setText(trans.TransactionName);
                holder.expenseAmount.setText( strINR +" "+ Integer.toString(trans.Withdraw));
                holder.txtAccountName.setText(trans.AccountName);
                holder.txtRemarks.setText(trans.TransactionRemarks);


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
        TextView txtAccountName,txtRemarks,expenseName,txtDate,expenseAmount;
    }


    public void SelectMonth(View v)
    {

        Calendar newCalendar = Calendar.getInstance();

        monthDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();

                newDate.set(year, monthOfYear, dayOfMonth);
                selYear = newDate.get(Calendar.YEAR);
                selMonth = newDate.get(Calendar.MONTH);

                transactionMonth = findViewById(R.id.transactionMonth);
                transactionMonth.setText(Utility.ChangeToMonthDisplayFormat(Utility.GetDateToString(  newDate.getTime())));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        monthDialog.show();
    }
}

