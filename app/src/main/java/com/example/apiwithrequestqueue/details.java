package com.example.apiwithrequestqueue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class details extends AppCompatActivity {

    private String name;
    private String url;
    Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        url = intent.getStringExtra("url");

        SpellDetails spellDetails = new SpellDetails();
        spellDetails.execute();
    }

    class SpellDetails extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            RequestQueue requestQueue;
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());

            requestQueue = new RequestQueue(cache, network);

            requestQueue.start();

            String combinedUrl = "https://www.dnd5eapi.co" + url;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, combinedUrl, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.d("RJS", "Response: " + response.toString());

                            try {

                                String spellDesc = response.getString("desc");
                                String spellLvl = response.getString("level");

                                TextView txtDesc = findViewById(R.id.txtDesc);
                                TextView txtLvl = findViewById(R.id.txtlvl);

                                // Adds scrolling to the desc text view
                                txtDesc.setMovementMethod(new ScrollingMovementMethod());

                                spellDesc = spellDesc.substring(2, spellDesc.length()-2);
                                spellDesc = spellDesc.replaceAll("\"", "\n");
                                spellDesc = spellDesc.replaceAll(",", "");

                                txtDesc.setText(spellDesc);
                                txtLvl.setText("Level: " + spellLvl);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                        }
                    });

            requestQueue.add(jsonObjectRequest);
            return null;
        }
    }
}