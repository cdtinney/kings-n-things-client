package com.kingsandthings.client.game;

import java.beans.PropertyChangeEvent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.phase.InitialRecruitmentPhase;
import com.kingsandthings.common.model.phase.Phase;
import com.kingsandthings.common.model.phase.PhaseManager;
import com.kingsandthings.common.model.phase.ThingRecruitmentPhase;

public class GameActionView extends VBox implements Updatable {

	// Model
	private Game game;
	
	public GameActionView(Game game) {
		this.game = game;
	}
	
	public void initialize() {
		
		setPrefWidth(235);
		getStyleClass().addAll("pane", "board");
		
		setAlignment(Pos.TOP_CENTER);
		setSpacing(10);
		
		addCup();
		addDraw();
		addRecruitmentActions();
		addPhaseActions();
		
		addListeners();
		
	}

	@Override
	public void update(Game game) {
		this.game = game;
	}
	
	@SuppressWarnings("unchecked")
	public Integer getNumPaidSelected() {
		ComboBox<Integer>  list = (ComboBox<Integer>) lookup("#numPaidRecruits");
		return list.getSelectionModel().getSelectedItem();
	}
	
	@SuppressWarnings("unchecked")
	public void resetNumPaidList() {
		ComboBox<Integer>  list = (ComboBox<Integer>) lookup("#numPaidRecruits");
		list.getSelectionModel().select(0);
	}
	
	public void enableDiceButton(boolean enable) {
		lookup("#rollDice").setDisable(enable);
	}
	
	@SuppressWarnings("unused")
	private void onPhaseChanged(PropertyChangeEvent event) {
		
		Phase newPhase = (Phase) event.getNewValue();
		if (newPhase == null) {
			setPhaseName("None");
			return;
		}
		
		Class<? extends Phase> clazz = newPhase.getClass();
		lookup("#drawThing").setDisable(clazz != ThingRecruitmentPhase.class && clazz != InitialRecruitmentPhase.class);
		lookup("#numPaidRecruits").setDisable(clazz != ThingRecruitmentPhase.class);
		
		setPhaseName(newPhase.getName());
		
	}
	
	private void setPhaseName(String name) {
		Label label = (Label) lookup("#phaseName");
		label.setText("Current Phase: " + name);
	}
	
	private void addCup() {
		
		ImageView imgView = new ImageView(new Image("/images/extra/cup.png"));
		imgView.setPreserveRatio(true);
		imgView.setFitWidth(200);
		
		getChildren().addAll(imgView);
		
	}

	private void addDraw() {
		
		Button drawThingButton = new Button("Draw Things");
		drawThingButton.setId("drawThing");
		drawThingButton.getStyleClass().addAll("nofocus");
		drawThingButton.setDisable(true);
		
		getChildren().addAll(drawThingButton);
		
	}
	
	private void addRecruitmentActions() {
		
		HBox thingRecruitmentBox = new HBox(5);
		thingRecruitmentBox.setAlignment(Pos.CENTER);

		Label numPaid = new Label("Paid Recruits (5g ea): ");
		numPaid.setFont(Font.font("Lucida Sans", 12));
		numPaid.setTextFill(Color.WHITE);
		
		ObservableList<Integer> numPaidRecruits = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5);
		ComboBox<Integer> comboBox = new ComboBox<Integer>(numPaidRecruits);
		comboBox.setId("numPaidRecruits");
		comboBox.getStyleClass().addAll("nofocus");
		comboBox.getSelectionModel().select(0);
		comboBox.setDisable(true);
		
		thingRecruitmentBox.getChildren().addAll(numPaid, comboBox);
		
		getChildren().add(thingRecruitmentBox);
		
	}
	
	private void addPhaseActions() {
		
		Label phaseName = new Label("Current Phase: none");
		phaseName.setFont(Font.font("Lucida Sans", 12));
		phaseName.setTextFill(Color.WHITE);
		phaseName.setId("phaseName");
		phaseName.setPrefWidth(235);
		phaseName.setWrapText(true);
		
		VBox.setMargin(phaseName, new Insets(100, 0, 0, 15));
		
		HBox buttons = new HBox(5);
		buttons.setAlignment(Pos.CENTER);
		
		Button endTurnButton = new Button("End Turn");
		endTurnButton.setId("endTurn");
		endTurnButton.getStyleClass().add("nofocus");
		
		buttons.getChildren().addAll(endTurnButton);
		
		getChildren().addAll(phaseName, buttons);
		
		
	}
	
	private void addListeners() {
		
		PropertyChangeDispatcher.getInstance().addListener(PhaseManager.class, "currentPhase", this, "onPhaseChanged");
		
	}

}
