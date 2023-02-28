package com.example.rgdz2.arena;

import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Obstacle extends Cylinder {

    public Obstacle(PhongMaterial material, int i){
        super(60, 240);
        this.setMaterial(material);
        this.getTransforms().addAll(
                new Rotate(90.0 * i, Rotate.Y_AXIS),
                new Translate(600.0, -130.0, 600.0)
        );
    }

    public void handleCollision(Ball ball) {
        Bounds lopta = ball.getBoundsInParent();
        Bounds prepreka = this.getBoundsInParent();

        double dx = lopta.getCenterX() - prepreka.getCenterX();
        double dz = lopta.getCenterZ() - prepreka.getCenterZ();
        double dr = ball.getRadius() + this.getRadius();
        double distanceSquared = dx * dx + dz * dz;
        double radiusSquared = dr * dr;
        boolean collided = distanceSquared < radiusSquared;
        if (collided) {
            Point3D normal = new Point3D(lopta.getCenterX() - prepreka.getCenterX(), 0.0,
                                        lopta.getCenterZ() - prepreka.getCenterZ()).normalize();
            double speedDotNormal = ball.getSpeed().dotProduct(normal);
            ball.setSpeed(ball.getSpeed().subtract(normal.multiply(2.0 * speedDotNormal)));
        }
    }

}

