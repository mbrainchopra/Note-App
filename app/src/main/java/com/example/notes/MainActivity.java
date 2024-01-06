package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private NoteAdapter noteAdapter;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;

    private ProgressBar progressBar2;


    List<NoteList> noteLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar2 = findViewById(R.id.progressBar2);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoadd = new Intent(MainActivity.this, AddnoteActivity.class);
                startActivity(gotoadd);
            }
        });

        getNotes();
    }

    private void getNotes() {
        progressBar2.setVisibility(View.VISIBLE);
        String url = "https://raw.githubusercontent.com/RishabhRaghunath/JustATest/master/notes";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar2.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        floatingActionButton.setVisibility(View.VISIBLE);
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject noteObject = jsonArray.getJSONObject(i);

                                String title = noteObject.getString("title");
                                String body = noteObject.getString("body");
                                String createdTime = noteObject.getString("created_time");

                                try {
                                    long unixTimestamp = Long.parseLong(createdTime) * 1000L;
                                    Date date = new Date(unixTimestamp);
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy hh:mm a", Locale.getDefault());

                                    NoteList notelist = new NoteList();
                                    notelist.setTitle(title);
                                    notelist.setFormattedDate(sdf.format(date));
                                    notelist.setBody(body);
                                    noteLists.add(notelist);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                            }

                            noteAdapter = new NoteAdapter(MainActivity.this, noteLists);
                            GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(noteAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                progressBar2.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);
                error.printStackTrace();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }
}
