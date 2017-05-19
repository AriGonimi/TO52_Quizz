package fr.utbm.tx.quizz.javabean;

public class TrainingCategory {
    String name;
    boolean selected = false;
int id;
    public TrainingCategory(String name) {
        super();
        this.name = name;
    }
    public TrainingCategory(String name,int id) {
        super();
        this.name = name;
        this.id=id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


/*

 */
    public int getId() {
        return id;
    }
    /*

     */
    public int SetId(int id ){
        this.id=id;
        return id;

    }
}