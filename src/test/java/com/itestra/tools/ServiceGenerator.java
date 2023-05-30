package com.itestra.tools;

import com.google.common.collect.LinkedHashMultimap;

import javax.ws.rs.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.itestra.tools.GeneratorUtil.N;
import static com.itestra.tools.GeneratorUtil.T;


public class ServiceGenerator {
    private static final String SERVICENAME_REPLACEMENT = "##SERVICENAME##";
    private static final String METHODEN_REPLACEMENT = "##METHODEN##";
    private static final String SERVICE =
            "@Injectable({" + N +
                    "  providedIn: 'root'" + N +
                    "})" + N +
                    "export class " + SERVICENAME_REPLACEMENT + " {" + N +
                    "  constructor(private http: HttpClient) { }" + N +
                    "" + N +
                    METHODEN_REPLACEMENT +
                    "}" + N;
    private static final String METHODEN_NAME = "##METHODENNAME##";
    private static final String PARAMETER = "##PARAMETER##";
    private static final String HTTP_PARAMS = "##HTTP_PARAMS##";
    private static final String RESULT_TYPE = "##RESULT_TYPE##";
    private static final String URL = "##URL##";
    private static final String TYPE = "##TYPE##";
    private static final String HTTP_PARAMETER = "##HTTP_PARAMETER##";
    private static final String MAPPING = "##MAPPING##";
    private static final String RESULT_CLONING = "##RESULT_CLONING##";
    private static final String MAPPER = "      .pipe(" + N + RESULT_CLONING +
            "        catchError(e => { console.error(e); return throwError(() => e); })" + N +
            "      )";
    private static final String METHODE = "  " + METHODEN_NAME + "(" + PARAMETER + "): Observable<" + RESULT_TYPE + "> {" +
            HTTP_PARAMS + "" + N +
            "    return this.http." + TYPE + "<" + RESULT_TYPE + ">(`${environment.apiUrl}" + URL + "`" + HTTP_PARAMETER + ")" + N +
            MAPPER + ";" + N +
            "  }" + N + N;
    private final DTOGenerator dtoGenerator;

    public ServiceGenerator(final DTOGenerator dtoGenerator) {
        this.dtoGenerator = dtoGenerator;
    }

    public LinkedHashMap<String, String> generateServices(final LinkedHashMultimap<Class<?>, Method> servicesWithMethods) {
        final LinkedHashMap<String, String> services = new LinkedHashMap<>();

        for (final Map.Entry<Class<?>, Collection<Method>> classCollectionEntry : servicesWithMethods.asMap().entrySet()) {
            services.put(GeneratorUtil.toFilePath(classCollectionEntry.getKey()), generateServices(classCollectionEntry.getKey(), classCollectionEntry.getValue()));
        }
        return services;
    }

    private String generateServices(final Class<?> service, final Collection<Method> methods) {
        final ImportCollector importCollector = new ImportCollector();

        importCollector.addAbsolutImport("service/config", "BASE_URL");
        importCollector.addAbsolutImport("service/handleResponse", "handleResponse");


        StringJoiner stringJoiner = new StringJoiner("\n\n");
        methods.forEach(method -> {
            stringJoiner.add(generateMethod(service, method, importCollector));
        });

        return stringJoiner.toString();
    }

    private String generateMethod(Class<?> service, Method method, ImportCollector importCollector) {
        String functionName = method.getName();
        String path = getPath(service) + getPath(method);
        String restMethod = getRestAnnotation(method);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("export async function " + functionName + "(" + getParams(method) + "): Promise<" + getReturnType(method, importCollector) + "> {" + N);
        stringBuilder.append(T + "const response = await fetch(`${BASE_URL}" + path + "`, {" + N);
        stringBuilder.append(T + T + "method: '" + restMethod + "'," + N);
        stringBuilder.append(T + T + "headers: {" + N);
        stringBuilder.append(T + T + T + "\"Content-Type\": \"application/json\"" + N);
        stringBuilder.append(T + T + "}," + N);
        stringBuilder.append(T + "});" + N + N);
        stringBuilder.append(T + "await handleResponse(response);" + N + N);
        stringBuilder.append(T + "return " + getReturn(method, importCollector) + ";" + N);
        stringBuilder.append("}" + N);
        return stringBuilder.toString();
    }

    private String getParams(Method method) {
        return "";
    }

    private String getRestAnnotation(Method method) {
        return Arrays.stream(method.getAnnotations()).filter(annotation -> annotation instanceof PUT || annotation instanceof POST || annotation instanceof GET || annotation instanceof DELETE || annotation instanceof PATCH).findFirst().map(a -> a != null ? a.annotationType().getSimpleName() : null).orElse(null);
    }

    private String getPath(AnnotatedElement annotatedElement) {
        Path pathAnnotation = (Path) Arrays.stream(annotatedElement.getAnnotations()).filter(annotation -> annotation instanceof Path).findFirst().orElse(null);
        return pathAnnotation != null ? pathAnnotation.value() : "";
    }

    private String getReturn(Method method, ImportCollector importCollector) {
        String tsReturntype = getReturnType(method, importCollector);
        if (tsReturntype.equals("void")) {
            return "";
        } else {
            return getNewInstance(tsReturntype) + "(await response.json() as " + tsReturntype + ")" + getMap(tsReturntype);
        }
    }

    private String getMap(String tsReturnType) {
        return (tsReturnType.contains("[]") && useNew(tsReturnType)) ? ".map((response) => new " + tsReturnType.replace("[]", "") + "(response))" : "";
    }

    private String getNewInstance(String tsReturnType) {
        return (!tsReturnType.equals("void") && !tsReturnType.contains("[]") && useNew(tsReturnType)) ? "new " + tsReturnType : "";

    }

    private boolean useNew(String tsReturnType) {
        return !(tsReturnType.startsWith("string") || tsReturnType.startsWith("number") || tsReturnType.startsWith("boolean"));
    }

    private String getReturnType(Method method, ImportCollector importCollector) {
        Type returnType = method.getGenericReturnType();
        if (returnType.equals(Void.TYPE)) {
            return "void";
        }
        Type actualParameterizedType = null;
        if (returnType instanceof ParameterizedType) {
            actualParameterizedType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
            return dtoGenerator.getJSType((Class<?>) ((ParameterizedType) returnType).getRawType(), (Class<?>) actualParameterizedType, importCollector);
        } else {
            return dtoGenerator.getJSType((Class<?>) returnType, (Class<?>) null, importCollector);
        }


    }

}
