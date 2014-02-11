package com.kingsandthings.model.phase;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.kingsandthings.game.events.PropertyChangeDispatcher;

public class PhaseManager {
	
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(PhaseManager.class.getName());
	
	private static PhaseManager INSTANCE = null;
	
	private List<Phase> phases;
	private int currentPhaseNumber = 0;
	
	private PhaseManager() {
		
		phases = new ArrayList<Phase>();
		
		// Add the phases (in order)
//		phases.add(new StartingKingdomsPhase());
//		phases.add(new TowerPlacementPhase());
//		phases.add(new GoldCollectionPhase());
		phases.add(new InitialPlacementPhase());
		phases.add(new MovementPhase());
		
	}
	
	public static PhaseManager getInstance() {
		
		if (INSTANCE == null) {
			INSTANCE = new PhaseManager();
		}
		
		return INSTANCE;
		
	}
	
	public void beginPhases() {
		phases.get(0).begin();
	}
	
	public void endPlayerTurn() {
		getCurrentPhase().nextTurn();
	}
	
	public Phase getCurrentPhase() {
		return phases.get(currentPhaseNumber);
	}
	
	public void nextPhase() {
		
		Phase oldPhase = getCurrentPhase();
		
		currentPhaseNumber = (currentPhaseNumber + 1) % phases.size();
		
		Phase newPhase = phases.get(currentPhaseNumber);
		
		notifyPhaseChange(oldPhase, newPhase);
		
		newPhase.begin();
		
	}
	
	private void notifyPhaseChange(Phase oldPhase, Phase newPhase) {
		PropertyChangeDispatcher.getInstance().notify(this, "currentPhase", oldPhase, newPhase);
	}

}