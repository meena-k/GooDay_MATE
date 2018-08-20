package com.example.mate.gooday_mate;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mate.gooday_mate.service.Config;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //Defining views
    private String LOGIN_URL = Config.URL + "login.php";
    private EditText edit_id, edit_pw, edit_email;

    HashMap<String, String> userMap;
    public static String call_id;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Initializing views
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        initView();

    }

    private void initView() {

        Button btn_login = findViewById(R.id.btn_login);
        Button btn_workerRegister = findViewById(R.id.btn_workerRegister);
        edit_id = findViewById(R.id.edit_id);
        edit_pw = findViewById(R.id.edit_pw);

        btn_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });

        btn_workerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterManagerActivity.class);
                startActivity(intent);
            }
        });
    }


    private void login() {
        //Getting values from edit texts
        final String id = edit_id.getText().toString().trim();
        final String password = edit_pw.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.show();
                        //Log.i("response", response.toString());
                        if (response.contains("success")) {
                            progressDialog.dismiss();
                            SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
                            Config.manager_name = id;
                            //Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Adding values to editor
                            editor.putBoolean("loggedin", true);
                            editor.putString("id", id);

                            //Saving values to editor
                            editor.commit();

                           // new MateFirebaseInstanceIDService().onTokenRefresh();

                            //Starting profile activity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                    }

                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("manager_id", id);
                params.put("manager_pw", password);

                //returning parameter
                return params;
            }
        };

        //Creating a string request

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        //Calling the login function
        login();
    }
}