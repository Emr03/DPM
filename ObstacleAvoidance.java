package Project;

public class ObstacleAvoidance {
	private final int bandCenter = 20, bandwidth = 2;
	private final int motorStraight = 275, FILTER_OUT = 20;

	// holds the distance returned by US sensors
	// 0 -> left, 1 -> middle, 2 -> right
	private int[] distance = new int[3];

	private int filterControl = 40; // used to wait after detecting a gap
	boolean avoided = false;
	
	private int[] left_wheel_control = {};  //gain vector that sets the speed of the left wheel
	private int[] right_wheel_control = {}; //gain vector that sets the speed of the right wheel 

	/**
	 * Avoids an obstacle using MIMO P-control with Ultrasonic sensor readings
	 */
	public void avoid() {
		while (!avoided) {
			distance[0] = Robot.usPoller_left.getDistance();
			distance[1] = Robot.usPoller_middle.getDistance();
			distance[2] = Robot.usPoller_right.getDistance();

			//check if coast is clear
			
			
			int leftSpeed=0, rightSpeed=0; 
			
			//vector multiplication
			for (int i=0; i<3; i++){
				leftSpeed+=left_wheel_control[i]*distance[i]; 
				rightSpeed+=right_wheel_control[i]*distance[i]; 
			}
			
			Robot.leftMotor.setSpeed(leftSpeed);
			Robot.rightMotor.setSpeed(rightSpeed); 
			
			Robot.leftMotor.forward(); 
			Robot.rightMotor.forward(); 
		}

	}

}
