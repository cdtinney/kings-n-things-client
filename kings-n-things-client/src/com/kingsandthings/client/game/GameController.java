package com.kingsandthings.client.game;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.stage.Stage;

import com.kingsandthings.client.ClientMenuController;
import com.kingsandthings.client.game.board.BoardController;
import com.kingsandthings.client.game.player.PlayerPaneController;
import com.kingsandthings.common.controller.Controller;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.network.GameClient;
import com.kingsandthings.common.network.NetworkObjectHandler;
import com.kingsandthings.common.network.NetworkRegistry.GameStatus;
import com.kingsandthings.common.network.NetworkRegistry.Instruction;
import com.kingsandthings.common.network.NetworkRegistry.UpdateGame;
import com.kingsandthings.common.network.NetworkRegistry.WinGame;
import com.kingsandthings.common.util.Dialog;
import com.kingsandthings.common.util.Dialog.Type;

public class GameController extends Controller implements NetworkObjectHandler {
	
	private static Logger LOGGER = Logger.getLogger(GameController.class.getName());

	// Networking
	private GameClient gameClient;
	
	// Model 
	private Game game;
	
	// View
	private GameView view;
	
	// Sub-controllers
	private BoardController boardController;
	private PlayerPaneController playerController;
	private GameActionController gameActionController;
	
	public GameController() {
		
		boardController = new BoardController();
		playerController = new PlayerPaneController();
		gameActionController = new GameActionController();
		
	}
	
	public void initialize(Stage stage, Game game, ClientMenuController parent, GameClient client) {
		
		gameClient = client;
		if (gameClient != null) {
			gameClient.setHandler(this);
		}
		
		initialize(stage, game, parent);
		
	}
	
	public void initialize(Stage stage, Game game, ClientMenuController parent) {

		this.game = game;
		
		view = new GameView();
		view.initialize();
		
		initializeSubControllers();
		addSubViews();
		
		stage.setScene(view);
		
		stage.setWidth(GameView.WIDTH);
		stage.setHeight(GameView.HEIGHT);
		
	}

	@Override
	public void handleObject(Object object) {
		
		if (object instanceof UpdateGame) {
			updateGame((UpdateGame) object);
			return;
		}
		
		if (object instanceof Instruction) {
			handleInstruction((Instruction) object);
			return;
		}
		
		if (object instanceof WinGame) {
			handleWinGame((WinGame) object);
			return;
		}
		
		if (object instanceof GameStatus) {
			handleStatus((GameStatus) object);
			return;
		}
		
		LOGGER.info("Received " + object);
		
	}
	
	private void handleStatus(final GameStatus gameStatus) {
		
		Platform.runLater(new Runnable() {
			
			public void run() {
				view.setStatusText(gameStatus.message);
			}
			
		});
		
	}

	private void handleWinGame(final WinGame winGame) {
		
		Platform.runLater(new Runnable() {
			
			public void run() {
				Dialog.show(Type.NOTIFY, "Player: " + winGame.winner.getName() + " has won!");
			}
			
		});
		
	}

	private void handleInstruction(Instruction instruction) {
		boardController.updateInstruction(instruction.text);
	}

	private void updateGame(UpdateGame update) {
		
		Game game = update.game;
		
		boardController.update(game);
		playerController.update(game);
		gameActionController.update(game);
		
	}
	
	private void initializeSubControllers() {
		
		boardController.initialize(game, gameClient);
		playerController.initialize(game, gameClient);
		gameActionController.initialize(game, gameClient);
		
	}
	
	private void addSubViews() {
		
		view.addToBorderPane(boardController.getView(), "center");
		view.addToBorderPane(playerController.getView(), "right");
		view.addToBorderPane(gameActionController.getView(), "left");
		
	}

}
