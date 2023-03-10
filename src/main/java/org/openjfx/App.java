package org.openjfx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.io.File;
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
    
    // General settings
    @FXML private HBox sceneSettings;
    @FXML private HBox renderSettings;
    @FXML private HBox cameraSettings;
    @FXML private HBox sphereSettings;
    
    // Scene settings
    private int sceneSelected = 1;
    private HittableList scene0 = new HittableList(); // empty scene
    @FXML private RadioButton sceneButton1;
    private HittableList scene1 = new HittableList();
    {
        // Cornell box
        // left
        Sphere s1 = new Sphere(new Vec(1e5+1, 40.8, 81.6), 1e5, new Lambertian(new Vec(0.75, 0.25, 0.25)));
        // right
        Sphere s2 = new Sphere(new Vec(-1e5+99, 40.8, 81.6), 1e5, new Lambertian(new Vec(0.25, 0.25, 0.75)));
        // back
        Sphere s3 = new Sphere(new Vec(50, 40.8, 1e5), 1e5, new Lambertian(new Vec(0.75, 0.75, 0.75)));
        // front
        Sphere s4 = new Sphere(new Vec(50, 40.8, -1e5+170), 1e5, new Lambertian(new Vec(0.0, 0.0, 0.0)));
        // bottom
        Sphere s5 = new Sphere(new Vec(50, 1e5, 81.6), 1e5, new Lambertian(new Vec(0.75, 0.75, 0.75)));
        // top
        Sphere s6 = new Sphere(new Vec(50, -1e5+81.6, 81.6), 1e5, new Lambertian(new Vec(0.75, 0.75, 0.75)));
        
        Sphere s7 = new Sphere(new Vec(27, 16.5, 47), 16.5, new Metal(new Vec(0.8, 0.8, 0.9), 0.1));
        Sphere s8 = new Sphere(new Vec(73, 16.5, 78), 16.5, new Dielectric(new Vec(1,1,1),1.5));
        Sphere s9 = new Sphere(new Vec(50, 681.6-.27, 81.6), 600, new DiffuseLight(new Vec(12, 12, 12)));
        
        
        scene1.add(s1);
        scene1.add(s2);
        scene1.add(s3);
        scene1.add(s4);
        scene1.add(s5);
        scene1.add(s6);
        scene1.add(s7);
        scene1.add(s8);
        scene1.add(s9);
        
        // camera 50, 52, 160
    }
    @FXML private RadioButton sceneButton2;
    private HittableList scene2 = new HittableList();
    {
        // Generating a scene which shows a view of saturn.png's rings
        // sun
        Vec sunCenter = new Vec(300,52,0); // center of the sun
        double sunRadius = 50; // radius of the sun
        Vec sunColor = new Vec(5,5,5); // color of the sun
        Sphere sun = new Sphere(sunCenter, sunRadius, new DiffuseLight(sunColor));
        scene2.add(sun);
        // saturn.png
        Vec center = new Vec(50,52,60); // center of the rings
        double saturnRadius = 10; // radius of saturn.png
        Vec saturnColor = new Vec(
          (234.0/255.0),
          (214.0/255.0),
          (184.0/255.0)
        ); // color of saturn.png
        Sphere saturn = new Sphere(center, saturnRadius, new Lambertian(saturnColor));
        scene2.add(saturn);
        
        double minRingRadius = 30; // radius of the rings
        double maxRingRadius = 70; // radius of the rings
        double ringThickness = 0.1; // thickness of the rings
        
        Vec ringAngle = new Vec(Math.toRadians(10),0,Math.toRadians(15)); // angle of the rings
        
        double ringSize = 1.0; // size of asteroids in the rings
        double numRings = 1; // number of rings
        
        // creating the rings
        // rotate around center
        for (int i = 0; i < 360*numRings; i++) {
            // i is the yaw angle
            // distance from the center
            double distance = Math.random() * (maxRingRadius - minRingRadius) + minRingRadius;
            double height = Math.random() * ringThickness - ringThickness/2;
            
            // distance from the center
            Vec directionFromCenter = new Vec(
              Math.cos(Math.toRadians(i)),
              height,
              Math.sin(Math.toRadians(i))
            );
            // rotate around the center
            directionFromCenter = Vec.rotate(ringAngle, directionFromCenter);
            // position of the asteroid
            Vec position = Vec.add(center, Vec.mult(directionFromCenter, distance));
            
            // Generate random brown color
            Vec color = new Vec(
              Math.random() * 0.1 + (112.0/255.0),
              Math.random() * 0.1 + (128.0/255.0),
              Math.random() * 0.1 + (144/255.0)
            );
            
            Sphere asteroid = new Sphere(position, ringSize, new Lambertian(color));
            scene2.add(asteroid);
        }
        
        
        
        
    }
    @FXML private RadioButton sceneButton3;
    private HittableList scene3 = new HittableList();
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
        scene3.add(s1);
        scene3.add(s2);
//        scene3.add(s3);
        scene3.add(s4);
    }
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
    // Metal specific
    @FXML private HBox metalSettings;
    @FXML private TextField fuzzField;
    // Dielectric specific
    @FXML private HBox refractionSettings;
    @FXML private TextField refractIndexField;
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
    @FXML private Text pitchText;
    @FXML private Text yawText;
    @FXML private Text rollText;
    private boolean isLookAt = false;
    @FXML private RadioButton pitchyawroll;
    @FXML private RadioButton lookat;
    
    private static final String emptyError = new String("%s cannot be empty%n");
    private static final String integerValueError = new String("%s has to be an integer%n");
    private static final String doubleValueError = new String("%s has to be a decimal number%n");
    private static final String lessThanZeroError = new String("%s has to be greater than 0%n");
    
    private volatile int[] pixels = new int[imageWidth * imageHeight];
    private WritableImage image = new WritableImage(imageWidth, imageHeight);
    private PixelWriter pixelWriter = image.getPixelWriter();
    private Timeline timeline;
    {
        // Animation loop to update image
        timeline = new Timeline(new KeyFrame(javafx.util.Duration.millis(500), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateImage(pixels, pixelWriter, image);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }
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
    
        // See if render is already running
        cancelRender();
        
        // Setup image
        imageView.setFitWidth(imageWidth);
        imageView.setFitHeight(imageHeight);
        // display image
        this.image = new WritableImage(imageWidth, imageHeight);
        this.pixelWriter = image.getPixelWriter();
        // array
        this.pixels = new int[imageWidth * imageHeight];
        
        // Setup render thread
        App.renderThread = new Thread(new RenderThread(
          timeline,
          pixels,
          world,
          camPosition, camRotation, isLookAt,
          numPasses, samplesPerPixel, maxDepth,
          0, imageWidth, 0, imageHeight
        ));
    
        // Start render
        updateImage(pixels, pixelWriter, image);
        renderThread.start();
        timeline.play();
    }
    
    @FXML public void cancelRender() {
        if (renderThread != null && renderThread.isAlive()) {
            // stop render
            System.out.println();
            System.out.println("Killing old render");
            RenderThread.stop();
            // wait for render to stop
            try {
                renderThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // stop animation
            timeline.stop();
            System.out.println("Old render killed");

        }
    }
    
    @FXML public void saveImage() {
        // Save image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter("PNG", "*.png"),
          new FileChooser.ExtensionFilter("JPG", "*.jpg"),
          new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            String name;
            if (isLookAt) {
                name = "look at x";
            } else {
                name = "pitch";
            }
            parseDouble(name, pitch);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validYaw() {
        try {
            String name;
            if (isLookAt) {
                name = "look at y";
            } else {
                name = "yaw";
            }
            parseDouble(name, yaw);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validRoll() {
        try {
            String name;
            if (isLookAt) {
                name = "look at z";
            } else {
                name = "roll";
            }
            parseDouble(name, roll);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void selectPitchYawRoll() {
        System.out.println("Pitch, yaw, roll");
        if (!pitchyawroll.isSelected()) {
            this.pitchyawroll.setSelected(true);
            return;
        }
        this.isLookAt = false;
        this.lookat.setSelected(false);
        // update labels
        this.pitchText.setText("Pitch");
        this.yawText.setText("Yaw");
        this.rollText.setText("Roll");
    }
    @FXML public void selectLookAt() {
        System.out.println("Look at");
        if (!lookat.isSelected()) {
            this.lookat.setSelected(true);
            return;
        }
        this.isLookAt = true;
        this.pitchyawroll.setSelected(false);
        // update labels
        this.pitchText.setText("Look at x");
        this.yawText.setText("Look at y");
        this.rollText.setText("Look at z");
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
    @FXML public void validMetalFuzz() {
        try {
            parseDouble("fuzz", fuzzField);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    @FXML public void validRefractiveIndex() {
        try {
            parseDouble("refractive index", refractIndexField);
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        }
    }
    // drop down
    @FXML public void getMaterialDropDown() {
        // get all classes that are subclasses of Material
        ArrayList<Material> classes = new ArrayList<Material>();
        classes.add(Material.LAMBERTIAN);
        classes.add(Material.METAL);
        classes.add(Material.DIELECTRIC);
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
            } else if ( materialType instanceof Metal ) {
                material = new Metal(new Vec(rValue.getValue(), gValue.getValue(), bValue.getValue()), parseDouble("fuzz", fuzzField));
            } else if ( materialType instanceof Dielectric ) {
                material = new Dielectric(new Vec(rValue.getValue(), gValue.getValue(), bValue.getValue()), parseDouble("refractive index", refractIndexField));
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
            rValue.setMax(15);
            gValue.setMax(15);
            bValue.setMax(15);
            metalSettings.setDisable(true);
            metalSettings.setVisible(false);
            refractionSettings.setDisable(true);
            refractionSettings.setVisible(false);
        } else if (material instanceof Metal) {
            rValue.setMax(1);
            gValue.setMax(1);
            bValue.setMax(1);
            metalSettings.setDisable(false);
            metalSettings.setVisible(true);
            refractionSettings.setDisable(true);
            refractionSettings.setVisible(false);
            
            Metal metal = (Metal) material;
            fuzzField.setText(Double.toString(metal.getFuzz()));
        } else if (material instanceof Dielectric) {
            rValue.setMax(1);
            gValue.setMax(1);
            bValue.setMax(1);
            metalSettings.setDisable(true);
            metalSettings.setVisible(false);
            refractionSettings.setDisable(false);
            refractionSettings.setVisible(true);
            
            Dielectric dielectric = (Dielectric) material;
            refractIndexField.setText(Double.toString(dielectric.getRefractiveIndex()));
        } else {
            rValue.setMax(1);
            gValue.setMax(1);
            bValue.setMax(1);
            metalSettings.setDisable(true);
            metalSettings.setVisible(false);
            refractionSettings.setDisable(true);
            refractionSettings.setVisible(false);
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
        sphereSelect.setValue(sphere);
        selectSphere();
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

