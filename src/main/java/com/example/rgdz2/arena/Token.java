package com.example.rgdz2.arena;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class Token extends  Cylinder{

    private int points;

    public Token(int i, int p){
        super(50,5);
        this.setMaterial(new PhongMaterial(Color.GOLD));

        this.points = p;

        Rotate rotate = new Rotate(0.0, Rotate.Y_AXIS);
        Translate translate = new Translate(0.0, -5.0, 0.0);
        this.getTransforms().addAll(translate,
                new Rotate(90.0 * i, Rotate.Y_AXIS),
                new Translate(450.0, 0.0, 0.0),
                new Translate(0.0, -55.0, 0.0),
                rotate,
                new Rotate(90.0, Rotate.Z_AXIS));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.0), new KeyValue(rotate.angleProperty(), 0, Interpolator.LINEAR),
                                                    new KeyValue(translate.yProperty(), -5.0)),
                new KeyFrame(Duration.seconds(3.0), new KeyValue(rotate.angleProperty(), 180, Interpolator.LINEAR),
                                                    new KeyValue(translate.yProperty(), -50.0)),
                new KeyFrame(Duration.seconds(6.0), new KeyValue(rotate.angleProperty(), 360, Interpolator.LINEAR),
                                                    new KeyValue(translate.yProperty(), -5.0)));

        timeline.setCycleCount ( Timeline.INDEFINITE );

        timeline.play();

    }

    public int getPoints() {
        return points;
    }

    public boolean handleCollision(Ball ball) {
        Bounds a = this.getBoundsInParent();
        Bounds b = ball.getBoundsInParent();
        return a.intersects(b);
    }
}

