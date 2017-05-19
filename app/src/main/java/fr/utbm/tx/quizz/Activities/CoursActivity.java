package fr.utbm.tx.quizz.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fr.utbm.tx.quizz.R;

public class CoursActivity extends Activity implements View.OnClickListener {
    private Button cours1=null;
    private Button cours2=null;
    private Button cours3=null;
    private Button cours4=null;
    private Button goCours=null;
    private List<String> nameCourse = new ArrayList<String>();
    private List<String> listId = new ArrayList<String>();
    private int numBtn =0;
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

        setContentView(R.layout.course_choice);
        cours1 = (Button) findViewById(R.id.cours1);
        cours2 = (Button) findViewById(R.id.cours2);
        cours3 = (Button) findViewById(R.id.cours3);
        cours4 = (Button) findViewById(R.id.cours4);

        cours1.setOnClickListener(this);
        cours2.setOnClickListener(this);
        cours3.setOnClickListener(this);
        cours4.setOnClickListener(this);

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

                    tts.speak("Développement durable",TextToSpeech.QUEUE_ADD, map);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    tts.speak("Stratégies et Gouvernances",TextToSpeech.QUEUE_ADD, map);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    tts.speak("Bonnes Pratiques",TextToSpeech.QUEUE_ADD, map);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    tts.speak("Energies, Air et Climat",TextToSpeech.QUEUE_ADD, map);
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        /*
                         * When speak is done wait for 2sec and start
                         * startListening(imageView)
                         */
                        @Override
                        public void onDone(String utteranceId) {

                            if (utteranceId.equals("UniqueID")) {
                                try {
                                    Thread.sleep(500);
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

    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.cours1):
                numBtn = 0;
                tts.stop();
                break;
            case (R.id.cours2):
                numBtn = 1;
                tts.stop();
                break;
            case (R.id.cours3):
                numBtn = 2;
                tts.stop();
                break;
            case (R.id.cours4):
                numBtn = 3;
                tts.stop();
                break;
        }
        new JSONCourse().execute("http://to52.julienpetit.fr/api/v1/learning/categories");
    }

    public class JSONCourse extends AsyncTask<String,String,List<String> > {
        @Override
        protected List<String> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                buffer.append("{\"JSON\":");
                String line = "" ;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                buffer.append("}");
                String finaljson = buffer.toString();
                JSONObject parentObject = new JSONObject(finaljson);
                JSONArray parentArray = parentObject.getJSONArray("JSON");
                JSONObject coursename = parentArray.getJSONObject(numBtn);
                try {
                    JSONArray children = coursename.getJSONArray("children");
                    for (int i = 0; i < children.length(); i++) {
                        JSONObject finalObject = children.getJSONObject(i);
                        String name = finalObject.getString("name");
                        String id = finalObject.getString("id");
                        // adding the final object in the list
                        nameCourse.add(name);
                        listId.add(id);
                    }
                } catch (Exception e) {
                    Log.d("ERROR", e.getMessage() + " - " + e.getStackTrace()[0].getClassName());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(List<String> result){
            super.onPostExecute(result);

            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.popupcourse, null);
            goCours = (Button) popupView.findViewById(R.id.goCours);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CoursActivity.this, android.R.layout.simple_spinner_item, nameCourse);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            final Spinner popupSpinner = (Spinner) popupView.findViewById(R.id.Spinner);
            popupSpinner.setAdapter(adapter);
            nameCourse = new ArrayList<String>();

            final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            goCours.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(getBaseContext(), LectureActivity.class);
                    myIntent.putExtra("Lecture",listId.get(popupSpinner.getSelectedItemPosition()));
                    startActivity(myIntent);
                }
            });
            popupWindow.showAsDropDown(cours1, 30, 0);
        }
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
            if(textResult.equalsIgnoreCase("Développement durable"))
                cours1.performClick();
            else if (textResult.equalsIgnoreCase("Stratégie et Gouvernance"))
                cours2.performClick();
            else if (textResult.equalsIgnoreCase("Bonne Pratique"))
                cours3.performClick();
            else if (textResult.equalsIgnoreCase("Energie, Air et Climat"))
                cours4.performClick();
            else if (textResult.equalsIgnoreCase("Lire le cours"))
                goCours.performClick();
        }

        public void onPartialResults(Bundle partialResults){}
        public void onEvent(int eventType, Bundle params){}
    }
}
