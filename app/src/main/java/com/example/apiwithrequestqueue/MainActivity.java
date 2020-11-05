package com.example.apiwithrequestqueue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<SpellItems> allSpells = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spells spells = new Spells();
        spells.execute();

        listView = findViewById(R.id.list_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, details.class);
                intent.putExtra("name", allSpells.get(i).getName());
                intent.putExtra("url", allSpells.get(i).getUrl());

                startActivity(intent);
            }
        });
    }

    /**
     * Async class that runs the http request for the spells
     */
    class Spells extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            RequestQueue requestQueue;
            Cache cache = new DiskBasedCache(getCacheDir(), 1024*1024);
            Network network = new BasicNetwork(new HurlStack());

            requestQueue = new RequestQueue(cache, network);

            requestQueue.start();

            String url = "https://www.dnd5eapi.co/api/spells";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("RJS", "Response: " + response.toString());

                            try {
                                JSONArray spells = response.getJSONArray("results");
                                for(int i = 0; i < spells.length(); i++){
                                    JSONObject test = spells.getJSONObject(i);
                                    String name = test.getString("name");
                                    String url = test.getString("url");
                                    allSpells.add(new SpellItems(name, url));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ArrayList<String> spellNames = new ArrayList<>();
                            for(int i = 0; i < allSpells.size(); i++){
                                spellNames.add(allSpells.get(i).getName());
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, spellNames);
                            listView.setAdapter(arrayAdapter);
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

    /**
     * Class that will contain the name of a spell and the url.
     */
    class SpellItems {
        private String name;
        private String url;

        public SpellItems(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}