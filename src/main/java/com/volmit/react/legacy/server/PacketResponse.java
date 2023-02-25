package com.volmit.react.legacy.server;

import org.bukkit.Bukkit;
import primal.json.JSONArray;
import primal.json.JSONObject;
import primal.lang.collection.GList;

public class PacketResponse {
    private final JSONObject js;

    public PacketResponse() {
        this.js = new JSONObject();
    }

    public void put(String s, PacketResponseType o) {
        js.put(s, o.toString());
    }

    public void put(String s, Double o) {
        if (Double.isFinite(o)) {
            js.put(s, o);
        } else {
            Bukkit.broadcastMessage(s + o);
        }
    }

    public void put(String s, Integer o) {
        js.put(s, o);
    }

    public void put(String s, Long o) {
        js.put(s, o);
    }

    public void put(String s, String o) {
        js.put(s, o);
    }

    public void put(String s, GList<String> o) {
        JSONArray jsa = new JSONArray();

        for (String i : o) {
            jsa.put(i);
        }

        js.put(s, jsa);
    }

    public String getString(String s) {
        return js.getString(s);
    }

    public Double getDouble(String s) {
        return js.getDouble(s);
    }

    public Integer getInt(String s) {
        return js.getInt(s);
    }

    public Long getLong(String s) {
        return js.getLong(s);
    }

    public Boolean getBoolean(String s) {
        return js.getBoolean(s);
    }

    @Override
    public String toString() {
        return js.toString();
    }

    public GList<String> getStringList(String s) {
        GList<String> list = new GList<>();

        for (Object i : js.getJSONArray(s)) {
            list.add(i.toString());
        }

        return list;
    }
}
