package com.volmit.react.api;

import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

public interface IRenderer
{
	void draw(BufferedFrame frame, MapCanvas c, MapView v);
}
