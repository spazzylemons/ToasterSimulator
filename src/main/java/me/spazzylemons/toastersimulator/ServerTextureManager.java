package me.spazzylemons.toastersimulator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ServerTextureManager {
    private static final Map<UUID, byte[]> textures = new HashMap<>();

    private ServerTextureManager() {}

    public static void put(UUID playerId, byte[] texture) {
        textures.put(playerId, texture);
    }

    public static void remove(UUID playerId) {
        textures.remove(playerId);
    }

    public static Set<Map.Entry<UUID, byte[]>> entrySet() {
        return textures.entrySet();
    }

    public static void clear() {
        textures.clear();
    }
}
