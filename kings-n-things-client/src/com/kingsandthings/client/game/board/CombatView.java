package com.kingsandthings.client.game.board;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.common.util.DataImageView;

public class CombatView extends VBox {

	private static final int WIDTH = 500;
	private static final int THING_WIDTH = 60;
	
	// Model
	private Game game;
	private Tile tile;
	
	private Player attacker;
	private List<DataImageView> attackerThingImages;
	
	private Player defender;
	private List<DataImageView> defenderThingImages;
	
	// Views
	private List<GridPane> grids;
	
	public CombatView(Game game) {
		this.game = game;
		
		attackerThingImages = new ArrayList<DataImageView>();
		defenderThingImages = new ArrayList<DataImageView>();
		grids = new ArrayList<GridPane>();
	}
	
	public void initialize() {
		
		setPrefWidth(WIDTH);
		setSpacing(20);
		
		setLayoutX(644 / 2 - 250);
		setLayoutY(50);
		
		setStyle("-fx-opacity: 0.9; -fx-background-color: transparent, derive(#1d1d1d,20%);");
		
		addCloseButton();
		
		addPlayerGrid(true);
		addPlayerGrid(false);
		
		addActionButtons();
		
	}
	
	public void setAttacker(Player player) {
		attacker = player;
	}
	
	public void setDefender(Player player) {
		defender = player;
	}
	
	public List<DataImageView> getImageViews() {
		
		List<DataImageView> imageViews = new ArrayList<DataImageView>();
		imageViews.addAll(attackerThingImages);
		imageViews.addAll(defenderThingImages);
		
		return imageViews;
		
	}
	
	public void setTile(Tile tile) {
		this.tile = tile;
	}
	
	public void updateThings() {
		
		Player defender = tile.getOwner();
		((Text) lookup("#defenderText")).setText("Defender: " + defender.getName());
		
		List<Thing> defenderThings = tile.getThings().get(defender);
		for (int i=0; i<defenderThings.size(); ++i) {

			DataImageView imgView = defenderThingImages.get(i);
			Thing thing = defenderThings.get(i);
			
			imgView.setImage(thing.getImage());
			imgView.setData(thing);
			
			imgView.setVisible(true);
			imgView.getParent().setVisible(true);
			
		}
		
		// Find the attacker
		Player attacker = null;
		for (Player player : game.getPlayerManager().getPlayers()) {
			if (!player.equals(defender) && !tile.getThings().get(defender).isEmpty()) {
				attacker = player;
				break;
			}
		}
		
		if (attacker == null) {
			return;
		}
		
		((Text) lookup("#attackerText")).setText("Attacker: " + attacker.getName());
		
		List<Thing> attackerThings = tile.getThings().get(attacker);
		for (int i=0; i<attackerThings.size(); ++i) {

			DataImageView imgView = attackerThingImages.get(i);
			Thing thing = attackerThings.get(i);
			
			imgView.setImage(thing.getImage());
			imgView.setData(thing);
			
			imgView.setVisible(true);
			imgView.getParent().setVisible(true);
			
		}
		
	}
	
	public void clear() {
		
		DataImageView.clear(attackerThingImages, true);
		DataImageView.clear(defenderThingImages, true);		
		
		for (GridPane grid : grids) {
			grid.setVisible(false);
		}
		
		setTile(null);
		setVisible(false);
		
	}
	
	private void addActionButtons() {
		
		HBox actionsBox = new HBox();
		setMargin(actionsBox, new Insets(0, 0, 10, 10));
		
		Button finishSelection = new Button("Finish Selection");
		finishSelection.getStyleClass().add("nofocus");
		finishSelection.setId("finishSelection");
		
		actionsBox.getChildren().add(finishSelection);
		
		getChildren().add(actionsBox);
		
	}
	
	private void addCloseButton() {
		
		HBox closeButtonBox = new HBox();
		closeButtonBox.setAlignment(Pos.TOP_RIGHT);
		
		Button b = new Button("Close");
		b.getStyleClass().add("nofocus");
		b.setId("close");
		
		closeButtonBox.getChildren().add(b);
		HBox.setMargin(b, new Insets(5, 5, 0, 0));
		
		getChildren().add(closeButtonBox);
		
	}
	
	private void addPlayerGrid(boolean attacker) {
		
		// Grid
		GridPane grid = GridPaneBuilder.create().hgap(10).vgap(5).prefHeight(150).visible(false).build();
		grid.managedProperty().bind(grid.visibleProperty());
		setMargin(grid, new Insets(0, 10, 0, 10));
		
		Text text = TextBuilder.create().text(attacker ? "Attacker" : "Defender").font(Font.font("Lucida Sans", 20)).fill(Color.WHITE).build();
		text.setId(attacker? "attackerText" : "defenderText");
		text.managedProperty().bind(text.visibleProperty());
		text.visibleProperty().bind(grid.visibleProperty());
		setMargin(text, new Insets(0, 0, 0, 10));
		getChildren().add(text);
		
		for (int i=0; i<Tile.MAXIMUM_THINGS; ++i) {
			
			DataImageView imgView = new DataImageView(false, THING_WIDTH);
			imgView.managedProperty().bind(imgView.visibleProperty());
			imgView.setVisible(false);
			
			if (attacker) {
				attackerThingImages.add(imgView);
			} else {
				defenderThingImages.add(imgView);
			}
			
			grid.add(imgView, i%8, i >= 8 ? 1 : 0);
			
		}
		
		grids.add(grid);
		getChildren().add(grid);
		
	}
	
}
