package com.brightspot.tool.field.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;

@ObjectField.AnnotationProcessorClass(MimeTypesProcessor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MimeTypes {

    String value();
}

class MimeTypesProcessor implements ObjectField.AnnotationProcessor<MimeTypes> {

    @Override
    public void process(ObjectType type, ObjectField field, MimeTypes annotation) {
        field.setMimeTypes(annotation.value());
    }
}
