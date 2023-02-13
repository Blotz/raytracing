package org.openjfx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
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
    // image
    public static final  double aspectRatio = 16.0 / 9.0;
    public static final int imageWidth = 400;
    public static final int imageHeight = (int) (imageWidth / aspectRatio);

    @FXML
    private ImageView imageView;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/scene.fxml"));
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
        WritableImage rwimage = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = rwimage.getPixelWriter();
        // call render function
        Render.render(pixelWriter);
        // set image to ImageView
        imageView.setImage(rwimage);
        imageView.setFitWidth(imageWidth);
        imageView.setFitHeight(imageHeight);
    }
    
    public static void main(String[] args) {
        launch();
    }
}

