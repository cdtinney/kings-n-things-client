package com.kingsandthings.game;

import java.util.List;
import java.util.logging.Logger;

import javafx.event.Event;

import com.kingsandthings.Controller;
import com.kingsandthings.model.Game;
import com.kingsandthings.model.Player;
import com.kingsandthings.model.phase.Phase;

public class GameActionController extends Controller {

	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(GameActionController.class.getName());

	// Model
	private Game game;
	
	// View
	private GameActionView view;
	
	public void initialize(Game game) {
		
		this.game = game;
		
		view = new GameActionView(game);
		view.initialize();
		
		setupHandlers();
		
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
		Player player = game.getActivePlayer();
		
		if (!phase.getStep().equals("Draw_Things")) {
			return;
		}
		
		if (phase.getName().equals("Thing Recruitment")) {
			int numPaid = view.getNumPaidSelected();
			
			boolean success = game.getCup().recruitThings(player, numPaid);
			if (success) {
				game.getPhaseManager().endPlayerTurn();
			}
			
			view.resetNumPaidList();
			
			return;
		}
		
//		List<Thing> things = game.getCup().drawThings(10);
//		game.addInitialThingsToPlayer(things, player);
	
		view.toggleThingList();
	}
	
	@SuppressWarnings("unused")
	private void handleEndTurnButton(Event event) {
		game.getPhaseManager().endPlayerTurn();		
	}
	
}
