package com.example.rgdz2;

import com.example.rgdz2.arena.*;
import com.example.rgdz2.timer.Timer;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.*;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
	public static final double WINDOW_WIDTH  = 800;
	public static final double WINDOW_HEIGHT = 800;
	
	private static final double PODIUM_WIDTH  = 2000;
	private static final double PODIUM_HEIGHT = 10;
	private static final double PODIUM_DEPTH  = 2000;
	
	private static final double CAMERA_FAR_CLIP = 100000;
	private static final double CAMERA_Z        = -5000;
	private static final double CAMERA_X_ANGLE  = -45;
	
	private static final double BALL_RADIUS = 50;
	
	private static final double DAMP = 0.999;
	private static final double ARENA_DAMP = 0.995;
	
	private static final double MAX_ANGLE_OFFSET = 30;
	private static final double MAX_ACCELERATION = 400;
	
	private static final int    NUMBER_OF_HOLES = 4;
	private static final double HOLE_RADIUS     = 2 * Main.BALL_RADIUS;
	private static final double HOLE_HEIGHT     = PODIUM_HEIGHT;
	private static final int ATTEMPTS = 5;
	private static final int TOKEN_VALUE = 5;
	public static final double SPECIAL_ACCELERATION = 1.5;
	public static final int GAME_DURATION = 60;
	
	private Group root;
	private Group subRoot;
	private Box podium;
	private Ball  ball;
	private Arena arena;
	private Hole holes[];
	private Camera camera1;
	private Camera camera2;
	private Rotate cameraXR, cameraYR;
	private Translate cameraTranslate;
	private double cameraX = 0, cameraY = 0;
	private Scene scene;
	private SubScene subScene1, subScene2;
	private Box ograda[];
	private Circle remaining[];
	private Token zetoni[];
	private Obstacle prepreke[];
	private SpecialObstacle specPrepreke[];
	private int pokusaji;
	private Translate ballPosition;
	private Text text, vreme;
	private int score;
	private Group reflector;
	private PointLight svetlo;
	private boolean ukljuceno;
	private int teren, lopta;
	private DuzinaIgre igra;
	private boolean kraj;

	private void postaviKamere(Translate ballPosition){
		this.camera1 = new PerspectiveCamera ( true );
		cameraXR = new Rotate(-30, Rotate.X_AXIS);
		cameraYR = new Rotate(0.0, Rotate.Y_AXIS);
		cameraTranslate = new Translate ( 0, 0, CAMERA_Z );
		camera1.setFarClip ( Main.CAMERA_FAR_CLIP );
		camera1.getTransforms ( ).addAll (
				cameraXR,
				cameraYR,
				cameraTranslate
		);
		this.root.getChildren ( ).add ( camera1 );
		subScene1.setCamera ( camera1 );

		this.camera2 = new PerspectiveCamera ( true );
		camera2.setFarClip(Main.CAMERA_FAR_CLIP);
		camera2.getTransforms().addAll(
				new Translate(0, -5000, 0),
				ballPosition,
				new Rotate(-90, Rotate.X_AXIS)
		);
	}

	private void dodajLopticu(Translate ballPosition){
		double max = 300;
		Material ballMaterial = new PhongMaterial ( Color.RED );  //dodavanje lopte
		switch (lopta){
			case 1: max = 300; ballMaterial = new PhongMaterial ( Color.GREEN ); break;
			case 2: max = 400; ballMaterial = new PhongMaterial ( Color.YELLOW ); break;
			case 3: max = 500; ballMaterial = new PhongMaterial ( Color.ORANGE ); break;
			case 4: max = 600; ballMaterial = new PhongMaterial ( Color.RED ); break;
		}
		this.ball = new Ball ( Main.BALL_RADIUS, ballMaterial, ballPosition, max );

		this.arena.getChildren ( ).add ( this.ball );

	}

	private void dodajBojuTerenu(){
		Color c = Color.BLUE;
		switch (teren){
			case 1: c = Color.BLUE; break;
			case 2: c = Color.LIGHTGREEN; break;
			case 3: c = Color.WHITE; break;
		}
		podium.setMaterial ( new PhongMaterial (c) );
	}

	private void dodajOgradu(){
		final PhongMaterial fenceMaterial = new PhongMaterial(Color.BROWN);
		Box b1 = new Box(10, 100, 1000);
		b1.setMaterial(fenceMaterial);
		b1.getTransforms().addAll(
				new Translate(Main.PODIUM_WIDTH/2 - 10, -55, 0)
		);
		Box b2 = new Box(10.0, 100.0, 1000.0);
		b2.setMaterial(fenceMaterial);
		b2.getTransforms().addAll(
				new Rotate(90, Rotate.Y_AXIS),
				new Translate(Main.PODIUM_WIDTH/2 - 10, -55, 0)
		);
		Box b3 = new Box(10.0, 100.0, 1000.0);
		b3.setMaterial(fenceMaterial);
		b3.getTransforms().addAll(
				new Rotate(180, Rotate.Y_AXIS),
				new Translate(Main.PODIUM_WIDTH/2 - 10, -55, 0)
		);
		Box b4 = new Box(10.0, 100.0, 1000.0);
		b4.setMaterial(fenceMaterial);
		b4.getTransforms().addAll(
				new Rotate(270, Rotate.Y_AXIS),
				new Translate(Main.PODIUM_WIDTH/2 - 10, -55, 0)
		);

		ograda = new Box[]{b1, b2, b3, b4};
		this.arena.getChildren().addAll(ograda);

	}

	private void dodajPokusaje(){
		this.remaining = new Circle[Main.ATTEMPTS];
		for (int i = 0; i < Main.ATTEMPTS; i++){
			Circle c = new Circle(7,Color.RED);
			c.getTransforms().addAll(
					new Translate(780 - i * 18,10)
			);
			this.subRoot.getChildren().addAll(c);
			remaining[i] = c;
		}
	}

	private void restart(){
		this.pokusaji--;
		this.subRoot.getChildren().remove(remaining[pokusaji]);
		this.ballPosition.setX(-900.0);
		this.ballPosition.setY(-55.0);
		this.ballPosition.setZ(900.0);
		this.ball.setSpeed(new Point3D( 0, 0, 0 ));
		this.arena.setRotateX(0);
		this.arena.setRotateZ(0);

		for (int i = 0; i < 4; i++){
			if (zetoni[i] == null){
				Token t = new Token(i, Main.TOKEN_VALUE);
				zetoni[i] = t;
				this.arena.getChildren ( ).addAll( zetoni[i] );
			}
		}
	}

	private void dodajRupe(){
		this.holes = new Hole[4];

		double x = ( Main.PODIUM_WIDTH / 2 - 2 * Main.HOLE_RADIUS );
		double z = - ( Main.PODIUM_DEPTH / 2 - 2 * Main.HOLE_RADIUS );

		Translate holePosition = new Translate ( x, -30, z );
		Material holeMaterial = new PhongMaterial ( Color.YELLOW );

		Hole hole = new Hole(Main.HOLE_RADIUS, Main.HOLE_HEIGHT, holeMaterial, holePosition, 10);
		this.arena.getChildren ( ).addAll ( hole );
		holes[0] = hole;

		holeMaterial = new PhongMaterial ( Color.BLACK );
		//druga rupa
		x = -x;
		holePosition = new Translate ( x, -30, z );
		hole = new Hole(Main.HOLE_RADIUS, Main.HOLE_HEIGHT, holeMaterial, holePosition, -5);
		this.arena.getChildren ( ).addAll ( hole );
		holes[1] = hole;

		//treca rupa
		x = -x;
		z = -z;
		holePosition = new Translate ( x, -30, z );
		hole = new Hole(Main.HOLE_RADIUS, Main.HOLE_HEIGHT, holeMaterial, holePosition, -5);
		this.arena.getChildren ( ).addAll ( hole );
		holes[2] = hole;

		//cetvrta rupa
		holePosition = new Translate ( 0, -30, 0 );
		hole = new Hole(Main.HOLE_RADIUS, Main.HOLE_HEIGHT, holeMaterial, holePosition, -5);
		this.arena.getChildren ( ).addAll ( hole );
		holes[3] = hole;
	}

	private void dodajZetone(){
		zetoni = new Token[4];
		for (int i = 0; i < 4; i++) {
			Token t = new Token(i, Main.TOKEN_VALUE);
			zetoni[i] = t;
		}
		this.arena.getChildren ( ).addAll( zetoni );
	}

	private void dodajPrepreke(){
		Image slika = new Image(this.getClass().getClassLoader().getResourceAsStream("obstacle.jpg"));
		PhongMaterial materijal = new PhongMaterial();
		materijal.setDiffuseMap(slika);

		prepreke = new Obstacle[4];
		for (int i = 0; i < 4; i++){
			Obstacle o = new Obstacle(materijal, i);
			prepreke[i] = o;
		}
		this.arena.getChildren().addAll(prepreke);
	}

	private void dodajSpecijalnePrepreke(){
		specPrepreke = new SpecialObstacle[4];
		for ( int i = 0; i < 4; i++){
			SpecialObstacle s = new SpecialObstacle(i);
			specPrepreke[i] = s;
		}
		this.arena.getChildren().addAll(specPrepreke);
	}

	private void dodajSvetiljku(){
		this.reflector = new Group();
		PhongMaterial materijal = new PhongMaterial(Color.GRAY);
		Image slika = new Image(this.getClass().getClassLoader().getResourceAsStream("selfIllumination.png"));
		materijal.setSelfIlluminationMap(slika);
		Box box = new Box(150.0, 150.0, 150.0);
		box.setMaterial(materijal);
		this.reflector.getChildren().add(box);
		this.svetlo = new PointLight(Color.WHITE);
		this.reflector.getChildren().add(this.svetlo);
		this.reflector.getTransforms().add(new Translate(0.0, -1000.0, 0.0));
		this.root.getChildren().add(this.reflector);
		this.ukljuceno = true;
	}

	private Scene pocetniMeni(Stage stage, Scene scene, ImagePattern pozadina){
		//kreiranje pocetne scene za izbor terena i topa
		Group meni = new Group();
		Scene scene1 = new Scene(meni, 800, 800, pozadina);
		Button dugme = new Button("Izaberi");
		dugme.getTransforms().addAll(new Translate(330,700));
		dugme.setPrefSize(140,30);

		double tx = 200, ty = 200, td = 50;

		Image image0 = new Image(Main.class.getClassLoader().getResourceAsStream("teren1.png"));
		ImageView iv0 = new ImageView(image0);
		iv0.setFitHeight(ty-10); iv0.setFitWidth(tx-10);
		Image image1 = new Image(Main.class.getClassLoader().getResourceAsStream("teren2.png"));
		ImageView iv1 = new ImageView(image1);
		iv1.setFitHeight(ty-10); iv1.setFitWidth(tx-10);
		Image image2 = new Image(Main.class.getClassLoader().getResourceAsStream("teren3.png"));
		ImageView iv2 = new ImageView(image2);
		iv2.setFitHeight(ty-10); iv2.setFitWidth(tx-10);

		Button teren1 = new Button();
		teren1.setPrefSize(tx,ty);
		teren1.getTransforms().addAll(new Translate(td, 100));
		teren1.setOnAction(e->teren = 1);
		teren1.setGraphic(iv0);
		Button teren2 = new Button();
		teren2.setPrefSize(tx,ty);
		teren2.getTransforms().addAll(new Translate(2*td + tx, 100));
		teren2.setOnAction(e->teren = 2);
		teren2.setGraphic(iv1);
		Button teren3 = new Button();
		teren3.setPrefSize(tx,ty);
		teren3.getTransforms().addAll(new Translate(3*td + 2*tx, 100));
		teren3.setOnAction(e->teren = 3);
		teren3.setGraphic(iv2);

		Label lb1 = new Label("BLUE");
		lb1.getTransforms().addAll(new Translate(120,305));
		lb1.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		lb1.setTextFill(Color.BLUE);
		Label lb2 = new Label("GREEN");
		lb2.getTransforms().addAll(new Translate(370,305));
		lb2.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		lb2.setTextFill(Color.LIGHTGREEN);
		Label lb3 = new Label("WHITE");
		lb3.getTransforms().addAll(new Translate(620,305));
		lb3.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		lb3.setTextFill(Color.WHITE);

		double lx = 150, ly = 150, ld = 40;

		Image i0 = new Image(Main.class.getClassLoader().getResourceAsStream("lopta1.png"));
		ImageView t0 = new ImageView(i0);
		t0.setFitHeight(ly-10); t0.setFitWidth(lx-10);
		Image i1 = new Image(Main.class.getClassLoader().getResourceAsStream("lopta2.png"));
		ImageView t1 = new ImageView(i1);
		t1.setFitHeight(ly-10); t1.setFitWidth(lx-10);
		Image i2 = new Image(Main.class.getClassLoader().getResourceAsStream("lopta3.png"));
		ImageView t2 = new ImageView(i2);
		t2.setFitHeight(ly-10); t2.setFitWidth(lx-10);
		Image i3 = new Image(Main.class.getClassLoader().getResourceAsStream("lopta4.png"));
		ImageView t3 = new ImageView(i3);
		t3.setFitHeight(ly-10); t3.setFitWidth(lx-10);

		Button top1 = new Button();
		top1.setPrefSize(lx,ly);
		top1.getTransforms().addAll(new Translate(ld,400));
		top1.setOnAction(e->lopta = 1);
		top1.setGraphic(t0);
		Button top2 = new Button();
		top2.setPrefSize(lx,ly);
		top2.getTransforms().addAll(new Translate(2*ld + lx,400));
		top2.setOnAction(e->lopta = 2);
		top2.setGraphic(t1);
		Button top3 = new Button();
		top3.setPrefSize(lx,ly);
		top3.getTransforms().addAll(new Translate(3*ld + 2*lx,400));
		top3.setOnAction(e->lopta = 3);
		top3.setGraphic(t2);
		Button top4 = new Button();
		top4.setPrefSize(lx,ly);
		top4.getTransforms().addAll(new Translate(4*ld + 3*lx,400));
		top4.setOnAction(e->lopta = 4);
		top4.setGraphic(t3);

		Label l1 = new Label("300");
		l1.getTransforms().addAll(new Translate(100,560));
		l1.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		l1.setTextFill(Color.GREEN);
		Label l2 = new Label("400");
		l2.getTransforms().addAll(new Translate(290,560));
		l2.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		l2.setTextFill(Color.YELLOW);
		Label l3 = new Label("500");
		l3.getTransforms().addAll(new Translate(480,560));
		l3.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		l3.setTextFill(Color.ORANGE);
		Label l4 = new Label("600");
		l4.getTransforms().addAll(new Translate(670,560));
		l4.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		l4.setTextFill(Color.RED);

		meni.getChildren().addAll(dugme, teren1, teren2, teren3, top1, top2, top3, top4, l1, l2, l3, l4, lb1, lb2, lb3);
		dugme.setOnAction(e->{stage.setScene(scene);dodajLopticu(ballPosition); dodajBojuTerenu(); igra.pokreni();});
		return scene1;
	}

	@Override
	public void start ( Stage stage ) throws IOException {
		this.root = new Group ( );
		this.subRoot = new Group();
		
		subScene1 = new SubScene(this.root, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, true, SceneAntialiasing.BALANCED);
		subScene2 = new SubScene(this.subRoot, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

		this.podium = new Box (Main.PODIUM_WIDTH, Main.PODIUM_HEIGHT, Main.PODIUM_DEPTH);

		ballPosition = new Translate (- ( Main.PODIUM_WIDTH / 2 - 2 * Main.BALL_RADIUS ),
				- ( Main.BALL_RADIUS + Main.PODIUM_HEIGHT / 2 ), Main.PODIUM_DEPTH / 2 - 2 * Main.BALL_RADIUS);


		postaviKamere(ballPosition); //dodavanje kamere, ograde i svih komponenti u arenu

		this.arena = new Arena ( );
		this.arena.getChildren ( ).add ( podium );

		dodajOgradu();
		dodajZetone();
		dodajRupe();
		dodajPrepreke();
		dodajSpecijalnePrepreke();
		dodajPokusaje();
		dodajSvetiljku();
		pokusaji = Main.ATTEMPTS;
		teren = 1; lopta = 1;
		
		this.root.getChildren ( ).add ( this.arena );

		score = 0;
		text = new Text(); //prikazivanje skora
		text.setText("0"); text.setX(30); text.setY(25);
		text.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 30));
		text.setFill(Color.RED);
		Group tx = new Group(text);
		this.subRoot.getChildren().addAll(tx);

		this.igra = new DuzinaIgre();
		kraj = false;

		vreme = new Text();
		vreme.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 25));
		vreme.setX(WINDOW_WIDTH-60); vreme.setY(WINDOW_HEIGHT - 30);
		vreme.setFill(Color.WHITE);
		vreme.setText("A");
		Group vr = new Group(vreme);
		this.subRoot.getChildren().addAll(vr, this.igra);
		
		Timer timer = new Timer (
			deltaSeconds -> {
				int d = GAME_DURATION - ((int) this.igra.getVreme());
				if (d == 0) kraj = true;
				vreme.setText(" "+d);
				this.arena.update(Main.DAMP);
				if ( Main.this.ball != null ) {
					//loptica se pomera, proveravamo je l ispala sa terena
					boolean outOfArena = Main.this.ball.update (
							deltaSeconds, Main.PODIUM_DEPTH / 2, -Main.PODIUM_DEPTH / 2,
							-Main.PODIUM_WIDTH / 2, Main.PODIUM_WIDTH / 2,
							this.arena.getXAngle ( ), this.arena.getZAngle ( ), Main.MAX_ANGLE_OFFSET,
							Main.DAMP, this.ograda, this.specPrepreke );

					boolean isInHole = false;
					for (int i = 0 ; i < 4; i++){
						//za svaku rupu proveravamo je l loptica upala
						if (holes[i].handleCollision(this.ball)){
							isInHole = true;
							this.score += holes[i].getPoints(); //ako je upala loptica dodajemo broj poena koje ona nosi
							text.setText(" " + this.score); //prikazujemo broj poena
							break;
						}
						//za svaki zeton proveravamo je l ga loptica udarila
						if (zetoni[i] != null) {
							if (zetoni[i].handleCollision(this.ball)) {
								this.score += zetoni[i].getPoints(); //ako je udarila, povecava se broj poena
								this.arena.getChildren().remove(zetoni[i]); //uklanjamo ga
								this.zetoni[i] = null;
								text.setText(" " + this.score); //prikazujemo broj poena
								break;
							}
						}
						//za svaki valjak proveravamo je l udario u njega
						prepreke[i].handleCollision(this.ball);
					}

					if ( outOfArena || isInHole ) {
						if (this.pokusaji > 0){
							restart(); //ako ima jos pokusaja nije kraj igre, vrati teren i lopticu
						}else {
							this.arena.getChildren ( ).remove ( this.ball ); //ako je kraj igre skloni lopticu
							Main.this.ball = null;
							this.arena.disable();
							Text krajTX = new Text("NEMATE VISE POKUSAJA!");
							krajTX.setX(230); krajTX.setY(400);
							krajTX.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 30));
							krajTX.setFill(Color.RED);
							this.subRoot.getChildren().addAll(krajTX);
							igra.zaustavi();
						}
					}
					if (kraj){
						this.arena.getChildren ( ).remove ( this.ball ); //ako je kraj igre skloni lopticu
						Main.this.ball = null;
						this.arena.disable();
						Text krajTX = new Text("VREME JE ISTEKLO!");
						krajTX.setX(250); krajTX.setY(400);
						krajTX.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 30));
						krajTX.setFill(Color.RED);
						this.subRoot.getChildren().addAll(krajTX);
					}
				}
			}
		);

		scene = new Scene(new Group(new Node[] { this.subScene1, this.subScene2}), 800.0, 800.0, true, SceneAntialiasing.BALANCED);

		scene.addEventHandler ( KeyEvent.ANY, event -> this.arena.handleKeyEvent ( event, Main.MAX_ANGLE_OFFSET ) );
		scene.addEventHandler ( KeyEvent.ANY, this::handleKeyEvent);
		scene.addEventHandler ( MouseEvent.ANY, this::handleMouseEvent);
		scene.addEventHandler ( ScrollEvent.ANY, this::handleScrollEvent);

		Image pozadina = new Image(Main.class.getClassLoader().getResourceAsStream("background.jpg"));
		scene.setFill(new ImagePattern(pozadina));
		timer.start ( );

		Scene scene1 = this.pocetniMeni(stage, scene, new ImagePattern(pozadina));

		stage.setTitle ( "Rolling Ball" );
		stage.setScene ( scene1 );
		stage.show ( );
	}

	private void handleKeyEvent(KeyEvent event){
		if (event.getEventType().equals(KeyEvent.KEY_PRESSED)){
			if(event.getCode().equals(KeyCode.DIGIT0) || event.getCode().equals(KeyCode.NUMPAD0)){
				if (this.ukljuceno) {
					this.reflector.getChildren().remove(this.svetlo);
					ukljuceno = false;
				}else {
					this.reflector.getChildren().add(this.svetlo);
					ukljuceno = true;
				}
			}
			if(event.getCode().equals(KeyCode.DIGIT1) || event.getCode().equals(KeyCode.NUMPAD1)){
				subScene1.setCamera(camera1);
			}
			if(event.getCode().equals(KeyCode.DIGIT2) || event.getCode().equals(KeyCode.NUMPAD2)){
				subScene1.setCamera(camera2);
			}
		}
	}

	private void handleMouseEvent( MouseEvent event) {
		if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
			cameraX = event.getSceneX();
			cameraY = event.getSceneY();
		}
		else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
			final double dx = event.getSceneX() - cameraX;
			final double dy = event.getSceneY() - cameraY;
			cameraX = event.getSceneX();
			cameraY = event.getSceneY();
			final double signX = (dx > 0.0) ? 1.0 : -1.0;
			final double signY = (dy > 0.0) ? 1.0 : -1.0;
			final double newAngleX = cameraXR.getAngle() - signY * 0.5;
			final double newAngleY = cameraYR.getAngle() - signX * 0.5;
			cameraXR.setAngle(Utilities.clamp(newAngleX, -90.0, 0.0));
			cameraYR.setAngle(newAngleY);
		}
	}

	public void handleScrollEvent(final ScrollEvent event) {
		if (event.getDeltaY() > 0.0) {
			cameraTranslate.setZ(cameraTranslate.getZ() + 30.0);
		}
		else {
			cameraTranslate.setZ(cameraTranslate.getZ() - 30.0);
		}
	}


	public static void main ( String[] args ) {
		launch ( );
	}
}