package com.example.shang.radiodemo;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

/**
 * Created by Shang on 2017/6/5.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    public OnRemoveConfirmListener listener;

    public void setOnRemoveConfirmListener(OnRemoveConfirmListener listener) {
        this.listener = listener;
    }

    interface OnRemoveConfirmListener{
        void onremove(int position);
    }


    boolean flag = false;

    private final ItemTouchHelperAdapter mAdapter;

    public SimpleItemTouchHelperCallback(
            ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final RecyclerView.ViewHolder target) {
        // 调用adapter中的方法处理拖动
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        // 使用回调函数来处理删除操作
        if (listener!=null){
            listener.onremove(viewHolder.getAdapterPosition());
        }
    }
}
