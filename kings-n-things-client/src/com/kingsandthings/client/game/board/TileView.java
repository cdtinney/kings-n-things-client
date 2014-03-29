package com.kingsandthings.client.game.board;

import java.util.logging.Logger;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.InnerShadowBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.things.Thing;

public class TileView extends ImageView {
	
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(TileView.class.getName());
	
	private static Image defaultImg = new Image("/images/tiles/back.png");
	private static final int TILE_WIDTH = 100;
	
	// Model
	private Tile tile;		
	
	// View
	private TileActionMenu actionMenu;
	private ImageView controlMarkerImageView;
	private ImageView fortImageView;
	private ImageView thingStackImageView;
	
	public TileView (String id, int x, int y) {
		
		if (id != null) {
			setId(id);
		}
		
		setImage(defaultImg);
		setPreserveRatio(true);
		setCache(true);
		setFitWidth(TILE_WIDTH);
		setX(x);
		setY(y);
		
		actionMenu = new TileActionMenu(this);
		
	}
	
	public void initialize(Tile tile) {
		
		this.tile = tile;
		
		addControlMarkerView();
		addFortView();
		addThingStackView();
		
		addPropagationHandler(controlMarkerImageView);
		addPropagationHandler(fortImageView);
		addPropagationHandler(thingStackImageView);
		
		update();
		
	}
	
	public void setTile(Tile tile, boolean update) {
		this.tile = tile;
		
		if (update) {
			update();
		}
		
	}

	public Tile getTile() {
		return tile;
	}
	
	public void update() {
		
		setImage(tile.getImage());
		
		updateFortView();
		updateControlMarkerView();
		updateThingsStackView();
		updateBattleHighlight();
	}
	
	/*
	 * Returns an instance of an action menu specific to this tile instance.
	 */
	public TileActionMenu getActionMenu() {
		return actionMenu;
	}
	
	/*
	 * Toggles visibility of the action menu.
	 */
	public void toggleActionMenu() {

		boolean visible = actionMenu.showingProperty().get();
		
		if (visible || !actionMenu.visibleItems()) {
			actionMenu.hide();
		} else {
			
			// Menu must be shown to determine width
			actionMenu.show(this, Side.RIGHT, -25, 10);
			double menuWidth = actionMenu.getWidth();
			actionMenu.hide();

			double parentWidth = ((Pane) this.getParent()).getWidth();
			double tileX = getX();
			double tileWidth = getFitWidth();
			
			if ((tileX + tileWidth/2 + menuWidth) < parentWidth) {
				actionMenu.show(this, Side.RIGHT, -25, 10);
			} else {
				actionMenu.show(this, Side.LEFT, 25, 10);
			}
			
		}
		
	}
	
	public void updateBattleHighlight() {
		boolean hasBattle = tile.hasBattleToResolve();
		
		if (!hasBattle) {
			setEffect(null);
		} else {
			setEffect(InnerShadowBuilder.create().radius(6).choke(5).color(Color.RED).build());
		}
		
	}
	
	public void updateControlMarkerView() {
		boolean hasOwner = tile.getOwner() != null;
		controlMarkerImageView.setImage(hasOwner ? tile.getOwner().getControlMarker() : null);
	}
	
	public void updateFortView() {
		boolean hasFort = tile.getFort() != null;
		fortImageView.setImage(hasFort ? tile.getFort().getImage() : null);
	}
	
	public void updateThingsStackView() {
		thingStackImageView.setImage(tile.hasThings() ? Thing.getStackImage() : null);
	}
	
	public void addHighlight(boolean valid) {
	
		InnerShadow innerShadow = InnerShadowBuilder.create().radius(4).choke(5).build();
		
		if (valid) {
			innerShadow.setColor(Color.LIMEGREEN);
		} else {
			innerShadow.setColor(Color.RED);
		}
		
		setEffect(innerShadow);
		
	}
	
	public void removeHighlight() {
		setEffect(null);
	}
	
	private void addThingStackView() {

		double stackWidth = 35;
		double x = getX() + TILE_WIDTH/2 - stackWidth/2;
		double y = getY() + 35;

		thingStackImageView = ImageViewBuilder.create().fitWidth(stackWidth).x(x).y(y).preserveRatio(true).cache(true).build();
		((Pane) getParent()).getChildren().add(thingStackImageView);	
		
	}
	
	private void addFortView() {

		fortImageView = ImageViewBuilder.create().fitWidth(30).x(getX() + 18).y(getY() + 3).preserveRatio(true).cache(true).build();
		((Pane) getParent()).getChildren().add(fortImageView);	
		
	}
	
	private void addControlMarkerView() {
		
		controlMarkerImageView = ImageViewBuilder.create().fitWidth(30).x(getX() + 47).y(getY() + 3).preserveRatio(true).cache(true).build();
		((Pane) getParent()).getChildren().add(controlMarkerImageView);	
		
	}
	
	private void addPropagationHandler(ImageView imageView) {
		
		final TileView instance = this;
		imageView.addEventHandler(Event.ANY, new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				instance.fireEvent(event);
			}
		});
		
	}
	
}
