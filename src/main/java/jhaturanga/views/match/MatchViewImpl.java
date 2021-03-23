package jhaturanga.views.match;

import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import jhaturanga.commons.graphics.EndGamePopup;
import jhaturanga.commons.graphics.MatchBoardView;
import jhaturanga.model.timer.ObservableTimer;
import jhaturanga.views.AbstractJavaFXView;
import jhaturanga.views.pages.PageLoader;
import jhaturanga.views.pages.Pages;

public final class MatchViewImpl extends AbstractJavaFXView implements MatchView {

    @FXML
    private AnchorPane root;

    @FXML
    private BorderPane grid;

    @FXML
    private Label timerP1;

    @FXML
    private Button saveMatchButton;

    @FXML
    private Label timerP2;

    @FXML
    private Label player1Label;

    @FXML
    private Label player2Label;

    private void onTimeChange() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                timerP1.setText(getMatchController().getWhiteReminingTime());
                timerP2.setText(getMatchController().getBlackReminingTime());
            }
        });

    }

    private void onTimeFinish() {
        Platform.runLater(() -> {
            final EndGamePopup popup = new EndGamePopup();
            popup.setMessage("Tempo finito");
            popup.setButtonAction(() -> {
                this.backToMainMenu();
                popup.close();
            });
            popup.show();
        });
    }

    @Override
    public void init() {
        this.getMatchController().start();

        final Pane board = new MatchBoardView(this.getMatchController(), this);
        if (this.getMatchController().getApplicationInstance().getGameType().isEmpty()) {
            this.saveMatchButton.setDisable(true);
        }

        this.grid.prefWidthProperty().bind(Bindings.min(root.widthProperty(), root.heightProperty()));
        this.grid.prefHeightProperty().bind(Bindings.min(root.widthProperty(), root.heightProperty()));
        this.grid.setCenter(board);
        this.getController().getApplicationInstance().getTimer().ifPresent(t -> {
            new ObservableTimer(t, this::onTimeFinish, this::onTimeChange).start();
        });
        this.player1Label.setText(
                this.getMatchController().getApplicationInstance().getWhitePlayer().get().getUser().getUsername());
        this.player2Label.setText(
                this.getMatchController().getApplicationInstance().getBlackPlayer().get().getUser().getUsername());
    }

    @FXML
    public void giveUpMatch(final Event event) {
        this.getMatchController().getApplicationInstance().getTimer().get().stop();
        this.getMatchController().getApplicationInstance().clearMatchInfo();
        Platform.runLater(() -> {
            final EndGamePopup popup = new EndGamePopup();
            popup.setMessage(
                    this.getMatchController().getPlayerTurn().getUser().getUsername() + " are you sure to give up?");
            popup.setButtonAction(() -> {
                this.backToMainMenu();
                popup.close();
            });
            popup.show();
        });

    }

    @FXML
    public void backToMenu(final Event event) throws IOException {
        this.saveMatch(event);
        this.backToMainMenu();
    }

    private void backToMainMenu() {
        this.getMatchController().getApplicationInstance().clearMatchInfo();
        PageLoader.switchPage(this.getStage(), Pages.HOME, this.getController().getApplicationInstance());
    }

    @FXML
    public void saveMatch(final Event event) throws IOException {
        this.getMatchController().saveMatch();
    }

}
