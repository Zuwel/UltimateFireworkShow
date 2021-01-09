package com.zuwel.fireworkshowplus.objects;

import com.zuwel.fireworkshowplus.FireworkShow;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Show implements ConfigurationSerializable {

    private String name;
    private String author;
    private ArrayList<Frame> frames = new ArrayList<Frame>();
    private ArrayList<Integer> taskids = new ArrayList<Integer>();
    private boolean running = false;
    private boolean highest = false;

    public Show() {
        this("New Show", null);
    }

    public Show(String name, String author) {
        this.name = name;
        this.author = author;
    }

    public Show(Show show) {
        name = show.name;
        frames = (ArrayList<Frame>) show.frames.clone();
        highest = show.highest;
    }

    public void play() {
        if ( running ) return;
        running = true;

        long current = 0;
        for ( final Frame f : frames ) {
            current += f.getDelay();
            taskids.add(Bukkit.getScheduler().scheduleSyncDelayedTask(FireworkShow.fws, new Runnable() {
                @Override
                public void run() {
                    f.play(highest);
                }
            }, current));
        }

        taskids.add(Bukkit.getScheduler().scheduleSyncDelayedTask(FireworkShow.fws, new Runnable() {
            @Override
            public void run() {
                running = false;
                taskids.clear();
            }
        }, current));
    }

    public void stop() {
        if ( !running ) return;

        for ( int id : taskids ) {
            Bukkit.getScheduler().cancelTask(id);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setFrames(ArrayList<Frame> frames) {
        this.frames = frames;
    }
    public ArrayList<Frame> getFrames() {
        return frames;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setAuthor(String author) { this.author = author; }
    public String getAuthor() { return author; }

    public void setHighest(boolean highest) {
        this.highest = highest;
    }
    public boolean getHighest() {
        return highest;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("highest", highest);
        map.put("frames", frames);

        return map;
    }

    public static Show deserialize(Map<String, Object> args) {
        String name = (String) args.get("name");
        boolean highest = (Boolean) args.get("highest");
        Show show = new Show();
        show.setName(name);
        show.setHighest(highest);
        for ( Frame f : (ArrayList<Frame>) args.get("frames") ) {
            show.frames.add(f);
        }
        return show;
    }
}
