package fr.utbm.tx.quizz.Activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

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

public class
LectureActivity extends Activity
{
    private Button btn_select = null;
    private String idLecture = null;
    private Spinner spinTitle = null;
    private TextView text = null;
    private int fontSize = 18;
    private Menu menu= null;
    private LinearLayout lLayout = null;
    private TextToSpeech tts;
    final HashMap<String, String> map = new HashMap<String, String>();



    private List<String> listTitle = new ArrayList<String>();
    private List<String> listContent = new ArrayList<String>();

    @Override
    protected  void onPause(){
        if (tts != null) {
            tts.stop();
        }
        super.onPause();
    }

    protected  void onStop(){
        if (tts != null) {
            tts.stop();
        }
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lecture_activity);
        btn_select = (Button) findViewById(R.id.cours1);
        spinTitle = (Spinner) findViewById(R.id.courseSpinner);
        text = (TextView) findViewById(R.id.information);
        lLayout = (LinearLayout) findViewById(R.id.act_lect);

        Bundle extras = getIntent().getExtras();
        idLecture = extras.getString("Lecture");
        String http = "http://to52.julienpetit.fr/api/v1/learning/cards/category/" + idLecture;
        new JSONLecture().execute(http);
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

                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                            "UniqueID");

                    tts.setLanguage(Locale.FRANCE);

                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        /*
                         * When speak is done wait for 1sec and start
                         *
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
        getMenuInflater().inflate(R.menu.actionbar_lecture, menu);
        this.menu = menu;
        return true;
    }

    public class JSONLecture extends AsyncTask<String,String,List<String> > {
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
                String line = "";
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
                        String title = finalObject.getString("title");
                        String content = finalObject.getString("content");
                        // adding the final object in the list
                        listTitle.add(title);
                        listContent.add(content);
                    }
                } catch (Exception e) {
                    Log.d("CREATION", e.getMessage() + " - " + e.getStackTrace()[0].getClassName());
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

        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(LectureActivity.this, android.R.layout.simple_spinner_item, listTitle);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinTitle.setAdapter(adapter1);
            btn_select = (Button) findViewById(R.id.activation);
            spinTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected (AdapterView < ? > parent, View v, int pos, long id){
                    tts.stop();
                    text.setText(listContent.get(spinTitle.getSelectedItemPosition()));
                    tts.speak(text.getText().toString(),TextToSpeech.QUEUE_FLUSH, map);
                }

                @Override
                public void onNothingSelected (AdapterView < ? > arg0){
                }
            });
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_textsize_plus: {
                if (fontSize < 25) {
                    fontSize++;
                    text.setTextSize(fontSize);
                }

                return true;
            }
            case R.id.action_textsize_minus: {
                if (fontSize > 7) {
                    fontSize--;
                    text.setTextSize(fontSize);
                }

                return true;
            }
            case R.id.action_moon: {

                if(menu.getItem(2).getTitle().toString().equals("Mode Nuit")){
                    menu.getItem(2).setTitle(R.string.action_moon_empty);

                    menu.getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_action_moon_empty));
                    text.setTextColor(Color.WHITE);
                    lLayout.setBackgroundColor(Color.BLACK);
                    spinTitle.setBackgroundColor(Color.WHITE);
                } else {
                    menu.getItem(2).setTitle(R.string.action_moon_full);
                    menu.getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_action_moon_full));
                    text.setTextColor(Color.BLACK);
                    lLayout.setBackgroundColor(0);
                    spinTitle.setBackgroundColor(0);
                }

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
