package it.unibo.ai.didattica.competition.tablut.domain;

import java.util.ArrayList;

/**
 * 
 * @author G. Carrino, M. Vannucchi
 */
public class StateUlt extends State {
    
    public class Position {
        public int x, y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }


        public Position[] getPossibleMoves(Position from) {
            Position[] possibleTo = new Position[16];
            int stateX = from.x;
            int stateY = from.y;
    
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

    

}
