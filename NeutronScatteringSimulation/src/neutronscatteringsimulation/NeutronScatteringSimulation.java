/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neutronscatteringsimulation;

import java.util.ArrayList;
import java.util.Random;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.ObservableFloatArray;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;

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

    ArrayList<ParaffinBlock> blocks = new ArrayList<>();

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

    private double minDistance(Vector3 O, Vector3 R, ParaffinBlock block) {
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

    private double minDistance(ParaffinBlock topBlock, ParaffinBlock bottomBlock, Vector3 R) {
        double minDistance;

        minDistance = Double.MAX_VALUE;
        for (Vector3 p : topBlock.points) {
            minDistance = Math.min(minDistance(p, R, bottomBlock), minDistance);
        }

        return minDistance;
    }

    private void moveCloser(ArrayList<ParaffinBlock> currentBlocks, ArrayList<ParaffinBlock> lastBlocks, Vector3 axis1) {
        double minDistance = Double.MAX_VALUE;
        for (ParaffinBlock b1 : currentBlocks) {
            for (ParaffinBlock b2 : lastBlocks) {
                minDistance = Math.min(minDistance, Math.min(minDistance(b1, b2, axis1), minDistance(b2, b1, axis1)));
            }
        }
        addAll(currentBlocks, new Vector3(-(minDistance), -(minDistance), -(minDistance)).multiply(axis1));
    }

    private void addAll(ArrayList<ParaffinBlock> bs, Vector3 v) {
        for (ParaffinBlock b : bs) {
            b.updatePoints(v);
        }
    }

    private ArrayList<ParaffinBlock> buildWall(int rows, int cols, Vector3 axis1, Vector3 axis2, double WIDTH, double LENGTH, double DEPTH, double TRISIZE, double MAXBUMP) {
        // all units in cm
        final Vector3 dimensions = new Vector3(WIDTH, LENGTH, DEPTH);

        ParaffinBlock block, lastBlock;
        MeshView view;
        ArrayList<ParaffinBlock> lastBlocks = new ArrayList<>();
        ArrayList<ParaffinBlock> currentBlocks = new ArrayList<>();
        ArrayList<ParaffinBlock> allBlocks = new ArrayList<>();

        double minDistance;
        for (int i = 0; i < rows; i++) {
            lastBlock = null;
            for (int j = 0; j < cols; j++) {
                block = new ParaffinBlock(WIDTH, LENGTH, DEPTH, TRISIZE, MAXBUMP);
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

        ArrayList<ParaffinBlock> newBlocks;
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
        return .015;
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
            showPoint(O, Color.BLUE);
            Vector3 R = randomDir();
            ParaffinBlock nextBlock = null;
            double minDistance, distance;
            double sigmaScattering, sigmaAbsorption, sigmaTotal, distanceCovered;
            while (true) {
                if (!insideBlock) {
                    minDistance = Double.MAX_VALUE;
                    nextBlock = null;
                    for (ParaffinBlock block : blocks) {
                        distance = minDistance(O, R, block);
                        if (distance < minDistance) {
                            minDistance = distance;
                            nextBlock = block;
                        }
                    }
                    if (minDistance == Double.MAX_VALUE) {
                        System.out.println("neutron escaped!!! watch out");
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
                        System.out.println("   absorpted - we're safe!");
                        showPoint(O, Color.GOLD);
                        break;
                    }
                } else {
                    showPoint(intersectionPoint, Color.PURPLE);
                    O = intersectionPoint.add(R.multiply(0.1));
                    System.out.println("moving on");
                    insideBlock = !insideBlock;
                }
            }
            System.out.println();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);
        buildCamera();

        buildIgloo();
        runSimulation(1);

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

}
