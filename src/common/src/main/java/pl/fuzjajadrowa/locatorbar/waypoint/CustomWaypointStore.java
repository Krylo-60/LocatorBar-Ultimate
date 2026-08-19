package pl.fuzjajadrowa.locatorbar.waypoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public final class CustomWaypointStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE_PATH = Path.of("config", "locatorbar_custom_waypoints.json");
    private static final List<CustomWaypoint> WAYPOINTS = new CopyOnWriteArrayList<>();
    private static boolean loaded = false;

    public record CustomWaypoint(
            UUID id,
            String name,
            String world,
            double x,
            double y,
            double z,
            int color,
            String icon,
            boolean visible
    ) {}

    private CustomWaypointStore() {
    }

    public static synchronized void load() {
        if (!Files.exists(FILE_PATH)) {
            loaded = true;
            return;
        }

        try (Reader reader = Files.newBufferedReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<CustomWaypoint>>() {}.getType();
            List<CustomWaypoint> loadedList = GSON.fromJson(reader, listType);
            WAYPOINTS.clear();
            if (loadedList != null) {
                WAYPOINTS.addAll(loadedList);
            }
            loaded = true;
        } catch (IOException ignored) {
            loaded = true;
        }
    }

    public static synchronized void save() {
        try {
            if (FILE_PATH.getParent() != null && !Files.exists(FILE_PATH.getParent())) {
                Files.createDirectories(FILE_PATH.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(FILE_PATH)) {
                GSON.toJson(new ArrayList<>(WAYPOINTS), writer);
            }
        } catch (IOException ignored) {
        }
    }

    public static List<CustomWaypoint> getAll() {
        if (!loaded) {
            load();
        }
        return Collections.unmodifiableList(WAYPOINTS);
    }

    public static List<CustomWaypoint> getForWorld(String world) {
        if (!loaded) {
            load();
        }
        List<CustomWaypoint> list = new ArrayList<>();
        for (CustomWaypoint wp : WAYPOINTS) {
            if (wp.world().equals(world) && wp.visible()) {
                list.add(wp);
            }
        }
        return list;
    }

    public static void add(CustomWaypoint waypoint) {
        if (!loaded) {
            load();
        }
        WAYPOINTS.add(waypoint);
        save();
    }

    public static void remove(UUID id) {
        if (!loaded) {
            load();
        }
        WAYPOINTS.removeIf(wp -> wp.id().equals(id));
        save();
    }
}
