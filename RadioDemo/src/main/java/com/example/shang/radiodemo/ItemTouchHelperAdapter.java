package com.example.shang.radiodemo;

/**
 * Created by Shang on 2017/6/5.
 */

public interface ItemTouchHelperAdapter {

    // 移动
    void onItemMove(int fromPosition, int toPosition);

    // 删除
    void onItemDismiss(int position);
}
