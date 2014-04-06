package com.kingsandthings.client.game.board;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.combat.Battle;
import com.kingsandthings.common.model.phase.CombatPhase;
import com.kingsandthings.common.model.phase.Phase;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.common.util.DataImageView;

public class CombatView extends VBox {
	
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
		}
		
	}
	
	public void update() {
		
		clear();
		
		updateNames();
		updateThingImages();
		
		setVisible(true);
		
	}
	
	public void clear() {
		
		DataImageView.clear(attackerThingImages, true);
		DataImageView.clear(defenderThingImages, true);		
		
		for (GridPane grid : grids) {
			grid.setVisible(false);
		}
		
		setVisible(false);
		
	}
	
	public List<DataImageView> getLocalImgViews() {

		List<DataImageView> imageViews = new ArrayList<DataImageView>();
		imageViews.addAll(attackerThingImages);
		imageViews.addAll(defenderThingImages);
		
		return imageViews;
		
	}
	
	public Button getDiceButton() 		{ return diceButton; 	}
	public Button getHitsButton()	 	{ return hitsButton;	}
	public Button getRetreatButton() 	{ return retreatButton; }
	
	public void setLocalName(String name) {
		localName = name;
	}
	
	private void updateNames() {

		((Text) lookup("#defenderText")).setText("Defender: " + battle.getDefender().getName());
		((Text) lookup("#attackerText")).setText("Attacker: " + battle.getAttacker().getName());
		
	}
	
	private void updateThingImages() {
		
		if (battle.getDefender() == null || battle.getDefender() == null) {
			LOGGER.warning("Attacker and defender must be set before updating the combat creature images.");
			return;
		}
		
		setCreatureImages(battle.getDefenderCreatures(), defenderThingImages, localName.equals(battle.getDefender().getName()));
		setCreatureImages(battle.getAttackerCreatures(), attackerThingImages, localName.equals(battle.getAttacker().getName()));
		
	}
	
	private void setCreatureImages(List<Creature> creatures, List<DataImageView> imgViews, boolean local) {
		
		for (int i=0; i<creatures.size(); ++i) {
			
			DataImageView imgView = imgViews.get(i);
			Thing thing = creatures.get(i);
			setThingImage(imgView, thing, local);
			
		}
		
	}
	
	private void setThingImage(DataImageView imgView, Thing thing, boolean visibleImg) {

		imgView.setImage(visibleImg? thing.getImage() : Thing.getBackImage());
		imgView.setData(thing);
		
		imgView.setVisible(true);
		imgView.getParent().setVisible(true);
		
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
		
		actionsBox.getChildren().addAll(diceButton, hitsButton, retreatButton);
		
		getChildren().add(actionsBox);
		
	}
	
	private void addPlayerGrid(boolean attacker) {
		
		GridPane grid = GridPaneBuilder.create().hgap(10).vgap(5).prefHeight(150).visible(false).build();
		grid.managedProperty().bind(grid.visibleProperty());
		setMargin(grid, new Insets(0, 10, 0, 10));
		
		Text text = TextBuilder.create().text(attacker ? "Attacker" : "Defender").font(Font.font("Lucida Sans", 20)).fill(Color.WHITE).build();
		text.setId(attacker? "attackerText" : "defenderText");
		text.managedProperty().bind(text.visibleProperty());
		text.visibleProperty().bind(grid.visibleProperty());
		setMargin(text, new Insets(0, 0, 0, 10));
		getChildren().add(text);
		
		for (int i=0; i<Tile.MAXIMUM_THINGS; ++i) {
			
			DataImageView imgView = new DataImageView(false, THING_WIDTH);
			imgView.managedProperty().bind(imgView.visibleProperty());
			imgView.setVisible(false);
			
			if (attacker) {
				attackerThingImages.add(imgView);
			} else {
				defenderThingImages.add(imgView);
			}
			
			grid.add(imgView, i%8, i >= 8 ? 1 : 0);
			
		}
		
		grids.add(grid);
		getChildren().add(grid);
		
	}
	
}
