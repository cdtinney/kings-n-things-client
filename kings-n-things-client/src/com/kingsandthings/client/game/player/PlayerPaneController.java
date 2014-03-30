package com.kingsandthings.client.game.player;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import com.kingsandthings.client.game.Updatable;
import com.kingsandthings.common.controller.Controller;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.PlayerManager;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.common.network.GameClient;
import com.kingsandthings.game.events.PropertyChangeDispatcher;
import com.kingsandthings.util.CustomDataFormat;
import com.kingsandthings.util.DataImageView;

public class PlayerPaneController extends Controller implements Updatable {
	
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(PlayerPaneController.class.getName());
	
	// Networking
	private GameClient gameClient;
	
	// Model
	private Game game;
	private List<Thing> selectedThings;
	
	// View
	private PlayerPane view;
	
	public void initialize(Game game, GameClient gameClient) {
		this.gameClient = gameClient;
		
		initialize(game);
	}
	
	public void initialize(Game game) {
		this.game = game;
		
		view = new PlayerPane(game.getPlayerManager().getPlayers());
		view.initialize();
		view.setLocalPlayer(gameClient.getName());
		
		selectedThings = new ArrayList<Thing>();
		
		addHandlers();
		addListeners();
		
	}
	
	public void update(Game game) {
		this.game = game;
		
		view.update(game);
	}
	
	public Node getView() {
		return view;
	}
	
	private void addHandlers() {
		
		for (final PlayerView playerView : view.getPlayerViews()) {
			
			// Don't add event handlers for views other than the local player
			if (!playerView.getLocal()) {
				continue;
			}
			
			for (DataImageView rackImage : playerView.getRackImageViews()) {
				
				addEventHandler(rackImage, "setOnMouseClicked", "handleRackImageClicked");
				addEventHandler(rackImage, "setOnDragDetected", "handleThingDragDetected");
				addEventHandler(rackImage, "setOnDragDone", "handleThingDragDone");
				
				rackImage.addEventFilter(Event.ANY, new EventHandler<Event> (){

					@Override
					public void handle(Event event) {
						
						if (event.getEventType() == MouseEvent.MOUSE_ENTERED || event.getEventType() == MouseEvent.MOUSE_EXITED) {
							return;
						}
						
						if (!gameClient.activePlayer()) {
							event.consume();
						}
						
						// TODO - consume depending on phases
//						if (!game.getPhaseManager().getCurrentPhase().getStep().equals("Thing_Placement")) {
//							event.consume();
//						}
						
					}
					
				});
				
			}
			
			for (DataImageView fortImage : playerView.getFortImageViews()) {
				
				addEventHandler(fortImage, "setOnDragDetected", "handleThingDragDetected");
				addEventHandler(fortImage, "setOnDragDone", "handleThingDragDone");
				
				fortImage.addEventFilter(Event.ANY, new EventHandler<Event>() {
					
					@Override
					public void handle(Event event) {
						
						// Allow drag done events to propagate. The active player may have changed
						// before the event was fired.
						if (event.getEventType() == DragEvent.DRAG_DONE) {
							return;
						}
						
						if (!gameClient.activePlayer()) {
							event.consume();
						}
						
					}
					
				});
				
			}
		}
		
	}
	
	@SuppressWarnings("unused")
	private void activePlayerChanged(PropertyChangeEvent event) {
		selectedThings.clear();
		view.clearSelectedImages();
	}
	
	@SuppressWarnings("unused")
	private void handleRackImageClicked(Event event) {
			
		DataImageView imageView = (DataImageView) event.getSource();
		Thing thing = (Thing) imageView.getData();
		
		if (selectedThings.contains(thing)) {
			selectedThings.remove(thing);
			imageView.setSelected(false);
			
		} else {
			selectedThings.add(thing);
			imageView.setSelected(true);
			
		}
		
	}
	
	@SuppressWarnings({ "unused", "deprecation" })
	private void handleThingDragDetected(Event event) {
		
		DataImageView imageView = (DataImageView) event.getSource();
		
		// Handle the case where the user simply wants to drag one Thing
		if (selectedThings.isEmpty()) {
			selectedThings.add((Thing) imageView.getData());
		}

		Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);
		ClipboardContent content = new ClipboardContent();
		
		content.put(CustomDataFormat.THINGS, selectedThings);
		
		List<String> imageUrls = new ArrayList<String>();
		for (Thing thing : selectedThings) {
			imageUrls.add(thing.getImage().impl_getUrl());
		}
		
		content.put(CustomDataFormat.IMAGES, imageUrls);
		db.setContent(content);
		
	}
	
	@SuppressWarnings("unused")
	private void handleThingDragDone(Event event) {
		
		DragEvent dragEvent = (DragEvent) event;
		selectedThings.clear();
		
	}
	
	private void addListeners() {
		PropertyChangeDispatcher.getInstance().addListener(PlayerManager.class, "activePlayer", this, "activePlayerChanged");
	}

}
