package com.kingsandthings.model.phase;

import com.kingsandthings.game.board.BoardView;
import com.kingsandthings.model.Game;
import com.kingsandthings.model.Player;
import com.kingsandthings.model.things.Fort;

public class TowerPlacementPhase extends Phase {

	public TowerPlacementPhase(Game game) {
		super(game, "Tower Placement", true, true, 1, true);
	}
	
	@Override
	public void begin() {
		super.begin();
		
		BoardView.setInstructionText("please place a tower");
		
		for (Player player: game.getPlayerManager().getPlayers()) {
			
			// Give each player a tower
			player.addFort(Fort.getTower());
		}
		
	}

}
