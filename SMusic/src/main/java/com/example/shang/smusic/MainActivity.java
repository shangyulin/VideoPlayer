package com.example.shang.smusic;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shang.smusic.Adapter.MusicAdapter;
import com.example.shang.smusic.Receiver.ReflashReceiver;
import com.example.shang.smusic.Service.MediaService;
import com.example.shang.smusic.Utils.HandlerManager;
import com.example.shang.smusic.Utils.MediaUtil;
import com.example.shang.smusic.Utils.PromptManager;
import com.example.shang.smusic.bean.Music;

public class MainActivity extends Activity {

    private ListView listView;
    private MusicAdapter musicAdapter;

    /*************
     * 音乐控制
     ****************/
    private ImageView playPause;// 播放暂停
    private ImageView playNext;// 播放下一首
    private ImageView playPrev;// 播放上一首
    private ImageView playMode;// 修改播放模式

    private ImageView reflash;

    private ReflashReceiver receiver;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConstantValue.STARTED:
                    // 开始刷新播放列表界面
                    PromptManager.showProgressDialog(MainActivity.this);
                    break;
                case ConstantValue.FINISHED:
                    // 结束刷新播放列表界面
                    MediaUtil.getInstacen().initMusics(MainActivity.this);
                    PromptManager.closeProgressDialog();
                    musicAdapter.notifyDataSetChanged();
                    unregisterReceiver(receiver);
                    break;
                case ConstantValue.PLAY_END:
                    MediaUtil.CURRENTPOS++;
                    if (MediaUtil.CURRENTPOS < MediaUtil.getInstacen()
                            .getSongList().size()) {
                        Music music = MediaUtil.getInstacen().getSongList()
                                .get(MediaUtil.CURRENTPOS);
                        startPlayService(music, ConstantValue.OPTION_PLAY);
                        changeNotice(Color.RED);
                    }
                    break;
            }
        }
    };
    private ImageView lrc;

    private void changeNotice(int color) {
        // 根据位置找到具体的标签
        TextView tx = (TextView) listView
                .findViewWithTag(MediaUtil.CURRENTPOS);
        if (tx != null) {
            tx.setTextColor(color);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        HandlerManager.putHandler(handler);// 将主线程的handler保存

        listView = (ListView) findViewById(R.id.play_list);
        reflash = (ImageView) findViewById(R.id.title_right);
        lrc = (ImageView) findViewById(R.id.ImgMenu);
        init();
        mediaControl();
        setLitener();
    }

    private void setLitener() {
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (MediaUtil.PLAYSTATE) {
                    // 若当前状态为播放状态，则暂停
                    case ConstantValue.OPTION_PLAY:
                    case ConstantValue.OPTION_CONTINUE:
                        startPlayService(null, ConstantValue.OPTION_PAUSE);
                        // 更改播放图标状态
                        playPause.setImageResource(R.drawable.appwidget_pause);
                        break;
                    // 若当前状态为暂停，则播放
                    case ConstantValue.OPTION_PAUSE:
                        if (MediaUtil.CURRENTPOS >= 0
                                && MediaUtil.CURRENTPOS < MediaUtil.getInstacen()
                                .getSongList().size()) {
                            // 判断播放位置是否合法
                            // 播放音乐
                            startPlayService(MediaUtil.getInstacen().getSongList()
                                            .get(MediaUtil.CURRENTPOS),
                                    ConstantValue.OPTION_CONTINUE);
                            // 更改播放图标状态
                            playPause.setImageResource(R.drawable.img_playback_bt_play);

                        }
                        break;
                }
            }
        });

        playNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MediaUtil.getInstacen().getSongList().size() > MediaUtil.CURRENTPOS + 1) {
                    changeNotice(Color.WHITE);
                    MediaUtil.CURRENTPOS++;
                    startPlayService(
                            MediaUtil.getInstacen().getSongList()
                                    .get(MediaUtil.CURRENTPOS),
                            ConstantValue.OPTION_PLAY);
                    playPause.setImageResource(R.drawable.img_playback_bt_play);
                    MediaUtil.PLAYSTATE = ConstantValue.OPTION_PLAY;
                    changeNotice(Color.RED);
                }

            }
        });
        playPrev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MediaUtil.CURRENTPOS > 0) {
                    changeNotice(Color.WHITE);
                    MediaUtil.CURRENTPOS--;
                    startPlayService(
                            MediaUtil.getInstacen().getSongList()
                                    .get(MediaUtil.CURRENTPOS),
                            ConstantValue.OPTION_PLAY);
                    playPause.setImageResource(R.drawable.img_playback_bt_play);
                    MediaUtil.PLAYSTATE = ConstantValue.OPTION_PLAY;
                    changeNotice(Color.RED);
                }

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                changeNotice(Color.WHITE);

                MediaUtil.CURRENTPOS = position;
                Music music = MediaUtil.getInstacen().getSongList()
                        .get(MediaUtil.CURRENTPOS);
                startPlayService(music, ConstantValue.OPTION_PLAY);
                playPause.setImageResource(R.drawable.img_playback_bt_play);
                changeNotice(Color.RED);
            }
        });
        reflash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter intentFilter = new IntentFilter(
                        Intent.ACTION_MEDIA_SCANNER_STARTED);
                intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
                intentFilter.addDataScheme("file");
                receiver = new ReflashReceiver();
                registerReceiver(receiver, intentFilter);
                sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            }
        });
        lrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LrcActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startPlayService(Music music, int option) {
        Intent intent = new Intent(getApplicationContext(), MediaService.class);
        if (music != null) {
            intent.putExtra("file", music.getPath());
        }
        intent.putExtra("option", option);
        startService(intent);
    }

    private void mediaControl() {
        playPause = (ImageView) findViewById(R.id.imgPlay);
        playPrev = (ImageView) findViewById(R.id.imgPrev);
        playNext = (ImageView) findViewById(R.id.imgNext);

        if (MediaUtil.PLAYSTATE == ConstantValue.OPTION_PAUSE) {
            playPause.setImageResource(R.drawable.appwidget_pause);
        }
    }

    private void init() {
        loadSongList();
        musicAdapter = new MusicAdapter(this);
        listView.setAdapter(musicAdapter);

    }

    private void loadSongList() {
        new MyLoadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 加载本地音乐资源
     */
    class MyLoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            PromptManager.showProgressDialog(MainActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            MediaUtil.getInstacen().initMusics(MainActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            PromptManager.closeProgressDialog();
            musicAdapter.notifyDataSetChanged();
        }
    }
}
