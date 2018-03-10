/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.ObservableFloatArray;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 *
 * @author jfellows
 */
public class NeutronScatteringSimulation extends Application {

    static Random random = new Random();

    final Group root = new Group();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -400;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.01;
    private static final double CAMERA_FAR_CLIP = 100000.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;

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

    private void handleMouse(Scene scene, final Node root) {
        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged((MouseEvent me) -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);

            if (me.isPrimaryButtonDown()) {
                cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * ROTATION_SPEED);
                cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * ROTATION_SPEED);
            } else if (me.isSecondaryButtonDown()) {
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX * MOUSE_SPEED;
                camera.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown()) {
                cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * TRACK_SPEED);
                cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * TRACK_SPEED);
            }
        });
    }

    private final double LENGTH = 150;

    private void buildAxes() {
        Cylinder cx = new Cylinder(1, LENGTH);
        PhongMaterial pmx = new PhongMaterial(Color.RED);
        cx.setMaterial(pmx);
        cx.setRotationAxis(new Point3D(0, 0, 1));
        cx.setRotate(90);
        world.getChildren().add(cx);

        Cylinder cy = new Cylinder(1, LENGTH);
        PhongMaterial pmy = new PhongMaterial(Color.GREEN);
        cy.setMaterial(pmy);
        world.getChildren().add(cy);

        Cylinder cz = new Cylinder(1, LENGTH);
        PhongMaterial pmz = new PhongMaterial(Color.BLUE);
        cz.setMaterial(pmz);
        cz.setRotationAxis(new Point3D(1, 0, 0));
        cz.setRotate(90);
        world.getChildren().add(cz);
    }

    private void showPoint(Point3D p, Color c, double r) {
        PhongMaterial red = new PhongMaterial(c);
        Sphere sphere = new Sphere(r);
        sphere.setTranslateX(p.getX());
        sphere.setTranslateY(p.getY());
        sphere.setTranslateZ(p.getZ());
        sphere.setMaterial(red);
        world.getChildren().addAll(sphere);
    }

    private void showPoints(TriangleMesh mesh) {
        ObservableFloatArray points = mesh.getPoints();
        PhongMaterial red = new PhongMaterial(Color.RED);

        Sphere sphere;
        for (int i = 0; i < points.size(); i += 3) {
            sphere = new Sphere(1);
            sphere.setTranslateX(points.get(i));
            sphere.setTranslateY(points.get(i + 1));
            sphere.setTranslateZ(points.get(i + 2));
            sphere.setMaterial(red);
            world.getChildren().addAll(sphere);
        }
    }

    private void drawLine(Point3D p1, Point3D p2, Color c) {
        Point3D v = p2.subtract(p1);

        Cylinder line = new Cylinder(1, v.magnitude());
        PhongMaterial pm = new PhongMaterial(c);
        line.setMaterial(pm);

        double phi = Math.atan2(v.getX(), v.getY()) * 180 / Math.PI;
        double theta = Math.acos(v.getZ() / v.magnitude()) * 180 / Math.PI;

        line.getTransforms().add(new Translate((p2.getX() + p1.getX()) / 2, (p2.getY() + p1.getY()) / 2, (p2.getZ() + p1.getZ()) / 2));
        line.getTransforms().add(new Rotate(180 - phi, new Point3D(0, 0, 1)));
        line.getTransforms().add(new Rotate(theta - 90, new Point3D(1, 0, 0)));
        world.getChildren().add(line);
    }

    private void showBoundingSphere(Block b) {
        Sphere s = new Sphere(b.radius);
        s.setTranslateX(b.center.getX());
        s.setTranslateY(b.center.getY());
        s.setTranslateZ(b.center.getZ());
        s.setMaterial(new PhongMaterial(new Color(0, 0, 1, .5)));
        world.getChildren().add(s);
    }

    private boolean boundingSpheresIntersect(Block b1, Block b2) {
        return b1.center.distance(b2.center) < b1.radius + b2.radius;
    }

    private boolean rayIntersectsBoundingSphere(Point3D O, Point3D R, Block b) {
        Point3D L = b.center.subtract(O);
        if (L.magnitude() < b.radius) {
            return true;
        }
        double tca = L.dotProduct(R);
        if (tca < 0) {
            return false;
        }
        double d2 = L.dotProduct(L) - tca * tca;
        return d2 < b.radius * b.radius;
    }

    private double minDistance(Point3D O, Point3D R, Block block) {
        double minDistance = Double.MAX_VALUE;
        double D, t, distance;
        Point3D v0, v1, v2;
        Point3D edge0, edge1, edge2;
        Point3D N, P, C0, C1, C2;

        for (Block.Face f : block.faces) {
            v0 = block.points.get(f.p0);
            v1 = block.points.get(f.p1);
            v2 = block.points.get(f.p2);

            edge0 = v1.subtract(v0);
            edge1 = v2.subtract(v1);
            edge2 = v0.subtract(v2);

            N = edge0.crossProduct(edge1).normalize();

            D = -(N.dotProduct(v0));
            t = (N.dotProduct(O) + D) / -(N.dotProduct(R));

            if (t < 0) {
                continue;
            }

            P = O.add(R.multiply(t));

            // is P in the triangle?
            C0 = P.subtract(v0);
            C1 = P.subtract(v1);
            C2 = P.subtract(v2);

            if (N.dotProduct(edge0.crossProduct(C0)) > 0
                    && N.dotProduct(edge1.crossProduct(C1)) > 0
                    && N.dotProduct(edge2.crossProduct(C2)) > 0) {
                distance = P.distance(O);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        return minDistance;
    }

    private double minDistance(Block topBlock, Block bottomBlock, Point3D R) {
        double minDistance;

        minDistance = Double.MAX_VALUE;
        for (Point3D p : topBlock.points) {
            minDistance = Math.min(minDistance(p, R, bottomBlock), minDistance);
        }

        return minDistance;
    }

    private void collapseIgloo() {
        // sort blocks by "height"
        blocks.sort(Comparator.comparing(a -> a.center.getY()));

        double minDistance, distance;
        Point3D R = new Point3D(0, -1, 0);
        for (int i = 0; i < blocks.size(); i++) {
            minDistance = Double.MAX_VALUE;
            for (int j = i - 1; j >= 0; j--) {
                if (boundingSpheresIntersect(blocks.get(i), blocks.get(j))) {
                    distance = minDistance(blocks.get(i), blocks.get(j), R);
                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                }
            }
            if (minDistance != Double.MAX_VALUE) {
                blocks.get(i).move(R.multiply(minDistance));
            }
        }
    }

    ArrayList<Block> blocks;
    private final boolean LINE_MODE = true;

    private void loadIgloo(String filename) {
        ArrayList<TriangleMesh> meshes;
        try {
            meshes = ObjConverter.convert(new File(filename));
            blocks = new ArrayList<>();
            MeshView view;
            Block block;
            for (TriangleMesh mesh : meshes) {
                block = new FragmentTiler(mesh, 2, 3);
                view = new MeshView(block);
                world.getChildren().add(view);
                if (LINE_MODE) {
                    view.setDrawMode(DrawMode.LINE);
                }
                blocks.add(block);
            }
        } catch (IOException ex) {
            Logger.getLogger(NeutronScatteringSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Point3D randomDir(double magnitude) {
        double theta = random.nextDouble() * 2 * Math.PI;
        double phi = Math.acos(random.nextDouble() * 2 - 1);
        return new Point3D(magnitude * Math.cos(theta) * Math.sin(phi), magnitude * Math.sin(theta) * Math.sin(phi), magnitude * Math.cos(phi));
    }

    private double sigmaScatteringAir(Point3D R) {
        return 0.002;
    }

    private double sigmaAbsorptionAir(Point3D R) {
        return 0.0005;
    }

    private double sigmaScatteringParaffin(Point3D R) {
        return 0.4;
    }

    private double sigmaAbsorptionParaffin(Point3D R) {
        return 0.02;
    }
    
    double BOLTZMANN_CONSTANT = 8.617 * 0.0001; // eV/K
    double ROOM_TEMPERATURE = 293.15; // K
    double PROTON_MASS = 1.007276; // u
    double NEUTRON_MASS = 1.008664; // u

    private Point3D scatter(Point3D neutron) {
        double protonEnergy = random.nextGaussian() * ((BOLTZMANN_CONSTANT * ROOM_TEMPERATURE) / 2);
        Point3D proton = randomDir(protonEnergy);
        Point3D referenceFrame = neutron.multiply(NEUTRON_MASS).add(proton.multiply(PROTON_MASS)).multiply(1 / (NEUTRON_MASS + PROTON_MASS));
        Point3D neutronRef = neutron.subtract(referenceFrame);
        Point3D scatteredRef = randomDir(neutronRef.magnitude());
        Point3D scattered = scatteredRef.add(referenceFrame);
        return scattered;
    }

    // units in eV
    double THERMAL_NEUTRON_ENERGY = 0.025;
    double INITIAL_NEUTRON_ENERGY = 2.45 * 100_000_000;
    ArrayList<NeutronResultData> results;

    private void runSimulation(int numNeutrons) {
        results = new ArrayList<>();
        for (; numNeutrons > 0; numNeutrons--) {
            Color c = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble(), .5);
            boolean insideBlock = false;
            Point3D O = new Point3D(0, 0, 0);
            showPoint(O, Color.BLUE, 1.5);
            Point3D R = randomDir(INITIAL_NEUTRON_ENERGY);
            Block nextBlock = null;
            double minDistance, distance;
            double sigmaScattering, sigmaAbsorption, sigmaTotal, distanceCovered;
            while (true) {
                if (!insideBlock) {
                    minDistance = Double.MAX_VALUE;
                    nextBlock = null;
                    for (Block block : blocks) {
                        if (rayIntersectsBoundingSphere(O, R, block)) {
                            distance = minDistance(O, R, block);
                            if (distance < minDistance) {
                                minDistance = distance;
                                nextBlock = block;
                            }
                        }
                    }
                    sigmaScattering = sigmaScatteringAir(R);
                    sigmaAbsorption = sigmaAbsorptionAir(R);
                } else {
                    minDistance = minDistance(O, R, nextBlock);

                    sigmaScattering = sigmaScatteringParaffin(R);
                    sigmaAbsorption = sigmaAbsorptionParaffin(R);
                }
                if (minDistance == Double.MAX_VALUE) {
                    // neutron escaped
                    drawLine(O, O.add(R.normalize().multiply(LENGTH)), c);
                    results.add(new NeutronResultData(numNeutrons, NeutronResult.ESCAPED, R.magnitude()));
                    break;
                }
                Point3D intersectionPoint = O.add(R.normalize().multiply(minDistance));
                sigmaTotal = sigmaScattering + sigmaAbsorption;
                distanceCovered = -(1 / sigmaTotal) * Math.log(random.nextDouble());
                Point3D pastO = O;

                if (distanceCovered < minDistance) {
                    O = O.add(R.normalize().multiply(distanceCovered));
                    drawLine(pastO, O, c);
                    // hit hydrogen
                    if ((sigmaScattering / sigmaTotal) > random.nextDouble()) {
                        // scattering
                        R = scatter(R);
                        showPoint(O, Color.GREEN, 1.5);
                    } else {
                        // absorbed
                        showPoint(O, Color.GOLD, 1.5);
                        results.add(new NeutronResultData(numNeutrons, NeutronResult.ABSORBED, R.magnitude()));
                        break;
                    }
                } else {
                    showPoint(intersectionPoint, Color.PURPLE, 1.5);
                    O = intersectionPoint.add(R.normalize().multiply(0.01));
                    drawLine(pastO, O, c);
                    // going into or out of a block
                    insideBlock = !insideBlock;
                }
            }
        }
    }
    
    private void writeResults() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd kk-mm-ss").format(new Date());
        File file = new File(timestamp + ".csv");
        try {
            file.createNewFile();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Number,Status,Energy (eV)\n");
                for (NeutronResultData data : results) {
                    writer.write(data + "\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(NeutronScatteringSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);
        buildCamera();

        // build and run simulation
        buildAxes();
        loadIgloo("test.obj");
        collapseIgloo();
        runSimulation(100);
        writeResults();

        Scene scene = new Scene(root, 1024, 768, true);
        scene.setFill(Color.LIGHTGRAY);
        handleMouse(scene, world);

        primaryStage.setTitle("Neutron Scattering Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setCamera(camera);
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

    private enum NeutronResult {
        ESCAPED, ABSORBED
    }

    private static class NeutronResultData {

        final int num;
        final NeutronResult result;
        final double escapeEnergy;

        public NeutronResultData(int num, NeutronResult result, double escapeEnergy) {
            this.num = num;
            this.result = result;
            this.escapeEnergy = escapeEnergy;
        }
        
        @Override
        public String toString() {
            return String.format("%d,%s,%.2f", num, result, escapeEnergy);
        }
    }
}
