package com.proch.practicetools;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

import com.google.common.primitives.Bytes;

public class Tuner extends Thread {
	static {
		System.loadLibrary("FFT");
	}

	public native double processSampleData(byte[] sample, int sampleRate);

	public native double processSampleDataAvg(byte[] sample, int sampleRate);

	public native void resetAverages();

	public native double[] processTest(byte[] sample, int sampleRate);
	// private static final int[] OPT_SAMPLE_RATES = { 11025, 8000, 22050, 44100
	// };
	// private static final int[] BUFFERSIZE_PER_SAMPLE_RATE = { 8 * 1024, 4 *
	// 1024, 16 * 1024,
	// 32 * 1024 };

	public double currentFrequency = 0.0;

	private static final int SAMPLE_RATE = 44100;
	private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private static final int READ_BUFFER_SIZE = 32 * 1024 * 2;
	private static final int PROCESS_BUFFER_SIZE = 2;
	private AudioRecord audioRecorder;
	private final Handler mHandler;
	private Runnable callback;

	public Tuner(Handler mHandler, Runnable callback) {
		this.mHandler = mHandler;
		this.callback = callback;
		audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG,
				ENCODING, SAMPLE_RATE * 6);
	}

	public void run() { // fft
		//resetAverages();
		audioRecorder.startRecording();
		byte[] readBuffer = new byte[READ_BUFFER_SIZE];
		byte[][] processBuffer = new byte[PROCESS_BUFFER_SIZE][READ_BUFFER_SIZE];
		int nextToFillIndex = 0;
		
		while (audioRecorder.read(readBuffer, 0, readBuffer.length) > 0) {
			long startTime = System.currentTimeMillis();
			processBuffer[nextToFillIndex++] = readBuffer;
			nextToFillIndex %= PROCESS_BUFFER_SIZE;

			currentFrequency = processSampleData(Bytes.concat(processBuffer), SAMPLE_RATE);
			System.out.println("process time  = " + (System.currentTimeMillis() - startTime));
			
			if (currentFrequency > 0) {
				mHandler.post(callback);
			}
		}
	}

	public void stopRunning() {
		super.stop();
		
	}
	
	public void close() {
		audioRecorder.stop();
		audioRecorder.release();
	}
}
