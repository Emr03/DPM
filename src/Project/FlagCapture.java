package Project;

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
	private boolean isCaptured;
	private int maxDist=15;
	private UltrasonicPoller usPoller_left;
	private UltrasonicPoller usPoller_right;
	
	/**
	 * Creates an Object of type FlagCapture
	 * @param flagColor float representing color of opponents flag
	 */
	
	public FlagCapture(float flagColor){
		this.flagColor=flagColor;
		this.isCaptured=false;
		this.usPoller_left=Robot.usPoller_left;
		this.usPoller_right=Robot.usPoller_right;
		
	}
	
	
	/**
	 * This method searches for the Flag
	 * 
	 */
	public void Search(){
		
		while(!isCaptured){
			if(getFilteredData(usPoller_left)<maxDist || getFilteredData(usPoller_right)<maxDist){
//				navigator.travelTo
				
			}
			
			
			
		}
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	/**
	 * This method captures the Flag once found
	 * @return isCaptured Boolean that is true when Flag has been captured.
	 */
	public boolean captureFlag(){
		
		
		
		
		
		
		return isCaptured;
		
	}
	
	
	private int getFilteredData(UltrasonicPoller usPoller) {
		
		int distance = usPoller.getDistance();
		
		if (distance> 30){
			distance=maxDist;
		}
	
					
		return distance;
	}
	
	
	

}
