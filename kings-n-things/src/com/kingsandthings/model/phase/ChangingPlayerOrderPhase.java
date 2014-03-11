package com.kingsandthings.model.phase;

import com.kingsandthings.model.Game;

public class ChangingPlayerOrderPhase extends Phase {

	public ChangingPlayerOrderPhase(Game game) {
		super(game, "Changing Player Order Phase (SKIP)", false, false, 1, false);
	}
	
}
