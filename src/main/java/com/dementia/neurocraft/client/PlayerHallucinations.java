package com.dementia.neurocraft.client;


import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerHallucinations {
    public static final List<String> names;

    private static final int SAMPLE_SIZE = 100;

    static {
        List<String> names1;
        try {
            names1 = sampleNames();
        } catch (IOException e) {
            names1 = new ArrayList<>(SAMPLE_SIZE);
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                names1.add(randomName());
            }
        }
        names = names1;
    }

    public static String getRandomName() {
        return names.get(new Random().nextInt(SAMPLE_SIZE));
    }

    private static List<String> sampleNames() throws IOException {
        List<String> reservoir = new ArrayList<>(SAMPLE_SIZE);
        ResourceLocation loc = new ResourceLocation("neurocraft", "players/names.txt");
        var resource = Minecraft.getInstance().getResourceManager().getResource(loc).orElseThrow();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open()))) {
            String line;
            int count = 0;
            Random rand = new Random();

            while ((line = reader.readLine()) != null) {
                for (String name : line.split(" ")) {
                    if (name.isBlank()) continue;

                    count++;
                    if (reservoir.size() < SAMPLE_SIZE) {
                        reservoir.add(name);
                    } else {
                        int j = rand.nextInt(count);
                        if (j < SAMPLE_SIZE) {
                            reservoir.set(j, name);
                        }
                    }
                }
            }
        }
        return reservoir;
    }


    private static String randomName() {
        String[] syll = {"ba", "be", "bi", "bo", "bu", "ka", "ke", "ki", "ko", "ku", "ra", "re", "ri", "ro", "ru",
                "ta", "te", "ti", "to", "tu", "za", "ze", "zi", "zo", "zu"};
        java.util.Random r = java.util.concurrent.ThreadLocalRandom.current();
        int len = 3 + r.nextInt(3);            // 3–5 syllables
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(syll[r.nextInt(syll.length)]);
        return sb.substring(0, Math.min(16, sb.length()));
    }
}
