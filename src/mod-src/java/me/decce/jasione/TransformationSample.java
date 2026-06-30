package me.decce.jasione;

import me.decce.transformingbase.core.Jasione;
import net.minecraft.core.Direction;

import java.time.DayOfWeek;
import java.util.Arrays;

public class TransformationSample {
    private static Object[] objects;

    public static void init() {
        assertEquality();
        assertMutateSafety();
    }

    public static void assertEquality() {
        var dir1 = Direction.values();
        var dir2 = Direction.values();
        if (dir1 != dir2) {
            Jasione.LOGGER.error("Assertion dir1==dir2 failed, Jasione is not working properly");
        }
    }

    public static void assertMutateSafety() {
        var dir1 = Direction.values();
        var dir2 = Direction.values();
        dir1[0] = Direction.EAST;
        if (dir2[0] == Direction.EAST) {
            Jasione.LOGGER.error("Assertion dir2[0]!=EAST failed, Jasione is not working properly");
        }
        if (dir1 == dir2) {
            Jasione.LOGGER.error("Assertion dir1!=dir2 failed, Jasione is not working properly");
        }
    }

    // The methods below are not run or asserted, but can be inspected manually by settings dumpClasses=true in the config
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
