package com.brightspot.tool.field.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.http.HttpServletRequest;

import com.brightspot.auth.AuthenticationFilter;
import com.psddev.cms.view.servlet.ServletViewRequestAnnotationProcessor;
import com.psddev.cms.view.servlet.ServletViewRequestAnnotationProcessorClass;

@ServletViewRequestAnnotationProcessorClass(CurrentUserProcessor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CurrentUser {

}

class CurrentUserProcessor implements ServletViewRequestAnnotationProcessor<CurrentUser> {

    @Override
    public Object getValue(HttpServletRequest request, String fieldName, CurrentUser annotation) {
        return AuthenticationFilter.getAuthenticatedUser(request);
    }
}
