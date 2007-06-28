/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Centerline Computers, Inc.                               *
 *****************************************************************************/
package org.nanocontainer.nanowar.jsf;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.PicoContainer;

/**
 * This is a variable resolver implementation for Java ServerFaces.
 * <h2>Installation</h2>
 * <p>Add install this variable resolver by adding setting the application's variable resolver
 * to <em>org.nanocontainer.nanowar.jsf.NanoWarDelegatingVariableResolver</em>.  An example follows:</p>
 * <hr/>
 * <pre>
 *   &lt;faces-config&gt;
 *      &lt;application&gt;
 *          <strong>
 *          &lt;variable-resolver&gt;
 *              org.nanocontainer.nanowar.jsf.NanoWarDelegatingVariableResolver
 *          &lt;/variable-resolver&gt;
 *          </strong>
 *      &lt;/application&gt;
 *      ....
 *   &lt;/faces-config&gt;
 *  </pre>
 * <hr/>
 * <h2>Usage</h2>
 * <h4>Part 1 - Write your Constructor Dependency Injection (CDI) - based backing bean:</h4>
 * <p>Even though you are writing a backing bean, you can utilize PicoContainers CDI capabilities to the fullest.
 *  Example:
 * </p>
 * <pre>
 *    //Imports and variables...
 *    
 *    public ListCheeseController(<strong>CheeseService service</strong>) {
 *       this.service = service;       
 *    }
 *    
 *    //The rest of the class.
 * </pre>
 * <h4>Part 2 - Set up your NanoWAR services.</h4>
 * <p>(This assumes you have installed NanoWAR properly.  Please see the NanoWAR documentation for specific
 * instructions)</p>
 * <p>You need to name your services with the name you will be giving your <tt>Backing Bean</tt>.  Example:
 * <pre>
 *    pico = builder.container(parent: parent) {
 *        if(assemblyScope instanceof javax.servlet.ServletContext) {
 *          // Application Services would go here.
 *        } else if (assemblyScope instanceof javax.servlet.ServletRequest) {
 *            <strong>addComponent(key: 'cheeseBean', class: 'org.nanocontainer.nanowar.samples.jsf.ListCheeseController')</strong>
 *        }
 *    }
 * </pre>
 * <h4>Part 3 - Set up your managed beans for JSF</h4>
 * <p>Set the managed bean names in your <tt>faces-config</tt> to equal the names given to the backing
 * beans in the nanowar composition script.  Example:</p>
 * <pre>
 *    &lt;managed-bean&gt;
 *        &lt;description&gt;CDI Injected Bean&lt;/description&gt;
 *        <strong>&lt;managed-bean-name&gt;cheeseBean&lt;/managed-bean-name&gt;</strong>
 *        &lt;managed-bean-class&gt;
 *            org.nanocontainer.nanowar.samples.jsf.CheeseController
 *        &lt;/managed-bean-class&gt;
 *        &lt;managed-bean-scope&gt;request&lt;/managed-bean-scope&gt;
 *    &lt;/managed-bean&gt;
 * </pre>
 * <p>Notice how the same names were used in the <tt>faces-config</tt> as in the nanowar configuration.
 * When the JSF page asks for the bean named 'addCheeseBean', the Nano variable resolver will take that name
 * and check nanocontainer for an object of that instance.  If it finds one, it will send it back to the
 * page.</p> 
 * <em>Note:</em>
 * <p>This class currently has only been tested using MyFaces.  There are reports that this technique doesn't 
 * work on all reference implementation versions.  We welcome success or failure feedback!</p>
 * @author Michael Rimov
 */
public class NanoWarDelegatingVariableResolver extends VariableResolver  {

    /**
     * The nested variable resolver.
     */
    private VariableResolver nested;
    
    /**
     * Decorated Variable resolver.
     * @param decorated
     */
    public NanoWarDelegatingVariableResolver(VariableResolver decorated) {
        super();
        if (decorated == null) {
            throw new NullPointerException("decorated");
        }
        nested = decorated;
    }
    
    /**
     * Retrieve the delegated value.
     * @return the wrapped variable resolver.
     */
    protected VariableResolver getNested() {
        return nested;
    }

    /**
     * {@inheritDoc}
     * @param facesContext
     * @param name
     * @return the resulting object, either resolved through NanoWAR, or passed onto the delegate resolver.
     * @throws EvaluationException
     * @see javax.faces.el.VariableResolver#resolveVariable(javax.faces.context.FacesContext, java.lang.String)
     */
    public Object resolveVariable(FacesContext facesContext, String name) {
        
        PicoContainer nano = getPicoContainer(facesContext);
        
        Object result =  nano.getComponent(name);
        if (result == null) {
            return nested.resolveVariable(facesContext, name);
        }
        
        return result;
    }
    
    /**
     * Tries to locate the nanocontainer first at request level, and then if it doesn't find it
     * there. (Filter might not be installed), it tries Application level.  If that fails it throws
     * an exception since you wouldn't expect the NanoWarDelegatingVariableResolver 
     * @param facesContext
     * @return NanoContainer instance.
     * @throws EvaluationException if it cannot find a NanoWAR instance.
     */
    protected PicoContainer getPicoContainer(FacesContext facesContext) {
        Map requestAttributeMap = facesContext
            .getExternalContext()
            .getRequestMap();
        
        PicoContainer container = null;
        
        //First check request map.
        if (requestAttributeMap != null) {
            container = (PicoContainer)requestAttributeMap.get(KeyConstants.REQUEST_CONTAINER);
        }
        
        if (requestAttributeMap == null || container == null) {
            
            //If that fails, check session for container.
            Map sessionMap = facesContext.getExternalContext().getSessionMap();
            if (sessionMap != null) {
                //If there is a session.
                container = (PicoContainer)sessionMap.get(KeyConstants.SESSION_CONTAINER);                
            }
            
            if (sessionMap == null || container == null) {
                
                //If that fails, check for App level container.
                container = (PicoContainer) facesContext.getExternalContext().getApplicationMap().get(KeyConstants.APPLICATION_CONTAINER);
                if (container == null) {
                    //If that fails... Fail.
                    throw new EvaluationException("The NanoWar delegating variable resolver is installed, however no NanoWar "
                        +"container was found in the request or application scope.");
                }
            }
        }
        
        return container;
    }

}
