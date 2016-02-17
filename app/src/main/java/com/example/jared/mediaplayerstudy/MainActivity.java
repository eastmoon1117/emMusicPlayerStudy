package com.example.jared.mediaplayerstudy;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button playMusic;
    private Button pauseMusic;
    private Button stopMusic;

    private TextView totalTime_text;
    private TextView playingTime_text;

    private SeekBar playingProcess;

    private int totalTime = 0;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private Handler hangler = new Handler();
    private boolean flag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playMusic = (Button)findViewById(R.id.play);
        playMusic.setOnClickListener(new myOnClickListener());

        pauseMusic = (Button)findViewById(R.id.pause);
        pauseMusic.setOnClickListener(new myOnClickListener());

        playingProcess = (SeekBar)findViewById(R.id.seek);
        playingProcess.setOnSeekBarChangeListener(new mySeekBarListener());

        totalTime_text = (TextView)findViewById(R.id.totalTime);
        playingTime_text = (TextView)findViewById(R.id.playingTime);

        initMediaPlayer();
    }

    public void setTotalTime() {
        totalTime = mediaPlayer.getDuration() / 1000;
        Log.d("MediaPlayerTest", String.valueOf(totalTime));
        String pos = String.valueOf(totalTime/60/10)+String.valueOf(totalTime/60%10)
                +':'+String.valueOf(totalTime%60/10)+String.valueOf(totalTime%60%10);
        totalTime_text.setText(pos);
        playingProcess.setProgress(0);
        playingProcess.setMax(totalTime);
    }

    public void updateTimepos() {
        int timepos = playingProcess.getProgress()+1;
        if(timepos >= totalTime-1) {
            timepos = 0;
            flag = false;
        }
        playingProcess.setProgress(timepos);
        String pos = String.valueOf(timepos/60/10)+String.valueOf(timepos/60%10)
                +':'+String.valueOf(timepos%60/10)+String.valueOf(timepos%60%10);
        playingTime_text.setText(pos);

    }

    public void initMediaPlayer() {
        try {
            String file_path = "/sdcard/qqmusic/song/daoxiang.mp3";
            //File file = new File(Environment.getExternalStorageDirectory(), "music.mp3");
            mediaPlayer.setDataSource(file_path);
            mediaPlayer.prepare();
            setTotalTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshTimepos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(flag && playingProcess.getProgress()<totalTime-1) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    hangler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateTimepos();
                        }
                    });
                }
            }
        }).start();
    }

    private class mySeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            updateTimepos();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo( playingProcess.getProgress()*1000);
            updateTimepos();
        }
    }

    private class myOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.play:
                    setPlayMusic();
                    break;
                case R.id.pause:
                    setPauseMusic();
                    break;
            }
        }
    }

    public void setPlayMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            flag = true;
            refreshTimepos();
        }
    }

    public void setPauseMusic() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            flag = false;
        }
    }
}
