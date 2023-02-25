package com.volmit.react.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import primal.lang.collection.GList;

/**
 * Scoreboard & Tab utilities
 *
 * @author cyberpwn
 */
public class SlateUtil {
    /**
     * Get the board manager
     *
     * @return the manager
     */
    public static ScoreboardManager getManager() {
        return Bukkit.getScoreboardManager();
    }

    /**
     * Create a new board
     *
     * @return the board
     */
    public static Scoreboard newBoard() {
        return getManager().getNewScoreboard();
    }

    /**
     * Create a new objective
     *
     * @param board the binding board
     * @param name  the name of the objective
     * @return the objective
     */
    public static Objective newObjective(Scoreboard board, String name) {
        Objective o = board.registerNewObjective("slate", "dummy");

        name = trimString(name);

        o.setDisplayName(name);
        o.setDisplaySlot(DisplaySlot.SIDEBAR);

        return o;
    }

    public static Objective newHeadObjective(Scoreboard board, String name) {
        Objective o = board.registerNewObjective("slate", "dummy");

        name = trimString(name);

        o.setDisplayName(name);
        o.setDisplaySlot(DisplaySlot.BELOW_NAME);

        return o;
    }

    /**
     * Create a new objective
     *
     * @param board the binding board
     * @param name  the name of the objective
     * @return the objective
     */
    public static Objective newTabObjective(Scoreboard board, String name) {
        Objective o = board.registerNewObjective("slate", "dummy");

        name = trimString(name);

        o.setDisplayName(name);
        o.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        return o;
    }

    /**
     * Set the score with text
     *
     * @param name  the name
     * @param value the value
     * @param o     the objective
     */
    public static void setScore(String name, int value, Objective o) {
        name = trimString(name);

        o.getScore(name).setScore(value);
    }

    /**
     * Build a custom slate
     *
     * @param name the name of it
     * @param data the list of data
     * @return the slate
     */
    public static Scoreboard buildSlate(String name, GList<String> data) {
        Scoreboard slate = newBoard();
        Objective o = newObjective(slate, name);

        int ind = data.size();
        for (String i : data) {
            try {
                Team team = slate.registerNewTeam("team" + ind);
                team.addEntry(ChatColor.values()[ind] + "");
                i = trimString(i);
                team.setPrefix(i);
                setScore(ChatColor.values()[ind] + "", ind, o);

                ind--;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        return slate;
    }

    /**
     * Update a slate
     *
     * @param slate the slate to update
     * @param data  the list of data
     * @return the slate
     */
    public static Scoreboard updateSlate(Scoreboard slate, GList<String> data) {
        int ind = data.size();
        for (String i : data) {
            try {
                i = trimString(i);
                slate.getTeam("team" + ind).setPrefix(i);
                ind--;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        return slate;
    }

    /**
     * Build a custom slate
     *
     * @param name the name of it
     * @param data the list of data
     * @return the slate
     */
    public static Scoreboard buildTabSlate(String name, GList<String> data) {
        Scoreboard slate = newBoard();
        Objective o = newTabObjective(slate, name);

        int ind = data.size();

        for (String i : data) {
            i = trimString(i);
            setScore(i, ind, o);

            ind--;
        }

        return slate;
    }

    /**
     * Trim a string to work with older versions
     *
     * @param string The string to trim
     * @return the trimmed string
     */
    public static String trimString(String string) {
        if (Protocol.R1_7_1.to(Protocol.R1_12_2).contains(Protocol.getProtocolVersion())) {
            if (string.length() > 15) {
                string = string.substring(0, 15);
            }
        }

        if (string.length() > 40) {
            string = string.substring(0, 37) + "...";
        }
        return string;
    }

    public static String convertJSON(String noJson) {
        if (noJson == null) {
            return null;
        }

        if (noJson.startsWith("{") && noJson.endsWith("}")) {
            return noJson;
        }

        return String.format("{\"text\":\"%s\"}", noJson);
    }

    public static String[] convertJSON(String... noJson) {
        if (noJson == null) {
            return null;
        }

        String[] converted = new String[noJson.length];

        for (int i = 0; i < noJson.length; i++) {
            if (i != 0 && (!noJson[i].startsWith("{") || !noJson[i].endsWith("}"))) {
                noJson[i] = "\\n" + noJson[i];
            }
            converted[i] = convertJSON(noJson[i]);
        }

        return converted;
    }

    public static void setTabTitle(Player p, String header, String footer) {
        NMSX.sendTabTitle(p, header, footer);
    }
}