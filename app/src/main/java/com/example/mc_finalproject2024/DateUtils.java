package com.example.mc_finalproject2024;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String formatDateString(String originalDateString) {
        // Parse the original date string which is assumed to be in yyyy-MM-dd format
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat newFormat = new SimpleDateFormat("EEEE MMMM d", Locale.US); // Partially formatted

        try {
            Date date = originalFormat.parse(originalDateString);
            String partiallyFormatted = newFormat.format(date);
            return appendOrdinalIndicator(partiallyFormatted);
        } catch (ParseException e) {
            e.printStackTrace();
            return originalDateString; // return original date if parsing fails
        }
    }

    private static String appendOrdinalIndicator(String date) {
        String[] parts = date.split(" ");
        if (parts.length < 3) return date; // Safety check

        String day = parts[2]; // The day part, e.g., "24"
        int dayNum = Integer.parseInt(day);
        switch (dayNum % 10) {
            case 1: if (dayNum % 100 != 11) return date + "st";
            case 2: if (dayNum % 100 != 12) return date + "nd";
            case 3: if (dayNum % 100 != 13) return date + "rd";
            default: return date + "th";
        }
    }
}

