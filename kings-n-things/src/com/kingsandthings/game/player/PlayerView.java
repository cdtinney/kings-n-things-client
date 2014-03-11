package com.kingsandthings.game.player;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import com.kingsandthings.game.InitializableView;
import com.kingsandthings.model.Player;
import com.kingsandthings.model.things.Fort;
import com.kingsandthings.model.things.Thing;
import com.kingsandthings.util.DataImageView;

public class PlayerView extends Pane implements InitializableView {
	
	private static final Image rackImg = new Image("/images/extra/rack.png");
	
	private static final int WIDTH = 425;
	private static final int HEIGHT = 165;
	
	private static final int RACK_ITEM_WIDTH = 30;
	private static final int RACK_ITEM_GAP = 23;
	
	private static final int INITIAL_RACK_ITEM_X = 37;
	private static final int INITIAL_RACK_X = 15;
	private static final int INITIAL_RACK_Y = 55;
	
	private static final int FORTS_X = WIDTH - 100;
	private static final int FORTS_Y = INITIAL_RACK_Y;
	
	// Model
	private Player player;
	
	// View elements
	private Text playerNameText;
	private Text numGoldText;
	private ImageView controlMarkerImage;
	
	private List<DataImageView> rackImages;
	private List<DataImageView> fortImages;
	
	private VBox fortsVBox;
	
	public PlayerView(Player player) {
		this.player = player;
	}
	
	@Override
	public void initialize() {

		setPrefWidth(WIDTH);
		setPrefHeight(HEIGHT);
		
		getStyleClass().addAll("player");
		
		rackImages = new ArrayList<DataImageView>();
		fortImages = new ArrayList<DataImageView>();
		
		addRackImages();
		addPlayerNameText();
		addNumGoldText();
		addPlayerControlMarker();
		addFortsVBox();
		
		update();
		
	}

	public Player getPlayer() {
		return player;
	}
	
	public List<DataImageView> getRackImageViews() {
		return rackImages;
	}
	
	public List<DataImageView> getFortImageViews() {
		return fortImages;
	}
	
	public void setFortThings(List<Fort> forts) {

		DataImageView.clear(fortImages, true);
		
		// TASK - fix this (by removing Fort from the view)
		if (forts.size() > fortImages.size()) {
			return;
		}
		
		for (int i=0; i<forts.size(); ++i) {
			
			Fort fort = forts.get(i);
			
			if (fort.isPlaced()) {
				continue;
			}
			
			fortImages.get(i).setImage(fort.getImage());
			fortImages.get(i).setData(fort);
			
		}
		
	}
	
	public void setRackThings(List<Thing> things) {
		
		DataImageView.clear(rackImages, true);	
		
		for (int i=0; i<things.size(); ++i) {
			
			Thing thing = things.get(i);
			
			rackImages.get(i).setImage(thing.getImage());
			rackImages.get(i).setData(thing);
			
		}
		
	}
	
	public void setControlMarkerImage(Image image) {
		controlMarkerImage.setImage(image);
	}
	
	public void setPlayerName(String name) {
		playerNameText.setText(name);
	}
	
	public void setNumGoldText(int numGold) {
		numGoldText.setText("gold: " + numGold);
	}
	
	public void setActive(boolean active) {
		
		if (active) {
			getStyleClass().add("activePlayer");
		} else {
			getStyleClass().remove("activePlayer");
		}
		
	}
	
	public void clearSelectedRackImages() {
		
		for (DataImageView imgView : rackImages) {
			imgView.setSelected(false);
		}
		
	}
	
	private void update() {
		
		// TODO
//		if (PlayerManager.getInstance().getActivePlayer() == player) {
//			setActive(true);
//		}
		
		setNumGoldText(player.getNumGold());
		setPlayerName(player.getName());
		setControlMarkerImage(player.getControlMarker());
		setRackThings(player.getRack().getThings());
		setFortThings(player.getForts());
		
	}

	private void addFortsVBox() {
		
		fortsVBox = VBoxBuilder.create().spacing(4).layoutX(FORTS_X).layoutY(FORTS_Y).build();	
		
		// TASK - figure out how to add event handlers dynamically		
		DataImageView test = new DataImageView(true, RACK_ITEM_WIDTH);
		fortImages.add(test);
		fortsVBox.getChildren().add(test);
		
		getChildren().add(fortsVBox);
		
	}
	
	private void addRackImages() {
		
		// Add the actual images of the rack
		ImageView topRack = getRackImageView(INITIAL_RACK_X, INITIAL_RACK_Y);
		ImageView bottomRack = getRackImageView(INITIAL_RACK_X, INITIAL_RACK_Y + topRack.fitHeightProperty().get() + 50);
		
		getChildren().addAll(topRack, bottomRack);
		
		// Create placeholder images for the items on the rack
		for (int i=0; i<10; ++i) {
			
			DataImageView imgView = new DataImageView(true, RACK_ITEM_WIDTH);
			
			// Compute the coordinates
			final int x = INITIAL_RACK_ITEM_X + (i%5)*RACK_ITEM_WIDTH + (i%5)*RACK_ITEM_GAP;
			final int y = (i < 5 ? INITIAL_RACK_Y : INITIAL_RACK_Y + 50);
			imgView.relocate(x, y);
			
			rackImages.add(imgView);
			
			getChildren().add(imgView);
			
		}
		
	}

	private void addNumGoldText() {

		numGoldText = new Text();
		numGoldText.setFont(Font.font("Lucida Sans", 16));
		numGoldText.setFill(Color.GOLD);
		numGoldText.setLayoutX(340);
		numGoldText.setLayoutY(30);
		
		getChildren().add(numGoldText);
		
	}
	
	private void addPlayerNameText() {
		
		playerNameText = new Text();
		playerNameText.setFont(Font.font("Lucida Sans", 20));
		playerNameText.setFill(Color.WHITE);
		playerNameText.setLayoutX(70);
		playerNameText.setLayoutY(35);
		
		getChildren().add(playerNameText);
		
	}
	
	private void addPlayerControlMarker() {
		
		controlMarkerImage = new ImageView();
		controlMarkerImage.setPreserveRatio(true);
		controlMarkerImage.setCache(true);
		controlMarkerImage.setFitWidth(30);
		controlMarkerImage.setX(30);
		controlMarkerImage.setY(15);
		
		getChildren().add(controlMarkerImage);
		
	}
	
	private ImageView getRackImageView(double x, double y) {
		
		ImageView imageView = new ImageView(rackImg);
		imageView.setPreserveRatio(true);
		imageView.setCache(true);
		imageView.setFitWidth(290);
		imageView.setX(x);
		imageView.setY(y);
		
		return imageView;
		
	}

}
