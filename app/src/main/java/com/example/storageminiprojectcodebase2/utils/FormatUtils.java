package com.example.storageminiprojectcodebase2.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {
    public static String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(price) + " đ";
    }
}
