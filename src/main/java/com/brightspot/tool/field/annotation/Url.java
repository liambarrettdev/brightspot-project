package com.brightspot.tool.field.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;

@ObjectField.AnnotationProcessorClass(UrlProcessor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Url {

}

class UrlProcessor implements ObjectField.AnnotationProcessor<Url> {

    private static final String PATTERN = "((https?:\\/\\/))(\\w+:{0,1})?(\\S+)(:[0-9]+)?";

    @Override
    public void process(ObjectType type, ObjectField field, Url annotation) {
        field.setPattern(PATTERN);
        field.setPredicateValidationMessage("Not a valid URL format");
    }
}
