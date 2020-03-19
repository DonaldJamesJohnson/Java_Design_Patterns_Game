import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TestMap extends Application {

    private final int tileSize = 20;
    private final int numTilesHoriz = 500;
    private final int numTilesVert = 500;

    private final int speed = 400; // pixels / second
    private boolean up ;
    private boolean down ;
    private boolean left ;
    private boolean right ;

    private final int numFilledTiles = numTilesHoriz * numTilesVert / 20 ;  
    
    Pane pane = createBackground();
    Rectangle player = new Rectangle(numTilesHoriz*tileSize/2, numTilesVert*tileSize/2, 20, 20);

    @Override
    public void start(Stage primaryStage) {
    	// Create pane 
        // Create player 
        player.setStroke(Color.BLACK);
        player.setFill(Color.BLUE);
        pane.getChildren().add(player);
        // Create scene
        Scene scene = new Scene(new BorderPane(pane), 800, 800);
        // Set clip for scene
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(scene.widthProperty());
        clip.heightProperty().bind(scene.heightProperty());

        clip.xProperty().bind(Bindings.createDoubleBinding(
                () -> clampRange(player.getX() - scene.getWidth() / 2, 0, pane.getWidth() - scene.getWidth()), 
                player.xProperty(), scene.widthProperty()));
        clip.yProperty().bind(Bindings.createDoubleBinding(
                () -> clampRange(player.getY() - scene.getHeight() / 2, 0, pane.getHeight() - scene.getHeight()), 
                player.yProperty(), scene.heightProperty()));

        pane.setClip(clip);
        pane.translateXProperty().bind(clip.xProperty().multiply(-1));
        pane.translateYProperty().bind(clip.yProperty().multiply(-1));
        
        // Player movement
        scene.setOnKeyPressed(e -> processKey(e.getCode(), true));
        scene.setOnKeyReleased(e -> processKey(e.getCode(), false));

        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = -1 ;
            @Override
            public void handle(long now) {
                long elapsedNanos = now - lastUpdate ;
                if (lastUpdate < 0) {
                    lastUpdate = now ;
                    return ;
                }
                double elapsedSeconds = elapsedNanos / 1_000_000_000.0 ;
                double deltaX = 0 ;
                double deltaY = 0 ;
                if (right) deltaX += speed ;
                if (left) deltaX -= speed ;
                if (down) deltaY += speed ;
                if (up) deltaY -= speed ;
                player.setX(clampRange(player.getX() + deltaX * elapsedSeconds, 0, pane.getWidth() - player.getWidth()));
                player.setY(clampRange(player.getY() + deltaY * elapsedSeconds, 0, pane.getHeight() - player.getHeight()));
                lastUpdate = now ;
            }
        };

        primaryStage.setScene(scene);
        primaryStage.show();

        timer.start();
    }

    private double clampRange(double value, double min, double max) {
        if (value < min) return min ;
        if (value > max) return max ;
        return value ;
    }

    private void processKey(KeyCode code, boolean on) {
        switch (code) {
        case A: 
            left = on ;
            break ;
        case D: 
            right = on ;
            break ;
        case W:
            up = on ;
            break ;
        case S:
            down = on ;
            break ;
        case UP: 
        	shoot(player);
        case LEFT:
        	shoot(player);
        case RIGHT: 
        	shoot(player);
        case DOWN:
        	shoot(player);
        default:
            break ;
        }
    }

    private Pane createBackground() {

        List<Integer> filledTiles = sampleWithoutReplacement(numFilledTiles, numTilesHoriz * numTilesVert);

        Canvas canvas = new Canvas(numTilesHoriz * tileSize, numTilesVert * tileSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.DIMGRAY);

        Pane pane = new Pane(canvas);

        pane.setMinSize(numTilesHoriz * tileSize, numTilesVert * tileSize);
        pane.setPrefSize(numTilesHoriz * tileSize, numTilesVert * tileSize);
        pane.setMaxSize(numTilesHoriz * tileSize, numTilesVert * tileSize);

        for (Integer tile : filledTiles) {
            int x = (tile % numTilesHoriz) * tileSize ;
            int y = (tile / numTilesHoriz) * tileSize ;
            gc.fillRect(x, y, tileSize, tileSize);
        }

        return pane ;
    }

    private List<Integer> sampleWithoutReplacement(int sampleSize, int populationSize) {
        Random rng = new Random();
        List<Integer> population = new ArrayList<>();
        for (int i = 0 ; i < populationSize; i++) 
            population.add(i);
        List<Integer> sample = new ArrayList<>();
        for (int i = 0 ; i < sampleSize ; i++) 
            sample.add(population.remove(rng.nextInt(population.size())));
        return sample;
    }

    private void shoot(Rectangle who)
    {
    	Rectangle bullet = new Rectangle(who.getTranslateX() + 20, who.getTranslateY(), 5, 20);
    	bullet.setFill(Color.BLACK);
    	pane.getChildren().add(bullet);
    }
    public static void main(String[] args) {
        launch(args);
    }
}