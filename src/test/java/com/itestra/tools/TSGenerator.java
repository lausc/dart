package com.itestra.tools;

import com.google.common.collect.LinkedHashMultimap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TSGenerator {
    private static final String APP_PATH = "/src/service";

    private static final Logger log = LogManager.getLogger();

    private LinkedHashMultimap<Class<?>, Method> servicesWithMethods = LinkedHashMultimap.create();

    public void generateForMethod(final Class<?> clazz, final Method method) {
        servicesWithMethods.put(clazz, method);
    }

    public void writeFiles(final String path) throws InvocationTargetException, IllegalAccessException, IOException {
        log.info("Generating TS Files for Services: {}", servicesWithMethods.keySet().stream().map(Class::getSimpleName).collect(Collectors.joining(", ")));

        final DTOGenerator dtoGenerator = new DTOGenerator();
        final Map<String, String> fileNameToContent = new ServiceGenerator(dtoGenerator).generateServices(servicesWithMethods);
        fileNameToContent.putAll(dtoGenerator.generateDTOs());

        final File folder = new File(path + APP_PATH);
        if (!folder.exists()) {
            throw new RuntimeException("Pfad nicht gefunden: " + path + APP_PATH);
        }
        for (final Map.Entry<String, String> entry : fileNameToContent.entrySet()) {
            final String fileName = entry.getKey();
            final String content = entry.getValue();

            final String filePath = folder.getAbsolutePath() + "/" + fileName;
            final List<File> existingFiles = find(folder.getAbsolutePath(), fileName);
            for (final File existingFile : existingFiles) {
                existingFile.delete();
                log.info("File gelöscht: {}", existingFile);
            }
            final File file = new File(filePath);
            if (file.exists()) {
                log.info("File gelöscht: {}", file);
                file.delete();
            }
            file.getParentFile().mkdirs();
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(content);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            log.info("File erfolgreich geschrieben: {} ", file);

        }
    }


    private List<File> find(final String searchDirectory, final String fileName) throws IOException {
        try (final Stream<Path> files = Files.walk(Paths.get(searchDirectory))) {
            return files
                    .filter(f -> f.getFileName().toString().equals(fileName))
                    .map(path -> path.toAbsolutePath().toString())
                    .map(path -> new File(path))
                    .collect(Collectors.toList());

        }
    }

}
