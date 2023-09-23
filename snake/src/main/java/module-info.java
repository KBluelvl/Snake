module game.snake {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens game to javafx.fxml;
    exports game;
}