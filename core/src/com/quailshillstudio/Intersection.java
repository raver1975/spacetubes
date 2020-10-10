package com.quailshillstudio;

/**
 * Test the intersection between two lines (two pairs of two points)
 *
 * If the test passes, the object will supply the coordinates of the
 * intersection and the subject and clipper alphas as us and uc respectively.
 */
class Intersection {
    float x, y;
    float us, uc;

    Intersection(Vertex s1, Vertex s2, Vertex c1, Vertex c2) {
        float den = (c2.y - c1.y) * (s2.x - s1.x) - (c2.x - c1.x) * (s2.y - s1.y);

        if (den == 0.0f) {
            return;
        }

        us = ((c2.x - c1.x) * (s1.y - c1.y) - (c2.y - c1.y) * (s1.x - c1.x)) / den;
        uc = ((s2.x - s1.x) * (s1.y - c1.y) - (s2.y - s1.y) * (s1.x - c1.x)) / den;

        if (test()) {
            x = s1.x + us * (s2.x - s1.x);
            y = s1.y + us * (s2.y - s1.y);
        }
    }

    final boolean test() {
        return (0 < us && us < 1) && (0 < uc && uc < 1);
    }
}