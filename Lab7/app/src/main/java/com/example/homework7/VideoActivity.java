package com.example.homework7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback{

    String mockUrl = "https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";
    private boolean ISFULLSCREEN = false,ISCHANGING = false;
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private ImageButton btnPlayVideo,btnMaxSize;
    private RelativeLayout rl_group;
    private SeekBar mSeekBar;
    private ImageView PauseImage;
    private TextView current,total;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private ActionBar actionBar;


    @SuppressLint("HandlerLeak")
    Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 100:
                    findViewById(R.id.MediaUI).setVisibility(View.GONE);
                    break;
                case 200:
                    try {
                        mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                default:
                    break;
            }

        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initbinding();
        surfaceHolderInit();
        viewsAddListener();
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);
    }

    private void initbinding() {
        mSurfaceView = findViewById(R.id.vv_detail);
        btnPlayVideo = findViewById(R.id.bt_pause);
        btnMaxSize = findViewById(R.id.bt_maxsize);
        PauseImage = findViewById(R.id.image_clickplay_moren);
        mSeekBar = findViewById(R.id.seekBar);
        current = findViewById(R.id.tv_currentProgress);
        total = findViewById(R.id.tv_totalProgress);
        rl_group = findViewById(R.id.rl_group);
        mSeekBar.setOnSeekBarChangeListener(new MySeekbar());
        actionBar = getSupportActionBar();
    }

    private void surfaceHolderInit() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mMediaPlayer = new MediaPlayer();
        mSurfaceView.setKeepScreenOn(true);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(100), 3000);
    }

    private void viewsAddListener() {
        btnPlayVideo.setOnClickListener(this);
        btnMaxSize.setOnClickListener(this);
    }

    public boolean onTouchEvent(android.view.MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                findViewById(R.id.MediaUI).setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessageDelayed(100, 3000);
                break;

            default:
                break;
        }
        return true;
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mTimer.cancel();
        mTimerTask.cancel();
        mMediaPlayer.release();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (mockUrl != null || !"".equals(mockUrl)) {
            new Thread(){
                public void run() {

                    playVideo();
                };
            }.start();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    protected void playVideo() {
        try {
            mMediaPlayer.reset(); // 重置
            mMediaPlayer.setDataSource(mockUrl);
            // 视频地址
            mMediaPlayer.setDisplay(mSurfaceHolder); // holder
            mMediaPlayer.setOnPreparedListener(new PreparedListener()); //
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            long mills = mMediaPlayer.getDuration();
            SimpleDateFormat formatter;
            if(mills/1000>=3600) formatter = new SimpleDateFormat("HH:mm:ss");
            else formatter = new SimpleDateFormat("mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            String str = formatter.format(mills);
            total.setText(str);
            mSeekBar.setMax(mMediaPlayer.getDuration());
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if(ISCHANGING==true) {
                        return;
                    }
                    mHandler.sendEmptyMessage(200);
                }
            };
            mTimer.schedule(mTimerTask, 0, 10);
        }catch (IOException e) {
            Log.e("918", e.toString());
        }
    }

    class PreparedListener implements MediaPlayer.OnPreparedListener {
        int postSize;
        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.e("onPrepared", "----onPrepared");
            PauseImage.setVisibility(View.GONE); // 取消dialog
            btnPlayVideo.setImageResource(R.mipmap.pause);
            if (mMediaPlayer != null) {
                mMediaPlayer.start(); // 播放
                mHandler.sendMessageDelayed(mHandler.obtainMessage(100), 3000);
            } else {
                return;
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_pause: {
                if(mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    btnPlayVideo.setImageResource(R.mipmap.play);
                    PauseImage.setVisibility(View.VISIBLE);
                }else {
                    mMediaPlayer.start();
                    btnPlayVideo.setImageResource(R.mipmap.pause);
                    PauseImage.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.bt_maxsize: {
                if(ISFULLSCREEN) {
                    ISFULLSCREEN = false;
                    if(actionBar!=null) actionBar.show();
                    btnMaxSize.setImageResource(R.mipmap.fullscreen);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            dp2px(VideoActivity.this, 250));
                    rl_group.setLayoutParams(lp);
                }else { // 非全屏切换全屏
                    ISFULLSCREEN = true;
                    if (Build.VERSION.SDK_INT < 16) {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    }else {
                        View decorView = getWindow().getDecorView();
                        // Hide the status bar.
                        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                        decorView.setSystemUiVisibility(uiOptions);
                    }
                    if(actionBar!=null) actionBar.hide();
                    btnMaxSize.setImageResource(R.mipmap.delete);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    rl_group.setLayoutParams(lp);
                }
                break;
            }
            default:break;
        }
    }

    class MySeekbar implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            int position = mMediaPlayer.getCurrentPosition();//获取当前播放的位置
            SimpleDateFormat formatter;
            if(position/1000>=3600) formatter = new SimpleDateFormat("HH:mm:ss");
            else formatter = new SimpleDateFormat("mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            String str = formatter.format(position);
            current.setText(str);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            ISCHANGING=true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            mMediaPlayer.seekTo(mSeekBar.getProgress());
            ISCHANGING=false;
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ISFULLSCREEN = true;
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }else {
                View decorView = getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
            if(actionBar!=null) actionBar.hide();
            btnMaxSize.setImageResource(R.mipmap.delete);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            rl_group.setLayoutParams(lp);
        } else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            ISFULLSCREEN = false;
            if(actionBar!=null) actionBar.show();
            btnMaxSize.setImageResource(R.mipmap.fullscreen);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    dp2px(VideoActivity.this, 250));
            rl_group.setLayoutParams(lp);
        }
    }
}