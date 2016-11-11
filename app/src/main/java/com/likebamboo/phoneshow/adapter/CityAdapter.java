
package com.likebamboo.phoneshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.likebamboo.phoneshow.R;
import com.likebamboo.phoneshow.entities.City;
import com.likebamboo.phoneshow.util.Utils;

import java.util.ArrayList;

public class CityAdapter extends BaseAdapter {

    private Context mContext = null;

    /**
     * 视图构造器
     */
    private LayoutInflater inflater = null;

    /**
     * 列数
     */
    private ArrayList<City> mDatas = new ArrayList<City>();

    public CityAdapter(Context context, ArrayList<City> datas) {
        super();
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.mDatas = datas;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDatas.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        if (arg0 < mDatas.size()) {
            return mDatas.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (v == null) {
            v = inflater.inflate(R.layout.city_list_item, null);
            holder = new ViewHolder();

            holder.nameTv = (TextView)v.findViewById(R.id.city_item_name_tv);
            holder.locTv = (TextView)v.findViewById(R.id.city_item_loc_tv);

            v.setTag(holder);
        } else {
            holder = (ViewHolder)v.getTag();
        }

        City item = mDatas.get(position);
        holder.nameTv.setText(item.getName());
        holder.locTv.setText(Utils.formatHtml(mContext.getString(R.string.city_loc,
                "<font color='green'>" + item.getLat() + "</font>",
                "<font color='green'>" + item.getLon() + "</font>")));

        return v;
    }

    private static class ViewHolder {
        private TextView nameTv = null;

        private TextView locTv = null;
    }
}
