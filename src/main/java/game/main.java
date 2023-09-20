package game;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
public class main extends Application {
    private static final int WIDTH = 660;
    private static final int HEIGHT = WIDTH;
    private static final int ROWS = 20;
    private static final int COLUMNS = ROWS;
    private static final int SQUARE_SIZE = WIDTH / ROWS;
    private static Canvas canvas = new Canvas(WIDTH, HEIGHT);
    private static GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    private static List<Point> snakeBody = new ArrayList();
    private static Point snakeHead = new Point(10, 10);
    private static KeyCode currentDirection = KeyCode.UP;
    private static int foodX;
    private static int foodY;
    private int score = 0;
    private boolean move = true;

    @Override
    public void start(Stage stage) {
        stage.setTitle("snake");
        VBox root = new VBox();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyCode code = keyEvent.getCode();
                if (move) {
                    if (code == KeyCode.UP) {
                        if (currentDirection != KeyCode.DOWN) {
                            currentDirection = KeyCode.UP;
                        }
                    } else if (code == KeyCode.DOWN) {
                        if (currentDirection != KeyCode.UP) {
                            currentDirection = KeyCode.DOWN;
                        }
                    } else if (code == KeyCode.LEFT) {
                        if (currentDirection != KeyCode.RIGHT) {
                            currentDirection = KeyCode.LEFT;
                        }
                    } else if (code == KeyCode.RIGHT) {
                        if (currentDirection != KeyCode.LEFT) {
                            currentDirection = KeyCode.RIGHT;
                        }
                    }
                    move = false;
                    mouvementDelay();
                }
            }
        });
        snakeBody.add(snakeHead);
        generateFood();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(150), e -> run()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void run() {
        if (!isGameOver()) {
            drawBackground();
            drawSnake();
            drawFood();
            drawScore();
            for (int i = snakeBody.size() - 1; i >= 1; i--) {
                snakeBody.get(i).x = snakeBody.get(i - 1).x;
                snakeBody.get(i).y = snakeBody.get(i - 1).y;
            }
            switch (currentDirection) {
                case LEFT -> moveLeft();
                case RIGHT -> moveRight();
                case UP -> moveUp();
                case DOWN -> moveDown();
            }
            outOfBound();
            eatFood();
        }
    }

    private void moveUp() {
        snakeHead.y--;
    }

    private void moveDown() {
        snakeHead.y++;
    }

    private void moveLeft() {
        snakeHead.x--;
    }

    private void moveRight() {
        snakeHead.x++;
    }

    private void outOfBound() {
        if (snakeHead.x < 0) {
            snakeHead.x = ROWS;
        } else if (snakeHead.x >= ROWS) {
            snakeHead.x = 0;
        } else if (snakeHead.y < 0) {
            snakeHead.y = COLUMNS;
        } else if (snakeHead.y >= COLUMNS) {
            snakeHead.y = 0;
        }
    }

    private void drawBackground() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if ((i + j) % 2 == 0) {
                    graphicsContext.setFill(Color.DARKGREEN);
                } else {
                    graphicsContext.setFill(Color.GREEN);
                }
                graphicsContext.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private void drawSnake() {
        // draw head
        graphicsContext.setFill(Color.DARKCYAN);
        graphicsContext.fillRoundRect(snakeHead.x * SQUARE_SIZE, snakeHead.y * SQUARE_SIZE,
                SQUARE_SIZE - 1, SQUARE_SIZE - 1, 20, 20);
        // draw eyes
        drawEyes();
        graphicsContext.setFill(Color.BLUE);
        // draw body
        for (Point point : snakeBody) {
            if (!point.equals(snakeHead)) {
                graphicsContext.fillRect(point.x * SQUARE_SIZE, point.y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private void drawEyes() {
        graphicsContext.setFill(Color.WHITE);
        if (currentDirection == KeyCode.UP) {
            graphicsContext.fillRoundRect(snakeHead.x * SQUARE_SIZE + 1, snakeHead.y * SQUARE_SIZE,
                    SQUARE_SIZE / 2 - 2, SQUARE_SIZE / 2 - 2, 10, 10);
            graphicsContext.fillRoundRect((snakeHead.x + 0.5) * SQUARE_SIZE, snakeHead.y * SQUARE_SIZE,
                    SQUARE_SIZE / 2 - 2, SQUARE_SIZE / 2 - 2, 10, 10);
        } else if (currentDirection == KeyCode.DOWN) {
            graphicsContext.fillRoundRect(snakeHead.x * SQUARE_SIZE + 1, (snakeHead.y + 0.5) * SQUARE_SIZE,
                    SQUARE_SIZE / 2 - 2, SQUARE_SIZE / 2 - 2, 10, 10);
            graphicsContext.fillRoundRect((snakeHead.x + 0.5) * SQUARE_SIZE, (snakeHead.y + 0.5) * SQUARE_SIZE,
                    SQUARE_SIZE / 2 - 2, SQUARE_SIZE / 2 - 2, 10, 10);
        } else if (currentDirection == KeyCode.RIGHT) {
            graphicsContext.fillRoundRect((snakeHead.x + 0.5) * SQUARE_SIZE, snakeHead.y * SQUARE_SIZE,
                    SQUARE_SIZE / 2 - 2, SQUARE_SIZE / 2 - 2, 10, 10);
            graphicsContext.fillRoundRect((snakeHead.x + 0.5) * SQUARE_SIZE, (snakeHead.y + 0.5) * SQUARE_SIZE,
                    SQUARE_SIZE / 2 - 2, SQUARE_SIZE / 2 - 2, 10, 10);
        } else if (currentDirection == KeyCode.LEFT) {
            graphicsContext.fillRoundRect(snakeHead.x * SQUARE_SIZE + 1, snakeHead.y * SQUARE_SIZE,
                    SQUARE_SIZE / 2 - 2, SQUARE_SIZE / 2 - 2, 10, 10);
            graphicsContext.fillRoundRect(snakeHead.x * SQUARE_SIZE + 1, (snakeHead.y + 0.5) * SQUARE_SIZE,
                    SQUARE_SIZE / 2 - 2, SQUARE_SIZE / 2 - 2, 10, 10);
        }
    }

    private void drawScore() {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.setFont(new Font(30));
        graphicsContext.fillText("" + score, 50, 50);
    }

    private void drawFood() {
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillArc(foodX * SQUARE_SIZE, foodY * SQUARE_SIZE, SQUARE_SIZE - 2, SQUARE_SIZE - 2, 0, 360, ArcType.OPEN);
    }

    private void generateFood() {
        boolean finish = false;
        while (!finish) {
            foodX = (int) (Math.random() * ROWS);
            foodY = (int) (Math.random() * COLUMNS);

            Point food = new Point(foodX, foodY);

            if (!snakeBody.contains(food)) {
                {
                    finish = true;
                }
            }
        }
    }

    private void eatFood() {
        if (snakeHead.equals(new Point(foodX, foodY))) {
            generateFood();
            snakeBody.add(new Point(-1, -1));
            score++;
        }
    }

    private boolean isGameOver() {
        for (int i = 1; i < snakeBody.size(); i++) {
            if (snakeHead.x == snakeBody.get(i).x && snakeHead.y == snakeBody.get(i).y) {
                return true;
            }
        }
        return false;
    }

    private void mouvementDelay() {
        int durationMillis = 150;
        Timer timer = new Timer(durationMillis, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to execute when timer is done
                ((Timer) e.getSource()).stop(); // stop the timer
                move = true;
            }
        });

        // start the timeline
        timer.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}