package com.volmit.react.controller;

import com.volmit.react.Config;
import com.volmit.react.Surge;
import com.volmit.react.util.Controller;
import com.volmit.react.util.TickListSplitter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import primal.json.JSONObject;
import primal.lang.collection.GMap;

public class TickListController extends Controller {
    private GMap<World, TickListSplitter> splitter;

    @Override
    public void dump(JSONObject object) {

    }

    @EventHandler
    public void on(WorldLoadEvent e) {
        if (!Config.ENABLED_SPLITTER || Config.SAFE_MODE_NMS) {
            return;
        }

        splitter.put(e.getWorld(), new TickListSplitter(e.getWorld()));
    }

    @EventHandler
    public void on(WorldUnloadEvent e) {
        if (!Config.ENABLED_SPLITTER || Config.SAFE_MODE_NMS) {
            splitter.clear();
            return;
        }

        try {
            splitter.get(e.getWorld()).dumpAll();
            splitter.remove(e.getWorld());
        } catch (Throwable ex) {

        }
    }

    @Override
    public void start() {
        splitter = new GMap<>();
        if (!Config.ENABLED_SPLITTER || Config.SAFE_MODE_NMS) {
            return;
        }
        Surge.register(this);


        for (World i : Bukkit.getWorlds()) {
            splitter.put(i, new TickListSplitter(i));
        }
    }

    @EventHandler
    public void on(BlockBreakEvent e) {
        if (!Config.ENABLED_SPLITTER || Config.SAFE_MODE_NMS) {
            return;
        }

        checkReg(e.getBlock().getWorld());
    }

    public void checkReg(World world) {
        if (!Config.ENABLED_SPLITTER || Config.SAFE_MODE_NMS) {
            return;
        }
        if (splitter.containsKey(world)) {
            return;
        }

        splitter.put(world, new TickListSplitter(world));
    }

    @Override
    public void stop() {
        if (!Config.ENABLED_SPLITTER || Config.SAFE_MODE_NMS) {
            return;
        }
        Surge.unregister(this);

        if (!Config.ENABLED_SPLITTER || Config.SAFE_MODE_NMS) {
            return;
        }

        for (World i : Bukkit.getWorlds()) {
            try {
                splitter.get(i).dumpAll();
                splitter.remove(i);
            } catch (Throwable ex) {

            }
        }
    }

    @Override
    public void tick() {
        if (!Config.ENABLED_SPLITTER || Config.SAFE_MODE_NMS) {
            return;
        }

        for (World i : splitter.k()) {
            splitter.get(i).tick();
        }
    }

    @Override
    public int getInterval() {
        return 0;
    }

    @Override
    public boolean isUrgent() {
        return true;
    }

    public boolean throttle(Chunk chunk, int tickDelay, long time) {
        if (splitter.containsKey(chunk.getWorld())) {
            return splitter.get(chunk.getWorld()).throttle(chunk, tickDelay, time);
        }

        return false;
    }
}
