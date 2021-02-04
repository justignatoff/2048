import java.util.Comparator;

public class MoveEfficiency implements Comparable<MoveEfficiency>{
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    public int getNumberOfEmptyTiles() {
        return numberOfEmptyTiles;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        return Comparator.comparingInt(MoveEfficiency::getNumberOfEmptyTiles).thenComparingInt(MoveEfficiency::getScore).compare(this, o);
    }
}
