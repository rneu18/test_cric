package com.theNewCone.cricketScoreCard.intf;

public interface ListInteractionListener {
    void onListFragmentInteraction(Object selItem);
    void onListFragmentMultiSelect(Object selItem, boolean removeItem);
}
