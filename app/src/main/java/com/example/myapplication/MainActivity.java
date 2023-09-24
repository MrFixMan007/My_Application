package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng point = new LatLng(52.15521858508918, 46.437888573401914);
        mMap.addMarker(new MarkerOptions().position(point).title("Начальная координата"));
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String url = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&hourly=temperature_2m";

        List<Double> temps = new ArrayList<>();
        GetURLData getURLData = new GetURLData(temps);
//        getURLData.execute(url);
    }

    private class GetURLData extends AsyncTask<String, String, String>{
        List<Double> temps;

        public GetURLData(List<Double> temps) {
            this.temps = temps;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("wait", "ЖДИИИ");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if(connection != null)
                    connection.disconnect();

                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject= new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONObject("hourly").getJSONArray("temperature_2m");
                for(int i = 0; i < jsonArray.length(); i++){
                    temps.add((Double) jsonArray.get(i));
                }
                temps.stream().peek(x->Log.e("uns", x.toString())).count();
//                Log.e("unswer", jsonObject.getJSONObject("hourly").getJSONArray("temperature_2m").get(0).toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }


}