package com.future.study.android.gridview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class MyAdapter extends BaseAdapter {
    private Context context = null;
    private List<Map<String, Object>> datas = new ArrayList<>();

    /**
     *
     * @param context
     */
    public MyAdapter(Context context) {
        this.context = context;
    }

    /**
     *
     * @param data
     */
    public void add(Map<String, Object> data) {
        this.datas.add(data);
    }

    public void remove() {
        this.datas.remove(0);
    }

    @Override
    public int getCount() {
        return this.datas.size()+2;
    }

    @Override
    public Object getItem(int position) {
        if(position>this.datas.size()-1) {
            return null;
        }
        return this.datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        if(position>this.datas.size()-1) {
            return 0;
        }
        Map<String, Object> data = this.datas.get(position);
        return (Long)data.get("id");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if(position==this.datas.size()+1) {
            // 显示添加按钮
            convertView = inflater.inflate(R.layout.gridview_item_add, null);
        }else if(position==this.datas.size()) {
            // 显示删除按钮
            convertView = inflater.inflate(R.layout.gridview_item_remove, null);
        }else {
            convertView = inflater.inflate(R.layout.gridview_item, null);
            Map<String, Object> data = this.datas.get(position);
            String nickname = (String)data.get("nickname");
            TextView textViewNickname = convertView.findViewById(R.id.textViewNickname);
            textViewNickname.setText(nickname);
        }
        return convertView;
    }
}
