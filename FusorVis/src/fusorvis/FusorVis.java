
package fusorvis;

import fusorcompmodeling.*;

import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.event.EventHandler;
import static javafx.scene.input.KeyCode.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCombination;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
/**
 *
 * @author guberti
 */
 
public class FusorVis extends Application {
    
    final Group root = new Group();
    final Xform chargeGroup = new Xform();
    final Xform componentGroup = new Xform();
    final Xform axisGroup = new Xform();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    
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
    
    private void buildWireComponents(List<GridComponent> parts) {
        final PhongMaterial wireMaterial = new PhongMaterial();
        wireMaterial.setDiffuseColor(Color.LIGHTSLATEGREY);
        wireMaterial.setSpecularColor(Color.LIGHTGREY);
        
        for (GridComponent part : parts) {
            if (part.type == ComponentType.Cylinder) {
                // Render a cylinder
                final Cylinder c = new Cylinder(part.radius, part.height);
                c.setMaterial(wireMaterial);
                c.setTranslateX(part.pos.x);
                // Need to add half height because JavaFX centers are at
                // the centers of cylinders, not at the bases
                c.setTranslateY(part.pos.y + part.height/2);
                c.setTranslateZ(part.pos.z);
                // Apply rotations
                c.getTransforms().add(new Rotate(radToDeg(-part.pos.theta),part.pos.x,part.pos.y-part.height/2,part.pos.z,Rotate.Z_AXIS));
                c.getTransforms().add(new Rotate(radToDeg(part.pos.phi),part.pos.x,part.pos.y-part.height/2,part.pos.z,Rotate.X_AXIS));
                componentGroup.getChildren().add(c);
            }
        }
        componentGroup.setVisible(true);
        world.getChildren().addAll(componentGroup);
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
        final Sphere posZAxis = new Sphere(2.0);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);
        posXAxis.setMaterial(redMaterial);
        posXAxis.setTranslateX(120);
        posZAxis.setMaterial(blueMaterial);
        posZAxis.setTranslateZ(120);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis, posXAxis, posZAxis);
        world.getChildren().addAll(axisGroup);
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
    
    // Helper functions
    private double radToDeg(double radians) {
        return (radians*180)/Math.PI;
    }
    private void handleMouse(Scene scene, final Node root) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
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
                } else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX * modifierFactor * modifier;
                    camera.setTranslateZ(newZ);
                } else if (me.isMiddleButtonDown()) {
                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3);  // -
                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3);  // -
                }
            }
        });
    }
    private void handleKeyboard(Scene scene, Stage stage) {
        final boolean moveCamera = true;
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case X: // CTRL+X closes window
                        if (event.isControlDown()) {
                            // Close down window
                            stage.close();
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
                }
            }
        });
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        XMLParser p = new XMLParser("SimpleXML.xml");
        List<GridComponent> parts = p.parseObjects();
        
        Point[] points = PointDistributer.shakeUpPoints(parts, 2000, 0);

        buildCamera();
        buildElectrons(points);
        buildWireComponents(parts);
        buildAxes();
        buildScene();

        Scene scene = new Scene(root, 1024, 768, true);
        scene.setFill(Color.GREY);
        handleMouse(scene, world);
        handleKeyboard(scene, primaryStage);
        
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreenExitHint("");
        
        primaryStage.setTitle("Fusor Electric Field Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setFullScreen(true);
        scene.setCamera(camera);
    }
    
    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}