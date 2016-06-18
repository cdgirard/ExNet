import girard.sc.expt.web.ExptOverlord;
import girard.sc.expt.web.SubjectLoginPage;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ExSocRunner extends Application
{
    Button m_experimenterLoginBtn, m_subjectLoginBtn;
    Text actiontarget;
    Stage m_primaryStage;
    
    EventHandler<ActionEvent> subjectEvent = p -> { 
	ExptOverlord eo = new ExptOverlord();
	eo.addPanel(new SubjectLoginPage(eo));
	eo.validate();
	m_primaryStage.hide();
    };
    
    EventHandler<ActionEvent> experimenterEvent = p -> actiontarget.setText("Experimenter in button pressed");
    
    @Override
    public void start(Stage stage)
    {
	m_primaryStage = stage;
	m_primaryStage.setTitle("ExSoc Welcome");

	GridPane grid = new GridPane();
	grid.setAlignment(Pos.CENTER);
	grid.setHgap(10);
	grid.setVgap(10);
	grid.setPadding(new Insets(25, 25, 25, 25));

	// Scene scene = new Scene(grid, 300, 275);
	Scene scene = new Scene(grid);
	m_primaryStage.setScene(scene);
	scene.getStylesheets().add
	 (ExSocRunner.class.getResource("images/Runner.css").toExternalForm());
	
	Text scenetitle = new Text("Welcome to ExSoc");
	scenetitle.setId("welcome-text");
	grid.add(scenetitle, 0, 0, 2, 1);

	m_experimenterLoginBtn = new Button("Experimenter");
	HBox hbBtn = new HBox(10);
	hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
	hbBtn.getChildren().add(m_experimenterLoginBtn);
	grid.add(hbBtn, 0, 1, 2, 1);
	
	m_experimenterLoginBtn.setOnAction(experimenterEvent);

	m_subjectLoginBtn = new Button("Subject");
	hbBtn = new HBox(10);
	hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
	hbBtn.getChildren().add(m_subjectLoginBtn);
	grid.add(hbBtn, 0, 2, 2, 1);

	m_subjectLoginBtn.setOnAction(subjectEvent);
	
	actiontarget = new Text();
	actiontarget.setId("actiontarget");
	grid.add(actiontarget, 1, 6);

	m_primaryStage.show();
    }

    public static void main(String[] args)
    {
	launch(args);
    }

}
