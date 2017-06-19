/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorvis;
import fusorcompmodeling.Point;
/**
 *
 * @author guberti
 */
public class CalcThread implements Runnable {
    private Point[] points;
    private Point[] tasks;
    public int taskStart;
    public int taskEnd;
    public EFieldData[] results;
    private VisualizationType v;
    private Thread t;
    public boolean finished;
    public boolean reintegrated;
    
    CalcThread(Point[] tasks, int tS, int tE, Point[] comparePoints, VisualizationType v) {
        this.tasks = tasks;
        this.points = comparePoints;
        results = new EFieldData[tE - tS];
        this.v = v;
        finished = false;
        reintegrated = false;
        this.taskEnd = tE;
        this.taskStart = tS;
    }
    
    @Override
    public void run() {
        int index = 0;
        for (int i = taskStart; i < taskEnd; i++) {
            results[index] = v.calcPoint(points, tasks[i]);
            index++;
        }
        finished = true;
    }
    
    // This start method must NOT be a part of the constructor
    // even though its code will be run immediatly afterwards
    public void start() {
        t = new Thread(this);
        t.start();
    }
}
