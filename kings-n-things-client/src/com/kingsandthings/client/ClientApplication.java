package com.kingsandthings.client;
	
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.kingsandthings.logging.LogHandler;

public class ClientApplication extends Application {
	
	private static Logger LOGGER = Logger.getLogger(ClientApplication.class.getName());
	
	private final float VERSION = 0.2f;
	
	private ClientMenuController mainMenuController = new ClientMenuController();
	
	@Override
	public void start(Stage primaryStage) {
		
		primaryStage.setResizable(false);
		primaryStage.setTitle("Kings & Things - Client -  v" + VERSION);
		primaryStage.getIcons().add(new Image("/images/icon.png"));
		
		mainMenuController.initialize(primaryStage);
		
		primaryStage.show();
		
	}
	
	@Override
	public void stop() {
		LOGGER.info("Application has stopped.");
	}
	
	public static void main(String[] args) {
		
		LogHandler.setup(LOGGER);
		
		launch(args);
	}
}
