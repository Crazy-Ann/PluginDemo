package com.yjt.aar1.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjt.aar1.R;
import com.yjt.aar1.base.BaseFragment;

public class AAR1Fragment extends BaseFragment {

    public AAR1Fragment() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.view_aar1, container, false);
        return mView;
    }
}
