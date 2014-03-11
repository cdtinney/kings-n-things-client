package com.kingsandthings.game.board;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import com.kingsandthings.game.InitializableView;
import com.kingsandthings.model.Game;
import com.kingsandthings.model.Player;
import com.kingsandthings.model.board.Tile;
import com.kingsandthings.model.things.Thing;
import com.kingsandthings.util.DataImageView;

public class ExpandedTileView extends VBox implements InitializableView {

	private static final int WIDTH = 500;
	private static final int THING_WIDTH = 60;
	
	// Model
	private Game game;
	private Tile tile;
	
	// Map each player to a list of images
	private Map<Player, List<DataImageView>> playerThingImages;
	
	// Views
	private List<GridPane> grids;
	
	public ExpandedTileView(Game game) {
		this.game = game;
		
		playerThingImages = new LinkedHashMap<Player, List<DataImageView>>();
		grids = new ArrayList<GridPane>();
	}
	
	@Override
	public void initialize() {
		
		setPrefWidth(WIDTH);
		setSpacing(20);
		
		setLayoutX(644 / 2 - 250);
		setLayoutY(50);
		
		setStyle("-fx-opacity: 0.9; -fx-background-color: transparent, derive(#1d1d1d,20%);");
		
		addCloseButton();
		
		for (Player player : playerThingImages.keySet()) {
			addPlayerGrid(player);
		}
		
		addActionButtons();
		
	}
	
	public List<DataImageView> getImageViews() {
		
		List<DataImageView> imageViews = new ArrayList<DataImageView>();
		for (List<DataImageView> playerImageViews : playerThingImages.values()) {
			imageViews.addAll(playerImageViews);
		}
		
		return imageViews;
		
	}
	
	public void setPlayers(List<Player> players) {
		
		for (Player player : players) {
			playerThingImages.put(player, new ArrayList<DataImageView>());
		}
	}
	
	public void setTile(Tile tile) {
		this.tile = tile;
	}
	
	public void updatePlayerThings() {
		
		if (tile == null) {
			return;
		}
		
		for (Player player : playerThingImages.keySet()) {
			
			if (player != game.getActivePlayer()) {
				continue;
			}
			
			List<Thing> things = tile.getThings().get(player);
			
			if (things == null || things.size() > Tile.MAXIMUM_THINGS) {
				return;
			}
			
			for (int i=0; i<things.size(); ++i) {

				DataImageView imgView = playerThingImages.get(player).get(i);
				Thing thing = things.get(i);
				
				imgView.setImage(thing.getImage());
				imgView.setData(thing);
				
				imgView.setVisible(true);
				imgView.getParent().setVisible(true);
				
			}
			
		}
		
	}
	
	public void clear() {

		for (List<DataImageView> imageViews : playerThingImages.values()) {
			DataImageView.clear(imageViews, true);
		}
		
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
	
	private void addPlayerGrid(Player player) {
		
		// Grid
		GridPane grid = GridPaneBuilder.create().hgap(10).vgap(5).prefHeight(150).visible(false).build();
		grid.managedProperty().bind(grid.visibleProperty());
		setMargin(grid, new Insets(0, 10, 0, 10));
		
		// Player name text
		Text text = TextBuilder.create().text(player.getName()).font(Font.font("Lucida Sans", 20)).fill(Color.WHITE).build();
		text.managedProperty().bind(text.visibleProperty());
		text.visibleProperty().bind(grid.visibleProperty());
		setMargin(text, new Insets(0, 0, 0, 10));
		getChildren().add(text);
		
		for (int i=0; i<Tile.MAXIMUM_THINGS; ++i) {
			
			DataImageView imgView = new DataImageView(false, THING_WIDTH);
			imgView.managedProperty().bind(imgView.visibleProperty());
			imgView.setVisible(false);
			
			playerThingImages.get(player).add(imgView);
			
			grid.add(imgView, i%8, i >= 8 ? 1 : 0);
			
		}
		
		grids.add(grid);
		getChildren().add(grid);
		
	}
	
}
