package com.kingsandthings.client.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.kingsandthings.client.game.GameView;
import com.kingsandthings.common.logging.LogFormatter;
import com.kingsandthings.common.logging.LogLevel;

public class ClientLogHandler extends Handler {
	
	private static GameView view;
	
	public static void setHandler(Logger logger) {
	
		Logger parent = logger.getParent();
		
		for (Handler handler: parent.getHandlers()) {
			parent.removeHandler(handler);
		}
		
		ClientLogHandler customHandler = new ClientLogHandler();
		customHandler.setFormatter(new LogFormatter());
		customHandler.setLevel(LogLevel.TRACE);	
		
		parent.setLevel(LogLevel.DEBUG);
		parent.addHandler(customHandler);
		
	}
	
	public static void setView(GameView view) {
		ClientLogHandler.view = view;
	}

	@Override
	public void publish(LogRecord r) {
		
		// Display status messages on the view
		if (r.getLevel() == LogLevel.STATUS && view != null) {
			view.setStatusText(r.getMessage());
		}
		
		// Print to stdout
		System.out.println(getFormatter().format(r));
		
	}

	@Override
	public void close() throws SecurityException {
		// Do nothing
	}

	@Override
	public void flush() {
		// Do nothing
	}
}
