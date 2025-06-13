package com.dementia.neurocraft.dev;

import com.dementia.neurocraft.client.features.ClientFeatureController;
import com.dementia.neurocraft.common.features.Feature;

import com.dementia.neurocraft.server.features.ServerFeatureController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public final class FeatureLangUpdater {
    private static final Path LANG_FILE = Path.of("src/main/resources/assets/neurocraft/lang/en_us.json");
    private static final String PREFIX = "neurocraft.serverConfig.";

    public static void main(String[] args) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Load existing lang file
        File file = LANG_FILE.toFile();
        if (!file.exists()) {
            throw new IllegalStateException("Lang file not found: " + LANG_FILE);
        }

        JsonObject root;
        try (FileReader reader = new FileReader(file)) {
            root = gson.fromJson(reader, JsonObject.class);
        }

        int added = 0;
        for (Feature feature : ClientFeatureController.getFeatures()) {
            String key = PREFIX + feature.getId();
            if (!root.has(key)) {
                root.addProperty(key, feature.getDisplayName());
                added++;
            }
        }
        for (Feature feature : ServerFeatureController.getFeatures()) {
            String key = PREFIX + feature.getId();
            if (!root.has(key)) {
                root.addProperty(key, feature.getDisplayName());
                added++;
            }
        }

        if (added > 0) {
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(root, writer);
            }
            System.out.println("Updated lang file with " + added + " new entries.");
            System.exit(0);
        } else {
            System.out.println("No new entries needed. Lang file is up to date.");
            System.exit(0);
        }
    }
}
