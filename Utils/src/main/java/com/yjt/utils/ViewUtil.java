package com.yjt.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yjt.constant.Constant;

import java.lang.reflect.Method;

public class ViewUtil {

    private static ViewUtil mViewUtil;

    private ViewUtil() {
        // cannot be instantiated
    }

    public static synchronized ViewUtil getInstance() {
        if (mViewUtil == null) {
            mViewUtil = new ViewUtil();
        }
        return mViewUtil;
    }

    public static synchronized void releaseInstance() {
        if (mViewUtil != null) {
            mViewUtil = null;
        }
    }

    public int dp2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dp(Context ctx, float pxValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public int getScreenWidth(Context ctx) {
        return ctx.getResources().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight(Context ctx) {
        return ctx.getResources().getDisplayMetrics().heightPixels;
    }

    public float getDensity(Context ctx) {
        return ctx.getResources().getDisplayMetrics().density;
    }

    public int getDensityDpi(Context ctx) {
        return ctx.getResources().getDisplayMetrics().densityDpi;
    }

    public DisplayMetrics getScreenPixel(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public boolean isSoftKeyAvail(Activity activity) {
        final boolean[] isSoftkey = {false};
        final View rootView = (activity).getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int rootViewHeight = rootView.getRootView().getHeight();
                int viewHeight = rootView.getHeight();
                int heightDiff = rootViewHeight - viewHeight;
                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                    isSoftkey[0] = true;
                }
            }
        });
        return isSoftkey[0];
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public int getNavigationBarStatus(Context ctx) {
        boolean hasMenuKey = ViewConfiguration.get(ctx).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            return ctx.getResources().getDimensionPixelSize(ctx.getResources().getIdentifier("navigation_bar_height", "dimen", "android"));
        } else {
            return 0;
        }
    }

    public int getStatusBarHeight(Context ctx) {
        int height = 0;
        int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public int getNavigationBarHeight(Context ctx) {
        int height = 0;
        Resources resources = ctx.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && checkDeviceHasNavigationBar(ctx)) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    private boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources resources = context.getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = resources.getBoolean(id);
        }
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class);
            String navBarOverride = (String) method.invoke(clazz, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }

    public int getTopBarHeight(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    public boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    public boolean isInvisible(View view) {
        return view.getVisibility() == View.INVISIBLE;
    }

    public boolean isGone(View view) {
        return view.getVisibility() == View.GONE;
    }

    public void setViewVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public void setViewGone(View view) {
        view.setVisibility(View.GONE);
    }

    public void setViewInvisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    public void setToolBar(final AppCompatActivity activity, int toolbarId, boolean isHomeButtonEnable) {
        if (activity != null && toolbarId != Constant.View.RESOURCE_DEFAULT) {
            Toolbar toolbar = findView(activity, toolbarId);
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(isHomeButtonEnable);
                toolbar.setTitle(activity.getTitle());
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });
            }
        }
    }

    public <V> V findView(Activity activity, @IdRes int resId) {
        return (V) activity.findViewById(resId);
    }

    public <V> V findView(View rootView, @IdRes int resId) {
        return (V) rootView.findViewById(resId);
    }

    public <V> V findViewAttachOnclick(Activity activity, @IdRes int resId, View.OnClickListener onClickListener) {
        View view = activity.findViewById(resId);
        view.setOnClickListener(onClickListener);
        return (V) view;
    }

    public <V> V findViewAttachOnclick(View rootView, @IdRes int resId, View.OnClickListener onClickListener) {
        View view = rootView.findViewById(resId);
        view.setOnClickListener(onClickListener);
        return (V) view;
    }


    public boolean isScrollable(ViewGroup group) {
        int totalHeight = 0;
        for (int i = 0; i < group.getChildCount(); i++) {
            totalHeight += group.getChildAt(i).getMeasuredHeight();
        }
        return group.getMeasuredHeight() < totalHeight;
    }

    public boolean isScrollTop(RecyclerView recyclerView) {
        if (recyclerView != null && recyclerView.getChildCount() > 0) {
            if (recyclerView.getChildAt(0).getTop() < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isScrollTop(ListView listView) {
        if (listView != null && listView.getChildCount() > 0) {
            if (listView.getChildAt(0).getTop() < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isScrollTop(ExpandableListView listView) {
        if (listView != null && listView.getChildCount() > 0) {
            if (listView.getChildAt(0).getTop() < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isScrollTop(ScrollView scrollView) {
        if (scrollView != null) {
            if (scrollView.getScrollY() > 0) {
                return false;
            }
        }
        return true;
    }

    public void toggleView(View view, boolean show) {
        if (show) {
            setViewVisible(view);
        } else {
            setViewGone(view);
        }
    }

    public void setText(Context ctx, TextView textView, String content, int imageId, int width, int height, int padding, int position, boolean clickable) {
        textView.setText(content);
        if (imageId != -1) {
            Drawable drawable = ctx.getResources().getDrawable(imageId);
            drawable.setBounds(0, 0, width, height);
            switch (position) {
                case Constant.View.DRAWABLE_TOP:
                    textView.setCompoundDrawablePadding(padding);
                    textView.setCompoundDrawables(null, drawable, null, null);
                    break;
                case Constant.View.DRAWABLE_LEFT:
                    textView.setCompoundDrawablePadding(padding);
                    textView.setCompoundDrawables(drawable, null, null, null);
                    break;
                case Constant.View.DRAWABLE_RIGHT:
                    textView.setCompoundDrawablePadding(padding);
                    textView.setCompoundDrawables(null, null, drawable, null);
                    break;
                case Constant.View.DRAWABLE_BOTTOM:
                    textView.setCompoundDrawablePadding(padding);
                    textView.setCompoundDrawables(null, null, null, drawable);
                    break;
                default:
                    textView.setCompoundDrawablePadding(padding);
                    textView.setCompoundDrawables(drawable, null, null, null);
                    break;
            }
        }
        textView.setClickable(clickable);
    }

    public void setText(TextView textView, CharSequence text, Typeface font) {
        if (text != null) {
            textView.setText(text);
            textView.setTypeface(font);
        } else {
            ViewUtil.getInstance().setViewGone(textView);
        }
    }

    public void setText(Button button, CharSequence text, Typeface font, View.OnClickListener listener) {
        setText(button, text, font);
        if (listener != null) {
            button.setOnClickListener(listener);
        }
    }

    public void setButton(Context ctx, Button button, int backgroundId, int textColorId, boolean enable) {
        if (backgroundId != Constant.View.RESOURCE_DEFAULT) {
            button.setBackgroundDrawable(ctx.getResources().getDrawable(backgroundId));
        }
        if (textColorId != Constant.View.RESOURCE_DEFAULT) {
            button.setTextColor(ctx.getResources().getColor(textColorId));
        }
        button.setEnabled(enable);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setSystemUiVisibility(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                        View.SYSTEM_UI_FLAG_FULLSCREEN |
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE);

    }

    public void runOnUiThread(final Activity activity, final String prompt) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.getInstance().showToast(activity, prompt, Toast.LENGTH_SHORT);
            }
        });
    }

    public ProgressDialog showProgressDialog(Activity activity, String title, String message, DialogInterface.OnCancelListener cancelListener, boolean cancelable) {
        if (activity != null && !activity.isFinishing()) {
            lockScreenOrientation(activity);
            Dialog dialog = ProgressDialog.show(activity, title, message, true, true, cancelListener);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(cancelable);
            return (ProgressDialog) dialog;
        } else {
            return null;
        }
    }

    public void hideDialog(Dialog dialog, Activity activity) {
        if (dialog != null && dialog.isShowing() && activity != null && !activity.isFinishing()) {
            dialog.dismiss();
            unLockScreenOrientation(activity);
        }
    }

    public void hideDialog(DialogFragment dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void lockScreenOrientation(Activity activity) {
        Configuration newConfig = activity.getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (newConfig.hardKeyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (newConfig.hardKeyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void unLockScreenOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
