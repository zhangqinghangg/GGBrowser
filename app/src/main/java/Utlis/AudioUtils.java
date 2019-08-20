package Utlis;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by li on 2
 */

/**
 * 描述：语音播放工具类
 */
public class AudioUtils {
	private static AudioUtils audioUtils;
	private SpeechSynthesizer mySynthesizer;

	public AudioUtils() {

	}

	public static AudioUtils getInstance() {
		if (audioUtils == null) {
			synchronized (AudioUtils.class) {
				if (audioUtils == null) {
					audioUtils = new AudioUtils();
				}
			}
		}
		return audioUtils;
	}

	private InitListener myInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
		}
	};

	/**
	 * 描述:
	 */
	public void init(Context context, String pitch, String volume) {
		mySynthesizer = SpeechSynthesizer.createSynthesizer(context,
				myInitListener);
		// 设置发音人
		mySynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
		// 设置音调
		mySynthesizer.setParameter(SpeechConstant.PITCH, pitch);
		// 设置音量
		mySynthesizer.setParameter(SpeechConstant.VOLUME, volume);
	}

	/**
	 * 描述:根据传入的文本转换音频并播放
	 */
	public void speakText(String content) {
		int code = mySynthesizer.startSpeaking(content,
				new SynthesizerListener() {

					@Override
					public void onSpeakBegin() {

					}

					@Override
					public void onBufferProgress(int i, int i1, int i2, String s) {

					}

					@Override
					public void onSpeakPaused() {

					}

					@Override
					public void onSpeakResumed() {

					}

					@Override
					public void onSpeakProgress(int i, int i1, int i2) {

					}

					@Override
					public void onCompleted(SpeechError speechError) {

					}

					@Override
					public void onEvent(int i, int i1, int i2, Bundle bundle) {

					}
				});
	}

}