package com.hdyl.pushbox;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class SoundService extends Service {
	private MediaPlayer mp;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mp != null) {
			mp.release();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			boolean playing = intent.getBooleanExtra("playing", false);
			if (mp != null) {
				mp.release();
			}
			if (playing) {
				int arr[] = { R.raw.m2, R.raw.mainmusic1 };
				mp = MediaPlayer.create(this, arr[new Random().nextInt(arr.length)]);
				mp.setLooping(true);
				mp.start();
			} else {
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}