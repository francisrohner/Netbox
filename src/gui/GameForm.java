package gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.Duration;

public class GameForm extends Application {

    private final static int CANVAS_WIDTH = 500;
    private final static int CANVAS_HEIGHT = 500;
    private final static double MOVE_AMOUNT = 0.5;
    private Image image;
    private KeyHandler keyHandler;
    public Timer timer;
    private GraphicsContext gc;
    private double imageX, imageY;
    private char direction;

    public void setDirection(char direction) { this.direction = direction; }
    public char getDirection() { return direction; }
    @Override
    public void start(final Stage primaryStage) {
        UpdateTask updateTask = new UpdateTask(this);
        timer = FxTimer.runPeriodically(
                Duration.ofMillis(1),
                updateTask);
        final Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        initDraw(graphicsContext);

        keyHandler = new KeyHandler(this);
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, keyHandler);

        Group root = new Group();
        VBox vBox = new VBox();
        vBox.getChildren().addAll(canvas);
        root.getChildren().add(vBox);
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setTitle("Game Form");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.exit(0);
            }
        });

        //timer.schedule(new UpdateTask(this), 100);

    }
    public void kill()
    {
        System.exit(0);
    }
    public static void main(String[] args) {
        launch(args);
    }

    public void update()
    {
        gc.clearRect(0,0, CANVAS_WIDTH, CANVAS_HEIGHT);
        gc.setFill(Color.LIGHTGRAY);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);

        gc.fill();
        gc.strokeRect(
                0,              //x of the upper left corner
                0,              //y of the upper left corner
                CANVAS_WIDTH,    //width of the rectangle
                CANVAS_HEIGHT);  //height of the rectangle

        gc.setLineWidth(1);
        if(direction == 'E')
            imageX +=+ MOVE_AMOUNT;
        else if(direction == 'W')
            imageX -= MOVE_AMOUNT;
        else if(direction == 'N')
            imageY -= MOVE_AMOUNT;
        else if(direction == 'S')
            imageY += MOVE_AMOUNT;
        gc.drawImage(image, imageX, imageY, CANVAS_WIDTH / 3, CANVAS_HEIGHT / 3);
    }

    private void initDraw(GraphicsContext gc){
        this.gc = gc;
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        gc.setFill(Color.LIGHTGRAY);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);

        gc.fill();
        gc.strokeRect(
                0,              //x of the upper left corner
                0,              //y of the upper left corner
                canvasWidth,    //width of the rectangle
                canvasHeight);  //height of the rectangle

        gc.setLineWidth(1);

        try {
            image = new Image(new FileInputStream("duke.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageX = 250;
        imageY = 250;
    }

}