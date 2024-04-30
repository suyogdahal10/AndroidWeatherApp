package com.example.mc_finalproject2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private List<DailyWeather> weatherList;

    public WeatherAdapter(List<DailyWeather> weatherList) {
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        return new WeatherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        DailyWeather weather = weatherList.get(position);
        String formattedDate = DateUtils.formatDateString(weather.getDate());
        holder.dateTextView.setText(formattedDate);
        holder.tempTextView.setText(String.format(Locale.getDefault(), "%.0f°F - %.0f°F", weather.getMaxTemp(), weather.getMinTemp()));
        holder.conditionTextView.setText(weather.getCondition());
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView tempTextView;
        TextView conditionTextView;

        WeatherViewHolder(View view) {
            super(view);
            dateTextView = view.findViewById(R.id.date);
            tempTextView = view.findViewById(R.id.temperature);
            conditionTextView = view.findViewById(R.id.condition);
        }
    }
}




