package com.epam.esm.servlet;


import com.epam.esm.config.PersistenceConfig;
import com.epam.esm.config.WebConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

//TODO -- registers a DispatcherServlet and use Java-based Spring configuration
// duplicate of StringBootApplication functionality?
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() { return new Class<?>[]{PersistenceConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
