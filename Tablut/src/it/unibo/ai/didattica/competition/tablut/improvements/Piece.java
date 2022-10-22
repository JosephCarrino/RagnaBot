package it.unibo.ai.didattica.competition.tablut.improvements;


import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class Piece {
    
    public class Position {
        public int x, y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

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

        Position position = new Position(0, 0);
        Pawn kind = Pawn.fromString("0");
        State state;

        public Piece(State s, Pawn kind, int xPos, int yPos) {
            this.state = s;
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
