/**
 *
 */
package com.initech.news.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.initech.news.R;

/**
 * Helper class for getting screen dimensions and doing dimensional pixel conversion;
 *
 * @author petey
 */
public final class ScreenUtil {

    private ScreenUtil() {
    }

    public static final int SCREEN_DENSITY_LOW = 120, SCREEN_DENSITY_MEDIUM = 160, SCREEN_DENSITY_TV = 213, SCREEN_DENSITY_HIGH = 240, SCREEN_DENSITY_XTRA_HIGH = 320;

    private static int screenDpi = -1;
    private static float density = -1;

    public static int dpToPx(Context context, int dp) {
        float density = getDensity(context);
        return (int) (dp * density + 0.5f);
    }

    public static Drawable scaleDrawable(Context context, int drawableResourceId, int width, int height) {
        Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources(), drawableResourceId);
        return new BitmapDrawable(Bitmap.createScaledBitmap(sourceBitmap, width, height, true));
    }

    static class Dimension {
        int width, height;
        float density;
    }

    public static Dimension getDimensions(Context context) {
        return getDimensions(context, 0);
    }

    /**
     * Get Screen Dimension related to a specified orientation
     *
     * @param context
     * @param rotation
     * @return
     */
    public static Dimension getDimensions(Context context, int rotation) {
        Dimension d = new Dimension();
        d.height = getScreenHeight(context);
        d.width = getScreenWidth(context);
        d.density = getDensity(context);
        return d;
    }

    private static Display getDefaultDisplay(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    public static boolean isTablet(final Context context) {
        return context.getResources().getBoolean(R.bool.isTablet); //TODO
    }

    public static boolean isLandscapeOrientation(final Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isPortraitOrientation(final Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static float getDensity(Context context) {
        if (density == -1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getDefaultDisplay(context).getMetrics(metrics);
            density = metrics.density;
        }
        return density;
    }

    public static int getBetterOrientation(Context context) {
        screenDpi = -1;
        Dimension d = getDimensions(context);
        if (d.width < d.height) {
            return Configuration.ORIENTATION_PORTRAIT;
        } else if (d.width > d.height) {
            return Configuration.ORIENTATION_LANDSCAPE;
        } else if (d.width == d.height) {
            return Configuration.ORIENTATION_SQUARE;
        }
        return Configuration.ORIENTATION_UNDEFINED;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenDpi(Context context) {
        if (screenDpi == -1) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            try {
                screenDpi = DisplayMetrics.class.getField("densityDpi").getInt(displayMetrics);
            } catch (Exception e) {
                screenDpi = SCREEN_DENSITY_MEDIUM;
            }
        }
        return screenDpi;
    }

    public static boolean isAutoScreenRotateEnabled(final Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    }

    //https://developer.android.com/training/system-ui/immersive.html
    public static void showSystemUI(final Window window) {

		/*
       * no matter how tempting it is to
		 * modify the following code, don't.  it's a rabbit hole.
		 */
        if (Build.VERSION.SDK_INT >= 19) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

    }

    //https://developer.android.com/training/system-ui/immersive.html
    public static void hideSystemUI(final Window window) {

		/*
       * no matter how tempting it is to
		 * modify the following code, don't.  it's a rabbit hole.
		 */
        if (Build.VERSION.SDK_INT >= 19) {  //kitkat
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }

    }

    /**
     * @param view - The view whose window token
     *             has an attached keyboard.
     */
    public static void hideVirtualKeyboard(final View view) {
        try {
            ((InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (final Throwable t) {
        }
    }

    /**
     * Calculates preferred/optimal portrait-mode video height assuming:
     * <p>
     * 1) 16:9 aspect ratio
     * 2) full-screen width
     *
     * @param c
     * @return
     */
    public static int getPortraitVideoHeight(final Context c) {
        final WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        final int deviceWidth = displayMetrics.widthPixels < displayMetrics.heightPixels ? displayMetrics.widthPixels : displayMetrics.heightPixels;
        return 9 * deviceWidth / 16;
    }

    public static int getPortraitHeight(final Context c) {
        final WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        return displayMetrics.widthPixels > displayMetrics.heightPixels ? displayMetrics.heightPixels : displayMetrics.widthPixels;
    }

    /**
     * If device has physical navigation keys (BACK KEY) return 0.
     * However, if device does not have physical buttons, return
     * the height of the navigation bar.
     *
     * @return
     */
    public static int getNavigationBarHeight(final Context c) {

        final boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasBackKey) {
            final int resourceId = c.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return resourceId > 0 ? c.getResources().getDimensionPixelSize(resourceId) : 0;
        } else {
            return 0;
        }
    }

    /**
     * For APIs that support it, calculate the actual screen width _currently_ taken up by on-screen buttons. This can happen on any
     * device. In landscape mode, it's ubiquitous on phones and can occur on tablets. This method is typically only useful in landscape
     * mode.
     *
     * @param activity
     * @return The actual screen width _currently_ taken up by on-screen buttons, if any.
     */
    @SuppressLint("NewApi")
    public static int getCurrentNavigationBarWidth(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            final DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            final int usableWidth = metrics.widthPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            final int realWidth = metrics.widthPixels;
            if (realWidth > usableWidth) {
                return realWidth - usableWidth;
            } else {
                return 0;
            }
        } else {
            // If we can't calculate it, assume that any device that has soft keys is displaying the navigation bar right now.
            return getNavigationBarHeight(activity);
        }
    }

    /**
     * Attempts to apply a 'safe-zone' padding to a given view.
     * To be used in tv-related apps.
     *
     * @param view
     */
    public static void applySafeZoneToView(final View view) {

        final Display display = getDefaultDisplay(view.getContext());
        final Point size = new Point();
        display.getSize(size);
        final int padWidthPixels = (size.x / 10) / 2;
        final int padHeightPixels = (size.y / 10) / 2;
        view.setPadding(padWidthPixels, padHeightPixels, padWidthPixels, padHeightPixels);
    }
}
