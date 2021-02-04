import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile [][] gameTiles;
    private boolean isSaveNeeded = true;
    int score = 0;
    int maxTile = 0;
    private Stack<Tile[][]>  previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();

    public Model() {
        resetGameTiles();
    }

    private void saveState(Tile[][] tiles) {
        Tile [][] tmpGame = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i ++)
            for (int j = 0; j < FIELD_WIDTH; j ++)
                tmpGame[i][j] = new Tile(tiles[i][j].value);
        previousStates.push(tmpGame);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousStates.isEmpty())
            if (!previousScores.isEmpty()) {
                gameTiles = (Tile[][]) previousStates.pop();
                score = (int) previousScores.pop();
            }
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public boolean canMove() {
        if(!getEmptyTiles().isEmpty())return true;

        for(int i = 0; i < FIELD_WIDTH; i++) {
            for(int j = 0; j < FIELD_WIDTH-1; j++) {
                if (gameTiles[i][j].value == gameTiles[i][j+1].value) return true;
            }
        }

        for(int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH-1; j++) {
                if(gameTiles[j][i].value == gameTiles[j+1][i].value) return true;
            }
        }
        return false;
    }

    public void addTile () {
        List<Tile> list = getEmptyTiles();
        if (!list.isEmpty()) {
            list.get((int) (list.size() * Math.random())).value = (Math.random() < 0.9 ? 2 : 4);
        }

    }

    private List<Tile> getEmptyTiles() {
        List<Tile> emptyList = new ArrayList<>();
        for (int i = 0; i < gameTiles.length; i ++)
            for (int j = 0; j < gameTiles.length; j ++)
                if (gameTiles[i][j].value == 0)
                    emptyList.add(gameTiles[i][j]);
        return emptyList;
    }

    void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < gameTiles.length; i ++)
            for (int j = 0; j < gameTiles.length; j ++)
                gameTiles[i][j] = new Tile();
        addTile();
        addTile();
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean anyChange = false;
        for (int i = 0; i < tiles.length; i ++) {

            if (tiles[i].value == 0) {
                outer:
                for (int j = i+1; j < tiles.length; j++) {
                    if (tiles[j].value != 0) {
                        tiles[i].value = tiles[j].value;
                        tiles[j].value = 0;
                        anyChange = true;
                        break outer;
                    }
                }
            }
        }

        return anyChange;
    }

    private boolean  mergeTiles(Tile[] tiles) {
        boolean change = false;

        for (int i = 0; i < tiles.length; i++) {
            try {

                if (tiles[i].value > 0 && tiles[i].value == tiles[i + 1].value) {

                    tiles[i].value += tiles[i + 1].value;
// Если выполняется условие слияния плиток, проверяем является ли новое значения больше максимального
// и при необходимости меняем значение поля maxTile.
                    if (tiles[i].value > maxTile) {
                        maxTile = tiles[i].value;
                    }
//Увеличиваем значение поля score на величину веса плитки образовавшейся в результате слияния.
                    score += tiles[i].value;
                    change = true;

                    for (int j = i + 1; j < tiles.length; j++) {
                        if (j != tiles.length - 1) {
                            tiles[j].value = tiles[j + 1].value;
                        } else tiles[j].value = 0;
                    }
                }
            }catch (ArrayIndexOutOfBoundsException ex) {
                continue;
            }
        }
        return change;
    }

    public void left() {
        boolean isChange = false;
        if (isSaveNeeded)
            saveState(gameTiles);

        for (int i = 0; i < gameTiles.length; i ++) {
            boolean compress = compressTiles(gameTiles[i]);
            boolean merge = mergeTiles(gameTiles[i]);
            if (compress | merge)
                isChange = true;
        }
        if (isChange)
            addTile();

        isSaveNeeded = true;
    }

    public void up() {
        saveState(gameTiles);
        rotate90();
        left();
        rotate90();
        rotate90();
        rotate90();
    }

    public void right() {
        saveState(gameTiles);
        rotate90();
        rotate90();
        left();
        rotate90();
        rotate90();
    }

    public void down() {
        saveState(gameTiles);
        rotate90();
        rotate90();
        rotate90();
        left();
        rotate90();
    }

    private void rotate90(){
        Tile [][] rotMat = new Tile[FIELD_WIDTH][FIELD_WIDTH];

        for (int rw = 0; rw < gameTiles.length; rw++) {
            for (int cl = 0; cl < gameTiles.length; cl++) {
                rotMat[gameTiles.length - 1 - cl][rw] = gameTiles[rw][cl];
            }
        }
        gameTiles = rotMat;
    }

    public void randomMove() {
        int n = ((int)(Math.random()*100))%4;
        switch (n) {
            case 0 : left(); break;
            case  1 : right(); break;
            case  2 : up(); break;
            case  3 : down(); break;
        }
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue<>(4, Collections.reverseOrder());
        queue.add(getMoveEfficiency(() -> right()));
        queue.add(getMoveEfficiency(() -> up()));
        queue.add(getMoveEfficiency(() -> down()));
        queue.add(getMoveEfficiency(() -> left()));

        queue.peek().getMove().move();
    }

    public boolean hasBoardChanged() {
        Tile[][] lastBoard = previousStates.peek();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (lastBoard[i][j].value != gameTiles[i][j].value) {
                    return true;
                }
            }
        }

        return false;
    }

    public MoveEfficiency getMoveEfficiency(Move move) {
        move.move();
        if (!hasBoardChanged()) {
            rollback();
            return new MoveEfficiency(-1, 0, move);
        }

        int emptyTilesCount = getEmptyTiles().size();
//        for (int i = 0; i < FIELD_WIDTH; i++) {
//            for (int j = 0; j < FIELD_WIDTH; j++) {
//                if (gameTiles[i][j].isEmpty()) {
//                    emptyTilesCount++;
//                }
//            }
//        }

        MoveEfficiency moveEfficiency = new MoveEfficiency(emptyTilesCount, score, move);
        rollback();

        return moveEfficiency;
    }
}
