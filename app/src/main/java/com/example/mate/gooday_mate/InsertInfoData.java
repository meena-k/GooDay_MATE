package com.example.mate.gooday_mate;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;

public class InsertInfoData {
    String REGISTER_URL = Config.URL + "manager_register.php";
    HashMap<String, String> userMap;

    public InsertInfoData(HashMap<String, String> userMap) {
        this.userMap = userMap;
        Log.i("userUri", userMap.toString());
        insertToDatabase(userMap);
    }

    private void insertToDatabase(HashMap<String, String> userMap) {
        //String name, String birth, String sex, String phone, String enterdate, String image) {
        Log.i("insertDatabaseUri", userMap.toString());
        class InsertData extends AsyncTask<String, Void, String> {


            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i("insertDatabaseUri", "please..");

/*
                loading = ProgressDialog.show(InsertInfoData.class, "Please Wait", null, true, true);
*/
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
/*
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
*/
            }

            @Override
            protected String doInBackground(String... params) {
                Log.i("doInBackground", "enter");

                try {
                    String userUri = (String) params[0];
                    Log.i("doInBackgroundTry", userUri);
                   /*    = (String) params[4];
                    String image = (String) params[5];*/


                /*    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(userUri, "UTF-8");
                    data += "&" + URLEncoder.encode("birth", "UTF-8") + "=" + URLEncoder.encode(birth, "UTF-8");
                    data += "&" + URLEncoder.encode("sex", "UTF-8") + "=" + URLEncoder.encode(sex, "UTF-8");
                    data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
                    data += "&" + URLEncoder.encode("enterdate", "UTF-8") + "=" + URLEncoder.encode(enterdate, "UTF-8");
                    data += "&" + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8");

                    URL url = new URL(REGISTER_URL);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }*/
                    // return sb.toString();
                    return userUri;

                } catch (Exception e) {
                    Log.i("err", e.getMessage());
                    return new String("Exception: " + e.getMessage());

                }

            }

            public void execute(HashMap<String, String> userMap) {
            }
        }
        InsertData task = new InsertData();
        task.execute(userMap);
    }
}