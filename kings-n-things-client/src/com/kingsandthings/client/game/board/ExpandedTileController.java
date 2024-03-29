package com.kingsandthings.client.game.board;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.event.Event;

import com.kingsandthings.common.controller.Controller;
import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.common.util.DataImageView;

public class ExpandedTileController extends Controller {
	
	private static Logger LOGGER = Logger.getLogger(ExpandedTileController.class.getName());

	// Model
	private List<Thing> selectedThings;
	
	// View
	private ExpandedTileView view;
	
	public void initialize(Game game) {
		
		view = new ExpandedTileView(game);
		
		view.setPlayers(game.getPlayerManager().getPlayers());
		view.initialize();
		view.setVisible(false);
		
		selectedThings = new ArrayList<Thing>();
		
		addEventHandler(view, "close", "setOnMouseClicked", "onClose");
		
		for (DataImageView imgView : view.getImageViews()) {
			addEventHandler(imgView, "setOnMouseClicked", "handleThingImageClicked");
		}
		
	}
	
	public ExpandedTileView getView() {
		return view;
	}
	
	public List<Thing> getSelectedThings() {
		return selectedThings;
	}
	
	public void show(Tile tile, String playerName) {
		
		selectedThings.clear();

		view.clear();
		view.setTile(tile);
		view.updatePlayerThings(playerName);
		view.setVisible(true);
		
	}
	
	public void hideView() {
		view.clear();
	}
	
	@SuppressWarnings("unused")
	private void handleThingImageClicked(Event event) {
			
		DataImageView imageView = (DataImageView) event.getSource();
		Thing thing = (Thing) imageView.getData();
		
		if (selectedThings.contains(thing)) {
			selectedThings.remove(thing);
			imageView.setSelected(false);
			
		} else {
			
			Creature c = (Creature) thing;
			if (c.getMovementEnded()) {
				LOGGER.log(LogLevel.STATUS, "Creature can no longer move.");
				return;
			} else {
				LOGGER.log(LogLevel.STATUS, "Creature has " + c.getMovesLeft() + " moves left.");
			}
			
			selectedThings.add(thing);
			imageView.setSelected(true);
			
		}
		
	}
	
	@SuppressWarnings("unused")
	private void onClose(Event event) {
		view.clear();
		selectedThings.clear();
	}

}
