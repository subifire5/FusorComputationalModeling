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
public interface KDTreeNode {
     KDTreeNode getChildLeft();
     KDTreeNode getChildRight();
     KDTreeNode getParent();
     Point getPoint();
     
     
     void setChildLeft(KDTreeNode childLeft);
     void setChildRight(KDTreeNode childRight);
     void setParent(KDTreeNode parent);
     void setPoint(Point N);
}
