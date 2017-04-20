package fusorvis;

import fusorcompmodeling.*;

import java.util.List;

import javafx.application.Application;
import static javafx.application.Application.launch;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;

import static javafx.scene.input.KeyCode.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.shape.TriangleMesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.util.Collections;

/**
 *
 * @author guberti
 */
public class FusorVis extends Application {

    final Group root = new Group();
    final Xform chargeGroup = new Xform();
    final Xform componentGroup = new Xform();
    final Xform axisGroup = new Xform();
    final Xform referenceGroup = new Xform();
    GraphicsContext eFieldPixels;
    PixelWriter eFieldPixelWriter;
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();

    TextFlow textFieldRoot = new TextFlow();
    Stage textFieldStage = new Stage();
    Stage eFieldStage = new Stage();

    SortedMap<String, String> output = new TreeMap<String, String>();

    Text consoleDump = new Text();

    String xmlFileName = "SimpleXML";

    double timeStepMCS = 1;

    Sphere deutron = new Sphere(1.0);

    Point[] points;
    List<GridComponent> parts;

    List<Point> markedPoints;

    Stage primaryStage;

    Box eFieldSlice;
    public boolean eFieldBuilt = false;
    Rotate[] eFieldTransforms;

    // Efield generation stats
    double sliceWidth = 96 / 16;
    double sliceHeight = 54 / 16;
    double imageConversionFactor = 256;
    double blockSideLength = 16;

    ProgressBar pb = new ProgressBar();

    private static final double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;

    // Mouse + keyboard vars
    double ONE_FRAME = 1.0 / 24.0;
    double DELTA_MULTIPLIER = 200.0;
    double CONTROL_MULTIPLIER = 0.1;
    double SHIFT_MULTIPLIER = 0.1;
    double ALT_MULTIPLIER = 0.5;
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;

    double annodeVoltage = 0;
    double cathodeVoltage = -500;

    // Render vars
    double electronRadius = 1.0;

    private void buildElectrons(Point[] points) {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial blackMaterial = new PhongMaterial();
        blackMaterial.setDiffuseColor(Color.BLACK);
        blackMaterial.setSpecularColor(Color.DARKGREY);

        for (Point point : points) {
            final Sphere electron = new Sphere(electronRadius);
            electron.setTranslateX(point.x);
            electron.setTranslateY(point.y);
            electron.setTranslateZ(point.z);
            if (point.charge == 1) { // Positive charge
                electron.setMaterial(redMaterial); // Red
            } else { // Negative charge
                electron.setMaterial(blackMaterial); // Black
            }

            chargeGroup.getChildren().add(electron);
        }
        chargeGroup.setVisible(true);
        world.getChildren().addAll(chargeGroup);
    }

    private void buildAxes() { // Red = x, green = y, blue = z
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(240.0, 1, 1);
        final Box yAxis = new Box(1, 240.0, 1);
        final Box zAxis = new Box(1, 1, 240.0);
        final Sphere posXAxis = new Sphere(2.0);
        final Sphere posYAxis = new Sphere(2.0);
        final Sphere posZAxis = new Sphere(2.0);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);
        posXAxis.setMaterial(redMaterial);
        posXAxis.setTranslateX(120);
        posYAxis.setMaterial(greenMaterial);
        posYAxis.setTranslateY(120);
        posZAxis.setMaterial(blueMaterial);
        posZAxis.setTranslateZ(120);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis, posXAxis, posZAxis, posYAxis);
        world.getChildren().addAll(axisGroup);
    }

    private void buildReferencePoints(Point[] referencePoints) {

        for (Point p : referencePoints) {
            final PhongMaterial m = new PhongMaterial();
            m.setDiffuseColor(Color.BLACK);
            m.setDiffuseColor(Color.GREY);
            final Sphere s = new Sphere(0.2);
            s.setTranslateX(p.x);
            s.setTranslateY(p.y);
            s.setTranslateZ(p.z);

            referenceGroup.getChildren().add(s);
        }
        world.getChildren().addAll(referenceGroup);
    }

    private void buildCamera() {
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    private void buildScene() {
        root.getChildren().add(world);
    }

    private void buildTextWindow(Stage primaryStage) {
        String output = compileOutput();
        Text txt = new Text(output);
        txt.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        textFieldRoot.getChildren().add(txt);

        textFieldStage.setTitle("Model statistics");
        textFieldStage.setScene(new Scene(textFieldRoot));
        textFieldStage.initOwner(primaryStage);
        textFieldStage.initModality(Modality.APPLICATION_MODAL);
        textFieldStage.setAlwaysOnTop(false);
        textFieldStage.show();
        primaryStage.toFront();
    }

    private void buildEFieldStage(Stage primaryStage, Point[] points) {
        eFieldStage.setTitle("Electric Field");
        Group r = new Group();

        eFieldStage.setScene(new Scene(r, 96 * 16, 54 * 16));
        final Canvas canvas = new Canvas(96 * 16, 54 * 16);
        eFieldPixels = canvas.getGraphicsContext2D();
        eFieldPixelWriter = eFieldPixels.getPixelWriter();
        r.getChildren().add(canvas);

        eFieldStage.initOwner(primaryStage);
        eFieldStage.initModality(Modality.APPLICATION_MODAL);
        eFieldStage.show();
        primaryStage.toFront();
        updateEField(points);

        eFieldBuilt = true;

    }
    private int indexOf(double[] arr, double item) {
        // Simple binary search
        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (item < arr[mid]) {
                high = mid - 1;
            } else if (item > arr[mid]) {
                low = mid + 1;
            } else {
                return mid;
            }
        }
        return -1; // Will never get here
    }
    private double toColor(int length, int index) {
        // Return a val between 0.0 and 1.0
        return (double) index / (double) (length - 1);
    }
    private void updateEField(Point[] points) {
        EField e = new EField();
        EField.setkQ(annodeVoltage, cathodeVoltage, points);

        int arrayWidth = (int) ((int) sliceWidth * imageConversionFactor / blockSideLength);
        int arrayHeight = (int) ((int) sliceHeight * imageConversionFactor / blockSideLength);

        double widthUnit = sliceWidth / arrayWidth;
        double heightUnit = sliceHeight / arrayHeight;

        double[][][] fieldGrid = new double[arrayWidth][arrayHeight][3];
        for (int i = 0; i < arrayWidth; i++) {
            for (int k = 0; k < arrayHeight; k++) {
                Point p = new Point((-(sliceWidth / 2) + i * widthUnit), (-(sliceHeight / 2) + k * widthUnit), 0);
                System.out.println("Old point: " + p.toString());
                p = translateEFieldPixel(p);
                System.out.println("New point: " + p.toString());
                Vector efield = EField.EFieldSum(points, p);
                
                fieldGrid[i][k][0] = efield.x;
                fieldGrid[i][k][1] = efield.y;
                fieldGrid[i][k][2] = efield.z;
            }
        }
        System.out.println("Data recieved and stored in temporary storage");

        double[][] sorted = new double[3][arrayWidth * arrayHeight];
        for (int i = 0; i < arrayWidth; i++) {
            for (int k = 0; k < arrayHeight; k++) {
                for (int j = 0; j < 3; j++) {
                    sorted[j][k * arrayWidth + i] = fieldGrid[i][k][j];
                    
                }
            }
        }
        
        for (int j = 0; j < 3; j++) {
            Arrays.sort(sorted[j]);
        }

        for (int i = 0; i < arrayWidth; i++) {
            for (int k = 0; k < arrayHeight; k++) {
                Color c = new Color(
                        toColor(indexOf(sorted[0], fieldGrid[i][k][0]), sorted[0].length),
                        toColor(indexOf(sorted[1], fieldGrid[i][k][1]), sorted[1].length),
                        toColor(indexOf(sorted[2], fieldGrid[i][k][2]), sorted[2].length),
                        1.0);
                
                for (int j = 0; j < blockSideLength; j++) {
                    for (int l = 0; l < blockSideLength; l++) {
                        eFieldPixelWriter.setColor(i * 16 + j, k * 16 + l, c);
                    }
                }
            }
        }

        System.out.println("Pixel values assigned");
    }

    private void buildStage(Stage primaryStage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());

        primaryStage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    textFieldStage.setAlwaysOnTop(true);
                } else {
                    textFieldStage.setAlwaysOnTop(false);
                }
            }
        });
    }

    // Helper functions
    private double radToDeg(double radians) {
        return (radians * 180) / Math.PI;
    }

    private void handleMouse(Scene scene, final Node root) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });

        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {

                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                double modifier = 1.0;
                double modifierFactor = 0.1;

                if (me.isControlDown()) {
                    modifier = 0.1;
                }
                if (me.isShiftDown()) {
                    modifier = 10.0;
                }
                if (me.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * modifierFactor * modifier * 2.0);  // +
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * modifierFactor * modifier * 2.0);  // -

                    output.put("Camera phi", String.valueOf(cameraXform.ry.getAngle()));
                    output.put("Camera theta", String.valueOf(cameraXform.rx.getAngle()));

                } else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX * modifierFactor * modifier;
                    camera.setTranslateZ(newZ);
                } else if (me.isMiddleButtonDown()) {
                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3);  // -
                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3);  // -
                }
                compileOutput();
            }
        });
    }

    private void handleKeyboard(Scene scene, Stage stage) {
        final boolean moveCamera = true;

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            double translateStep = 0.2;
            double rotateStep = 1;

            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case Z: // CTRL+X closes window
                        if (event.isControlDown()) {
                            // Close down window
                            stage.close();
                        }
                        break;
                    case O:
                        if (event.isControlDown()) {
                            textFieldStage.setTitle("My New Stage Title");
                            textFieldStage.setScene(new Scene(textFieldRoot, 450, 450));
                            textFieldStage.show();
                        }
                        break;
                    case S:
                        System.out.println("KEY PRESSED");
                        if (event.isControlDown()) {
                            FileChooser fileChooser = new FileChooser();
                            System.out.println("IN FILE CHOOSER");
                            fileChooser.setTitle("Save file");
                            fileChooser.getExtensionFilters().add(new ExtensionFilter("JSON Files", "*.json"));
                            File savedFile = fileChooser.showSaveDialog(primaryStage);
                            if (savedFile != null) {
                                try {
                                    String txt = exportPointsAsJSON();
                                    savedFile.createNewFile();
                                    PrintStream stream = new PrintStream(savedFile, "UTF-8");
                                    stream.println(txt);
                                    stream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println("An ERROR occurred while saving the file!"
                                            + savedFile.toString());
                                    return;
                                }
                                System.out.println("File saved: " + savedFile.toString());
                            } else {
                                System.out.println("File save cancelled.");
                            }
                        }
                        break;
                    case PAGE_UP: // Get larger
                        scaleElectrons(1.1);
                        break;
                    case PAGE_DOWN: // Get smaller
                        scaleElectrons(0.9);
                        break;
                    case H: // Toggle wireframe visibility
                        toggleXform(componentGroup);
                        break;
                    case C: // Toggle electron visibility
                        toggleXform(chargeGroup);
                        break;
                    case A: // Toggle axis visibility
                        toggleXform(axisGroup);
                        break;
                    case X:
                        if (event.isControlDown()) {
                            FileChooser fileChooser = new FileChooser();
                            System.out.println("IN FILE CHOOSER");
                            fileChooser.setTitle("Save file");
                            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.XML"));
                            File savedFile = fileChooser.showSaveDialog(primaryStage);
                            if (savedFile != null) {
                                try {
                                    String txt = printShapesXML.printShapes(parts);
                                    savedFile.createNewFile();
                                    PrintStream stream = new PrintStream(savedFile, "UTF-8");
                                    stream.println(txt);
                                    stream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println("An ERROR occurred while saving the file!"
                                            + savedFile.toString());
                                    return;
                                }
                                System.out.println("File saved: " + savedFile.toString());
                            } else {
                                System.out.println("File save cancelled.");
                            }
                        }
                        break;
                    case P: // Seed points
                        // Insert code for setting up particles here

                        final PhongMaterial deutronMaterial = new PhongMaterial();
                        deutronMaterial.setDiffuseColor(Color.CORAL);
                        deutronMaterial.setSpecularColor(Color.PURPLE);

                        deutron.setMaterial(deutronMaterial);
                        deutron.setTranslateX(50);
                        deutron.setTranslateY(50);
                        deutron.setTranslateZ(50);

                        world.getChildren().add(deutron);
                        // Insert code for updating positions in this runnable
                        Runnable r = new Runnable() {
                            public void run() {
                                // Code for updating positions goes here
                                Point p = new Point(deutron.getTranslateX(), deutron.getTranslateY(), deutron.getTranslateZ());
                                //Vector v = StatsGen.getVelocity(points, 0, -20000.0, 0, p, ONE_FRAME);
                            }
                        };

                        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                        executor.scheduleAtFixedRate(r, 0, 50, TimeUnit.MILLISECONDS);
                        break;
                    case F:
                        if (event.isControlDown()) {
                            if (!eFieldBuilt) {
                                buildEFieldStage(primaryStage, points);
                            } else {
                                System.out.println("Updating e-field!");
                                updateEField(points);
                            }
                        } else {
                            if (!eFieldBuilt) {
                                //buildEFieldSlice();
                                eFieldBuilt = true;
                            }
                        }
                        break;

                    case Q:
                    case W:
                    case E:
                        double step = translateStep;
                        if (event.isControlDown()) {
                            step *= -1;
                        }
                        switch (event.getCode()) {
                            case Q:
                                eFieldSlice.setTranslateX(eFieldSlice.getTranslateX() + step);
                                break;
                            case W:
                                eFieldSlice.setTranslateY(eFieldSlice.getTranslateY() + step);
                                break;
                            case E:
                                eFieldSlice.setTranslateZ(eFieldSlice.getTranslateZ() + step);
                                break;
                        }
                        break;
                    case R:
                    case T:
                    case Y:
                        double rotStep = rotateStep;
                        if (event.isControlDown()) {
                            rotStep *= -1;
                        }
                        switch (event.getCode()) {
                            case R:
                                eFieldTransforms[0].setAngle(eFieldTransforms[0].getAngle() + rotStep);
                                break;
                            case T:
                                eFieldTransforms[1].setAngle(eFieldTransforms[1].getAngle() + rotStep);
                                break;
                            case Y:
                                eFieldTransforms[2].setAngle(eFieldTransforms[2].getAngle() + rotStep);
                                break;
                        }
                        break;
                    case U:
                        double scaleStep;

                        if (event.isControlDown()) {
                            scaleStep = 0.95;
                        } else {
                            scaleStep = 1.05;
                        }
                        eFieldSlice.setScaleX(eFieldSlice.getScaleX() * scaleStep);
                        eFieldSlice.setScaleY(eFieldSlice.getScaleX() * scaleStep);
                        eFieldSlice.setScaleZ(eFieldSlice.getScaleX() * scaleStep);
                        break;
                }
            }
        });
    }

    private String exportPointsAsJSON() {
        JSONArray arr = new JSONArray();
        for (Point p : points) {
            JSONObject obj = new JSONObject();
            obj.put("x", p.x);
            obj.put("y", p.y);
            obj.put("z", p.z);
            obj.put("charge", p.charge);
            arr.put(obj);
        }
        return arr.toString();
    }

    private void toggleXform(Xform g) {
        g.setVisible(!g.visibleProperty().get());
    }

    public void scaleElectrons(double scale) {
        for (int i = 0; i < chargeGroup.getChildren().size(); i++) {
            double xScale = chargeGroup.getChildren().get(i).getScaleX() * scale;
            double yScale = chargeGroup.getChildren().get(i).getScaleX() * scale;
            double zScale = chargeGroup.getChildren().get(i).getScaleX() * scale;
            chargeGroup.getChildren().get(i).setScaleX(xScale);
            chargeGroup.getChildren().get(i).setScaleY(yScale);
            chargeGroup.getChildren().get(i).setScaleZ(zScale);

        }
    }

    public void buildEFieldSlice() {
        final int baseWidth = 48;
        final int baseHeight = 27;

        output.put("E-Field Slice Width", Integer.toString(baseWidth));
        output.put("E-Field Slice Height", Integer.toString(baseHeight));

        final PhongMaterial planeMaterial = new PhongMaterial();
        planeMaterial.setDiffuseColor(new Color(0.5, 0.5, 0.5, 0.5));

        eFieldSlice = new Box(baseWidth, baseHeight, 0.025);
        eFieldSlice.setMaterial(planeMaterial);

        Rotate rx = new Rotate();
        rx.setAxis(Rotate.X_AXIS);
        Rotate ry = new Rotate();
        ry.setAxis(Rotate.Y_AXIS);
        Rotate rz = new Rotate();
        rz.setAxis(Rotate.Z_AXIS);

        eFieldTransforms = new Rotate[3];
        eFieldTransforms[0] = rx;
        eFieldTransforms[1] = ry;
        eFieldTransforms[2] = rz;

        eFieldSlice.getTransforms().addAll(eFieldTransforms);

        output.put("E-Field Slice Translation X", Double.toString(eFieldSlice.getTranslateX()));
        output.put("E-Field Slice Translation Y", Double.toString(eFieldSlice.getTranslateY()));
        output.put("E-Field Slice Translation Z", Double.toString(eFieldSlice.getTranslateZ()));
        output.put("E-Field Slice Rotation X", Double.toString(rx.getPivotX()));
        output.put("E-Field Slice Rotation Y", Double.toString(ry.getPivotY()));
        output.put("E-Field Slice Rotation Z", Double.toString(rz.getPivotZ()));

        world.getChildren().add(eFieldSlice);
    }

    public Point translateEFieldPixel(Point p) {
        Point r = new Point(p.x, p.y, p.z);
        double c = 1;

        r.y = r.y * Math.cos(eFieldTransforms[0].getAngle() * c) - r.z * Math.sin(eFieldTransforms[0].getAngle() * c);
        r.z = r.y * Math.sin(eFieldTransforms[0].getAngle() * c) + r.z * Math.cos(eFieldTransforms[0].getAngle() * c);

        r.z = r.z * Math.cos(eFieldTransforms[1].getAngle() * c) - r.x * Math.sin(eFieldTransforms[1].getAngle() * c);
        r.x = r.z * Math.sin(eFieldTransforms[1].getAngle() * c) + r.x * Math.cos(eFieldTransforms[1].getAngle() * c);

        r.x = r.x * Math.cos(eFieldTransforms[2].getAngle() * c) - r.y * Math.sin(eFieldTransforms[2].getAngle() * c);
        r.y = r.x * Math.sin(eFieldTransforms[2].getAngle() * c) + r.y * Math.cos(eFieldTransforms[2].getAngle() * c);

        assert !Double.isNaN(r.x);

        Vector v = r.convertToSphericalCoordsExc();
        v.length *= eFieldSlice.getScaleX();

        Point t = v.convertRayToCartesian(v.length);

        assert !Double.isNaN(r.x);

        t.x += eFieldSlice.getTranslateX();
        t.y += eFieldSlice.getTranslateY();
        t.z += eFieldSlice.getTranslateZ();

        return t;
    }

    public String compileOutput() {
        Iterator it = output.entrySet().iterator();
        String textOutput = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            textOutput += pair.getKey() + ": " + pair.getValue() + "\n";
        }
        consoleDump.setText(textOutput);
        return textOutput;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void start(Stage primaryStage) throws Exception {
        int pointCount = 1000;
        int optimizations = 0;
        
        double annodeVoltage = 0;
        double cathodeVoltage = -500;

        //List<GridComponent> parts = p.parseObjects();
        parts = new ArrayList<>();

        String jsonPath = "Bent Sphere.json";
        byte[] encoded = Files.readAllBytes(Paths.get(jsonPath));

        JSONArray pieceArr = new JSONArray(new String(encoded, Charset.defaultCharset()));
        for (int i = 0; i < pieceArr.length(); i++) {
            JSONObject infoObj = pieceArr.getJSONObject(i);
            if (infoObj.getString("type").equals("wire")) {
                Wire w = new Wire(infoObj.toString());
                parts.addAll(w.getAsGridComponents());
            } else if (infoObj.getString("type").equals("stl")) {
                StlMeshImporter imp = new StlMeshImporter();
                try {
                    System.out.println(infoObj.getString("filename"));
                    imp.read(new File(infoObj.getString("filename")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                double scaleFactor = infoObj.getDouble("scalefactor");
                JSONObject translationObj = infoObj.getJSONObject("positionadj");

                Point translation = new Point (
                        translationObj.getDouble("x"),
                        translationObj.getDouble("y"),
                        translationObj.getDouble("z"));
                
                boolean flip_yz = infoObj.getBoolean("flip_yz");
                boolean reflect = infoObj.getBoolean("flip_vertical");

                TriangleMesh mesh = imp.getImport();
                imp.close();
                float[] fA = null;
                fA = mesh.getPoints().toArray(fA);

                int[] iA = null;
                iA = mesh.getFaces().toArray(iA);

                for (int k = 0; k < iA.length; k += 6) {
                    Point p1 = new Point(fA[iA[k] * 3], fA[iA[k] * 3 + 1], fA[iA[k] * 3 + 2]);
                    Point p2 = new Point(fA[iA[k + 2] * 3], fA[iA[k + 2] * 3 + 1], fA[iA[k + 2] * 3 + 2]);
                    Point p3 = new Point(fA[iA[k + 4] * 3], fA[iA[k + 4] * 3 + 1], fA[iA[k + 4] * 3 + 2]);
                    Point[] verts = {p1, p2, p3};

                    for (Point p : verts) { // Apply transformations
                        if (flip_yz) {
                            double oldy = p.y;
                            p.y = p.z;
                            p.z = oldy;
                        }
                        if (reflect) {
                            p.y *= -1;
                        }
                        p.scale(scaleFactor);
                        p.sum(translation);
                    }

                    Triangle t = new Triangle(verts, infoObj.getInt("charge"));
                    parts.add(t);
                }
            }
        }
        points = PointDistributer.shakeUpPoints(parts, pointCount, optimizations);
        markedPoints = new ArrayList<>();

        double posAvgPotential = StatsGen.avgPotential(points, 1);
        double negAvgPotential = StatsGen.avgPotential(points, -1);

        Point q = new Point();
        q.x = 0.0;
        q.y = 0.0;
        q.z = 0.0;
  
        Vector efield = new Vector();
        EField.setkQ(annodeVoltage, cathodeVoltage, points);
        efield = EField.EFieldSum(points, q);

        output.put("Points", String.valueOf(points.length));
        output.put("Parts in grid", String.valueOf(parts.size()));
        output.put("Optimizations", String.valueOf(optimizations));
        output.put("Avg. potential of pos. points", String.valueOf(posAvgPotential * 1 / 1));
        output.put("Avg. potential of neg. points", String.valueOf(negAvgPotential));

        Point[] referencePoints = {};

        output.put("Sample point x e-field", String.valueOf(efield.x));
        output.put("Sample point y e-field", String.valueOf(efield.y));
        output.put("Sample point z e-field", String.valueOf(efield.z));

        buildCamera();
        buildElectrons(points);
        buildAxes();
        buildReferencePoints(referencePoints);
        buildScene();
        buildStage(primaryStage);
        
        buildEFieldSlice();
        buildEFieldStage(primaryStage, points);
        //buildTextWindow(primaryStage);

        Scene scene = new Scene(root, 1024, 768, true);
        scene.setFill(Color.GREY);
        handleMouse(scene, world);
        handleKeyboard(scene, primaryStage);

        //primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        //primaryStage.setFullScreenExitHint("");
        primaryStage.setTitle("Fusor Electric Field Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
        //primaryStage.setFullScreen(true);
        scene.setCamera(camera);
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}
