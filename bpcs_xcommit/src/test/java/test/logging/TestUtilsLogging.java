package test.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.Test;

public class TestUtilsLogging {
	
	@Test
	public void testUtilsLoggingMet() throws SecurityException, IOException {

		File file = new File("C:/Temp/paypallog.properties");
		LogManager.getLogManager().readConfiguration(new FileInputStream(file));
		
		Logger log = Logger.getLogger(TestUtilsLogging.class.getName());

		log.info("initializing - trying to load configuration file ...");
		log.fine(" fine initializing - trying to load configuration file ...");
		log.log(Level.FINE," x FINE Level : initializing - trying to load configuration file ...");
	}
}