package fr.utbm.tx.quizz.Activities;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import fr.utbm.tx.quizz.Dao.Question_Dao;
import fr.utbm.tx.quizz.Dao.Theme_Dao;
import fr.utbm.tx.quizz.R;
import fr.utbm.tx.quizz.javabean.Question;
import fr.utbm.tx.quizz.javabean.Score;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JouerActivity extends Activity implements OnClickListener {

    public static int NB_QUESTION=15;

    private int num_question=1;
    private boolean answered=false;
    private boolean first=true;
    private float result;
    private int button_id;
    private int fontSize;
    private float[] scoreByCategory = new float[6];
    private int[] nbQuestionsByCategory = new int[6];

    private View relativeLayout = null;

    private TextView no_question = null;
    private TextView timerText = null;
    private TextView clickText = null;
    private TextView questionView = null;
    private TextView info = null;

    private RadioGroup radioGroup = null;
    private RadioButton rad1 = null;
    private RadioButton rad2 = null;
    private RadioButton rad3 = null;
    private RadioButton rad4 = null;


    private Button continuer = null;
    private ProgressBar progressBar = null;
    private Drawable image = null;
    private AlertDialog dialog= null;
    private List <Question> questionsList = new ArrayList<Question>();
    private Bundle extras= new Bundle();
    private String tagMode = null;
    private Menu menu = null;
    private RelativeLayout rLayout = null;
    private Theme_Dao tdao = new Theme_Dao();

    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private Intent intent;
    final HashMap<String, String> map = new java.util.HashMap<String, String>();
    private boolean active = true;






    /*
     * CountDownIimer of 30 sec
     * every second we print the time remaining and set the progressBar with this time
     */
    private CountDownTimer timer = new CountDownTimer(30000, 1000) {
        public void onTick(long millisUntilFinished) {
            progressBar.setProgress((int)millisUntilFinished/1000);
            timerText.setText(String.valueOf(millisUntilFinished / 1000));
        }

        /*
         * when time out you can't answer, and print the correct answer
         *
         */
        public void onFinish() {
            progressBar.setProgress(0);
            timerText.setText("Temps écoulé! Cliquez pour continuer.");
            if(active){
                tts.speak("Temps écoulé! Cliquez pour continuer",TextToSpeech.QUEUE_ADD, map);
                tts.speak(info.getText().toString(),TextToSpeech.QUEUE_ADD, map);
            }
            RadioButton radioButton;
            radioButton=(RadioButton) findViewById(button_id);
            radioButton.setBackgroundResource(R.drawable.button_good_answer);
            continuer.setVisibility(View.VISIBLE);
            info.setVisibility(View.VISIBLE);
            answered = true;
        }
    };

    @Override
    protected  void onPause(){
        if (tts != null) {
            tts.stop();
        }
        active = false;
        super.onPause();
    }


    @Override
    protected  void onStop(){
        if (tts != null) {
            tts.stop();
        }
        active = false;
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        tagMode = extras.getString("TAG");
        rLayout = (RelativeLayout) findViewById(R.id.act_jouer);
        fontSize = 18;
        // get 15 questions
    new JSONTask().execute("http://to52.julienpetit.fr/api/v1/quiz/questions?limit="+NB_QUESTION);

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
                    //tts.speak(questionsList.get(num_question-1).toString(),TextToSpeech.QUEUE_FLUSH, map);
                    tts.speak(questionView.getText().toString(), TextToSpeech.QUEUE_ADD, map);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //ajoute les entrées de menu_test à l'ActionBar
        getMenuInflater().inflate(R.menu.actionbar_jouer, menu);
        this.menu = menu;
        return true;
    }

    public void runningActivity(){
        NB_QUESTION = questionsList.size();
        setContentView(R.layout.activity_jouer);

        /**
         * Affichage
         */
        relativeLayout = (View)findViewById(R.id.act_jouer);

        radioGroup=(RadioGroup)findViewById(R.id.radioGroup1);
        radioGroup.setOnClickListener(this);
        questionView=(TextView)findViewById(R.id.question);
        no_question=(TextView) findViewById(R.id.no_question);
        clickText=(TextView) findViewById(R.id.click);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        rad1 = (RadioButton) findViewById(R.id.radio0);
        rad2 = (RadioButton) findViewById(R.id.radio1);
        rad3 = (RadioButton) findViewById(R.id.radio2);
        rad4 = (RadioButton) findViewById(R.id.radio3);

        dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Questionnaire");
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                // Go to home menu because JouerActivity closed, reactive the activity in suspend()
                dialog.cancel();
                finish();
            }
        });

        continuer=(Button) findViewById(R.id.continuer);
        continuer.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                tts.stop();
                //user has answered
                num_question++;
                if(num_question<=NB_QUESTION){
                    //print the next question
                    display(questionsList.get(num_question-1));
                    speakRadioButton(radioGroup);
                    answered=false;
                } else {
                    // End of the Quizz -- pop up score
                    if(first){
                        result=((result/NB_QUESTION)*100);


                        Score score = new Score(result/10);
                        String finalMessage;
                        finalMessage =  "Quizz terminé. Score : "+Math.round(result)+"%.";
                        Bundle b = getIntent().getExtras();
                        int typ_quizz = b.getInt("play_theme");
                        String title=b.getString("TAG");

                        int pos=tdao.GetItembyTitle(new String(title));
                        String s=b.getString("play_theme");
                        WriteScore(score.toString(),pos);
                        //WriteScore(score.toString(),5);
                        for (int i = 0; i < scoreByCategory.length; i++){
                            if (nbQuestionsByCategory[i]>0){
                                scoreByCategory[i] = result*100/nbQuestionsByCategory[i];
                            }

                        }
                        if (nbQuestionsByCategory[0]>0){
                            finalMessage += "<br>";
                            finalMessage += "Developpement Durable : <b>"+Math.round(scoreByCategory[0])+"%</b>";

                        }
                        if (nbQuestionsByCategory[1]>0){
                            finalMessage += "<br>";
                            finalMessage += "Empreinte Écologique : <b>"+Math.round(scoreByCategory[1])+"%</b>";

                        }
                        if (nbQuestionsByCategory[2]>0){
                            finalMessage += "<br>";
                            finalMessage += "Formation : <b>"+Math.round(scoreByCategory[2])+"%</b>";

                        }
                        if (nbQuestionsByCategory[3]>0){
                            finalMessage += "<br>";
                            finalMessage += "Énergie Renouvelable : <b>"+Math.round(scoreByCategory[3])+"%</b>";

                        }
                        if (nbQuestionsByCategory[4]>0){
                            finalMessage += "<br>";
                            finalMessage += "La Trame verte et bleue : <b>"+Math.round(scoreByCategory[4])+"%</b>";

                        }

                        try{
                            dialog.setMessage(Html.fromHtml(finalMessage));
                            tts.speak(finalMessage,TextToSpeech.QUEUE_FLUSH, map);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        dialog.show();
                        continuer.setVisibility(View.GONE);
                        info.setVisibility(View.GONE);
                        first = false;
                    }
                }
            }

        });

        image = this.getResources().getDrawable( R.drawable.ic_affichage_info );
        int h = image.getIntrinsicHeight();
        int w = image.getIntrinsicWidth();
        image.setBounds(0, 0, w, h);

        display(questionsList.get(num_question-1));
        speakRadioButton(radioGroup);
        // Timer
        timerText = (TextView)findViewById(R.id.timer);
        timer.start();
    }


    @Override
    public void onClick(View v) {
        //view GONE = invisible and no place taken in the view
        clickText.setVisibility(View.GONE);

        if(!answered){
            tts.stop();
            /*//user has already answered
            num_question++;
            if(num_question<=NB_QUESTION){
                //print the next question
                display(questionsList.get(num_question-1));
                answered=false;
            }
            else{
				// End of the Quizz -- pop up score
				if(first) {
					noteResult =  result;
					result=result*100/nbQuestions;
					Score score = new Score(noteResult);
					// get type quizz
					Bundle b = getIntent().getExtras();
					int typ_quizz = b.getInt("play_theme");
					// Write score file
					WriteScore(score.toString(),typ_quizz);
					// Dialog
					dialog.setMessage(Html.fromHtml("Quizz terminé. Score : <b>"+Math.round(result)+"%</b>."));
					dialog.show();
					help.setVisibility(View.GONE);
					info.setVisibility(View.GONE);
					first = false;
				}
			}
        }else{*/
            //check if the click is on a answer possibility
            if(v.getId()==R.id.radio0 || v.getId()==R.id.radio1 || v.getId()==R.id.radio2 ||v.getId()==R.id.radio3){
                RadioButton radioButton;

                if(v.getId()==button_id){
                    //good answer
                    result++;
                    tts.speak("Réponse Juste",TextToSpeech.QUEUE_FLUSH, map);
                    //scoreByCategory[questionsList.get(num_question-1).getQuestionCat()]++;

                    /*
                    clickText.setText("Bien joué! Cliquez pour continuer.");
                    clickText.setVisibility(View.VISIBLE);
                    */
                }
                else{
                    radioButton=(RadioButton) findViewById(v.getId());
                    radioButton.setBackgroundResource(R.drawable.button_bad_answer);
                    tts.speak("Réponse fausse",TextToSpeech.QUEUE_FLUSH, map);
                    /*
                    clickText.setText("Faux! Cliquez pour continuer.");
                    clickText.setVisibility(View.VISIBLE);
                    */
                }

                radioButton=(RadioButton) findViewById(button_id);
                radioButton.setBackgroundResource(R.drawable.button_good_answer);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                info.setVisibility(View.VISIBLE);
                continuer.setVisibility(View.VISIBLE);
                answered=true;
                //stop the timer
                timer.cancel();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                tts.speak(info.getText().toString(),TextToSpeech.QUEUE_ADD, map);
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_textsize_plus: {
                if (fontSize < 25) {
                    fontSize++;
                    questionView.setTextSize(fontSize);
                    clickText.setTextSize(fontSize);
                    info.setTextSize(fontSize);
                    rad1.setTextSize(fontSize);
                    rad2.setTextSize(fontSize);
                    rad3.setTextSize(fontSize);
                    rad4.setTextSize(fontSize);
                }

                return true;
            }
            case R.id.action_textsize_minus: {
                if (fontSize > 7) {
                    fontSize--;
                    questionView.setTextSize(fontSize);
                    clickText.setTextSize(fontSize);
                    info.setTextSize(fontSize);
                    rad1.setTextSize(fontSize);
                    rad2.setTextSize(fontSize);
                    rad3.setTextSize(fontSize);
                    rad4.setTextSize(fontSize);
                }

                return true;
            }
            case R.id.action_moon: {

                if(menu.getItem(2).getTitle().toString().equals("Mode Nuit")){
                    menu.getItem(2).setTitle(R.string.action_moon_empty);

                    menu.getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_action_moon_empty));
                    questionView.setTextColor(Color.WHITE);
                    info.setTextColor(Color.WHITE);
                    timerText.setTextColor(Color.WHITE);
                    /*
                    rad1.setTextColor(Color.WHITE);
                    rad2.setTextColor(Color.WHITE);
                    rad3.setTextColor(Color.WHITE);
                    rad4.setTextColor(Color.WHITE);
                    */
                    relativeLayout.setBackgroundColor(Color.BLACK);
                    info.setBackground(getResources().getDrawable(R.drawable.info_frame_night));

                } else {
                    menu.getItem(2).setTitle(R.string.action_moon_full);
                    menu.getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_action_moon_full));
                    questionView.setTextColor(Color.BLACK);
                    info.setBackgroundColor(Color.BLACK);
                    info.setTextColor(Color.BLACK);
                    timerText.setTextColor(Color.BLACK);
                    /*
                    rad1.setTextColor(Color.BLACK);
                    rad2.setTextColor(Color.BLACK);
                    rad3.setTextColor(Color.BLACK);
                    rad4.setTextColor(Color.BLACK);
                    */
                    relativeLayout.setBackgroundColor(Color.WHITE);
                    info.setBackground(getResources().getDrawable(R.drawable.info_frame));

                }

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * print the question q and her answer possibilities
     */
	/*public void display(Question q,List <Question.Reponse> rep){*/
    public void display(Question q){
        no_question.setText("N° "+num_question+"/"+NB_QUESTION);
        questionView.setText(q.getQuestionTitle());
        tts.speak(q.getQuestionTitle(), TextToSpeech.QUEUE_ADD, map);
        continuer.setVisibility(View.GONE);
        RadioButton radioButton= new RadioButton(this);

        //get the number of radioButton -- 4 in our application
        int nb=radioGroup.getChildCount();
        for (int i=0;i<nb;i++){
            radioButton=(RadioButton) radioGroup.getChildAt(i);
            radioButton.setOnClickListener(this);

            if(q.getResponseList().size() > i){
                if(q.getResponseList().get(i).isRight_answer()){
                    radioButton.setChecked(true);
                    button_id=radioButton.getId();
                    displayAnswerInfo(q, radioButton);
                }

                radioButton.setText(q.getResponseList().get(i).getReponseTitle());
            }
            else{
                Log.d("display","list size of answer < radioButton numbers ");
            }
            radioButton.setBackgroundResource(R.drawable.button_radio);
        }

        timer.start();
    }

    public void displayAnswerInfo(Question rep, RadioButton radio){
        info = (TextView) findViewById(R.id.info);
        info.setVisibility(View.GONE);
        info.setText(rep.getHint());
        //info.setText("Hint");

        //with that, you can click on the url to go to the website
        Linkify.addLinks(info, Linkify.WEB_URLS);
        info.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public class JSONTask extends AsyncTask<String,String,List<Question> > {
        @Override
        protected List<Question> doInBackground(String... params) {
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

                try {
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        Question question = new Question();

                        question.setQuestionTitle(finalObject.getString("title"));
                        if(finalObject.has("hint")){
                            question.setHint(finalObject.getString("hint"));
                        }
                        else{
                            question.setHint("No Hint for this question");
                        }
                        question.setQuestionId(finalObject.getInt("id"));

                        // Ajout des reponses
                        List<Question.Reponse> responseList = new ArrayList<Question.Reponse>();
                        JSONArray finalReponse = finalObject.getJSONArray("answers");

                        try {
                            for (int j = 0; j < finalReponse.length(); j++) {
                                Question.Reponse response = new Question.Reponse();
                                JSONObject reponseObject = finalReponse.getJSONObject(j);
                                response.setReponseTitle(reponseObject.getString("title"));
                                response.setReponseId(reponseObject.getInt("id"));
                                response.setRight_answer(reponseObject.getBoolean("is_true"));
                                responseList.add(response);
                            }
                            question.setResponseList(responseList);
                        } catch (Exception e) {
                            Log.d("CREATION", e.getMessage());
                        }

                        //Ajout des tags
                        JSONArray finalTags = finalObject.getJSONArray("tags");
                        List<String> tags = new ArrayList<String>();
                        try {
                            for (int j = 0; j < finalTags.length(); j++) {
                                String currentTag;
                                JSONObject tagsObject = finalTags.getJSONObject(j);
                                currentTag = tagsObject.getString("name");
                                tags.add(currentTag);
                            }
                        } catch (Exception e) {
                            Log.d("CREATION", e.getMessage());
                        }
                        // adding the final object in the list
                        if(!tagMode.equals("Examen"))
                        {
                            for (int j = 0; j < tags.size(); j++) {
                                if (tagMode.equals(tags.get(j))) {
                                    Log.d("CREATION",tags.get(j));
                                    questionsList.add(question);
                                }
                            }
                        }
                        else {
                            questionsList.add(question);
                        }
                        Collections.shuffle(questionsList);
                    }
                } catch (Exception e) {
                    Log.d("CREATION", e.getMessage() + " - " + e.getStackTrace()[0].getClassName());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
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

        protected void onPostExecute(List<Question> result){
            super.onPostExecute(result);
            runningActivity();
        }
    }


   // the aim off this class  is to write the score after a training
    public void WriteScore(String data,int nb){
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        try{
            fOut = openFileOutput(nb+".dat", Context.MODE_APPEND);
            osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            Log.d("Score","Score write well");
        }
        catch (Exception e) {
            Log.d("Score","Score doesn't write");
        }
        finally {
            try {
                osw.close();
                fOut.close();
            } catch (IOException e) {
                Log.d("Score","Score doesn't write");
            }
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

    public void speakRadioButton(RadioGroup radioGroup){
        int nb=radioGroup.getChildCount();
        RadioButton radioButton= new RadioButton(this);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i=0;i<nb;i++) {
            radioButton = (RadioButton) radioGroup.getChildAt(i);
            String text = radioButton.getText().toString();
            tts.speak(text, TextToSpeech.QUEUE_ADD, map);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
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
            Log.v("TAG", "result : " + textResult);
            RadioButton radioButton0 = (RadioButton) radioGroup.getChildAt(0);
            RadioButton radioButton1 = (RadioButton) radioGroup.getChildAt(1);
            RadioButton radioButton2 = (RadioButton) radioGroup.getChildAt(2);
            RadioButton radioButton3 = (RadioButton) radioGroup.getChildAt(3);
            if(textResult.equalsIgnoreCase(radioButton0.getText().toString()))
                radioButton0.performClick();
            else if (textResult.equalsIgnoreCase(radioButton1.getText().toString()))
                radioButton1.performClick();
            else if (textResult.equalsIgnoreCase(radioButton2.getText().toString()))
                radioButton2.performClick();
            else if (textResult.equalsIgnoreCase(radioButton3.getText().toString()))
                radioButton3.performClick();
            else
                tts.speak("Veillez réessayer sil vous plait",TextToSpeech.QUEUE_FLUSH, map);
        }

        public void onPartialResults(Bundle partialResults){}
        public void onEvent(int eventType, Bundle params){}
    }
}
