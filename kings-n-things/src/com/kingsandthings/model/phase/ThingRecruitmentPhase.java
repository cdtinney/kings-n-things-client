package com.kingsandthings.model.phase;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.kingsandthings.game.board.BoardView;
import com.kingsandthings.model.Game;

public class ThingRecruitmentPhase extends Phase {
	
	private final static BooleanProperty active = new SimpleBooleanProperty(false);

	public ThingRecruitmentPhase(Game game) {
		super(game, "Thing Recruitment", true, true, 2, false);
	}
	
	public static BooleanProperty getActive() {
		return active;
	}
	
	@Override
	public void begin() {
		super.begin();
		active.set(true);
		
		BoardView.setInstructionText("please recruit things");
		currentStep = "Draw_Things";
		
	}
	
	@Override
	public void nextStep() {
		notify(Notification.STEP);
		
		BoardView.setInstructionText("please place your Things");
		currentStep = "Thing_Placement";
		
	}
	
	@Override
	public void end() {
		super.end();
		active.set(false);
		
	}
	
}
