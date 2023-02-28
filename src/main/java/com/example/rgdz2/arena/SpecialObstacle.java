package com.example.rgdz2.arena;

import com.example.rgdz2.Utilities;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class SpecialObstacle extends Box {

    public SpecialObstacle(int i){
        super(250,100,25);
        PhongMaterial materijal = new PhongMaterial(Color.SILVER);
        this.getTransforms().addAll(
                new Rotate(90.0 * i, Rotate.Y_AXIS),
                new Translate(710.0, -105.0, 0)
        );
    }

}
