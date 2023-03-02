package org.openjfx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.Parent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JavaFX App
 */
public class App extends Application {
    // image
    public static double aspectRatio = 16.0 / 9.0;
    public static int imageWidth = 1000;
    public static int imageHeight = (int) (imageWidth / aspectRatio);
    
    // Render
    public static Vec origin = new Vec(0f, 0f, 0f);
    
    // Camara
    private static Vec camPosition = new Vec(0, 0, 0);
    private static Vec camRotation = new Vec(0, 0, 0);

    // GUI
    @FXML private Button cancelButton;
    @FXML private ImageView imageView;
    
    @FXML private HBox sceneSettings;
    @FXML private HBox renderSettings;
    @FXML private HBox cameraSettings;
    @FXML private HBox sphereSettings;
    
    
    // Scene settings
    private int sceneSelected = 1;
    private HittableList scene0 = new HittableList();
    @FXML private RadioButton sceneButton1;
    private HittableList scene1 = new HittableList();
    @FXML private RadioButton sceneButton2;
    private HittableList scene2 = new HittableList();
    {
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
        Sphere s3 = new Sphere(
          new Vec(0,200.5,-1),
          100,
          new DiffuseLight(new Vec(1,1,1))
        );
        Sphere s4 = new Sphere(
          new Vec(0,3,-3.5),
          1,
          new DiffuseLight(new Vec(10,10,10))
        );
        scene2.add(s1);
        scene2.add(s2);
//        scene2.add(s3);
        scene2.add(s4);
    }
    @FXML private RadioButton sceneButton3;
    private HittableList scene3 = new HittableList();
    private HittableList[] scenes = {scene0, scene1, scene2, scene3};
    
    // Render settings
    @FXML private TextField numPasses;
    @FXML private TextField samplesPerPixel;
    @FXML private TextField maxDepth;
    @FXML private TextField imageWidthField;
    @FXML private TextField imageHeightField;
    
    
    // Sphere customisation
    @FXML private ComboBox<Hittable> sphereSelect;
    private Sphere sphereSelected = null;
    @FXML private ComboBox<Material> materialSelect;
    // colors
    @FXML private Slider rValue;
    @FXML private Slider gValue;
    @FXML private Slider bValue;
    // position
    @FXML private TextField xSphere;
    @FXML private TextField ySphere;
    @FXML private TextField zSphere;
    @FXML private TextField radius;
    
    // Camara
    @FXML private TextField xCam;
    @FXML private TextField yCam;
    @FXML private TextField zCam;
    @FXML private TextField pitch;
    @FXML private TextField yaw;
    @FXML private TextField roll;
    
    private static final String emptyError = new String("%s cannot be empty%n");
    private static final String integerValueError = new String("%s has to be an integer%n");
    private static final String doubleValueError = new String("%s has to be a decimal number%n");
    private static final String lessThanZeroError = new String("%s has to be greater than 0%n");
    
    private volatile int[] pixels = new int[imageWidth * imageHeight];
    private static Thread renderThread;
    
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/UI.fxml"));
        
        // Create Scene
        Scene scene = new Scene(root);
        // Set Scene to stage
        stage.setScene(scene);
        // Set title to Stage
        stage.setTitle("photoshop");
        // Display Stage
        stage.show();
    }
    
    @FXML public void render() {
        // Load ui settings
        // Scene
        HittableList world = scenes[sceneSelected];
        // Render
        int numPasses = parseInt("Number of passes", this.numPasses);
        int samplesPerPixel = parseInt("Samples per pixel", this.samplesPerPixel);
        int maxDepth = parseInt("Max depth", this.maxDepth);
        // resolution
        App.imageWidth = parseInt("Image width", this.imageWidthField);
        App.imageHeight = parseInt("Image height", this.imageHeightField);
        App.aspectRatio = (double) imageWidth / (double) imageHeight;
        
        // Camara
        Vec camPosition = new Vec(
          parseDouble("X camara", this.xCam),
          parseDouble("Y camara", this.yCam),
          parseDouble("Z camara", this.zCam)
        );
        Vec camRotation = new Vec(
          Math.toRadians(parseDouble("Pitch", this.pitch)),
          Math.toRadians(parseDouble("Yaw", this.yaw)),
          Math.toRadians(parseDouble("Roll", this.roll))
        );
        // Sphere
        selectSphere();
        
        // Setup image
        imageView.setFitWidth(imageWidth);
        imageView.setFitHeight(imageHeight);
        // display image
        WritableImage image = new WritableImage(imageWidth, imageHeight);
        PixelWriter imageWriter = image.getPixelWriter();
        // array
        pixels = new int[imageWidth * imageHeight];
        
        
        // Animation loop to update image
        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.millis(500), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateImage(pixels, imageWriter, image);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        
        
        // See if render is already running
        cancelRender();
        
        // Setup render thread
        App.renderThread = new Thread(new RenderThread(
          timeline,
          pixels,
          world,
          camPosition, camRotation,
          numPasses, samplesPerPixel, maxDepth,
          0, imageWidth, 0, imageHeight
          ));
    
        // Start render
        cancelButton.setDisable(false);
        updateImage(pixels, imageWriter, image);
        renderThread.start();
        timeline.play();
    }
    
    @FXML public void cancelRender() {
        if (renderThread != null && renderThread.isAlive()) {
            // stop render
            System.out.println("Killing old render");
            renderThread.interrupt();
            renderThread.stop();  // interrupt() doesn't always work
            if (renderThread.isAlive()) {
                System.out.println("Failed to kill render");
            }
            cancelButton.setDisable(true);
        }
    }
    private void updateImage(int[] pixelData, PixelWriter pixelWriter, Image image) {
        pixelWriter.setPixels(
          0, 0, imageWidth, imageHeight,
          PixelFormat.getIntArgbInstance(),
          pixelData,
          0, imageWidth);
        this.imageView.setImage(image);
    }
    // UI function buttons
    @FXML public void sceneSettings() {
        System.out.println("Scene settings");
        this.sceneSettings.setVisible(true);
        this.renderSettings.setVisible(false);
        this.cameraSettings.setVisible(false);
        this.sphereSettings.setVisible(false);
    }
    @FXML public void renderSettings() {
        System.out.println("Render settings");
        this.sceneSettings.setVisible(false);
        this.renderSettings.setVisible(true);
        this.cameraSettings.setVisible(false);
        this.sphereSettings.setVisible(false);
    }
    @FXML public void cameraSettings() {
        System.out.println("Camara settings");
        this.sceneSettings.setVisible(false);
        this.renderSettings.setVisible(false);
        this.cameraSettings.setVisible(true);
        this.sphereSettings.setVisible(false);
    }
    @FXML public void sphereSettings() {
        System.out.println("Sphere settings");
        this.sceneSettings.setVisible(false);
        this.renderSettings.setVisible(false);
        this.cameraSettings.setVisible(false);
        this.sphereSettings.setVisible(true);
    }
    
    // Scene Settings
    @FXML public void selectScene1() {
        System.out.println("Scene 1");
        if (!sceneButton1.isSelected()) {
            sceneButton1.setSelected(true);
            return;
        }
        sceneSelected = 1;
        sceneButton2.setSelected(false);
        sceneButton3.setSelected(false);
    }
    @FXML public void selectScene2() {
        System.out.println("Scene 2");
        if (!sceneButton2.isSelected()) {
            sceneButton2.setSelected(true);
            return;
        }
        sceneSelected = 2;
        sceneButton1.setSelected(false);
        sceneButton3.setSelected(false);
    }
    @FXML public void selectScene3() {
        System.out.println("Scene 3");
        if (!sceneButton3.isSelected()) {
            sceneButton3.setSelected(true);
            return;
        }
        sceneSelected = 3;
        sceneButton1.setSelected(false);
        sceneButton2.setSelected(false);
    }
    
    // Render Settings
    @FXML public void validNumOfSamples() {
        try {
            parseInt("Number of samples", samplesPerPixel);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validNumOfPasses() {
        try {
            parseInt("Number of passes", numPasses);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validMaxDepth() {
        try {
            parseInt("Max depth", maxDepth);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validImageWidth() {
        try {
            parseInt("Image width", imageWidthField);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validImageHeight() {
        try {
            parseInt("Image height", imageHeightField);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    
    // Camera Settings
    @FXML public void validXCam() {
        try {
            parseDouble("x position", xCam);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validYCam() {
        try {
            parseDouble("y position", yCam);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validZCam() {
        try {
            parseDouble("z position", zCam);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validPitch() {
        try {
            parseDouble("pitch", pitch);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validYaw() {
        try {
            parseDouble("yaw", yaw);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validRoll() {
        try {
            parseDouble("roll", roll);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    
    // Sphere Settings
    @FXML public void validXSphere() {
        try {
            parseDouble("x position", xSphere);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validYSphere() {
        try {
            parseDouble("y position", ySphere);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validZSphere() {
        try {
            parseDouble("z position", zSphere);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validRadius() {
        try {
            parsePostiveDouble("radius", radius);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    // drop down
    @FXML public void getMaterialDropDown() {
        // get all classes that are subclasses of Material
        ArrayList<Material> classes = new ArrayList<Material>();
        classes.add(Material.LAMBERTIAN);
        classes.add(Material.DIFFUSE_LIGHT);
    
        ObservableList<Material> materialList = FXCollections.observableArrayList(classes);
        materialSelect.setItems(materialList);
    }
    @FXML public void getSphereDropDown() {
        ArrayList<Hittable> hittables = this.scenes[sceneSelected].getObjects();
        if (hittables == null) {
            hittables = new ArrayList<Hittable>();
        }
        
        ObservableList<Hittable> hittableList = FXCollections.observableArrayList(hittables);
        sphereSelect.setItems(hittableList);
    }
    // saving
    private void saveSphere(Sphere sphere, Material materialType) {
        if (sphere == null) {
            return;
        }
        try {
            sphere.setCenter(
              new Vec(parseDouble("x position", xSphere),
                parseDouble("y position", ySphere),
                parseDouble("z position", zSphere)
              )
            );
            sphere.setRadius(parsePostiveDouble("radius", radius));
            
            Material material;
            
            if (materialType == null) {
                throw new IllegalArgumentException("No material selected");
            } else if ( materialType instanceof Lambertian ) {
                material = new Lambertian(new Vec(rValue.getValue(), gValue.getValue(), bValue.getValue()));
            } else if ( materialType instanceof DiffuseLight ) {
                material = new DiffuseLight(new Vec(rValue.getValue(), gValue.getValue(), bValue.getValue()));
            } else {
                throw new IllegalArgumentException("Invalid material selected");
            }
            sphere.setMaterial(material);
            
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    // select and delete
    @FXML public void selectMaterial() {
        Material material = materialSelect.getValue();
        if (material == null) {
            return;
        }
        
        if (material instanceof DiffuseLight) {
            rValue.setMax(10);
            gValue.setMax(10);
            bValue.setMax(10);
        } else {
            rValue.setMax(1);
            gValue.setMax(1);
            bValue.setMax(1);
        }
        
        Hittable object = sphereSelect.getValue();
        if (object == null) {
            return;
        }
        if (!(object instanceof Sphere)) {
            return;
        }
        Sphere sphere = (Sphere) object;
        rValue.setValue(sphere.r());
        gValue.setValue(sphere.g());
        bValue.setValue(sphere.b());
    }
    @FXML public void selectSphere() {
        Hittable object = sphereSelect.getValue();
        if (object == null) {
            return;
        }
        if (!(object instanceof Sphere)) {
            return;
        }
        Sphere sphere = (Sphere) object;

        // save old sphere
        if (sphereSelected != null) {
            saveSphere(sphereSelected, materialSelect.getValue());
        }
        
        sphereSelected = sphere;
        // update ui
        selectMaterial();
        xSphere.setText(Double.toString(sphere.getCenter().x()));
        ySphere.setText(Double.toString(sphere.getCenter().y()));
        zSphere.setText(Double.toString(sphere.getCenter().z()));
        radius.setText(Double.toString(sphere.getRadius()));
        materialSelect.setValue(sphere.getMaterial());
    }
    @FXML public void deleteSphere() {
        Hittable object = sphereSelect.getValue();
        if (object == null) {
            return;
        }
        this.scenes[sceneSelected].remove(object);
        getSphereDropDown();
    }
    @FXML public void newSphere() {
        Sphere sphere = new Sphere(new Vec(0, 0, 0), 1, new Lambertian(new Vec(0.5, 0.5, 0.5)));
        this.scenes[sceneSelected].add(sphere);
        getSphereDropDown();
    }
    
    // helper functions
    private static double parseDouble(String name, TextField field) {
        double value = 0;
        String errorMessage = new String();
        boolean hasError = false;
        
        if (field.getText().isEmpty()) {
            errorMessage += String.format(emptyError, name);
            hasError = true;
        }
        
        try {
            value = Double.parseDouble(field.getText());
        } catch (NumberFormatException e) {
            errorMessage += String.format(doubleValueError, name);
            hasError = true;
        }
        
        if (hasError) {
            throw new IllegalArgumentException(errorMessage);
        }
        
        return value;
    }
    private static double parsePostiveDouble(String name, TextField field) {
        double value = 0;
        boolean hasError = false;
        String errorMessage = new String();
        try {
            value = parseDouble(name, field);
        } catch (IllegalArgumentException e) {
            errorMessage += e.getMessage();
        }
        
        if (value <= 0) {
            errorMessage += String.format(lessThanZeroError, name);
            throw new IllegalArgumentException(errorMessage);
        }
        
        if (hasError) {
            throw new IllegalArgumentException(errorMessage);
        }
        
        return value;
    }
    private static int parseInt(String name, TextField field) {
        int value = 0;
        String errorMessage = new String();
        boolean hasError = false;
        
        if (field.getText().isEmpty()) {
            errorMessage += String.format(emptyError, name);
            hasError = true;
        }
        
        try {
            value = Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            errorMessage += String.format(integerValueError, name);
            hasError = true;
        }
        
        if (value < 0) {
            errorMessage += String.format(lessThanZeroError, name);
            hasError = true;
        }
        
        if (hasError) {
            throw new IllegalArgumentException(errorMessage);
        }
        
        return value;
    }
    private void showErrorMessage(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage);
        alert.show();
    }
    
    public static void main(String[] args) {
        launch();
    }
}

