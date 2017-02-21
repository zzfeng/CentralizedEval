package com.sundyn.centralizedeval.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import com.sundyn.centralizedeval.commen.CommenUnit;
import com.sundyn.centralizedeval.utils.PanoCache;
import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.views.AdsVideo;
import com.sundyn.centralizedeval.views.VerticalScrollTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class ImageAdapter extends BaseAdapter {
    private ArrayList<String> urls;// 所有图片的路径
    private ArrayList<String> types;

    private static final int TYPE_IMG = 0;
    private static final int TYPE_VIDEO = 1;
    private static final int TYPE_TEXT = 2;
    private Handler handler;
    private LayoutInflater inflater;

    public ImageAdapter(Context ctx, ArrayList<String> urls, ArrayList<String> types,
                        Handler handler) {
        inflater = LayoutInflater.from(ctx);
        this.urls = urls;
        this.types = types;
        this.handler = handler;
    }

    @Override
    public int getItemViewType(int position) {
        String type = types.get(position);
        if ("img".equalsIgnoreCase(type) || "IMAGE".equalsIgnoreCase(type))
            return TYPE_IMG;

        if ("video".equalsIgnoreCase(type))
            return TYPE_VIDEO;

        if ("text".equalsIgnoreCase(type))
            return TYPE_TEXT;

        return super.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);

        if (convertView == null) {
            switch (type) {
                case TYPE_IMG:
                    convertView = inflater.inflate(R.layout.image, null);
                    ImageView img = (ImageView)convertView.findViewById(R.id.img);
                    Bitmap bitmap = PanoCache.getBitmapFromMem(urls.get(position));
                    if (bitmap == null) {
                        bitmap = CommenUnit.getLoacalBitmap(urls.get(position), true,
                                convertView.getLayoutParams().width);
                        if (bitmap != null) {
                            PanoCache.addBitmapToMem(urls.get(position), bitmap);
                            img.setImageBitmap(bitmap);
                        }
                    } else {
                        img.setImageBitmap(bitmap);
                    }
                    break;

                case TYPE_VIDEO:
                    convertView = inflater.inflate(R.layout.video, null);
                    final AdsVideo video = (AdsVideo)convertView.findViewById(R.id.idAdsPlayer);
                    video.setOnErrorListener(new OnErrorListener() {

                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            video.stopPlayback();
                            handler.sendEmptyMessage(0);

                            return true;
                        }
                    });
                    video.setOnCompletionListener(new OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            handler.sendEmptyMessage(0);
                        }
                    });

                    video.setVideoPath(urls.get(position));
                    video.start();
                    break;
                case TYPE_TEXT:
                    convertView = inflater.inflate(R.layout.text, null);
                    VerticalScrollTextView mReader = (VerticalScrollTextView)convertView
                            .findViewById(R.id.idAdsReader);
                    List<String> lst = ReadTxtFile(urls.get(position));
                    mReader.setList(lst);
                    break;
            }
        }
        return convertView;
    }

    // 读取文本文件中的内容
    private List<String> ReadTxtFile(String strFilePath) {

        String path = strFilePath;
        String[] value1 = new String[1];
        CommenUnit.getCfgValueByName(path, "title", value1);

        String[] value2 = new String[1];
        CommenUnit.getCfgValueByName(path, "text", value2);

        List<String> lst = new ArrayList<String>();

        lst.add(value1[0] + "\n");
        lst.add(value2[0] + "\n");

        return lst;
    }
}
