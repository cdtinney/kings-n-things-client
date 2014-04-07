package com.kingsandthings.client.game.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.event.Event;
import javafx.event.EventHandler;

import com.kingsandthings.client.game.Updatable;
import com.kingsandthings.common.controller.Controller;
import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.IGame;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.combat.Battle.Step;
import com.kingsandthings.common.model.phase.CombatPhase;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.common.network.GameClient;
import com.kingsandthings.common.util.DataImageView;

public class CombatController extends Controller implements Updatable {
	
	private static Logger LOGGER = Logger.getLogger(ExpandedTileController.class.getName());
	
	// Networking
	private GameClient gameClient;

	// Model
	private List<Thing> selectedThings;
	private Game game;
	private CombatPhase combatPhase;
	
	private Map<Thing, Integer> creatureHits;
	private int totalHits = 0;
	
	// View
	private CombatView view;
	
	public void initialize(Game game, GameClient gameClient) {
		this.gameClient = gameClient;
		
		initialize(game);
		
		view.setLocalName(gameClient.getName());

	}
	
	public void initialize(Game game) {
		this.game = game;
		
		selectedThings = new ArrayList<Thing>();
		creatureHits = new HashMap<Thing, Integer>();
		
		view = new CombatView(game);
		view.initialize();
		view.setVisible(false);
		
		addEventHandlers();
		
	}
	
	public void update(Game game) {
		this.game = game;
		
		view.update(game);
	}
	
	public CombatView getView() {
		return view;
	}
	
	public void start(Tile tile) {
		
		selectedThings.clear();
		
		IGame remoteGame = gameClient.requestGame();
		remoteGame.resolveCombat(tile);
		
	}
	
	@SuppressWarnings("unused")
	private void handleThingImageClicked(Event event) {
			
		DataImageView imageView = (DataImageView) event.getSource();
		Thing thing = (Thing) imageView.getData();
		
		CombatPhase combatPhase = (CombatPhase) game.getPhaseManager().getCurrentPhase();
		if (combatPhase == null) {
			return;
		}
		
		Step step = combatPhase.getCurrentBattle().getCurrentStep();
		if (step != Step.APPLY_HITS) {
			return;
		}
		
		Creature creature = (Creature) thing;
		
		if (creatureHits.containsKey(creature)) {
			
			int currentHits = creatureHits.get(creature);
			currentHits = currentHits == 0 ? 1 : 0; 		//(currentHits + 1) % (combatValue + 1);
			creatureHits.put(creature, currentHits);
			
			view.setThingImageHits(imageView, currentHits);
			
		} else {
			
			creatureHits.put(creature, 1);
			view.setThingImageHits(imageView, 1);
			
		}
		
	}
	
	@SuppressWarnings("unused")
	private void handleDiceButtonClicked(Event event) {
		
		IGame remoteGame = gameClient.requestGame();
		remoteGame.rollCombatDice(gameClient.getName());
		
	}
	
	@SuppressWarnings("unused")
	private void handleApplyHitsButtonClicked(Event event) {
		LOGGER.log(LogLevel.DEBUG, "Apply hits button clicked");
	}
	
	@SuppressWarnings("unused")
	private void handleRetreatButtonClicked(Event event) {
		LOGGER.log(LogLevel.DEBUG, "Retreat button clicked");
	}
	
	private void addEventHandlers() {

		for (DataImageView imgView : view.getLocalImgViews()) {
			addEventHandler(imgView, "setOnMouseClicked", "handleThingImageClicked");
		}
		
		addEventHandler(view.getDiceButton(), "setOnMouseClicked", "handleDiceButtonClicked");
		addEventHandler(view.getHitsButton(), "setOnMouseClicked", "handleApplyHitsButtonClicked");
		addEventHandler(view.getRetreatButton(), "setOnMouseClicked", "handleRetreatButtonClicked");
		
	}

}
