package org.eastsideprep.javaneutrons.core;

import org.eastsideprep.javaneutrons.shapes.Cuboid;

public class MC0D extends MonteCarloSimulation {

    public interface MC0DRunLambda {

        void run(Part pseudoPart, Object o);
    }

    public interface MC0DAfterLambda {

        void run(Part pseudoPart, Object o);
    }
    private Part pseudoPart;
    private MC0DRunLambda runLambda;
    private MC0DAfterLambda afterLambda;
    private Object o;

    public MC0D(MC0DRunLambda lambda, MC0DAfterLambda after, Object material, Object o) {
        super();
        this.runLambda = lambda;
        this.afterLambda = after;
        this.o = o;
        pseudoPart = new Part("MC0D", new Cuboid(0.000001), material);
        this.assembly.add(pseudoPart);
    }

    @Override
    public void postProcess() {
        this.afterLambda.run(pseudoPart, o);
    }

    @Override

    public void simulateNeutron(Neutron n) {
        this.runLambda.run(pseudoPart, o);
        completed.incrementAndGet();
    }

}
