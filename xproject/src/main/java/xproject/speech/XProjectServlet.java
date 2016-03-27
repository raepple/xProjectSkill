package xproject.speech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.servlet.SpeechletServlet;

public class XProjectServlet extends SpeechletServlet {
	private static final Logger log = LoggerFactory.getLogger(XProjectServlet.class);
	private static final long serialVersionUID = 1;

	public XProjectServlet() {
		this.setSpeechlet((Speechlet) new XProjectSpeechlet());
		log.info("XProjectServlet initialized");
	}
}