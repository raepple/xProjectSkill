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
		response.getWriter().println("<html>"
				+ "<head><style>table, th, td {border: 1px solid black;}"
				+ "</style></head>"
				+ "<h3>Project DB</h3>");
		try {
			ProjectDAO projectDAO = new ProjectDAO();
		
			String action = request.getParameter("action");
			if (action != null) {
				String id = request.getParameter("id");
				if (id != null) {
					Long projectId = Long.parseLong(id);
					if (projectId != null) {
						
						if (action.equals("delete")) {
							projectDAO.deleteProject(projectId);
						} else if (action.equals("status")) {
							projectDAO.changeState(projectId);
						} 
					}
				}
			} 
			List<Project> projects = projectDAO.getAllProjects();
			response.getWriter().println("<p>Number of projects: " + projects.size() + "</p>");
			response.getWriter().println("<table><tr><th>Title</th><th>Lead</th><th>Status</th><th>Actions</th></tr>");
			for( Project p: projects ) {
				response.getWriter().println("<tr><td>" + p.getTitle() + "</td>"
						+ "<td>" + p.getProjectLead() + "</td>"
						+ "<td>" + p.getStatus() + "</td>"
						+ "<td><a href=\"SampleData?action=delete&id=" + p.getId() + "\">Delete</a> <a href=\"SampleData?action=status&id=" + p.getId() + "\">Change State</a></td></tr>");			
			}	
			response.getWriter().println("</table>");
			response.getWriter().println("<h3>New Project</h3>");
			response.getWriter().println("<form action=\"SampleData?action=add\" method=\"post\">"
					+ "Project title: <input type=\"text\" name=\"title\"><br>"
					+ "Project lead:  <input type=\"text\" name=\"lead\"><br>"
					+ "Project status: <input type=\"text\" name=\"status\"><br>"
					+ "<input type=\"submit\" value=\"Submit\"></form></html>");
		} catch (Exception e) {
			response.getWriter().println("Operation failed with reason: " + e.getMessage());
			log.error("Operation failed", (Throwable) e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			ProjectDAO projectDAO = new ProjectDAO();
			String action = request.getParameter("action");
			if (action != null) {
				if (action.equals("add")) {
					Project project = new Project();
					project.setProjectLead(request.getParameter("lead"));
					project.setTitle(request.getParameter("title"));
					project.setStatus(request.getParameter("status"));
					projectDAO.addProject(project);
				}
			}
		} catch (Exception e) {
			response.getWriter().println("Operation failed with reason: " + e.getMessage());
			log.error("Operation failed", (Throwable) e);
		}
		doGet(request, response);
	}
}