package pinger;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.control.ScrollPane;

public class FxPanel extends JFXPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextFlow textFlow;
    private ScrollPane scrollPaneFX; 
    
    FxPanel() {
        textFlow = new TextFlow();
        scrollPaneFX = new ScrollPane();

        Platform.runLater(() -> {
        	setScene(createScene());
        });
    }
    
	Scene createScene() {		
        Group  root  =  new  Group();
        Scene  scene  =  new  Scene(root);

        textFlow.setTextAlignment(TextAlignment.LEFT); 
        textFlow.setLineSpacing(1.0);
        
        scrollPaneFX.setPrefSize(600, 200);
        scrollPaneFX.setContent(textFlow);
        
        root.getChildren().add(scrollPaneFX);

        Platform.setImplicitExit(false);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        return (scene);
    }

	public void setTextFlow(String string, String color) {
		Text strToText = new Text();
		strToText.setFont(Font.font("Lucida Console", FontWeight.BOLD, 12));
		
		switch (color) {
			case "green":
				strToText.setFill(Color.GREEN);
				break;
			case "red":
				strToText.setFill(Color.RED);
				break;
			case "orange":
				strToText.setFill(Color.ORANGE);
				break;
			case "grey":
				strToText.setFill(Color.GREY);
				break;
			case "black":
				strToText.setFill(Color.BLACK);
				break;
			case "blue":
				strToText.setFill(Color.BLUE);
				break;
			case "white":
				strToText.setFill(Color.WHITE);
				break;
			default:
				strToText.setFill(Color.BLACK);
				break;
		}

		strToText.setText("\u0009"+ string + "\n");

		Platform.runLater(()->{
			textFlow.getChildren().add(0, strToText);
		});
	}
	
	public void clearTextFlow() {
		Platform.runLater(()->{
			textFlow.getChildren().clear();
		});	
	}
}
