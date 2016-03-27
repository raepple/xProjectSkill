package xproject.data;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "T_PROJECT")
@NamedQueries(value = { @NamedQuery(name = "AllProjects", query = "SELECT p FROM Project p"),
		@NamedQuery(name = "ProjectsForUser", query = "SELECT p FROM Project p WHERE p.projectLead = :userid") })
public class Project implements Serializable {
	private static final long serialVersionUID = 1;
	@Id
	@GeneratedValue
	private Long id;
	@Basic
	private String title;
	@Basic
	private String projectLead;
	@Basic
	private String status;

	public long getId() {
		return this.id;
	}

	public void setId(long newId) {
		this.id = newId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProjectLead() {
		return this.projectLead;
	}

	public void setProjectLead(String projectLead) {
		this.projectLead = projectLead;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}