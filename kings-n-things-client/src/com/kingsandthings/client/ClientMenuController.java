package com.kingsandthings.client;

import java.util.logging.Logger;

import javafx.event.Event;
import javafx.scene.Parent;
import javafx.stage.Stage;

import com.kingsandthings.client.game.GameController;
import com.kingsandthings.common.controller.Controller;
import com.kingsandthings.common.model.PlayerManager;
import com.kingsandthings.common.network.GameClient;
import com.kingsandthings.common.network.NetworkObjectHandler;
import com.kingsandthings.util.Dialog;

public class ClientMenuController extends Controller implements NetworkObjectHandler {
	
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(PlayerManager.class.getName());
	
	// Primary stage
	private Stage stage;
	
	// View
	private ClientMenuView view;
	
	// Sub-controllers
	private GameController gameController = new GameController();
	
	// Networking
	private GameClient client;
	
	public void initialize(Stage stage) {
		
		this.stage = stage;
		
		Dialog.setStage(stage);
		
		view = new ClientMenuView();
		view.initialize();
		
		stage.setScene(view);
		stage.centerOnScreen();
		
		setupHandlers();
		
	}
	
	public void stop() {
		client.end();
	}

	@Override
	public void handleObject(Object object) {
		
		System.out.println("controller received " + object);
		
	}
	
	private void setupHandlers() {
		
		Parent root = view.getRoot();
		
		addEventHandler(root, "joinGameButton", "setOnAction", "handleJoinGameButtonAction");
		addEventHandler(root, "exitButton", "setOnAction", "handleExitButtonAction");
		
	}
	
	protected void handleJoinButtonAction(Event event) {
		
		String playerName = view.getName();
		if (playerName == null || playerName.trim().length() == 0) {
			view.setStatusText("invalid player name");
			return;
		}
		
		client = new GameClient(playerName);
		client.getHandlers().add(this);
		client.start(view.getIP(),  view.getPort());
		
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
