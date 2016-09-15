/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Deque;
import java.util.ArrayDeque;

/**
 *
 * @author Daman
 */
public class KDNode implements Comparable<KDNode> {

    KDNode leftChild;
    KDNode rightChild;
    Point location;
    KDNode parent;
    int depth;

    KDNode() {

    }

    KDNode(KDNode leftChild, KDNode rightChild, Point location, KDNode parent, int depth) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.location = location;
        this.parent = parent;
        this.depth = depth;
    }

    KDNode(Point location) {
        this.location = location;
    }

    public static int compareX(KDNode a, KDNode b) {
        if (a.location.x > b.location.x) {
            return 1;
        } else if (a.location.x < b.location.x) {
            return -1;
        } else {
            return 0;
        }
    }

    public static int compareY(KDNode a, KDNode b) {
        if (a.location.y > b.location.y) {
            return 1;
        } else if (a.location.y < b.location.y) {
            return -1;
        } else {
            return 0;
        }
    }

    public static int compareZ(KDNode a, KDNode b) {
        if (a.location.z > b.location.z) {
            return 1;
        } else if (a.location.z < b.location.z) {
            return -1;
        } else {
            return 0;
        }
    }

    public double manhattanDistance(KDNode a) {
        return Math.abs(a.location.x - this.location.x) + Math.abs(a.location.y - this.location.y) + Math.abs(a.location.z - this.location.z);
    }

    public static KDNode insert(KDNode newKDNode, KDNode parent, int depth) {
        int axis = depth % 3;
        if (parent == null) {
            newKDNode.parent = parent;
            newKDNode.depth = depth;
            return newKDNode;
        }
        int result;
        if (axis == 0) {
            result = compareX(newKDNode, parent);
            if (result > 0) {
                parent.rightChild = insert(newKDNode, parent.rightChild, depth + 1);
            } else if (result < 0) {
                parent.leftChild = insert(newKDNode, parent.leftChild, depth + 1);
            }
        } else if (axis == 1) {
            result = compareY(newKDNode, parent);
            if (result > 0) {
                parent.rightChild = insert(newKDNode, parent.rightChild, depth + 1);
            } else if (result < 0) {
                parent.leftChild = insert(newKDNode, parent.leftChild, depth + 1);
            }
        } else if (axis == 2) {
            result = compareZ(newKDNode, parent);
            if (result > 0) {
                parent.rightChild = insert(newKDNode, parent.rightChild, depth + 1);
            } else if (result < 0) {
                parent.leftChild = insert(newKDNode, parent.leftChild, depth + 1);
            }
        }
        return parent;
    }

    public static KDNode kdtree(ArrayList<Point> points, int depth) {
        if (points == null || points.size() == 0) {
            return null;
        }
        int axis = depth % 3;
        switch (axis) {
            case 0:
                Collections.sort(points, Point.X_COMPARATOR);
                break;
            case 1:
                Collections.sort(points, Point.Y_COMPARATOR);
                break;
            case 2:
                Collections.sort(points, Point.Z_COMPARATOR);
                break;
            default:
                break;
        }
        KDNode node = new KDNode();
        if (points.size() > 0) {
            int medianIndex = points.size() / 2;
            Point currentPoint = points.get(medianIndex);
            node.location = currentPoint;
            node.depth = depth;
            ArrayList<Point> less = new ArrayList<Point>(points.size() - 1);
            ArrayList<Point> more = new ArrayList<Point>(points.size() - 1);
            //Process list to see where each non-median point lies
            for (int i = 0; i < points.size(); i++) {
                if (i == medianIndex) {
                    continue;
                }
                Point p = points.get(i);
                if (currentPoint.compareTo(p, axis) <= 0) {
                    less.add(p);
                } else {
                    more.add(p);
                }
            }
            if (less.size() > 0) {
                node.leftChild = kdtree(less, depth + 1);
                node.leftChild.parent = node;
            }
            if (more.size() > 0) {
                node.rightChild = kdtree(more, depth + 1);
                node.rightChild.parent = node;
            }
        }
        return node;
    }

    public boolean equals(KDNode a) {
        if (this.location.x == a.location.x && this.location.y == a.location.y && this.location.z == a.location.z) {
            return true;
        } else {
            return false;
        }
    }

    public static void queryKDNode(KDNode currentKDNode, RectHV queryEnv, int depth, ArrayList<KDNode> result) {
        if (currentKDNode == null) {
            return;
        }
        double min, max, discriminant;
        int axis = depth % 3;
        switch (axis) {
            case 0:
                min = queryEnv.xmin;
                max = queryEnv.xmax;
                discriminant = currentKDNode.location.x;
                break;
            case 1:
                min = queryEnv.ymin;
                max = queryEnv.ymax;
                discriminant = currentKDNode.location.y;
                break;
            case 2:
                min = queryEnv.zmin;
                max = queryEnv.zmax;
                discriminant = currentKDNode.location.z;
                break;
            default:
                min = queryEnv.xmin;
                max = queryEnv.xmax;
                discriminant = currentKDNode.location.x;
        }
        boolean searchLeft = min < discriminant;
        boolean searchRight = discriminant <= max;
        if (searchLeft) {
            queryKDNode(currentKDNode.leftChild, queryEnv, depth + 1, result);
        }
        if (queryEnv.contains(currentKDNode.location)) {
            result.add((KDNode) currentKDNode);
        }
        if (searchRight) {
            queryKDNode(currentKDNode.rightChild, queryEnv, depth + 1, result);
        }
    }

    public static int compareTo(int depth, int dim, Point aLocation, Point bLocation) {
        int axis = depth % dim;
        if (axis == 0) {
            return Point.X_COMPARATOR.compare(aLocation, bLocation);
        }
        if (axis == 1) {
            return Point.Y_COMPARATOR.compare(aLocation, bLocation);
        }
        return Point.Z_COMPARATOR.compare(aLocation, bLocation);
    }

    @SuppressWarnings("unchecked")
    public static void search(final KDNode node, final Deque<KDNode> results) {
        if (node != null) {
            results.add((KDNode) node);
            search(node.leftChild, results);
            search(node.rightChild, results);
        }
    }

    public Iterator<Point> iterator(KDNode root) {
        final Deque<Point> results = new ArrayDeque<Point>();
        //search(root, results);
        return results.iterator();
    }

    @Override
    public int compareTo(KDNode o) {
        return compareTo(depth, 3, this.location, o.location);
    }
}
