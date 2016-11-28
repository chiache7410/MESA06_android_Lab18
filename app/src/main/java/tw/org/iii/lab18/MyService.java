package tw.org.iii.lab18;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private MediaPlayer mp;
    private Timer timer;
    public MyService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mp = MediaPlayer.create(this, R.raw.music);
        int len = mp.getDuration();
        Intent it = new Intent("servicemusic");
        it.putExtra("len", len);
        sendBroadcast(it);
        timer = new Timer();
        timer.schedule(new MyTask(),0,500);
    }
    private class MyTask extends TimerTask {
        @Override
        public void run() {
            if (mp != null && mp.isPlaying()) {
                Intent it = new Intent("servicemusic");
                it.putExtra("now", mp.getCurrentPosition());
                sendBroadcast(it);
            }
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isPause = intent.getBooleanExtra("isPause", false);
        int seekto = intent.getIntExtra("seekto", -1);
        if (mp != null && seekto>=0) {
            mp.seekTo(seekto);
        }
        if (mp != null && !mp.isPlaying()) {
            if (!isPause) {
                mp.start();
            }
        } else if (mp != null && mp.isPlaying()) {
            if (isPause) {
                mp.pause();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }
        if (timer != null){
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }
}
