package com.kingsandthings.client.game;

import java.util.logging.Logger;

import javafx.stage.Stage;

import com.kingsandthings.client.ClientMenuController;
import com.kingsandthings.client.game.board.BoardController;
import com.kingsandthings.client.game.player.PlayerPaneController;
import com.kingsandthings.common.controller.Controller;
import com.kingsandthings.common.model.Game;

public class GameController extends Controller {
	
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(GameController.class.getName());
	
	// Model 
	private Game game;
	
	// View
	private GameView view;
	
	// Sub-controllers
	private BoardController boardController;
	private PlayerPaneController playerController;
	private GameActionController gameActionController;
	
	public void initialize(Stage stage, Game game, ClientMenuController parent) {
		
		view = new GameView();
		view.initialize();
		
		this.game = game;
		
		boardController = new BoardController();
		playerController = new PlayerPaneController();
		gameActionController = new GameActionController();
		
		initializeSubControllers();
		addSubViews();
		
		stage.setScene(view);
		stage.centerOnScreen();
		
		addEventHandler(view.getRoot(), "quitGameMenuItem", "setOnAction", "handleQuitGameMenuItemAction");
		
	}
	
	public GameActionController getGameActionController() {
		return gameActionController;
	}
	
	private void initializeSubControllers() {
		boardController.initialize(game);
		playerController.initialize(game);
		gameActionController.initialize(game);
	}
	
	private void addSubViews() {
		
		view.addToBorderPane(boardController.getView(), "center");
		view.addToBorderPane(playerController.getView(), "right");
		view.addToBorderPane(gameActionController.getView(), "left");
		
	}

}
