package com.sundyn.centralizedeval.plugin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sundyn.centralizedeval.R;

/**
 * Created by Administrator on 2017/2/21.
 */

public class BarAdviseItem extends BarBaseItem{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parent = null;
        parent = inflater.inflate(R.layout.bar_ads, null);
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FillAds.class));
            }
        });

        return parent;
    }
}
