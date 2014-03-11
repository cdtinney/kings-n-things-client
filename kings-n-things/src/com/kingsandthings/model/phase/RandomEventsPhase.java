package com.kingsandthings.model.phase;

import com.kingsandthings.model.Game;

public class RandomEventsPhase extends Phase {

	public RandomEventsPhase(Game game) {
		super(game, "Random Events (SKIP)", false, false, 1, false);
	}
	
}
