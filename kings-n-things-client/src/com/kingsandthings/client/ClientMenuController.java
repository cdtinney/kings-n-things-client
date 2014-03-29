package com.kingsandthings.client;

import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Parent;
import javafx.stage.Stage;

import com.kingsandthings.client.game.GameController;
import com.kingsandthings.common.controller.Controller;
import com.kingsandthings.common.model.PlayerManager;
import com.kingsandthings.common.network.GameClient;
import com.kingsandthings.common.network.NetworkObjectHandler;
import com.kingsandthings.common.network.NetworkRegistry.InitializeGame;
import com.kingsandthings.common.network.NetworkRegistry.NetworkPlayerStatus;
import com.kingsandthings.game.events.PropertyChangeDispatcher;
import com.kingsandthings.util.Dialog;

public class ClientMenuController extends Controller implements NetworkObjectHandler {
	
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
		
		addEventHandlers();
		addListeners();
		
	}
	
	public void stop() {
		
		if (client != null) {
			client.end();
		}
		
	}

	@Override
	public void handleObject(Object object) {
		
		if (object instanceof NetworkPlayerStatus) {
			handleStatusMessage((NetworkPlayerStatus) object);
		}
		
		if (object instanceof InitializeGame) {
			handleInitializeGame((InitializeGame) object);
		}
		
		LOGGER.info("Received " + object);
		
	}

	protected void handleJoinButtonAction(Event event) {
		
		String playerName = view.getName();
		if (playerName == null || playerName.trim().length() == 0) {
			view.setStatusText("invalid player name");
			return;
		}
		
		client = new GameClient(playerName);
		client.setHandler(this);
		client.start(view.getIP(),  view.getPort());
		
		view.setStatusText("waiting to connect...");
		view.setEnabled(false);
		
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
	
	private void handleInitializeGame(final InitializeGame initializeGame) {
		
		final ClientMenuController instance = this;
		
		// Run on the JavaFX thread
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				
				stage.setTitle("K&T - Client - " + client.getName());
				
				gameController.initialize(stage, initializeGame.game, instance, client);
				client.send(NetworkPlayerStatus.PLAYER_INITIALIZED);
				
			}
		
		});
		
	}
	
	private void handleStatusMessage(NetworkPlayerStatus status) {
		
		switch (status) {
		
			case ALL_PLAYERS_NOT_CONNECTED:
				onAllPlayersConnected(false);
				break;
			case ALL_PLAYERS_CONNECTED:
				onAllPlayersConnected(true);
				break;
			default:
				break;
				
		}
		
	}
	
	@SuppressWarnings("unused")
	private void onConnectionChange(PropertyChangeEvent evt) {
		
		boolean connected = (boolean) evt.getNewValue();
		
		if (connected) {
			view.setStatusText("connected. waiting for other players to connect...");
			
		} else {
			view.setStatusText("disconnected");
			view.setEnabled(true);
			
		}
		
	}
	
	private void onAllPlayersConnected(boolean connected) {
		
		if (connected) {
			view.setStatusText("all players connected. waiting for game to start...");
			
		} else {
			view.setStatusText("connected. waiting for other players to connect...");
			
		}
		
	}
	
	private void addEventHandlers() {
		
		Parent root = view.getRoot();
		
		addEventHandler(root, "joinGameButton", "setOnAction", "handleJoinGameButtonAction");
		addEventHandler(root, "exitButton", "setOnAction", "handleExitButtonAction");
		
	}
	
	private void addListeners() {

		PropertyChangeDispatcher.getInstance().addListener(GameClient.class, "connected", this, "onConnectionChange");
		
	}
	
}
