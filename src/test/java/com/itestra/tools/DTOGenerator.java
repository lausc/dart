package com.itestra.tools;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itestra.domain.AbstractDomainModel;

import java.lang.reflect.Type;
import java.lang.reflect.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.itestra.tools.GeneratorUtil.*;


public class DTOGenerator {
    private static final Method getEnumName;

    static {
        try {
            getEnumName = Enum.class.getMethod("name");
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Set<Class> toProcess = new HashSet<>();
    private final Set<Class> alreadyProcessed = new HashSet<>();

    public DTOGenerator() {
    }

    private static String instanceCloneByConstructor(final Class<?> type, final String jsType, final String cloneSource) {
        if (type.isEnum() || type.equals(EnumSet.class)) {
            return cloneSource;
        } else if (GeneratorUtil.isPrimitive(jsType)) {
            return cloneSource;
        } else if ("BigInt".equals(jsType)) {
            return cloneSource;
        } else if ("DateTime".equals(jsType)) {
            return cloneSource;
        } else {
            return "new " + jsType + "(" + cloneSource + ")";
        }
    }

    private static String instanceCloneFromAny(final Class<?> type, final String jsType, final String cloneSource, final ImportCollector importCollector) {
        if (type.isEnum() || type.equals(EnumSet.class)) {
            return jsType + "[" + cloneSource + " as keyof typeof " + jsType + "]";
        } else if (GeneratorUtil.isPrimitive(jsType)) {
            return cloneSource;
        } else if ("BigInt".equals(jsType)) {
            return "BigInt(" + cloneSource + ".toString())";
        } else if ("DateTime".equals(jsType)) {
            importCollector.addImport("shared/date-time-parser/date-time-parser.ts", "toDateTime");
            return "toDateTime(" + cloneSource + ")";
        } else {
            return "new " + jsType + "(" + cloneSource + ")";
        }
    }

    public void addDTOs(final Collection<Class> dtosToGenerate) {
        toProcess.addAll(dtosToGenerate);
    }

    public Map<String, String> generateDTOs() throws InvocationTargetException, IllegalAccessException {
        final HashMap<String, String> fileNameToContent = new HashMap<>();

        while (!toProcess.isEmpty()) {
            final Class dto = toProcess.stream().findFirst().get();
            toProcess.remove(dto);
            alreadyProcessed.add(dto);

            if (preventGeneration(dto)) {
                continue;
            }
            validateDTO(dto);
            final String filePath = GeneratorUtil.toFilePath(dto);
            if (dto.isEnum()) {
                fileNameToContent.put(filePath, createEnumContent(dto));
            } else {
                fileNameToContent.put(filePath, createDTOContent(dto));
            }
        }

        return fileNameToContent;
    }

    private void validateDTO(final Class dto) {
        for (final Field field : dto.getDeclaredFields()) {
            if (BigInteger.class.equals(field.getType()) && (field.getAnnotation(JsonSerialize.class) == null || field.getAnnotation(JsonDeserialize.class) == null)) {
                final String error = "BigInteger ohne Converter Annotation gefunden: " + field.getDeclaringClass().getSimpleName() + "#" + field.getName() + N
                        + "Bitte folgende Annotationen hinzufügen:" + N
                        + "@JsonSerialize(converter = BigIntConverter.class)" + N
                        + "@JsonDeserialize(converter = BigIntDeserializer.class)" + N + N
                        + "Ansonsten wird die Zahl auf TS Seite in eine number umgwandelt und verliert ggf. präzision";
                throw new RuntimeException(error);
            }
        }
    }

    private boolean preventGeneration(final Class dto) {
        return Number.class.isAssignableFrom(dto) || dto.isPrimitive() || String.class.isAssignableFrom(dto);
    }

    private String createEnumContent(final Class<?> dto) throws InvocationTargetException, IllegalAccessException {
        final StringBuilder builder = new StringBuilder();
        builder.append("export enum ").append(GeneratorUtil.classToDTOName(dto)).append("{").append(N);
        for (final Object field : dto.getEnumConstants()) {
            // exception: don't add "KEIN_MAHNSTOPP" to "MahnKennzeichenEnum" because the frontend will treat this value as null.
            if (getValueForEnumValue(field).equals("KEIN_MAHNSTOPP") && GeneratorUtil.classToDTOName(dto).equals("MahnKennzeichenEnum")) {
                continue;
            }
            builder.append("\t").append(getEnumName.invoke(field))
                    .append("=\"")
                    .append(getValueForEnumValue(field))
                    .append("\"")
                    .append(",")
                    .append(N);
        }
        builder.append("}");

        return builder.toString();
    }

    private String getValueForEnumValue(final Object value) throws InvocationTargetException, IllegalAccessException {
        return getEnumName.invoke(value).toString();
    }

    private String createDTOContent(final Class<?> dto) {
        try {
            final StringBuilder to = new StringBuilder();

            final ImportCollector importCollector = new ImportCollector();
            importCollector.addImport("shared/types.ts", "Any");

            to.append("export class ").append(GeneratorUtil.classToDTOName(dto));
            final Class<?> parentClass = GeneratorUtil.classToParent(dto);
            if (parentClass != null) {
                to.append(" extends ").append(GeneratorUtil.classToDTOName(parentClass));
                importCollector.addImport(parentClass);
            }

            to.append(" {").append(N);

            generateFields(to, dto, importCollector);
            to.append(N);
            generateConstructor(to, dto, importCollector);

            to.append("}").append(N).append(N);

            importCollector.removeImport(dto);

            return importCollector.generateImport(dto) + to;
        } catch (final RuntimeException e) {
            throw new RuntimeException("An error occurred while generating dto " + dto.getName(), e);
        }
    }

    private void generateFields(final StringBuilder to, final Class<?> dto, final ImportCollector importCollector) {
        for (final Field field : dto.getDeclaredFields()) {
            try {
                generateField(to, importCollector, field);
            } catch (final RuntimeException e) {
                throw new RuntimeException("An error occured while generating field " + field.getName(), e);
            }
        }
    }

    private void generateField(final StringBuilder to, final ImportCollector importCollector, final Field field) {
        if ("serialVersionUID".equals(field.getName()) || Modifier.isStatic(field.getModifiers())) {
            return;
        }

        to.append(T).append(field.getName()).append(": Partial<").append(getJSType(field.getType(), field, importCollector)).append(">");
        to.append(";").append(N);
    }

    private void generateConstructor(final StringBuilder to, final Class<?> dto, final ImportCollector importCollector) {
        final String dtoName = GeneratorUtil.classToDTOName(dto);
        to.append(T).append("constructor(init: Any<").append(dtoName).append(">) {");
        to.append(N);
        if (GeneratorUtil.classToParent(dto) != null) {
            to.append(TT).append("super(init);").append(N);
        }
        final StringBuilder forSameObject = new StringBuilder();
        createForFields(forSameObject, dto, importCollector, this::initFieldFromSameObject);
        final StringBuilder forAnyObject = new StringBuilder();
        createForFields(forAnyObject, dto, importCollector, this::initFieldFromAny);

        if (forSameObject.toString().equals(forAnyObject.toString())) {
            to.append(forSameObject);
        } else {
            to.append(TT).append("if (init instanceof ").append(dtoName).append(") {").append(N);
            to.append(forSameObject.toString().replace(TT, TTT));

            to.append(TT).append("} else {").append(N);
            to.append(forAnyObject.toString().replace(TT, TTT));

            to.append(TT).append("}").append(N);
        }
        to.append(T).append("}").append(N);
    }

    private void createForFields(final StringBuilder to, final Class<?> dto, final ImportCollector importCollector,
                                 final TriConsumer<StringBuilder, ImportCollector, Field> fieldGeneration) {
        for (final Field field : dto.getDeclaredFields()) {
            if ("serialVersionUID".equals(field.getName()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            fieldGeneration.accept(to, importCollector, field);
        }
    }

    private void initFieldFromSameObject(final StringBuilder to, final ImportCollector importCollector, final Field field) {
        to.append(TT).append("this.").append(field.getName()).append(" = ");
        getInstanceCopy(to, field.getType(), field);
        to.append(";").append(N);
    }

    private void initFieldFromAny(final StringBuilder to, final ImportCollector importCollector, final Field field) {

        to.append(TT).append("this.").append(field.getName()).append(" = ");

        getInstanceCreation(to, field.getType(), field, importCollector);

        to.append(";").append(N);
    }

    public String getJSType(final Class<?> type, final Field field, final ImportCollector importCollector) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            genericType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        }

        return getJSType(type, (Class<?>) genericType, importCollector);
    }

    public String getJSType(final Class<?> type, final Parameter parameter, final ImportCollector importCollector) {
        Type parameterizedType = parameter.getParameterizedType();
        if (parameterizedType instanceof ParameterizedType) {
            parameterizedType = ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
        }

        return getJSType(type, (Class<?>) parameterizedType, importCollector);
    }

    public String getJSType(final Class<?> type, final Class<?> genericType, final ImportCollector importCollector) {
        if (type.isPrimitive()) {
            switch (type.getName()) {
                case "boolean":
                    return "boolean";
                case "int":
                    return "number";
                case "long":
                    throw new RuntimeException("Long kann nicht vollständig von number abgebildet werden (max. 2^53-1 bzw. -(2^53-1)) \n Bitte BigDecimal verwenden");
            }
            return "string";
        } else {
            if (String.class.equals(type)) {
                return "string";
//            } else if (Long.class.isAssignableFrom(type)) {
//                return getJSType(genericType, long.class, importCollector);
            } else if (BigInteger.class.isAssignableFrom(type)) {
                return "string";
            } else if (Number.class.isAssignableFrom(type)) {
                return "number";
            } else if (Calendar.class.isAssignableFrom(type)) {
                return "Date";
            } else if (LocalDate.class.isAssignableFrom(type)) {
                importCollector.addAbsolutImport("luxon", "DateTime");
                return "DateTime";
            } else if (LocalDateTime.class.isAssignableFrom(type)) {
                importCollector.addAbsolutImport("luxon", "DateTime");
                return "DateTime";
            } else if (Boolean.class.isAssignableFrom(type)) {
                return "boolean";
            } else if (Collection.class.isAssignableFrom(type)) {
                return getJSType(genericType, Void.class, importCollector) + "[]";
            } else {
                final Class<?> parentType = GeneratorUtil.classToParent(type);
                if (parentType != null && !parentType.equals(AbstractDomainModel.class)) {
                    getJSType(parentType, genericType, new ImportCollector());
                }
                if (!alreadyProcessed.contains(type)) {
                    toProcess.add(type);
                }
                importCollector.addImport(type);
                return GeneratorUtil.classToDTOName(type);
            }
        }
    }

    private void getInstanceCopy(final StringBuilder to, final Class<?> type, final Field field) {
        final String jsType = getJSType(type, field, new ImportCollector());

        String creationFunction;

        if (jsType.matches(".*\\[\\]$")) { // collection
            final String jsTypeWithoutArray = jsType.replaceAll("\\[\\]$", "");
            creationFunction = "init." + field.getName() + ".map((element: Any<" + jsTypeWithoutArray + ">) => ";
            creationFunction += instanceCloneByConstructor(getGenericClass(field), jsTypeWithoutArray, "element");
            creationFunction += ")";
        } else {
            creationFunction = instanceCloneByConstructor(type, jsType, "init." + field.getName());
        }

        to.append(creationFunction);
    }

    private void getInstanceCreation(final StringBuilder to, final Class<?> type, final Field field, final ImportCollector importCollector) {
        final String jsType = getJSType(type, field, new ImportCollector());

        String creationFunction;

        if (jsType.matches(".*\\[\\]$")) { // collection
            final String jsTypeWithoutArray = jsType.replaceAll("\\[\\]$", "");
            creationFunction = "init." + field.getName() + ".map((element: Any<" + jsTypeWithoutArray + ">) => ";
            creationFunction += instanceCloneFromAny(getGenericClass(field), jsTypeWithoutArray, "element", importCollector);
            creationFunction += ")";
        } else {
            creationFunction = instanceCloneFromAny(type, jsType, "init." + field.getName(), importCollector);
        }

        to.append(creationFunction);
    }

    private Class<?> getGenericClass(final Field field) {
        final Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        }
        return field.getType();
    }


    @FunctionalInterface
    interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
}
