package com.example.shang.smusic.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.shang.smusic.ConstantValue;
import com.example.shang.smusic.Utils.HandlerManager;
import com.example.shang.smusic.Utils.MediaUtil;

import java.io.IOException;

/**
 * Created by Shang on 2017/10/11.
 */
public class MediaService extends Service implements MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static MediaPlayer player;

    private String file;

    private int postion = 0;// 播放进度

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (player == null) {
            player = new MediaPlayer();
            player.setOnSeekCompleteListener(this);
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        int option = intent.getIntExtra("option", -1);
        switch (option) {
            /**
             * 播放
             */
            case ConstantValue.OPTION_PLAY:
                file = intent.getStringExtra("file");// 获取文件的播放路径
                play(file);// 播放音乐
                MediaUtil.PLAYSTATE = option;// 修改当前状态
                break;
            /**
             * 暂停
             */
            case ConstantValue.OPTION_PAUSE:
                // 获取当前播放的进度
                postion = player.getCurrentPosition();
                pause();// 暂停音乐
                MediaUtil.PLAYSTATE = option;
                break;
            /**
             * 继续播放，从上次暂停的位置
             */
            case ConstantValue.OPTION_CONTINUE:
                playerToPosiztion(postion);
                // 判断文件是否为空
                if (file == "" || file == null) {
                    file = intent.getStringExtra("file");
                    play(file);
                } else {
                    player.start();
                }
                MediaUtil.PLAYSTATE = option;
                break;
            case ConstantValue.OPTION_UPDATE_PROGESS:
                playerToPosiztion(postion);
                break;
        }
    }

    private void playerToPosiztion(int posiztion) {
        if (posiztion > 0 && posiztion < player.getDuration()) {
            // 从指定的位置开始播放
            player.seekTo(posiztion);
        }
    }

    /**
     * 暂停音乐
     */
    private void pause() {
        // 判断音乐是否正在播放
        if (player != null && player.isPlaying()) {
            player.pause();// 暂停
        }
    }

    /**
     * 播放音乐
     */
    private void play(String path) {
        if (player == null){
            player = new MediaPlayer();
        }
        try {
            player.reset();// 重置
            player.setDataSource(path);// 设置播放文件
            player.prepare();// 完成播放前的准备工作
            player.start();// 播放音乐
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // 播放完成时发送handler消息，根据播放模式确定播放什么歌曲
        HandlerManager.getHandler().sendEmptyMessage(ConstantValue.PLAY_END);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
}
