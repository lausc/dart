package com.itestra.resource;

import javax.ws.rs.core.*;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DartResponse<T> extends Response {

    private Response response;
    private Class<T> clazz;

    private DartResponse(Response response, Class<T> clazz) {
        this.response = response;
        this.clazz = clazz;
    }

    public static <T> DartResponse<T> create(Object object, Class<T> clazz) {
        return new DartResponse<T>(Response.ok().entity(object).build(), clazz);
    }

    public static <T> DartResponse<T> create(GenericEntity<T> object) {

        return new DartResponse<T>(Response.ok().entity(object).build(), (Class<T>) object.getRawType());
    }

    public static DartResponse<Void> createVoid() {
        return new DartResponse<Void>(Response.ok().build(), Void.class);
    }

    @Override
    public int getStatus() {
        return response.getStatus();
    }

    @Override
    public StatusType getStatusInfo() {
        return response.getStatusInfo();
    }

    @Override
    public Object getEntity() {
        return response.getEntity();
    }

    @Override
    public <T> T readEntity(Class<T> aClass) {
        return response.readEntity(aClass);
    }

    @Override
    public <T> T readEntity(GenericType<T> genericType) {
        return response.readEntity(genericType);
    }

    @Override
    public <T> T readEntity(Class<T> aClass, Annotation[] annotations) {
        return response.readEntity(aClass, annotations);
    }

    @Override
    public <T> T readEntity(GenericType<T> genericType, Annotation[] annotations) {
        return response.readEntity(genericType, annotations);
    }

    @Override
    public boolean hasEntity() {
        return response.hasEntity();
    }

    @Override
    public boolean bufferEntity() {
        return response.bufferEntity();
    }

    @Override
    public void close() {
        response.close();
    }

    @Override
    public MediaType getMediaType() {
        return response.getMediaType();
    }

    @Override
    public Locale getLanguage() {
        return response.getLanguage();
    }

    @Override
    public int getLength() {
        return response.getLength();
    }

    @Override
    public Set<String> getAllowedMethods() {
        return response.getAllowedMethods();
    }

    @Override
    public Map<String, NewCookie> getCookies() {
        return response.getCookies();
    }

    @Override
    public EntityTag getEntityTag() {
        return response.getEntityTag();
    }

    @Override
    public Date getDate() {
        return response.getDate();
    }

    @Override
    public Date getLastModified() {
        return response.getLastModified();
    }

    @Override
    public URI getLocation() {
        return response.getLocation();
    }

    @Override
    public Set<Link> getLinks() {
        return response.getLinks();
    }

    @Override
    public boolean hasLink(String s) {
        return response.hasLink(s);
    }

    @Override
    public Link getLink(String s) {
        return response.getLink(s);
    }

    @Override
    public Link.Builder getLinkBuilder(String s) {
        return response.getLinkBuilder(s);
    }

    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return response.getMetadata();
    }

    @Override
    public MultivaluedMap<String, String> getStringHeaders() {
        return response.getStringHeaders();
    }

    @Override
    public String getHeaderString(String s) {
        return response.getHeaderString(s);
    }
}
