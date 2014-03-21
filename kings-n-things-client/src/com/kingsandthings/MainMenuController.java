package com.kingsandthings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javafx.event.Event;
import javafx.scene.Parent;
import javafx.stage.Stage;

import com.kingsandthings.common.controller.Controller;
import com.kingsandthings.common.model.PlayerManager;
import com.kingsandthings.common.network.GameClientImpl;
import com.kingsandthings.game.GameController;
import com.kingsandthings.util.Dialog;

public class MainMenuController extends Controller {
	
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(PlayerManager.class.getName());
	
	// Primary stage
	private Stage stage;
	
	// View
	private MainMenuView view;
	
	// Sub-controllers
	private GameController gameController = new GameController();
	
	// Networking
	private GameClientImpl client;
	
	public void initialize(Stage stage) {
		
		this.stage = stage;
		
		Dialog.setStage(stage);
		
		view = new MainMenuView();
		view.initialize();
		
		stage.setScene(view);
		stage.centerOnScreen();
		
		setupHandlers();
		
	}
	
	private void setupHandlers() {
		
		Parent root = view.getRoot();
		
		addEventHandler(root, "joinGameButton", "setOnAction", "handleJoinGameButtonAction");
		addEventHandler(root, "exitButton", "setOnAction", "handleExitButtonAction");
		
	}
	
	protected void handleJoinButtonAction(Event event) {
		
		List<String> playerNames = new ArrayList<String>();
		for (String name : playerNames) {
			
			if (name.trim().length() == 0) {
				view.setStatusText("one or more player names are empty");
				return;
			}
			
		}
		
		if (playerNames.size() == 0) {
			playerNames.addAll(Arrays.asList("Player 1", "Player 2", "Player 3", "Player 4"));
		}
		
		client = new GameClientImpl(view.getIP(), view.getPort());
		
		synchronized (client.getConnectedLock()) {

			try {
				client.getConnectedLock().wait();
				gameController.initialize(stage, playerNames, this);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				
			}
			
		}
	}
	
	protected void handleJoinGameButtonAction(Event event) {
		
		view.displayGameSettings();
		
		addEventHandler(view.getRoot(), "joinButton", "setOnAction", "handleJoinButtonAction");
		addEventHandler(view.getRoot(), "backButton", "setOnAction", "handleBackButtonAction");
		
	}
	
	protected void handleBackButtonAction(Event event) {
		view.displayMainMenu();
	}
	
	protected void handleExitButtonAction(Event event) {
		stage.close();
	}
	
}
