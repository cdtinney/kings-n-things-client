package com.kingsandthings.client.game;

import java.util.logging.Logger;

import javafx.event.Event;

import com.kingsandthings.common.controller.Controller;
import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.IGame;
import com.kingsandthings.common.model.phase.InitialRecruitmentPhase;
import com.kingsandthings.common.model.phase.Phase;
import com.kingsandthings.common.model.phase.ThingRecruitmentPhase;
import com.kingsandthings.common.network.GameClient;

public class GameActionController extends Controller implements Updatable {

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
		
	}
	
	@SuppressWarnings("unused")
	private void handleDrawThingButton(Event event) {
		
		Phase phase = game.getPhaseManager().getCurrentPhase();
		
		if (!gameClient.activePlayer()) {
			LOGGER.log(LogLevel.STATUS, "It is not your turn to draw.");
			return;
		}
		
		String step = phase.getStep();
		if (!step.equals(InitialRecruitmentPhase.DRAW) || !step.equals(ThingRecruitmentPhase.DRAW)) {
			LOGGER.log(LogLevel.STATUS, "You cannot draw Things in the current step.");
			return;
		}
		
		if (phase.getClass() == InitialRecruitmentPhase.class) {
			
			final IGame serverGame = gameClient.requestGame();
			serverGame.recruitThingsInitial();
			
		} else if (phase.getClass() == ThingRecruitmentPhase.class) {
			
			int numPaid = view.getNumPaidSelected();
			view.resetNumPaidList();
			
			final IGame serverGame = gameClient.requestGame();
			serverGame.recruitThings(numPaid);
			
		}
		
	}
	
	@SuppressWarnings("unused")
	private void handleEndTurnButton(Event event) {

		final IGame serverGame = gameClient.requestGame();
		serverGame.endTurn(gameClient.getName());
		
	}
	
}
