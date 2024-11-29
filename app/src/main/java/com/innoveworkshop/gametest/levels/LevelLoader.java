package com.innoveworkshop.gametest.levels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelLoader {
    private List<String[]> levels;
    private List<Map<String, Integer>> levelMetadata;

    public LevelLoader(InputStream levelFile) throws IOException {
        levels = new ArrayList<>();
        levelMetadata = new ArrayList<>();
        loadLevels(levelFile);
    }

    private void loadLevels(InputStream levelFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(levelFile))) {
            List<String> currentLevel = new ArrayList<>();
            Map<String, Integer> metadata = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    if (!currentLevel.isEmpty()) {
                        levels.add(currentLevel.toArray(new String[0]));
                        levelMetadata.add(new HashMap<>(metadata));
                        currentLevel.clear();
                        metadata.clear();
                    }
                } else if (line.contains("=")) {
                    String[] parts = line.split("=");
                    metadata.put(parts[0], Integer.parseInt(parts[1]));
                } else if (!line.isEmpty()) {
                    if (line.matches("^/+$")) { // Check if line contains only '/' characters
                        int emptyRows = line.length();
                        for (int i = 0; i < emptyRows; i++) {
                            currentLevel.add("0-0-0-0-0"); // Replace with an empty row (adjust the default row format as needed)
                        }
                    } else {
                        currentLevel.add(line);
                    }
                }
            }
            if (!currentLevel.isEmpty()) {
                levels.add(currentLevel.toArray(new String[0]));
                levelMetadata.add(metadata);
            }
        }
    }

    public String[] getLevel(int index) {
        if (index < 0 || index >= levels.size()) {
            throw new IndexOutOfBoundsException("Level index out of bounds");
        }
        return levels.get(index);
    }

    public Map<String, Integer> getMetadata(int index) {
        if (index < 0 || index >= levelMetadata.size()) {
            throw new IndexOutOfBoundsException("Metadata index out of bounds");
        }
        return levelMetadata.get(index);
    }

    public int getLevelCount() {
        return levels.size();
    }
}
