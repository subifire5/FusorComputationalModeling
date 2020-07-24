package org.eastsideprep.javaneutrons.core;

import org.eastsideprep.javaneutrons.shapes.Cuboid;

abstract public class MC0D extends MonteCarloSimulation {

    abstract public void run(Part pseudoPart, Object o, Neutron n);

    abstract public void after(Part pseudoPart, Object o);

    private Part pseudoPart;
    private Object o;

    public MC0D(Object material, Object o) {
        super();
        this.o = o;
        pseudoPart = new Part("MC0D", new Cuboid(1), material);
        this.assembly.add(pseudoPart);
        this.namedParts.put("MC0D", pseudoPart);
        if (pseudoPart.material.name != null) {
            this.materials.put(this.pseudoPart.material.name, this.pseudoPart.material);
        }
    }

    @Override
    public void postProcess() {
        this.after(pseudoPart, o);
    }

    @Override
    public void simulateNeutron(Neutron n) {
        this.run(pseudoPart, o, n);
        completed.incrementAndGet();
    }

}
