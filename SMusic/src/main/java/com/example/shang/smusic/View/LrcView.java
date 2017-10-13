package com.example.shang.smusic.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.shang.smusic.Utils.LyrcUtil;
import com.example.shang.smusic.bean.Lyrc;

import java.io.File;
import java.util.List;

/**
 * Created by Shang on 2017/10/11.
 */
public class LrcView extends TextView {

    private List<Lyrc> lyrcList;

    private int curLinePosition = 0;
    private int linePadding = 40;

    private Paint curPaint;
    private int curTextColor = Color.RED;
    private float curTextSize = 40;
    private Typeface curTypeface = Typeface.DEFAULT_BOLD;

    private Paint ortherPaint;
    private int ortherColor = Color.BLACK;
    private int ortherSize = 25;
    private Typeface ortherTypeface = Typeface.SERIF;

    private Handler hanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            invalidate();
        }
    };

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        lyrcList = LyrcUtil.readLRC(new File(Environment.getExternalStorageDirectory(), "a.lrc"));
        // 高亮画笔
        curPaint = new Paint();
        curPaint.setColor(curTextColor);
        curPaint.setTextSize(curTextSize);
        curPaint.setTextAlign(Paint.Align.CENTER);
        curPaint.setTypeface(curTypeface);
        // 常规画笔
        ortherPaint = new Paint();
        ortherPaint.setColor(ortherColor);
        ortherPaint.setTextSize(ortherSize);
        ortherPaint.setTextAlign(Paint.Align.CENTER);
        ortherPaint.setTypeface(ortherTypeface);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (curLinePosition < lyrcList.size()){
            if (lyrcList != null && lyrcList.size() > 0){
                Lyrc lyrc = null;
                // 当前行之前的文本向上移动
                for (int i = curLinePosition - 1; i >= 0; i--) {
                    lyrc = lyrcList.get(i);
                    canvas.drawText(lyrc.lrcString, getWidth() / 2, getHeight() / 2 + linePadding * (i - curLinePosition), ortherPaint);
                }
                // 当前行
                lyrc = lyrcList.get(curLinePosition);
                canvas.drawText(lyrc.lrcString, getWidth() / 2, getHeight() / 2, curPaint);
                // 当前行之后的文本向上移动
                for (int i = curLinePosition + 1; i < lyrcList.size(); i++) {
                    lyrc = lyrcList.get(i);
                    canvas.drawText(lyrc.lrcString, getWidth() / 2, getHeight() / 2 + linePadding * (i - curLinePosition), ortherPaint);
                }
                lyrc=lyrcList.get(curLinePosition);
                hanlder.sendEmptyMessageDelayed(10, lyrc.sleepTime);
                curLinePosition++;
            }else {
                canvas.drawText("未找到歌词", getWidth() / 2, getHeight() / 2, curPaint);
            }
        }
    }
}
