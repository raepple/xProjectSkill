package xproject.data;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1;
	private static final Logger log = LoggerFactory.getLogger(SampleDataServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ProjectDAO projectDAO = new ProjectDAO();
		try {
			Project project = new Project();
			project.setProjectLead("jdoe");
			project.setTitle("Security Audit");
			project.setStatus("critical");
			projectDAO.addProject(project);
			List<Project> projects = projectDAO.getAllProjects();
			response.getWriter().println("Number of projects: " + projects.size());
		} catch (Exception e) {
			response.getWriter().println("Operation failed with reason: " + e.getMessage());
			log.error("Operation failed", (Throwable) e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}