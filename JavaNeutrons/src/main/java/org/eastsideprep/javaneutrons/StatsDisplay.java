/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.javaneutrons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.Group;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.eastsideprep.javaneutrons.assemblies.Element;
import org.eastsideprep.javaneutrons.assemblies.Material;
import org.eastsideprep.javaneutrons.assemblies.Part;
import org.eastsideprep.javaneutrons.core.MonteCarloSimulation;

/**
 *
 * @author gunnar
 */
public class StatsDisplay extends Group {

    HBox hb = new HBox();
    VBox controls = new VBox();
    VBox chartType = new VBox();
    ComboBox object = new ComboBox();
    Pane chartPane = new Pane();
    MonteCarloSimulation sim;
    BorderPane root;
    ToggleGroup tg;

    public StatsDisplay(MonteCarloSimulation sim, BorderPane root) {
        this.sim = sim;
        this.root = root;

        controls.getChildren().addAll(chartType, object);
        hb.getChildren().addAll(controls, chartPane);
        this.getChildren().add(hb);

        this.populateRadioButtons();

        root.setCenter(this.sim.makeChart(null, null));
        this.object.setVisible(false);
    }

    private void populateRadioButtons() {
        RadioButton rb1 = new RadioButton("Escape counts");
        RadioButton rb2 = new RadioButton("Fluence");
        RadioButton rb3 = new RadioButton("Entry counts");
        RadioButton rb4 = new RadioButton("Event counts");
        RadioButton rb5 = new RadioButton("Path lengths");
        RadioButton rb6 = new RadioButton("Sigmas");
        RadioButton rb7 = new RadioButton("Cross-sections");

        rb1.setSelected(true);

        RadioButton[] rbs = new RadioButton[]{rb1, rb2, rb3, rb4, rb5, rb6, rb7};

        this.tg = new ToggleGroup();
        for (RadioButton rb : rbs) {
            rb.setToggleGroup(tg);
            rb.setUserData(rb.getText());
        }

        tg.selectedToggleProperty().addListener((ov, ot, nt) -> setComboBox());

        chartType.getChildren()
                .addAll(rbs);
    }

    private void populateComboBoxWithParts() {
        this.object.getItems().clear();
        populateComboBox(Part.namedParts.keySet());
    }

    private void populateComboBoxWithMaterials() {
        this.object.getItems().clear();
        populateComboBox(Material.materials.keySet());
    }

    private void populateComboBoxWithElements() {
        this.object.getItems().clear();
        ArrayList<Element> elements = new ArrayList<>(Element.elements.values());
        elements.sort((a, b) -> (a.atomicNumber - b.atomicNumber));
        List<String> s = elements.stream().map(e -> e.name).collect(Collectors.toList());
        populateComboBox(s);
    }

    private void populateComboBoxWithPartsAndMaterials() {
        this.object.getItems().clear();
        populateComboBoxWithParts();
        ArrayList<String> ms = new ArrayList<>(Material.materials.keySet());
        List<String> as = ms.stream().map(s -> "Interstitial "+s).collect(Collectors.toList());
        populateComboBox(as);
    }

    private void populateComboBox(Collection<String> s) {
        if (!(s instanceof ArrayList)) {
            ArrayList<String> items = new ArrayList<>(s);
            items.sort(null);
            s = items;
        }
        this.object.getItems().addAll(s);
        this.object.setValue(this.object.getItems().get(0));
        this.object.valueProperty().addListener((ov, t, t1) -> setChart());
    }

    void setComboBox() {
        Toggle t = tg.getSelectedToggle();
        if (t != null) {
            switch ((String) t.getUserData()) {
                case "Escape counts":
                    this.object.setVisible(false);
                    break;

                case "Fluence":
                    this.populateComboBoxWithParts();
                    this.object.setVisible(true);
                    break;

                case "Entry counts":
                    this.populateComboBoxWithParts();
                    this.object.setVisible(true);
                    break;

                case "Event counts":
                    this.populateComboBoxWithPartsAndMaterials();
                    this.object.setVisible(true);
                    break;

                case "Path lengths":
                    this.populateComboBoxWithMaterials();
                    this.object.setVisible(true);
                    break;

                case "Sigmas":
                    this.populateComboBoxWithMaterials();
                    this.object.setVisible(true);
                    break;

                case "Cross-sections":
                    this.populateComboBoxWithElements();
                    this.object.setVisible(true);
                    break;

                default:
                    this.object.setVisible(false);
                    break;
            }
        }
        setChart();
    }

    void setChart() {
        Toggle t = tg.getSelectedToggle();
        if (t != null) {
            switch ((String) t.getUserData()) {
                case "Escape counts":
                    root.setCenter(this.sim.makeChart(null, null));
                    break;

                case "Fluence":
                    root.setCenter(this.sim.makeChart((String) this.object.getValue(), "Fluence"));
                    break;

                case "Entry counts":
                    root.setCenter(this.sim.makeChart((String) this.object.getValue(), "Entry counts"));
                    break;

                case "Event counts":
                    root.setCenter(this.sim.makeChart((String) this.object.getValue(), "Event counts"));
                    break;

                case "Path lengths":
                    root.setCenter(this.sim.makeChart((String) this.object.getValue(), "Path lengths"));
                    break;

                case "Sigmas":
                    root.setCenter(this.sim.makeChart((String) this.object.getValue(), "Sigmas"));
                    break;

                case "Cross-sections":
                    root.setCenter(this.sim.makeChart((String) this.object.getValue(), "Cross-sections"));
                    break;

                default:
                    root.setCenter(null);
                    break;
            }
        }
    }

}
