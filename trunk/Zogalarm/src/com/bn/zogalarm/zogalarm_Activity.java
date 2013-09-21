package com.bn.zogalarm;

import com.bn.zogalarm.R;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class zogalarm_Activity extends Activity
{
	SoundPool sp; // 声明SoundPool的引用
	int currStreamId;// 当前正播放的streamId
	int soundId;
	private boolean SoundrunFlag = false;

	private Camera camera;
	private FlashUpdater flashupdater;
	private boolean flashrunFlag = false;

	@Override
	// 重写onCreate方法
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); // 设置layout

		Soundinit(); // 初始化声音池的方法
		FlashInit();

		// 按键事件
		Button b1 = (Button) this.findViewById(R.id.Button01); // 获取播放按钮
		b1.setOnClickListener // 为播放按钮添加监听器
		(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SoundOn(); // 播放1号声音资源
				FlashOn();
			}
		});
		Button b2 = (Button) this.findViewById(R.id.Button02); // 获取停止按钮
		b2.setOnClickListener // 为停止按钮添加监听器
		(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SoundOff();
				FlashOff();
			}
		});
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();

		SoundOff();
		FlashOff();
		FlashUnInit();
		Log.d("aa", "des");
	}

	// 初始化声音池的方法
	public void Soundinit()
	{
		sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 0); // 创建SoundPool对象
		soundId = sp.load(this, R.raw.music, 1); // 加载声音文件musictest并且设置为1号声音放入hm中
	}

	// 播放声音的方法
	public void SoundOn()
	{ // 获取AudioManager引用

		if (false == this.SoundrunFlag)
		{
			// 1.0即为最大音量
			float maxsound = 1.0f;
			// 无限播放
			int unlimitplay = -1;
			// 设置成最大声
			AudioManager am = (AudioManager) this
					.getSystemService(Context.AUDIO_SERVICE);
			;
			int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			// 最大音量值
			am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume,
					AudioManager.FLAG_PLAY_SOUND);

			// 调用SoundPool的play方法来播放声音文件 可以强制最大音量
			currStreamId = sp.play(soundId, maxsound, maxsound, 1, unlimitplay,
					1.0f);

			this.SoundrunFlag = true;
		}
	}

	public void SoundOff()
	{
		if (this.SoundrunFlag)
		{
			sp.stop(currStreamId); // 停止正在播放的某个声音
			this.SoundrunFlag = false;
		}
	}

	// 闪光灯
	public void FlashInit()
	{
		camera = Camera.open();
		// 保持屏幕
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	// 关闭摄像头
	public void FlashUnInit()
	{
		camera.release();

	}

	// 开启闪光灯
	public void FlashOn()
	{
		/*
		 * Camera.Parameters param = camera.getParameters();
		 * param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		 * camera.setParameters(param);
		 */
		// 线程开启
		if (false == this.flashrunFlag)
		{
			this.flashupdater = new FlashUpdater();
			this.flashrunFlag = true;
			this.flashupdater.start();
		}
	}

	// 关闭
	public void FlashOff()
	{
		/*
		 * Camera.Parameters param = camera.getParameters();
		 * param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		 * camera.setParameters(param);
		 */
		// 线程关闭
		if (this.flashrunFlag)
		{
			this.flashrunFlag = false;
			this.flashupdater.interrupt();
			this.flashupdater = null;
		}
		// 无论线程有没退出。关闭摄像头
		Camera.Parameters param = camera.getParameters();
		param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		camera.setParameters(param);
		Log.d("aa", "off");
	}

	private class FlashUpdater extends Thread
	{
		// 初始化线程

		public FlashUpdater()
		{
			super("Flash-Updater");
		}

		// 执行线程
		@Override
		public void run()
		{
			zogalarm_Activity zogalarmactivity = zogalarm_Activity.this;
			// 得到初始参数
			Camera.Parameters param;
			param = camera.getParameters();
			while (zogalarmactivity.flashrunFlag)
			{
				try
				{
					param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
					camera.setParameters(param);
					//
					Thread.sleep(40);
					param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					camera.setParameters(param);
					// 必须休眠，不然无法接收到消息
					Thread.sleep(40);
					Log.d("aa", "run");
				} catch (InterruptedException e)
				{
					zogalarmactivity.flashrunFlag = false;
					// 关闭线程以后的关闭闪光灯的操作由外部执行
					Log.d("aa", "intt");
				}
			}
			Log.d("aa", "stop");
		}
	} // Updater

}