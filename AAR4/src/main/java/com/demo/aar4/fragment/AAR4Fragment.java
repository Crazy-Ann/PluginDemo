package com.demo.aar4.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.aar4.R;
import com.demo.aar4.base.BaseFragment;

public class AAR4Fragment extends BaseFragment {

    public AAR4Fragment() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.view_aar4, container, false);
        return mView;
    }
}
