package com.theNewCone.cricketScoreCard.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.theNewCone.cricketScoreCard.R;
import com.theNewCone.cricketScoreCard.activity.TeamSelectActivity;
import com.theNewCone.cricketScoreCard.enumeration.TournamentFormat;
import com.theNewCone.cricketScoreCard.enumeration.TournamentStageType;
import com.theNewCone.cricketScoreCard.intf.DrawerController;
import com.theNewCone.cricketScoreCard.match.Team;
import com.theNewCone.cricketScoreCard.tournament.Tournament;
import com.theNewCone.cricketScoreCard.utils.CommonUtils;
import com.theNewCone.cricketScoreCard.utils.TournamentUtils;
import com.theNewCone.cricketScoreCard.utils.database.TournamentDBHandler;

public class NewTournamentFragment extends Fragment
		implements View.OnClickListener {

	private static final int REQ_CODE_SELECT_TEAMS = 1;

	View theView;

	EditText etTournamentName, etNumGroups, etTournamentTeamCount, etNumRounds;
	EditText etMaxOvers, etMaxPerBowler, etMaxWickets, etNumPlayers;
	TextView tvSelectedTeams, tvNumRounds;
	RadioButton rbTTRoundRobin, rbTTGroups, rbTTKnockOut, rbTTBilateral;
	RadioButton rbTSSuperFours, rbTSSuperSixes, rbTSKnockOut, rbTSQualifiers, rbTSNone;
	Button btnValidate, btnConfirm, btnSelectTeams;
	LinearLayout llGroupSize, llTournamentStage, llNumRounds, llTournamentType;
	GridLayout rgTTGroup, rgTSGroup, glOversAndPlayers;
	private Team[] selTeams;
	private int numTeams, numGroups, numRounds, maxOvers, maxWickets, maxPerBowler, numPlayers;
	private TournamentFormat type = null;
	private TournamentStageType stageType = null;

	public NewTournamentFragment() {
		// Required empty public constructor
	}

	public static NewTournamentFragment newInstance() {
		return new NewTournamentFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		theView = inflater.inflate(R.layout.fragment_new_tournament, container, false);

		initialize();

		if (getActivity() != null)
			getActivity().setTitle(getString(R.string.title_fragment_new_tournament));

		return theView;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnSelectTournamentTeams:
				getTeams();

				break;

			case R.id.btnTournamentValidate:
				if (validate()) {
					glOversAndPlayers.setVisibility(View.VISIBLE);
					btnConfirm.setVisibility(View.VISIBLE);
					lockTournamentInfo();
					updateMaxPerBowler();
					updateNumPlayers();
				}
				break;

			case R.id.btnConfirmTournament:
				if (confirmTournament())
					createNewTournament();
				break;

			case R.id.rbTTRoundRobin:
				clearOtherCheckedRadioButtons(rgTTGroup, view.getId());
				type = TournamentFormat.ROUND_ROBIN;

				setVisibility();
				updateStageOptions();
				break;

			case R.id.rbTTGroups:
				clearOtherCheckedRadioButtons(rgTTGroup, view.getId());
				type = TournamentFormat.GROUPS;
				numGroups = 2;

				setVisibility();
				updateStageOptions();
				break;

			case R.id.rbTTKnockOut:
				clearOtherCheckedRadioButtons(rgTTGroup, view.getId());
				type = TournamentFormat.KNOCK_OUT;

				setVisibility();
				updateStageOptions();
				break;

			case R.id.rbTTBilateral:
				clearOtherCheckedRadioButtons(rgTTGroup, view.getId());
				type = TournamentFormat.BILATERAL;

				setVisibility();
				updateStageOptions();
				break;

			case R.id.rbTSSuperFourStage:
				clearOtherCheckedRadioButtons(rgTSGroup, view.getId());
				stageType = TournamentStageType.SUPER_FOUR;
				break;

			case R.id.rbTSSuperSixStage:
				clearOtherCheckedRadioButtons(rgTSGroup, view.getId());
				stageType = TournamentStageType.SUPER_SIX;
				break;

			case R.id.rbTSKnockOut:
				clearOtherCheckedRadioButtons(rgTSGroup, view.getId());
				stageType = TournamentStageType.KNOCK_OUT;
				break;

			case R.id.rbTSQualifiers:
				clearOtherCheckedRadioButtons(rgTSGroup, view.getId());
				stageType = TournamentStageType.QUALIFIER;
				break;

			case R.id.rbTSNone:
				stageType = TournamentStageType.NONE;
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case REQ_CODE_SELECT_TEAMS:
				if (requestCode == TeamSelectActivity.RESP_CODE_OK) {
					if (data.getExtras() != null
							&& data.getSerializableExtra(TeamSelectActivity.ARG_RESP_TEAMS) != null) {
						selTeams = CommonUtils.objectArrToTeamArr(
								(Object[]) data.getSerializableExtra(TeamSelectActivity.ARG_RESP_TEAMS));

						if (selTeams != null && selTeams.length > 0) {
							llTournamentType.setVisibility(View.VISIBLE);
							updateFormatOptions();

							StringBuilder selTeamSB = new StringBuilder();
							for (Team team : selTeams) {
								selTeamSB.append(team.getName());
								selTeamSB.append(",\t");
							}
							selTeamSB.delete(selTeamSB.length() - 2, selTeamSB.length());

							tvSelectedTeams.setText(selTeamSB.toString());
						}
					}
				}
				break;
		}
	}

	private void initialize() {
		etTournamentName = theView.findViewById(R.id.etTournamentName);
		etNumGroups = theView.findViewById(R.id.etTournamentGroups);
		etTournamentTeamCount = theView.findViewById(R.id.etTournamentTeamCount);

		etMaxOvers = theView.findViewById(R.id.etMaxOvers);
		etMaxPerBowler = theView.findViewById(R.id.etMaxPerBowler);
		etMaxWickets = theView.findViewById(R.id.etMaxWickets);
		etNumPlayers = theView.findViewById(R.id.etNumPlayers);
		etNumRounds = theView.findViewById(R.id.etNumRounds);

		String maximumOvers = etMaxOvers.getText().toString();
		String maximumWickets = etMaxWickets.getText().toString();
		maxOvers = maximumOvers.equals("") ? 0 : Integer.parseInt(maximumOvers);
		maxWickets = maximumWickets.equals("") ? 0 : Integer.parseInt(maximumWickets);

		tvSelectedTeams = theView.findViewById(R.id.tvSelectedTeams);
		tvNumRounds = theView.findViewById(R.id.tvNumRounds);

		btnSelectTeams = theView.findViewById(R.id.btnSelectTournamentTeams);
		btnValidate = theView.findViewById(R.id.btnTournamentValidate);
		btnConfirm = theView.findViewById(R.id.btnConfirmTournament);

		rbTTRoundRobin = theView.findViewById(R.id.rbTTRoundRobin);
		rbTTGroups = theView.findViewById(R.id.rbTTGroups);
		rbTTKnockOut = theView.findViewById(R.id.rbTTKnockOut);
		rbTTBilateral = theView.findViewById(R.id.rbTTBilateral);

		rbTSSuperFours = theView.findViewById(R.id.rbTSSuperFourStage);
		rbTSSuperSixes = theView.findViewById(R.id.rbTSSuperSixStage);
		rbTSKnockOut = theView.findViewById(R.id.rbTSKnockOut);
		rbTSQualifiers = theView.findViewById(R.id.rbTSQualifiers);
		rbTSNone = theView.findViewById(R.id.rbTSNone);

		llGroupSize = theView.findViewById(R.id.llGroupSize);
		llTournamentStage = theView.findViewById(R.id.llTournamentStage);
		llTournamentType = theView.findViewById(R.id.llTournamentType);
		llNumRounds = theView.findViewById(R.id.llNumRounds);

		rgTTGroup = theView.findViewById(R.id.rgTournamentType);
		rgTSGroup = theView.findViewById(R.id.rgTournamentStage);
		glOversAndPlayers = theView.findViewById(R.id.glOversAndPlayers);

		btnSelectTeams.setOnClickListener(this);
		btnValidate.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);

		rbTTRoundRobin.setOnClickListener(this);
		rbTTGroups.setOnClickListener(this);
		rbTTKnockOut.setOnClickListener(this);
		rbTTBilateral.setOnClickListener(this);

		rbTSSuperFours.setOnClickListener(this);
		rbTSSuperSixes.setOnClickListener(this);
		rbTSKnockOut.setOnClickListener(this);
		rbTSQualifiers.setOnClickListener(this);
		rbTSNone.setOnClickListener(this);

		setTextChangeListeners();
		updateMaxPerBowler();
		updateNumPlayers();
	}

	private void setVisibility() {
		int groupSizeVisibility = View.GONE;
		int numRoundsVisibility = View.GONE;
		int stageVisibility = View.GONE;

		switch (type) {
			case ROUND_ROBIN:
				numRoundsVisibility = View.VISIBLE;
				stageVisibility = View.VISIBLE;
				tvNumRounds.setText(R.string.numRounds);
				etNumRounds.setText(R.string.one);
				break;

			case GROUPS:
				numRoundsVisibility = View.VISIBLE;
				stageVisibility = View.VISIBLE;
				groupSizeVisibility = View.VISIBLE;
				tvNumRounds.setText(R.string.numRounds);
				etNumRounds.setText(R.string.one);
				break;

			case BILATERAL:
				numRoundsVisibility = View.VISIBLE;
				tvNumRounds.setText(R.string.numMatches);
				etNumRounds.setText(R.string.three);
				break;

			case KNOCK_OUT:
				etNumRounds.setText(R.string.one);
				numRounds = 1;
				break;
		}

		llGroupSize.setVisibility(groupSizeVisibility);
		llNumRounds.setVisibility(numRoundsVisibility);
		llTournamentStage.setVisibility(stageVisibility);
	}

	private void updateFormatOptions() {
		type = null;
		clearOtherCheckedRadioButtons(rgTTGroup, 0);
		boolean knockOutOk = numTeams > 2 && CommonUtils.isPowerOf(numTeams, 2);
		boolean groupsOK = numTeams >= 6 && !CommonUtils.isPrime(numTeams);
		boolean roundRobinOk = numTeams > 2;
		boolean bilateralOk = numTeams == 2;

		rbTTRoundRobin.setEnabled(roundRobinOk);
		rbTTGroups.setEnabled(groupsOK);
		rbTTKnockOut.setEnabled(knockOutOk);
		rbTTBilateral.setEnabled(bilateralOk);
	}

	private void updateStageOptions() {
		boolean superFourOK = false, superSixOk = false, knockOutOk = false, qualifierOk = false, noneOk = false;
		stageType = null;
		clearOtherCheckedRadioButtons(rgTSGroup, 0);
		switch (type) {
			case ROUND_ROBIN:
				superFourOK = numTeams > 4;
				superSixOk = numTeams > 6;
				knockOutOk = numTeams > 2;
				qualifierOk = numTeams > 4;
				noneOk = true;
				rgTSGroup.setVisibility(View.VISIBLE);
				break;

			case GROUPS:
				if (numGroups > 1 && (numTeams % numGroups == 0) && (numTeams / numGroups > 2)) {
					superFourOK = CommonUtils.isDivisibleBy(4, numGroups);
					qualifierOk = superFourOK;
					superSixOk = numTeams > 6 && CommonUtils.isDivisibleBy(6, numGroups);
					knockOutOk = CommonUtils.isPowerOf(numGroups, 2)
							|| CommonUtils.isPowerOf(numGroups * 2, 2)
							|| CommonUtils.isPowerOf(numGroups * 4, 2);

					rgTSGroup.setVisibility(View.VISIBLE);
				}
				break;

			case KNOCK_OUT:
				stageType = TournamentStageType.KNOCK_OUT;
				rgTSGroup.setVisibility(View.VISIBLE);
				rbTSNone.setChecked(true);
				noneOk = true;
				break;

			case BILATERAL:
				stageType = TournamentStageType.NONE;
				rgTSGroup.setVisibility(View.VISIBLE);
				rbTSNone.setChecked(true);
				noneOk = true;
				break;
		}

		rbTSSuperFours.setEnabled(superFourOK);
		rbTSSuperSixes.setEnabled(superSixOk);
		rbTSKnockOut.setEnabled(knockOutOk);
		rbTSQualifiers.setEnabled(qualifierOk);
		rbTSNone.setEnabled(noneOk);
	}

	private void getTeams() {
		String teamSize = etTournamentTeamCount.getText().toString();
		numTeams = (teamSize.equals("")) ? 0 : Integer.parseInt(teamSize);
		if (numTeams == 0) {
			Toast.makeText(getContext(), "Please provide number of teams", Toast.LENGTH_SHORT).show();
		} else {
			Intent selTeamsIntent = new Intent(getContext(), TeamSelectActivity.class);
			selTeamsIntent.putExtra(TeamSelectActivity.ARG_IS_MULTI, true);
			if (selTeams != null && selTeams.length > 0) {
				selTeamsIntent.putExtra(TeamSelectActivity.ARG_EXISTING_TEAMS, selTeams);
			}
			selTeamsIntent.putExtra(TeamSelectActivity.ARG_SELECT_COUNT, numTeams);
			startActivityForResult(selTeamsIntent, REQ_CODE_SELECT_TEAMS);
		}
	}

	private void clearOtherCheckedRadioButtons(ViewGroup viewGroup, int selectedViewID) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			RadioButton radioButton = theView.findViewById(viewGroup.getChildAt(i).getId());
			if (radioButton.getId() != selectedViewID) {
				radioButton.setChecked(false);
			}
		}
	}

	private void setRadioGroupEnabled(ViewGroup viewGroup, boolean enabled) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			RadioButton radioButton = theView.findViewById(viewGroup.getChildAt(i).getId());
			radioButton.setEnabled(enabled);
		}
	}

	private void setTextChangeListeners() {
		etTournamentTeamCount.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable != null && !editable.toString().equals("")) {
					if (numTeams != Integer.parseInt(editable.toString())) {
						numTeams = Integer.parseInt(editable.toString());
						if (selTeams != null && selTeams.length != 0) {
							Toast.makeText(getContext(),
									getResources().getString(R.string.teamCountChangedReselect), Toast.LENGTH_SHORT).show();

							rgTSGroup.setVisibility(View.GONE);
						}
					}
				} else {
					numTeams = 0;
				}

				updateFormatOptions();
				if (type != null)
					updateStageOptions();
			}
		});

		etNumGroups.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable != null && !editable.toString().equals("")) {
					numGroups = Integer.parseInt(editable.toString());
					updateStageOptions();
				}
			}
		});

		etMaxWickets.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable != null && !editable.toString().equals("")) {
					if (maxWickets != Integer.parseInt(editable.toString())) {
						maxWickets = Integer.parseInt(editable.toString());
						updateNumPlayers();
						updateMaxPerBowler();
					}
				}
			}
		});

		etMaxOvers.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable != null && !editable.toString().equals("")) {
					if (maxOvers != Integer.parseInt(editable.toString())) {
						maxOvers = Integer.parseInt(editable.toString());
						updateMaxPerBowler();
					}
				}
			}
		});

		etNumPlayers.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable != null && !editable.toString().equals("")) {
					if (numPlayers != Integer.parseInt(editable.toString())) {
						numPlayers = Integer.parseInt(editable.toString());
					}
				}
			}
		});

		etMaxPerBowler.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable != null && !editable.toString().equals("")) {
					if (maxPerBowler != Integer.parseInt(editable.toString())) {
						maxPerBowler = Integer.parseInt(editable.toString());
					}
				}
			}
		});
	}

	private void lockTournamentInfo() {
		etTournamentTeamCount.setEnabled(false);
		etNumGroups.setEnabled(false);
		btnSelectTeams.setEnabled(false);
		etNumRounds.setEnabled(false);
		setRadioGroupEnabled(rgTTGroup, false);
		setRadioGroupEnabled(rgTSGroup, false);

		if (getActivity() != null) {
			DrawerController drawerController = (DrawerController) getActivity();
			drawerController.disableAllDrawerMenuItems();
			drawerController.enableDrawerMenuItem(R.id.menu_help);
		}
	}

	private boolean validate() {
		StringBuilder messageSB = new StringBuilder();
		int errorNumber = 1;
		numGroups = etNumGroups.getText().toString().equals("") ? 0 : Integer.parseInt(etNumGroups.getText().toString());
		numTeams = etTournamentTeamCount.getText().toString().equals("") ? 0 : Integer.parseInt(etTournamentTeamCount.getText().toString());

		String tournamentName = etTournamentName.getText().toString();
		if (tournamentName.length() < 10) {
			messageSB.append(errorNumber++);
			messageSB.append(". ");
			messageSB.append(getResources().getString(R.string.invalidTournamentName));
		}

		if (numTeams < 2) {
			messageSB.append(errorNumber++);
			messageSB.append(". ");
			messageSB.append(getResources().getString(R.string.NT_needMinimumTwoTeams));
		} else if (selTeams == null || selTeams.length == 0) {
			messageSB.append(errorNumber++);
			messageSB.append(". ");
			messageSB.append(getResources().getString(R.string.NT_selectTeams));
		} else if (numTeams != selTeams.length) {
			messageSB.append(errorNumber++);
			messageSB.append(". ");
			messageSB.append(getResources().getString(R.string.NT_teamCountMismatch));
		}

		if (type == null) {
			messageSB.append(errorNumber++);
			messageSB.append(". ");
			messageSB.append(getResources().getString(R.string.NT_selectType));
		} else {
			if ((type == TournamentFormat.ROUND_ROBIN || type == TournamentFormat.GROUPS) && stageType == null) {
				messageSB.append(errorNumber++);
				messageSB.append(". ");
				messageSB.append(getResources().getString(R.string.NT_selectStage));
			}

			String numberOfRounds = etNumRounds.getText().toString();
			numRounds = numberOfRounds.equals("") ? 0 : Integer.parseInt(numberOfRounds);
			if (numRounds < 0) {
				messageSB.append(errorNumber++);
				messageSB.append(". ");
				messageSB.append(getResources().getString(R.string.NT_needMinimumOneRound));
			}

			if (type == TournamentFormat.KNOCK_OUT) {
				if (!CommonUtils.isPowerOf(numTeams, 2)) {
					messageSB.append(errorNumber++);
					messageSB.append(". ");
					messageSB.append(getResources().getString(R.string.NT_invalidKOTeamSize));
				}
			}

			if (type == TournamentFormat.GROUPS) {
				if (numGroups < 2) {
					messageSB.append(errorNumber++);
					messageSB.append(". ");
					messageSB.append(getResources().getString(R.string.NT_minimumGroupsNotMet));
				} else if (!CommonUtils.isDivisibleBy(numTeams, numGroups)) {
					messageSB.append(errorNumber++);
					messageSB.append(". ");
					messageSB.append(getResources().getString(R.string.NT_invalidGroupDistribution));
				} else if (numTeams / numGroups <= 2) {
					messageSB.append(errorNumber++);
					messageSB.append(". ");
					messageSB.append(getResources().getString(R.string.NT_minimumTeamsPerGroupNotMet));
				}

				if (numRounds > 2) {
					messageSB.append(errorNumber++);
					messageSB.append(". ");
					messageSB.append(getResources().getString(R.string.NT_limitRoundsForGroup));
				}
			}

			if (type == TournamentFormat.ROUND_ROBIN) {
				if (numTeams < 3) {
					messageSB.append(errorNumber++);
					messageSB.append(". ");
					messageSB.append(getResources().getString(R.string.NT_minimumTeamForRR));
				}

				if (numRounds > 3) {
					messageSB.append(errorNumber++);
					messageSB.append(". ");
					messageSB.append(getResources().getString(R.string.NT_limitRoundsForRR));
				}
			}

			if (type == TournamentFormat.BILATERAL) {
				if (numTeams != 2) {
					messageSB.append(errorNumber++);
					messageSB.append(". ");
					messageSB.append(getResources().getString(R.string.NT_limitTeamsForBilateral));
				}
			}

			if (stageType == null) {
				messageSB.append(errorNumber++);
				messageSB.append(". Tournament Stage Type not selected.");
			} else {
				if (type == TournamentFormat.GROUPS) {
					if (stageType == TournamentStageType.KNOCK_OUT) {
						if (!(CommonUtils.isPowerOf(numGroups, 2)
								|| CommonUtils.isPowerOf(numGroups * 2, 2)
								|| (numTeams / numGroups >= 4 && CommonUtils.isPowerOf(numGroups * 4, 2)))) {
							messageSB.append(errorNumber++);
							messageSB.append(". ");
							messageSB.append(getResources().getString(R.string.NT_numGroupsUnsuitableForKnockOut));
						}
					} else if (stageType == TournamentStageType.SUPER_FOUR) {
						if (!(CommonUtils.isDivisibleBy(numGroups, 4)
								|| CommonUtils.isDivisibleBy(numGroups * 2, 4))) {
							messageSB.append(errorNumber++);
							messageSB.append(". ");
							messageSB.append(getResources().getString(R.string.NT_numGroupsUnsuitableForSuper4));
						}
					} else if (stageType == TournamentStageType.SUPER_SIX) {
						if (!(CommonUtils.isDivisibleBy(numGroups, 6)
								|| CommonUtils.isDivisibleBy(numGroups * 2, 6)
								|| CommonUtils.isDivisibleBy(numGroups * 3, 6))) {
							messageSB.append(errorNumber++);
							messageSB.append(". ");
							messageSB.append(getResources().getString(R.string.NT_numGroupsUnsuitableForSuper6));
						}
					}
				}
			}
		}

		if (errorNumber > 1) {
			showMessage(getResources().getString(R.string.NT_infoValidationTitle), messageSB.toString());
			return false;
		} else {
			return true;
		}
	}

	private boolean confirmTournament() {
		StringBuilder messageSB = new StringBuilder();
		int errorNumber = 1;

		if (maxOvers <= 0 || maxWickets <= 0 || maxPerBowler <= 0 || numPlayers <= 0) {
			messageSB.append(errorNumber++);
			messageSB.append(") ");
			messageSB.append(getResources().getString(R.string.NM_invalidPlayersOversWickets));
		} else {
			if (maxWickets >= numPlayers) {
				messageSB.append(errorNumber++);
				messageSB.append(") ");
				messageSB.append(getResources().getString(R.string.NM_wicketsMoreThanPlayers));
			}
			if (maxPerBowler > maxOvers) {
				messageSB.append(errorNumber++);
				messageSB.append(") ");
				messageSB.append(getResources().getString(R.string.NM_bowlerOversMoreThanMaxOvers));
			}
			if (maxPerBowler < ((maxOvers % numPlayers == 0) ? maxOvers / numPlayers : (maxOvers / numPlayers + 1))) {
				messageSB.append(errorNumber++);
				messageSB.append(") ");
				messageSB.append(getResources().getString(R.string.NM_notEnoughBowlers));
			}
		}

		if (errorNumber > 1) {
			showMessage(getResources().getString(R.string.NM_oversPlayersValidationTitle), messageSB.toString());
			return false;
		} else {
			return true;
		}
	}

	private void showMessage(String title, String message) {
		if (getFragmentManager() != null) {
			InformationDialog infoDialog = InformationDialog.newInstance(title, message);
			infoDialog.show(getFragmentManager(), "TournamentInfoValidationFailure");
		}
	}

	private void updateNumPlayers() {
		numPlayers = CommonUtils.updateNumPlayers(maxWickets);
		etNumPlayers.setText(String.valueOf(numPlayers));
	}

	private void updateMaxPerBowler() {
		maxPerBowler = CommonUtils.updateMaxPerBowler(maxOvers, numPlayers);
		etMaxPerBowler.setText(String.valueOf(maxPerBowler));
	}

	private void createNewTournament() {
		maxOvers = Integer.parseInt(etMaxOvers.getText().toString());
		maxWickets = Integer.parseInt(etMaxWickets.getText().toString());
		numPlayers = Integer.parseInt(etNumPlayers.getText().toString());
		maxPerBowler = Integer.parseInt(etMaxPerBowler.getText().toString());

		Tournament tournament = new Tournament(etTournamentName.getText().toString(), selTeams,
				maxOvers, maxWickets, numPlayers, maxPerBowler, numGroups, numRounds, type, stageType);

		TournamentDBHandler dbh = new TournamentDBHandler(getContext());

		int id = dbh.createNewTournament(tournament);

		if (id == dbh.CODE_NEW_TOURNAMENT_DUP_RECORD) {
			Toast.makeText(getContext(), getResources().getString(R.string.NM_duplicateTournamentName), Toast.LENGTH_LONG).show();
		} else {
			tournament.setId(id);
			Toast.makeText(getContext(), getResources().getString(R.string.NM_tournamentCreationSuccessful), Toast.LENGTH_SHORT).show();

			if (type == TournamentFormat.GROUPS) {
				showGroupConfirmation(tournament);
			} else {
				TournamentUtils utils = new TournamentUtils(getContext());
				tournament = utils.createInitialGroups(tournament);
				showScheduleConfirmation(tournament);
			}
		}
	}

	private void showGroupConfirmation(Tournament tournament) {
		if (getActivity() != null) {
			FragmentManager fragMgr = getActivity().getSupportFragmentManager();
			String fragmentTag = TournamentGroupsFragment.class.getSimpleName();
			fragMgr.beginTransaction()
					.replace(R.id.frame_container, TournamentGroupsFragment.newInstance(tournament), fragmentTag)
					.commit();
		}
	}

	private void showScheduleConfirmation(Tournament tournament) {
		if (getActivity() != null) {
			FragmentManager fragMgr = getActivity().getSupportFragmentManager();
			String fragmentTag = TournamentScheduleFragment.class.getSimpleName();
			fragMgr.beginTransaction()
					.replace(R.id.frame_container, TournamentScheduleFragment.newInstance(tournament, 0), fragmentTag)
					.commit();
		}
	}
}
