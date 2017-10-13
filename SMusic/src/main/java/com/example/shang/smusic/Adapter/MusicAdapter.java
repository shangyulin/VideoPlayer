package com.example.shang.smusic.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shang.smusic.ConstantValue;
import com.example.shang.smusic.R;
import com.example.shang.smusic.Utils.MediaUtil;

/**
 * Created by Shang on 2017/10/11.
 */
public class MusicAdapter extends BaseAdapter {

    private Context context;

    public MusicAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return MediaUtil.getInstacen().getSongList().size();
    }

    @Override
    public Object getItem(int position) {
        return MediaUtil.getInstacen().getSongList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        public TextView tx1;
        public TextView tx2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.listitem, null);
            holder.tx1 = (TextView) view.findViewById(R.id.ListItemName);
            holder.tx2 = (TextView) view.findViewById(R.id.ListItemContent);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = (ViewHolder) view.getTag();
            holder.tx1.setTextColor(Color.WHITE);
        }
        if (position == MediaUtil.CURRENTPOS
                && (MediaUtil.PLAYSTATE == ConstantValue.OPTION_PAUSE || MediaUtil.PLAYSTATE == ConstantValue.OPTION_PLAY)) {
            holder.tx1.setTextColor(Color.GREEN);
        }
        holder.tx1.setTag(position);// 修改颜色用
        holder.tx1.setText((position + 1) + "." + MediaUtil.getInstacen().getSongList().get(position).getTitle());
        holder.tx2.setText((MediaUtil.getInstacen().getSongList().get(position)).getArtist());
        return view;
    }
}
