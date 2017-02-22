package com.sundyn.centralizedeval.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sundyn.centralizedeval.bean.UserBean;
import com.sundyn.centralizedeval.views.GalleryItem;

import java.util.List;

/**
 * Created by Administrator on 2017/2/22.
 */

public class GalleryAdapter extends BaseAdapter {

    private static String TAG = "GalleryAdapter";

    private Context context;

    private List<UserBean> users;

    /**
     *
     */
    public GalleryAdapter(Context context, List<UserBean> users) {
        super();
        this.context = context;
        this.users = users;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {

        return users.size();
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {

        return users.get(position);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GalleryItem galleryItem = null;
        if (convertView == null) {
            galleryItem = new GalleryItem(context);

            convertView = galleryItem;
        } else {
            galleryItem = (GalleryItem)convertView;
        }
        galleryItem.setUser(users.get(position));

        return convertView;
    }

}
