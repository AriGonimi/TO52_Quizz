package fr.utbm.tx.quizz.javabean;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ASUS on 01/12/2016.
 */

    public class Score {
        private int val;
        private String date;

        @SuppressLint("SimpleDateFormat")
        public Score(float val) {
            this.val = (int)val;

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            this.date = df.format(c.getTime());
        }
        public Score(float val,String date) {
            this.val = (int)val;
            this.date = date;
        }

        @Override
        public String toString() {
            return val + ";" + date + "\n";
        }

        public int getVal() {
            return val;
        }
        public void setVal(Integer val) {
            this.val = val;
        }
        public String getDate() {
            return date;
        }
        public void setDate(String date) {
            this.date = date;
        }

    }

