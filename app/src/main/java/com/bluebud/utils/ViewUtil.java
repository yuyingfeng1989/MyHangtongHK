package com.bluebud.utils;

import android.view.View;

public class ViewUtil {

    public static void setVisible(View view) {
        setVisibility(view, View.VISIBLE);
    }

    public static void setGone(View view) {
        setVisibility(view, View.GONE);
    }

    public static void setInvisible(View view) {
        setVisibility(view, View.INVISIBLE);
    }

    private static void setVisibility(View view, int visibility) {
        if (view == null) {
            return;
        }
        view.setVisibility(visibility);
    }

}
