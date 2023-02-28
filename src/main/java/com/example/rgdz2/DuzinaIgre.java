package com.example.rgdz2;

import com.example.rgdz2.Main;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class DuzinaIgre extends Rectangle{

    //private double x;
    //private double y;
    private double max;
    private Timeline animacija;

    public DuzinaIgre(){
        super(0, 0,1,10);
        super.setFill(Color.WHITE);
        Scale linijaScale = new Scale(1,1);
        super.getTransforms ( ).addAll (
                new Translate(0, 790),
                linijaScale);

        animacija = new Timeline (
                new KeyFrame( Duration.seconds ( 0 ), new KeyValue( linijaScale.xProperty ( ), Main.WINDOW_WIDTH, Interpolator.LINEAR ) ),
                new KeyFrame( Duration.seconds (Main.GAME_DURATION ), new KeyValue ( linijaScale.xProperty ( ), 0.01, Interpolator.LINEAR ) )
        );


    }
    public void pokreni(){

        animacija.play();
    }
    public void zaustavi(){

        animacija.pause();
    }
    public void restart(){

        animacija.jumpTo(Duration.seconds(0));
    }
    public void vrati(int t){
        Duration time = animacija.getCurrentTime();
        animacija.jumpTo(time.subtract(Duration.seconds(t)));
    }
    public double getVreme(){
        return animacija.getCurrentTime().toSeconds();
    }

}
