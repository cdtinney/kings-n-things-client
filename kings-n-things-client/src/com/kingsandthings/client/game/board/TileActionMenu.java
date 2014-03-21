package com.kingsandthings.client.game.board;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;

import com.kingsandthings.common.model.phase.MovementPhase;
import com.kingsandthings.common.model.phase.StartingKingdomsPhase;

public class TileActionMenu extends ContextMenu {
	
	public TileView owner;
	
	private Map<String, MenuItem> menuItems;
	
	public TileActionMenu(TileView tileView) {
		
		owner = tileView;
		
		menuItems = new LinkedHashMap<String, MenuItem>();

		menuItems.put("placeControlMarker", MenuItemBuilder.create().visible(false).text("Place control marker").build());
		menuItems.put("selectThings", MenuItemBuilder.create().visible(false).text("Select things").build());
		
		getItems().addAll(menuItems.values());
		
		get("selectThings").visibleProperty().bind(MovementPhase.getActive());
		get("placeControlMarker").visibleProperty().bind(StartingKingdomsPhase.getActive());
		
	}
	
	public TileView getOwner() {
		return owner;
	}
	
	public MenuItem get(String id) {
		return menuItems.get(id);
	}
	
	public boolean visibleItems() {
		
		for (MenuItem item : getItems()) {
			if (item.isVisible()) {
				return true;
			}
		}
		
		return false;
		
	}

}
