package Project;

public enum Cardinal_Dir {
	NORTH(Math.PI / 2,"north"),
	EAST(0,"east"),
	SOUTH(3 * Math.PI / 2,"south"),
	WEST(Math.PI,"west");
	
	private double theta=0;
	private String direction;
	private Cardinal_Dir(double theta,String direction){
		this.theta=theta;
		this.direction=direction;
		
	}
		
	public double getTheta(){
		return this.theta;
		
	}

	public String getDirection(){
		return this.direction;
	}
}
