package ml.codeboy.engine;

import java.awt.*;

public class Vector {
    public double x, y;

    public Vector() {
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Vector other) {
        double distX = other.x - x, distY = other.y - y;
        return Math.sqrt(distX * distX + distY * distY);
    }

    public double distance(Point other) {
        double distX = other.x - x, distY = other.y - y;
        return Math.sqrt(distX * distX + distY * distY);
    }

    public int getX() {
        return (int) Math.round(x);
    }

    public int getY() {
        return (int) Math.round(y);
    }
}
