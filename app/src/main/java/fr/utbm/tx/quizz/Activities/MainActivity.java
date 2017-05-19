package fr.utbm.tx.quizz.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import fr.utbm.tx.quizz.Dao.Theme_Dao;
import fr.utbm.tx.quizz.R;


public class MainActivity extends Activity {
	private Button jouer=null;
	//private Button settings=null;
	private Button quitter=null;
	private boolean isConnected = false;
	private Button score = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getActionBar().show();

        setContentView(R.layout.activity_main);
        jouer=(Button)findViewById(R.id.btn_play);
        //settings=(Button)findViewById(R.id.btn_settings);
        quitter=(Button)findViewById(R.id.btn_quit);
		score = (Button) findViewById(R.id.btn_Evolution);
			jouer.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//Initialisation de la prochaine activité
					isConnected = isNetworkAvailable();
					if (isConnected) {
						try {

							Intent myIntent = new Intent(getBaseContext(), ModeActivity.class);
							startActivity(myIntent);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(MainActivity.this, "Veuillez vérifier que la connection internet est active.",
								Toast.LENGTH_LONG).show();
						finish();
					}
				}
			});

		score.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				Intent myIntent = new Intent(getBaseContext(), ChoiceScoreActivity.class);
				startActivity(myIntent);
			}
		});
        
        /*settings.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		 //Initialisation de la prochaine activité
        		 Intent myIntent = new Intent(getBaseContext(), SettingsActivity.class);    
 			     startActivity(myIntent);
        	}
        });*/

			quitter.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(MainActivity.this, "Au revoir.", Toast.LENGTH_SHORT).show();
					// Fermeture de l'activité courante
					finish();
				}
			});
    }

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager	= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}