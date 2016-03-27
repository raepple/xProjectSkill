package xproject.data;

import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

public abstract class AbstractDAO {
	private static EntityManagerFactory emf = null;
	private static final String JNDI_KEY_DATA_SOURCE = "java:comp/env/jdbc/xProjectDB";

	public static synchronized EntityManager getEntityManager() {
		if (emf == null) {
			emf = AbstractDAO.initDB();
		}
		return emf.createEntityManager();
	}

	private static EntityManagerFactory initDB() {
		DataSource dataSource = AbstractDAO.getDataSource();
		HashMap<String, DataSource> properties = new HashMap<String, DataSource>();
		properties.put("javax.persistence.nonJtaDataSource", dataSource);
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("xproject", properties);
		return emf;
	}

	private static DataSource getDataSource() {
		Object dataSource;
		try {
			InitialContext ctx = new InitialContext();
			dataSource = ctx.lookup(JNDI_KEY_DATA_SOURCE);
		} catch (NamingException e) {
			throw new IllegalStateException("JNDI lookup failure", e);
		}
		if (dataSource instanceof DataSource) {
			return (DataSource) dataSource;
		}
		throw new IllegalStateException("No data source available in JNDI context");
	}
}