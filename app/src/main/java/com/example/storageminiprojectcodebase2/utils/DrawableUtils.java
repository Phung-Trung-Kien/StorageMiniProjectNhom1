package com.example.storageminiprojectcodebase2.utils;

import android.content.Context;

public class DrawableUtils {
    /**
     * Get drawable resource ID from its name.
     * @param context the context
     * @param name the name of the drawable resource
     * @return the resource ID, or 0 if not found or name is empty
     */
    public static int getDrawableResourceId(Context context, String name) {
        if (name == null || name.isEmpty() || context == null) {
            return 0;
        }
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }
}
