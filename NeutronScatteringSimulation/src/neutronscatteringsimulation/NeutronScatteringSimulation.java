/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import java.io.File;
import java.util.Random;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import neutronscatteringsimulation.Material.Element;

/**
 *
 * @author jfellows
 */
public class NeutronScatteringSimulation extends Application {

    static Random random = new Random();
    private File iglooFile;

    @Override
    public void start(Stage stage) {
        ObservableList<Element> paraffinComp = FXCollections.observableArrayList(new Element("H-1", 0.148605), new Element("C-12", 0.851395));
        final double PARAFFIN_DENSITY = 0.93;
        ObservableList<Element> airComp = FXCollections.observableArrayList(new Element("N-14", 0.7547), new Element("O-16", 0.2320));
        final double AIR_DENSITY = 0.001292;

        iglooFile = new File("test.obj");

        final double DEFAULT_INITIAL_NEUTRON_ENERGY = 2.45; // MeV
        final boolean LINE_MODE = true;
        final boolean AXES = true;

        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Number of Neutrons:"), 0, 0);
        TextField numNeutronsField = createNumericField("10");
        form.add(numNeutronsField, 1, 0);

        form.add(new Label("Initial Neutron Energy (MeV):"), 0, 1);
        TextField initialEnergyField = createNumericField(Double.toString(DEFAULT_INITIAL_NEUTRON_ENERGY));
        form.add(initialEnergyField, 1, 1);
        form.add(new Separator(), 0, 2, 2, 1);

        form.add(new Label("Air Composition:"), 0, 3);
        form.add(new Label("Density (g/cm3):"), 0, 4);
        TextField airDensityField = createNumericField(Double.toString(AIR_DENSITY));
        form.add(airDensityField, 1, 4);
        VBox airCompositionTable = createMaterialTable(airComp);
        form.add(airCompositionTable, 0, 5, 2, 1);

        form.add(new Label("Block Composition:"), 0, 6);
        form.add(new Label("Density (g/cm3):"), 0, 7);
        TextField paraffinDensityField = createNumericField(Double.toString(PARAFFIN_DENSITY));
        form.add(paraffinDensityField, 1, 7);
        VBox blockCompositionTable = createMaterialTable(paraffinComp);
        form.add(blockCompositionTable, 0, 8, 2, 1);
        form.add(new Separator(), 0, 9, 2, 1);

        Button chooseFile = new Button("Choose Igloo File");
        Text fileName = new Text(iglooFile.getName());
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Igloo File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("OBJ File", "*.obj"));
        fileChooser.setInitialDirectory(new File("."));
        chooseFile.setOnAction(e -> {
            iglooFile = fileChooser.showOpenDialog(null);
            fileName.setText(iglooFile.getName());
        });
        form.add(chooseFile, 0, 10);
        form.add(fileName, 1, 10);

        form.add(new Label("Bumpiness:"), 0, 11);
        TextField maxBumpField = createNumericField("2");
        form.add(maxBumpField, 1, 11);

        form.add(new Label("Tiler:"), 0, 12);
        String fragmentTilerName = "Fragment Tiler";
        String recursiveTilerName = "Recursive Tiler";
        ChoiceBox tilerChoice = new ChoiceBox(FXCollections.observableArrayList(fragmentTilerName, recursiveTilerName));
        form.add(tilerChoice, 1, 12);

        Label tilerInputLabel = new Label();
        form.add(tilerInputLabel, 0, 13);
        TextField tilerInputField = createNumericField("");
        form.add(tilerInputField, 1, 13);
        form.add(new Separator(), 0, 14, 2, 1);

        tilerChoice.getSelectionModel().selectedItemProperty().addListener((obx, oldItem, newItem) -> {
            if (newItem.equals(fragmentTilerName)) {
                tilerInputLabel.setText("Recursion Level:");
                tilerInputField.setText("2");
            } else if (newItem.equals(recursiveTilerName)) {
                tilerInputLabel.setText("Max Triangle Area:");
                tilerInputField.setText("200");
            }
        });
        tilerChoice.setValue(fragmentTilerName);

        Button run = new Button("Run Simulation");
        HBox hb = new HBox(10);
        hb.setAlignment(Pos.BOTTOM_RIGHT);
        hb.getChildren().add(run);
        form.add(hb, 1, 15);

        BorderPane main = new BorderPane();
        main.setLeft(form);

        run.setOnAction(e -> {
            final int NUM_NEUTRONS = Integer.parseUnsignedInt(numNeutronsField.getText());
            final double INITIAL_NEUTRON_ENERGY = Double.parseDouble(initialEnergyField.getText());
            final Material PARAFFIN = new Material(Double.parseDouble(paraffinDensityField.getText()), paraffinComp);
            final Material AIR = new Material(Double.parseDouble(airDensityField.getText()), airComp);
            final Tiler TILER;
            if (tilerChoice.getValue().equals(fragmentTilerName)) {
                TILER = new FragmentTiler(Integer.parseUnsignedInt(tilerInputField.getText()));
            } else {
                TILER = new RecursiveTiler(Integer.parseUnsignedInt(tilerInputField.getText()));
            }
            final double MAX_BUMP = Double.parseDouble(maxBumpField.getText());
            
            Task sim = new Simulation(NUM_NEUTRONS, INITIAL_NEUTRON_ENERGY, PARAFFIN, AIR, iglooFile, TILER, MAX_BUMP, LINE_MODE, AXES);

            ProgressBar bar = new ProgressBar();
            bar.progressProperty().bind(sim.progressProperty());
            main.setCenter(bar);

            sim.setOnSucceeded(s -> {
                SubScene simScene = (SubScene) sim.getValue();
                Pane pane = new Pane();
                pane.getChildren().add(simScene);
                simScene.heightProperty().bind(pane.heightProperty());
                simScene.widthProperty().bind(pane.widthProperty());
                main.setCenter(pane);
            });
            
            Thread th = new Thread(sim);
            th.setDaemon(true);
            th.start();
        });

        Scene scene = new Scene(main);
        stage.setTitle("Neutron Scattering Simulation!");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private TextField createNumericField(String initialValue) {
        TextField textField = new TextField();
        textField.setTextFormatter(new TextFormatter<>(change -> {
            try {
                Double.parseDouble(change.getControlNewText());
            } catch (NumberFormatException e) {
                if (!change.getControlNewText().equals("") && !change.getControlNewText().equals(".")) {
                    return null;
                }
            }
            return change;
        }));
        textField.setText(initialValue);
        return textField;
    }

    private VBox createMaterialTable(ObservableList<Element> elements) {
        TableView<Element> table = new TableView<>();
        table.setEditable(true);

        TableColumn<Element, String> isotopes = new TableColumn<>("Isotope");
        isotopes.setCellValueFactory(c -> c.getValue().isotope);

        TableColumn<Element, Number> massPercentages = new TableColumn("Mass Percentage");
        massPercentages.setCellValueFactory(c -> c.getValue().massPercentage);
        massPercentages.setCellFactory(c -> new EditCell(new NumberStringConverter(), createNumericField("")));

        table.getColumns().addAll(isotopes, massPercentages);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(elements);

        HBox add = new HBox(10);
        TextField isotope = new TextField();
        isotope.setPromptText("Isotope");
        TextField massPercentage = createNumericField("");
        massPercentage.setPromptText("Mass Percentage");
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            elements.add(new Element(isotope.getText(), Double.parseDouble(massPercentage.getText())));
            isotope.clear();
            massPercentage.clear();
        });
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Element selectedItem = table.getSelectionModel().getSelectedItem();
            elements.remove(selectedItem);
        });
        deleteButton.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> {
            deleteButton.setDisable(false);
        });
        add.getChildren().addAll(isotope, massPercentage, addButton, deleteButton);

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(table, add);
        return vbox;
    }

    public class EditCell<S, T> extends TableCell<S, T> {

        // Text field for editing
        private final TextField textField;

        // Converter for converting the text in the text field to the user type, and vice-versa:
        private final StringConverter<T> converter;

        public EditCell(StringConverter<T> converter, TextField textField) {
            this.converter = converter;
            this.textField = textField;

            itemProperty().addListener((obx, oldItem, newItem) -> {
                if (newItem == null) {
                    setText(null);
                } else {
                    setText(converter.toString(newItem));
                }
            });
            setGraphic(textField);
            setContentDisplay(ContentDisplay.TEXT_ONLY);

            textField.setOnAction(evt -> {
                commitEdit(this.converter.fromString(textField.getText()));
            });
            textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    commitEdit(this.converter.fromString(textField.getText()));
                }
            });
            textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (null != event.getCode()) {
                    switch (event.getCode()) {
                        case ESCAPE:
                            textField.setText(converter.toString(getItem()));
                            cancelEdit();
                            event.consume();
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        // set the text of the text field and display the graphic
        @Override
        public void startEdit() {
            super.startEdit();
            textField.setText(converter.toString(getItem()));
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            textField.requestFocus();
        }

        // revert to text display
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        // commits the edit. Update property if possible and revert to text display
        @Override
        public void commitEdit(T item) {

            // This block is necessary to support commit on losing focus, because the baked-in mechanism
            // sets our editing state to false before we can intercept the loss of focus.
            // The default commitEdit(...) method simply bails if we are not editing...
            if (!isEditing() && !item.equals(getItem())) {
                TableView<S> table = getTableView();
                if (table != null) {
                    TableColumn<S, T> column = getTableColumn();
                    CellEditEvent<S, T> event = new CellEditEvent<>(table,
                            new TablePosition<>(table, getIndex(), column),
                            TableColumn.editCommitEvent(), item);
                    Event.fireEvent(column, event);
                }
            }

            super.commitEdit(item);

            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
