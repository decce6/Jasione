package me.decce.jasione;

import net.minecraft.core.Direction;

import java.time.DayOfWeek;
import java.util.Arrays;

public class TransformationSample {
    private static Object[] objects;

    public static void init() {
    }

    public static String[] read() {
        var dirs = Direction.values(); // Should be cached
        String[] strings = new String[dirs.length];
        for (int i = 0; i < dirs.length; i++) {
            strings[i] = dirs[i].getName();
        }
        return strings;
    }

    public static void iterate() {
        for (Direction direction : Direction.values()) { // Should be cached
            System.out.println(direction);
        }
    }

    public static void multi() {
        Direction.values()[0] = Direction.EAST; // Should not be cached
        Direction.values(); // Should be cached
    }

    public static void stream() {
        Arrays.stream(Direction.values()).forEach(System.out::println); // Should be cached
    }

    public static void save() {
        objects = Direction.values(); // Should not be cached
    }

    public static void mutate() {
        var dirs = Direction.values(); // Should not be cached
        dirs[0] = Direction.EAST;
    }

    public static Direction[] escape() {
        return Direction.values(); // Should not be cached
    }

    public static void sort() {
        Arrays.sort(Direction.values()); // Should not be cached
    }

    public static void lvtAliasing() {
        Object[] direction;
        if (Math.random() > 0.42d) {
            direction = Direction.values(); // Should be cached
        }
        else {
            direction = DayOfWeek.values(); // Should be cached
        }
        System.out.println(direction[0]);
    }

    public static void lvtAliasing2() {
        Object[] direction;
        if (Math.random() > 0.42d) {
            direction = Direction.values(); // Should not be cached
        }
        else {
            direction = DayOfWeek.values(); // Should not be cached
        }
        direction[0] = null;
    }
}
