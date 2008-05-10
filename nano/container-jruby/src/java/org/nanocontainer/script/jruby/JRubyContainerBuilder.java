/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 * Original code by Nick Sieger                                                                          *
 *****************************************************************************/

package org.nanocontainer.script.jruby;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Collections;

import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.exceptions.RaiseException;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.nanocontainer.integrationkit.LifecycleMode;
import org.picocontainer.PicoCompositionException;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.behaviors.Caching;

/**
 * The script uses the {@code nanocontainer.rb} script to create an instance of
 * {@link PicoContainer}. There are implicit variables named "$parent" and
 * "$assembly_scope".
 * 
 * @author Nick Sieger
 */
public final class JRubyContainerBuilder extends ScriptedContainerBuilder {
	public static final String MARKUP_EXCEPTION_PREFIX = "nanobuilder: ";

	private final String script;

	public JRubyContainerBuilder(Reader script, ClassLoader classLoader) {
		this(script,classLoader, LifecycleMode.AUTO_LIFECYCLE);
	}
	
	
	public JRubyContainerBuilder(Reader script, ClassLoader classLoader, LifecycleMode lifecycle) {
		super(script, classLoader, lifecycle);
		this.script = toString(script);
	}

	private String toString(Reader script) {
		int charsRead;
		char[] chars = new char[1024];
		StringWriter writer = new StringWriter();
		try {
			while ((charsRead = script.read(chars)) != -1) {
				writer.write(chars, 0, charsRead);
			}
		} catch (IOException e) {
			throw new RuntimeException("unable to read script from reader", e);
		}
		return writer.toString();
	}

	/**
	 * {@inheritDoc}
	 * <p>Latest method of invoking jruby script have been adapted from <a 
	 * href="http://wiki.jruby.org/wiki/Java_Integration" title="Click to visit JRuby Wiki">
	 * JRuby wiki:</a></p>
	 * @todo create a way to prevent initialization and shutdown with each script invocation.
	 */
	protected PicoContainer createContainerFromScript(PicoContainer parentContainer, Object assemblyScope) {
		if (parentContainer == null) {
			parentContainer = new EmptyPicoContainer();
		}
		parentContainer = new DefaultNanoContainer(getClassLoader(), new DefaultPicoContainer(new Caching(),
		        parentContainer));

		
		
		RubyInstanceConfig rubyConfig = new RubyInstanceConfig();
		rubyConfig.setLoader(this.getClassLoader());
		Ruby ruby = JavaEmbedUtils.initialize(Collections.EMPTY_LIST, rubyConfig);
		ruby.getLoadService().require("org/nanocontainer/script/jruby/nanobuilder");
		ruby.defineReadonlyVariable("$parent", JavaEmbedUtils.javaToRuby(ruby, parentContainer));
		ruby.defineReadonlyVariable("$assembly_scope", JavaEmbedUtils.javaToRuby(ruby, assemblyScope));
		
		
		try {
			
			//IRubyObject result = ruby.executeScript(script);
			IRubyObject result = JavaEmbedUtils.newRuntimeAdapter().eval(ruby, script);
			return (PicoContainer) JavaEmbedUtils.rubyToJava(ruby, result, PicoContainer.class);
		} catch (RaiseException re) {
			if (re.getCause() instanceof NanoContainerMarkupException) {
				throw (NanoContainerMarkupException) re.getCause();
			}
			String message = (String) JavaEmbedUtils.rubyToJava(ruby, re.getException().message, String.class);
			if (message.startsWith(MARKUP_EXCEPTION_PREFIX)) {
				throw new NanoContainerMarkupException(message.substring(MARKUP_EXCEPTION_PREFIX.length()));
			} else {
				throw new PicoCompositionException(message, re);
			}
		} finally {
			JavaEmbedUtils.terminate(ruby);
		}
	}
}
