package com.future.study.android.listview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class MyListAdapter extends BaseAdapter implements View.OnClickListener{
    private final static String TAG = MyListAdapter.class.getSimpleName();
    private Context context = null;
    private List<Map<String, Object>> datas = new ArrayList<>();

    /**
     *
     * @param context
     */
    public MyListAdapter(Context context) {
        this.context = context;
    }

    /**
     *
     * @param data
     */
    public void add(Map<String, Object> data) {
        this.datas.add(data);
    }

    /**
     *
     */
    public void removeLast() {
        this.datas.remove(this.datas.size()-1);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (Long)datas.get(i).get("id");
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        if(view == null) {
            view = layoutInflater.inflate(R.layout.layout_listview_row, null);
        }

        Map<String, Object> data = datas.get(i);
        String nickname = (String)data.get("nickname");

        TextView textView = view.findViewById(R.id.textViewNickname);
        textView.setText(nickname);

        Button buttonAgree = view.findViewById(R.id.buttonAgree);
        buttonAgree.setOnClickListener(this);
        buttonAgree.setTag(data);

        view.setTag(data);
        return view;
    }

    @Override
    public void onClick(View view) {
        Object data = view.getTag();
        Log.i(TAG, data.toString());
    }
}
