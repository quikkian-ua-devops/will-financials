package org.kuali.kfs.sys.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.sun.jersey.api.container.filter.LoggingFilter;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

@ApplicationPath("api/v1/business-object")
public class BusinessObjectApplication extends Application {
    
    protected Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> clazzes = new HashSet<>();

    public BusinessObjectApplication() {
        if (SpringContext.getBean(ConfigurationService.class).getPropertyValueAsBoolean("apis.enabled")) {
            singletons.add(new BusinessObjectResource());
            clazzes.add(LoggingFilter.class);
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        return clazzes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}