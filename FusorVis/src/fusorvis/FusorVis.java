package fusorvis;

import fusorcompmodeling.*;

import java.util.List;

import javafx.application.Application;
import static javafx.application.Application.launch;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;

import static javafx.scene.input.KeyCode.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.shape.TriangleMesh;

import java.util.ArrayList;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

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
    final Xform world = new Xform();
    
    CameraManager camera = new CameraManager();
    
    TextFieldWindow text;
    EFieldManager eFieldManager;

    String xmlFileName = "SimpleXML";

    double timeStepMCS = 1;

    Point[] points;
    List<GridComponent> parts;

    List<Point> markedPoints;

    Stage primaryStage;

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

    Controller c;
    

    // Render vars
    double electronRadius = 0.2;

    private void buildElectrons(Point[] points) {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.BLACK);
        redMaterial.setSpecularColor(Color.DARKGREY);

        final PhongMaterial blackMaterial = new PhongMaterial();

        blackMaterial.setDiffuseColor(Color.DARKRED);
        blackMaterial.setSpecularColor(Color.RED);
        

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

    private void buildScene() {
        root.getChildren().add(world);
    }
    

    private void buildStage(Stage primaryStage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
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
                    camera.cameraXform.ry.setAngle(camera.cameraXform.ry.getAngle() - mouseDeltaX * modifierFactor * modifier * 2.0);  // +
                    camera.cameraXform.rx.setAngle(camera.cameraXform.rx.getAngle() + mouseDeltaY * modifierFactor * modifier * 2.0);  // -

                    text.output.put("Camera phi", String.valueOf(camera.cameraXform.ry.getAngle()));
                    text.output.put("Camera theta", String.valueOf(camera.cameraXform.rx.getAngle()));

                } else if (me.isSecondaryButtonDown()) {
                    double z = camera.camera.getTranslateZ();
                    double newZ = z + mouseDeltaX * modifierFactor * modifier;
                    camera.camera.setTranslateZ(newZ);
                } else if (me.isMiddleButtonDown()) {
                    camera.cameraXform2.t.setX(camera.cameraXform2.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3);  // -
                    camera.cameraXform2.t.setY(camera.cameraXform2.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3);  // -
                }
                text.compileOutput();
            }
        });
    }
    boolean flag = false;
    public ArrayList<Sphere> Deuterons = new ArrayList();

    private void handleKeyboard(Scene scene, Stage stage) {

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            double translateStep = 1.5;
            double rotateStep = 1;
            Box slice = eFieldManager.eFieldSlice;

            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case Z: // CTRL+X closes window
                        if (event.isControlDown()) {
                            // Close down window
                            stage.close();
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

                        final PhongMaterial deuteronMaterial = new PhongMaterial();

                        deuteronMaterial.setDiffuseColor(Color.PURPLE);
                        Sphere deuteron = new Sphere(1.0);

                        deuteron.setMaterial(deuteronMaterial);
                        
                        if (!flag) {
                            c = new Controller(points,annodeVoltage,cathodeVoltage);
                        }
                        Deuterons.add(deuteron);
                        
                        Point pos = new Point();
                        pos.x = 0;
                        pos.y = 18;
                        pos.z = 6;
                        c.addAtom(pos,Double.valueOf("3.34449439655E-27"));
                        
                        // addAtom code ends here
                        deuteron.setTranslateX(pos.x);
                        deuteron.setTranslateY(pos.y);
                        deuteron.setTranslateZ(pos.z);
                        world.getChildren().add(deuteron);
                        
                        if (!flag) {
                            flag = true;
                        } else {
                            break;
                        }
                        Runnable r = new Runnable() {
                            public void run() {
                                // Code for updating positions goes here
                                c.stepAllForeward(points, 0.01);
                                    System.out.println("Running once, size of Deuterons is " + Deuterons.size());
                                    for(int i = 0; i < Deuterons.size(); i++){
                                        Deuterons.get(i).setTranslateX(c.atoms[i].position.x);
                                        Deuterons.get(i).setTranslateY(c.atoms[i].position.y);
                                        Deuterons.get(i).setTranslateZ(c.atoms[i].position.z);
                                    }
                                
                            }
                        };

                        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                        executor.scheduleAtFixedRate(r, 0, 2, TimeUnit.MILLISECONDS);
                        break;
                    case F:
                        if (event.isControlDown()) {
                            if (!eFieldManager.eFieldBuilt) {
                                eFieldManager.buildEFieldStage(primaryStage, points);
                            } else {
                                System.out.println("Updating e-field!");
                                eFieldManager.updateEField(points);
                            }
                        } else {
                            if (!eFieldManager.eFieldBuilt) {
                                eFieldManager.buildEFieldSlice(text);
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
                                slice.setTranslateX(slice.getTranslateX() + step);
                                break;
                            case W:
                                slice.setTranslateY(slice.getTranslateY() + step);
                                break;
                            case E:
                                slice.setTranslateZ(slice.getTranslateZ() + step);
                                break;
                        }
                        if (eFieldManager.autoUpdate) {
                            eFieldManager.updateEField(points);
                        }
                        break;
                    case R:
                    case T:
                    case Y:
                        double rotStep = rotateStep;
                        if (event.isControlDown()) {
                            rotStep *= -1;
                        }
                        Rotate[] transforms = eFieldManager.eFieldTransforms;
                        switch (event.getCode()) {
                            case R:
                                transforms[0].setAngle(transforms[0].getAngle() + rotStep);
                                break;
                            case T:
                                transforms[1].setAngle(transforms[1].getAngle() + rotStep);
                                break;
                            case Y:
                                transforms[2].setAngle(transforms[2].getAngle() + rotStep);
                                break;
                        }
                        if (eFieldManager.autoUpdate) {
                            eFieldManager.updateEField(points);
                        }
                        break;
                    case U:
                        double scaleStep;

                        if (event.isControlDown()) {
                            scaleStep = 0.95;
                        } else {
                            scaleStep = 1.05;
                        }
                        
                        slice.setScaleX(slice.getScaleX() * scaleStep);
                        slice.setScaleY(slice.getScaleX() * scaleStep);
                        slice.setScaleZ(slice.getScaleX() * scaleStep);
                        if (eFieldManager.autoUpdate) {
                            eFieldManager.updateEField(points);
                        }
                        break;
                        
                    case I:
                        if (!eFieldManager.eFieldBuilt) {
                            break;
                        }
                        if (event.isControlDown()) {
                            slice.setScaleX(1.0);
                            slice.setScaleY(1.0);
                            slice.setScaleZ(1.0);
                            slice.setTranslateX(0);
                            slice.setTranslateY(0);
                            slice.setTranslateZ(0);
                            for (int i = 0; i < 3; i++) {
                                eFieldManager.eFieldTransforms[i].setAngle(0.0);
                            }
                        } else {
                            slice.setVisible(!slice.visibleProperty().getValue());
                        }
                        if (eFieldManager.autoUpdate) {
                            eFieldManager.updateEField(points);
                        }
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
            double revScale = chargeGroup.getChildren().get(i).getScaleX() * scale;
            revScale = Math.min(revScale, 40*electronRadius);
            revScale = Math.max(revScale, electronRadius/5);
            
            chargeGroup.getChildren().get(i).setScaleX(revScale);
            chargeGroup.getChildren().get(i).setScaleY(revScale);
            chargeGroup.getChildren().get(i).setScaleZ(revScale);

        }
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void start(Stage primaryStage) throws Exception {
        
        int pointCount = 1000;
        int optimizations = 20;

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
        long start = System.nanoTime();
        points = PointDistributer.shakeUpPoints(parts, pointCount, optimizations);
        
        EField.setkQ(annodeVoltage, cathodeVoltage, points);
        markedPoints = new ArrayList<>();

        double posAvgPotential = StatsGen.avgPotential(points, 1);
        double negAvgPotential = StatsGen.avgPotential(points, -1);

        Point q = new Point();
        q.x = 0.0;
        q.y = 0.0;
        q.z = 0.0;
  
        EField.setkQ(annodeVoltage, cathodeVoltage, points);

        text = new TextFieldWindow();
        
        text.output.put("Points", String.valueOf(points.length));
        text.output.put("Parts in grid", String.valueOf(parts.size()));
        text.output.put("Optimizations", String.valueOf(optimizations));
        text.output.put("Avg. potential of pos. points", String.valueOf(posAvgPotential * 1 / 1));
        text.output.put("Avg. potential of neg. points", String.valueOf(negAvgPotential));

        text.compileOutput();
        text.buildWindow(primaryStage);
        
        Point[] referencePoints = {};


        root.getChildren().add(camera.buildCamera());
        buildElectrons(points);
        buildAxes();
        buildReferencePoints(referencePoints);
        buildScene();
        buildStage(primaryStage);
        
        eFieldManager = new EFieldManager();
        
        root.getChildren().add(eFieldManager.buildEFieldSlice(text));
        eFieldManager.buildEFieldStage(primaryStage, points);

        Scene scene = new Scene(root, 1024, 768, true);
        scene.setFill(Color.GREY);
        handleMouse(scene, world);
        handleKeyboard(scene, primaryStage);

        primaryStage.setTitle("Fusor Electric Field Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.setCamera(camera.camera);
    }

     // Java main for when running without JavaFX launcher
    public static void main(String[] args) {
        launch(args);
    }
}
