package mule;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.Canvas;

import mule.model.town.*;
import mule.model.resources.*;

public class TownController implements Initializable, ControlledScreen {

    private ScreensController controller;


    @FXML private Canvas townParent;

    @FXML private ToolBar toolBar;
    private static ToolBar toolBarInstance;

    @FXML private Label timerLabel;
    private static Label timerLabelInstance;

    @FXML private TextArea displayText;
    private static TextArea displayTextInstance;

    @Override public final void initialize(URL url, ResourceBundle rb) {
        Main.setTown(new Town(townParent));
        setupInfoBar();
        setupDisplayText();

        townParent.addEventHandler(MouseEvent.MOUSE_CLICKED,
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent event) {
                    int x = (int) (event.getX());
                    int y = (int) (event.getY());
                    processClick(x, y);
                }
            });
    }

    private void processClick(int x, int y) {
        if (x >= 360 && y >= 210) {
            goToBlackJackScreen();
        } else if (x > 360 && y < 150) {
            goToStoreScreen();
        }
    }

    private void incrementTurn() {
        if (Main.getTurn().hasNextPlayer()) {
            unboldPlayerFont(Main.getTurn().getCurrentPlayer());
            Main.getTurn().nextPlayer();
            boldPlayerFont(Main.getTurn().getCurrentPlayer());
        } else if (Main.getTurn().hasNextTurn()) {
            Main.getTurn().nextTurn();
            goToMapScreen();
        }
    }

    public static void unboldPlayerFont(int i) {
        ((Label) toolBarInstance.getItems().get(i * 2))
                .setFont(Font.font("System", FontWeight.NORMAL, 12));
    }

    public static void boldPlayerFont(int i) {
        ((Label) toolBarInstance.getItems().get(i * 2))
                .setFont(Font.font("System", FontWeight.BOLD, 12));
    }

    private void goToMapScreen() {
        controller.setScreen(Main.MAP_ID);
        Main.setToolBar(MapController.getToolBar());
        Main.setTimerLabel(MapController.getTimerLabel());
        MapController.getDisplayText().setText(displayText.getText());
        MapController.boldPlayerFont(Main.getTurn().getCurrentPlayer());
        MapController.updateMuleImagePos(-75, -75);
        for (int i = 0; i < Main.getPlayerCount(); i++) {
            MapController.updatePlayerMenu(i);
        }
    }

    private void goToStoreScreen() {
        Main.loadScene(Main.STORE_ID, Main.STORE_FILE);
        controller.setScreen(Main.STORE_ID);
        Main.setToolBar(StoreController.getToolBar());
        Main.setTimerLabel(StoreController.getTimerLabel());
        StoreController.getDisplayText().setText(displayText.getText());
        StoreController.boldPlayerFont(Main.getTurn().getCurrentPlayer());
        for (int i = 0; i < Main.getPlayerCount(); i++) {
            MapController.updatePlayerMenu(i);
        }
    }

    private void goToBlackJackScreen() {
        Main.loadScene(Main.BLACKJACK_ID, Main.BLACKJACK_FILE);
        controller.setScreen(Main.BLACKJACK_ID);
    }

    /**
     * Sets the main screen controller
     * @param screenParent the screen controller
     */
    public final void setScreenParent(ScreensController screenParent) {
        controller = screenParent;
    }

    private void setupInfoBar() {
        toolBarInstance = toolBar;
        Main.setToolBar(toolBarInstance);
        timerLabelInstance = timerLabel;
        Main.setTimerLabel(timerLabelInstance);

        for (int i = 0; i < Main.getPlayerCount(); i++) {
            updatePlayerMenu(i);
        }
        for (int i = Main.getPlayerCount(); i < 4; i++) {
            toolBarInstance.getItems().get(i * 2).setVisible(false);
            toolBarInstance.getItems().get(i * 2 + 1).setVisible(false);
        }

        boldPlayerFont(Main.getTurn().getCurrentPlayer());

    }

    /**
     * Updates the players information in the menubar
     * @param i The rank of the player to update
     */
    public static void updatePlayerMenu(int i) {
        ((Label) (toolBarInstance.getItems().get(i * 2))).setText(
                Main.getPlayer(i).getName() + "\n" +
                "M: " + Main.getPlayer(i).getMoney() + "\n" +
                "E: " + Main.getPlayer(i).getResource(new Energy()) + "\n" +
                "F: " + Main.getPlayer(i).getResource(new Food()) + "\n" +
                "S: " + Main.getPlayer(i).getResource(new Smithore()));
    }

    private void setupDisplayText() {
        displayText.setEditable(false);
        displayTextInstance = displayText;
        displayTextInstance.setText("Welcome to the Town. Go to the store to buy resources " +
                "or go to the Pub to end your turn\n" + MapController.getDisplayText().getText());
    }

    public static void updateDisplayText(String text) {
        displayTextInstance.setText(text + "\n" + displayTextInstance.getText());
    }


    public static TextArea getDisplayText() { return displayTextInstance; }

    public static Label getTimerLabel() { return timerLabelInstance; }

    public static ToolBar getToolBar() { return toolBarInstance; }
}
