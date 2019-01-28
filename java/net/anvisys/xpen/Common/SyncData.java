package net.anvisys.xpen.Common;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.xpen.Object.Expense;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SyncData {

    Context mContext;
    public SyncListener listener;
    RequestQueue queue;
    public SyncData(Context mContext) {
        this.mContext = mContext;
    }

    public void StartSyncing()
    {
        //  GetProjectData();
        //GetActivityData();
    }

    public void UpdateExpense()
    {
        queue = Volley.newRequestQueue(mContext);
        DataAccess da = new DataAccess(mContext);
        da.open();
        List<Expense> expenseList = da.GetAllExpenses();

        for (Expense expense: expenseList
                ) {
            if(expense.TranId>=0) {
                if (expense.ExpenseType.matches("Project")) {
                    sendQuickToServer(expense, mContext);
                } else if (expense.ExpenseType.matches("Activity")) {
                    sendExpenseItemToServer(expense, mContext);
                } else if (expense.ExpenseType.matches("Personal")) {
                    sendPersonalToServer(expense, mContext);
                }
            }
        }

        queue.addRequestFinishedListener(new RequestFinished());

    }

    private class RequestFinished implements RequestQueue.RequestFinishedListener
    {

        @Override
        public void onRequestFinished(Request request) {
            if(listener!= null)
                listener.OnExpenseCheckIn();
        }
    }


    private void sendExpenseItemToServer(final Expense tran, final Context context)
    {

        String reqBody = "{\"ActivityID\":\"" + tran.ActivityId + "\",\"ItemName\":\"" + tran.expense_item + "\",\"ExpenseAmount\":\"" + tran.expense + "\",\"ReceiveAmount\":" + 0 +
                ",\"ExpenseDescription\":\"" + tran.Remarks + "\",\"ExpenseDate\":\"" + tran.ExpenseDate +"\",\"SelectedRow\":\"" + 0 + "\",\"Status\":\"" + "Added" +  "\"}";

        String url = APP_CONST.APP_SERVER_URL + "api/ExpenseItem/AddItem";

        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            // RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    try {
                        String Response = jObj.getString("Response");
                        if(Response.matches("OK"))
                        {
                            DataAccess da = new DataAccess(context);
                            da.open();
                            da.DeleteExpense(tran.TranId);
                            da.close();
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

        } finally {

        }

    }

    private void sendPersonalToServer(final Expense tran, final Context context)
    {

        String reqBody = "{\"ExpenseType\":\"" + tran.expense_item + "\",\"ExpenseAmount\":\"" + tran.expense + "\",\"UserID\":" + Session.GetUser(context).UserID +
                ",\"ExpenseDescription\":\"" + tran.Remarks + "\",\"ExpenseDate\":\"" + tran.ExpenseDate +"\",\"SelectedRow\":\"" + 0 + "\",\"Status\":\"" + "Added" +  "\"}";

        String url = APP_CONST.APP_SERVER_URL + "api/Expense";

        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            // RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    try {
                        String Response = jObj.getString("Response");
                        if(Response.matches("OK"))
                        {
                            DataAccess da = new DataAccess(context);
                            da.open();
                            da.DeleteExpense(tran.TranId);
                            da.close();
                            //tran.DataSource = "Server";

                        }
                        else
                        {

                        }



                    } catch (JSONException jEx) {

                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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

        } finally {

        }

    }


    private void sendQuickToServer(final Expense tran, final Context context)
    {

        String reqBody = "{\"ActivityName\":\"" + tran.expense_item + "\",\"ExpenseAmount\":\"" + tran.expense + "\",\"EmployeeID\":" + Session.GetUser(context).UserID +
                ",\"ProjectID\":\"" + tran.ProjectID + "\",\"CreatedBy\":\""+ Session.GetUser(context).UserID+ "\",\"OrgID\":" +Session.GetUser(context).OrgID +
                ",\"ActivityDescription\":\"" + tran.Remarks + "\",\"CreationDate\":\"" + tran.ExpenseDate  + "\",\"ActivityStatus\":\"" + "Submitted" +  "\"}";

        String url = APP_CONST.APP_SERVER_URL + "api/Activity/CreateActivity";

        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            //RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    try {
                        String Response = jObj.getString("Response");
                        if(Response.matches("OK"))
                        {
                            DataAccess da = new DataAccess(context);
                            da.open();
                            da.DeleteExpense(tran.TranId);
                            da.close();
                            // tran.DataSource = "Server";

                        }
                        else
                        {

                        }


                    } catch (JSONException jEx) {
                        int a=1;
                        a++;
                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
            int a=1;
            a++;

        } finally {

        }

    }


    public interface SyncListener
    {
        public void OnExpenseCheckIn();
    }
}
