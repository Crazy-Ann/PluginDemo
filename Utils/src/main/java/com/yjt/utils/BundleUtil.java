package com.yjt.utils;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;

import com.yjt.constant.Constant;
import com.tencent.mars.xlog.XLog;

import java.util.Collection;

public class BundleUtil {

    private static BundleUtil mBundleUtil;

    private BundleUtil() {
        // cannot be instantiated
    }

    public static synchronized BundleUtil getInstance() {
        if (mBundleUtil == null) {
            mBundleUtil = new BundleUtil();
        }
        return mBundleUtil;
    }

    public static synchronized void releaseInstance() {
        if (mBundleUtil != null) {
            mBundleUtil = null;
        }
    }

    public boolean hasIntentExtraValue(Activity activity, String extraKey) {
        return activity.getIntent() != null && activity.getIntent().hasExtra(extraKey);
    }

    public boolean hasBundleExtraValue(Activity activity, String extraKey) {
        return activity.getIntent().getExtras() != null && activity.getIntent().hasExtra(extraKey);
    }

    public int getIntData(Activity activity, String key) {
        if (activity.getIntent() != null) {
            return activity.getIntent().getExtras().getInt(key);
        }
        return -1;
    }

    public int getIntData(Bundle bundle, String key) {
        if (bundle != null) {
            return bundle.getInt(key);
        }
        return -1;
    }

    public int getIntData(Intent intent, String key) {
        if (intent != null) {
            return intent.getIntExtra(key, -1);
        }
        return -1;
    }

    public double getDoubleData(Activity activity, String key) {
        if (activity.getIntent() != null) {
            return activity.getIntent().getExtras().getDouble(key);
        }
        return -1.0;
    }


    public long getLongData(Bundle bundle, String key, long defaultValue) {
        if (bundle != null) {
            return bundle.getLong(key, defaultValue);
        }
        return -1;
    }


    public CharSequence getCharSequenceData(Bundle bundle, String key) {
        if (bundle != null) {
            return bundle.getCharSequence(key);
        }
        return null;
    }

    public CharSequence[] getCharSequenceArrayData(Bundle bundle, String key) {
        if (bundle != null) {
            return bundle.getCharSequenceArray(key);
        }
        return null;
    }

    public String getStringData(Bundle bundle, String key) {
        if (bundle != null) {
            return bundle.getString(key);
        }
        return null;
    }


    public String getStringData(Activity activity, String key) {
        if (activity.getIntent() != null) {
            return activity.getIntent().getExtras().getString(key);
        }
        return null;
    }

    public boolean getBooleanData(Bundle bundle, String key) {
        return bundle != null && bundle.getBoolean(key);
    }

    public Bundle getBundleData(Intent intent, String key) {
        if (intent != null) {
            return intent.getBundleExtra(key);
        }
        return null;
    }

    public <T extends Parcelable> T getParcelableData(Activity activity, String key) {
        if (activity.getIntent() != null) {
            return (T) activity.getIntent().getExtras().getParcelable(key);
        }
        return null;
    }

    public <T extends Parcelable> T getParcelableData(Bundle bundle, String key) {
        if (bundle != null) {
            return (T) bundle.getParcelable(key);
        }
        return null;
    }

    public <T extends Collection<? extends Parcelable>> T getParcelableArrayListData(Bundle bundle, String key) {
        if (bundle != null) {
            return (T) bundle.getParcelableArrayList(key);
        }
        return null;
    }

    public void setResult(FragmentActivity activity, int resultCode, int returnCode, String returnMessage) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.Extra.RESULT_CODE, returnCode);
        bundle.putString(Constant.Extra.RESULT_MESSAGE, returnMessage);
        intent.putExtras(bundle);
        activity.setResult(resultCode, intent);
        activity.finish();
        XLog.getInstance().println(activity.getClass().getSimpleName() + " setResult");
    }

    public void setResult(FragmentActivity activity, int resultCode, Intent intent) {
        activity.setResult(resultCode, intent);
        activity.finish();
        XLog.getInstance().println(activity.getClass().getSimpleName() + " setResult");
    }
}
