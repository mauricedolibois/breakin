package com.innoveworkshop.gametest.levels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {
    private List<String[]> levels;

    public LevelLoader(InputStream levelFile) throws IOException {
        levels = new ArrayList<>();
        loadLevels(levelFile);
    }

    private void loadLevels(InputStream levelFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(levelFile))) {
            List<String> currentLevel = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    if (!currentLevel.isEmpty()) {
                        levels.add(currentLevel.toArray(new String[0]));
                        currentLevel.clear();
                    }
                } else if (!line.isEmpty()) {
                    currentLevel.add(line);
                }
            }
            if (!currentLevel.isEmpty()) {
                levels.add(currentLevel.toArray(new String[0]));
            }
        }
    }

    public String[] getLevel(int index) {
        if (index < 0 || index >= levels.size()) {
            throw new IndexOutOfBoundsException("Level index out of bounds");
        }
        return levels.get(index);
    }

    public int getLevelCount() {
        return levels.size();
    }
}
