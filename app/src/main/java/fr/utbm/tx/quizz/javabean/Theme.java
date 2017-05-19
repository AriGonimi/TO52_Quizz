package fr.utbm.tx.quizz.javabean;

/**
 * Created by ASUS on 01/12/2016.
 */

public class Theme {

    // Attributes
    private int theme_id;
    private String theme_lib;

    // Constructor
    public Theme() {

    }
    public Theme( String lib) {

        this.theme_lib = lib;
    }
    public Theme(int id, String lib) {
        this.theme_id = id;
        this.theme_lib = lib;
    }

    // Getter & Setter
    public int getTheme_id() {
        return theme_id;
    }

    public void setTheme_id(int theme_id) {
        this.theme_id = theme_id;
    }

    public String getTheme_lib() {
        return theme_lib;
    }

    public void setTheme_lib(String theme_lib) {
        this.theme_lib = theme_lib;
    }

    @Override
    public String toString() {
        return "Theme [theme_id=" + theme_id + ", theme_lib=" + theme_lib +"]";
    }
}
