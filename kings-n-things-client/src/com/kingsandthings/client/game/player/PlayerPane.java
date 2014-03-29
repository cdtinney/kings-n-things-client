package com.kingsandthings.client.game.player;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

import com.kingsandthings.client.game.Updatable;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.PlayerManager;
import com.kingsandthings.common.model.Rack;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.game.events.PropertyChangeDispatcher;

public class PlayerPane extends VBox implements Updatable {
	
	// View
	private List<PlayerView> playerViews;
	
	// Model
	private List<Player> players;
	
	public PlayerPane(List<Player> players) {
		this.players = players;
	}

	public void initialize() {
		
		getStyleClass().addAll("pane", "board");
		
		addPlayerViews();
		addListeners();
		
	}
	
	public void update(Game game) {
		
		players = game.getPlayerManager().getPlayers();
		
		for (Player player : players) {
			PlayerView view = getPlayerView(player);
			
			if (view != null) {
				view.updatePlayer(player);
			}
			
		}
		
	}
	
	public List<PlayerView> getPlayerViews() {
		return playerViews;
	}
	
	public void setLocalPlayer(String playerName) {
		
		for (Player player : players) {
			
			if (player.getName().equals(playerName)) {
				getPlayerView(player).setLocal();
			}
			
		}
		
	}
	
	public void clearSelectedImages() {
		
		for (PlayerView playerView : playerViews) {
			playerView.clearSelectedRackImages();
		}
		
	}
	
	private void addListeners() {
		
		// Player model
		PropertyChangeDispatcher.getInstance().addListener(Player.class, "numGold", this, "updatePlayerGold");
		PropertyChangeDispatcher.getInstance().addListener(Player.class, "forts", this, "updatePlayerForts");
		
		// Player manager
		PropertyChangeDispatcher.getInstance().addListener(PlayerManager.class, "activePlayer", this, "updateActivePlayer");
		
		// Rack model
		PropertyChangeDispatcher.getInstance().addListener(Rack.class, "things", this, "updatePlayerRack");
		
	}
	
	private void addPlayerViews() {

		playerViews = new ArrayList<PlayerView>();
		
		for (Player player : players) {
			
			PlayerView playerView = new PlayerView(player);
			playerView.initialize();

			setMargin(playerView, new Insets(30, 10, 0, 10));
			getChildren().add(playerView);
			
			playerViews.add(playerView);
			
		}
		
	}
	
	private PlayerView getPlayerView(Player player) {
		
		for (PlayerView playerView : playerViews) {
			
			if (playerView.getPlayer().equals(player)) {
				return playerView;
			}
		}
		
		return null;
		
	}
	
	@SuppressWarnings("unused")
	private void updateActivePlayer(PropertyChangeEvent event) {
		
		Player oldPlayer = (Player) event.getOldValue();
		Player newPlayer = (Player) event.getNewValue();
		
		if (oldPlayer != null) {
			getPlayerView(oldPlayer).setActive(false);
		}
		
		if (newPlayer != null) {
			getPlayerView(newPlayer).setActive(true);
		}
		
	}
	
	@SuppressWarnings("unused")
	private void updatePlayerGold(PropertyChangeEvent event) {
		
		Player player = (Player) event.getSource();
		int numGold = player.getNumGold();
		
		getPlayerView(player).setNumGoldText(numGold);
		
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	private void updatePlayerRack(PropertyChangeEvent event) {
		
		Rack rack = (Rack) event.getSource();
		
		List<Thing> newThings = (List<Thing>) event.getNewValue();
		
		getPlayerView(rack.getOwner()).setRackThings(newThings);
		
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	private void updatePlayerForts(PropertyChangeEvent event) {

		Player player = (Player) event.getSource();
		
		List<Fort> newForts = (List<Fort>) event.getNewValue();
		
		getPlayerView(player).setFortThings(newForts);
		
	}
	
}
