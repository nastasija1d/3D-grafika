package com.example.rgdz2.arena;

import com.example.rgdz2.Utilities;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;

public class Arena extends Group {
	
	private Rotate rotateX;
	private Rotate rotateZ;
	
	public Arena ( Node... children ) {
		super ( children );
		
		this.rotateX = new Rotate ( 0, Rotate.X_AXIS );
		this.rotateZ = new Rotate ( 0, Rotate.Z_AXIS );
		
		super.getTransforms ( ).addAll (
				this.rotateX,
				this.rotateZ
		);
	}
	
	public void handleKeyEvent ( KeyEvent event, double maxOffset ) {
		double dxAngle = 0;
		double dzAngle = 0;
		
		if ( event.getCode ( ).equals ( KeyCode.UP ) ) {
			dxAngle = -1;
		} else if ( event.getCode ( ).equals ( KeyCode.DOWN ) ) {
			dxAngle = 1;
		} else if ( event.getCode ( ).equals ( KeyCode.LEFT ) ) {
			dzAngle = -1;
		} else if ( event.getCode ( ).equals ( KeyCode.RIGHT ) ) {
			dzAngle = 1;
		}
		
		double newXAngle = Utilities.clamp ( this.rotateX.getAngle ( ) + dxAngle, -maxOffset, maxOffset );
		double newZAngle = Utilities.clamp ( this.rotateZ.getAngle ( ) + dzAngle, -maxOffset, maxOffset );
		
		this.rotateX.setAngle ( newXAngle );
		this.rotateZ.setAngle ( newZAngle );
	}

	public double getXAngle ( ) {

		return this.rotateX.getAngle ( );
	}

	public double getZAngle ( ) {

		return this.rotateZ.getAngle ( );
	}

	public void setRotateX(double x) {
		this.rotateX.setAngle(x);
	}

	public void setRotateZ(double z) {
		this.rotateZ.setAngle(z);
	}

	public void update (double damp){
		this.rotateX.setAngle(this.rotateX.getAngle() * damp);
		this.rotateZ.setAngle(this.rotateZ.getAngle() * damp);
	}

	public void disable(){
		this.setRotateX(0);
		this.setRotateZ(0);
		this.rotateX = new Rotate ( 0, Rotate.X_AXIS );
		this.rotateZ = new Rotate ( 0, Rotate.Z_AXIS );
	}
}
