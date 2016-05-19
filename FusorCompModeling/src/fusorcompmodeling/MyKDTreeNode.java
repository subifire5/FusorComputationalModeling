/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

/**
 *
 * @author sfreisem-kirov
 */
public class MyKDTreeNode implements KDTreeNode{
    public KDTreeNode ChildLeft;
    public KDTreeNode ChildRight;
    public KDTreeNode Parent;
    public Point NPoint;
    @Override
    public KDTreeNode getChildLeft() {
        return ChildLeft;
    }

    @Override
    public KDTreeNode getChildRight() {
        return ChildRight;
    }
    
    @Override
    public KDTreeNode getParent() {
        return Parent;
    }    
    
    @Override
    public Point getPoint() {
        return NPoint;
    }

    @Override
    public void setChildLeft(KDTreeNode childLeft) {
        ChildLeft = childLeft;
    }

    @Override
    public void setChildRight(KDTreeNode childRight) {
        ChildRight = childRight;
    }
    
        @Override
    public void setParent(KDTreeNode parent) {
        Parent = parent;
    }
    

    @Override
    public void setPoint(Point N) {
        NPoint = N;
    }
    
    public boolean isLeaf(){
        if(ChildRight == null &&  ChildLeft == null){
            return true;
        }
        else{
            return false;
        }
    }




}
