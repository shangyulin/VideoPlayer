package com.example.shang.radiodemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by Shang on 2017/10/12.
 */
public class MainActivity extends Activity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    private String path = "http://baobab.wdjcdn.com/145076769089714.mp4";//视频地址
    private Uri uri;
    private ProgressBar pb;
    private TextView downloadRateView, loadRateView;
    private CustomMediaController mCustomMediaController;
    private VideoView mVideoView;
    private RecyclerView list;

    private List<Video> data = new ArrayList<>();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //设置视频解码监听
        if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }
        initView();
        initData();
    }

    //初始化控件
    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.buffer);// 播放界面
        // 手势控制声音、屏幕亮度
        mCustomMediaController = new CustomMediaController(this, mVideoView, this);
        // 设置视频名称
        mCustomMediaController.setVideoName("闹腾音乐");
        // 缓冲是显示的进度条
        pb = (ProgressBar) findViewById(R.id.probar);
        // 网速
        downloadRateView = (TextView) findViewById(R.id.download_rate);
        // 加载进度
        loadRateView = (TextView) findViewById(R.id.load_rate);

        list = (RecyclerView) findViewById(R.id.list);
    }

    //初始化数据
    private void initData() {
        uri = Uri.parse(path);
        mVideoView.setVideoURI(uri);//设置视频播放地址
        mCustomMediaController.show(5000);// 控制界面显示时常，若没有操作5秒后退出
        mVideoView.setMediaController(mCustomMediaController);
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//高画质
        mVideoView.requestFocus();
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);// 设置重放速度
            }
        });

        Video video = new Video("http://img4.jiecaojingxuan.com/2016/11/23/00b026e7-b830-4994-bc87-38f4033806a6.jpg@!640_360"
                , "http://video.jiecao.fm/11/23/xin/%E5%81%87%E4%BA%BA.mp4", "搞笑一");
        Video video1 = new Video("http://img4.jiecaojingxuan.com/2016/3/14/2204a578-609b-440e-8af7-a0ee17ff3aee.jpg"
                , "http://gslb.miaopai.com/stream/ed5HCfnhovu3tyIQAiv60Q__.mp4", "搞笑二");
        Video video2 = new Video("http://img4.jiecaojingxuan.com/2016/11/23/00b026e7-b830-4994-bc87-38f4033806a6.jpg@!640_360"
                , "http://video.jiecao.fm/11/23/xin/%E5%81%87%E4%BA%BA.mp4", "搞笑三");
        Video video3 = new Video("http://img4.jiecaojingxuan.com/2016/11/23/00b026e7-b830-4994-bc87-38f4033806a6.jpg@!640_360"
                , "http://video.jiecao.fm/11/23/xin/%E5%81%87%E4%BA%BA.mp4", "搞笑四");
        Video video4 = new Video("http://img4.jiecaojingxuan.com/2016/11/23/00b026e7-b830-4994-bc87-38f4033806a6.jpg@!640_360"
                , "http://video.jiecao.fm/11/23/xin/%E5%81%87%E4%BA%BA.mp4", "搞笑五");
        Video video5 = new Video("http://img4.jiecaojingxuan.com/2016/11/23/00b026e7-b830-4994-bc87-38f4033806a6.jpg@!640_360"
                , "http://video.jiecao.fm/11/23/xin/%E5%81%87%E4%BA%BA.mp4", "搞笑六");
        data.add(video);
        data.add(video1);
        data.add(video2);
        data.add(video3);
        data.add(video4);
        data.add(video5);
        adapter = new MyAdapter(this, data);
        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(list);
        // 滑动删除时调用确认处理
        callback.setOnRemoveConfirmListener(new SimpleItemTouchHelperCallback.OnRemoveConfirmListener() {
            @Override
            public void onremove(final int position) {
                final Video content = data.get(position);
                data.remove(position);
                adapter.notifyItemRemoved(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("warn")
                        .setMessage("确定删除")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                data.add(position, content);
                                adapter.notifyItemInserted(position);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        adapter.setRecycleItemClickListener(new MyAdapter.OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String data) {
                TextView text = (TextView) view.findViewWithTag(position);
                text.setTextColor(Color.RED);

                mVideoView.setVideoURI(Uri.parse(data));
                mVideoView.start();
            }
        });
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    pb.setVisibility(View.VISIBLE);
                    downloadRateView.setText("");
                    loadRateView.setText("");
                    downloadRateView.setVisibility(View.VISIBLE);
                    loadRateView.setVisibility(View.VISIBLE);

                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                // 加载完成后播放视频
                mVideoView.start();
                pb.setVisibility(View.GONE);
                downloadRateView.setVisibility(View.GONE);
                loadRateView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                downloadRateView.setText("" + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        loadRateView.setText(percent + "%");
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //屏幕切换时，设置全屏
        if (mVideoView != null) {
            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        long sec = outState.getLong("time");
        mVideoView.seekTo(sec);
        mVideoView.start();
        super.onRestoreInstanceState(outState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        long sec = mVideoView.getCurrentPosition();
        outState.putLong("time", sec);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
