package fr.utbm.tx.quizz.Dao;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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
import java.util.Iterator;
import java.util.List;

import fr.utbm.tx.quizz.javabean.CategoryAdapter;
import fr.utbm.tx.quizz.javabean.Question;
import fr.utbm.tx.quizz.javabean.Theme;
import fr.utbm.tx.quizz.javabean.TrainingCategory;

/**
 * Created by ASUS on 06/12/2016.
 */
// the aim of this class is to get all theme from json file



public class Theme_Dao {
    private ArrayList<TrainingCategory> categoryList = new ArrayList<TrainingCategory>();
    List<Theme> themelist = new ArrayList<Theme>();


    //constructor
    public Theme_Dao(){

}
    public List<TrainingCategory> GetThemes(){


        new JSONTAG().execute("http://to52.julienpetit.fr/api/v1/quiz/tags");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       // for (Iterator i = suits.iterator(); i.hasNext(); ) {
        return categoryList;
    }

public int GetItembyTitle(String title){
  int pos =0;
    new JSONTAG().execute("http://to52.julienpetit.fr/api/v1/quiz/tags");
   //categoryList = GetThemes();
    try {
        Thread.sleep(2000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    for(Iterator<TrainingCategory> i = categoryList.iterator(); i.hasNext(); ) {
        TrainingCategory item = i.next();
       // int Qid=item.getQuestionId();
//String titre=item.getName().toString();
        if (title.equals(new String(item.getName())))
        {
            pos=item.getId();
            break;
        }



    }
    return pos;
}
    public class JSONTAG extends AsyncTask<String,String,List<String> > {
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

                try {
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        TrainingCategory cat = new TrainingCategory(finalObject.getString("name"),finalObject.getInt("id"));
                        // adding the final object in the list
                        categoryList.add(cat);
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

        protected void onPostExecute(List<String> result){
            super.onPostExecute(result);
            //setCategoryTags();
        }
    }

}
