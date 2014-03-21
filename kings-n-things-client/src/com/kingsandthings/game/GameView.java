package com.kingsandthings.game;

import java.beans.PropertyChangeEvent;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

public class GameView extends Scene implements InitializableView {
	
	private final static int WIDTH = 1400;
	private final static int HEIGHT = 900;
	
	private static Label status;
	
	private BorderPane root;
	
	public GameView() {
		super(new BorderPane(), WIDTH, HEIGHT);	
		
		root = (BorderPane) getRoot();
		getStylesheets().add(getClass().getResource("/css/Game.css").toExternalForm());
	}
	
	@Override
	public void initialize() {
		addMenuBar();
		addStatusText();
		
		//PropertyChangeDispatcher.getInstance().addListener(PlayerManager.class, "activePlayer", this, "activePlayerChanged");
	}
	
	public static String getStatusText() {
		return status != null? status.getText() : null;
	}
	
	public static void setStatusText(String message) {
		
		if (status == null) {
			return;
		}
		
		status.setText("STATUS: " + (message == null? "" : message));		
	}

	public void addToBorderPane(Node node, String position) {
		
		if (position.equals("center")) {
			root.setCenter(node);
			
		} else if (position.equals("bottom")) {
			root.setBottom(node);
			
		} else if (position.equals("top")) {
			root.setTop(node);
			
		} else if (position.equals("left")) {
			root.setLeft(node);
			
		} else if (position.equals("right")) {
			root.setRight(node);
			
		}
		
	}
	
	@SuppressWarnings("unused")
	private void activePlayerChanged(PropertyChangeEvent event) {
		setStatusText(null);
	}
	
	private void addMenuBar() {
		
		MenuBar menuBar = new MenuBar();
		menuBar.setPrefWidth(WIDTH);
		
		// Game menu
		Menu gameMenu = new Menu("Game");
		
		MenuItem quitGameItem = new MenuItem("Quit Game");
		quitGameItem.setId("quitGameMenuItem");
		
		gameMenu.getItems().addAll(quitGameItem);
		
		// Help menu
		Menu helpMenu = new Menu("Help");
		
		MenuItem aboutItem = new MenuItem("About");
		aboutItem.setId("aboutMenuItem");
		
		helpMenu.getItems().addAll(aboutItem);
		
		// Add all menus to bar
		menuBar.getMenus().addAll(gameMenu, helpMenu);
		
		root.setTop(menuBar);
		
	}
	
	private void addStatusText() {
		
		status = new Label("STATUS:");
		status.setFont(new Font("Lucida Console", 12));
		status.getStyleClass().add("statusText");
		status.setPrefWidth(root.getWidth());
		
		root.setBottom(status);
		
	}
	
}
