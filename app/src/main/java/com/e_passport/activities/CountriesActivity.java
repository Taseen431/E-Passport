package com.e_passport.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e_passport.R;
import com.e_passport.utilities.AppUtilities;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountriesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private LinearLayout infoContainer;
    private YouTubePlayerView youTubePlayerView;
    private TextView infoDescription;
    private ListView listView;
    private ArrayAdapter adapter;
    private String[] list;
    private JSONArray countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries);

        infoContainer = (LinearLayout) findViewById(R.id.info_container);
        infoDescription = (TextView) findViewById(R.id.info_description);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        listView = (ListView) findViewById(R.id.countries_listview);

        String payload = getIntent().getStringExtra("payload");

        if (payload.equalsIgnoreCase("load_data")){
            infoContainer.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            loadData();
        } else {
            listView.setVisibility(View.GONE);
            infoContainer.setVisibility(View.VISIBLE);
            try {
                JSONObject info = new JSONObject(payload);
                String video_url = info.getString("video_url");
                infoDescription.setText(info.getString("description"));
                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        String videoId = extractYTId(video_url);
                        youTubePlayer.loadVideo(videoId, 0);
                    }
                });
            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }

    private void loadData() {
        AppUtilities.showLoadingDialog(this, getString(R.string.data_loading));
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getResources().getString(R.string.json_data_url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    countries = new JSONObject(response).getJSONObject("data").getJSONArray("countries");
                    list = new String[countries.length()];
                    for (int i = 0; i < countries.length(); i++){
                        list[i] = countries.getJSONObject(i).getString("name");
                    }
                    adapter = new ArrayAdapter<String>(CountriesActivity.this, R.layout.countries_listview, list);
                    listView.setOnItemClickListener(CountriesActivity.this);
                    listView.setAdapter(adapter);
                    AppUtilities.hideLoadingDialog();
                } catch (Exception error){
                    AppUtilities.updateLoadingDialog(error.getMessage());
                    AppUtilities.hideLoadingDialog(2000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppUtilities.updateLoadingDialog(error.getMessage());
                AppUtilities.hideLoadingDialog(2000);
            }
        });
        requestQueue.add(stringRequest);
    }

    public static String extractYTId(String ytUrl) {
        String vId = "";
        Pattern pattern = Pattern.compile("^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()){
            vId = matcher.group(1);
        }
        return vId;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            startActivity(new Intent(this, CountriesActivity.class).putExtra("payload", countries.getJSONObject(position).getJSONObject("info").toString()));
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}