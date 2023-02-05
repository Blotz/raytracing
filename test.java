/*
CS-255 Getting started code for the assignment
I do not give you permission to post this code online
Do not post your solution online
Do not copy code
Do not use JavaFX functions or other libraries to do the main parts of the assignment
All of those functions must be written by yourself
You may use libraries to achieve a better GUI
*/
import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;  
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class test extends Application {

    @Override
    public void start(Stage stage) throws FileNotFoundException {
		stage.setTitle("Photoshop");
		Image image = null;
		
		//Read the image
		try {
			image = new Image(new FileInputStream("raytrace.jpg"));  
		}
		catch(Exception e) {
			System.out.println("The raytrace.jpg image was not found in the expected directory");
			System.out.println("The expected directory is: "+ System.getProperty("user.dir"));
			System.exit(0);
		}
		
		//Create the graphical view of the image
		ImageView imageView = new ImageView(image); 
		
		//Create the simple GUI
		Button invert_button = new Button("Invert");

		//Add all the event handlers (this is a minimal GUI - you may try to do better)
		invert_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
				//At this point, "image" will be the original image
				//imageView is the graphical representation of an image
				//imageView.getImage() is the currently displayed image
				
				//Let's invert the currently displayed image by calling the invert function later in the code
				Image inverted_image=ImageInverter(imageView.getImage());
				//Update the GUI so the new image is displayed
				imageView.setImage(inverted_image);
            }
        });
		
		imageView.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
			System.out.println(event.getX());
			event.consume();
		});
		
		//Using a flow pane
		FlowPane root = new FlowPane();
		//Gaps between buttons
		root.setVgap(10);
        root.setHgap(5);

		//Add all the buttons and the image for the GUI
		root.getChildren().addAll(invert_button, imageView);

		//Display to user
        Scene scene = new Scene(root, 1024, 768);
        stage.setScene(scene);
        stage.show();
    }

	//Example function of invert
	public Image ImageInverter(Image image) {
		//Find the width and height of the image to be process
		int width = (int)image.getWidth();
        int height = (int)image.getHeight();
		//Create a new image of that width and height
		WritableImage inverted_image = new WritableImage(width, height);
		//Get an interface to write to that image memory
		PixelWriter inverted_image_writer = inverted_image.getPixelWriter();
		//Get an interface to read from the original image passed as the parameter to the function
		PixelReader image_reader=image.getPixelReader();
		
		//Iterate over all pixels
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				//For each pixel, get the colour
				Color color = image_reader.getColor(x, y);
				//Do something (in this case invert) - the getColor function returns colours as 0..1 doubles
				color=Color.color(1.0-color.getRed(), 1.0-color.getGreen(), 1.0-color.getBlue());
				
				//Apply the new colour
				inverted_image_writer.setColor(x, y, color);
			}
		}
		return inverted_image;
	}
	
	
    public static void main(String[] args) {
        launch();
    }

}