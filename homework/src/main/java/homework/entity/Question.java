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
 * The persistent class for the question database table.
 * 
 */
@Entity
@NamedQuery(name="Question.findAll", query="SELECT q FROM Question q")
public class Question extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="QUESTION_ID")
	private int questionId;

	private String correct;

	private String question;

	private String remark;

	@Column(name="SEP_CHAR")
	private String sepChar;

	//uni-directional many-to-one association to Subject
	@ManyToOne
	@JoinColumn(name="SUBJECT_ID")
	private Subject subject;

	public Question() {
	}

	public int getQuestionId() {
		return this.questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public String getCorrect() {
		return this.correct;
	}

	public void setCorrect(String correct) {
		this.correct = correct;
	}

	public String getQuestion() {
		return this.question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSepChar() {
		return this.sepChar;
	}

	public void setSepChar(String sepChar) {
		this.sepChar = sepChar;
	}

	public Subject getSubject() {
		return this.subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

}