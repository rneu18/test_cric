package com.theNewCone.cricketScoreCard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.theNewCone.cricketScoreCard.Constants;
import com.theNewCone.cricketScoreCard.R;
import com.theNewCone.cricketScoreCard.enumeration.ExtraType;
import com.theNewCone.cricketScoreCard.match.Team;
import com.theNewCone.cricketScoreCard.utils.CommonUtils;

import java.util.Locale;

public class ExtrasActivity extends Activity
	implements View.OnClickListener{

	public final static String ARG_NB_EXTRA = "NB_Extra";
	public final static String ARG_EXTRA_RUNS = "Extra_Runs";
	public final static String ARG_TEAM = "Team";
	public final static String ARG_BATTING_TEAM = "BattingTeam";
	public final static String ARG_BOWLING_TEAM = "BowlingTeam";

	public final static int RESULT_CODE_OK = 1;
	public final static int RESULT_CODE_CANCEL = -1;

	RadioGroup rgExtraNB, rgExtraRuns;

	ExtraType extraType;
	Intent incomingIntent;
	Team battingTeam, bowlingTeam;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_extras);
		this.setFinishOnTouchOutside(false);

		setupView();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnExtraOK:
				processExtras(RESULT_CODE_OK);
				break;

			case R.id.btnExtraCancel:
				processExtras(RESULT_CODE_CANCEL);
				break;

			case R.id.rbExtraNBNone:
				populateRuns(ExtraType.NONE);
				break;

			case R.id.rbExtraNBBye:
				populateRuns(ExtraType.BYE);
				break;

			case R.id.rbExtraNBLegBye:
				populateRuns(ExtraType.LEG_BYE);
				break;

			case R.id.rbExtraOtherRuns:
				findViewById(R.id.etOtherRuns).setVisibility(View.VISIBLE);
				break;
		}
	}

	private void setupView() {
		TextView tvExtraNB = findViewById(R.id.tvExtraNBText);
		TextView tvExtra = findViewById(R.id.tvExtraText);
		rgExtraNB = findViewById(R.id.rgExtraNB);
		rgExtraRuns = findViewById(R.id.rgExtraRuns);

		Button btnOK = findViewById(R.id.btnExtraOK);
		Button btnCancel = findViewById(R.id.btnExtraCancel);
		btnCancel.setOnClickListener(this);
		btnOK.setOnClickListener(this);

		RadioButton rbExtraNBNone = findViewById(R.id.rbExtraNBNone);
		RadioButton rbExtraNBBye = findViewById(R.id.rbExtraNBBye);
		RadioButton rbExtraNBLegBye = findViewById(R.id.rbExtraNBLegBye);

		rbExtraNBNone.setOnClickListener(this);
		rbExtraNBBye.setOnClickListener(this);
		rbExtraNBLegBye.setOnClickListener(this);

		incomingIntent = getIntent();
		if(incomingIntent != null) {
			extraType = (ExtraType) incomingIntent.getSerializableExtra(Constants.ARG_EXTRA_TYPE);
			battingTeam = incomingIntent.getSerializableExtra(ARG_BATTING_TEAM) != null
					? (Team) incomingIntent.getSerializableExtra(ARG_BATTING_TEAM) : null;
			bowlingTeam = incomingIntent.getSerializableExtra(ARG_BOWLING_TEAM) != null
					? (Team) incomingIntent.getSerializableExtra(ARG_BOWLING_TEAM) : null;
		}

		tvExtra.setText(getExtraText());

		switch (extraType) {
			case NO_BALL:
				rgExtraNB.setVisibility(View.VISIBLE);
				tvExtraNB.setVisibility(View.VISIBLE);
				break;
		}

		populateRuns(ExtraType.NONE);
	}

	private String getExtraText() {
		switch (extraType) {
			case WIDE:
				return getString(R.string.extrasDialogTitleAdditional);
			case NO_BALL:
				return getString(R.string.extrasDialogTitleBatsman);
			case PENALTY:
				return getString(R.string.extraDialogTitlePenalty);
			default:
				return getString(R.string.extrasDialogTitle);
		}
	}

	private void populateRuns(ExtraType extraSubType) {
		int i=0;

		RadioButton rbExtraOtherRuns = findViewById(R.id.rbExtraOtherRuns);
		rgExtraRuns.removeAllViews();

		String[] extraDataArray = CommonUtils.getExtraDetailsArray(extraType, extraSubType);

		if (extraType == ExtraType.PENALTY && battingTeam != null && bowlingTeam != null) {
			extraDataArray = new String[]{battingTeam.getName(), bowlingTeam.getName()};
		}

		for(String extraData : extraDataArray) {
			RadioButton radioButton = new RadioButton(this);
			radioButton.setText(extraData);
			radioButton.setId(View.generateViewId());
			if(i==0) {
				radioButton.setChecked(true);
				radioButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						findViewById(R.id.etOtherRuns).setVisibility(View.GONE);
					}
				});
			} else if (extraData.equalsIgnoreCase("OTHER")) {
				radioButton.setText(extraData);
				radioButton.setId(rbExtraOtherRuns.getId());
				radioButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						findViewById(R.id.etOtherRuns).setVisibility(View.VISIBLE);
					}
				});
			}

			rgExtraRuns.addView(radioButton);
			i++;
		}
	}

	private void processExtras(int resultCode) {
		ExtraType extraNBType = ExtraType.NONE;
		int numRuns = -1;
		String team = Constants.BATTING_TEAM;

		boolean validationOk = true;

		if(resultCode == RESULT_CODE_OK) {
			if (extraType == ExtraType.NO_BALL) {
				int rgExtraNBSelId = rgExtraNB.getCheckedRadioButtonId();
				RadioButton rbExtraNB = findViewById(rgExtraNBSelId);

				if (getString(R.string.extraNBBye).equals(rbExtraNB.getText().toString())) {
					extraNBType = ExtraType.BYE;
				} else if (getString(R.string.extraNBLegBye).equals(rbExtraNB.getText().toString())) {
					extraNBType = ExtraType.LEG_BYE;
				}
			}

			int rgExtraRunsSelId = rgExtraRuns.getCheckedRadioButtonId();
			switch (extraType) {
				case PENALTY:
					team = ((RadioButton) findViewById(rgExtraRunsSelId)).getText().toString();
					if (battingTeam != null && bowlingTeam != null) {
						if (team.equalsIgnoreCase(battingTeam.getName()))
							team = Constants.BATTING_TEAM;
						else if (team.equalsIgnoreCase(bowlingTeam.getName())) {
							team = Constants.BOWLING_TEAM;
						}
					}
					numRuns = 5;
					break;

				default:
					String theRuns = ((RadioButton) findViewById(rgExtraRunsSelId)).getText().toString();
					if (theRuns.equalsIgnoreCase("OTHER")) {
						theRuns = ((EditText) findViewById(R.id.etOtherRuns)).getText().toString();
						if(theRuns.equals("")) {
							Toast.makeText(this, "Please enter the runs", Toast.LENGTH_SHORT).show();
							validationOk = false;
						}
					}
					if(validationOk)
					{
						numRuns = Integer.parseInt(theRuns);
						if(numRuns > Constants.MAX_RUNS_POSSIBLE) {
							Toast.makeText(this,
									String.format(Locale.getDefault(),"Number of Runs limited to %d", Constants.MAX_RUNS_POSSIBLE),
									Toast.LENGTH_SHORT).show();
							validationOk = false;
						} else if (numRuns < 0) {
							Toast.makeText(this, "Number of runs cannot be negative", Toast.LENGTH_SHORT).show();
							validationOk = false;
						}
					}
					break;
			}
		}

		if(validationOk) {
			Intent respIntent = new Intent();
			respIntent.putExtra(Constants.ARG_EXTRA_TYPE, extraType);
			respIntent.putExtra(ARG_NB_EXTRA, extraNBType);
			respIntent.putExtra(ARG_EXTRA_RUNS, numRuns);
			respIntent.putExtra(ARG_TEAM, team);
			setResult(resultCode, respIntent);
			finish();
		}
	}
}
