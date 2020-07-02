package org.eastsideprep.javaneutrons.core;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.util.StringConverter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.eastsideprep.javaneutrons.TestGM;
import org.eastsideprep.javaneutrons.materials.Air;

public class MonteCarloSimulation {

    static boolean parallel = true;

    public interface ProgressLambda {

        void reportProgress(int p);
    }

    public final Assembly assembly;
    private final Vector3D origin;
    private Vector3D direction;
    private final AtomicLong completed;
    public final LinkedTransferQueue visualizations;
    private final Group viewGroup;
    private final Group dynamicGroup;
    private Air air;
    public long lastCount;
    private int visualObjectLimit;
    private long start;
    public Material interstitialMaterial;
    public Material initialMaterial;
    public double initialEnergy;
    public ArrayList<Neutron> neutrons;
    public boolean trace;
    public boolean scatter;

    public static boolean visualLimitReached = false;

    public MonteCarloSimulation(Assembly assembly, Vector3D origin, Group g) {
        this(assembly, origin, null, Neutron.startingEnergyDD, null, null, g);
    }

    public MonteCarloSimulation(Assembly assembly, Vector3D origin, Vector3D direction, double initialEnergy, Material interstitialMaterial, Material initialMaterial, Group g) {
        this.assembly = assembly;
        this.origin = origin;
        this.visualizations = new LinkedTransferQueue<Node>();
        this.completed = new AtomicLong(0);
        this.viewGroup = g;
        this.dynamicGroup = new Group();
        this.viewGroup.getChildren().clear();
        // make some axes
        Util.Graphics.drawCoordSystem(g);
        // add the assembly objects
        this.viewGroup.getChildren().add(this.assembly.getGroup());
        // and a group for event visualiations
        this.viewGroup.getChildren().add(dynamicGroup);
        this.interstitialMaterial = interstitialMaterial;
        this.initialMaterial = initialMaterial;
        this.initialEnergy = initialEnergy;
        this.neutrons = new ArrayList<Neutron>();
        this.direction = direction;

        if (this.interstitialMaterial == null) {
            this.interstitialMaterial = Air.getInstance("Interstitial air");
        }

        if (this.initialMaterial == null) {
            // todo : find initial material from origin
            Event e = this.assembly.rayIntersect(origin, Vector3D.PLUS_I, false, visualizations);
            if (e != null && e.code == Event.Code.Entry) {
                this.initialMaterial = e.part.shape.getContactMaterial(e.face);
                if (this.initialMaterial != null) {
                    System.out.println("Determined initial medium to be " + this.initialMaterial.name);
                }
            }
            if (this.initialMaterial == null) {
                this.initialMaterial = this.interstitialMaterial;
                System.out.println("No inital medium found, defaulting to " + this.initialMaterial.name);
            }
        }
    }

    public void checkTallies() {
        double totalNeutronPath = this.neutrons.stream().mapToDouble(n -> n.totalPath).sum();
        double totalPartsPath = this.assembly.getParts().stream().mapToDouble(p -> p.getTotalPath()).sum();
        Set<Material> materials = this.assembly.getMaterials();
        double totalMaterialsPath = materials.stream().mapToDouble(m -> m == null ? 0.0 : m.totalFreePath).sum();
        Set<Material> interstitials = this.assembly.getContainedMaterials();
        interstitials.add(initialMaterial);
        interstitials.add(interstitialMaterial);
        double totalInterstitialsPath = interstitials.stream().mapToDouble(m -> m.totalFreePath).sum();

        System.out.println("Total neutron path: " + String.format("%6.3e", totalNeutronPath));
        System.out.println("Total parts path: " + String.format("%6.3e", totalPartsPath));
        System.out.println("Total materials path: " + String.format("%6.3e", totalMaterialsPath));
        System.out.println("Total interstitials path: " + String.format("%6.3e", totalInterstitialsPath));
        System.out.println("");
        System.out.println("n: " + String.format("%10.7e", totalNeutronPath) + " = p+i: " + String.format("%10.7e", totalPartsPath + totalInterstitialsPath));
        System.out.println("n: " + String.format("%10.7e", totalNeutronPath) + " = m+i: " + String.format("%10.7e", totalMaterialsPath + totalInterstitialsPath));
    }

    // this will be called from UI thread
    public long update() {
        //viewGroup.getChildren().remove(this.dynamicGroup);
        int size = this.dynamicGroup.getChildren().size();
        if (size < this.visualObjectLimit) {
            this.visualizations.drainTo(this.dynamicGroup.getChildren(), this.visualObjectLimit - size);
        } else {
            MonteCarloSimulation.visualLimitReached = true;
        }

        // drain the rest and forget about it
        LinkedList<Node> list = new LinkedList<>();
        this.visualizations.drainTo(list);

        return completed.get();
    }

    public void clearVisuals() {
        this.viewGroup.getChildren().remove(this.dynamicGroup);
        this.viewGroup.getChildren().remove(this.assembly.getGroup());
    }

    public void simulateNeutrons(long count, int visualObjectLimit) {
        this.lastCount = count;
        this.trace = count <= 10;
        this.visualObjectLimit = visualObjectLimit;
        MonteCarloSimulation.visualLimitReached = false;

        this.viewGroup.getChildren().remove(this.dynamicGroup);
        this.dynamicGroup.getChildren().clear();
        this.viewGroup.getChildren().add(this.dynamicGroup);

        System.out.println("");
        System.out.println("");
        System.out.println("Running new MC simulation for " + count + " neutrons ...");

        this.start = System.currentTimeMillis();

        assembly.resetDetectors();
        Collection<Material> c = Material.materials.values();
        c.stream().forEach(m -> m.resetDetector());

        // and enviroment (will count escaped neutrons)
        Environment.getInstance().reset();
        this.trace = count <= 10;
        
//        Stream<Neutron> sn = Stream.iterate(null, (n)->new Neutron(this.origin, 
//                this.direction != null ? this.direction : Util.Math.randomDir(), 
//                this.initialEnergy, this));

        if (count > 0) {
            for (long i = 0; i < count; i++) {
                Vector3D dir = this.direction != null ? this.direction : Util.Math.randomDir();
                Neutron n = new Neutron(this.origin, dir, this.initialEnergy, this);
                this.neutrons.add(n);
            }
            Stream<Neutron> sn = this.neutrons.stream();

            Thread th = new Thread(() -> {
                if (!MonteCarloSimulation.parallel || this.lastCount < 100) {
                    sn.forEach(n -> simulateNeutron(n));
                } else {
                    sn.parallel().forEach(n -> simulateNeutron(n));
                }
            });

            Platform.runLater(() -> th.start());
        } else {
            this.scatter = false;
            do {
                Vector3D dir = this.direction != null ? this.direction : Util.Math.randomDir();
                Neutron n = new Neutron(this.origin, dir, this.initialEnergy, this);
                n.mcs = this;
                simulateNeutron(n);
            } while (!this.scatter);
        }
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.start;
    }

    public void simulateNeutron(Neutron n) {
        this.assembly.evolveNeutronPath(n, this.visualizations, true);
        completed.incrementAndGet();
        if (trace) {
            System.out.println("");
        }
    }

    private class Formatter extends StringConverter<Number> {

        @Override
        public String toString(Number n) {
            return String.format("%6.3e", n.doubleValue());
        }

        @Override
        public Double fromString(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public Chart makeChart(String detector, String series, boolean log) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> bc;
        LineChart<String, Number> lc;
        Part p;
        Material m;
        DecimalFormat f;
        String e;
        double factor;

        if (detector != null) {
            switch (series) {
                case "Entry counts":
                    bc = new BarChart<>(xAxis, yAxis);
                    p = Part.getByName(detector);
                    f = new DecimalFormat("0.###E0");
                    e = f.format(p.getTotalDepositedEnergy() * 1e-4);
                    bc.setTitle("Part \"" + p.name + "\", total deposited energy: " + e + " J");
                    xAxis.setLabel("Energy (eV)");
                    yAxis.setLabel("Count");
                    bc.getData().add(p.entriesOverEnergy.makeSeries("Entry counts", log));
                    break;

                case "Fluence":
                    bc = new BarChart<>(xAxis, yAxis);
                    p = Part.getByName(detector);
                    if (p != null) {
                        f = new DecimalFormat("0.###E0");
                        e = f.format(p.getTotalFluence() / this.lastCount);
                        bc.setTitle("Part \"" + p.name + "\" (" + p.material.name + ")"
                                + "\nTotal fluence = " + e + " (n/cm^2)/src"
                                + ", src = " + this.lastCount);
                        xAxis.setLabel("Energy (eV)");
                        yAxis.setLabel("Fluence (n/cm^2)/src");
                        yAxis.setTickLabelFormatter(new Formatter());
                        bc.getData().add(p.fluenceOverEnergy.makeSeries("Fluence", this.lastCount, log));
                    } else {
                        factor = (4.0 / 3.0 * Math.PI * Math.pow(1000, 3) - this.assembly.getVolume());
                        m = Material.getByName("Air");
                        f = new DecimalFormat("0.###E0");
                        e = f.format(m.totalFreePath / (this.lastCount * factor));
                        bc.setTitle("Interstitial air"
                                + "\nTotal fluence = " + e + " (n/cm^2)/src"
                                + ", src = " + this.lastCount);
                        xAxis.setLabel("Energy (eV)");
                        yAxis.setLabel("Fluence (n/cm^2)/src");
                        yAxis.setTickLabelFormatter(new Formatter());
                        bc.getData().add(m.lengthOverEnergy.makeSeries("Fluence", this.lastCount * factor, log));
                    }
                    break;

                case "Event counts":
                    bc = new BarChart<>(xAxis, yAxis);
                    p = Part.getByName(detector);
                    if (p != null) {
                        bc.setTitle("Part \"" + p.name + "\", total events: " + p.getTotalEvents());
                        xAxis.setLabel("Energy (eV)");
                        yAxis.setLabel("Count");
                        bc.getData().add(p.scattersOverEnergyBefore.makeSeries("Scatter (before)", log));
                        bc.getData().add(p.scattersOverEnergyAfter.makeSeries("Scatter (after)", log));
                        bc.getData().add(p.capturesOverEnergy.makeSeries("Capture", log));
                    } else {
                        m = Material.getByName(detector);
                        bc.setTitle("Material \"" + m.name + "\", total events: " + m.totalEvents);
                        xAxis.setLabel("Energy (eV)");
                        yAxis.setLabel("Count");
                        bc.getData().add(m.scattersOverEnergyBefore.makeSeries("Scatter (before)", log));
                        bc.getData().add(m.scattersOverEnergyAfter.makeSeries("Scatter (after)", log));
                        bc.getData().add(m.capturesOverEnergy.makeSeries("Capture", log));
                    }
                    break;

                case "Path lengths":
                    factor = detector.equals("Air") ? (4.0 / 3.0 * Math.PI * Math.pow(1000, 3) - this.assembly.getVolume()) : 1;
                    bc = new BarChart<>(xAxis, yAxis);
                    m = Material.getByName(detector);
                    bc.setTitle("Material \"" + m.name + "\"\nMean free path: "
                            + (Math.round(100 * m.totalFreePath / m.pathCount) / 100.0) + " cm, "
                            + "Fluence: " + String.format("%6.3e", m.totalFreePath / (this.lastCount * factor))
                            + " (n/cm^2)/src = " + this.lastCount
                    );
                    xAxis.setLabel("Length (cm)");
                    yAxis.setLabel("Count");
                    bc.getData().add(m.lengths.makeSeries("Length"));
                    break;

                case "Cross-sections":
                    lc = new LineChart<>(xAxis, yAxis);
                    Isotope element = Isotope.getByName(detector);
                    lc.setTitle("Microscopic ross-sections for element " + detector);
                    xAxis.setLabel("Energy (eV)");
                    yAxis.setLabel("log10(cross-section/barn)");
                    lc.getData().add(element.makeCSSeries("Scatter"));
                    lc.getData().add(element.makeCSSeries("Capture"));
                    lc.getData().add(element.makeCSSeries("Total"));
                    return lc;

                case "Sigmas":
                    lc = new LineChart<>(xAxis, yAxis);
                    m = Material.getByName(detector);
                    lc.setTitle("Macroscopic cross-sections for material " + detector);
                    xAxis.setLabel("Energy (eV)");
                    yAxis.setLabel("Sigma (cm^-1)");
                    lc.getData().add(m.makeSigmaSeries("Sigma (" + detector + ")"));
                    return lc;

                case "Custom test":
                    bc = new BarChart<>(xAxis, yAxis);
                    bc.setTitle("Custom test");
                    xAxis.setLabel("Energy (eV)");
                    yAxis.setLabel("counts");
                    bc.getData().add(TestGM.customTest(log, detector.equals("X-axis only")));
                    return bc;

                default:
                    return null;
            }
        } else {
            // Enviroment chart
            bc = new BarChart<>(xAxis, yAxis);
            bc.setTitle("Environment:\nP(escape)="
                    + (Math.round(10000 * Environment.getEscapeProbability()) / 10000.0)
                    + ", P(capture)="
                    + (Math.round(10000 * (1 - Environment.getEscapeProbability())) / 10000.0)
                    + ", Total neutrons: " + this.lastCount
            );
            xAxis.setLabel("Energy (eV)");
            yAxis.setLabel("Count");

            bc.getData().add(Environment.getInstance().counts.makeSeries("Escape counts", log));
        }
        return bc;
    }

}
