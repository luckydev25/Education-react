package com.volmit.react.controller;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.volmit.react.Surge;
import com.volmit.react.util.Controller;
import com.volmit.react.util.JSONObject;
import com.volmit.react.util.nmp.TickListSplitter;
import com.volmit.volume.lang.collections.GMap;

public class TickListController extends Controller
{
	private GMap<World, TickListSplitter> splitter;

	@Override
	public void dump(JSONObject object)
	{

	}

	@EventHandler
	public void on(WorldLoadEvent e)
	{
		splitter.put(e.getWorld(), new TickListSplitter(e.getWorld()));
	}

	@EventHandler
	public void on(WorldUnloadEvent e)
	{
		try
		{
			splitter.get(e.getWorld()).dumpAll();
			splitter.remove(e.getWorld());
		}

		catch(Throwable ex)
		{

		}
	}

	@Override
	public void start()
	{
		splitter = new GMap<>();
		Surge.register(this);

		for(World i : Bukkit.getWorlds())
		{
			try
			{
				splitter.get(i).dumpAll();
				splitter.remove(i);
			}

			catch(Throwable ex)
			{

			}
		}
	}

	@Override
	public void stop()
	{
		Surge.unregister(this);

		for(World i : Bukkit.getWorlds())
		{
			splitter.put(i, new TickListSplitter(i));
		}
	}

	@Override
	public void tick()
	{
		for(World i : splitter.k())
		{
			splitter.get(i).tick();
		}
	}

	@Override
	public int getInterval()
	{
		return 0;
	}

	@Override
	public boolean isUrgent()
	{
		return true;
	}
}
