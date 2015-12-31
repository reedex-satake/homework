package homework.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the history database table.
 *
 */
@Entity
@NamedQuery(name="History.findAll", query="SELECT h FROM History h")
public class History extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="HISTORY_ID")
	private int historyId;

	@Column(name="CORRECT_PERCENT")
	private BigDecimal correctPercent;

	private int elapsed;

	@Temporal(TemporalType.DATE)
	@Column(name="REGIST_DATE")
	private Date registDate;

	//uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="USER_ID")
	private User user;

	public History() {
	}

	public int getHistoryId() {
		return this.historyId;
	}

	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}

	public BigDecimal getCorrectPercent() {
		return this.correctPercent;
	}

	public void setCorrectPercent(BigDecimal correctPercent) {
		this.correctPercent = correctPercent;
	}

	public int getElapsed() {
		return this.elapsed;
	}

	public void setElapsed(int elapsed) {
		this.elapsed = elapsed;
	}

	public Date getRegistDate() {
		return this.registDate;
	}

	public void setRegistDate(Date registDate) {
		this.registDate = registDate;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}