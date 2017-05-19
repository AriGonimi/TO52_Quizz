package fr.utbm.tx.quizz.Dao;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fr.utbm.tx.quizz.Activities.JouerActivity;
import fr.utbm.tx.quizz.javabean.Question;
import fr.utbm.tx.quizz.javabean.Theme;

/**
 * Created by ASUS on 07/12/2016.
 */

public class Question_Dao {

    private List <Question> questionsList = new ArrayList<Question>();
    private Bundle extras= new Bundle();
    private String tagMode = null;
private int NB_QUESTION=15;
public Question_Dao(){
    //new JSONTask().execute("http://to52.julienpetit.fr/api/v1/quiz/questions?limit="+NB_QUESTION);
}

    public List<Question> chooseQuestionFromTheme(int theme_id) {
        List<Question> questions = new ArrayList<Question>();
        new JSONTask().execute("http://to52.julienpetit.fr/api/v1/quiz/questions?limit="+NB_QUESTION);


        for(Iterator<Question> i = questionsList.iterator(); i.hasNext(); ) {
          Question item = i.next();
            int Qid=item.getQuestionId();

            if (Qid==theme_id)

            questions.add(item);


        }

        //Theme t =new Theme("Developpement durable");
      //  Question q =new Question("Développement durable");
        ///questions.add(q);
        Log.i("DAO", "chooseQuestionsByTheme done :" + questions);

        return questions;

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
            //runningActivity();
        }
    }
}
