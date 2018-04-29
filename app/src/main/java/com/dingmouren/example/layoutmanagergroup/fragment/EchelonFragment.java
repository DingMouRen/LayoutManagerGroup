package com.dingmouren.example.layoutmanagergroup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dingmouren.example.layoutmanagergroup.R;

/**
 * Created by dingmouren on 2018/4/29.
 * 梯形布局
 */

public class EchelonFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_echelon,container,false);
        return rootView;
    }
}
