package com.hrgirdowner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class UrlActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs_url" ;
    public static final String MyPREFERENCES1 = "MyPrefs" ;
    int PRIVATE_MODE = 0;
    SharedPreferences pref, shared_pref;
    SharedPreferences.Editor editor, editor1;
    
    ProgressDialog progressDialog;
    CheckInternetConnection internetConnection;
    UserSessionManager session;
    ConnectionDetector cd;
    URL url;

    String response, myJson;
    String set_url;
    String get_url;
    String url_http, url_main;
    
    Button btn_submit;
    EditText ed_url;
    LinearLayout poweredby_layout;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);
        
        session = new UserSessionManager(getApplicationContext());
        internetConnection = new CheckInternetConnection(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        url_http = cd.geturl();
        url_main = cd.get_infogird_url();
        //Log.i("url_http", url_http+""+url_main);
        
        btn_submit = (Button)findViewById(R.id.btn_urlSubmit);
        ed_url = (EditText)findViewById(R.id.company_url);
        poweredby_layout = (LinearLayout)findViewById(R.id.layout_poweredby_url);

        if (internetConnection.hasConnection(getApplicationContext()))
        {
            if (session.isUrlPresent())
            {
                if (session.isUserLoggedIn())
                {
                    Intent intent = new Intent(UrlActivity.this, DashBoard.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(UrlActivity.this, LogInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
        else {
            internetConnection.showNetDisabledAlertToUser(UrlActivity.this);
        }
        

        ed_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poweredby_layout.setVisibility(View.GONE);
            }
        });

        ed_url.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                poweredby_layout.setVisibility(View.VISIBLE);
            }
        });
        
        shared_pref = getSharedPreferences(MyPREFERENCES1, MODE_PRIVATE);
        
        btn_submit.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v) 
            {
                if (internetConnection.hasConnection(getApplicationContext()))
                {
                    if (!ed_url.getText().toString().equals(""))
                    {
                        if (ed_url.getText().toString().length() >= 3) {
                            set_url = ed_url.getText().toString();
                            editor1 = shared_pref.edit();
                            editor1.clear();
                            editor1.commit();

                            getUrlData();
                        }
                        else {
                            ed_url.setError("Please enter valid url");
                        }
                    } 
                    else {
                        ed_url.setError("Please enter url");
                    }
                }
                else {
                    internetConnection.showNetDisabledAlertToUser(UrlActivity.this);
                }
            }
        });

        ed_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ed_url.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    public void getUrlData()
    {
        class GetUrlData extends AsyncTask<String, Void, String>
        {
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(UrlActivity.this, "Please wait", "Checking url...", true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params)
            {
                try
                {
                    String leave_url = ""+url_http+""+url_main+"/owner/hrmapi/checkurl/?";

                    String query3 = String.format("url=%s", URLEncoder.encode(set_url, "UTF-8"));
                    URL url = new URL(leave_url + query3);
                    Log.i("url", "" + url);

                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(false);
                    connection.setAllowUserInteraction(false);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK)
                    {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = br.readLine()) != null)
                        {
                            response = "";
                            response += line;
                        }
                    }
                    else
                    {
                        response = "";
                    }
                }
                catch (Exception e){
                    Log.e("Exception", e.toString());
                }

                return response;
            }

            @Override
            protected void onPostExecute(String result)
            {
                if (result != null)
                {
                    myJson = result;
                    Log.i("myJson", myJson);
                    //progressDialog.dismiss();

                    if (myJson.equals("[]"))
                    {
                        progressDialog.dismiss();
                        Toast.makeText(UrlActivity.this, "Sorry... Bad internet connection", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        try
                        {
                            JSONArray jsonArray = new JSONArray(myJson);
                            //Log.i("jsonArray", "" + jsonArray);

                            JSONObject object = jsonArray.getJSONObject(0);

                            String responseCode = object.getString("responsecode");

                            if (responseCode.equals("1"))
                            {
                                get_url = object.getString("url");
                                
                                session.createUrlLogin(set_url);

                                pref = getApplicationContext().getSharedPreferences(MyPREFERENCES, PRIVATE_MODE);
                                editor = pref.edit();
                                editor.putString("url", get_url);
                                editor.commit();

                                getLogoData();
                            }
                            else 
                            {
                                progressDialog.dismiss();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UrlActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                                alertDialog.setTitle("Invalid Url");
                                alertDialog.setMessage("Please contact with organisation for correct url");
                                alertDialog.setCancelable(true);
                                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });

                                alertDialog.show();
                            }

                        }
                        catch (JSONException e) {
                            progressDialog.dismiss();
                            Log.e("JsonException", e.toString());
                        }
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(UrlActivity.this, "Sorry...Bad internet connection", Toast.LENGTH_LONG).show();
                }
            }
        }

        GetUrlData getUrlData = new GetUrlData();
        getUrlData.execute();
    }

    public void getLogoData()
    {
        class GetLogoData extends AsyncTask<String, Void, String>
        {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(String... params)
            {
                try
                {
                    String leave_url = ""+url_http+""+get_url+"/owner/hrmapi/logo/?";
                    
                    URL url = new URL(leave_url);
                    Log.i("url", "" + url);

                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(false);
                    connection.setAllowUserInteraction(false);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK)
                    {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = br.readLine()) != null)
                        {
                            response = "";
                            response += line;
                        }
                    }
                    else
                    {
                        response = "";
                    }
                }
                catch (Exception e){
                    Log.e("Exception", e.toString());
                }

                return response;
            }

            @Override
            protected void onPostExecute(String result)
            {
                if (result != null)
                {
                    myJson = result;
                    Log.i("myJson", myJson);

                    progressDialog.dismiss();


                    if (myJson.equals("[]"))
                    {
                        //Toast.makeText(UrlActivity.this, "Sorry... Bad internet connection", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        try
                        {
                            JSONArray jsonArray = new JSONArray(myJson);
                            //Log.i("jsonArray", "" + jsonArray);

                            JSONObject object = jsonArray.getJSONObject(0);

                            String get_logo = object.getString("logo");

                            String logo_url = "https://"+get_url+"/files/"+get_url+"/images/logo/";

                            String logo_final = logo_url +""+ get_logo;

                            shared_pref = getApplicationContext().getSharedPreferences(MyPREFERENCES, PRIVATE_MODE);
                            editor1 = shared_pref.edit();
                            editor1.putString("logo", logo_final);
                            editor1.commit();

                            Intent intent = new Intent(UrlActivity.this, LogInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        catch (JSONException e) {
                            Log.e("JsonException", e.toString());
                        }
                    }
                }
                else {
                    if (progressDialog.isShowing() && progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(UrlActivity.this, "Sorry...Bad internet connection", Toast.LENGTH_LONG).show();
                }
            }
        }

        GetLogoData getUrlData = new GetLogoData();
        getUrlData.execute();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);

    }
}
