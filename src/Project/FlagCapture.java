package Project;

import java.util.concurrent.TimeUnit;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

/**
 * Flag Capture CLass
 * 
 * This class is called to search and capture the Flag in the Opponent's Zone
 * 
 * @author DPM TEAM18
 * @version 1.0, 7 Nov 2015
 * 
 */


public class FlagCapture {
	
	private float flagColor;
	public boolean isCaptured;
	private int maxDist=15;
	private boolean mainPath= true;
	private UltrasonicPoller usPoller_left;
	private UltrasonicPoller usPoller_right;
	
	public static final EV3LargeRegulatedMotor armMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	public static final EV3MediumRegulatedMotor grabberMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B")); 
	//private int now_armTacho, last_armTacho; 
	private int ARM_SPEED = 80, GRAB_SPEED = 120;
	
	//speed to approach flags
	private int FLAG_SPEED=50;
	//current heading of the robot
	private double[] current_position;
	//entry tile in search routine
	private double [] search_start;
	//search routine start corner
	private int search_corner=0;
	// outer index of 2D Waypoint array
	private int waypoint=0;
	//tolerated distance error from  waypoint
	private double dist_error= 1.5;
	//Waypoint array
	private double [][] Waypoint= new double[5][2];
	//has the waypoint been reached
	private boolean atWaypoint=false;
	
	public enum Cardinal_Dir {
		EAST, NORTH, WEST, SOUTH
	};
	
	
	
	/**
	 * Creates an Object of type FlagCapture
	 * @param flagColor float representing color of opponents flag
	 */
	
	public FlagCapture(float flagColor, double[] entry_points){
		this.flagColor=flagColor;
		this.isCaptured=false;
		this.search_start=entry_points;
		armMotor.setAcceleration(3000);

//		this.usPoller_left=Robot.usPoller_left;
		this.usPoller_right = Robot.usPoller_right;
		this.usPoller_left = Robot.usPoller_left; 
	}
	
	private void setCorner(){
		
		//An array of each waypoint and its x&y position
		double top= (Robot.MyHome_upperRight[1])*Robot.tile + Robot.tile/2;
		
		if(search_start[0]==Robot.MyHome_lowerLeft[0]){			
			
			if(search_start[1]==top){
				search_corner=4;				
			}
			search_corner=1;
		}
		else if(search_start[0]==Robot.MyHome_upperRight[0]){
			if(search_start[1]==top){
				search_corner=3;
			}
			search_corner=2;
		}
	}	
	
	private double[][] getWaypoints(){
		switch(search_corner){
		case 1:
			this.Waypoint[0][0]=Robot.odometer.getX();
			this.Waypoint[0][1]=(Robot.odometer.getY()+ (3*Robot.tile));
			this.Waypoint[1][0]=Waypoint[0][0]+Robot.tile/2;
			this.Waypoint[1][1]=Waypoint[0][1];
			this.Waypoint[2][0]=Waypoint[1][0];
			this.Waypoint[2][1]=Waypoint[1][1]- (2.5*Robot.tile);
			this.Waypoint[3][0]=Waypoint[2][0]+Robot.tile/2;
			this.Waypoint[3][1]=Waypoint[2][1];
			this.Waypoint[4][0]=Waypoint[3][0];
			this.Waypoint[4][1]=Waypoint[3][1]+ (2.5*Robot.tile);
			break;
		case 2:
			this.Waypoint[0][0]=Robot.odometer.getX();
			this.Waypoint[0][1]=(Robot.odometer.getY()+ (3*Robot.tile));
			this.Waypoint[1][0]=Waypoint[0][0]-Robot.tile/2;
			this.Waypoint[1][1]=Waypoint[0][1];
			this.Waypoint[2][0]=Waypoint[1][0];
			this.Waypoint[2][1]=Waypoint[1][1]-(2.5*Robot.tile);
			this.Waypoint[3][0]=Waypoint[2][0]-Robot.tile/2;
			this.Waypoint[3][1]=Waypoint[2][1];
			this.Waypoint[4][0]=Waypoint[3][0];
			this.Waypoint[4][1]=Waypoint[3][1]+(2.5*Robot.tile);
			break;
		case 3:
			this.Waypoint[0][0]=Robot.odometer.getX();
			this.Waypoint[0][1]=(Robot.odometer.getY()- (3*Robot.tile));
			this.Waypoint[1][0]=Waypoint[0][0]-Robot.tile/2;
			this.Waypoint[1][1]=Waypoint[0][1];
			this.Waypoint[2][0]=Waypoint[1][0];
			this.Waypoint[2][1]=Waypoint[1][1]+(2.5*Robot.tile);
			this.Waypoint[3][0]=Waypoint[2][0]-Robot.tile/2;
			this.Waypoint[3][1]=Waypoint[2][1];
			this.Waypoint[4][0]=Waypoint[3][0];
			this.Waypoint[4][1]=Waypoint[3][1]-(2.5*Robot.tile);
			break;
			
		case 4:
			this.Waypoint[0][0]=Robot.odometer.getX();
			this.Waypoint[0][1]=(Robot.odometer.getY()- (3*Robot.tile));
			this.Waypoint[1][0]=Waypoint[0][0]+Robot.tile/2;
			this.Waypoint[1][1]=Waypoint[0][1];
			this.Waypoint[2][0]=Waypoint[1][0];
			this.Waypoint[2][1]=Waypoint[1][1]+(2.5*Robot.tile);
			this.Waypoint[3][0]=Waypoint[2][0]+Robot.tile/2;
			this.Waypoint[3][1]=Waypoint[2][1];
			this.Waypoint[4][0]=Waypoint[3][0];
			this.Waypoint[4][1]=Waypoint[3][1]-(2.5*Robot.tile);
			break;		
				
		}
		
		return this.Waypoint;
	}
	/**
	 * This method searches for the Flag
	 * @return 
	 * 
	 */
	public void Search() throws InterruptedException{
	
		/*
		 * Remember to add a boolean to the odometry correction thread and set it false here
		 */
		setCorner();
		getWaypoints();
		
		while(!isCaptured){
			
			while(!atWaypoint)	{
					forward(1);
					while(!(isObstacle() || atWaypoint()));
					if(!atWaypoint()){
						Robot.odometer.getPosition(current_position);
						Investigate();	
						nextWaypoint(waypoint);
					}
					else if(atWaypoint()){
						nextWaypoint(waypoint);
					}
			}															
		}			
	}		
				
	private boolean isObstacle() {
		if ((usPoller_left.getDistance() < 10 || usPoller_right.getDistance() < 10)) {
			return true;
		}

		else
			return false;
	}	
		
	private boolean atWaypoint() {

		double deltaX= Waypoint[waypoint][0]-Robot.odometer.getX();
		double deltaY= Waypoint[waypoint][1]-Robot.odometer.getY();
		if(Math.abs(deltaX)<dist_error && Math.abs(deltaY)<dist_error){
			return true;
		}
		else{
			return false;
		}
	}	
		
	private void nextWaypoint(int waypoint){
				
		if(atWaypoint()){
			this.waypoint++;
			if(!(waypoint%2==0)){
				 if (search_corner==1 || search_corner==4){
					 changeDirection(Cardinal_Dir.EAST);		
				 }
				 else if(search_corner==2|| search_corner==3){
					 changeDirection(Cardinal_Dir.WEST);
				 }
			}		
			else{
				if(waypoint==2){
					if(search_corner==1 || search_corner==2){
						changeDirection(Cardinal_Dir.SOUTH);		
					}
					else{
						changeDirection(Cardinal_Dir.NORTH);
					}
				}
				else if(waypoint==4){
					if(search_corner==1 || search_corner==2){
						changeDirection(Cardinal_Dir.NORTH);		
					}
					else{
						changeDirection(Cardinal_Dir.SOUTH);		
					}					
				}
			}
			this.atWaypoint=true;
		}
		
		else{
			this.atWaypoint=false;
		}
	}
	
	private void travelToFlag(){
		
		double current_heading= current_position[2];
		
		if(Robot.localizer.getFilteredData(usPoller_left)>=Robot.localizer.getFilteredData(usPoller_right)){
			if((current_heading-3 * Math.PI / 2)<=0.087){
				Robot.navigator.turnTo(Math.PI);
				while(!(Robot.localizer.getFilteredData(usPoller_left)==Robot.localizer.getFilteredData(usPoller_right)));
				Robot.navigator.stopMotors();
			}
			else{
				Robot.navigator.turnTo(0);
				while(!(Robot.localizer.getFilteredData(usPoller_left)==Robot.localizer.getFilteredData(usPoller_right)));
				Robot.navigator.stopMotors();			
			}
		}
		else{
			if((current_heading-3 * Math.PI / 2)<=0.087){
				Robot.navigator.turnTo(0);
				while(!(Robot.localizer.getFilteredData(usPoller_left)==Robot.localizer.getFilteredData(usPoller_right)));
				Robot.navigator.stopMotors();
			}
			else{
				Robot.navigator.turnTo(Math.PI);
				while(!(Robot.localizer.getFilteredData(usPoller_left)==Robot.localizer.getFilteredData(usPoller_right)));
				Robot.navigator.stopMotors();
			}
		}
	}

	
	
	 private void Investigate() throws InterruptedException{
		
		travelToFlag();
		forward(2);
		while(Robot.colorPoller.getColor()>=14);
		if(Robot.colorPoller.getColor() == (Robot.Opp_Color)){
			captureFlag();					
		}
		else{
			Robot.navigator.travelTo(current_position[0],current_position[1]);
			Robot.navigator.turnTo(current_position[2]);
			GetOutTheWay();	
			
		}
	 }
		
	
	/**
	 * This method captures the Flag once found
	 * @return isCaptured Boolean that is true when Flag has been captured.
	 * @throws InterruptedException 
	 */
	private void captureFlag() throws InterruptedException{
		//if setAcceleration is not smooth enough, P-control of arm speed goes here (uses tachocounts)
		//rotate forward 120 degrees?
		armMotor.rotate(120); 
		grabberMotor.forward(); //keep applying torque
		TimeUnit.SECONDS.sleep(3); //give 3 seconds to grab before lifting
		armMotor.rotate(-120); 
		this.isCaptured=true;
	}
	
	private void GetOutTheWay() throws InterruptedException {
		captureFlag(); 
		if(search_corner==1 || search_corner==4){
			changeDirection(Cardinal_Dir.WEST);
		}
		else{
			changeDirection(Cardinal_Dir.EAST);
		}
		armMotor.rotate(120);
		grabberMotor.flt();
		armMotor.rotate(-120);
	}
	

	private void forward(int speed){
		
		if(speed==1){
		Robot.leftMotor.setSpeed(Robot.FORWARD_SPEED);
		Robot.rightMotor.setSpeed(Robot.FORWARD_SPEED);
		
		Robot.leftMotor.forward();
		Robot.rightMotor.forward();
		}
		else{
			Robot.leftMotor.setSpeed(FLAG_SPEED);
			Robot.rightMotor.setSpeed(FLAG_SPEED);
			
			Robot.leftMotor.forward();
			Robot.rightMotor.forward();
		}
	}
	
	private void changeDirection(Cardinal_Dir direction) {
		double theta;
		switch (direction) {
		case NORTH:
			theta = Math.PI / 2;
			break;
		case SOUTH:
			theta = 3 * Math.PI / 2;
			break;
		case WEST:
			theta = Math.PI;
			break;
		case EAST:
			theta = 0;
			break;
		default:
			return;
		}
		Robot.navigator.turnTo(theta);
	}
	
//	public  int convertDistance(double radius, double distance) {
//		return (int) ((180.0 * distance) / (Math.PI * radius));
//	}
//
//	public  int convertAngle(double radius, double width, double angle) {
//		return convertDistance(radius, Math.PI * width * angle / 360.0);
//	}
	

}
