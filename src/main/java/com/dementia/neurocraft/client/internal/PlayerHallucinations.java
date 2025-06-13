package com.dementia.neurocraft.client.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static com.dementia.neurocraft.Neurocraft.LOGGER;

@Mod.EventBusSubscriber(modid = "neurocraft", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlayerHallucinations {
    private static final int SAMPLE_SIZE = 5;
    private static final Random RNG = new Random();

    private static List<String> NAMES = null;
    private static boolean started = false;
    private static final Map<String, PlayerSkin> SKINS = new ConcurrentHashMap<>();
    private static final Set<String> DYNAMIC_NAMES = ConcurrentHashMap.newKeySet();

    public static String getRandomName() {
        int base = NAMES == null ? 0 : NAMES.size();
        int extra = DYNAMIC_NAMES.size();
        if (base + extra == 0) return "Steve";

        int idx = RNG.nextInt(base + extra);
        return idx < base ? NAMES.get(idx) : DYNAMIC_NAMES.stream().skip(idx - base).findFirst().orElse("Steve");
    }

    public static PlayerSkin getSkin(String name) {
        return SKINS.getOrDefault(name, DefaultPlayerSkin.get(UUID.nameUUIDFromBytes(name.getBytes())));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent ev) {
        if (ev.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();

        if (!started && mc.player != null) {
            started = true;
            CompletableFuture.runAsync(() -> {
                NAMES = loadNames();
                LOGGER.info("[SkinFetch] Loaded {} static names", NAMES.size());
                downloadSkins(NAMES);
            }, Util.ioPool());
        }

        if (!started || mc.getConnection() == null) return;

        mc.getConnection().getOnlinePlayers().forEach(info -> {
            String name = info.getProfile().getName();
            if (DYNAMIC_NAMES.add(name)) {
                LOGGER.info("[SkinFetch] Detected live player '{}', fetching skin asynchronously", name);
                CompletableFuture.runAsync(() -> downloadSkins(List.of(name)), Util.ioPool());
            }
        });
    }


    private static void downloadSkins(List<String> names) {
        SkinManager sm = Minecraft.getInstance().getSkinManager();

        for (String name : names) {
            CompletableFuture.supplyAsync(() -> buildProfile(name), Util.ioPool()).thenCompose(profileOpt -> profileOpt.map(sm::getOrLoad).orElseGet(() -> CompletableFuture.completedFuture(null))).thenAcceptAsync(ps -> {
                if (ps != null && ps.texture() != null) {
                    SKINS.put(name, ps);
                    LOGGER.info("[SkinFetch] Skin OK for {} → {}", name, ps.texture());
                } else {
                    LOGGER.warn("[SkinFetch] Custom skin missing for {}; defaulting", name);
                    SKINS.put(name, DefaultPlayerSkin.get(UUID.nameUUIDFromBytes(name.getBytes())));
                }
            }, Minecraft.getInstance());
        }
    }

    private static Optional<GameProfile> buildProfile(String name) {
        Optional<UUID> uuidOpt = fetchUuid(name);
        if (uuidOpt.isEmpty()) return Optional.empty();

        UUID uuid = uuidOpt.get();
        Optional<Property> textures = fetchTextures(uuid);
        if (textures.isEmpty()) {
            LOGGER.warn("[SkinFetch] No textures property for {}", name);
            return Optional.of(new GameProfile(uuid, name));
        }

        GameProfile gp = new GameProfile(uuid, name);
        gp.getProperties().put("textures", textures.get());
        return Optional.of(gp);
    }

    private static Optional<UUID> fetchUuid(String name) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.minecraftservices.com/minecraft/profile/lookup/name/" + name).openConnection();
            vanillaHeaders(con);
            int code = con.getResponseCode();

            if (code == 204) {
                LOGGER.info("[SkinFetch] {} → free username (204)", name);
                return Optional.empty();
            }
            if (code != 200) {
                LOGGER.warn("[SkinFetch] UUID call for {} → HTTP {}", name, code);
                return Optional.empty();
            }

            JsonObject jo = JsonParser.parseReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
            String dashed = jo.get("id").getAsString().replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
            return Optional.of(UUID.fromString(dashed));

        } catch (Exception ex) {
            LOGGER.error("[SkinFetch] UUID fetch failed for {}: {}", name, ex.toString());
            return Optional.empty();
        }
    }

    private static Optional<Property> fetchTextures(UUID uuid) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false").openConnection();
            vanillaHeaders(con);
            int code = con.getResponseCode();
            if (code != 200) {
                LOGGER.warn("[SkinFetch] Textures call for {} → HTTP {}", uuid, code);
                return Optional.empty();
            }

            JsonObject jo = JsonParser.parseReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray arr = jo.getAsJsonArray("properties");
            if (arr == null || arr.size() == 0) return Optional.empty();

            JsonObject texObj = arr.get(0).getAsJsonObject();
            String val = texObj.get("value").getAsString();
            String sig = texObj.has("signature") ? texObj.get("signature").getAsString() : null;
            return Optional.of(new Property("textures", val, sig));
        } catch (Exception ex) {
            LOGGER.error("[SkinFetch] Textures fetch failed for {}: {}", uuid, ex.toString());
            return Optional.empty();
        }
    }

    private static void vanillaHeaders(HttpURLConnection con) {
        con.setConnectTimeout(200);
        con.setReadTimeout(200);
        con.setRequestProperty("User-Agent", "Minecraft Java/" + SharedConstants.getCurrentVersion().getName());
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Connection", "close");
        con.setRequestProperty("Host", con.getURL().getHost());
    }

    private static List<String> loadNames() {
        List<String> out = new ArrayList<>(SAMPLE_SIZE);
        var loc = new ResourceLocation("neurocraft", "players/names.txt");

        try {
            var res = Minecraft.getInstance().getResourceManager().getResource(loc);
            if (res.isPresent()) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(res.get().open()))) {
                    int seen = 0;
                    String ln;
                    while ((ln = br.readLine()) != null) {
                        for (String n : ln.trim().split("\\s+")) {
                            if (!isValidUsername(n)) continue;
                            seen++;
                            if (out.size() < SAMPLE_SIZE) out.add(n);
                            else if (RNG.nextInt(seen) < SAMPLE_SIZE) out.set(RNG.nextInt(SAMPLE_SIZE), n);
                        }
                    }
                }
            } else {
                LOGGER.warn("[SkinFetch] names.txt not found; randomising");
            }
        } catch (Exception e) {
            LOGGER.error("[SkinFetch] Error reading names.txt: {}", e.toString());
        }

        while (out.size() < SAMPLE_SIZE) out.add(randomName());
        return out;
    }

    private static boolean isValidUsername(String s) {
        return s.length() >= 3 && s.length() <= 16 && s.matches("^[A-Za-z0-9_]+$");
    }

    private static String randomName() {
        String[] syl = {"ba", "be", "bi", "bo", "bu", "ka", "ke", "ki", "ko", "ku", "ra", "re", "ri", "ro", "ru", "ta", "te", "ti", "to", "tu", "za", "ze", "zi", "zo", "zu"};
        int len = 3 + RNG.nextInt(3);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(syl[RNG.nextInt(syl.length)]);
        return sb.substring(0, Math.min(16, sb.length()));
    }
}
