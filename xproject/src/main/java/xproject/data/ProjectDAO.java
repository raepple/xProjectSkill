package xproject.data;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectDAO extends AbstractDAO {
	private static Logger log = LoggerFactory.getLogger(ProjectDAO.class);

	public List<Project> getAllProjects() {
		EntityManager em = ProjectDAO.getEntityManager();
		try {
			@SuppressWarnings("unchecked")
			List<Project> projects = em.createNamedQuery("AllProjects").getResultList();
			log.debug("All projects total number: {}", projects.size());
			return projects;
		} finally {
			em.close();
		}
	}

	public List<Project> getProjectsForUser(String userId) {
		EntityManager em = ProjectDAO.getEntityManager();
		try {
			@SuppressWarnings("unchecked")
			List<Project> projectsForUser = em.createNamedQuery("ProjectsForUser").setParameter("userid", userId)
					.getResultList();
			log.debug("Projects for user {} : {}", userId, projectsForUser.size());
			return projectsForUser;
		} finally {
			em.close();
		}
	}

	public long addProject(Project project) {
		EntityManager em = ProjectDAO.getEntityManager();
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		try {
			em.persist(project);
			transaction.commit();
			log.debug("Project added with id {}", project.getId());
			return project.getId();
		} finally {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			em.close();
		}
	}
}