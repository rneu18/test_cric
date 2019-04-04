package com.theNewCone.cricketScoreCard.statistics;

public class PlayerMatchData {
	private int matchID;
	private int runsScored, ballsPlayed, foursHit, sixesHit;
	private boolean isOut;

	private double oversBowled;
	private int runsGiven, wicketsTaken, maidens;

	private int catches, stumps, runOuts;

	public int getMatchID() {
		return matchID;
	}

	public void setMatchID(int matchID) {
		this.matchID = matchID;
	}

	public int getRunsScored() {
		return runsScored;
	}

	public void setRunsScored(int runsScored) {
		this.runsScored = runsScored;
	}

	int getBallsPlayed() {
		return ballsPlayed;
	}

	public void setBallsPlayed(int ballsPlayed) {
		this.ballsPlayed = ballsPlayed;
	}

	public void setFoursHit(int foursHit) {
		this.foursHit = foursHit;
	}

	public void setSixesHit(int sixesHit) {
		this.sixesHit = sixesHit;
	}

	public boolean isOut() {
		return isOut;
	}

	public void setOut(boolean out) {
		isOut = out;
	}

	double getOversBowled() {
		return oversBowled;
	}

	public void setOversBowled(double oversBowled) {
		this.oversBowled = oversBowled;
	}

	int getRunsGiven() {
		return runsGiven;
	}

	public void setRunsGiven(int runsGiven) {
		this.runsGiven = runsGiven;
	}

	int getWicketsTaken() {
		return wicketsTaken;
	}

	public void setWicketsTaken(int wicketsTaken) {
		this.wicketsTaken = wicketsTaken;
	}

	int getMaidens() {
		return maidens;
	}

	public void setMaidens(int maidens) {
		this.maidens = maidens;
	}

	int getCatches() {
		return catches;
	}

	public void setCatches(int catches) {
		this.catches = catches;
	}

	int getStumps() {
		return stumps;
	}

	public void setStumps(int stumps) {
		this.stumps = stumps;
	}

	int getRunOuts() {
		return runOuts;
	}

	public void setRunOuts(int runOuts) {
		this.runOuts = runOuts;
	}
}
