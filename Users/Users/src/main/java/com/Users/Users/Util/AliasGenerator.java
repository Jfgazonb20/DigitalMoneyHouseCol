package com.Users.Users.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AliasGenerator {

    private static final String WORDS_FILE_PATH = "C:\\Users\\PIPEPC\\Downloads\\AliasWords.txt";
    private static final List<String> words = new ArrayList<>();
    private static final Random random = new Random();

    static {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(WORDS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
            if (words.isEmpty()) {
                throw new RuntimeException("El archivo de palabras está vacío.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el archivo de palabras: " + e.getMessage(), e);
        }
    }

    public static String generateAlias() {
        return words.get(random.nextInt(words.size())) + "." +
                words.get(random.nextInt(words.size())) + "." +
                words.get(random.nextInt(words.size()));
    }

    public static String generateCvu() {
        StringBuilder cvu = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            cvu.append(random.nextInt(10));
        }
        return cvu.toString();
    }
}
