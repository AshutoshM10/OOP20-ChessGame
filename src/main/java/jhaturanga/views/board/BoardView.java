package jhaturanga.views.board;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.geometry.HPos;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import jhaturanga.controllers.game.MatchController;
import jhaturanga.model.board.Board;
import jhaturanga.model.board.BoardPositionImpl;
import jhaturanga.model.game.GameController;
import jhaturanga.model.piece.Piece;
import jhaturanga.model.piece.PieceType;
import jhaturanga.model.player.Player;

public final class BoardView extends Pane {

    private static final double PIECE_SCALE = 1.5;

    private final MatchController matchController;
    private final GameController gameController;
    private final GridPane grid;
    private final Map<Rectangle, Pair<Integer, Integer>> piecesPosition;

    // TODO: implement image caching for quickly redraw
    private final Map<Pair<PieceType, Player>, Image> piecesImage;

    public BoardView(final MatchController matchController, final GameController gameController) {

        this.matchController = matchController;
        this.piecesPosition = new HashMap<>();
        this.gameController = gameController;
        this.piecesImage = new HashMap<>();
        this.loadImages();

        this.grid = new GridPane();
        this.grid.requestFocus();
        this.grid.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.A)) {
                this.redraw(this.matchController.getPrevBoard());
            } else if (e.getCode().equals(KeyCode.D)) {
                this.redraw(this.matchController.getNextBoard());
            }
        });

        this.getChildren().add(this.grid);
        this.drawBoard(this.matchController.getBoardStatus());
        this.redraw(this.matchController.getPrevBoard());
    }

    private void drawBoard(final Board board) {
        final int bigger = Integer.max(board.getColumns(), board.getRows());

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                final int i2 = i;
                final int j2 = j;
                final Pane tile = new Pane();
                tile.setOnMouseClicked(e -> {
                    if (e.getButton().equals(MouseButton.SECONDARY)) {
                        if (tile.getStyle().equals("-fx-background-color:#00FF00")) {
                            tile.setStyle(
                                    (i2 + j2) % 2 == 0 ? "-fx-background-color:#CCC" : "-fx-background-color:#333");
                        } else {
                            tile.setStyle("-fx-background-color:#00FF00");
                        }
                    }
                });
                // TODO: adjust for style.
                tile.setStyle((i + j) % 2 == 0 ? "-fx-background-color:#CCC" : "-fx-background-color:#333");

                tile.prefWidthProperty().bind(this.widthProperty().divide(bigger));
                tile.prefHeightProperty().bind(this.heightProperty().divide(bigger));

                this.grid.add(tile, j, i);
            }
        }
    }

    private void drawPiece(final Piece piece) {

        final Rectangle pieceViewPort = new Rectangle();

        pieceViewPort.setFill(new ImagePattern(this.piecesImage.get(new Pair<>(piece.getType(), piece.getPlayer()))));

        pieceViewPort.widthProperty().bind(this.grid.widthProperty()
                .divide(this.matchController.getBoardStatus().getColumns()).divide(PIECE_SCALE));
        pieceViewPort.heightProperty().bind(
                this.grid.heightProperty().divide(this.matchController.getBoardStatus().getRows()).divide(PIECE_SCALE));

        pieceViewPort.setOnMousePressed(e -> {
            if (!this.matchController.isInNavigationMode()) {
                this.grid.getChildren().remove(pieceViewPort);
                this.getChildren().add(pieceViewPort);
            }
        });

        pieceViewPort.setOnMouseDragged(e -> {
            if (!this.matchController.isInNavigationMode()) {
                pieceViewPort.setX(e.getX() - pieceViewPort.getWidth() / 2);
                pieceViewPort.setY(e.getY() - pieceViewPort.getHeight() / 2);
            }
        });

        pieceViewPort.setOnMouseReleased(e -> {
            if (!this.matchController.isInNavigationMode()) {
                final int oldCol = this.piecesPosition.get(pieceViewPort).getKey();
                final int oldRow = this.piecesPosition.get(pieceViewPort).getValue();

                final int newCol = (int) (((e.getSceneX() - this.getLayoutX()) / this.grid.getWidth())
                        * this.matchController.getBoardStatus().getColumns());
                final int newRow = (int) (((e.getSceneY() - this.getLayoutY()) / this.grid.getHeight())
                        * this.matchController.getBoardStatus().getRows());

                if ((e.getSceneX() - this.getLayoutX()) < 0 || (e.getSceneY() - this.getLayoutY()) < 0) { // Out of
                                                                                                          // Board
                    System.out.println("Out of Board");
                    this.abortMove(pieceViewPort);
                } else if (!this.matchController.move(new BoardPositionImpl(oldCol, oldRow),
                        new BoardPositionImpl(newCol, newRow))) { // Illegal Move
                    System.out.println("Illegal Move");
                    this.abortMove(pieceViewPort);
                } else { // move

                    this.getChildren().remove(pieceViewPort);
                    this.grid.add(pieceViewPort, newCol, newRow);
                    this.redraw(this.matchController.getBoardStatus());
                }
                this.grid.requestFocus();
            }
        });

        this.piecesPosition.put(pieceViewPort,
                new Pair<>(piece.getPiecePosition().getX(), piece.getPiecePosition().getY()));
        this.grid.add(pieceViewPort, piece.getPiecePosition().getX(), piece.getPiecePosition().getY());
        GridPane.setHalignment(pieceViewPort, HPos.CENTER);
    }

    private void abortMove(final Rectangle piece) {
        this.getChildren().remove(piece);
        this.grid.add(piece, this.piecesPosition.get(piece).getKey(), this.piecesPosition.get(piece).getValue());
    }

    private void loadImages() {
        this.matchController.getBoardStatus().getBoardState().forEach(p -> {
            final Image img = new Image("file:" + ClassLoader.getSystemResource("piece/PNGs/No_shadow/1024h/"
                    + p.getPlayer().getColor().toString().charAt(0) + "_" + p.getType().toString() + ".png").getFile());
            this.piecesImage.put(new Pair<>(p.getType(), p.getPlayer()), img);
        });
    }

    public void redraw(final Board board) {
        final var toRemove = this.grid.getChildren().stream().filter(n -> n instanceof Rectangle)
                .collect(Collectors.toList());
        this.grid.getChildren().removeAll(toRemove);
        this.piecesPosition.clear();

        board.getBoardState().forEach(i -> this.drawPiece(i));
    }

}
