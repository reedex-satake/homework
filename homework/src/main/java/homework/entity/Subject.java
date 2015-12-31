package homework.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;


/**
 * The persistent class for the subject database table.
 * 
 */
@Entity
@NamedQuery(name="Subject.findAll", query="SELECT s FROM Subject s")
public class Subject extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SUBJECT_ID")
	private int subjectId;

	private String description;

	@Column(name="SUBJECT_TYPE")
	private short subjectType;

	@Column(name="TARGET_AGE")
	private short targetAge;

	public Subject() {
	}

	public int getSubjectId() {
		return this.subjectId;
	}

	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public short getSubjectType() {
		return this.subjectType;
	}

	public void setSubjectType(short subjectType) {
		this.subjectType = subjectType;
	}

	public short getTargetAge() {
		return this.targetAge;
	}

	public void setTargetAge(short targetAge) {
		this.targetAge = targetAge;
	}

}