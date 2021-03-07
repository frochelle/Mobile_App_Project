package com.example.td_3_4;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class accounts_activity extends AppCompatActivity {

    //Connection with API
    private TextView mTextViewResult;
    private RequestQueue mQueue;
    private Object JsonArrayRequest;

    //Store data
    private static final String FILE_NAME = "data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_activity);

        mTextViewResult = findViewById(R.id.text_view_result);
        Button buttonParse = findViewById(R.id.button_parse);

        mQueue = Volley.newRequestQueue(this);
        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse();
            load();
            }
        });

        load();
    }

    public void save(String text){ //String text are the API strings
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE); //Mode private so only the app can access the file
            fos.write(text.getBytes());

            // Il fait un clear ici à checker plus tard  peut etre
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void load(){
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null){
                sb.append(text).append("\n");
            }

            //text_view_result
            mTextViewResult.setText(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void jsonParse(){
        String url = getString(R.string.api_url);
        StringBuilder sb = new StringBuilder();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONArray jsonArray = response;

                        for(int i = 0; i < jsonArray.length(); i++){
                            try {
                                JSONObject account = jsonArray.getJSONObject(i);
                                String id = account.getString("id");
                                String accountName = account.getString("accountName");
                                String amount = account.getString("amount");
                                String iban = account.getString("iban");
                                String currency = account.getString("currency");

                                //mTextViewResult.append("Account n°" + id + " : " + accountName + "\n" + "IBAN" + iban + "\n" + amount + " " + currency + "\n\n");
                                sb.append("Account n°" + id + " : " + accountName + "\n" + "IBAN" + iban + "\n" + amount + " " + currency + "\n\n");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        save(sb.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}