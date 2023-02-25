package com.volmit.react.util;

import org.json.JSONObject;

public interface IController
{
	public void start();

	public void stop();

	public void tick();

	public int getInterval();

	public boolean isUrgent();

	public void setTime(double ms);

	public double getTime();

	public void dump(JSONObject object);
}
