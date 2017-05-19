package fr.utbm.tx.quizz.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;

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
import java.util.List;
import android.view.View.OnClickListener;
import android.widget.Toast;

import fr.utbm.tx.quizz.Dao.Theme_Dao;
import fr.utbm.tx.quizz.R;
import fr.utbm.tx.quizz.javabean.CategoryAdapter;
import fr.utbm.tx.quizz.javabean.TrainingCategory;


public class TrainingActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
	private Button startTest=null;
private int posId ;
	private ListView listCategory;
	private String tagSend = null;

	private ArrayList<TrainingCategory> categoryList = new ArrayList<TrainingCategory>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_training);
		listCategory = (ListView)findViewById(R.id.listview);
		startTest=(Button)findViewById(R.id.btn_starttest);
		startTest.setVisibility(View.INVISIBLE);
		new JSONTAG().execute("http://to52.julienpetit.fr/api/v1/quiz/tags");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		startTest.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Initialisation de la prochaine activité
				if(tagSend!= null) {
					try {
						Intent myIntent = new Intent(getBaseContext(), JouerActivity.class);
						myIntent.putExtra("TAG", tagSend);
						startActivity(myIntent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else{
					Toast.makeText(TrainingActivity.this, "Veuillez sélectionner une catégorie d'entrainement.",
							Toast.LENGTH_LONG).show();
				}
        	}
        	
        });
    }

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		posId=0;
		int pos = listCategory.getPositionForView(buttonView);
		posId=pos;
		if (pos != ListView.INVALID_POSITION) {
			TrainingCategory cat = categoryList.get(pos);
			tagSend = cat.getName();
			cat.setSelected(isChecked);
		}
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
						TrainingCategory cat = new TrainingCategory(finalObject.getString("name"));
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
			setCategoryTags();
		}
	}


	public void setCategoryTags()
	{
		CategoryAdapter categoryAdapter = new CategoryAdapter(categoryList, this);
		listCategory.setAdapter(categoryAdapter);
		startTest.setVisibility(View.VISIBLE);
	}
}