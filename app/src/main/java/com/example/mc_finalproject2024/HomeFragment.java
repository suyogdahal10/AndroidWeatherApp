package com.example.mc_finalproject2024;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.internal.location.zzz;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.appbar.MaterialToolbar;
import android.Manifest;
import android.location.Location;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_LOCATION = 101;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle ("Home");
        checkLocationPermission();
    }



    public void fetchWeather(Location location){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        String url = "https://api.weatherapi.com/v1/current.json?key=8cbf36afe3834be78b8160352242304&q=" + lat + "," + lon;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("WeatherAPI", "Response: " + response);
                        updateWeatherUI(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("WeatherAPI", "Error: " + error.toString());
                error.printStackTrace();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }


    private void updateWeatherUI(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject current = jsonObject.getJSONObject("current");
            JSONObject location = jsonObject.getJSONObject("location");

            String state = location.getString("region");
            String locationName = location.getString("name");
            double temp = current.getDouble("temp_f");
            String conditionText = current.getJSONObject("condition").getString("text");
            double windSpeed = current.getDouble("wind_mph");
            Integer humidityPercent = current.getInt("humidity");

            String stateCity = (locationName +", "+state);

            TextView locationView = getView().findViewById(R.id.location_name);
            TextView temperatureView = getView().findViewById(R.id.temperature);
            TextView conditionView = getView().findViewById(R.id.condition);
            TextView windView = getView().findViewById(R.id.windspeed);
            TextView humidityView = getView().findViewById(R.id.humidity);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    locationView.setText(stateCity);
                    temperatureView.setText(String.format(Locale.getDefault(), "%.0f°F", temp));
                    conditionView.setText(conditionText);
                    windView.setText(String.format(Locale.getDefault(),"%.0f mph",windSpeed));
                    humidityView.setText(humidityPercent.toString()+"%");

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", (dialogInterface, i) ->
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION))
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            }
        } else{
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {

            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d("Location", "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());

                            fetchWeather(location);
                        } else {
                            Log.e("Location", "Location is null");
                        }
                    }
                });
    }

}
