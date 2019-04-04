package com.theNewCone.cricketScoreCard.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.theNewCone.cricketScoreCard.R;
import com.theNewCone.cricketScoreCard.activity.GraphsActivity;
import com.theNewCone.cricketScoreCard.activity.ScoreCardActivity;
import com.theNewCone.cricketScoreCard.intf.DrawerController;
import com.theNewCone.cricketScoreCard.match.CricketCardUtils;
import com.theNewCone.cricketScoreCard.match.Match;
import com.theNewCone.cricketScoreCard.match.Team;
import com.theNewCone.cricketScoreCard.player.Player;
import com.theNewCone.cricketScoreCard.utils.CommonUtils;

import java.util.Locale;

public class MatchSummaryFragment extends Fragment {

	private static final String BUNDLE_CC_UTILS = "CricketCardUtils";
	private static final String BUNDLE_MATCH_INFO = "MatchInfo";

	CricketCardUtils ccUtils;
	Match match;

	public static MatchSummaryFragment newInstance(CricketCardUtils ccUtils, Match match) {
		MatchSummaryFragment fragment = new MatchSummaryFragment();
		fragment.initialize(ccUtils, match);

		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View theView = inflater.inflate(R.layout.fragment_match_summary, container, false);

		getSavedState(savedInstanceState);

		if(getActivity() != null) {
			DrawerController drawerController = (DrawerController) getActivity();
			drawerController.setDrawerEnabled(true);
			drawerController.enableAllDrawerMenuItems();
			getActivity().setTitle(getString(R.string.title_fragment_match_summary));
		}

		setupView(theView);

		return theView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_fragments_match_summary, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_scoreCard:
				if(getActivity() != null) {
					Intent scoreCardIntent = new Intent(getContext(), ScoreCardActivity.class);
					scoreCardIntent.putExtra(ScoreCardActivity.ARG_CRICKET_CARD_UTILS, CommonUtils.convertToJSON(ccUtils));
					startActivity(scoreCardIntent);
				}
				break;

			case R.id.menu_show_graph:
				if(getActivity() != null) {
					Intent graphIntent = new Intent(getContext(), GraphsActivity.class);
					graphIntent.putExtra(GraphsActivity.ARG_CRICKET_CARD_UTILS, CommonUtils.convertToJSON(ccUtils));
					startActivity(graphIntent);
					break;
				}
		}
		return true;
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		if(ccUtils != null)
			outState.putString(BUNDLE_CC_UTILS, CommonUtils.convertToJSON(ccUtils));

		if (match != null)
			outState.putSerializable(BUNDLE_MATCH_INFO, match);
	}

	private void initialize(CricketCardUtils ccUtils, Match matchInfo) {
		this.ccUtils = ccUtils;
		this.match = matchInfo;
	}

	private void getSavedState(Bundle bundle) {
		if(bundle != null) {
			ccUtils = CommonUtils.convertToCCUtils(bundle.getString(BUNDLE_CC_UTILS));
			match = (Match) bundle.getSerializable(BUNDLE_MATCH_INFO);
		}
	}

	private void setupView(View rootView) {
		TextView tvSCVersus = rootView.findViewById(R.id.tvSCVersus);
		TextView tvSCMatchDate = rootView.findViewById(R.id.tvSCMatchDate);
		TextView tvSCTossInfo = rootView.findViewById(R.id.tvSCTossInfo);
		TextView tvSCTeam1Name = rootView.findViewById(R.id.tvSCTeam1Name);
		TextView tvSCTeam2Name = rootView.findViewById(R.id.tvSCTeam2Name);
		TextView tvSCTeam1 = rootView.findViewById(R.id.tvSCTeam1);
		TextView tvSCTeam2 = rootView.findViewById(R.id.tvSCTeam2);
		TextView tvSCMatchResult = rootView.findViewById(R.id.tvSCMatchResult);
		TextView tvSCPoM = rootView.findViewById(R.id.tvSCPoM);

		if(ccUtils != null) {

			Team team1 = ccUtils.getTeam1(), team2 = ccUtils.getTeam2();

			String tossWonBy = (team1.getId() == ccUtils.getTossWonBy())  ? team1.getName() : team2.getName();
			String electedTo = (team1.getId() == ccUtils.getTossWonBy()) ? "Bat" : "Field";

			tvSCVersus.setText(String.format(Locale.getDefault(), "%s vs %s", team1.getName(), team2.getName()));
			tvSCTossInfo.setText(String.format(Locale.getDefault(),
					"%s won the toss and elected to %s", tossWonBy.toUpperCase(), electedTo.toUpperCase()));

			if (match != null && match.getDate() != null) {
				tvSCMatchDate.setText(String.format(Locale.getDefault(), "Played on %s",
						CommonUtils.dateToString(match.getDate(), "EEEE, MMMM d, yyyy")));
			}

			String teamName = team1.getShortName() + " Team";
			tvSCTeam1Name.setText(teamName);
			teamName = team2.getShortName() + " Team";
			tvSCTeam2Name.setText(teamName);

			StringBuilder teamInfoSB = new StringBuilder();
			for (Player player : team1.getMatchPlayers()) {
				String playerName = player.getName();
				playerName = (player.getID() == team1.getCaptain().getID()) ? playerName + " (c)" : playerName;
				playerName = (player.getID() == team1.getWicketKeeper().getID()) ? playerName + " (wk)" : playerName;
				teamInfoSB.append(playerName);
				teamInfoSB.append(", ");
			}
			teamInfoSB.delete(teamInfoSB.length() - 2, teamInfoSB.length());
			tvSCTeam1.setText(teamInfoSB.toString());

			teamInfoSB = new StringBuilder();
			for (Player player : team2.getMatchPlayers()) {
				String playerName = player.getName();
				playerName = (player.getID() == team1.getCaptain().getID()) ? playerName + " (c)" : playerName;
				playerName = (player.getID() == team1.getWicketKeeper().getID()) ? playerName + " (wk)" : playerName;
				teamInfoSB.append(playerName);
				teamInfoSB.append(", ");
			}
			teamInfoSB.delete(teamInfoSB.length() - 2, teamInfoSB.length());
			tvSCTeam2.setText(teamInfoSB.toString());

			if(ccUtils.getResult() != null)
				tvSCMatchResult.setText(ccUtils.getResult());

			if(ccUtils.getPlayerOfMatch() != null)
				tvSCPoM.setText(String.format(Locale.getDefault(), getString(R.string.tvPoMData), ccUtils.getPlayerOfMatch().getName()));
		}
	}
}
