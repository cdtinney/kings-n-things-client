package com.kingsandthings.client.game.board;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

import com.kingsandthings.client.game.Updatable;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.combat.Battle;
import com.kingsandthings.common.model.combat.Battle.Step;
import com.kingsandthings.common.model.phase.CombatPhase;
import com.kingsandthings.common.model.phase.Phase;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.common.util.DataImageView;

public class CombatView extends VBox implements Updatable {
	
	private static Logger LOGGER = Logger.getLogger(CombatView.class.getName());

	private static final int WIDTH = 500;
	private static final int THING_WIDTH = 60;

	// Model
	private Game game;
	private Battle battle;	
	private String localName;	// unique name of the local player
	
	// View elements
	private List<GridPane> grids;
	private List<DataImageView> attackerThingImages;
	private List<DataImageView> defenderThingImages;
	
	// Action buttons
	private Button diceButton;
	private Button hitsButton;
	private Button retreatButton;	
	private Button skipRetreatButton;	
	
	public CombatView(Game game) {
		this.game = game;
		
		attackerThingImages = new ArrayList<DataImageView>();
		defenderThingImages = new ArrayList<DataImageView>();
		grids = new ArrayList<GridPane>();
	}
	
	public void initialize() {
		
		setPrefWidth(WIDTH);
		setSpacing(20);
		
		setLayoutX(644 / 2 - 250);
		setLayoutY(50);
		
		setStyle("-fx-opacity: 0.9; -fx-background-color: transparent, derive(#1d1d1d,20%);");
		
		addStepText();
		addPlayerGrid(true);
		addPlayerGrid(false);
		
		addActionButtons();
		
	}
	
	public void update(Game game) {
		this.game = game;
		
		Phase phase = game.getPhaseManager().getCurrentPhase();
		if (!(phase instanceof CombatPhase)) {
			return;
		}
		
		CombatPhase combatPhase = (CombatPhase) phase;
		if (combatPhase.getCurrentBattle() == null) {
			return;
		}
		
		battle = ((CombatPhase) game.getPhaseManager().getCurrentPhase()).getCurrentBattle();
		
		Player attacker = battle.getAttacker();
		Player defender = battle.getDefender();
		if (attacker.getName().equals(localName) || defender.getName().equals(localName)) {
			update();
			
			setVisible(!battle.getResolved());
		}
		
	}
	
	public void update() {
		
		updateStepText();
		updatePlayerNames();
		updateThingImages();
		updateActionButtons();
		
	}
	
	public boolean ownImage(DataImageView imgView) {
		
		if (localName.equals(battle.getDefender().getName())) {
			return defenderThingImages.contains(imgView);
		}
		
		if (localName.equals(battle.getAttacker().getName())) {
			return attackerThingImages.contains(imgView);
		}
		
		return false;
		
	}
	
	public Button getDiceButton() 			{ return diceButton; 	}
	public Button getHitsButton()	 		{ return hitsButton;	}
	public Button getRetreatButton() 		{ return retreatButton; }
	public Button getSkipRetreatButton() 	{ return skipRetreatButton; }
	
	public List<DataImageView> getLocalImgViews() {

		List<DataImageView> imageViews = new ArrayList<DataImageView>();
		imageViews.addAll(attackerThingImages);
		imageViews.addAll(defenderThingImages);
		
		return imageViews;
		
	}
	
	public void setLocalName(String name) {
		localName = name;
	}
	
	public void setThingImageHits(DataImageView imgView, int hits) {
		
		Label label = imgView.getLabel();
		label.setText(String.valueOf(hits));
		
	}
	
	private void updateStepText() {
		
		String text = battle == null ? "STEP: " : ("STEP: " + battle.getCurrentStep().toString());
		((Text) lookup("#stepText")).setText(text);
		
	}
	
	private void updatePlayerNames() {
		
		String defenderName = battle.getDefender().getName();
		String attackerName = battle.getAttacker().getName();

		((Text) lookup("#defenderText")).setText("Defender: " + (defenderName == null? "" : defenderName));
		((Text) lookup("#attackerText")).setText("Attacker: " + (attackerName == null? "" : attackerName));
		
		((Text) lookup("#defenderText")).setUnderline(battle.isCurrentPlayer(defenderName));
		((Text) lookup("#attackerText")).setUnderline(battle.isCurrentPlayer(attackerName));
		
	}
	
	private void updateThingImages() {
		
		if (battle.getDefender() == null || battle.getDefender() == null) {
			LOGGER.warning("Attacker and defender must be set before updating the combat creature images.");
			return;
		}
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				DataImageView.clear(attackerThingImages, true);
				DataImageView.clear(defenderThingImages, true);	
				
				setCreatureImages(battle.getDefenderCreatures(), battle.getElimDefenderThings(), defenderThingImages);
				setCreatureImages(battle.getAttackerCreatures(), battle.getElimAttackerThings(), attackerThingImages);
			}
			
		});	
		
	}
	
	private void updateActionButtons() {
		
		Step step = battle.getCurrentStep();
		
		diceButton.setDisable(step != Step.ROLL_DICE);
		hitsButton.setDisable(step != Step.APPLY_HITS);
		retreatButton.setDisable(step != Step.RETREAT);
		skipRetreatButton.setDisable(step != Step.RETREAT);
		
	}
	
	private void setCreatureImages(List<Creature> creatures, List<Thing> elimThings, List<DataImageView> imgViews) {
		
		for (int i=0; i<creatures.size(); ++i) {
			
			DataImageView imgView = imgViews.get(i);
			Thing thing = creatures.get(i);
			setThingImage(imgView, thing, false);
			
		}
		
		for (int i=0; i<elimThings.size(); ++i) {
			
			int adjustedIndex = i + creatures.size();
			
			DataImageView imgView = imgViews.get(adjustedIndex);
			Thing thing = elimThings.get(i);
			setThingImage(imgView, thing, true);
			
		}
		
	}
	
	private void setThingImage(DataImageView imgView, Thing thing, boolean elim) {

		imgView.setImage(elim ? Thing.getBackImage() : thing.getImage());
		imgView.setData(thing);
		
		imgView.setVisible(true);
		imgView.getParent().setVisible(true);
		
	}
	
	private void addStepText() {
		
		Text stepText = new Text("STEP: ");
		stepText.setFont(Font.font("Lucida Sans", 20));
		stepText.setFill(Color.WHITE);
		stepText.setId("stepText");
		setMargin(stepText, new Insets(10, 0, 0, 10));
		
		getChildren().add(stepText);
		
	}
	
	private void addActionButtons() {
		
		HBox actionsBox = new HBox();
		setMargin(actionsBox, new Insets(10, 10, 10, 10));
		actionsBox.setSpacing(10);
		
		diceButton = new Button("Roll Dice");
		diceButton.getStyleClass().add("nofocus");
		diceButton.setId("rollDice");
		
		hitsButton = new Button("Apply Hits");
		hitsButton.getStyleClass().add("nofocus");
		hitsButton.setId("applyHits");
		
		retreatButton = new Button("Retreat");
		retreatButton.getStyleClass().add("nofocus");
		retreatButton.setId("retreat");
		
		skipRetreatButton = new Button("Skip Retreat");
		skipRetreatButton.getStyleClass().add("nofocus");
		skipRetreatButton.setId("skipRetreat");
		
		actionsBox.getChildren().addAll(diceButton, hitsButton, retreatButton, skipRetreatButton);
		
		getChildren().add(actionsBox);
		
	}
	
	private void addPlayerGrid(boolean attacker) {
		
		GridPane grid = GridPaneBuilder.create().hgap(10).vgap(5).prefHeight(150).visible(true).build();
		grid.managedProperty().bind(grid.visibleProperty());
		setMargin(grid, new Insets(0, 10, 0, 10));
		
		Text text = TextBuilder.create().text(attacker ? "Attacker" : "Defender").font(Font.font("Lucida Sans", 20)).fill(Color.WHITE).build();
		text.setId(attacker? "attackerText" : "defenderText");
		text.managedProperty().bind(text.visibleProperty());
		text.visibleProperty().bind(grid.visibleProperty());
		setMargin(text, new Insets(5, 0, 0, 10));
		getChildren().add(text);
		
		for (int i=0; i<Tile.MAXIMUM_THINGS; ++i) {
			
			DataImageView imgView = new DataImageView(false, THING_WIDTH);
			imgView.managedProperty().bind(imgView.visibleProperty());
			imgView.setVisible(true);
			
			if (attacker) {
				attackerThingImages.add(imgView);
			} else {
				defenderThingImages.add(imgView);
			}
			
			Label label = new Label("", imgView);
			label.setContentDisplay(ContentDisplay.BOTTOM);
			label.setFont(Font.font("Lucida Sans", 14));
			label.setTextFill(Color.RED);
			
			addPropagationHandler(label, imgView);
			
			imgView.setLabel(label);
			
			grid.add(label, i%8, i >= 8 ? 1 : 0);
			
		}
		
		grids.add(grid);
		getChildren().add(grid);
		
	}
	
	private void addPropagationHandler(Label label, final DataImageView imgView) {
		
		label.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				
				if (event.getTarget().equals(imgView)) {
					event.consume();
					return;
				}
				
				imgView.fireEvent(event);
			}
		});
		
	}
	
}
