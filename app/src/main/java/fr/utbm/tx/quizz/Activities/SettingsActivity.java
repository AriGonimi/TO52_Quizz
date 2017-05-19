package fr.utbm.tx.quizz.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import fr.utbm.tx.quizz.R;


public class SettingsActivity extends Activity {
	
	private Button save = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        save = (Button)findViewById(R.id.btn_save);
        save.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		// Go to home menu
        		finish();
 			     // TODO save settings
        	}
        	
        });
    }
}