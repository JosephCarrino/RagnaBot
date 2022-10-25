package it.unibo.ai.didattica.competition.tablut.improvements;


import java.util.ArrayList;
import java.util.List;

/**
 * Class for coordinates of a piece
 *
 * @author G. Carrino, M. Vannucchi
 */
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
    public List<Position> getPossibleMoves() {
        int stateX = this.x;
        int stateY = this.y;

        List<Position> possibleTo = new ArrayList<>();


        for (int i = 0; i < 9 ; i++){
            if (i != stateY)
                possibleTo.add(new Position(stateX, i));
        }
        for (int i = 0; i < 9; i++){
            if (i != stateX)
                possibleTo.add(new Position(i, stateY));
        }

        return possibleTo;
    }

    public String toString(){
        return this.x + ", " + this.y;
    }
}
