package Tank;

import robocode.HitWallEvent;
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;




public class TankMember extends SmartTank {
	
	private int moveDirection = 1;
	
	public void run() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForRobotTurn(true);
		super.run();
		while (true) {
			 setTurnRadarRight(360);
			 if (enemyTankList.size() >= 1) {
				chooseFocus();
				 //getFocusTarget();
				movement();
				shoot();
			 }
			 
			
			/* 
			 else if (enemyTankList.size() == 1) {
					if (this.getEnergy() > focusTarget.get_energy()) {
						shoot();
						
					}
			 }*/
			 execute();
		//out.println(focusTarget.get_name());
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		super.onScannedRobot(e);
	
	}
	
	public void onMessageReceived(MessageEvent e) {
		super.onMessageReceived(e);
	}
	
	public void onHitWall(HitWallEvent e){
		moveDirection = -1;
		setAhead( 300*moveDirection);
		
	}
	
	private void chooseFocus() {
		focusTarget = getFocusTarget();
		//for (EnemyTank en : enemyTankList) {
			if (focusTarget.get_distance() > getFocusTarget().get_distance()) {
				focusTarget = getFocusTarget() ;
				
		}
	}

	

	
	void shoot() {
		double power = Math.min(500 / getFocusTarget().get_distance(), 3);
		double bulletSpeed = 20 - power * 3;
		double time =  getFocusTarget().get_distance() / bulletSpeed;
		/*futureX =  getFocusTarget().getFutureXFromLastRecord( getTime() - getFocusTarget().getTurn() + (int) time);
		futureY =  getFocusTarget().getFutureYFromLastRecord( getTime() - getFocusTarget().getTurn() + (int) time);
		fx = (int) Math.abs(Math.min(fx, getBattleFieldWidth()));
		fy = (int) Math.abs(Math.min(fy, getBattleFieldHeight())); */

		//Math.abs(Math.min(calcuteFutureX(getTime() - focusTarget.getTurn() + time), getBattleFieldWidth()));
		//Math.abs(Math.min(calcuteFutureY(getTime() - focusTarget.getTurn() + time), getBattleFieldWidth()));
		
		double absDeg = Helper.absoluteBearing(getX(), getY(),Math.abs(Math.min(calcuteFutureX(getTime() - focusTarget.getTurn() + time), getBattleFieldWidth())), Math.abs(Math.min(calcuteFutureY(getTime() - focusTarget.getTurn() + time), getBattleFieldWidth())));
		
		setTurnGunRight(Helper.normalizeBearing(absDeg - getGunHeading()));
		
		
		// if the gun is cool and we're pointed in the right direction, shoot!
					if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 1) {
						
						setFire(power);
		}
		
	}
	
	
	
	  public double calcuteFutureX(double timing){
	    	double futureX = getX() + Math.sin(Math.toRadians(getFocusTarget().getAbsBearingDegree())) * focusTarget.get_distance();
			return futureX + Math.sin(Math.toRadians(getFocusTarget().get_heading())) * getFocusTarget().get_velocity() * timing;
	    }
	    
	  public double calcuteFutureY(double timing){
	    	double futureY =  getY() + Math.cos(Math.toRadians(getFocusTarget().getAbsBearingDegree())) * getFocusTarget().get_distance();
			return futureY + Math.cos(Math.toRadians(getFocusTarget().get_heading())) * getFocusTarget().get_velocity() * timing;
	    }
	
		void movement() {
			
			// move a little closer
			if (getFocusTarget().get_distance() > 200){
				setTurnRight(getFocusTarget().get_bearing());
				setAhead(getFocusTarget().get_distance() / 2);
			}
			// but not too close
			else if (getFocusTarget().get_distance() < 200){
				setTurnRight(getFocusTarget().get_bearing());
				setBack(getFocusTarget().get_distance());
			}
		
		}
		
		public void onRobotDeath(RobotDeathEvent e) {
			for (EnemyTank enemy : enemyTankList) {
				if (e.getName().equals(enemy.get_name())) {
					enemyTankList.remove(enemy);
					break;
				}
			}
		}
}
