package com.itestra.tools;

import javax.ws.rs.Path;
import java.util.EnumSet;


public class GeneratorUtil {

    public static final String N = "\n";
    public static final String T = "\t";
    public static final String TT = T + T;
    public static final String TTT = T + T + T;
    public static final String TTTT = T + T + T + T;
    public static final String TS_FILE_ENDING = ".ts";

    public static final String DEFAULT_PACKAGE_PREFIX = "com.itestra.dart";
    public static final String DEFAULT_DTO_PACKAGE_NAME = "/dto";

    public static String classToDTOName(final Class dto) {
        return dto.getSimpleName();
    }

    private static String dtoToFilePath(final Class dto) {
        final String path = buildPath(dto);
        final String dotName = classToDTOName(dto);
        return path + "/" + dotName + TS_FILE_ENDING;
    }

    private static String dtoToClassName(final Class dto) {
        return classToDTOName(dto);
    }

    private static String serviceToFilename(final Class service) {
        final String path = buildPath(service);
        final String simpleName = serviceToClassName(service);
        final String dotName = camelToDotSnake(simpleName);
        return path + "/" + dotName + TS_FILE_ENDING;
    }

    private static String buildPath(final Class clazz) {
        if (clazz.getPackageName().startsWith(DEFAULT_PACKAGE_PREFIX)) {
            return clazz.getPackageName().replace(DEFAULT_PACKAGE_PREFIX, "").replace(".", "/");
        }
        return DEFAULT_DTO_PACKAGE_NAME;
    }

    public static boolean isCollection(final String jsType) {
        return jsType.matches("^.+\\[]$");
    }

    public static String toFilePath(final Class clazz) {
        return isController(clazz) ? serviceToFilename(clazz) : dtoToFilePath(clazz);
    }

    private static String serviceToClassName(final Class service) {
        return service.getSimpleName().replace("Controller", "Service");
    }

    public static String toClassName(final Class clazz) {
        if (isController(clazz)) {
            return serviceToClassName(clazz);
        }
        return dtoToClassName(clazz);
    }

    private static boolean isController(final Class clazz) {
        return clazz.getAnnotationsByType(Path.class).length > 0;
    }

    public static String camelToDotSnake(String str) {
        // Regular Expression
        final String regex = "([a-z])([A-Z]+)";

        // Replacement string
        final String replacement = "$1.$2";

        // Replace the given regex
        // with replacement string
        // and convert it to lower case.
        str = str
                .replaceAll(
                        regex, replacement)
                .toLowerCase();

        // return string
        return str;
    }

    public static String classToVariableName(final Class<?> type) {
        final char[] chars = type.getSimpleName().toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars).replace("DTO", "");
    }

    public static Class<?> classToParent(final Class<?> dto) {
        if (dto.getSuperclass() == Object.class || dto.getSuperclass() == Enum.class) {
            return null;
        }
        return dto.getSuperclass();
    }

    public static boolean isEnum(final Class<?> type) {
        return type.isEnum() || type.equals(EnumSet.class);
    }

    public static boolean isPrimitive(final String jsType) {
        switch (jsType) {
            case "string":
            case "number":
            case "boolean":
                return true;
            default:
                return false;
        }
    }

    public enum Type {
        PRIMITIVE,
        COLLECTION,
        COMPLEX
    }
}
