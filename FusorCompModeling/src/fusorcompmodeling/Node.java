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
public class Node implements Comparable<Node> {

    Node leftChild;
    Node rightChild;
    Point location;
    Node parent;
    int depth;

    Node() {

    }

    Node(Node leftChild, Node rightChild, Point location, Node parent, int depth) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.location = location;
        this.parent = parent;
        this.depth = depth;
    }

    Node(Point location) {
        this.location = location;
    }

    public static int compareX(Node a, Node b) {
        if (a.location.x > b.location.x) {
            return 1;
        } else if (a.location.x < b.location.x) {
            return -1;
        } else {
            return 0;
        }
    }

    public static int compareY(Node a, Node b) {
        if (a.location.y > b.location.y) {
            return 1;
        } else if (a.location.y < b.location.y) {
            return -1;
        } else {
            return 0;
        }
    }

    public static int compareZ(Node a, Node b) {
        if (a.location.z > b.location.z) {
            return 1;
        } else if (a.location.z < b.location.z) {
            return -1;
        } else {
            return 0;
        }
    }

    public double manhattanDistance(Node a) {
        return Math.abs(a.location.x - this.location.x) + Math.abs(a.location.y - this.location.y) + Math.abs(a.location.z - this.location.z);
    }

    public static Node insert(Node newNode, Node parent, int depth) {
        int axis = depth % 3;
        if (parent == null) {
            newNode.parent = parent;
            newNode.depth = depth;
            return newNode;
        }
        int result;
        if (axis == 0) {
            result = compareX(newNode, parent);
            if (result > 0) {
                parent.rightChild = insert(newNode, parent.rightChild, depth + 1);
            } else if (result < 0) {
                parent.leftChild = insert(newNode, parent.leftChild, depth + 1);
            }
        } else if (axis == 1) {
            result = compareY(newNode, parent);
            if (result > 0) {
                parent.rightChild = insert(newNode, parent.rightChild, depth + 1);
            } else if (result < 0) {
                parent.leftChild = insert(newNode, parent.leftChild, depth + 1);
            }
        } else if (axis == 2) {
            result = compareZ(newNode, parent);
            if (result > 0) {
                parent.rightChild = insert(newNode, parent.rightChild, depth + 1);
            } else if (result < 0) {
                parent.leftChild = insert(newNode, parent.leftChild, depth + 1);
            }
        }
        return parent;
    }

    public static Node kdtree(ArrayList<Point> points, int depth) {
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
        Node node = new Node();
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

    public boolean equals(Node a) {
        if (this.location.x == a.location.x && this.location.y == a.location.y && this.location.z == a.location.z) {
            return true;
        } else {
            return false;
        }
    }

    public static void queryNode(Node currentNode, RectHV queryEnv, int depth, ArrayList<Node> result) {
        if (currentNode == null) {
            return;
        }
        double min, max, discriminant;
        int axis = depth % 3;
        switch (axis) {
            case 0:
                min = queryEnv.xmin;
                max = queryEnv.xmax;
                discriminant = currentNode.location.x;
                break;
            case 1:
                min = queryEnv.ymin;
                max = queryEnv.ymax;
                discriminant = currentNode.location.y;
                break;
            case 2:
                min = queryEnv.zmin;
                max = queryEnv.zmax;
                discriminant = currentNode.location.z;
                break;
            default:
                min = queryEnv.xmin;
                max = queryEnv.xmax;
                discriminant = currentNode.location.x;
        }
        boolean searchLeft = min < discriminant;
        boolean searchRight = discriminant <= max;
        if (searchLeft) {
            queryNode(currentNode.leftChild, queryEnv, depth + 1, result);
        }
        if (queryEnv.contains(currentNode.location)) {
            result.add((Node) currentNode);
        }
        if (searchRight) {
            queryNode(currentNode.rightChild, queryEnv, depth + 1, result);
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
    public static void search(final Node node, final Deque<Node> results) {
        if (node != null) {
            results.add((Node) node);
            search(node.leftChild, results);
            search(node.rightChild, results);
        }
    }

    public Iterator<Point> iterator(Node root) {
        final Deque<Point> results = new ArrayDeque<Point>();
        //search(root, results);
        return results.iterator();
    }

    @Override
    public int compareTo(Node o) {
        return compareTo(depth, 3, this.location, o.location);
    }
}
