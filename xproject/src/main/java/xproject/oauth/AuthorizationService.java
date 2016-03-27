package xproject.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.account.TenantContext;
import com.sap.core.connectivity.api.DestinationException;
import com.sap.core.connectivity.api.DestinationFactory;
import com.sap.core.connectivity.api.DestinationNotFoundException;
import com.sap.core.connectivity.api.http.HttpDestination;

public class AuthorizationService {
	private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);
	private static final String DESTINATION_OAUTHAS = "oauth";
	private static final String ATTRIBUTE_MAIL = "mail";
	private static final String ATTRIBUTE_DISPLAYNAME = "display_name";
	private static final String ATTRIBUTE_USERID = "userid";
	private static final String ATTRIBUTE_FIRSTNAME = "first_name";
	private static final String ATTRIBUTE_LASTNAME = "last_name";

	public User getUserFromAccessToken(String accessToken) {
		HttpClient client = this.createClient();
		try {
			String url = this.buildAccessTokenInfoURL();
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("access_token", accessToken);
			log.debug("Request to check token [{}", accessToken);
			HttpResponse resp = client.execute((HttpUriRequest) httpGet);
			int statusCode = resp.getStatusLine().getStatusCode();
			log.debug("Status code [{}] for request to check token [{}]", statusCode, accessToken);
			String respStr = null;
			if (statusCode == 200) {
				HttpEntity entity = resp.getEntity();
				respStr = EntityUtils.toString((HttpEntity) entity);
				log.debug("Response from OAuth AS: {}", respStr);
				ObjectMapper mapper = new ObjectMapper();
				JsonNode accessTokenInfo = mapper.readTree(respStr);
				User hcpUser = new User();
				hcpUser.setUserid(accessTokenInfo.findValue(ATTRIBUTE_USERID).textValue());

				if (accessTokenInfo.findValue(ATTRIBUTE_DISPLAYNAME) != null) {
					hcpUser.setDisplayName(accessTokenInfo.findValue(ATTRIBUTE_DISPLAYNAME).get(0).textValue());
				}
				if (accessTokenInfo.findValue(ATTRIBUTE_FIRSTNAME) != null) {
					hcpUser.setFirstName(accessTokenInfo.findValue(ATTRIBUTE_FIRSTNAME).get(0).textValue());
				}
				if (accessTokenInfo.findValue(ATTRIBUTE_LASTNAME) != null) {
					hcpUser.setLastName(accessTokenInfo.findValue(ATTRIBUTE_LASTNAME).get(0).textValue());
				}
				if (accessTokenInfo.findValue(ATTRIBUTE_MAIL) != null) {
					hcpUser.setMail(accessTokenInfo.findValue(ATTRIBUTE_MAIL).get(0).textValue());
				}
				log.debug("HCP User set to {}, {}, {}, {}, {}", new Object[] { hcpUser.getUserid(),
						hcpUser.getDisplayName(), hcpUser.getFirstName(), hcpUser.getLastName(), hcpUser.getMail() });
				User user = hcpUser;
				return user;
			}
			log.error("Response from OAUTH AS not OK. Response Code {}", statusCode);
			return null;
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		} finally {
			ClientConnectionManager connectionManager = client.getConnectionManager();
			if (connectionManager != null) {
				connectionManager.shutdown();
			}
		}
	}

	private String buildAccessTokenInfoURL() throws UnsupportedEncodingException {
		try {
			InitialContext ctx = new InitialContext();
			TenantContext tenantctx = (TenantContext) ctx.lookup("java:comp/env/TenantContext");
			String tenantId = tenantctx.getTenant().getId();
			tenantId = URLEncoder.encode(tenantId, StandardCharsets.UTF_8.name());
			String result = "https://oauthasservices.hanatrial.ondemand.com:8443/oauth2/api/v1/token/info?tenant_id="
					+ tenantId;
			log.debug("Access Token Info URL is {}", result);
			return result;
		} catch (NamingException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	private HttpClient createClient() {
		HttpDestination httpDestination = null;
		httpDestination = this.lookupDestination();
		try {
			HttpClient client = httpDestination.createHttpClient();
			return client;
		} catch (DestinationException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	private HttpDestination lookupDestination() {
		DestinationFactory destinationFactory = this.getDestinationFactory();
		if (destinationFactory != null) {
			try {
				HttpDestination destination = (HttpDestination) destinationFactory.getDestination(DESTINATION_OAUTHAS);
				return destination;
			} catch (DestinationNotFoundException e) {
				log.error(e.getMessage());
				return null;
			}
		}
		return null;
	}

	private DestinationFactory getDestinationFactory() {
		try {
			InitialContext ctx = new InitialContext();
			DestinationFactory destinationFactory = (DestinationFactory) ctx
					.lookup(DestinationFactory.JNDI_NAME);
			return destinationFactory;
		} catch (NamingException e) {
			log.error(e.getMessage());
			return null;
		}
	}
}