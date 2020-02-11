package application;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javafx.application.Application;
import javafx.collections.*;
import javafx.collections.ObservableArrayBase;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Main extends Application {

	Stage window;
	Scene scene1, scene2;

	// Data fields
	protected File inFile;
	FileTrim fileTrim;
	protected FileManager fManager;
	protected CsmoMaker csmoManager;
	protected String fileDestination;
	List<String> roomList;
	int numberOfRooms = 1;
	
	// Create combobox
	ComboBox<String> comboBox = new ComboBox<>();
	// Create Listview
	ListView<String> listView = new ListView<>();
	// Create spinner
	Spinner<Integer> spinner = new Spinner<>();

	@Override
	public void start(Stage primaryStage) {
		try {
			window = primaryStage;
			// System.out.println(javafx.scene.text.Font.getFamilies());
			window.setTitle("Classroom Schedule Maker - Wild Stallions Group");

			// Button to import file
			Button btImport = new Button("Choose File");
			btImport.setPrefWidth(110);
			Text textSelectedFile = new Text("");
			btImport.setOnAction(e -> {
				FileChooser fileChooser = new FileChooser();
				inFile = fileChooser.showOpenDialog(null);
				textSelectedFile.setText(inFile.getName());
				System.out.println("The file name is " + inFile.getName());
				if (inFile != null) {
					System.out.println("File is valid.");
				} else {
					System.out.println("File is not valid.");
				}
			});

			// Add button for Trim File
			Button btTrim = new Button("Trim File");
			btTrim.setPrefWidth(110);
			Text textTrimmedFile = new Text("");
			btTrim.setOnAction(e -> {
				try {
					window.setScene(scene2);
					fileTrim = new FileTrim(inFile);
					textTrimmedFile.setText("Success!");

					fileDestination = fileTrim.getFileDestination();
					// Create ArrayManager, CsmoMaker, and FileManager
					ArrayManager aManager = new ArrayManager(
							new File(fileDestination + "Complete Entries.csv"));
					System.out.println("Array Manager created: " + aManager.getArray().toString());
					csmoManager = new CsmoMaker(aManager);
					fManager = new FileManager("Schedule.csmo");
					System.out.println("Room list: " + csmoManager.getRoomList().toString());
					roomList = csmoManager.getRoomList();
					for (String room : roomList) {
						comboBox.getItems().add(room);
					}
					for (String room : roomList) {
						listView.getItems().add(room);
					}
					// Create spinner value factory
					SpinnerValueFactory<Integer> valueFactory = //
			                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, csmoManager.getRoomList().size(), 1);
					spinner.setValueFactory(valueFactory);
					spinner.setPrefWidth(50);
					
					comboBox.setPromptText("Select room");
					fileDestination = fileDestination + "New Schedule.csmo";
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					System.out.println("File not found.");
					e1.printStackTrace();
				}
			});

			/** GridPane for Scene1 */
			// Create GridPane
			GridPane layout1 = new GridPane();

			// Add title text and logo
			Text title = new Text("Classroom Schedule Maker");
			title.setStyle("-fx-font-size: 40; -fx-font-weight: bold; -fx-font-family: Bauhaus 93; -fx-fill: #180D01;");
			//ImageView logo = new ImageView("\\bin\\application\\stallion.png");
			//Label lbLogo1 = new Label("Wild Stallions", logo);
			//lbLogo1.setStyle("-fx-font-size: 40");
			//lbLogo1.setContentDisplay(ContentDisplay.TOP);

			// gridPane.setGridLinesVisible(true);
			layout1.setHgap(10);
			layout1.setVgap(10);
			layout1.setAlignment(Pos.TOP_CENTER);
			layout1.setPadding(new Insets(10, 10, 10, 10));
			layout1.setColumnSpan(title, 2);
			layout1.add(title, 0, 0);
			layout1.add(btImport, 0, 1);
			layout1.add(textSelectedFile, 1, 1);
			layout1.add(btTrim, 0, 2);
			layout1.add(textTrimmedFile, 1, 2);
			//layout1.add(logo, 4, 4);
			//layout1.add(lbLogo1, 4, 4);

			scene1 = new Scene(layout1, 960, 600);
			scene1.getStylesheets().add(getClass().getResource("style.css").toString());

			/****
			 * /* Scene 2 - Convert to .csmo scene
			 ****/
			Text title2 = new Text("Classroom Schedule Maker");
			title2.setStyle(
					"-fx-font-size: 40; -fx-font-weight: bold; -fx-font-family: Bauhaus 93; -fx-fill: #180D01;");
			//ImageView logo2 = new ImageView("stallion.png");
			//Label lbLogo = new Label("Wild Stallions", logo2);
			//lbLogo.setStyle("-fx-font-size: 40");
			//lbLogo.setContentDisplay(ContentDisplay.TOP);

			Button btConvert = new Button("Create schedule");
			btConvert.setPrefWidth(110);

			Button btBack = new Button("Back");
			btBack.setOnAction(e -> {
				window.setScene(scene1);
			});

			// Create Checkboxes
			VBox checkBoxes = new VBox(10);
			CheckBox chkMultipleFiles = new CheckBox("Multiple files");
			CheckBox chkFillEmptySpace = new CheckBox("Fill empty space");
			checkBoxes.getChildren().add(chkFillEmptySpace);

			// Create radio buttons
			VBox radioButtons = new VBox(20);
			final ToggleGroup group = new ToggleGroup();
			RadioButton rbAllCourses = new RadioButton("All courses");
			RadioButton rbSelectRoom = new RadioButton("Select room");
			RadioButton rbListViewRooms = new RadioButton("Select multiple rooms");
			rbAllCourses.setToggleGroup(group);
			rbSelectRoom.setToggleGroup(group);
			rbListViewRooms.setToggleGroup(group);
			radioButtons.getChildren().addAll(rbAllCourses, rbSelectRoom, rbListViewRooms);
			
			// create alert 
	        Alert alert = new Alert(AlertType.CONFIRMATION);
	        alert.setContentText("Schedule successfully created!");

			GridPane layout2 = new GridPane();
			layout2.setHgap(10);
			layout2.setVgap(10);
			layout2.setAlignment(Pos.TOP_CENTER);
			layout2.setPadding(new Insets(10, 10, 10, 10));
			layout2.setColumnSpan(title2, 2);
			layout2.add(title2, 0, 0);
			layout2.add(btConvert, 0, 1);
			//layout2.add(logo2, 4, 4);
			layout2.add(btBack, 0, 5);
			layout2.add(radioButtons, 1, 1);
			//layout2.add(lbLogo, 4, 4);
			layout2.add(checkBoxes, 0, 2);

			scene2 = new Scene(layout2, 960, 600);
			scene2.getStylesheets().add(getClass().getResource("style.css").toString());

			// Action for radio buttons
			EventHandler<ActionEvent> radioButtonHandler = e -> {
				if (rbAllCourses.isSelected()) {
					checkBoxes.getChildren().add(chkMultipleFiles);
					checkBoxes.getChildren().add(spinner);
				} else {
					checkBoxes.getChildren().remove(chkMultipleFiles);
					checkBoxes.getChildren().remove(spinner);
				}

				if (rbSelectRoom.isSelected()) {
					// List<String> roomList = csmoManager.getRoomList();
					checkBoxes.getChildren().add(comboBox);
					//layout2.add(comboBox, 1, 3);
				} else {
					checkBoxes.getChildren().remove(comboBox);
					//layout2.getChildren().remove(comboBox);
				}

				if (rbListViewRooms.isSelected()) {

					listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
					layout2.add(listView, 1, 4);
				} else {
					layout2.getChildren().remove(listView);
				}

			};

			rbAllCourses.setOnAction(radioButtonHandler);
			rbSelectRoom.setOnAction(radioButtonHandler);
			rbListViewRooms.setOnAction(radioButtonHandler);

			// Action for btConvert
			btConvert.setOnAction(e -> {

				if (chkFillEmptySpace.isSelected()) {
					csmoManager.FillEmptySpace();
				}

				if (rbAllCourses.isSelected()) {
					if (chkMultipleFiles.isSelected()) {
						numberOfRooms = spinner.getValue();
						fManager.WriteFile(fileDestination, csmoManager.getSplitDataByNumberOfRooms(numberOfRooms));
					} else {
						fManager.WriteFile(fileDestination, csmoManager.getDataAllCoursesRoomCollumn());
						System.out.println("Created new file NewSchedule.csmo with all courses.");
					}
				}
				if (rbSelectRoom.isSelected()) {
					fManager.WriteFile(fileDestination, csmoManager.getOneRoomData(comboBox.getValue()));
					System.out.println("Created new file NewSchedule.csmo with data for one room.");
				}

				if (rbListViewRooms.isSelected()) {
					// Convert observable list to array to ArrayList
					ArrayList<String> arrayList = new ArrayList<String>(
							listView.getSelectionModel().getSelectedItems());
					fManager.WriteFile(fileDestination, csmoManager.getSelectedRooms(arrayList));
				}
				
				alert.show();

			});

			window.setScene(scene1);
			window.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
