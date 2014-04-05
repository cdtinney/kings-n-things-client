package com.kingsandthings.client.game.board;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;

import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.model.phase.CombatPhase;
import com.kingsandthings.common.model.phase.ConstructionPhase;
import com.kingsandthings.common.model.phase.MovementPhase;
import com.kingsandthings.common.model.phase.Phase;
import com.kingsandthings.common.model.phase.PhaseManager;
import com.kingsandthings.common.model.phase.StartingKingdomsPhase;

public class TileActionMenu extends ContextMenu {
	
	public TileView owner;
	
	private Map<String, MenuItem> menuItems;
	private Map<Class<? extends Phase>, List<MenuItem>> phaseItems;
	
	public TileActionMenu(TileView tileView) {
		
		owner = tileView;
		
		menuItems = new LinkedHashMap<String, MenuItem>();
		phaseItems = new LinkedHashMap<Class<? extends Phase>, List<MenuItem>>();
		
		addMenuItems();
		addListeners();
		
		setPhaseItems(MovementPhase.class, get("selectThings"));
		setPhaseItems(StartingKingdomsPhase.class, get("placeControlMarker"));
		setPhaseItems(ConstructionPhase.class, get("buildFort"));
		setPhaseItems(ConstructionPhase.class, get("upgradeFort"));
		setPhaseItems(CombatPhase.class, get("resolveBattle"));
		
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
	
	@SuppressWarnings("unused")
	private void onPhaseChanged(PropertyChangeEvent event) {
		
		hideMenuItems();
		
		Phase newPhase = (Phase) event.getNewValue();
		if (newPhase == null) {
			return;
		}
		
		List<MenuItem> items = phaseItems.get(newPhase.getClass());
		if (items == null) {
			return;
		}
		
		for (MenuItem item : items) {
			item.setVisible(true);
		}
		
	}
	
	private void setPhaseItems(Class<? extends Phase> clazz, MenuItem ... items) {
		
		if (phaseItems.get(clazz) == null) {
			phaseItems.put(clazz, new ArrayList<MenuItem>());
		}
		
		phaseItems.get(clazz).addAll(Arrays.asList(items));
		
	}
	
	private void hideMenuItems() {

		for (MenuItem item : menuItems.values()) {
			item.setVisible(false);
		}
		
	}
	
	
	private void addListeners() {
		
		PropertyChangeDispatcher.getInstance().addListener(PhaseManager.class, "currentPhase", this, "onPhaseChanged");
		
	}
	
	private void addMenuItems() {

		// Initial control marker placement phase
		menuItems.put("placeControlMarker", MenuItemBuilder.create().visible(false).text("Place control marker").build());
		
		// Movement phase
		menuItems.put("selectThings", MenuItemBuilder.create().visible(false).text("Select things").build());
		
		// Construction phase
		menuItems.put("buildFort", MenuItemBuilder.create().visible(false).text("Build fort (5 gold)").build());
		menuItems.put("upgradeFort", MenuItemBuilder.create().visible(false).text("Upgrade fort (5 gold)").build());
		
		// Combat phase
		menuItems.put("resolveBattle", MenuItemBuilder.create().visible(false).text("Resolve battle").build());
		
		getItems().addAll(menuItems.values());
		
	}

}
