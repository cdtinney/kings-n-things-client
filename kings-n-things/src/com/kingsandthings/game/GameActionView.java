package com.kingsandthings.game;

import java.beans.PropertyChangeEvent;
import java.util.List;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import com.kingsandthings.game.events.NotificationDispatcher;
import com.kingsandthings.game.events.PropertyChangeDispatcher;
import com.kingsandthings.model.Game;
import com.kingsandthings.model.phase.InitialPlacementPhase;
import com.kingsandthings.model.phase.Phase;
import com.kingsandthings.model.phase.PhaseManager;
import com.kingsandthings.model.phase.ThingRecruitmentPhase;

public class GameActionView extends VBox implements InitializableView {

	// Model
	private Game game;
	
	public GameActionView(Game game) {
		this.game = game;
	}
	
	@Override
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
		
		bindElements();
		
	}
	
	@SuppressWarnings("unchecked")
	public void toggleThingList() {

		ListView<String>  list = (ListView<String>) lookup("#thingList");
		Node selectButton = lookup("#selectThings");
		
		// If its already visible, clear selection and hide
		if (list.visibleProperty().get()) {
			
			list.getSelectionModel().clearSelection();
			
			list.setVisible(false);
			selectButton.setVisible(false);
			
		} else {
			
			// Update list of Things
			list.setItems(FXCollections.observableArrayList(game.getCup().getThingNames()));
			
			// TASK - Demo only (selectable list of things)
			if (game.getPhaseManager().getCurrentPhase().getStep().equals("Draw_Things")) {
				list.setVisible(true);
				selectButton.setVisible(true);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getSelectedThings() {
		ListView<String>  list = (ListView<String>) lookup("#thingList");
		return list.getSelectionModel().getSelectedIndices();
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
	
	private void addListeners() {
		PropertyChangeDispatcher.getInstance().addListener(PhaseManager.class, "currentPhase", this, "onPhaseChanged");
		
		NotificationDispatcher.getInstance().addListener(ThingRecruitmentPhase.class, Phase.Notification.STEP, this, "onRecruitmentPhaseStep");
		NotificationDispatcher.getInstance().addListener(InitialPlacementPhase.class, Phase.Notification.STEP, this, "onInitialPlacementStep");
	}
	
	private void bindElements() {
		
		// Disable draw thing button if active phase is not Thing Recruitment or Initial Placement
		BooleanBinding binding = (ThingRecruitmentPhase.getActive().or(InitialPlacementPhase.getActive())).not();
		lookup("#drawThing").disableProperty().bind(binding);
		
		// Disable num paid combobox if active phase is not Thing Recruitment
		lookup("#numPaidRecruits").disableProperty().bind(ThingRecruitmentPhase.getActive().not());
		
	}
	
	@SuppressWarnings("unused")
	private void onInitialPlacementStep() {
		lookup("#endTurn").setDisable(false);
	}
	
	@SuppressWarnings("unused")
	private void onRecruitmentPhaseStep() {
		lookup("#endTurn").setDisable(false);
	}
	
	@SuppressWarnings("unused")
	private void onPhaseChanged(PropertyChangeEvent event) {
		
		Phase newPhase = (Phase) event.getNewValue();
		if (newPhase == null) {
			lookup("#endTurn").setDisable(true);
			setPhaseName("None");
			return;
		}
		
		if (!newPhase.getStep().equals("Draw_Things")) {
			lookup("#endTurn").setDisable(newPhase.isMandatory() && newPhase.playerInteractionRequired());
		} else {
			lookup("#endTurn").setDisable(true);
		}
		
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
		
		ListView<String> list = new ListView<String>();
		list.setId("thingList");
		list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		list.setMaxWidth(205);
		list.setPrefHeight(210);
		
		list.managedProperty().bind(list.visibleProperty());
		list.setVisible(false);
		
		Button selectThings = new Button("Select Things");
		selectThings.setId("selectThings");
		selectThings.getStyleClass().addAll("nofocus");

		selectThings.managedProperty().bind(selectThings.visibleProperty());
		selectThings.setVisible(false);
		
		getChildren().addAll(drawThingButton, list, selectThings);
		
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
		
		Label phaseName = new Label();
		phaseName.setFont(Font.font("Lucida Sans", 12));
		phaseName.setTextFill(Color.WHITE);
		phaseName.setId("phaseName");
		phaseName.setPrefWidth(235);
		phaseName.setWrapText(true);
		
		VBox.setMargin(phaseName, new Insets(100, 0, 0, 15));
		
		String currentPhaseName = game.getPhaseManager().getCurrentPhase().getName();
		phaseName.setText("Current Phase: " + currentPhaseName);
		
		HBox buttons = new HBox(5);
		buttons.setAlignment(Pos.CENTER);
		
		Button endTurnButton = new Button("End Turn");
		endTurnButton.setId("endTurn");
		endTurnButton.getStyleClass().add("nofocus");
		
		endTurnButton.setDisable(game.getPhaseManager().getCurrentPhase().isMandatory());
		
		buttons.getChildren().addAll(endTurnButton);
		
		getChildren().addAll(phaseName, buttons);
		
		
	}

}
