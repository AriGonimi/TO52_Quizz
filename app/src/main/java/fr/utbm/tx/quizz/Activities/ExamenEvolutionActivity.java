package fr.utbm.tx.quizz.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.utbm.tx.quizz.Dao.Question_Dao;
import fr.utbm.tx.quizz.Dao.Theme_Dao;
import fr.utbm.tx.quizz.R;
import fr.utbm.tx.quizz.javabean.Question;
import fr.utbm.tx.quizz.javabean.Score;
import fr.utbm.tx.quizz.javabean.Theme;
import fr.utbm.tx.quizz.javabean.TrainingCategory;

/**
 * Created by ASUS on 01/12/2016.
 */

public class ExamenEvolutionActivity extends Activity {
    private Button retourBtn = null;
    private TextView textView = null;
    private TextView titleEvolution = null;
    private List<Theme> lstThemes = new ArrayList<Theme>();
    private String tabColor[] = {"#FB8C00", "#BA68C8", "#F44336", "#3F51B5", "#1B5E20", "#FFFF00"
            , "#607D8B"};
    List<TrainingCategory> catlist= new ArrayList<TrainingCategory>();
    private Theme_Dao tdao =new Theme_Dao();
    private Question_Dao qdao =new Question_Dao();
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_examen);
        //build chart
       // catlist=  tdao.GetThemes();
        // while (lstThemes.size()==0){

      ///  lstThemes=GetThemeList(catlist);
       // titre de l'évolution
        titleEvolution = (TextView) findViewById(R.id.titleEvolution);
       titleEvolution.setText(titleEvolution(0));

        //build chart
    openChart();
    }
    private XYSeries loadMySYSeries(XYSeries xy, List<Score> lst, int play_theme){
        xy.add(0, 0);
        if(lst != null){
            int j = 1;
            int nbQuestion;
            int val;
            String annotation = " ";
            List <Question> questions = new ArrayList <Question>();
            // qDao.open(); replace by getting questions from the api
            if(play_theme == 0){
                nbQuestion = 15;
            }
            else{

//source du probleme
                // questions=qdao.chooseQuestionFromTheme(play_theme);
                nbQuestion = 15;//questions.size();   // size of questions
            }
            for(Score s : lst){
                xy.add(j, s.getVal());  // add x & y on chart
                val = s.getVal()*100/15; // value in percent
                annotation = val + "%";  // text annotation
                xy.addAnnotation(annotation, j, s.getVal());
                j++;
            }
        }
        return xy;
    }
    private int maxValue(List<Integer> l){
        int maxVal = 0;
        maxVal = l.get(0);
        for(Integer i: l) {
            if(i > maxVal) maxVal = i;
        }
        return maxVal;
    }

    //read specific file score
    public List<Score> ReadScore(int nb){
        int val;
        String date = "";
        List<Score> lst = new ArrayList<Score>();
        try {
            InputStream inputStream = openFileInput(nb+".dat");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                while ( (line = bufferedReader.readLine()) != null ) {
                    String[] v = line.split(";");
                    val = Integer.parseInt(v[0]);
                    date = v[1];
                    Score score = new Score(val,date);
                    lst.add(score);
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.d("File", "File not found: " + e.toString());
            lst = null;
        } catch (IOException e) {
            Log.d("File","Can not read file: " + e.toString());
            lst = null;
        }
        return lst;
    }

    private void openChart(){
        // title theme
        /// tDAO.open(); replace by getting the themes by api



        /// replace by getting the theme by api
///TrainingActivity activity =new TrainingActivity();
        //activity.setCategoryTags();
        String theme0 = "Examen";

        // Creating an  XYSeries for score classic
        XYSeries classiqueSeries = new XYSeries(theme0);

        // init list of score

        List<Score> lst0 = ReadScore(0);
        //size of list score
        List<Integer> listTaille = new ArrayList<Integer>();
        int t0 = 0;
        int mSize = 0;
        if(lst0 != null){
            listTaille.add(lst0.size());
        }else{
            listTaille.add(0);
        }
        // set data from file
        classiqueSeries = loadMySYSeries(classiqueSeries,lst0,0);


        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        // Adding Series to the dataset (Classique firstly)
        dataset.addSeries(classiqueSeries);

        // Creating XYSeriesRenderer to customize Classique Series
        XYSeriesRenderer classiqueRenderer = new XYSeriesRenderer();
        classiqueRenderer.setColor(Color.BLUE);//parseColor(tabColor[2]));

        classiqueRenderer.setPointStyle(PointStyle.DIAMOND);
        classiqueRenderer.setFillPoints(true);
        classiqueRenderer.setLineWidth(5);
        classiqueRenderer.setDisplayChartValues(true);


        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle(""); // title chart
        multiRenderer.setXTitle("N° des parties");
        multiRenderer.setYTitle("Score");
        multiRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.BLACK);
        multiRenderer.setZoomButtonsVisible(true);
        multiRenderer.setYAxisMax(15);
        multiRenderer.setYAxisMin(0);
        multiRenderer.setLabelsTextSize(14); // size of x and y
        multiRenderer.setLegendTextSize(24);
        multiRenderer.setMargins(new int[] { 25, 25, 40, 25 });
        multiRenderer.setAxisTitleTextSize(20); // size title x and y

        // adding element to chart
        multiRenderer.addSeriesRenderer(classiqueRenderer);
/* ----------------------------------------------------
    	 *  DYNAMIQUE BUILDING CHART (theme 1 to theme N)
    	 * ----------------------------------------------------*/
        // indice
       /* int k = 1;
        for(Theme t : lstThemes){
            // new XYSeries theme
            XYSeries s = new XYSeries(t.getTheme_lib());
            // new score for theme
            List<Score> l = ReadScore(t.getTheme_id());
            // add size score element
            if(l != null){
                listTaille.add(l.size());
            }
            else{

                listTaille.add(0);
                break;
            }
            //set data from file
            s = loadMySYSeries(s,l,t.getTheme_id());

            // Adding Series to the dataset (thème1, ...., thème5)
            dataset.addSeries(s);


            // Creating XYSeriesRenderer to customize Classique Series
            XYSeriesRenderer themeRenderer = new XYSeriesRenderer();
            themeRenderer.setColor(Color.parseColor(tabColor[k]));
            themeRenderer.setPointStyle(PointStyle.POINT);
            themeRenderer.setFillPoints(true);
            themeRenderer.setLineWidth(5);
            themeRenderer.setDisplayChartValues(true);
            // adding element to chart
            multiRenderer.addSeriesRenderer(themeRenderer);
            k++;
            if(tabColor.length < k){
                k = 0;
            }
        }*/
        // graduation on x
        mSize = maxValue(listTaille);
        for(int i=0;i<mSize;i++){  //maxVal
            multiRenderer.addXTextLabel(i+1, (i+1)+"e");
        }
        // graduation on y
        for(int i=0;i<16;i++){
            multiRenderer.addYTextLabel(i+1, (i+1)+"");
        }

        // get layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.chartexamen);
        // init view
        View chartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiRenderer);
        // set view chart
        layout.addView(chartView);

    }
    public List<Theme> GetThemeList(List<TrainingCategory> catlist){
        List<Theme> themelist =new ArrayList<Theme>();
        int taille=catlist.size();
        String name="";
        for(Iterator<TrainingCategory> i = catlist.iterator(); i.hasNext(); ) {
            TrainingCategory item = i.next();
            //  for (int j = 0; j < taille; j++) {


            themelist.add(new Theme(item.getId(),item.getName()));



            //  themelist.add(new Theme(j,name));
        }
        return themelist;
    }
    public String titleEvolution(int id){
        String startEvolution = "00-00-00";
        String endEvolution = "00-00-00";
        List<Score> lst = ReadScore(id);
        if(lst != null){
            startEvolution = lst.get(0).getDate();
            endEvolution = lst.get(lst.size()-1).getDate();
        }
        return "Du "+startEvolution+" au "+endEvolution;
    }
}
