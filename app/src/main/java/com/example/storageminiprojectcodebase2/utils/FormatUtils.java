package com.example.storageminiprojectcodebase2.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {
    private FormatUtils() {}

    public static String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        // Ensure we show integer-like prices cleanly (matching current data seed).
        return formatter.format(Math.round(price)) + " đ";
    }
}

