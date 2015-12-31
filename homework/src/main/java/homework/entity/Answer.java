package homework.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;


/**
 * The persistent class for the answer database table.
 * 
 */
@Entity
@NamedQuery(name="Answer.findAll", query="SELECT a FROM Answer a")
public class Answer extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ANSWER_ID")
	private int answerId;

	private String answer;

	@Column(name="CORRECT_WRONG")
	private String correctWrong;

	//uni-directional many-to-one association to History
	@ManyToOne
	@JoinColumn(name="HISTORY_ID")
	private History history;

	//uni-directional many-to-one association to Question
	@ManyToOne
	@JoinColumn(name="QUESTION_ID")
	private Question question;

	public Answer() {
	}

	public int getAnswerId() {
		return this.answerId;
	}

	public void setAnswerId(int answerId) {
		this.answerId = answerId;
	}

	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getCorrectWrong() {
		return this.correctWrong;
	}

	public void setCorrectWrong(String correctWrong) {
		this.correctWrong = correctWrong;
	}

	public History getHistory() {
		return this.history;
	}

	public void setHistory(History history) {
		this.history = history;
	}

	public Question getQuestion() {
		return this.question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

}