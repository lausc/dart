package com.itestra.tools;

import com.google.common.collect.Lists;
import com.itestra.controller.PlayerController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TSGeneratorMain {
    private static final Logger log = LogManager.getLogger();

    private static final String PATH = "/home/ingo/deve/lauri/dart/dart-app";

    public static void main(final String[] args) throws InvocationTargetException, IllegalAccessException, IOException {
        log.info("From Generator");

        final List<Class<?>> controllerClasses = Lists.newArrayList(
                PlayerController.class);

        final TSGenerator tsGenerator = new TSGenerator();
        for (final Class<?> controllerClass : controllerClasses) {
            final Method[] methods = controllerClass.getMethods();
            for (final Method method : methods) {
                Set<Class> annotationSet = Arrays.stream(method.getAnnotations()).map(Annotation::annotationType).collect(Collectors.toSet());
                if (annotationSet.contains(GET.class) || annotationSet.contains(PUT.class) || annotationSet.contains(POST.class)) {
                    tsGenerator.generateForMethod(controllerClass, method);
                }
            }
        }
        tsGenerator.writeFiles(PATH);

    }
}
