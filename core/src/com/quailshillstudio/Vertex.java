package com.quailshillstudio;

/**
 * This class is almost exactly as described in the paper by Gnther/Greiner.
 * Basically it is a node in a circular doubly linked list.
 */
public class Vertex {
    /** Coordinates of the vertex */
    float x, y;

    /** References to the next and previous vertices of the polygon */
    Vertex next, prev;

    /** Reference to the corresponding intersection vertex in the other polygon */
    Vertex neighbour;

    /** Intersection points relative distance from previous vertex */
    float alpha = 0.0f;

    /** True if intersection is an entry point to another polygon;
     * False if it is an exit point */
    boolean entry = true;

    /** True if vertex is an intersection */
    boolean intersect = false;

    /** True if the vertex has been checked (last phase) */
    boolean checked = false;

    /** Constructor */
    public Vertex(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /** Constructor using a two-value array, to represent the coordinates */
    public Vertex(float[] coord) {
        this.x = coord[0];
        this.y = coord[1];
    }

    /** Create an intersection vertex */
    static Vertex intersection(float x, float y, float alpha) {
        Vertex vertex = new Vertex(x, y);
        vertex.alpha = alpha;
        vertex.intersect = true;
        vertex.entry = false;
        return vertex;
    }

    /** Set the vertex as checked */
    public void setChecked() {
        this.checked = true;
        if (neighbour != null && !neighbour.checked) {
            neighbour.setChecked();
        }
    }

    /**
     * Test if a vertex lies inside a polygon (odd-even rule)
     *
     * This function calculates the "winding" number for a point, which
     * represents the number of times a ray emitted from the point to
     * infinity intersects any edge of the polygon.
     *
     * An even winding number means the point lies OUTSIDE the polygon;
     * an odd number means it lies INSIDE it.
     */
    boolean isInside(Polygon poly) {
        int winding_number = 0;
        Vertex infinity = new Vertex(10000, this.y);
        Vertex q = poly.first;
        Intersection i;
        do {
            i = new Intersection(this, infinity, q, poly.getNext(q.next));
            if (!q.intersect && i.test()) {
                winding_number++;
            }
            q = q.next;
        } while (!q.equals(poly.first));
        return (winding_number % 2) != 0;
    }

    /** String representation of the vertex for debugging purposes */
    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + x +
                ", y=" + y +
                ", next=" + next +
                ", prev=" + prev +
                ", neighbour=" + neighbour +
                ", alpha=" + alpha +
                ", entry=" + entry +
                ", intersect=" + intersect +
                ", checked=" + checked +
                '}';
    }
}
