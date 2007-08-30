/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.gems.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.TestCase;
/**
 * test capabilities of object reference storing stuff in JNDI
 * @author k.pribluda
 *
 */
public class JNDIObjectReferenceTestCase extends TestCase {

	Context ctx;
	JNDIObjectReference reference;
	
	public void setUp() throws Exception {
		super.setUp();
		Hashtable ht = new Hashtable();
		ht.put("java.naming.factory.initial","org.osjava.sj.memory.MemoryContextFactory");
		ctx = new InitialContext(ht);

	}
	/**
	 * object shall be stored and returned back
	 * @throws NamingException
	 */
	public void testStorageAndRetrieval() throws NamingException {
		reference = new JNDIObjectReference("glee:/glum/glarch/blurge", ctx);
		String obj = new String("that's me");		
		reference.set(obj);
		// shall be the same object - from reference or from 
		// context itself
		assertSame(obj,reference.get());
		assertSame(obj,ctx.lookup("glee:/glum/glarch/blurge"));
		
		// try to rebind context
		
		Integer glum = new Integer(239);
		reference.set(glum);
		assertSame(glum,reference.get());
		assertSame(glum,ctx.lookup("glee:/glum/glarch/blurge"));
		
		
		// and also unbind
		reference.set(null);
		assertNull(ctx.lookup("glee:/glum/glarch/blurge"));
	}
	
	/**
	 * test that object is safely stored in root context
	 */
	public void testStorageInRoot() {
		reference = new JNDIObjectReference("glarch", ctx);
		String obj = new String("that's me");		
		reference.set(obj);
		
		assertSame(obj,reference.get());
	}


}
