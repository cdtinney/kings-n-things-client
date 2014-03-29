package com.kingsandthings.client.game;

import java.util.List;
import java.util.logging.Logger;

import javafx.event.Event;

import com.kingsandthings.common.controller.Controller;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.IGame;
import com.kingsandthings.common.model.phase.InitialRecruitmentPhase;
import com.kingsandthings.common.model.phase.Phase;
import com.kingsandthings.common.model.phase.ThingRecruitmentPhase;
import com.kingsandthings.common.network.GameClient;

public class GameActionController extends Controller implements Updatable {

	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(GameActionController.class.getName());

	// Networking
	GameClient gameClient;
	
	// Model
	private Game game;
	
	// View
	private GameActionView view;
	
	public void initialize(Game game, GameClient gameClient) {
		this.gameClient = gameClient;
		
		initialize(game);
	}
	
	public void initialize(Game game) {
		this.game = game;
		
		view = new GameActionView(game);
		view.initialize();
		
		setupHandlers();
		
	}
	
	public void update(Game game) {
		this.game = game;
		
		view.update(game);	
	}
	
	public GameActionView getView() {
		return view;
	}
	
	private void setupHandlers() {
		
		addEventHandler(view, "endTurn", "setOnAction", "handleEndTurnButton");
		
		addEventHandler(view, "drawThing", "setOnAction", "handleDrawThingButton");
		addEventHandler(view, "selectThings", "setOnAction", "handleSelectThingsButton");
		
	}
	
	@SuppressWarnings({ "unused" })
	private void handleSelectThingsButton(Event event) {
		
		List<Integer> selected = view.getSelectedThings();
		
		// Add the selected things to the player's list of Things
		game.addThingIndicesToPlayer(selected, game.getActivePlayer());
		
		// Hide the list of Things
		view.toggleThingList();
		
	}
	
	@SuppressWarnings("unused")
	private void handleDrawThingButton(Event event) {
		
		Phase phase = game.getPhaseManager().getCurrentPhase();
		
		if (!gameClient.activePlayer()) {
			return;
		}
		
		if (phase.getClass() == InitialRecruitmentPhase.class) {
			
			final IGame serverGame = gameClient.requestGame();
			serverGame.recruitThingsInitial();
			
		} else if (phase.getClass() == ThingRecruitmentPhase.class) {
			
//			int numPaid = view.getNumPaidSelected();
//			
//			boolean success = game.getCup().recruitThings(player, numPaid);
//			if (success) {
//				game.getPhaseManager().endPlayerTurn();
//			}
//			
//			view.resetNumPaidList();
			
		}
		
	}
	
	@SuppressWarnings("unused")
	private void handleEndTurnButton(Event event) {
		game.getPhaseManager().endPlayerTurn();		
	}
	
}
