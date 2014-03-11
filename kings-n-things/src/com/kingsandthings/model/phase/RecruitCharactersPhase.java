package com.kingsandthings.model.phase;

import com.kingsandthings.model.Game;

public class RecruitCharactersPhase extends Phase {

	public RecruitCharactersPhase(Game game) {
		super(game, "Recruit Characters (SKIP)", false, false, 1, false);
	}

}
