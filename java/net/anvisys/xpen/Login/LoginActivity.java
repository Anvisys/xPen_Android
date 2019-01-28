package net.anvisys.xpen.Login;

import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import net.anvisys.xpen.MainActivity;
import net.anvisys.xpen.Object.Profile;
import net.anvisys.xpen.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    ImageView btnImage;
    TextView textLogin, textPassword,name;
    ProgressBar prgBar;
    Button btnLogin,btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textLogin = findViewById(R.id.txtLogin);
        textPassword = findViewById(R.id.txtPassword);
        prgBar = findViewById(R.id.progressBar);
        prgBar.setVisibility(View.GONE);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnImage = findViewById(R.id.profile);
        name=findViewById(R.id.name);
    }

    public void Login(View v)
    {
        validate();

    }

    public void Register(View v)
    {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }


    private void validate()
    {
        btnRegister.setEnabled(false);
        btnLogin.setEnabled(false);
        prgBar.setVisibility(View.VISIBLE);

        String login = textLogin.getText().toString();
        String password = textPassword.getText().toString();


        String reqBody = "{\"User_Login\":\"" + login + "\",\"User_Password\":\"" + password + "\"}";

        String url = APP_CONST.APP_SERVER_URL + "api/User/Validate";

        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {
                    try {
                        String UserName = jObj.getString("UserName");
                        if(UserName.matches("Error"))
                        {
                            Snackbar.make(getCurrentFocus(), "Validation failed", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }
                        else
                        {
                            Profile myProfile = new Profile();
                            myProfile.UserID= Integer.toString(jObj.getInt("UserId"));
                            myProfile.NAME= jObj.getString("UserName");
                            myProfile.E_MAIL= jObj.getString("UserEmail");
                            myProfile.MOB_NUMBER= jObj.getString("UserMobile");
                            myProfile.Role= jObj.getString("UserRole");
                            myProfile.OrgID=  Integer.toString(jObj.getInt("OrgId"));
                            myProfile.OrganizationName= jObj.getString("OrgName");

                            Session.AddUser(getApplicationContext(),myProfile);
                            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(mainIntent);
                            LoginActivity.this.finish();
                        }
                        btnRegister.setEnabled(true);
                        btnLogin.setEnabled(true);
                        prgBar.setVisibility(View.GONE);

                    } catch (JSONException jEx) {
                        btnRegister.setEnabled(true);
                        btnLogin.setEnabled(true);
                        prgBar.setVisibility(View.GONE);
                        Snackbar.make(getCurrentFocus(), "Failed to read Data", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    btnRegister.setEnabled(true);
                    btnLogin.setEnabled(true);
                    prgBar.setVisibility(View.GONE);
                    Snackbar.make(getCurrentFocus(), "Validation failed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                }
            });


            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);


            //*******************************************************************************************************
        } catch (JSONException js) {
            btnRegister.setEnabled(true);
            btnLogin.setEnabled(true);
            prgBar.setVisibility(View.GONE);
        }
        catch (Exception ex)
        {
            btnRegister.setEnabled(true);
            btnLogin.setEnabled(true);
            prgBar.setVisibility(View.GONE);
            Snackbar.make(getCurrentFocus(), ex.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        finally {

        }


    }
}
