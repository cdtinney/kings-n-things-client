package com.kingsandthings.client.game.board;

import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBoxBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.kingsandthings.client.game.Updatable;
import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.board.Tile;

public class BoardView extends Pane implements Updatable {
	
	private static Logger LOGGER = Logger.getLogger(BoardView.class.getName());
	
	private TileView[][] tileViews = new TileView[10][10];
	
	private Stage diceStage;
	private Label instructions;
	
	public void initialize(Game game) {
		
		getStyleClass().addAll("pane", "board");
		
		int initialX = 70;
		int initialY = 190;
		int xOffset = 79;
		int yOffset = 91;
		int columnOffset = 45;
		
		tileViews = generateTileViews(initialX, initialY, xOffset, yOffset, columnOffset, 10);
		
		addTileViews(tileViews);
		addInstructionText();
		addDiceStage();
		
		initializeTiles(game.getBoard().getTiles());
		
		addListeners();
		
	}
	
	public void update(Game game) {

		Tile[][] modelTiles = game.getBoard().getTiles();
		for (int i=0; i<modelTiles.length; ++i) {
			for (int j=0; j<modelTiles[i].length; ++j) {
				
				if (tileViews[i][j] != null && modelTiles[i][j] != null) {
					tileViews[i][j].setTile(modelTiles[i][j], false);
				}			
				
			}
		}
		
	}
	
	public void setInstruction(String text) {
		instructions.setText(text);
	}
	
	public TileView[][] getTiles() {
		return tileViews;
	}
	
	public void initializeTiles(Tile[][] modelTiles) {
		
		for (int i=0; i<modelTiles.length; ++i) {
			for (int j=0; j<modelTiles[i].length; ++j) {
				
				Tile tile = modelTiles[i][j];
				
				try {
					
					final TileView view = tileViews[i][j];
					
					if (tile == null || view == null) {
						continue;
					}
					
					view.initialize(tile);
					
				} catch (IndexOutOfBoundsException e) {
					LOGGER.warning("Model and view tile array size mismatch - " + e.getMessage());
					
				}
				
			}
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public Integer getDiceValue() {
		
		if (diceStage == null) {
			return -1;
		}
		
		if(diceStage.getScene() == null) {
			return -1;
		}
		
		Integer val = (Integer) ((ComboBox) diceStage.getScene().lookup("#diceValue")).getValue();
		return val;
		
	}
	
	public void showDice() {		
		
		if (diceStage.getOwner() == null) {
			diceStage.initOwner(getScene().getWindow());
		}
		
		Stage parent = (Stage) getScene().getWindow();
		diceStage.setX(parent.getX() + parent.getWidth() / 2 - 175);
		diceStage.setY(parent.getY() + parent.getHeight() / 2 + 350);
		
        diceStage.showAndWait();
        
	}
	
	private void addListeners() {
		
		PropertyChangeDispatcher.getInstance().addListener(Tile.class, "owner", this, "onTileOwnerChange");
		PropertyChangeDispatcher.getInstance().addListener(Tile.class, "fort", this, "onTileFortChanged");
		PropertyChangeDispatcher.getInstance().addListener(Tile.class, "things", this, "onTileThingsChange");
		PropertyChangeDispatcher.getInstance().addListener(Tile.class, "battleToResolve", this,"onBattleChange");
		PropertyChangeDispatcher.getInstance().addListener(Tile.class, "specialIncome", this,"onTileSpecialIncomeChange");
		
	}
	
	@SuppressWarnings("unused")
	private void onBattleChange(PropertyChangeEvent evt) {
		
		TileView view = getViewFromEvent(evt);
		if (view == null) {
			return;
		}
		
		view.updateBattleHighlight();
	}
	
	@SuppressWarnings("unused")
	private void onTileOwnerChange(PropertyChangeEvent evt) {
		
		TileView view = getViewFromEvent(evt);
		if (view == null) {
			return;
		}

		view.updateControlMarkerView();
		view.setImage(view.getTile().getImage());
		
	}
	
	@SuppressWarnings("unused")
	private void onTileThingsChange(PropertyChangeEvent evt) {
		
		TileView view = getViewFromEvent(evt);
		if (view == null) {
			return;
		}
		
		view.updateThingsStackView();
	}
	
	@SuppressWarnings("unused")
	private void onTileFortChanged(PropertyChangeEvent evt) {
		
		TileView view = getViewFromEvent(evt);
		if (view == null) {
			return;
		}
		
		view.updateFortView();		
	}
	
	@SuppressWarnings("unused")
	private void onTileSpecialIncomeChange(PropertyChangeEvent evt) {
		
		TileView view = getViewFromEvent(evt);
		if (view == null) {
			return;
		}

		view.updateSpecialIncomeView();
	}
	
	private TileView getViewFromEvent(PropertyChangeEvent evt) {
		
		Tile tile = (Tile) evt.getSource();
		if (tile == null) {
			LOGGER.warning("Event source is not a Tile");
			return null;
		}
		
		TileView view = findTileView(tile);
		if (view == null) {
			LOGGER.warning("No TileView found for the specified Tile");
		}
		
		return view;
		
	}
	
	private TileView findTileView(Tile tile) {
		
		for (TileView[] row : tileViews) {
			for (TileView view : row) {
				if (view == null) continue;
				
				if (view.getTile().equals(tile)) return view;
				
			}
		}
		
		return null;
		
	}
	
	private void addTileViews(TileView[][] tiles) {
		
		for (TileView[] row : tiles) {
			for (TileView tile : row) {
				
				if (tile != null) {
					getChildren().add(tile);
				}
				
			}
		}
		
	}
	
	private void addDiceStage() {
		
	 	diceStage = new Stage();
	    diceStage.initStyle(StageStyle.TRANSPARENT);
	    diceStage.initModality(Modality.APPLICATION_MODAL);
		
		ImageView imgView = new ImageView(new Image("/images/extra/dice.png"));
		imgView.setPreserveRatio(true);
		imgView.setFitWidth(30);
		
		Button rollDiceButton = new Button("Roll Dice", imgView);
		rollDiceButton.getStyleClass().addAll("diceButton", "nofocus");
		rollDiceButton.setPrefHeight(40);
		rollDiceButton.setPrefWidth(130);
		
		ObservableList<Integer> options = FXCollections.observableArrayList(
			        1, 2, 3, 4, 5, 6
			    );
		ComboBox<Integer> comboBox = new ComboBox<Integer>(options);
		comboBox.setId("diceValue");
		
		HBox box = new HBox(10);
		box.setAlignment(Pos.CENTER);
		box.getChildren().addAll(rollDiceButton, comboBox);
		
		Scene myDialogScene = new Scene(VBoxBuilder.create().children(box).alignment(Pos.CENTER).padding(new Insets(0)).build());
		
        diceStage.setScene(myDialogScene);
       
        rollDiceButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				diceStage.close();
			}
        });
		
	}
	
	private void addInstructionText() {
		
		instructions = new Label("instructions go here");
		instructions.getStyleClass().add("instructionsText");
		instructions.setPrefWidth(getBoundsInParent().getWidth() + 75);
		getChildren().add(instructions);
		
	}
	
	private TileView[][] generateTileViews(int initialX, int initialY, int xOffset, int yOffset, int columnOffset, int size) {

		TileView[][] tiles = new TileView[size][size];
		
		// column 0
		tiles[0][0] = new TileView(null, initialX, initialY);
		
		tiles[1][0] = new TileView(null, initialX, initialY + yOffset);
		tiles[2][0] = new TileView(null, initialX, initialY + yOffset*2);
		tiles[3][0] = new TileView(null, initialX, initialY + yOffset*3);
		
		// column 1
		tiles[0][1] = new TileView(null, initialX + xOffset, initialY - columnOffset);
		tiles[1][1] = new TileView(null, initialX + xOffset, initialY + yOffset - columnOffset);
		tiles[2][1] = new TileView(null, initialX + xOffset, initialY + yOffset*2 - columnOffset);
		tiles[3][1] = new TileView(null, initialX + xOffset, initialY + yOffset*3 - columnOffset);
		tiles[4][1] = new TileView(null, initialX + xOffset, initialY + yOffset*4 - columnOffset);
		
		// column 3
		tiles[0][2] = new TileView(null, initialX + xOffset*2, initialY - columnOffset*2);
		tiles[1][2] = new TileView(null, initialX + xOffset*2, initialY + yOffset - columnOffset*2);
		tiles[2][2] = new TileView(null, initialX + xOffset*2, initialY + yOffset*2 - columnOffset*2);
		tiles[3][2] = new TileView(null, initialX + xOffset*2, initialY + yOffset*3 - columnOffset*2);
		tiles[4][2] = new TileView(null, initialX + xOffset*2, initialY + yOffset*4 - columnOffset*2);
		tiles[5][2] = new TileView(null, initialX + xOffset*2, initialY + yOffset*5 - columnOffset*2);
		
		// column 4 (center)
		tiles[0][3] = new TileView(null, initialX + xOffset*3, initialY - columnOffset*3);
		tiles[1][3] = new TileView(null, initialX + xOffset*3, initialY + yOffset - columnOffset*3);
		tiles[2][3] = new TileView(null, initialX + xOffset*3, initialY + yOffset*2 - columnOffset*3);
		tiles[3][3] = new TileView(null, initialX + xOffset*3, initialY + yOffset*3 - columnOffset*3);
		tiles[4][3] = new TileView(null, initialX + xOffset*3, initialY + yOffset*4 - columnOffset*3);
		tiles[5][3] = new TileView(null, initialX + xOffset*3, initialY + yOffset*5 - columnOffset*3);
		tiles[6][3] = new TileView(null, initialX + xOffset*3, initialY + yOffset*6 - columnOffset*3);
		
		// column 5
		tiles[0][4] = new TileView(null, initialX + xOffset*4, initialY - columnOffset*2);
		tiles[1][4] = new TileView(null, initialX + xOffset*4, initialY + yOffset - columnOffset*2);
		tiles[2][4] = new TileView(null, initialX + xOffset*4, initialY + yOffset*2 - columnOffset*2);
		tiles[3][4] = new TileView(null, initialX + xOffset*4, initialY + yOffset*3 - columnOffset*2);
		tiles[4][4] = new TileView(null, initialX + xOffset*4, initialY + yOffset*4 - columnOffset*2);
		tiles[5][4] = new TileView(null, initialX + xOffset*4, initialY + yOffset*5 - columnOffset*2);

		// column 6
		tiles[0][5] = new TileView(null, initialX + xOffset*5, initialY - columnOffset);
		tiles[1][5] = new TileView(null, initialX + xOffset*5, initialY + yOffset - columnOffset);
		tiles[2][5] = new TileView(null, initialX + xOffset*5, initialY + yOffset*2 - columnOffset);
		tiles[3][5] = new TileView(null, initialX + xOffset*5, initialY + yOffset*3 - columnOffset);
		tiles[4][5] = new TileView(null, initialX + xOffset*5, initialY + yOffset*4 - columnOffset);
		
		// column 7
		tiles[0][6] = new TileView(null, initialX + xOffset*6, initialY);
		tiles[1][6] = new TileView(null, initialX + xOffset*6, initialY + yOffset);
		tiles[2][6] = new TileView(null, initialX + xOffset*6, initialY + yOffset*2);
		tiles[3][6] = new TileView(null, initialX + xOffset*6, initialY + yOffset*3);
		
		return tiles;
		
	}
	
}
