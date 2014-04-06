package com.kingsandthings.client.game.board;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

import com.kingsandthings.client.game.Updatable;
import com.kingsandthings.common.controller.Controller;
import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.PlayerManager;
import com.kingsandthings.common.model.board.IBoard;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.SpecialIncome;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.common.network.GameClient;
import com.kingsandthings.common.util.CustomDataFormat;

public class BoardController extends Controller implements Updatable {
	
	private static Logger LOGGER = Logger.getLogger(BoardController.class.getName());
	
	// Networking
	private GameClient gameClient;

	// Model
	private Game game;
	private List<Thing> selectedThings;
	
	private Tile initialMovementTile;
	private boolean selectedForMovement = false;
	
	// Views
	private BoardView boardView;	
	
	// Sub-controllers
	private ExpandedTileController expandedTileController;
	private CombatController combatController;
	
	public void initialize(Game game, GameClient gameClient) {
		this.gameClient = gameClient;
		
		initialize(game);
		
	}
	
	public void initialize(Game game) {

		this.game = game;
		selectedThings = new ArrayList<Thing>();
		
		// Initialize the board view
		boardView = new BoardView();
		boardView.initialize(game);		
		
		// Initialize the expand tile controller, add to board (not visible)
		expandedTileController = new ExpandedTileController();
		expandedTileController.initialize(game);
		boardView.getChildren().add(expandedTileController.getView());

		// Initialize the combat controller, add to board (not visible)
		combatController = new CombatController();
		combatController.initialize(game, gameClient);
		boardView.getChildren().add(combatController.getView());
		
		// Set up event handlers for tiles
		addTileEventHandlers();
		
		addEventHandler(expandedTileController.getView(), "finishSelection", "setOnAction", "handleThingSelection");
		
		PropertyChangeDispatcher.getInstance().addListener(PlayerManager.class, "activePlayer", this, "onPlayerChanged");
		
	}
	
	@Override
	public void update(Game game) {
		this.game = game;
		
		// Update sub-controllers
		combatController.update(game);
		
		// Update views
		boardView.update(game);
		
	}
	
	public void updateInstruction(final String text) {
		
		Platform.runLater(new Runnable() {
			
			public void run() {
				boardView.setInstruction(text);
			}
			
		});
		
	}
	
	public Node getView() {
		return boardView;
	}
	
	@SuppressWarnings("unused")
	private void onPlayerChanged(PropertyChangeEvent event) {
		selectedThings.clear();
		selectedForMovement = false;
		initialMovementTile = null;		
	}
	
	@SuppressWarnings("unused")
	private void handleThingSelection(Event event) {
		expandedTileController.hideView();
		selectedThings = expandedTileController.getSelectedThings();
		selectedForMovement = !selectedThings.isEmpty();
	}
	
	// Tile action menu handler
	@SuppressWarnings("unused")
	private void handleSelectThings(Event event) {

		MenuItem item = (MenuItem) event.getSource();
		TileActionMenu tileActionMenu = (TileActionMenu) item.getParentPopup();
		TileView tileView = tileActionMenu.getOwner();
		
		initialMovementTile = tileView.getTile();
		expandedTileController.show(tileView.getTile(), gameClient.getName());
		
	}
	
	// Tile action menu handler
	@SuppressWarnings("unused")
	private void handlePlaceControlMarker(Event event) {
		
		MenuItem item = (MenuItem) event.getSource();
		
		TileActionMenu tileActionMenu = (TileActionMenu) item.getParentPopup();
		TileView tileView = tileActionMenu.getOwner();
		
		if (!gameClient.activePlayer()) {
			LOGGER.log(LogLevel.STATUS, "You are not the active player.");
			return;
		}
		
		IBoard serverBoard = gameClient.requestBoard();
		boolean result = serverBoard.setTileControl(tileView.getTile(), true);
		
	}
	
	// Tile action menu handler
	@SuppressWarnings("unused")
	private void handleResolveBattle(Event event) {
		
		MenuItem item = (MenuItem) event.getSource();
		
		TileActionMenu tileActionMenu = (TileActionMenu) item.getParentPopup();
		TileView tileView = tileActionMenu.getOwner();
		
		if (!gameClient.activePlayer()) {
			LOGGER.log(LogLevel.STATUS, "You are not the active player.");
			return;
		}
		
		Tile tile = tileView.getTile();
		if (!tile.hasBattleToResolve()) {
			LOGGER.log(LogLevel.STATUS, "Tile has no battles to resolve.");
			return;
		}
		
		combatController.start(tile);
		
	}
	
	// Tile action menu handler
	@SuppressWarnings("unused")
	private void handleBuildFort(Event event) {

		MenuItem item = (MenuItem) event.getSource();
		
		TileActionMenu tileActionMenu = (TileActionMenu) item.getParentPopup();
		TileView tileView = tileActionMenu.getOwner();
		
		if (!gameClient.activePlayer()) {
			LOGGER.log(LogLevel.STATUS, "You are not the active player.");
			return;
		}
		
		IBoard serverBoard = gameClient.requestBoard();
		boolean result = serverBoard.buildFort(tileView.getTile());
		
		System.out.println(result);
		
	}
	
	// Tile action menu handler
	@SuppressWarnings("unused")
	private void handleUpgradeFort(Event event) {

		MenuItem item = (MenuItem) event.getSource();
		
		TileActionMenu tileActionMenu = (TileActionMenu) item.getParentPopup();
		TileView tileView = tileActionMenu.getOwner();
		
		if (!gameClient.activePlayer()) {
			LOGGER.log(LogLevel.STATUS, "You are not the active player.");
			return;
		}
		
		IBoard serverBoard = gameClient.requestBoard();
		boolean result = serverBoard.upgradeFort(tileView.getTile());
		
	}
	
	@SuppressWarnings("unused")
	private void handleTileDragOver(Event event) {

		DragEvent dragEvent = (DragEvent) event;
		
		if (dragEvent.getDragboard().hasContent(CustomDataFormat.THINGS)) {
			
			TileView tileView = (TileView) event.getSource();
			Player owner = tileView.getTile().getOwner();
			
			boolean activePlayerOwned = owner != null && owner.getName().equals(gameClient.getName());
			if (activePlayerOwned) {
				dragEvent.acceptTransferModes(TransferMode.ANY);
			}
			
			tileView.addHighlight(activePlayerOwned);
			
		}
		
		dragEvent.consume();

	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	private void handleTileDragDropped(Event event) {

		DragEvent dragEvent = (DragEvent) event;
		
		boolean hasThings = dragEvent.getDragboard().hasContent(CustomDataFormat.THINGS);
		if (!hasThings) {
			return;
		}
		
		TileView tileView = (TileView) event.getSource();
		Tile tile = tileView.getTile();
		
		List<Thing> things = (List<Thing>) dragEvent.getDragboard().getContent(CustomDataFormat.THINGS);
		List<String> imageUrls = (List<String>) dragEvent.getDragboard().getContent(CustomDataFormat.IMAGES);
		
		for (int i=0; i<things.size(); ++i) {
			things.get(i).setImage(new Image(imageUrls.get(i)));
		}

		boolean success;
		IBoard board = gameClient.requestBoard();
		
		if (things.size() == 1 && things.get(0) instanceof Fort) {
			success = board.placeFort((Fort) things.get(0), tile);
			
		} else if (things.size() == 1 && things.get(0) instanceof SpecialIncome) {
			success = board.placeSpecialIncome((SpecialIncome) things.get(0), tile);
			
		} else {
			success = board.addThingsToTile(tile, things);
			
		}
		
		dragEvent.setDropCompleted(success);
		dragEvent.consume();
		
	}

	@SuppressWarnings("unused")
	private void handleTileDragExit(Event event) {
		TileView tileView = (TileView) event.getSource();
		tileView.removeHighlight();
	}
	
	@SuppressWarnings("unused")
	private void handleTileClick(Event event) {
		
		if (!gameClient.activePlayer()) {
			LOGGER.log(LogLevel.STATUS, "You are not the active player.");
			return;
		}
		
		TileView tileView = (TileView) event.getSource();
		Tile tile = tileView.getTile();
		
		if (selectedForMovement) {

			List<Tile> neighbours = game.getBoard().getNeighbours(game.getBoard().getTiles(), initialMovementTile);
			
			if (!neighbours.contains(tile)) {
				LOGGER.log(LogLevel.STATUS, "Cannot move Things more than one tile at a time.");
				return;
				
			} else if (!tile.isDiscovered()) {

				boardView.setInstruction("you are attempting to move to an unexplored hex. please roll the dice.");
				
				// Blocks until the user rolls
				boardView.showDice();
				
				// TASK - Demo only (hardcoded dice roll for movement)
				int roll = 1;
				//int roll = board.rollDice(1);

				IBoard board = gameClient.requestBoard();
				boolean success = board.moveThingsToUnexploredTile(roll, initialMovementTile, tile, selectedThings);
				
				boardView.setInstruction("do some movement");
				
			} else {
				
				IBoard board = gameClient.requestBoard();
				board.moveThings(initialMovementTile, tile, selectedThings);
				
			}

			tileView.removeHighlight();
			
			selectedThings.clear();
			initialMovementTile = null;
			selectedForMovement = false;
			return;
			
		}
		
		tileView.toggleActionMenu();	
		
	}
	
	@SuppressWarnings("unused")
	private void handleTileMouseExit(Event event) {
		TileView tileView = (TileView) event.getSource();
		
		if (selectedForMovement) {
			tileView.removeHighlight();
			return;
		}
		
		tileView.setOpacity(1.0);
	}
	
	@SuppressWarnings("unused")
	private void handleTileMouseEnter(Event event) {
		TileView tileView = (TileView) event.getSource();
		
		if (selectedForMovement) {
			
			List<Tile> neighbours = game.getBoard().getNeighbours(game.getBoard().getTiles(), initialMovementTile);
			tileView.addHighlight(neighbours.contains(tileView.getTile()));
			return;
			
		}
		
		tileView.setOpacity(0.8);
	}
	
	private void addTileEventHandlers() {
		
		TileView[][] tileViews = boardView.getTiles();
	
		for (int i=0; i<tileViews.length; ++i) {
			for (int j=0; j<tileViews[i].length; ++j) {
				
				final TileView tileView = tileViews[i][j];
				if (tileView == null) {
					continue;
				}
				
				addEventHandler(tileView, "setOnMouseClicked", "handleTileClick");
				addEventHandler(tileView, "setOnMouseEntered", "handleTileMouseEnter");
				addEventHandler(tileView, "setOnMouseExited", "handleTileMouseExit");
				
				// Drag and drop
				addEventHandler(tileView, "setOnDragOver", "handleTileDragOver");
				addEventHandler(tileView, "setOnDragDropped", "handleTileDragDropped");
				addEventHandler(tileView, "setOnDragExited", "handleTileDragExit");
				
				// Action menu (TODO - Refactor. Too many event handlers.
				addEventHandler(tileView.getActionMenu().get("placeControlMarker"), "setOnAction", "handlePlaceControlMarker");
				addEventHandler(tileView.getActionMenu().get("selectThings"), "setOnAction", "handleSelectThings");
				addEventHandler(tileView.getActionMenu().get("buildFort"), "setOnAction", "handleBuildFort");
				addEventHandler(tileView.getActionMenu().get("upgradeFort"), "setOnAction", "handleUpgradeFort");
				addEventHandler(tileView.getActionMenu().get("resolveBattle"), "setOnAction", "handleResolveBattle");
				
			}
		}
	}

}
