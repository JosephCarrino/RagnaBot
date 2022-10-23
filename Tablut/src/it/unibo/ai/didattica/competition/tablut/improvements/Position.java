package it.unibo.ai.didattica.competition.tablut.improvements;


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
