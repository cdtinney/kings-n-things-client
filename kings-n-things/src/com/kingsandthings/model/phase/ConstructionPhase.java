package com.kingsandthings.model.phase;

import com.kingsandthings.model.Game;

public class ConstructionPhase extends Phase {
	
	public ConstructionPhase(Game game) {
		super(game, "Construction Phase (SKIP)", false, false, 1, false);
	}

}
