package fr.utbm.tx.quizz.javabean;


import java.util.List;

public class Question {

	private String title;
	private int id;
	private String hint;
	private List<Reponse> responseList;
	private int cat;
public Question(String theme){
	this.title=theme;
}
	public Question(){

	}
	public String getQuestionTitle() {
		return title;
	}

	public void setQuestionTitle(String title) {
		this.title = title;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public int getQuestionId() {
		return id;
	}

	public int getQuestionCat() {return cat;}

	public void setQuestionCategorie(int cat) {this.cat = cat; }

	public void setQuestionId(int id) {

		this.id = id;
	}

	public List<Reponse> getResponseList()
	{
		return responseList;
	}

	public void setResponseList(List<Reponse> responseList) {
		this.responseList = responseList;
	}



	public static class Reponse {
		private String title;
		private int id;
		private boolean right_answer;

		public boolean isRight_answer() {
			return right_answer;
		}

		public void setRight_answer(boolean right_answer) {
			this.right_answer = right_answer;
		}


		public String getReponseTitle() {
			return title;
		}

		public int getReponseId() {
			return id;
		}

		public void setReponseTitle(String title) {

			this.title = title;
		}
		public void setReponseId(int id) {

			this.id = id;
		}
	}
}
