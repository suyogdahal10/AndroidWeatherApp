package com.example.mc_finalproject2024;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import android.Manifest;


public class WeeklyWeatherFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private RecyclerView recyclerView;
    private WeatherAdapter adapter;
    private List<DailyWeather> weatherList = new ArrayList<>();

    public static WeeklyWeatherFragment newInstance() {
        return new WeeklyWeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weekly_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle ("Weekly Weather");
        recyclerView = view.findViewById(R.id.recycler_view_weekly);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WeatherAdapter(weatherList);
        recyclerView.setAdapter(adapter);
        Log.d("WeatherAPI", "Checking location permissions");
        checkLocationPermission();
    }


    //API Request for the weekly weather
    private void fetchWeeklyWeather(double latitude, double longitude) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://api.weatherapi.com/v1/forecast.json?key=8cbf36afe3834be78b8160352242304&q=" + latitude + "," + longitude + "&days=7";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    parseWeatherData(response);
                },
                error -> {
                    Log.e("WeatherAPI", "Error fetching weather data: " + error.toString());
                    Toast.makeText(getContext(), "Error fetching weather data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }


    //Parse our JSON and update view.
    private void parseWeatherData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray forecastDays = jsonObject.getJSONObject("forecast").getJSONArray("forecastday");

            weatherList.clear();
            for (int i = 0; i < forecastDays.length(); i++) {
                JSONObject dayObject = forecastDays.getJSONObject(i);
                JSONObject day = dayObject.getJSONObject("day");
                String date = dayObject.getString("date");
                double maxTemp = day.getDouble("maxtemp_f");
                double minTemp = day.getDouble("mintemp_f");
                String condition = day.getJSONObject("condition").getString("text");
                weatherList.add(new DailyWeather(date, maxTemp, minTemp, condition));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e("WeatherAPI", "JSON parsing error", e);
            Toast.makeText(getContext(), "Failed to parse weather data", Toast.LENGTH_SHORT).show();
        }
    }



    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("WeatherAPI", "Permission not granted, requesting permission");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            Log.d("WeatherAPI", "Permission granted, getting location");
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.e("WeatherAPI", "Location result is null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.d("WeatherAPI", "Location obtained: " + location.getLatitude() + ", " + location.getLongitude());
                        fetchWeeklyWeather(location.getLatitude(), location.getLongitude());
                        // Make sure to stop updates after receiving location
                        fusedLocationProviderClient.removeLocationUpdates(this);
                        break;
                    }
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

}

