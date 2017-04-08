package View;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by thomas on 7-4-17.
 */
public class View implements Runnable {
	private Queue<String> messageStack = new LinkedBlockingQueue<>();

	//TODO
	public View() {
		//stub
	}

	//TODO
	@Override
	public void run() {

	}

	public boolean hasMessage() {
		return messageStack.size() > 0;
	}


	public String pollMessage() {
		return messageStack.poll();
	}
}
