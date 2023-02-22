package org.openjfx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.Parent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * JavaFX App
 */
public class App extends Application {
    // image
    public static final  double aspectRatio = 16.0 / 9.0;
    public static final int imageWidth = 1000;
    public static final int imageHeight = (int) (imageWidth / aspectRatio);
    
    // Render
    public static Vec origin = new Vec(0f, 0f, 0f);
    public static int samplesPerPixel = 100;
    public static int maxDepth = 50;
    
    // Camara
    private static Vec camPosition = new Vec(0, 0, 0);
    private static Vec camRotation = new Vec(0, 0, 0);
    
    // World
    public static HittableList world = new HittableList();
    static {
        Sphere s1 = new Sphere(
            new Vec(0,0,-4),
            0.5,
            new Lambertian(new Vec(0.1, 0.2, 0.5))
        );
        Sphere s2 = new Sphere(
            new Vec(0,-100.5,-1),
            100,
            new Lambertian(new Vec(0.8, 0.8, 0.0))
        );
        world.add(s1);
        world.add(s2);
    }

    // GUI
    @FXML
    private ImageView imageView;
    
    // Sphere customisation
    @FXML
    private ComboBox sphereSelect;
    @FXML
    private Slider rValue;
    @FXML
    private Slider gValue;
    @FXML
    private Slider bValue;
    @FXML
    private TextField xSphere;
    @FXML
    private TextField ySphere;
    @FXML
    private TextField zSphere;
    @FXML
    private TextField rSphere;
    
    // Camara
    @FXML
    private TextField xCamara;
    @FXML
    private TextField yCamara;
    @FXML
    private TextField zCamara;
    @FXML
    private TextField pitchCamara;
    @FXML
    private TextField yawCamara;
    @FXML
    private TextField rollCamara;
    
    private boolean hasError = false;
    private String errorMessage = new String();
    private static final String doubleValueError = new String("%s has to be a decimal number");
    private static final String lessThanZeroError = new String("%s has to be greater than 0");
    private Hittable objectSelected = null;
    
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
    public void renderOnEnter(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            App.samplesPerPixel = 5;
            App.maxDepth = 3;
            render();
        }
    }
    @FXML
    public void renderOnButton(ActionEvent event) {
        App.samplesPerPixel = 100;
        App.maxDepth = 50;
        render();
    }
    public void render() {
        // System.out.println("Getting camara position");
        // Save settings
        saveSphere();
        saveCamara();
    
        if (hasError) {
            showErrorMessage();
            return;
        }
        Parent root = imageView.getParent();
        root.setDisable(true);
        
        // System.out.println("Starting main render loop...");
        // generate a writable image
        WritableImage rwimage = new WritableImage(imageWidth, imageHeight);
        
        int[] pixelData = new int[imageWidth * imageHeight];
        WritableImage copyImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter copyPixelWriter = copyImage.getPixelWriter();
    
        // set image to ImageView
        imageView.setImage(rwimage);
        imageView.setFitWidth(imageWidth);
        imageView.setFitHeight(imageHeight);
        
        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.millis(100), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateImage(pixelData, copyPixelWriter, copyImage);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        
        Render render = new Render(world, camPosition, camRotation);
    
        Thread thread = new Thread(() -> {
            render.render(pixelData, 0, imageWidth, 0, imageHeight);
            timeline.stop();
            updateImage(pixelData, copyPixelWriter, copyImage);
            root.setDisable(false);
        });
        updateImage(pixelData, copyPixelWriter, copyImage);
        thread.start();
        timeline.play();
    }
    
    /**
     * Updates the image
     * @param pixelData A temporary array to store the pixel data
     * @param pixelWriter The target image writer
     * @param rwimage The target image
     */
    private void updateImage(int[] pixelData, PixelWriter pixelWriter, WritableImage rwimage) {
        pixelWriter.setPixels(0, 0, imageWidth, imageHeight, PixelFormat.getIntArgbInstance(), pixelData, 0, imageWidth);
        imageView.setImage(rwimage);
    }
    
    @FXML
    public void getSphereDropDown() {
        // Generate new list
        ArrayList<Hittable> options = App.world.getObjects();
        // Create dropdown with each sphere being enumerated
        // And each option points to the sphere object
        ObservableList<Hittable> optionsList = FXCollections.observableArrayList(options);
        
        sphereSelect.setItems(optionsList);
    }
    @FXML
    public void selectSphere() {
        Hittable newObjectSelected = (Hittable) sphereSelect.getValue();
        if (newObjectSelected == null) {
            xSphere.setText("0");
            ySphere.setText("0");
            zSphere.setText("0");
            rSphere.setText("0");
    
            rValue.setValue(0f);
            gValue.setValue(0f);
            bValue.setValue(0f);
            return;
        }
        
        // save old sphere
        // The sphere has been changed and isnt null
        if (!newObjectSelected.equals(objectSelected)) {
            System.out.println(String.format("%s -> %s, equal: %s",
              objectSelected,
              newObjectSelected,
              newObjectSelected.equals(objectSelected)
            ));
            
            
            if (objectSelected instanceof Sphere) {
                Sphere sphere = (Sphere) objectSelected;
                parseSphere(sphere);
            }
            objectSelected = newObjectSelected;
        }
        
        // update ui
        if (objectSelected instanceof Sphere) {
            Sphere sphere = (Sphere) objectSelected;
            xSphere.setText(removeTrailingZeros(String.format("%f", sphere.center.x())));
            ySphere.setText(removeTrailingZeros(String.format("%f", sphere.center.y())));
            zSphere.setText(removeTrailingZeros(String.format("%f", sphere.center.z())));
            rSphere.setText(removeTrailingZeros(String.format("%f", sphere.radius)));
            
             rValue.setValue(sphere.r() * 255);
             gValue.setValue(sphere.g() * 255);
             bValue.setValue(sphere.b() * 255);
        }
        
    }
    @FXML
    public void addSphere(ActionEvent event) {
        // save old sphere
        if (objectSelected instanceof Sphere) {
            Sphere sphere = (Sphere) objectSelected;
            parseSphere(sphere);
        }
        
        // add new sphere
        Sphere sphere = new Sphere(
          new Vec(0, 0, 0),
          0,
          new Lambertian(new Vec(0, 0, 0))
        );
        App.world.add(sphere);
        
        // update ui
        getSphereDropDown();
        sphereSelect.setValue(sphere);
        selectSphere();
        
    }
    @FXML
    public void removeSphere(ActionEvent event) {
        // delete sphere
        if (objectSelected == null) {
            return;
        }
        if (objectSelected instanceof Sphere) {
            Sphere sphere = (Sphere) objectSelected;
            App.world.remove(sphere);
        }
        
        // update ui
        objectSelected = null;
        sphereSelect.setValue(null);
        getSphereDropDown();
    }
    public void saveSphere() {
        Hittable objectSelected = (Hittable) sphereSelect.getValue();
        if (objectSelected == null) {
            return;
        }
        
        if (objectSelected instanceof Sphere) {
            Sphere sphere = (Sphere) objectSelected;
            parseSphere(sphere);
        }
    }
    private void parseSphere(Sphere sphere) {
        double xPos = 0f;
        double yPos = 0f;
        double zPos = 0f;
        
        double radius = 0f;
        
        double r = 0f;
        double g = 0f;
        double b = 0f;
        
        try {
            xPos = Double.parseDouble(this.xSphere.getText());
        } catch(NumberFormatException e) {
            errorMessage(String.format(doubleValueError, "Sphere X position"));
        }
        try {
            yPos = Double.parseDouble(this.ySphere.getText());
        } catch(NumberFormatException e) {
            errorMessage(String.format(doubleValueError, "Sphere Y position"));
        }
        try {
            zPos = Double.parseDouble(this.zSphere.getText());
        } catch(NumberFormatException e) {
            errorMessage(String.format(doubleValueError, "Sphere Z position"));
        }
        try {
            radius = Double.parseDouble(this.rSphere.getText());
            if (radius < 0) {
                throw new IllegalArgumentException();
            }
        } catch(NumberFormatException e) {
            errorMessage(String.format(doubleValueError, "Sphere radius"));
        } catch (IllegalArgumentException e) {
            errorMessage(String.format(lessThanZeroError, "Sphere radius"));
        }
        
        if (hasError) {
            return;
        }
        
        r = this.rValue.getValue();
        g = this.gValue.getValue();
        b = this.bValue.getValue();
        
        // Convert to absorbion
        r = r / 255f;
        g = g / 255f;
        b = b / 255f;
        
        sphere.setCenter(new Vec(xPos, yPos, zPos));
        sphere.setRadius(radius);
        sphere.setColor(r, g, b);
    }
    
    
    public void saveCamara() {
        parseCamara();
    }
    private void parseCamara() {
        double xPos = 0f;
        double yPos = 0f;
        double zPos = 0f;
        
        double pitch = 0f;
        double yaw   = 0f;
        double roll  = 0f;
        
        try {
            xPos = Double.parseDouble(this.xCamara.getText());
        } catch(NumberFormatException e) {
            errorMessage(String.format(doubleValueError, "Camara X position"));
        }
        try {
            yPos = Double.parseDouble(this.yCamara.getText());
        } catch(NumberFormatException e) {
            errorMessage(String.format(doubleValueError, "Camara Y position"));
        }
        try {
            zPos = Double.parseDouble(this.zCamara.getText());
        } catch(NumberFormatException e) {
            errorMessage(String.format(doubleValueError, "Camara Z position"));
        }
        try {
            pitch = Double.parseDouble(this.pitchCamara.getText());
        } catch(NumberFormatException e) {
            errorMessage(String.format(doubleValueError, "Camara pitch"));
        }
        try {
            yaw = Double.parseDouble(this.yawCamara.getText());
        } catch(NumberFormatException e) {
            errorMessage(String.format(doubleValueError, "Camara yaw"));
        }
        try {
            roll = Double.parseDouble(this.rollCamara.getText());
        } catch(NumberFormatException e) {
            errorMessage(String.format(doubleValueError, "Camara roll"));
        }
        
        if (hasError) {
            return;
        }
        
        // convert to radians
        pitch = Math.toRadians(pitch);
        yaw   = Math.toRadians(yaw);
        roll  = Math.toRadians(roll);
        
        App.camPosition = new Vec(xPos, yPos, zPos);
        App.camRotation = new Vec(pitch, yaw, roll);
    }
    
    private static String removeTrailingZeros(String value) {
        if (value.indexOf('.') < 0) {
            return value;
        }
        return value.replaceAll("0*$", "").replaceAll("\\.$", "");
    }
    
    private void showErrorMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage);
        alert.show();
        hasError = false;
        errorMessage = new String();
    }
    private void errorMessage(String message) {
        hasError = true;
        errorMessage += String.format("%s%n", message);
    }
    
    public static void main(String[] args) {
        launch();
    }
}

