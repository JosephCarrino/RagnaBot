package it.unibo.ai.didattica.competition.tablut.improvements;


import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

import java.util.ArrayList;
import java.util.List;

/**
 * Improvement of 'pawn' class, in order to save position and make harder operations
 * 
 * @author G. Carrino, M. Vannucchi
 */
public class Piece {

        // Current piece position
        Position position;

        // The piece type, can be "W", "B" or "K"
        Pawn kind;

        public Piece(Pawn kind, Position position) {
            // Piece should not be empty pawn or throne
            if (!kind.equals(Pawn.EMPTY) && !kind.equals(Pawn.THRONE))
                this.kind = kind;
            else
                throw new IllegalStateException("Piece must be checker or king.");

            this.position = position;
        }

        public String toString(){
            return kind.toString() + "(" + position.toString() + ")";
        }

        /**
         *
         * @return Return the turn type of the player of the given piece.
         */
        public State.Turn getPiecePlayer(){
            if (this.kind.equals(Pawn.WHITE) || this.kind.equals(Pawn.KING)) {
                return State.Turn.WHITE;
            } else if (this.kind.equals(Pawn.BLACK)) {
                return State.Turn.BLACK;
            } else {
                return State.Turn.DRAW;
            }
        }

        /**
         *
         * @return Return a list of the possible action that the piece can do in the current state of the board
         */
        public List<Action> getValidActions(CompleteState state) {
            List<Position> possibleMoves = position.getPossibleMoves();
            String startingBox = state.getBox(position);

            List<Action> validActions = new ArrayList<>();
            for (Position toPosition: possibleMoves) {
                try {
                    String toBox = state.getBox(toPosition);
                    Action action = new Action(startingBox, toBox, state.getTurn());
                    if (state.isActionValid(action))
                        validActions.add(action);
                } catch (Exception ignored) {
                }
            }

            return validActions;
        }
}
