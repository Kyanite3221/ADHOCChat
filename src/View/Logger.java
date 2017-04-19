package View;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalTime;

/**
 * Created by thomas on 19-4-17.
 */
public class Logger {
	private PrintWriter writer = null;

	public Logger() {
		try {
			writer = new PrintWriter("log.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void write(String s) {
		writer.println(LocalTime.now() + " - " + s);
		writer.flush();
	}

	public void close() {
		writer.close();
	}
}
