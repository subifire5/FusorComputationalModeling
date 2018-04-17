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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import static neutronscatteringsimulation.NeutronScatteringSimulation.random;

/**
 *
 * @author jfellows
 */
public class Simulation extends Task {

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

    private void handleMouse(SubScene scene, final Node root) {
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

    private final int numNeutrons;
    private final double INITIAL_NEUTRON_ENERGY;
    private final Material BLOCK;
    private final Material AIR;
    private final File IGLOO_FILE;
    private final Tiler TILER;
    private final double MAX_BUMP;
    private final boolean AXES;

    private final double LENGTH = 150;
    private final double BOLTZMANN_CONSTANT = 8.617 * 1E-4; // eV/K
    private final double ROOM_TEMPERATURE = 293.16; // K
    private final double PROTON_MASS = 1.007276; // u
    private final double NEUTRON_MASS = 1.008664; // u

    private ArrayList<Block> blocks;
    private ArrayList<NeutronResultData> results;

    public Simulation(int numNeutrons, double INITIAL_NEUTRON_ENERGY, Material BLOCK, Material AIR, File IGLOO_FILE, Tiler TILER, double MAX_BUMP, boolean AXES) {
        this.numNeutrons = numNeutrons;
        this.INITIAL_NEUTRON_ENERGY = INITIAL_NEUTRON_ENERGY;
        this.BLOCK = BLOCK;
        this.AIR = AIR;
        this.IGLOO_FILE = IGLOO_FILE;
        this.TILER = TILER;
        this.MAX_BUMP = MAX_BUMP;
        this.AXES = AXES;
    }
    
    public void setWireframe(boolean wireframe) {
        for (Node n : world.getChildren()) {
            if (n instanceof MeshView) {
                MeshView view = (MeshView) n;
                view.setDrawMode(wireframe ? DrawMode.LINE : DrawMode.FILL);
            }
        }
    }

    @Override
    public SubScene call() {
        root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);
        buildCamera();

        if (AXES) {
            buildAxes();
        }
        loadIgloo(IGLOO_FILE);
        collapseIgloo();
        runSimulation(numNeutrons);
        writeResults();

        SubScene scene = new SubScene(root, 1024, 768, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.LIGHTGRAY);
        handleMouse(scene, world);
        scene.setCamera(camera);
        return scene;
    }
    
    private void runSimulation(int numNeutrons) {
        results = new ArrayList<>();
        for (; numNeutrons > 0; numNeutrons--) {
            updateProgress(this.numNeutrons - numNeutrons, this.numNeutrons);
            Color c = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble(), .5);
            boolean insideBlock = false;
            Point3D O = new Point3D(0, 0, 0);
            showPoint(O, Color.BLUE, 1.5);
            Point3D R = randomDir(INITIAL_NEUTRON_ENERGY);
            Block nextBlock = null;
            double minDistance, distance;
            double sigmaScattering, sigmaTotal, distanceCovered;
            while (true) {
                if (!insideBlock) {
                    minDistance = Double.MAX_VALUE;
                    nextBlock = null;
                    for (Block block : blocks) {
                        if (rayIntersectsBoundingSphere(O, R.normalize(), block)) {
                            distance = minDistance(O, R.normalize(), block);
                            if (distance < minDistance) {
                                minDistance = distance;
                                nextBlock = block;
                            }
                        }
                    }
                    sigmaScattering = AIR.sigmaElasticScattering(R);
                    sigmaTotal = AIR.sigmaTotal(R);
                } else {
                    minDistance = minDistance(O, R, nextBlock);
                    sigmaScattering = BLOCK.sigmaElasticScattering(R);
                    sigmaTotal = BLOCK.sigmaTotal(R);
                }
                if (minDistance == Double.MAX_VALUE) {
                    // neutron escaped
                    drawLine(O, O.add(R.normalize().multiply(LENGTH)), c);
                    results.add(new NeutronResultData(numNeutrons, NeutronResult.ESCAPED, R.magnitude()));
                    break;
                }
                Point3D intersectionPoint = O.add(R.normalize().multiply(minDistance));
                distanceCovered = -(1 / sigmaTotal) * Math.log(random.nextDouble());
                Point3D pastO = O;

                if (distanceCovered < minDistance) {
                    O = O.add(R.normalize().multiply(distanceCovered));
                    drawLine(pastO, O, c);
                    // hit something
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
                    O = intersectionPoint.add(R.normalize().multiply(0.001));
                    drawLine(pastO, O, c);
                    // going into or out of a block
                    insideBlock = !insideBlock;
                }
            }
        }
    }

    private Point3D scatter(Point3D neutron) {
        double protonEnergy = random.nextGaussian() * ((BOLTZMANN_CONSTANT * ROOM_TEMPERATURE) / 2);
        Point3D proton = randomDir(protonEnergy);
        Point3D referenceFrame = neutron.multiply(NEUTRON_MASS).add(proton.multiply(PROTON_MASS)).multiply(1 / (NEUTRON_MASS + PROTON_MASS));
        Point3D neutronRef = neutron.subtract(referenceFrame);
        Point3D scatteredRef = randomDir(neutronRef.magnitude());
        Point3D scattered = scatteredRef.add(referenceFrame);
        return scattered;
    }

    private Point3D randomDir(double magnitude) {
        double theta = random.nextDouble() * 2 * Math.PI;
        double phi = Math.acos(random.nextDouble() * 2 - 1);
        return new Point3D(magnitude * Math.cos(theta) * Math.sin(phi), magnitude * Math.sin(theta) * Math.sin(phi), magnitude * Math.cos(phi));
    }

    private boolean rayIntersectsBoundingSphere(Point3D O, Point3D R, Block b) {
        Point3D vcO = b.center.subtract(O);
        double dot = vcO.dotProduct(R);

        if (dot < 0) {
            if (vcO.magnitude() >= b.radius) {
                return false;
            } else {
                Point3D Oc = O.add(R.multiply(dot));
                if (b.center.subtract(Oc).magnitude() >= b.radius) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showPoint(Point3D p, Color c, double r) {
        PhongMaterial red = new PhongMaterial(c);
        Sphere sphere = new Sphere(r, 10);
        sphere.setTranslateX(p.getX());
        sphere.setTranslateY(p.getY());
        sphere.setTranslateZ(p.getZ());
        sphere.setMaterial(red);
        world.getChildren().addAll(sphere);
    }

    private void drawLine(Point3D p1, Point3D p2, Color c) {
        Point3D v = p2.subtract(p1);

        Cylinder line = new Cylinder(1, v.magnitude(), 4);
        PhongMaterial pm = new PhongMaterial(c);
        line.setMaterial(pm);

        double phi = Math.atan2(v.getX(), v.getY()) * 180 / Math.PI;
        double theta = Math.acos(v.getZ() / v.magnitude()) * 180 / Math.PI;

        line.getTransforms().add(new Translate((p2.getX() + p1.getX()) / 2, (p2.getY() + p1.getY()) / 2, (p2.getZ() + p1.getZ()) / 2));
        line.getTransforms().add(new Rotate(180 - phi, new Point3D(0, 0, 1)));
        line.getTransforms().add(new Rotate(theta - 90, new Point3D(1, 0, 0)));
        world.getChildren().add(line);
    }

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

    private void loadIgloo(File file) {
        ArrayList<TriangleMesh> meshes;
        meshes = ObjConverter.convert(file);
        blocks = new ArrayList<>();
        MeshView view;
        Block block;
        for (TriangleMesh mesh : meshes) {
            block = new Block(mesh, TILER, MAX_BUMP);
            view = new MeshView(block);
            world.getChildren().add(view);
            blocks.add(block);
        }
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

    private boolean boundingSpheresIntersect(Block b1, Block b2) {
        return b1.center.distance(b2.center) < b1.radius + b2.radius;
    }

    private double minDistance(Block topBlock, Block bottomBlock, Point3D R) {
        double minDistance;

        minDistance = Double.MAX_VALUE;
        for (Point3D p : topBlock.points) {
            minDistance = Math.min(minDistance(p, R, bottomBlock), minDistance);
        }

        return minDistance;
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

    public ScatterChart buildChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        ScatterChart chart = new ScatterChart(xAxis, yAxis);
        chart.setTitle("Neutron Energies");
        xAxis.setLabel("Final Energy (eV)");
        yAxis.setLabel("Num");
        XYChart.Series absorbed = new XYChart.Series();
        XYChart.Series escaped = new XYChart.Series();
        absorbed.setName("Absorbed");
        escaped.setName("Escaped");
        
        for (NeutronResultData result : results) {
            XYChart.Series series = result.result == NeutronResult.ABSORBED ? absorbed : escaped;
            series.getData().add(new XYChart.Data(result.escapeEnergy, result.num));
        }
        
        chart.getData().addAll(absorbed, escaped);
        return chart;
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
