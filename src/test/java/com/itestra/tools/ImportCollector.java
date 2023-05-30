package com.itestra.tools;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;

import static com.itestra.tools.GeneratorUtil.N;

public class ImportCollector {
    private Set<Class<?>> imports = new HashSet<>();
    private HashMultimap<String, String> fileToClasses = HashMultimap.create();
    private HashMultimap<String, String> absoluteFileToClasses = HashMultimap.create();

    public void addImport(final Class<?> c) {
        imports.add(c);
    }

    public void addImport(final String fromFile, final String... clazz) {
        fileToClasses.putAll(fromFile, Arrays.asList(clazz));
    }

    public void removeImport(final Class<?> c) {
        imports.remove(c);
    }

    public void addAbsolutImport(final String fromFile, final String... clazz) {
        absoluteFileToClasses.putAll(fromFile, Arrays.asList(clazz));
    }

    public String generateImport(final Class<?> ausgangsKlasse) {

        final StringBuilder builder = new StringBuilder();
        for (final Class<?> anImport : imports) {
            final String className = GeneratorUtil.classToDTOName(anImport);
            final String filePath = GeneratorUtil.toFilePath(anImport);
            fileToClasses.put(filePath, className);
        }
        final String ausgangsPfad = GeneratorUtil.toFilePath(ausgangsKlasse);

        Set<Map.Entry<String, Collection<String>>> imports = fileToClasses.asMap().entrySet();
        final HashMap<String, String> pathsToImport = new HashMap<>();
        for (final Map.Entry<String, Collection<String>> fileNameToImports : imports) {
            final String importNames = fileNameToImports.getValue().stream().sorted().collect(Collectors.joining(", "));
            final String filePath = fileNameToImports.getKey();
            final String relativeFilePath = computeRelativePath(ausgangsPfad, filePath);
            pathsToImport.put(relativeFilePath, importNames);
        }

        imports = absoluteFileToClasses.asMap().entrySet();
        for (final Map.Entry<String, Collection<String>> fileNameToImports : imports) {
            final String importNames = fileNameToImports.getValue().stream().sorted().collect(Collectors.joining(", "));
            final String absoluteFilePath = fileNameToImports.getKey();
            pathsToImport.put(absoluteFilePath, importNames);
        }

        // sort
        for (final Map.Entry<String, String> pathToImport : pathsToImport.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList())) {
            builder.append("import { ").append(pathToImport.getValue()).append(" } from \"").append(pathToImport.getKey()).append("\";");
            builder.append(N);
        }
        builder.append(N);
        return builder.toString();
    }

    private String computeRelativePath(final String sourcePath, final String targetPath) {
        if (targetPath.startsWith("@")) {
            return targetPath;
        }
        if (!targetPath.contains("/")) {
            return targetPath;
        }
        final String[] sourceSplit = sourcePath.split("/");
        final String[] targetSplit = targetPath.split("/");

        for (int i = 0; i < sourceSplit.length; i++) {
            final String s = sourceSplit[i];
            if (targetSplit.length > i) {
                final String t = targetSplit[i];
                if (s.equals(t)) {
                    continue;
                }
            }
            final StringBuilder builder = new StringBuilder();
            if (i < sourceSplit.length - 1) {
                builder.append("../".repeat(Math.max(0, sourceSplit.length - 1 - i)));
            } else {
                builder.append("./");
            }
            for (int j = i; j < targetSplit.length - 1; j++) {
                builder.append(targetSplit[j]).append('/');
            }
            return builder.append(targetSplit[targetSplit.length - 1].replace(GeneratorUtil.TS_FILE_ENDING, "")).toString();
        }


        return "./" + targetSplit[targetSplit.length - 1].replace(GeneratorUtil.TS_FILE_ENDING, "");
    }
}
