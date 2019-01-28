package net.anvisys.xpen.Login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import net.anvisys.xpen.DashboardActivity;
import net.anvisys.xpen.Object.Profile;
import net.anvisys.xpen.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    EditText txtOrgName,txtEmplyeeCount,txtUserName,txtUserMobile,txtUserEMail,txtCity;
    Button btnRegister;
    Profile newProfile;
    String plan;
    TextView planTitle,btnPlan;
    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        try {

            txtOrgName = view.findViewById(R.id.txtOrgName);
            txtEmplyeeCount =  view.findViewById(R.id.txtEmplyeeCount);
            txtUserName =  view.findViewById(R.id.txtUserName);
            txtUserMobile =  view.findViewById(R.id.txtUserMobile);
            txtUserEMail =  view.findViewById(R.id.txtUserEMail);
            planTitle=view.findViewById(R.id.planName);
            btnPlan=view.findViewById(R.id.change);
            txtCity =  view.findViewById(R.id.txtCity);
            btnRegister = view.findViewById(R.id.btnRegister);
            progressBar =  view.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);

            btnPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    PlanFragment PLAN = new PlanFragment();
                    fragmentTransaction.replace(R.id.regisrter, PLAN);
                    fragmentTransaction.commit();
                }
            });


            Bundle bundle = this.getArguments();

            if (bundle != null) {
                plan = bundle.getString("plan");
            }
            if (plan.matches("Individual")) {
                txtOrgName.setVisibility(View.GONE);
                txtEmplyeeCount.setVisibility(View.GONE);
                txtCity.setVisibility(View.GONE);
                planTitle.setText("Individual Plan");
            }
            else if (plan.matches("Web Industrial")){
                planTitle.setText("Web Inadustrial Plan");
            }
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (IsDataValid()) RegisterUser(getContext());
                }
            });
        }catch (Exception ex)
        {
            int a=1;
        }

        return view;
    }



    private boolean IsDataValid()
    {
        newProfile = new Profile();
        newProfile.OrganizationName = txtOrgName.getText().toString();
        newProfile.NAME = txtUserName.getText().toString();
        newProfile.E_MAIL = txtUserEMail.getText().toString();
        newProfile.MOB_NUMBER = txtUserMobile.getText().toString();


        if(plan.matches("Enterprise") || plan.matches("WebCorporate")) {
            if (newProfile.OrganizationName.matches("")) {
                txtOrgName.setFocusable(true);
                return false;
            }
        }
        else
        {
            newProfile.OrganizationName = "Individual";
            newProfile.Role = "Individual";
            newProfile.OrgID = "0";
        }

        if (newProfile.NAME.matches("")) {
            txtUserName.setFocusable(true);
            return false;
        } else if (newProfile.E_MAIL.matches("")) {
            txtUserEMail.setFocusable(true);
            return false;
        } else if (newProfile.MOB_NUMBER.matches("")) {
            txtUserMobile.setFocusable(true);
            return false;
        } else {
            return true;
        }


    }



    private void RegisterUser(Context context)
    {
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);
        int RequestID=1;
        String url = APP_CONST.APP_SERVER_URL + "api/User/AddUser";
        String date = "";
        try {
            String reqBody =  "{\"UserLogin\":\"" +newProfile.E_MAIL+"\", \"OrganizationID\": 0, \"Password\": \"Password@123\", \"Role\": \"Individual\", \"UserName\":\""+ newProfile.NAME+
                    "\",\"UserEmail\":\""+ newProfile.E_MAIL +"\",\"UserMobile\":\""+ newProfile.MOB_NUMBER+ "\",\"Status\":\"Active\",\"RegisterDate\":\""+ date+
                    "\",\"OrgName\":\"Individual\",\"SolutionType\":\"Individual\"}";


            JSONObject jsRequest = new JSONObject(reqBody);
            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject object) {
                    try {
                        if(object.getString("Response").matches("Invald"))
                        {
                            if(object.getString("IsMail").matches("false"))
                            {
                                ShowSnackBar("EMail is already registered");
                            }
                            if(object.getString("IsMail").matches("false"))
                            {
                                ShowSnackBar("EMail is already registered");
                            }

                        }
                        else if(object.getString("Response").matches("OK"))
                        {

                            Session.AddUser(getContext(),newProfile);
                            Intent dashboardIntent = new Intent(getContext(), DashboardActivity.class);
                            startActivity(dashboardIntent);
                            getActivity().finish();
                        }
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);

                    } catch (JSONException jEx) {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                }
            });
            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);
            //*******************************************************************************************************
        }
        catch (JSONException jex)
        {
            int a=1;
            a++;
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
        }

        catch (Exception js) {
            int a=1;
            a++;
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
        } finally {

        }

    }

    private void ShowSnackBar(String msg)
    {
        Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }



}
