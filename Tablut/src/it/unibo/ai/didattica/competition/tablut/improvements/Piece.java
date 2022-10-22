package it.unibo.ai.didattica.competition.tablut.improvements;


import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

/**
 * Improvement of 'pawn' class, in order to save position and make harder operations
 * 
 * @author G. Carrino, M. Vannucchi
 */
public class Piece {
    
    // Class for coordinates of a piece
    public class Position {
        public int x, y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /***
         * 
         * @return An array of all reachable Positions starting from the current
         */
        public Position[] getPossibleMoves() {
            Position[] possibleTo = new Position[16];
            int stateX = this.x;
            int stateY = this.y;
    
            for (int i = 0; i < 9 ; i++){
                if (i != stateY)
                    possibleTo[i] = new Position(stateX, i);
            }
            for (int i = 0; i < 9; i++){
                if (i != stateX)
                    possibleTo[i+8] = new Position(i, stateY); 
            }

            return possibleTo;
        }

        public String toString(){
            return this.x + ", " + this.y;
        }
    }

        // Current piece position
        Position position = new Position(0, 0);

        // The piece type, can be "W", "B" or "K"
        Pawn kind = Pawn.fromString("0");

        // The current game state
        State state;

        public Piece(State s, Pawn kind, int xPos, int yPos) {
            this.state = s;

            // Piece should not be empty pawn or throne
            if ((kind.toString() != "0") && (kind.toString() != "T"))
                this.kind = kind;
            else
                throw new IllegalStateException("Piece must be checker or king.");
            
                this.position = new Position(xPos, yPos);
        }

        public String toString(){
            return kind.toString() + position.toString();
        }
    
}
