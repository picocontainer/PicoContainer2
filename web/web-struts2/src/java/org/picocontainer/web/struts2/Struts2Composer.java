/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.struts2;

import com.opensymphony.xwork2.interceptor.AliasInterceptor;
import com.opensymphony.xwork2.interceptor.ChainingInterceptor;
import com.opensymphony.xwork2.interceptor.DefaultWorkflowInterceptor;
import com.opensymphony.xwork2.interceptor.ExceptionMappingInterceptor;
import com.opensymphony.xwork2.interceptor.I18nInterceptor;
import com.opensymphony.xwork2.interceptor.ModelDrivenInterceptor;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.interceptor.PrepareInterceptor;
import com.opensymphony.xwork2.interceptor.ScopedModelDrivenInterceptor;
import com.opensymphony.xwork2.interceptor.StaticParametersInterceptor;
import org.apache.struts2.interceptor.CheckboxInterceptor;
import org.apache.struts2.interceptor.ExecuteAndWaitInterceptor;
import org.apache.struts2.interceptor.FileUploadInterceptor;
import org.apache.struts2.interceptor.ProfilingActivationInterceptor;
import org.apache.struts2.interceptor.ServletConfigInterceptor;
import org.apache.struts2.interceptor.StrutsConversionErrorInterceptor;
import org.apache.struts2.interceptor.debugging.DebuggingInterceptor;
import org.apache.struts2.interceptor.validation.AnnotationValidationInterceptor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.WebappComposer;

import javax.servlet.ServletContext;

import ognl.OgnlRuntime;

public abstract class Struts2Composer implements WebappComposer {

    public void composeApplication(MutablePicoContainer pico, ServletContext servletContext) {

        pico.addComponent(ExceptionMappingInterceptor.class);
        pico.addComponent(ServletConfigInterceptor.class);
        pico.addComponent(PrepareInterceptor.class);
        pico.addComponent(CheckboxInterceptor.class);
        pico.addComponent(ParametersInterceptor.class);
        pico.addComponent(StrutsConversionErrorInterceptor.class);
        pico.addComponent(AnnotationValidationInterceptor.class);
        pico.addComponent(DefaultWorkflowInterceptor.class);
        pico.addComponent(FileUploadInterceptor.class);
        pico.addComponent(ModelDrivenInterceptor.class);
        pico.addComponent(ChainingInterceptor.class);
        pico.addComponent(I18nInterceptor.class);
        pico.addComponent(AliasInterceptor.class);
        pico.addComponent(StaticParametersInterceptor.class);
        pico.addComponent(DebuggingInterceptor.class);
        pico.addComponent(ProfilingActivationInterceptor.class);
        pico.addComponent(ScopedModelDrivenInterceptor.class);
        pico.addComponent(ExecuteAndWaitInterceptor.class);
    }
}
