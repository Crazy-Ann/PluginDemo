package com.demo.aar2.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.aar2.R;
import com.demo.aar2.base.BaseFragment;

/**
 * Created by yjt on 2017/1/9.
 */

public class AAR2Fragment extends BaseFragment {

    public AAR2Fragment() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.view_aar2, container, false);
        return mView;
    }
}
