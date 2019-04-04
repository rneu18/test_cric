package com.theNewCone.cricketScoreCard.player;

import com.theNewCone.cricketScoreCard.enumeration.DismissalType;
import com.theNewCone.cricketScoreCard.utils.CommonUtils;

import java.io.Serializable;

public class BowlerStats implements Serializable {
	private String oversBowled;
	private double economy;
	private int runsGiven, maidens, wickets;
	private int bowled, caught, hitWicket, lbw, stumped;

    private Player player;

	public String getBowlerName() {
		return player.getName();
	}

	public String getOversBowled() {
		return oversBowled;
	}

	public void setOversBowled(String oversBowled) {
		this.oversBowled = oversBowled;
	}

	public double getEconomy() {
		return economy;
	}

	public int getRunsGiven() {
		return runsGiven;
	}

	public void incRunsGiven(int runsGiven, boolean isCancel) {
		this.runsGiven += isCancel ? (runsGiven * -1) : runsGiven;
	}

	public int getMaidens() {
		return maidens;
	}

	public void incMaidens() {
		this.maidens++;
	}

	public int getWickets() {
		return wickets;
	}

	public void incWickets(DismissalType dismissalType) {
		this.wickets++;
		switch (dismissalType) {
			case BOWLED:
				bowled++;
				break;

			case CAUGHT:
				caught++;
				break;

			case HIT_WICKET:
				hitWicket++;
				break;

			case LBW:
				lbw++;
				break;

			case STUMPED:
				stumped++;
				break;
		}
	}

    public Player getPlayer() {
        return player;
    }

	public BowlerStats(Player player) {
		this.player = player;
		this.oversBowled = "0.0";
		this.economy = CommonUtils.calcRunRate(runsGiven, Double.parseDouble(oversBowled));
	}

	public void evaluateEconomy() {
		this.economy = CommonUtils.calcRunRate(runsGiven, Double.parseDouble(oversBowled));
	}

	public int getBowled() {
		return bowled;
	}

	public int getCaught() {
		return caught;
	}

	public int getHitWicket() {
		return hitWicket;
	}

	public int getLbw() {
		return lbw;
	}

	public int getStumped() {
		return stumped;
	}
}
