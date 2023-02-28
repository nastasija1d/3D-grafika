package com.example.rgdz2.arena;

import com.example.rgdz2.Main;
import com.example.rgdz2.Utilities;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Material;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

public class Ball extends Sphere {
	private Translate position;
	private Point3D speed;
	private double maxAcceleration;
	
	public Ball ( double radius, Material material, Translate position, double m ) {
		super ( radius );
		super.setMaterial ( material );
		
		this.position = position;
		
		super.getTransforms ( ).add ( this.position );
		
		this.speed = new Point3D ( 0, 0, 0 );

		this.maxAcceleration = m;
	}
	
	public boolean update (double deltaSeconds, double top, double bottom, double left, double right,
			double xAngle, double zAngle, double maxAngleOffset, double damp, Box fence[], SpecialObstacle special[] //ograda, niz kvadara
	) {
		double newPositionX = this.position.getX ( ) + this.speed.getX ( ) * deltaSeconds;
		double newPositionZ = this.position.getZ ( ) + this.speed.getZ ( ) * deltaSeconds;
		
		this.position.setX ( newPositionX );
		this.position.setZ ( newPositionZ );
		
		double accelerationX = this.maxAcceleration * zAngle / maxAngleOffset;
		double accelerationZ = -this.maxAcceleration * xAngle / maxAngleOffset;
		
		double newSpeedX = ( this.speed.getX ( ) + accelerationX * deltaSeconds ) * damp;
		double newSpeedZ = ( this.speed.getZ ( ) + accelerationZ * deltaSeconds ) * damp;
		
		this.speed = new Point3D ( newSpeedX, 0, newSpeedZ );


		double ballRadius = this.getRadius();
		boolean collision = false;

		for (int i = 0; i < 4; i++){
			Bounds fenceBounds = fence[i].getBoundsInParent();
			double MinX = fenceBounds.getMinX();
			double MaxX = fenceBounds.getMaxX();
			double MinZ = fenceBounds.getMinZ();
			double MaxZ = fenceBounds.getMaxZ();
			double closestX = Utilities.clamp(newPositionX, MinX, MaxX);
			double closestZ = Utilities.clamp(newPositionZ, MinZ, MaxZ);
			double dx = closestX - newPositionX;
			double dz = closestZ - newPositionZ;
			double distanceSquared = dx * dx + dz * dz;
			double radiusSquared = ballRadius * ballRadius;
			collision = distanceSquared < radiusSquared;
			if (collision) {
				if (closestX == MaxX || closestX == MinX) {
					this.speed = new Point3D(-this.speed.getX(), 0.0, this.speed.getZ());
				}
				else if (closestZ == MaxZ || closestZ == MinZ) {
					this.speed = new Point3D(this.speed.getX(), 0.0, -this.speed.getZ());
				}
				break;
			}
		}

		for (int i = 0; i < 4; i++) {
			Bounds fenceBounds = special[i].getBoundsInParent();
			double MinX = fenceBounds.getMinX();
			double MaxX = fenceBounds.getMaxX();
			double MinZ = fenceBounds.getMinZ();
			double MaxZ = fenceBounds.getMaxZ();
			double closestX = Utilities.clamp(newPositionX, MinX, MaxX);
			double closestZ = Utilities.clamp(newPositionZ, MinZ, MaxZ);
			double dx = closestX - newPositionX;
			double dz = closestZ - newPositionZ;
			double distanceSquared = dx * dx + dz * dz;
			double radiusSquared = ballRadius * ballRadius;
			boolean collision1 = distanceSquared < radiusSquared;
			if (collision1) {
				if (closestX == MaxX || closestX == MinX) {
					this.speed = new Point3D(-this.speed.getX() * Main.SPECIAL_ACCELERATION, 0.0, this.speed.getZ() * Main.SPECIAL_ACCELERATION);
				} else if (closestZ == MaxZ || closestZ == MinZ) {
					this.speed = new Point3D(this.speed.getX() * Main.SPECIAL_ACCELERATION, 0.0, -this.speed.getZ() * Main.SPECIAL_ACCELERATION);
				}
				break;
			}
		}

		boolean xOutOfBounds = (( newPositionX > right ) || ( newPositionX < left ))&& !collision;
		boolean zOutOfBounds = (( newPositionZ > top ) || ( newPositionZ < bottom ))&& !collision;

		
		return xOutOfBounds || zOutOfBounds;
	}

	public Point3D getSpeed() {
		return speed;
	}

	public void setSpeed(Point3D speed) {
		this.speed = speed;
	}

}
