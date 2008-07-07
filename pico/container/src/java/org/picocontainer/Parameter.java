/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Jon Tirsen                        *
 *****************************************************************************/

package org.picocontainer;

import org.picocontainer.parameters.ComponentParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * This class provides control over the arguments that will be passed to a constructor. It can be used for finer control over
 * what arguments are passed to a particular constructor.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author Thomas Heller
 * @see MutablePicoContainer#addComponent(Object,Object,Parameter[]) a method on the
 *      {@link MutablePicoContainer} interface which allows passing in of an array of {@linkplain Parameter Parameters}.
 * @see org.picocontainer.parameters.ComponentParameter an implementation of this interface that allows you to specify the key
 *      used for resolving the parameter.
 * @see org.picocontainer.parameters.ConstantParameter an implementation of this interface that allows you to specify a constant
 *      that will be used for resolving the parameter.
 */
public interface Parameter {

	/**
	 * Zero parameter is used when you wish a component to be instantiated with its default constructor.  Ex:
	 * <div class="source">
	 * 	<pre>
	 * 		MutablePicoContainer mpc = new PicoBuilder().build();
	 * 		mpc.addComponent(Map.class, HashMap.class, Parameter.ZERO);
	 * 		mpc.addComponent(List.class, ArrayList.class, Parameter.ZERO);
	 * 	</pre>
	 * </div>
	 * <p>By specifying the default constructor in this example code, you allow PicoContainer to recognize
	 * that HashMap(Collection) should <em>not</em> be used and avoid a CircularDependencyException.</p>
	 */
    Parameter[] ZERO = new Parameter[0];
    Parameter[] DEFAULT = new Parameter[]{ ComponentParameter.DEFAULT };

    /**
     * Retrieve the object from the Parameter that satisfies the expected type.
     *
     * @param container             the container from which dependencies are resolved.
     * @param adapter               the {@link org.picocontainer.ComponentAdapter} that is asking for the instance
     * @param expectedType          the type that the returned instance needs to match.
     * @param expectedNameBinding Expected parameter name
     *
     * @param useNames
     * @param binding
     * @return the instance or <code>null</code> if no suitable instance can be found.
     *
     * @throws PicoCompositionException if a referenced component could not be instantiated.
     */
     Object resolveInstance(PicoContainer container, ComponentAdapter adapter,
                           Type expectedType, NameBinding expectedNameBinding,
                           boolean useNames, Annotation binding);

    /**
     * Check if the Parameter can satisfy the expected type using the container.
     *
     * @param container             the container from which dependencies are resolved.
     * @param adapter               the {@link ComponentAdapter} that is asking for the instance
     * @param expectedType          the required type
     * @param expectedNameBinding Expected parameter name
     *
     * @param useNames
     * @param binding
     * @return <code>true</code> if the component parameter can be resolved.
     *
     */
    boolean isResolvable(PicoContainer container, ComponentAdapter adapter,
                                Type expectedType, NameBinding expectedNameBinding,
                                boolean useNames, Annotation binding);

    /**
     * Verify that the Parameter can satisfy the expected type using the container
     *
     * @param container             the container from which dependencies are resolved.
     * @param adapter               the {@link org.picocontainer.ComponentAdapter} that is asking for the verification
     * @param expectedType          the required type
     * @param expectedNameBinding Expected parameter name
     *
     * @param useNames
     * @param binding
     * @throws PicoCompositionException if parameter and its dependencies cannot be resolved
     */
    void verify(PicoContainer container, ComponentAdapter adapter,
                Type expectedType, NameBinding expectedNameBinding,
                boolean useNames, Annotation binding);

    /**
     * Accepts a visitor for this Parameter. The method is normally called by visiting a {@link ComponentAdapter}, that
     * cascades the {@linkplain PicoVisitor visitor} also down to all its {@linkplain Parameter Parameters}.
     *
     * @param visitor the visitor.
     *
     */
    void accept(PicoVisitor visitor);
}
