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
	SoundPool sp; // ����SoundPool������
	int currStreamId;// ��ǰ�����ŵ�streamId
	int soundId;
	private boolean SoundrunFlag = false;

	private Camera camera;
	private FlashUpdater flashupdater;
	private boolean flashrunFlag = false;

	@Override
	// ��дonCreate����
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); // ����layout

		Soundinit(); // ��ʼ�������صķ���
		FlashInit();

		// �����¼�
		Button b1 = (Button) this.findViewById(R.id.Button01); // ��ȡ���Ű�ť
		b1.setOnClickListener // Ϊ���Ű�ť��Ӽ�����
		(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SoundOn(); // ����1��������Դ
				FlashOn();
			}
		});
		Button b2 = (Button) this.findViewById(R.id.Button02); // ��ȡֹͣ��ť
		b2.setOnClickListener // Ϊֹͣ��ť��Ӽ�����
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

	// ��ʼ�������صķ���
	public void Soundinit()
	{
		sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 0); // ����SoundPool����
		soundId = sp.load(this, R.raw.music, 1); // ���������ļ�musictest��������Ϊ1����������hm��
	}

	// ���������ķ���
	public void SoundOn()
	{ // ��ȡAudioManager����

		if (false == this.SoundrunFlag)
		{
			// 1.0��Ϊ�������
			float maxsound = 1.0f;
			// ���޲���
			int unlimitplay = -1;
			// ���ó������
			AudioManager am = (AudioManager) this
					.getSystemService(Context.AUDIO_SERVICE);
			;
			int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			// �������ֵ
			am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume,
					AudioManager.FLAG_PLAY_SOUND);

			// ����SoundPool��play���������������ļ� ����ǿ���������
			currStreamId = sp.play(soundId, maxsound, maxsound, 1, unlimitplay,
					1.0f);

			this.SoundrunFlag = true;
		}
	}

	public void SoundOff()
	{
		if (this.SoundrunFlag)
		{
			sp.stop(currStreamId); // ֹͣ���ڲ��ŵ�ĳ������
			this.SoundrunFlag = false;
		}
	}

	// �����
	public void FlashInit()
	{
		camera = Camera.open();
		// ������Ļ
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	// �ر�����ͷ
	public void FlashUnInit()
	{
		camera.release();

	}

	// ���������
	public void FlashOn()
	{
		/*
		 * Camera.Parameters param = camera.getParameters();
		 * param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		 * camera.setParameters(param);
		 */
		// �߳̿���
		if (false == this.flashrunFlag)
		{
			this.flashupdater = new FlashUpdater();
			this.flashrunFlag = true;
			this.flashupdater.start();
		}
	}

	// �ر�
	public void FlashOff()
	{
		/*
		 * Camera.Parameters param = camera.getParameters();
		 * param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		 * camera.setParameters(param);
		 */
		// �̹߳ر�
		if (this.flashrunFlag)
		{
			this.flashrunFlag = false;
			this.flashupdater.interrupt();
			this.flashupdater = null;
		}
		// �����߳���û�˳����ر�����ͷ
		Camera.Parameters param = camera.getParameters();
		param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		camera.setParameters(param);
		Log.d("aa", "off");
	}

	private class FlashUpdater extends Thread
	{
		// ��ʼ���߳�

		public FlashUpdater()
		{
			super("Flash-Updater");
		}

		// ִ���߳�
		@Override
		public void run()
		{
			zogalarm_Activity zogalarmactivity = zogalarm_Activity.this;
			// �õ���ʼ����
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
					// �������ߣ���Ȼ�޷����յ���Ϣ
					Thread.sleep(40);
					Log.d("aa", "run");
				} catch (InterruptedException e)
				{
					zogalarmactivity.flashrunFlag = false;
					// �ر��߳��Ժ�Ĺر�����ƵĲ������ⲿִ��
					Log.d("aa", "intt");
				}
			}
			Log.d("aa", "stop");
		}
	} // Updater

}