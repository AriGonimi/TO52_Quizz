package fr.utbm.tx.quizz.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import fr.utbm.tx.quizz.R;

public class ModeActivity extends Activity {
	private Button exam=null;
	private Button training=null;
	private Button cours = null;
	private TextToSpeech tts;
	private SpeechRecognizer speechRecognizer;
	private Intent intent;

	@Override
	protected  void onPause(){
		if (tts != null) {
			tts.stop();
		}
		super.onPause();
	}

	@Override
	protected  void onStop(){
		if (tts != null) {
			tts.stop();
		}
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_mode);
		exam=(Button)findViewById(R.id.btn_exam);
		training=(Button)findViewById(R.id.btn_training);
		cours = (Button)findViewById(R.id.btn_cours);
		exam.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Initialisation de la prochaine activité
				Intent myIntent = new Intent(getBaseContext(), JouerActivity.class);
				myIntent.putExtra("TAG", "Examen");
				tts.stop();
				startActivity(myIntent);
			}

		});

		training.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getBaseContext(), TrainingActivity.class);
				tts.stop();
				startActivity(myIntent);
			}
		});

		cours.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getBaseContext(), CoursActivity.class);
				tts.stop();
				startActivity(myIntent);
			}
		});

		initVoiceRecognizer();

		tts = new TextToSpeech(getBaseContext(),new TextToSpeech.OnInitListener() {

			@SuppressLint("NewApi")
					/*
					 * TextToSpeech initialization Can take few seconds to be
					 * initialized
					 */
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {

							/* HashMap used by setOnUtteranceProgressListener */
					HashMap<String, String> map = new HashMap<String, String>();

					map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
							"UniqueID");

					tts.setLanguage(Locale.FRANCE);

					tts.speak("Examen",TextToSpeech.QUEUE_ADD, map);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					tts.speak("Entrainemen",TextToSpeech.QUEUE_ADD, map);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					tts.speak("Cours",TextToSpeech.QUEUE_ADD, map);
					tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

						/*
                         * When speak is done wait for 2sec and start
                         * startListening(imageView)
                         */
						@Override
						public void onDone(String utteranceId) {

							if (utteranceId.equals("UniqueID")) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onError(String utteranceId) {
						}

						@Override
						public void onStart(String utteranceId) {
						}
					});
				}
			}
		});
	}

	private void initVoiceRecognizer(){
		speechRecognizer = getSpeechRecognizer();
		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
	}

	public void startListening(View v){
		tts.stop();
		if(speechRecognizer == null){
			speechRecognizer.cancel();
		}
		speechRecognizer.startListening(intent);
	}

	private SpeechRecognizer getSpeechRecognizer(){
		if(speechRecognizer == null){
			speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
			speechRecognizer.setRecognitionListener(new VoiceListener());
		}
		return speechRecognizer;
	}

	class VoiceListener implements RecognitionListener {
		public void onReadyForSpeech(Bundle params){}
		public void onBeginningOfSpeech(){}
		public void onRmsChanged(float rmsdB){}
		public void onBufferReceived(byte[] buffer){}
		public void onEndOfSpeech(){
			Log.d("TAG", "onEndofSpeech");
		}

		public void  onError(int error){
			Log.v("TAG", "error: " + error);
		}

		public void onResults(Bundle results){
			//String str = new String();
			Log.v("TAG", "error: " + results);
			ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			/*for (int i = 0; i < data.size(); i++) {
				Log.v("TAG", "result: " + data.get(i));
				str += data.get(i);
			}*/
			String textResult = data.get(0).toString().toLowerCase();
			Log.v("TAG", "result 10000ffg: " + textResult);
			if(textResult.equalsIgnoreCase("examen"))
				exam.performClick();
			else if (textResult.equalsIgnoreCase("entraînement"))
				training.performClick();
			else if (textResult.equalsIgnoreCase("cours"))
				cours.performClick();
		}

		public void onPartialResults(Bundle partialResults){}
		public void onEvent(int eventType, Bundle params){}
	}
}
