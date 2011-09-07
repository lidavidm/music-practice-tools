//package com.proch.practicetools;
//
//import android.media.AudioFormat;
//import android.media.AudioManager;
//import android.media.AudioTrack;
//
//public class RawAudioPlayer {
//
//	public static final int SAMPLE_RATE = 8000;// / 2;
//	public static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
//	public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_CONFIGURATION_MONO;
//	public static final int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG,
//			ENCODING);
//
//	private AudioTrack track;
//	private short[] buffer = new short[1024];
//
//	public RawAudioPlayer() {
//		track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG, ENCODING,
//				BUFFER_SIZE*2, AudioTrack.MODE_STREAM);
//		track.play();
//	}
//
//	public void close() {
//		track.release();
//	}
//
//	public void writeSamples(float[] samples) {
//		fillGeneratedBuffer(samples);
//		track.write(buffer, 0, buffer.length);
//	}
//
//	// public void playSamples(byte[] samples) {
//	// track.write(samples, 0, samples.length);
//	// }
//
//	private void fillGeneratedBuffer(float[] samples) {
//		if (buffer.length < samples.length)
//			buffer = new short[samples.length];
//
//		for (int i = 0; i < samples.length; i++)
//			buffer[i] = (short) (samples[i] * Short.MAX_VALUE);
//	}
//
//	public void writeBytes(byte[] samples) {
//		track.write(samples, 0, samples.length);
//	}
//	
//	public void writeShorts(short[] samples) {
//		track.write(samples, 0, samples.length);
//	}
//
//}
