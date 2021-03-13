package jhaturanga.views.editor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import jhaturanga.controllers.editor.EditorController;
import jhaturanga.model.board.Board;
import jhaturanga.model.board.BoardPosition;
import jhaturanga.model.board.BoardPositionImpl;
import jhaturanga.model.piece.Piece;
import jhaturanga.model.piece.PieceImpl;
import jhaturanga.model.piece.PieceType;
import jhaturanga.model.player.Player;
import jhaturanga.model.player.PlayerColor;
import jhaturanga.model.player.PlayerImpl;
import jhaturanga.views.AbstractView;
import jhaturanga.views.match.TileImpl;

public class EditorViewImpl extends AbstractView implements EditorView {

    @FXML
    private VBox whitePiecesSelector;

    @FXML
    private VBox blackPiecesSelector;

    @FXML
    private AnchorPane root;

    @FXML
    private TextField columnsSelector;

    @FXML
    private TextField rowsSelector;

    @FXML
    private BorderPane grid;

    @FXML
    public final void createBoard(final Event event) {
        this.getEditorController().resetBoard(Integer.parseInt(this.columnsSelector.getText()),
                Integer.parseInt(this.rowsSelector.getText()));
        this.redraw(this.getEditorController().getBoardStatus());
        this.drawBoard(this.getEditorController().getBoardStatus());
    };

    private static final double PIECE_SCALE = 1.5;
    private final Map<Rectangle, Piece> pieces = new HashMap<>();
    private final Map<Pair<PieceType, Player>, Image> piecesImage = new HashMap<>();
    private final Set<TileImpl> tilesHighlighted = new HashSet<>();
    private Player whitePlayer;
    private Player blackPlayer;
    private GridPane guiBoard = new GridPane();

    @Override
    public final void init() {
        this.whitePlayer = new PlayerImpl(PlayerColor.WHITE, this.getController().getModel().getFirstUser().get());
        this.blackPlayer = new PlayerImpl(PlayerColor.BLACK, this.getController().getModel().getSecondUser().get());
        this.loadAllPieces();
        this.drawAllPieces();
        this.setupListeners();
        this.drawBoard(this.getEditorController().getDefaultStartingBoard());
        this.redraw(this.getEditorController().getBoardStatus());
        this.grid.setCenter(this.guiBoard);
        this.grid.prefWidthProperty().bind(Bindings.min(root.widthProperty(), root.heightProperty()));
        this.grid.prefHeightProperty().bind(Bindings.min(root.widthProperty(), root.heightProperty()));
    }

    private void setupListeners() {
        this.pieces.keySet().forEach(i -> {
            i.setOnMousePressed(e -> this.onPieceClick(e, i));

            i.setOnMouseDragged(e -> this.onPieceDragged(e, i));

            i.setOnMouseReleased(e -> this.onPieceReleased(e, i));
        });

    }

    private void loadAllPieces() {
        Arrays.stream(PieceType.values()).forEach(i -> {

            final Rectangle pieceViewPortWhite = new Rectangle();
            final Rectangle pieceViewPortBlack = new Rectangle();

            pieceViewPortWhite.widthProperty().bind(this.whitePiecesSelector.widthProperty().divide(PIECE_SCALE));
            pieceViewPortWhite.heightProperty().bind(this.whitePiecesSelector.widthProperty().divide(PIECE_SCALE));

            pieceViewPortBlack.widthProperty().bind(this.whitePiecesSelector.widthProperty().divide(PIECE_SCALE));
            pieceViewPortBlack.heightProperty().bind(this.whitePiecesSelector.widthProperty().divide(PIECE_SCALE));

            this.pieces.put(pieceViewPortWhite, new PieceImpl(i, new BoardPositionImpl(0, 0), whitePlayer));
            this.pieces.put(pieceViewPortBlack, new PieceImpl(i, new BoardPositionImpl(0, 0), blackPlayer));
        });
    }

    private void drawAllPieces() {
        this.pieces.entrySet().forEach(i -> {
            final Image img = new Image("file:" + ClassLoader.getSystemResource(
                    "piece/PNGs/No_shadow/1024h/" + i.getValue().getPlayer().getColor().toString().charAt(0) + "_"
                            + i.getValue().getType().toString() + ".png")
                    .getFile());
            i.getKey().setFill(new ImagePattern(img));
            this.piecesImage.put(new Pair<>(i.getValue().getType(), i.getValue().getPlayer()), img);
            if (i.getValue().getPlayer().getColor().equals(PlayerColor.WHITE)) {
                this.whitePiecesSelector.getChildren().add(i.getKey());
            } else {
                this.blackPiecesSelector.getChildren().add(i.getKey());
            }
        });
    }

    private void onPieceClick(final MouseEvent event, final Rectangle piece) {
        // Check if it's over limit
        if (event.getButton().equals(MouseButton.SECONDARY) && this.guiBoard.getChildren().contains(piece)) {
            this.removePieceTotally(piece);
        } else {
            if (this.guiBoard.getChildren().contains(piece)) {
                this.resetHighlightedTiles();
                this.guiBoard.getChildren().remove(piece);
            } else {
                final Rectangle rect = new Rectangle();
                final Piece originalPiece = this.pieces.get(piece);
                rect.setFill(new ImagePattern(
                        this.piecesImage.get(new Pair<>(originalPiece.getType(), originalPiece.getPlayer()))));

                rect.widthProperty().bind(this.whitePiecesSelector.widthProperty().divide(PIECE_SCALE));
                rect.heightProperty().bind(this.whitePiecesSelector.widthProperty().divide(PIECE_SCALE));
                this.pieces.put(rect, new PieceImpl(originalPiece.getType(), originalPiece.getPiecePosition(),
                        originalPiece.getPlayer()));
                if (originalPiece.getPlayer().getColor().equals(PlayerColor.WHITE)) {
                    this.whitePiecesSelector.getChildren().remove(piece);
                    this.whitePiecesSelector.getChildren().add(rect);
                } else {
                    this.blackPiecesSelector.getChildren().remove(piece);
                    this.blackPiecesSelector.getChildren().add(rect);
                }
                this.setupListeners();
            }
            this.grid.getChildren().add(piece);
        }
    }

    private void resetHighlightedTiles() {
        this.tilesHighlighted.forEach(i -> {
            i.resetCircleHighlight();
            i.getChildren().clear();
        });
        this.tilesHighlighted.clear();
    }

    /**
     * On piece dragged handler.
     * 
     * @param event - the mouse event
     * @param piece - the piece which is dragged
     */
    private void onPieceDragged(final MouseEvent event, final Rectangle piece) {
        piece.setX(event.getX() - piece.getWidth() / 2);
        piece.setY(event.getY() - piece.getHeight() / 2);

    }

    private void onPieceReleased(final MouseEvent event, final Rectangle piece) {
        if (this.grid.getChildren().contains(piece) && this.isItReleasedOnBoard(event)) {
            final BoardPosition position = this.getBoardPositionsFromGuiCoordinates(event.getSceneX(),
                    event.getSceneY());
            final BoardPosition realPosition = this.getRealPositionFromBoardPosition(position);
            this.grid.getChildren().remove(piece);
            this.guiBoard.add(piece, realPosition.getX(), realPosition.getY());
            this.pieces.get(piece).setPosition(position);
            System.out.println(position);
            this.getEditorController().addPieceToBoard(this.pieces.get(piece));
            this.guiBoard.requestFocus();
            this.resetHighlightedTiles();
            this.redraw(this.getEditorController().getBoardStatus());
        } else if (!this.isItReleasedOnBoard(event)) {
            this.removePieceTotally(piece);
        }
    }

    private void removePieceTotally(final Rectangle piece) {
        this.grid.getChildren().remove(piece);
        this.guiBoard.getChildren().remove(piece);
        this.getEditorController().getBoardStatus().removeAtPosition(this.pieces.get(piece).getPiecePosition());
        this.pieces.remove(piece);
    }

    private boolean isItReleasedOnBoard(final MouseEvent event) {
        return this.guiBoard.contains(new Point2D(event.getSceneX(), event.getSceneY()));
    }

    private void drawPiece(final Piece piece) {

        final Rectangle pieceViewPort = new Rectangle();

        pieceViewPort.setFill(new ImagePattern(this.piecesImage.get(new Pair<>(piece.getType(), piece.getPlayer()))));

        pieceViewPort.widthProperty().bind(this.guiBoard.getChildren().stream().filter(i -> i instanceof TileImpl)
                .map(i -> (TileImpl) i).findAny().get().widthProperty().divide(PIECE_SCALE));
        pieceViewPort.heightProperty().bind(this.guiBoard.getChildren().stream().filter(i -> i instanceof TileImpl)
                .map(i -> (TileImpl) i).findAny().get().heightProperty().divide(PIECE_SCALE));

        /*
         * When a piece is pressed we save the selected rectangle and make a call to the
         * onPieceClick function.
         */
        pieceViewPort.setOnMousePressed(e -> this.onPieceClick(e, pieceViewPort));

        /**
         * Handler for make the piece draggable over the board.
         */
        pieceViewPort.setOnMouseDragged(e -> this.onPieceDragged(e, pieceViewPort));

        pieceViewPort.setOnMouseReleased(e -> this.onPieceReleased(e, pieceViewPort));

        this.pieces.put(pieceViewPort, piece);

        final BoardPosition realPosition = this.getRealPositionFromBoardPosition(piece.getPiecePosition());
        this.guiBoard.add(pieceViewPort, realPosition.getX(), realPosition.getY());
        GridPane.setHalignment(pieceViewPort, HPos.CENTER);
    }

    private void redraw(final Board board) {
        final var toRemove = this.guiBoard.getChildren().stream().filter(n -> n instanceof Rectangle)
                .collect(Collectors.toList());

        this.guiBoard.getChildren().removeAll(toRemove);

        board.getBoardState().forEach(i -> this.drawPiece(i));
    }

    /**
     * Draw the board.
     * 
     * @param board - the board to be drew
     */
    private void drawBoard(final Board board) {
        this.guiBoard.getChildren().clear();
        final int bigger = Integer.max(board.getColumns(), board.getRows());
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                final TileImpl tile = new TileImpl(this.getRealPositionFromBoardPosition(new BoardPositionImpl(j, i)));
                tile.prefWidthProperty().bind(this.guiBoard.widthProperty().divide(bigger));
                tile.prefHeightProperty().bind(this.guiBoard.heightProperty().divide(bigger));
                this.guiBoard.add(tile, j, i);
            }
        }
    }

    private BoardPosition getBoardPositionsFromGuiCoordinates(final double x, final double y) {
        final TileImpl tile = this.guiBoard.getChildren().stream().filter(i -> i instanceof TileImpl)
                .map(i -> (TileImpl) i).findAny().get();
        final int column = (int) (((x - this.guiBoard.getLayoutX())
                / (tile.getWidth() * this.getEditorController().getBoardStatus().getColumns()))
                * this.getEditorController().getBoardStatus().getColumns());
        final int row = this.getEditorController().getBoardStatus().getRows() - 1
                - (int) (((y - this.guiBoard.getLayoutY())
                        / (tile.getHeight() * this.getEditorController().getBoardStatus().getRows()))
                        * this.getEditorController().getBoardStatus().getRows());
        return new BoardPositionImpl(column, row);
    }

    private BoardPosition getRealPositionFromBoardPosition(final BoardPosition position) {
        return new BoardPositionImpl(position.getX(),
                this.getEditorController().getBoardStatus().getRows() - 1 - position.getY());
    }

    @Override
    public final EditorController getEditorController() {
        return (EditorController) this.getController();
    }

}