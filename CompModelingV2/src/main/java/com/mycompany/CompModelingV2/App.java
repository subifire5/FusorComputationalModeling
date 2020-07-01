package com.mycompany.CompModelingV2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import javafx.scene.shape.TriangleMesh;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableFloatArray;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Screen;

/* todo:
*  FIRST RIGHT NOW GO TO INPUT HANDLER, THERE IS A TODO THERE THAT'S VITAL
* Done. fix triangle bug: this bug is making it so that a triangle is missing
*    from the thin plates I put in. No charges appear on that triangle, 
*    NO BUENO, probably wouldn't even notice the bug if I only ran
*    the simulation on the chamber and grid, and that's a problem
* Done. fix the crowding bug: when randomly distributed, NO BALANCING AT ALL,
*   on the thin plates, a bunch of charges crowd on the bottom and ruin it for everyone
* Both problem 1 and 2 were part of the same bug; a bug in my binary search;
* When there were only two items in the arraylist left to check for my binary search
* the middle edge would automatically move to the left edge, regardless of which
* edge actually contained the result
* this was fixed.
* 3. UNITS. Add units to this for the thin plates in cm and volts and more
* 3. b. capacitance: Use this as an opportunity to check you calculations
*    by using the units to see what the capacitance should be, versus
*    what your program says
* 4. Show the average electric potential of the positive plate and the negative plate
*       those two numbers should approach each other.
* 5. create a CSV Reader for this program, so you can read and write to csvs
* 6. don't hardcode the file paths, get them as inputs from terminal or something
* 7. change Triangle Random Point formula to this: https://jsfiddle.net/jniac/fmx8bz9y/ 
* 8. Correct Charge values: Once you've finished charge balancing, the next
*    step is to find the difference in voltage between your annode and cathode
*    and keep that number as an offset. Then, go to hte center of the wire grid (like the middle)
*    That spot will have a really high electric potential. That spot is at the 
*    voltage difference you want. Find the number which you have to multiply
*    the electric potential there to get the voltage you want, and then
*    every time you get an electric potential, multiply it by that number.
* 9. Electric Potential --> Electric Potential Energy
*    Just switch from EP to EPE.
* 10. Split this program in two
*    Don't keep on having this program do both output of field
*    AAAAND input, split it eventually.
*  Graphs: make your graphs square instead of rectangular; the aspect ratio is off
*  your XY and XZ plane graphs are 100 x 100, but look 80 x 150
*  also, generate quiver graphs, the electric field graphs (using field lines on the quiver graphs basically)
*  generating electric field line graphs is non-trivial, and Dr. Whitmer may
*  have posted about it in Teams (Efield or Deuteron team)
*  Then prep for orbits, by first getting knowledge of euclid's method
*  Euclids method will give you a differential equation (given this field,
*  with this acceleration, and this start position and velocity, where it end)
*  It shouldn't stick to the grid as soon as it gets close. Think of it like this
*  If you have a skateboard on a smooth hill, you'll go up, then down then
*  back up, you won't just stop at the bottom immediately. same with the f
*  orbit, it won't go from the edge to the grid then stay at the grid, it'll
*  loop back around
*  The looping won't happen on electric field lines, because deuterium has mass
*  and the mass means momentum, so it'll take time for the acceleration to
*  turn the ions around once it's shot past/through the grid, so it'll slide off
*  the trajecory that field lines predict (they expect no mass)
*  Then once you've got that as close as possible, move to Runge Kutta
*  Runge Kutta takes a lot of time though
*  after RK, compute orbits fully
*  orbit lines are nearly infinitely thin, so they won't hit each other
*  instead, they'll hit the grid, or a triangle of the grid more likely
*  calculate when they'll do that.
 */
/**
 * JavaFX App
 */
public class App extends Application {

    //EDIT THESE
    double cap = 100000.0;
    double b = 1.1;
    //IMPORTANT CONTROLS:
    // wasd move camera 
    // left click drag circles the camera
    // right click drag zooms camera
    // q and e move camera up and down
    // Slice controls:
    // u left (+ ctrl, right)
    // i up (+ ctrl, down)
    // o forward (+ctrl, backward)
    //
    // v scale

    Group root = new Group();
    final Xform world = new Xform();
    final Camera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    final Xform axisGroup = new Xform();
    final Xform positiveCharges = new Xform();
    final Xform negativeCharges = new Xform();
    final Xform EFieldSlice = new Xform();
    Xform arrows = new Xform();
    final double CAMERA_NEAR_CLIP = 1;
    final double CAMERA_FAR_CLIP = 100000;
    final double CAMERA_INITIAL_DISTANCE = 5;
    final double NEGATIVE_SCALE = 1;
    final double POSITIVE_SCALE = 1;
    Vector POSITIVE_TRANSLATE = new Vector(200.0, 50.0, 20.0);
    Vector NEGATIVE_TRANSLATE = new Vector(200.0, 50.0, 20.0);
    final double CAMERA_INITIAL_Y_ANGLE = 0.0;
    final double CAMERA_INITIAL_X_ANGLE = 0.0;
    List<Sphere> slicePoints;
    Vector translateSlice = new Vector(150.0, 150.0, 0.0);
    Stage primaryStage;
    final double wModifier = 1;
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    // scale determines the distance of 1.0
    // if scale is set to 1, 1.0 = 1 meter
    // if scale is set to 0.01, 1.0 = 1 centimeter
    Double scaleDistance = 0.01; // meters per unit
    EField eField;

    @Override
    public void start(Stage stage) {

        /*String chamberFilePath = "chamber.stl";
        String gridFilePath = "FusorCubeGrid.stl";
        Geometry geometry = new Geometry(chamberFilePath, 1.0, gridFilePath, -35000.0);
        geometry.translateNegativeTriangles(new Vector(-30.0, 50.0, -80.0));
         */
        /*String leftPlatePath = "ThinPlate.stl";
        String rightPlatePath = "ThinRightPlate.stl";
        Geometry geometry = new Geometry(leftPlatePath, 1.0, rightPlatePath, -5.0);
        geometry.sumUpSurfaceArea();

        //Triangle[] triangles = testTriangleSet();
        ChargeDistributer chargeDistributer = new ChargeDistributer(geometry, scaleDistance, 100);
        chargeDistributer.balanceCharges(100);
        EFieldFileWriter writer = new EFieldFileWriter(chargeDistributer);
        writer.writeCSV("outputFile10.csv");
        */
        InputHandler input = new InputHandler();
        input.getInput();
        
        
        
        //Don't Read from Output file
        buildCharges(input.charges);

        //Read from outputFile 
        //EFieldFileParser parser = new EFieldFileParser();
        //buildCharges(parser.parseFile("outputFile.csv"));
        //eField = new EField(positiveCharges, negativeCharges, 1, -35000, scaleDistance, new Vector(0.0, 0.0, 0.0));
        eField = input.eField;
        buildCamera();
        buildAxes();
        buildEFieldSlice();
        buildWorld();
        primaryStage = new Stage();
        buildStage(primaryStage);
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Scene scene = new Scene(root, 640, 480, true);
        scene.setFill(Color.GREY);

        keyInput(scene, primaryStage);
        handleMouse(scene, root);
        primaryStage.setScene(scene);
        scene.setCamera(camera);
        primaryStage.show();

    }

    public Triangle[] testTriangleSet() {
        Triangle[] tList = new Triangle[4];
        Vector topLeftForward = new Vector(0.0, 10.0, 10.0);
        Vector topLeftBack = new Vector(0.0, 10.0, 0.0);
        Vector topRightForward = new Vector(10.0, 10.0, 10.0);
        Vector topRightBack = new Vector(10.0, 10.0, 0.0);
        Vector botLeftForward = new Vector(0.0, 0.0, 10.0);
        Vector botLeftBack = new Vector(0.0, 0.0, 0.0);
        Vector botRightForward = new Vector(10.0, 0.0, 10.0);
        Vector botRightBack = new Vector(10.0, 0.0, 0.0);

        tList[0] = new Triangle(topLeftForward, topRightForward, botLeftForward, 1);
        tList[1] = new Triangle(botLeftForward, botRightForward, topRightForward, 1);
        tList[2] = new Triangle(topLeftBack, topRightBack, botLeftBack, -1);
        tList[3] = new Triangle(botLeftBack, botRightBack, topRightBack, -1);
        return tList;
    }

    public void buildEFieldSlice() {
        List<Vector> slice = new ArrayList<Vector>();
        slicePoints = new ArrayList<Sphere>();
        Double sliceMax = null;
        for (Double i = 0.0; i < 100.0; i += 10.0) {
            for (Double j = 0.0; j < 100.0; j += 10.0) {
                for (Double k = 0.0; k < 10.0; k++) {
                    Vector v = new Vector(i, j, k);
                    v.plusEquals(translateSlice);
                    /*
                    Charge c = new Charge(v, 1);
                    Vector eSum = eField.forceOnCharge(c);
                    Double magnitude = eSum.norm();

                    if (sliceMax == null) {
                        sliceMax = magnitude;
                    }

                    if (sliceMax < magnitude) {
                        sliceMax = magnitude;
                    }
                     */
                    slice.add(v);
                }
            }
        }

        for (int i = 0; i < slice.size(); i++) {
            Vector v = slice.get(i);
            final Sphere s = new Sphere(1);
            //final PhongMaterial color = new PhongMaterial();
            s.setTranslateX(v.x);
            s.setTranslateY(v.y);
            s.setTranslateZ(v.z);
            PhongMaterial pm = new PhongMaterial();
            pm.setDiffuseColor(Color.WHITE);
            pm.setSpecularColor(Color.WHITE);
            s.setMaterial(pm);
            //Charge c = new Charge(v, 1);
            //Vector eSum = eField.forceOnCharge(c);
            //double norm = eSum.norm();

            //Color magnitude;
            //magnitude = new Color((norm / sliceMax), 0.0, 0.0, 1.0);
            //color.setSpecularColor(magnitude);
            //color.setDiffuseColor(magnitude);
            //s.setMaterial(color);
            //Vector endpoint = eSum.sum(v);
            /*Point3D startPoint = new Point3D(c.x, c.y, c.z);
            Point3D endPoint = new Point3D(endpoint.x, endpoint.y, endpoint.z);
            Cylinder arrow = createConnection(startPoint, endPoint);
            arrows.getChildren().add(arrow);
             */
            slicePoints.add(s);
            EFieldSlice.getChildren().add(s);
        }

        //world.getChildren().add(arrows);
        world.getChildren().add(EFieldSlice);
    }

    public Color getColor(Sphere s) {
        PhongMaterial material = new PhongMaterial();
        /*Transform t = s.getLocalToSceneTransform();
            
        Charge c = new Charge(s.getTranslateX()+t.getTx(), s.getTranslateY()+t.getTy(), s.getTranslateZ()+t.getTz(), 1);
        
        Vector eSum = eField.forceOnCharge(c);
         */
        Vector eSum = eField.forceOnCharge(s, 1);
        double length = eSum.norm();
        //System.out.println("norm / max: " + norm/max);
        return new Color(gradient(length), 0.0, 0.0, 1.0);
        // hard cap of 1.0
        // use function ln((norm/number)+1)
    }

    public double gradient(double d) {
        double x = d / cap;
        x += b;
        double y = getLesser(Math.log(x), 1.0);
        y = getGreater(y, 0.0);
        return y;
    }

    public double getLesser(double a, double b) {
        if (a < b) {
            return a;
        } else {
            return b;
        }
    }

    public double getGreater(double a, double b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    public Double getMax(List<Sphere> nodes) {
        Double max = null;

        for (int i = 0; i < nodes.size(); i++) {
            Sphere n = nodes.get(i);
            /*Transform t = n.getLocalToSceneTransform();
            
            Charge c = new Charge(n.getTranslateX()+t.getTx(), n.getTranslateY()+t.getTy(), n.getTranslateZ()+t.getTz(), 1);
            Vector eSum = eField.forceOnCharge(c);
             */
            Vector eSum = eField.forceOnCharge(n, 1);
            Double magnitude = eSum.norm();
            if (max == null) {
                max = magnitude;
            }
            if (max < magnitude) {
                max = magnitude;
            }
        }
        System.out.println("MAX: " + max);
        return max;
    }

    public void updateSlice() {
        //double max = getMax(slicePoints);
        //System.out.println("hohfi");

        for (int i = 0; i < slicePoints.size(); i++) {
            Sphere s = slicePoints.get(i);
            Color color = getColor(s);
            PhongMaterial pm = (PhongMaterial) s.getMaterial();
            //System.out.println("pm: " + pm);
            pm.setDiffuseColor(color);
            pm.setSpecularColor(color);
            s.setMaterial(pm);
            slicePoints.set(i, s);

            /*Vector endpoint = eSum.sum(new Vector(c.x, c.y, c.z));
            Point3D startPoint = new Point3D(c.x, c.y, c.z);
            Point3D endPoint = new Point3D(endpoint.x, endpoint.y, endpoint.z);
            Node m = arrows.getChildren().get(i);
            m = createConnection(startPoint, endPoint
             */
        }
    }
    // code for making a cylinder from one point to another
    //https://stackoverflow.com/questions/38799322/javafx-3d-transforming-cylinder-to-defined-start-and-end-points

    public Cylinder createConnection(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(0.1, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }

    public void buildStage(Stage primaryStage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());

    }

    public TriangleMesh importObject(String fileName) {
        Path path = Paths.get(fileName);
        StlMeshImporter meshImporter = new StlMeshImporter();
        File file = path.toFile();
        meshImporter.read(file);
        TriangleMesh mesh = meshImporter.getImport();
        meshImporter.close();
        return mesh;
    }

    public List<Triangle> getTriangles(TriangleMesh tMesh, int polarity) {

        // .getfaces/.getPOints/whatever all return Observable(Object)Arrays
        // to convert them into normal arrays
        // you pass them an array
        // then they return an array for you to use
        float[] points = null;
        points = tMesh.getPoints().toArray(points);
        int[] faceIndeces = null;
        faceIndeces = tMesh.getFaces().toArray(faceIndeces);
        List<Triangle> triangles = new ArrayList<Triangle>();
        System.out.println("Points mod 3: " + points.length % 3);
        System.out.println("Faces mod 6: " + faceIndeces.length % 6);
        System.out.println("Triangle Mesh Vertex Format: " + tMesh.getVertexFormat());
        System.out.println("point size: " + points.length);

        /*
        The Triangle Mesh Vertex Format is POINT_TEXCOORD in this case
        .getFaces returns an array of indeces into arrays
        .getPoints returns an array of floats, each one representing the x, y, or z of a point
        the array looks like this:
        [x1, y1, z1, x2, y2, z2,...]
        the .getFaces, however, is completely different
        what the indexes of .getFaces returns depends on the Vertex Format of your triangle mesh
        
        For example, the faces with VertexFormat.POINT_TEXCOORD that represent a single textured rectangle, using 2 triangles, have the following data order: [ 
        p0, t0, p1, t1, p3, t3, // First triangle of a textured rectangle 
        p1, t1, p2, t2, p3, t3 // Second triangle of a textured rectangle 
        ]
        where p0, p1, p2 and p3 are indices into the points array, n0, n1, n2 and
        n3 are indices into the normals array, and t0, t1, t2 and t3 are indices
        into the texCoords array. 
        
        so if you want a triangle, and our triangle meshes are in TEXCOORD,
        you move in sets of 6
        you get the first item in the faces array
        then use that index in the point array for x
        to get the y, you add one ot that index
        and z you add two
        then you get the third item in the faces array
        then the fifth item in the faces array;
        

        // this means poitns mod 9 should return 
         */
        for (int i = 0; i < faceIndeces.length; i += 6) {
            // three values make up a vector
            // three vectors (9 values) for a triangle
            Vector A = new Vector((double) points[faceIndeces[i] * 3], (double) points[faceIndeces[i] * 3 + 1], (double) points[faceIndeces[i] * 3 + 2]);
            Vector B = new Vector((double) points[faceIndeces[i + 2] * 3], (double) points[faceIndeces[i + 2] * 3 + 1], (double) points[faceIndeces[i + 2] * 3 + 2]);
            Vector C = new Vector((double) points[faceIndeces[i + 4] * 3], (double) points[faceIndeces[i + 4] * 3 + 1], (double) points[faceIndeces[i + 4] * 3 + 2]);

            Triangle t = new Triangle(A, B, C, polarity);
            /*
            if (polarity == 1) {
                for (int j = 0; j < t.points.norm; j++) {
                    // flip the x and y axis
                    Double oldy = t.points[j].y;
                    t.points[j].y = t.points[j].z;
                    t.points[j].z = oldy;
                    //reflecting
                    t.points[j].y *= -1;
                    //scaling
                    t.points[j].scale(1.25);
                    t.points[j].scale(POSITIVE_SCALE);
                    
                    /// translating
                    t.points[j].plusEquals(new Vector(35.0, 0.0, -35.0));
                    t.points[j].plusEquals(POSITIVE_TRANSLATE);
                }
            } else {
                for (int j = 0; j < t.points.norm; j++) {
                    //scaling
                    t.points[j].scale(NEGATIVE_SCALE);
                    
                    /// translating
                    t.points[j].plusEquals(new Vector(0.0, 50.0, 0.0));
                    t.points[j].plusEquals(NEGATIVE_TRANSLATE);
                }
            }*/

            t.getSurfaceArea();
            triangles.add(t);

        }
        return triangles;
    }

    public void scaleCharges(double scale) {
        positiveCharges.setScaleX(positiveCharges.getScaleX() + scale);
        positiveCharges.setScaleY(positiveCharges.getScaleY() + scale);
        positiveCharges.setScaleZ(positiveCharges.getScaleZ() + scale);
        negativeCharges.setScaleX(negativeCharges.getScaleX() + scale);
        negativeCharges.setScaleY(negativeCharges.getScaleY() + scale);
        negativeCharges.setScaleZ(negativeCharges.getScaleZ() + scale);
    }

    public void buildCamera() {
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

    public void buildCharges(Charge[] charges) {
        world.getChildren().add(negativeCharges);
        world.getChildren().add(positiveCharges);
        final PhongMaterial BlueColor = new PhongMaterial(Color.BLUE);
        BlueColor.setSpecularColor(Color.BLUE);
        final PhongMaterial RedColor = new PhongMaterial(Color.RED);
        RedColor.setSpecularColor(Color.RED);
        for (int i = 0; i < charges.length; i++) {
            Charge c = charges[i];
            if (c.polarity < 0) {
                final Sphere s = new Sphere(0.4);
                s.setMaterial(RedColor);
                s.setTranslateX(c.x);
                s.setTranslateY(c.y);
                s.setTranslateZ(c.z);
                negativeCharges.getChildren().add(s);
                negativeCharges.setScale(NEGATIVE_SCALE);
            } else {
                final Sphere s = new Sphere(0.4);
                s.setMaterial(BlueColor);
                s.setTranslateX(c.x);
                s.setTranslateY(c.y);
                s.setTranslateZ(c.z);
                positiveCharges.getChildren().add(s);
            }
        }

    }

    public void buildWorld() {
        root.getChildren().add(world);
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

                double modifier = 1;
                double modifierFactor = 1;

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

    public void keyInput(Scene scene, Stage stage) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            double translateStep = 15;
            double rotateStep = 1;
            Double step;
            Double rotStep;

            @Override
            public void handle(KeyEvent event) {
                if (event.isControlDown()) {
                    step = translateStep * -1.5;
                } else {
                    step = translateStep;
                }
                rotStep = rotateStep;
                if (event.isControlDown()) {
                    rotStep = rotateStep * -1;
                } else {
                    rotStep = rotateStep;
                }
                switch (event.getCode()) {
                    case X: // CTRL+X closes window
                        if (event.isControlDown()) {
                            // Close down window
                            stage.close();
                        }
                        break;
                    case PAGE_UP: // Get larger
                        scaleCharges(1.1);
                        break;
                    case PAGE_DOWN: // Get smaller
                        scaleCharges(-1.1);
                        break;
                    case W:
                        cameraXform.t.setZ(cameraXform.t.getZ() + wModifier);
                        break;
                    case A:
                        cameraXform.t.setX(cameraXform.t.getX() + wModifier);
                        break;
                    case S:
                        cameraXform.t.setZ(cameraXform.t.getZ() - wModifier);
                        break;
                    case D:
                        cameraXform.t.setX(cameraXform.t.getX() - wModifier);
                        break;
                    case E:
                        cameraXform.t.setY(cameraXform.t.getY() - wModifier);
                        break;
                    case Q:
                        cameraXform.t.setY(cameraXform.t.getY() + wModifier);
                        break;
                    case U:

                        EFieldSlice.setTranslateX(EFieldSlice.getTranslateX() + step);
                        updateSlice();

                        break;
                    case I:
                        EFieldSlice.setTranslateY(EFieldSlice.getTranslateY() + step);
                        updateSlice();
                        break;
                    case O:
                        EFieldSlice.setTranslateZ(EFieldSlice.getTranslateZ() + step);
                        updateSlice();
                        break;

                    case R:
                        EFieldSlice.rx.setAngle(EFieldSlice.rx.getAngle() + rotStep);
                        updateSlice();
                        break;
                    case T:
                        EFieldSlice.ry.setAngle(EFieldSlice.ry.getAngle() + rotStep);
                        updateSlice();
                        break;
                    case Y:
                        EFieldSlice.rz.setAngle(EFieldSlice.rz.getAngle() + rotStep);
                        updateSlice();
                        break;

                    case V:
                        double scaleStep;

                        if (event.isControlDown()) {
                            scaleStep = 0.95;
                        } else {
                            scaleStep = 1.05;
                        }
                        EFieldSlice.setScaleX(EFieldSlice.getScaleX() * scaleStep);
                        EFieldSlice.setScaleY(EFieldSlice.getScaleX() * scaleStep);
                        EFieldSlice.setScaleZ(EFieldSlice.getScaleX() * scaleStep);
                        updateSlice();
                        break;

                }
            }
        });
    }

    public static void main(String[] args) {

        launch();
    }

}
