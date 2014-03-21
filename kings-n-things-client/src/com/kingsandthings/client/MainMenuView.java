package com.kingsandthings.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import com.kingsandthings.client.game.InitializableView;

public class MainMenuView extends Scene implements InitializableView {
	
	private final static int WIDTH = 600;
	private final static int HEIGHT = 400;
	
	private BorderPane root;
	
	private VBox mainMenu; 
	private VBox gameSettings;
	
	private List<TextField> connectionFields;
	
	public MainMenuView() {
		super(new BorderPane(), WIDTH, HEIGHT);
		
		root = (BorderPane) getRoot();
		
		getStylesheets().add(getClass().getResource("/css/MainMenu.css").toExternalForm());
	}
	
	@Override
	public void initialize() {
		
		connectionFields = new ArrayList<TextField>();
		
		initializeMainMenu();	
		initializeGameSettings();	
		
		initializeStatusText();
		
		displayMainMenu();
		
	}
	
	public void setDefaultIPAndPort() {
		
		connectionFields.get(0).setText("127.0.0.1");
		connectionFields.get(1).setText("9000");
		
	}
	
	public String getIP() {
		return ((TextField) root.lookup("#ip")).getText();
	}
	
	public Integer getPort() {
		return Integer.parseInt(((TextField) root.lookup("#port")).getText());
	}
	
	public void setStatusText(String text) {
		((Text) root.lookup("#statusText")).setText(text);
	}
	
	public void displayMainMenu() { 
		root.setCenter(mainMenu); 
	}
	
	public void displayGameSettings() { 
		root.setCenter(gameSettings); 
		
		setDefaultIPAndPort();
	}

	private void initializeStatusText() {
		
		Text statusText = new Text();
		statusText.setFont(Font.font("Lucida Sans", 12));
		statusText.setFill(Color.RED);
		statusText.setId("statusText");
		
		root.setTop(statusText);
		
		BorderPane.setAlignment(statusText, Pos.CENTER);
		BorderPane.setMargin(statusText, new Insets(10));
		
	}
	
	private void initializeMainMenu() {
		
		mainMenu = new VBox(20);
		mainMenu.setStyle("-fx-background-color: #FFFFFF");
		mainMenu.setAlignment(Pos.CENTER);
		
		ImageView logoImg = new ImageView(new Image("/images/logo.png"));
		
		Button newGameButton = new Button("Join Game");
		newGameButton.setId("joinGameButton");
		newGameButton.setPrefWidth(125);
		
		Button exitButton = new Button("Exit");
		exitButton.setId("exitButton");
		exitButton.setMinWidth(125);
		
		mainMenu.getChildren().addAll(logoImg, newGameButton, exitButton);
		
	}
	
	private void initializeGameSettings() {
		
		gameSettings = new VBox(20);
		gameSettings.setStyle("-fx-background-color: #FFFFFF");
		gameSettings.setAlignment(Pos.CENTER);
	
		// Initialize grid for settings
		GridPane grid = new GridPane();
		grid.setId("settingsGrid");
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(10);
		grid.setVgap(20);
		
		// Player number label and slider
		Label playerNumLabel = new Label("Number of Players:");
		Slider playerNumSlider = new Slider(2, 4, 0);
		playerNumSlider.setId("playerNum");
		playerNumSlider.setMinorTickCount(0);
		playerNumSlider.setMajorTickUnit(1);
		playerNumSlider.setSnapToTicks(true);
		playerNumSlider.setShowTickMarks(true);
		playerNumSlider.setShowTickLabels(true);
		
		// Set to 4 and disable for now
		playerNumSlider.setValue(4);
		playerNumSlider.setDisable(true);
		
		GridPane.setConstraints(playerNumLabel, 0, 0, 1, 1, HPos.CENTER, VPos.TOP);
		GridPane.setConstraints(playerNumSlider, 1, 0);
			
		// Join button
		Button startButton = new Button("Join");
		startButton.setId("joinButton");
		startButton.setPrefWidth(150);
		GridPane.setConstraints(startButton, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER);
		
		// Back button
		Button backButton = new Button("Back");
		backButton.setId("backButton");
		backButton.setPrefWidth(150);
		GridPane.setConstraints(backButton, 0, 3, 2, 1, HPos.CENTER, VPos.CENTER);
		
		// Add labels and controls to grid
		grid.getChildren().addAll(playerNumLabel, playerNumSlider, startButton, backButton);	
		
		// Add the settings grid to the VBox
		gameSettings.getChildren().addAll(grid);
		
		addConnectionFields();
	}
	
	private void addConnectionFields() {

		GridPane grid = (GridPane) gameSettings.lookup("#settingsGrid");
		
		Label ipLabel = new Label("IP: ");
		TextField ipField = new TextField();
		ipField.setId("ip");
		
		GridPane.setConstraints(ipLabel, 0, 1);
		GridPane.setConstraints(ipField, 1, 1);
		
		Label portLabel = new Label("Port: ");
		TextField portField = new TextField();
		portField.setId("port");
		
		GridPane.setConstraints(portLabel, 0, 2);
		GridPane.setConstraints(portField, 1, 2);
		
		connectionFields.addAll(Arrays.asList(ipField, portField));
		
		grid.getChildren().addAll(ipLabel, ipField, portLabel, portField);

		GridPane.setConstraints(gameSettings.lookup("#joinButton"), 0, 2+2, 2, 1, HPos.CENTER, VPos.CENTER);
		GridPane.setConstraints(gameSettings.lookup("#backButton"), 0, 3+2, 2, 1, HPos.CENTER, VPos.CENTER);
		
	}
	
}
