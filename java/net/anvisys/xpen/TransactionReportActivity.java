package net.anvisys.xpen;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import net.anvisys.xpen.Object.Expense;
import net.anvisys.xpen.Object.Profile;
import net.anvisys.xpen.Object.ProjectData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class TransactionReportActivity extends AppCompatActivity {

    ProjectData selProject;
    ListView transactionListView;
    List<Expense> transactionList;
    ProgressBar progressBar;
    TransactionAdapter transactionAdapter;
    Profile myProfile;
    TextView txtProjectName,txtClientName,txtDeposit,txtWithdraw,noData;
    NumberFormat currFormat;

    int TotalDeposit, TotalWithdraw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Transaction");
        actionBar.show();

        txtProjectName = findViewById(R.id.txtProjectName);
        txtClientName = findViewById(R.id.txtClientName);
        txtDeposit = findViewById(R.id.txtDeposit);
        txtWithdraw = findViewById(R.id.txtWithdraw);

        noData = findViewById(R.id.noData);
        myProfile = Session.GetUser(getApplicationContext());
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        transactionListView = findViewById(R.id.transactionListView);

        currFormat = NumberFormat.getCurrencyInstance();
        currFormat.setCurrency(Currency.getInstance("INR"));

        Intent intent = getIntent();
        String type= intent.getStringExtra("Type");
        if(type.matches("Project")) {
            selProject = intent.getParcelableExtra("Project");

            txtProjectName.setText(selProject.ProjectName);
            txtClientName.setText(", "+ selProject.ClientName);

            transactionList = new ArrayList<>();
            transactionAdapter = new TransactionAdapter(getApplicationContext(), 0, transactionList);
            transactionListView.setAdapter(transactionAdapter);

            GetTransactions();
        }
    }



    private void GetTransactions()
    {
        TotalDeposit = 0;
        TotalWithdraw =0;
        try {
            progressBar.setVisibility(View.VISIBLE);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            String  url = APP_CONST.APP_SERVER_URL + "api/Transaction/Organization/" + myProfile.OrgID +"/" + selProject.ProjectID;

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
                                Expense expense = new Expense();
                                JSONObject jObj = json.getJSONObject(i);
                                expense.ExpenseDate = jObj.getString("TransactionDate");
                                expense.expense_item = jObj.getString("TransName");
                                expense.expense = jObj.getInt("Withdraw");
                                expense.receive = jObj.getInt("Deposit");
                                expense.ExpenseType = jObj.getString("TransType");
                                expense.Remarks = jObj.getString("TransactionRemarks");
                                transactionList.add(expense);

                                TotalDeposit = TotalDeposit +expense.receive;
                                TotalWithdraw =TotalWithdraw + expense.expense;
                            }
                            catch (Exception ex)
                            {
                                int a=1;
                                a++;
                            }

                        }
                        txtDeposit.setText("Depositted: "+ currFormat.format(TotalDeposit));
                        txtWithdraw.setText("Withdrawal: "+ currFormat.format(TotalWithdraw));
                        transactionAdapter.notifyDataSetChanged();

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


    private class TransactionAdapter extends ArrayAdapter<Expense>
    {
        LayoutInflater inflat;
        TableViewHolder holder;
        public TransactionAdapter(@NonNull Context context, int resource, @NonNull List<Expense> objects) {
            super(context, resource, objects);
            inflat = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return transactionList.size();
        }

        @Nullable
        @Override
        public Expense getItem(int position) {
            return transactionList.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            try{
                if(convertView == null)
                {
                    convertView = inflat.inflate(R.layout.row_item_expense, null);
                    holder = new TableViewHolder();
                    holder.txtDate =  convertView.findViewById(R.id.expenseName);
                    holder.tblField2 = convertView.findViewById(R.id.txtDate);
                    holder.tblField3 =  convertView.findViewById(R.id.expenseAmount);
                    holder.tblField4 =  convertView.findViewById(R.id.txtRemarks);
                    holder.txtActivity = convertView.findViewById(R.id.txtActivity);


                    convertView.setTag(holder);
                }

                holder=(TableViewHolder)convertView.getTag();
                Expense act = getItem(position);


                holder.txtDate.setText(Utility.ChangeToDateTimeDisplayFormat(act.ExpenseDate));
                if(act.expense==0)
                {
                    holder.tblField2.setText("Deposit");
                    holder.tblField3.setText(currFormat.format(act.receive));
                }
                else
                {
                    holder.tblField2.setText("Withdraw");
                    holder.tblField3.setText(currFormat.format(act.expense));
                }


                holder.tblField4.setText(act.ExpenseType + " " + act.Remarks);
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
        TextView tblField2,tblField3,tblField4,txtDate,txtActivity;
    }
    private void ShowSnackBar(String message)
    {
        Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}
