package fr.utbm.tx.quizz.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import fr.utbm.tx.quizz.Dao.Question_Dao;
import fr.utbm.tx.quizz.R;

/**
 * Created by ASUS on 23/11/2016.
 */
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.utbm.tx.quizz.R;
import fr.utbm.tx.quizz.javabean.Question;

public class ChoiceScoreActivity extends Activity{
    private Button tousBtn = null;
    private Button ExamenBtn = null;
    private Button themeBtn = null;
    private Button retourBtn = null;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_score);
        tousBtn =(Button)findViewById(R.id.btn_Tous);
       ExamenBtn =(Button)findViewById(R.id.Btn_examen);
        themeBtn =(Button)findViewById(R.id.btn_partheme);
        retourBtn =(Button)findViewById(R.id.btn_retour);



    // go to all score
    tousBtn.setOnClickListener(new OnClickListener(){
        public void onClick(View v){
            //Initialisation de la prochaine activité  //ExempleChartActivity
            Intent myIntent = new Intent(getBaseContext(), AllEvolutionActivity.class);
            startActivity(myIntent);
        }
    });

    // go to classique score
   ExamenBtn.setOnClickListener(new OnClickListener(){
        public void onClick(View v){
            //Initialisation de la prochaine activité
            Intent myIntent = new Intent(getBaseContext(), ExamenEvolutionActivity.class);
            startActivity(myIntent);
            //finish();
        }
    });

    // go to score by theme
    themeBtn.setOnClickListener(new OnClickListener(){
        public void onClick(View v){
            //Initialisation de la prochaine activité
            Intent myIntent = new Intent(getBaseContext(), ThemeEvolutionActivity.class);
            Question_Dao qdao = new Question_Dao();
            List<Question> questions= new ArrayList<Question>();

            // Select a random theme for the activity
            Random rand = new Random();
            int nbTheme = 5;
            int nb = rand.nextInt(nbTheme) + 1;

            int theme_id = nb; // Play theme number nb
           // questions=qdao.chooseQuestionFromTheme(theme_id);
         //int   nbQuestion = questions.size();   // size new  of questions
            myIntent.putExtra("play_theme", theme_id);

            // Start activity
            startActivity(myIntent);
        }
    });

    // Back to menu
    retourBtn.setOnClickListener(new OnClickListener(){
        public void onClick(View v){
            //Initialisation de la prochaine activité
            Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(myIntent);
            finish();
        }
    });

}


}
