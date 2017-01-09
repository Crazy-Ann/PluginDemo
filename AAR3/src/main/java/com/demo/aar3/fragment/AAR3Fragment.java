package com.demo.aar3.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.aar3.R;
import com.demo.aar3.base.BaseFragment;

public class AAR3Fragment extends BaseFragment {

    public AAR3Fragment() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.view_aar3, container, false);
        return mView;
    }
}
