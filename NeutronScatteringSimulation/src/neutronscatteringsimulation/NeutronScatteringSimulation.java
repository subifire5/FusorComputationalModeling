/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
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
import javafx.scene.shape.Box;
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

    ArrayList<Block> blocks = new ArrayList<>();

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

    private void showPoint(Vector3 p, Color c) {
        PhongMaterial red = new PhongMaterial(c);
        Sphere sphere = new Sphere(1);
        sphere.setTranslateX(p.x);
        sphere.setTranslateY(p.y);
        sphere.setTranslateZ(p.z);
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

    private double minDistance(Vector3 O, Vector3 R, Block block) {
        double minDistance = Double.MAX_VALUE;
        double D, t, distance;
        Vector3 v0, v1, v2;
        Vector3 edge0, edge1, edge2;
        Vector3 N, P, C0, C1, C2;

        for (Face f : block.faces) {
            v0 = block.points.get(f.p1);
            v1 = block.points.get(f.p2);
            v2 = block.points.get(f.p3);

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
//                    System.out.println("O: " + O);
//                    System.out.println("P: " + P);
//                    System.out.println("N: " + N);
//                    System.out.println("D: " + D);
//                    System.out.println("distance: " + distance);
//                    System.out.println("v0: " + v0);
//                    System.out.println("v1: " + v1);
//                    System.out.println("v2: " + v2);
//                    System.out.println("t: " + t);
//                    System.out.println("Ax + By + Cz + D for P = " + (N.x * P.x + N.y * P.y + N.z * P.z + D));
//                    System.out.println("Ax + By + Cz + D for v0 = " + (N.x * v0.x + N.y * v0.y + N.z * v0.z + D));
//                    System.out.println();
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        return minDistance;
    }

    private double minDistance(Block topBlock, Block bottomBlock, Vector3 R) {
        double minDistance;

        minDistance = Double.MAX_VALUE;
        for (Vector3 p : topBlock.points) {
            minDistance = Math.min(minDistance(p, R, bottomBlock), minDistance);
        }

        return minDistance;
    }

    private void moveCloser(ArrayList<Block> currentBlocks, ArrayList<Block> lastBlocks, Vector3 axis1) {
        double minDistance = Double.MAX_VALUE;
        for (Block b1 : currentBlocks) {
            for (Block b2 : lastBlocks) {
                minDistance = Math.min(minDistance, Math.min(minDistance(b1, b2, axis1), minDistance(b2, b1, axis1)));
            }
        }
        addAll(currentBlocks, new Vector3(-(minDistance), -(minDistance), -(minDistance)).multiply(axis1));
    }

    private void addAll(ArrayList<Block> bs, Vector3 v) {
        for (Block b : bs) {
            b.updatePoints(v);
        }
    }

    private ArrayList<Block> buildWall(int rows, int cols, Vector3 axis1, Vector3 axis2, double WIDTH, double LENGTH, double DEPTH, double TRISIZE, double MAXBUMP) {
        // all units in cm
        final Vector3 dimensions = new Vector3(WIDTH, LENGTH, DEPTH);

        Block block, lastBlock;
        MeshView view;
        ArrayList<Block> lastBlocks = new ArrayList<>();
        ArrayList<Block> currentBlocks = new ArrayList<>();
        ArrayList<Block> allBlocks = new ArrayList<>();

        double minDistance;
        for (int i = 0; i < rows; i++) {
            lastBlock = null;
            for (int j = 0; j < cols; j++) {
                block = new Block(WIDTH, LENGTH, DEPTH, TRISIZE, MAXBUMP);
                block.updatePoints(dimensions.add(MAXBUMP * 2).multiply(axis1).multiply(i));
                block.updatePoints(dimensions.add(MAXBUMP * 2).multiply(axis2).multiply(j));

                if (lastBlock != null) {
                    minDistance = Math.min(minDistance(block, lastBlock, axis2), minDistance(lastBlock, block, axis2));
                    block.updatePoints(new Vector3(-(minDistance), -(minDistance), -(minDistance)).multiply(axis2));
                }

                view = new MeshView(block);
                view.setDrawMode(DrawMode.LINE);
                world.getChildren().add(view);
                lastBlock = block;
                currentBlocks.add(block);
            }
            if (lastBlocks.size() > 0) {
                moveCloser(currentBlocks, lastBlocks, axis1);
            }
            lastBlocks = new ArrayList<>(currentBlocks);
            while (currentBlocks.size() > 0) {
                allBlocks.add(currentBlocks.remove(0));
            }
        }

        return allBlocks;
    }

    private void buildIgloo() {
        final double WIDTH = 44;
        final double LENGTH = 22;
        final double DEPTH = LENGTH;
        final double TRISIZE = 5.5;
        final double MAXBUMP = 3;

        final Vector3 X = new Vector3(1, 0, 0);
        final Vector3 Y = new Vector3(0, 1, 0);
        final Vector3 Z = new Vector3(0, 0, -1);

        ArrayList<Block> newBlocks;
        blocks.addAll(buildWall(2, 5, X, Y, WIDTH, LENGTH, DEPTH, TRISIZE, MAXBUMP));

        newBlocks = buildWall(1, 3, X, Z, WIDTH, LENGTH, DEPTH, TRISIZE, MAXBUMP);
        addAll(newBlocks, new Vector3(LENGTH, 0, -(DEPTH) - MAXBUMP * 2));
        moveCloser(newBlocks, blocks, Z);
        blocks.addAll(newBlocks);

        newBlocks = buildWall(1, 1, X, Z, DEPTH, LENGTH, WIDTH, TRISIZE, MAXBUMP);
        addAll(newBlocks, new Vector3((DEPTH + MAXBUMP * 2) * 3, 0, -(WIDTH / 2) - (DEPTH + MAXBUMP * 2)));
        moveCloser(newBlocks, blocks, X);
        blocks.addAll(newBlocks);

        newBlocks = buildWall(1, 1, X, Z, WIDTH / 2, LENGTH, DEPTH, TRISIZE, MAXBUMP);
        addAll(newBlocks, new Vector3((WIDTH + MAXBUMP * 2) * 1 + DEPTH, 0, -(DEPTH + MAXBUMP * 2) * 3));
        moveCloser(newBlocks, blocks, Z);
        blocks.addAll(newBlocks);

        newBlocks = buildWall(1, 3, X, Z, WIDTH, LENGTH, DEPTH, TRISIZE, MAXBUMP);
        addAll(newBlocks, new Vector3(LENGTH, (LENGTH + MAXBUMP * 2) * 4, -(DEPTH) - MAXBUMP * 2));
        moveCloser(newBlocks, blocks, Z);
        blocks.addAll(newBlocks);

        newBlocks = buildWall(1, 1, X, Z, DEPTH, LENGTH, WIDTH, TRISIZE, MAXBUMP);
        addAll(newBlocks, new Vector3((DEPTH + MAXBUMP * 2) * 3, (LENGTH + MAXBUMP * 2) * 4, -(WIDTH / 2) - (DEPTH + MAXBUMP * 2)));
        moveCloser(newBlocks, blocks, X);
        blocks.addAll(newBlocks);

        newBlocks = buildWall(1, 1, X, Z, WIDTH / 2, LENGTH, DEPTH, TRISIZE, MAXBUMP);
        addAll(newBlocks, new Vector3((WIDTH + MAXBUMP * 2) * 1 + DEPTH, (LENGTH + MAXBUMP * 2) * 4, -(DEPTH + MAXBUMP * 2) * 3));
        moveCloser(newBlocks, blocks, Z);
        blocks.addAll(newBlocks);

        newBlocks = buildWall(2, 5, Z, Y, DEPTH, LENGTH, WIDTH, TRISIZE, MAXBUMP);
        addAll(newBlocks, new Vector3(0, 0, -(WIDTH) - MAXBUMP * 2));
        moveCloser(newBlocks, blocks, Z);
        blocks.addAll(newBlocks);

        newBlocks = buildWall(2, 5, Z, Y, DEPTH, LENGTH, WIDTH, TRISIZE, MAXBUMP);
        addAll(newBlocks, new Vector3((WIDTH + MAXBUMP * 2) * 2, 0, -(WIDTH / 2)));
        moveCloser(newBlocks, blocks, X);
        blocks.addAll(newBlocks);

        newBlocks = buildWall(2, 5, X, Y, WIDTH, LENGTH, DEPTH, TRISIZE, MAXBUMP);
        addAll(newBlocks, new Vector3(DEPTH + MAXBUMP * 2, 0, -(DEPTH + MAXBUMP * 2) * 4));
        moveCloser(newBlocks, blocks, Z);
        blocks.addAll(newBlocks);
    }

    private Vector3 randomDir() {
        double t = Math.acos(2 * random.nextDouble() - 1);
        double p = 2 * Math.PI * random.nextDouble();
        return new Vector3(Math.cos(t) * Math.sin(p), Math.sin(t) * Math.sin(p), Math.cos(p));
    }

    private double sigmaScatteringAir(Vector3 R) {
        return .004;
    }

    private double sigmaAbsorptionAir(Vector3 R) {
        return .002;
    }

    private double sigmaScatteringParaffin(Vector3 R) {
        return .55;
    }

    private double sigmaAbsorptionParaffin(Vector3 R) {
        return .007;
    }

    private Vector3 scatter(Vector3 R) {
        return randomDir();
    }

    private void runSimulation(int numNeutrons) {
        for (; numNeutrons > 0; numNeutrons--) {
            boolean insideBlock = false;
            Vector3 O = new Vector3(55, 55, -44);
            Vector3 last = O;
            showPoint(O, Color.BLUE);
            Vector3 R = randomDir();
            Block nextBlock = null;
            double minDistance, distance;
            double sigmaScattering, sigmaAbsorption, sigmaTotal, distanceCovered;
            while (true) {
                if (!insideBlock) {
                    minDistance = Double.MAX_VALUE;
                    nextBlock = null;
                    for (Block block : blocks) {
                        distance = minDistance(O, R, block);
                        if (distance < minDistance) {
                            minDistance = distance;
                            nextBlock = block;
                        }
                    }
                    if (minDistance == Double.MAX_VALUE) {
                        System.out.println("neutron escaped!!! watch out");
                        O = last.add(R.normalize().multiply(200));
                        drawLine(last, O, Color.BLACK);

                        break;
                    }
                    showPoints(nextBlock);

                    sigmaScattering = sigmaScatteringAir(R);
                    sigmaAbsorption = sigmaAbsorptionAir(R);
                } else {
                    minDistance = minDistance(O, R, nextBlock);

                    sigmaScattering = sigmaScatteringParaffin(R);
                    sigmaAbsorption = sigmaAbsorptionParaffin(R);
                }
                Vector3 intersectionPoint = O.add(R.multiply(minDistance));

                sigmaTotal = sigmaScattering + sigmaAbsorption;
                distanceCovered = -(1 / sigmaTotal) * Math.log(random.nextDouble());

                if (distanceCovered < minDistance) {
                    O = O.add(R.multiply(distanceCovered));
                    System.out.println("hit some hydrogen");
                    if ((sigmaScattering / sigmaTotal) > random.nextDouble()) {
                        System.out.println("   scattering!");
                        R = scatter(R);

                        showPoint(O, Color.GREEN);
                    } else {
                        System.out.println("   absorbed - we're safe!");
                        showPoint(O, Color.GOLD);
                        drawLine(last, O, Color.BLACK);
                        break;
                    }
                } else {
                    showPoint(intersectionPoint, Color.PURPLE);
                    O = intersectionPoint.add(R.multiply(0.1));
                    System.out.println("moving on");
                    insideBlock = !insideBlock;
                }
                drawLine(last, O, Color.BLACK);
                last = O;
            }
            System.out.println();
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        random.setSeed(System.currentTimeMillis());
        root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);
        buildCamera();

//        buildAxes();
//        example();
//
//        buildIgloo();
//        runSimulation(10);

        ArrayList<TriangleMesh> meshes = ObjConverter.convert(new File("test.obj"));
        MeshView view;
        for (TriangleMesh mesh : meshes) {
            view = new MeshView(mesh);
            world.getChildren().add(view);
        }

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

    void drawLine(Vector3 p1, Vector3 p2, Color c) {
        Vector3 v = p2.subtract(p1);
        final double REACHFACTOR = 0.97;

        Cylinder line = new Cylinder(1, v.length() * REACHFACTOR);
        PhongMaterial pm = new PhongMaterial(c);
        line.setMaterial(pm);

//        System.out.println("x:" + v.x + " y:" + v.y + " z:" + v.z);
        double phi = Math.atan2(v.x, v.y) * 180 / Math.PI;
        double theta = Math.acos(v.z / v.length()) * 180 / Math.PI;
//        System.out.println("Theta:" + theta + " Phi:" + phi);

        line.getTransforms().add(new Translate((p2.x + p1.x) / 2, (p2.y + p1.y) / 2, (p2.z + p1.z) / 2));
        line.getTransforms().add(new Rotate(180 - phi, new Point3D(0, 0, 1)));
        line.getTransforms().add(new Rotate(theta - 90, new Point3D(1, 0, 0)));
        world.getChildren().add(line);
    }

    void example() {
        Vector3 p1 = new Vector3(30, 30, 30);
        Vector3 p2 = new Vector3(-100, -60, -60);

        showPoint(p1, Color.RED);
        showPoint(p2, Color.RED);
        drawLine(p1, p2, Color.GOLDENROD);
    }

    void buildAxes() {
        Cylinder cx = new Cylinder(1, 100);
        PhongMaterial pmx = new PhongMaterial(Color.RED);
        cx.setMaterial(pmx);
        cx.setRotationAxis(new Point3D(0, 0, 1));
        cx.setRotate(90);
        world.getChildren().add(cx);
        showPoint(new Vector3(50, 0, 0), Color.BLACK);

        Cylinder cy = new Cylinder(1, 100);
        PhongMaterial pmy = new PhongMaterial(Color.GREEN);
        cy.setMaterial(pmy);
        world.getChildren().add(cy);
        showPoint(new Vector3(0, 50, 0), Color.BLACK);

        Cylinder cz = new Cylinder(1, 100);
        PhongMaterial pmz = new PhongMaterial(Color.BLUE);
        cz.setMaterial(pmz);
        cz.setRotationAxis(new Point3D(1, 0, 0));
        cz.setRotate(90);
        world.getChildren().add(cz);
        showPoint(new Vector3(0, 0, 50), Color.BLACK);
    }

}
