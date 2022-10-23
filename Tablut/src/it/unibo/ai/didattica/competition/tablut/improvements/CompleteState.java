package it.unibo.ai.didattica.competition.tablut.improvements;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Improvement of 'State' class. Implements an easier to use tracking of the pieces in the board.
 * More operation con be made such as computing all the possible next-state.
 *
 * @author G. Carrino, M. Vannucchi
 */
public class CompleteState extends State {
    // Rules of the current game, so we can know whether a Action comply with rule.
    private final Game rules;

    // List containing all the white piece (king included).
    private List<Piece> whitePieces;

    // List containing all the black piece.
    private List<Piece> blackPieces;

    // Board of pieces can be used to find neighbour of a piece.
    private Piece[][] completeBoard;

    public CompleteState(Game rules){
        super();

        this.rules = rules;
        this.initialize();
    }

    public CompleteState(Game rules, State state) {
        super();

        this.rules = rules;

        // From the state in input we need to take the board and the current turn
        this.board = state.getBoard();
        this.turn = state.getTurn();

        this.initialize();
    }

    /**
     * Method that initialize the pieces lists using the board in the state.
     */
    private void initialize() {
        this.whitePieces = new ArrayList<>();
        this.blackPieces = new ArrayList<>();
        this.completeBoard = new Piece[9][9];

        for (int i = 0; i < this.getBoard().length; i++) {
            for (int j = 0; j < this.getBoard().length; j++) {
                Pawn pawnKind = this.getPawn(i, j);

                // If we find a empty pawn we just put a null value in the board
                if (pawnKind.equals(Pawn.EMPTY) || pawnKind.equals(Pawn.THRONE)) {
                    this.completeBoard[i][j] = null;
                    continue;
                }

                Position pawnPosition = new Position(i, j);
                Piece piece = new Piece(this, pawnKind, pawnPosition);

                if (piece.getPiecePlayer().equals(Turn.WHITE)) {
                    whitePieces.add(piece);
                } else{
                    blackPieces.add(piece);
                }

                this.completeBoard[i][j] = piece;
            }
        }
    }

    /**
     *
     * @param player the player turn of which we want the pieces
     * @return return the list of pieces belonging to the player
     */
    public List<Piece> getPlayerPieces(Turn player) {
        if (player.equals(Turn.WHITE))
            return this.whitePieces;
        else if (player.equals(Turn.BLACK))
            return this.blackPieces;
        else
            throw new IllegalArgumentException("Player must be either WHITE or BLACK");
    }

    /**
     *
     * @return Return a list of all the actions that the current player can do
     */
    public List<Action> getAllValidActions() {
        List<Piece> playerPieces = this.getPlayerPieces(this.getTurn());
        List<Action> possibleActions = new ArrayList<>();

        for(Piece piece: playerPieces) {
            List<Action> pieceValidActions = piece.getValidActions();
            possibleActions.addAll(pieceValidActions);
        }

        return possibleActions;
    }
}
