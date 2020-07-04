package org.eastsideprep.javaneutrons.core;

import java.util.HashMap;
import java.util.LinkedList;

public class Grid {

    public class Triangle {

        Part part;
        int face;

        Triangle(Part p, int f) {
            this.part = p;
            this.face = f;
        }
    }

    private class CellID {

        double minX;
        double minY;
        double minZ;

        @Override
        public int hashCode() {
            return Double.hashCode(minX) ^ Double.hashCode(minY) ^ Double.hashCode(minZ);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CellID other = (CellID) obj;
            return minX == other.minX && minY == other.minY && minZ == other.minZ;
        }

    }

    private class Cell {

        CellID id;

        LinkedList<Triangle> triangles = new LinkedList<>();

        void addTriangle(Triangle t) {
            this.triangles.add(t);
        }
    }

    HashMap<CellID, Cell> cells = new HashMap<>();

    void addTriangle(Triangle t) {
        // find appropriate cell
        
        // add triangle

    }

    Triangle rayIntersect(
            double ox, double oy, double oz,
            double dx, double dy, double dz,
            double v0x, double v0y, double v0z,
            double v1x, double v1y, double v1z,
            double v2x, double v2y, double v2z) {
        return null;
    }
}
