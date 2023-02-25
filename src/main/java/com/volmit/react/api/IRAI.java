package com.volmit.react.api;

import com.volmit.react.xrai.GoalManager;

import primal.lang.collection.GList;

public interface IRAI
{
	public GoalManager getGoalManager();

	public void tick();

	public GList<RAIEvent> getEvents();

	public void callEvent(RAIEvent e);

	public GList<IActionSource> getListeners();
}
