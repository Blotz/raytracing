package org.openjfx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    public static final int imageWidth = 256;
    public static final int imageHeight = 256;

    @FXML
    private ImageView imageView;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = null;
        
        root = FXMLLoader.load(getClass().getResource("fxml/scene.fxml"));
        Image image = new Image(getClass().getResource("image.jpg").toString(), imageWidth, imageHeight, true, true);
        // look up ImageView
        imageView = (ImageView) root.lookup("#imageView");
        // set image to ImageView
        imageView.setImage(image);
        
        // Create Scene
        Scene scene = new Scene(root);
        // Set Scene to stage
        stage.setScene(scene);
        // Set title to Stage
        stage.setTitle("photoshop");
        // Display Stage
        stage.show();
    }

    @FXML
    public void render(ActionEvent event) {
        System.out.println("Starting main render loop...");
        // generate a writable image
        Image rimage = imageView.getImage(); // Read only copy
        WritableImage image = new WritableImage(rimage.getPixelReader(), (int) rimage.getWidth(), (int) rimage.getHeight());
        // call render function
        Render.render(image);
        // set image to ImageView
        imageView.setImage(image);
    }
    
    public static void main(String[] args) {
        launch();
    }
}

