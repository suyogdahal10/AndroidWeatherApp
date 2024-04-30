package com.example.mc_finalproject2024;

import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private EditText locationEditText;
    private Button submitButton;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Your existing code
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle ("Search Location");

        locationEditText = view.findViewById(R.id.location_text);
        submitButton = view.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = locationEditText.getText().toString();
                if (!location.isEmpty()) {
                    fetchWeather(location);
                } else {
                    Toast.makeText(getActivity(), "Please enter a valid city name", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void fetchWeather(String location) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://api.weatherapi.com/v1/current.json?key=8cbf36afe3834be78b8160352242304&q=" + location;
        
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                this::updateWeatherUI, error -> {
            Log.e("WeatherAPI", "Error: " + error.toString());
            error.printStackTrace();
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
}
