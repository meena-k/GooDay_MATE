package com.example.mate.gooday_mate.service;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MINA on 2018-03-01.
 */

public class GetData {
    private static final String TAG = "GetData";
    String emrJsonString;
    String date = new SimpleDateFormat("yyyy.MM.dd").format(new Date(System.currentTimeMillis()));

    public GetData(String getDATA_URL) {
        Log.i("EMRActivity", "GetData : " );
        GetDataJSON g = new GetDataJSON();
        g.execute(getDATA_URL);
    }

    public String getJSON() {
        return emrJsonString;
    }

    private class GetDataJSON extends AsyncTask<String, Void, String> {
        String errStr = null;

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.i("EMRActivity", "Sync Error " + errStr);
            } else {
                Log.i("EMRActivity", "onPostExecute " + emrJsonString);
                emrJsonString = result;
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("EMRActivity", "doInBackground : " );
            String uri = params[0];
            String data = "birth=" + Config.KEY_BIRTH + "&date=" + date;

            try {
                URL url = new URL(uri);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    Log.i("EMRLOG", line.toString());
                    sb.append(line);
                    break;
                }
                return sb.toString().trim();

            } catch (Exception e) {
                errStr = e.toString();
                return null;
            }
        }

    }

    public void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(emrJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);
                String id = item.getString("id");
                String name = item.getString("name");
                String birth = item.getString("birth");
                String sex = item.getString("sex");
                String phone = item.getString("phone");
                String enterdate = item.getString("enterdate");
                String image = item.getString("image");
                String channel = item.getString("channel");
                String port = item.getString("port");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
