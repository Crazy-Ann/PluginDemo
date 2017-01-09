package com.demo.aar3.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjt.utils.LogUtil;

import java.lang.reflect.Field;

public class BaseFragment extends Fragment {

    protected View mView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onAttach() invoked!!");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onCreate() invoked!!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onCreateView() invoked!!");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onViewCreated() invoked!!");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onActivityCreated() invoked!!");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onStart() invoked!!");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onResume() invoked!!");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onSaveInstanceState() invoked!!");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onPause() invoked!!");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onStop() invoked!!");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.getInstance().println(this.getClass().getSimpleName()
                                              + " onDestroyView() invoked!!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onDestroy() invoked!!");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onDetach() invoked!!");
        try {
            Field field = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            field.setAccessible(true);
            field.set(this, null);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.getInstance().println(this.getClass().getSimpleName() + " onHiddenChanged() invoked!!" + hidden);
    }
}
