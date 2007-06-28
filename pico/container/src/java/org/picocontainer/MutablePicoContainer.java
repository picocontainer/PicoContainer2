/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer;

/**
 * This is the core interface used for registration of components with a container. It is possible to register
 * implementations and instances here
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 * @see <a href="package-summary.html#package_description">See package description for basic overview how to use PicoContainer.</a>
 * @since 1.0
 */
public interface MutablePicoContainer extends PicoContainer, Startable, Disposable {

    /**
     * Register a component and creates specific instructions on which constructor to use, along with
     * which components and/or constants to provide as constructor arguments.  These &quot;directives&quot; are
     * provided through an array of <tt>Parameter</tt> objects.  Parameter[0] correspondes to the first constructor
     * argument, Parameter[N] corresponds to the  N+1th constructor argument.
     * <h4>Tips for Parameter usage</h4>
     * <ul>
     * <li><strong>Partial Autowiring: </strong>If you have two constructor args to match and you only wish to specify one of the constructors and
     * let PicoContainer wire the other one, you can use as parameters:
     * <code><strong>new ComponentParameter()</strong>, new ComponentParameter("someService")</code>
     * The default constructor for the component parameter indicates auto-wiring should take place for
     * that parameter.
     * </li>
     * <li><strong>Force No-Arg constructor usage:</strong> If you wish to force a component to be constructed with
     * the no-arg constructor, use a zero length Parameter array.  Ex:  <code>new Parameter[0]</code>
     * <ul>
     *
     * @param componentKey a key that identifies the component. Must be unique within the container. The type
     *                     of the key object has no semantic significance unless explicitly specified in the
     *                     documentation of the implementing container.
     * @param componentImplementationOrInstance
     *                     the component's implementation class. This must be a concrete class (ie, a
     *                     class that can be instantiated). Or an intance of the compoent.
     * @param parameters   the parameters that gives the container hints about what arguments to pass
     *                     to the constructor when it is instantiated. Container implementations may ignore
     *                     one or more of these hints.
     *
     * @return the ComponentAdapter that has been associated with this component. In the majority of cases, this return
     *         value can be safely ignored, as one of the <code>getXXX()</code> methods of the
     *         {@link PicoContainer} interface can be used to retrieve a reference to the component later on.
     *
     * @throws PicoCompositionException if registration of the component fails.
     * @see org.picocontainer.Parameter
     * @see org.picocontainer.parameters.ConstantParameter
     * @see org.picocontainer.parameters.ComponentParameter
     */
    MutablePicoContainer addComponent(Object componentKey,
                                      Object componentImplementationOrInstance,
                                      Parameter... parameters);

    /**
     * Register an arbitrary object. The class of the object will be used as a key. Calling this method is equivalent to
     * calling  <code>addAdapter(componentImplementation, componentImplementation)</code>.
     *
     * @param implOrInstance Component implementation or instance
     *
     * @return the ComponentAdapter that has been associated with this component. In the majority of cases, this return
     *         value can be safely ignored, as one of the <code>getXXX()</code> methods of the
     *         {@link PicoContainer} interface can be used to retrieve a reference to the component later on.
     *
     * @throws PicoCompositionException if registration fails.
     */
    MutablePicoContainer addComponent(Object implOrInstance);

    /**
     * Register a component via a ComponentAdapter. Use this if you need fine grained control over what
     * ComponentAdapter to use for a specific component.
     *
     * @param componentAdapter the addAdapter
     *
     * @return the same adapter that was passed as an argument.
     *
     * @throws PicoCompositionException if registration fails.
     */
    MutablePicoContainer addAdapter(ComponentAdapter componentAdapter);

    /**
     * Unregister a component by key.
     *
     * @param componentKey key of the component to unregister.
     *
     * @return the ComponentAdapter that was associated with this component.
     */
    ComponentAdapter removeComponent(Object componentKey);

    /**
     * Unregister a component by instance.
     *
     * @param componentInstance the component instance to unregister.
     *
     * @return the ComponentAdapter that was associated with this component.
     */
    ComponentAdapter removeComponentByInstance(Object componentInstance);

    /**
     * Make a child container, using the same implementation of MutablePicoContainer as the parent.
     * It will have a reference to this as parent.  This will list the resulting MPC as a child.
     * Lifecycle events will be cascaded from parent to child
     * as a consequence of this.
     *
     * @return the new child container.
     *
     * @since 1.1
     */
    MutablePicoContainer makeChildContainer();

    /**
     * Add a child container. This action will list the the 'child' as exactly that in the parents scope.
     * It will not change the child's view of a parent.  That is determined by the constructor arguments of the child
     * itself. Lifecycle events will be cascaded from parent to child
     * as a consequence of calling this method.
     *
     * @param child the child container
     *
     * @return the same instance of MutablePicoContainer
     *
     * @since 1.1
     */
    MutablePicoContainer addChildContainer(PicoContainer child);

    /**
     * Remove a child container from this container. It will not change the child's view of a parent.
     * Lifecycle event will no longer be cascaded from the parent to the child.
     *
     * @param child the child container
     *
     * @return <code>true</code> if the child container has been removed.
     *
     * @since 1.1
     */
    boolean removeChildContainer(PicoContainer child);


    /**
     * You can change the characteristic of registration of all subsequent components in this container.
     *
     * @param characteristics characteristics
     *
     * @return the same Pico instance with changed characteritics
     */
    MutablePicoContainer change(ComponentCharacteristics... characteristics);

    /**
     * You can set for the following operation only the characteristic of registration of a component on the fly.
     *
     * @param characteristics characteristics
     *
     * @return the same Pico instance with temporary characteritics
     */
    MutablePicoContainer as(ComponentCharacteristics... characteristics);

}
