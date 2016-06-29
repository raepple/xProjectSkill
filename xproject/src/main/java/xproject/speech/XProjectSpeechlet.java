package xproject.speech;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.Image;
import com.amazon.speech.ui.LinkAccountCard;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.StandardCard;

import xproject.data.Project;
import xproject.data.ProjectDAO;
import xproject.oauth.AuthorizationService;

public class XProjectSpeechlet implements Speechlet {
	private static final Logger log = LoggerFactory.getLogger(XProjectSpeechlet.class);

	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
		log.info("onSessionStarted requestId={}, sessionId={}",  request.getRequestId(),
				 session.getSessionId());
	}

	public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());
		return this.getWelcomeResponse();
	}

	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		Intent intent = request.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;
		if ("xProjectIntent".equals(intentName)) {
			if (session.getUser() != null) {
				String accessToken = session.getUser().getAccessToken();
				log.debug("Access token from Alexa Service: {}", accessToken);
				AuthorizationService as = new AuthorizationService();
				xproject.oauth.User hcpUser = as.getUserFromAccessToken(accessToken);
				return this.getProjectStatusForUser(hcpUser);
			}
			throw new SpeechletException("No user");
		}
		if ("AMAZON.HelpIntent".equals(intentName)) {
			return this.getHelpResponse();
		}
		throw new SpeechletException("Invalid Intent");
	}

	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
		log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());
	}

	private SpeechletResponse getWelcomeResponse() {
		String speechText = "Welcome to xProject. Please link your account.";
		LinkAccountCard card = new LinkAccountCard();
		card.setTitle("xProject");
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speech);
		return SpeechletResponse.newAskResponse(speech, reprompt, card);
	}

	private SpeechletResponse getProjectStatusForUser(xproject.oauth.User user) {
		ProjectDAO projectDAO = new ProjectDAO();
		List<Project> userProjects = projectDAO.getProjectsForUser(user.getUserid());
		log.debug("Checking projects for lead with user id " + user.getUserid());
		String speechText = "";
		String salutation = "";
		if (user.getFirstName() != null) {
			salutation = String.valueOf(user.getFirstName()) + ", ";
		}
		if (userProjects.size() > 0) {

			for (Iterator<Project> projects = userProjects.iterator(); projects.hasNext();) {
				Project project = projects.next();
				if (project.getStatus().equals("critical")) {
					if (!speechText.isEmpty()) {
						speechText = String.valueOf(speechText) + ", ";
					}
					speechText = String.valueOf(speechText) + project.getTitle();
				}
			}
			speechText = speechText.isEmpty()
					? String.valueOf(salutation) + "none of your projects are in a critical state."
					: String.valueOf(salutation) + "the following projects are in a critical state: " + speechText;
		} else {
			speechText = "Sorry " + salutation + "I couldn't find any projects.";
		}
		StandardCard card = this.getXProjectStandardCard(speechText);
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);
		return SpeechletResponse.newTellResponse((OutputSpeech) speech, (Card) card);
	}

	private SpeechletResponse getHelpResponse() {
		String speechText = "You can ask for your projects status!";
		StandardCard card = this.getXProjectStandardCard(speechText);
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech((OutputSpeech) speech);
		return SpeechletResponse.newAskResponse((OutputSpeech) speech, (Reprompt) reprompt, (Card) card);
	}

	private StandardCard getXProjectStandardCard(String speechText) {
		StandardCard card = new StandardCard();
		Image xProjectLogo = new Image();
		xProjectLogo.setSmallImageUrl("https://xprojectd044724trial.hanatrial.ondemand.com/xproject/images/logo_s.png");
		xProjectLogo.setLargeImageUrl("https://xprojectd044724trial.hanatrial.ondemand.com/xproject/images/logo_l.png");
		card.setTitle("xProject");
		card.setImage(xProjectLogo);
		card.setText(speechText);
		return card;
	}
}