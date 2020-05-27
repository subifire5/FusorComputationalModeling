package org.eastsideprep.javaneutrons;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        var scene = new Scene(new StackPane(label), 640, 480);
        stage.setScene(scene);
        stage.show();
        
        Element e = new Element("Hydrogen", 1, 1, 0);
        try {
            e.fillEntries("125.csv");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(e.getArea(100));
        System.out.println(e.getArea(0.00002));
    }

    public static void main(String[] args) {
        launch();
    }

}