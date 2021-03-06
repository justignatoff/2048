import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {
    private Model model;
    private View view;
    private final static int WINNING_TILE = 2048;

    public Controller(Model model) {
        this.model = model;
        view = new View(this);
    }

    void resetGame() {
        model.score = 0;
        view.isGameWon = view.isGameLost = false;
        model.resetGameTiles();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            resetGame();
        }

        if (e.getKeyCode() == KeyEvent.VK_Z) {
            model.rollback();
        }

        if (e.getKeyCode() == KeyEvent.VK_R) {
            model.randomMove();
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            model.autoMove();
        }

        if (!model.canMove()) {
            view.isGameLost = true;
        }

        if (view.isGameLost == false && view.isGameWon == false) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    model.up();
                    break;
                case KeyEvent.VK_DOWN:
                    model.down();
                    break;
                case KeyEvent.VK_LEFT:
                    model.left();
                    break;
                case KeyEvent.VK_RIGHT:
                    model.right();
                    break;
            }
        }

        if (model.maxTile == WINNING_TILE) {
            view.isGameWon = true;
        }

        view.repaint();
    }

    public Tile[][] getGameTiles() {
        return model.getGameTiles();
    }

    public int getScore() {
        return model.score;
    }

    public View getView() {
        return view;
    }
}
