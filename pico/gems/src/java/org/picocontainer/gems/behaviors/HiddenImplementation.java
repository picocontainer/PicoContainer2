/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.gems.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.AbstractBehavior;
import org.picocontainer.behaviors.Cached;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.HashSet;

import org.objectweb.asm.*;


/**
 * This component adapter makes it possible to hide the implementation of a real subject (behind a proxy).
 * The proxy will implement all the interfaces of the
 * underlying subject. If you want caching,
 * use a {@link Cached} around this one.
 *
 * @author Paul Hammant
 */
public class HiddenImplementation extends AbstractBehavior implements Opcodes {

    public HiddenImplementation(final ComponentAdapter delegate) {
        super(delegate);
    }

    public Object getComponentInstance(final PicoContainer container) {
        Object o = getDelegate().getComponentInstance(container);
        Class[] interfaces = o.getClass().getInterfaces();
        if (interfaces.length != 0) {
            byte[] bytes = makeProxy("XX", interfaces, true);
            AsmClassLoader cl = new AsmClassLoader(HotSwappable.Swappable.class.getClassLoader());
            Class<?> pClazz = cl.defineClass("XX", bytes);
            try {
                Constructor<?> ctor = pClazz.getConstructor(HotSwappable.Swappable.class);
                final HotSwappable.Swappable swappable = getSwappable();
                swappable.swap(o);
                return ctor.newInstance(swappable);
            } catch (NoSuchMethodException e) {
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }
        return o;
    }

    public String getDescriptor() {
        return "Hidden";
    }

    protected HotSwappable.Swappable getSwappable() {
        return new HotSwappable.Swappable();
    }

    public byte[] makeProxy(String proxyName, Class[] interfaces, boolean setter) {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;

        Class<Object> superclass = Object.class;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, proxyName, null, dotsToSlashes(superclass), getNames(interfaces));

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_TRANSIENT, "swappable", encodedClassName(HotSwappable.Swappable.class), null, null);
            fv.visitEnd();
        }
        doConstructor(proxyName, cw);
        Set<String> methodsDone = new HashSet<String>();
        for (Class iface : interfaces) {
            Method[] meths = iface.getMethods();
            for (Method meth : meths) {
                if (!methodsDone.contains(meth.toString())) {
                    doMethod(proxyName, cw, iface, meth);
                    methodsDone.add(meth.toString());
                }
            }
        }

        cw.visitEnd();

        return cw.toByteArray();
    }

    private String[] getNames(Class[] interfaces) {
        String[] retVal = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            retVal[i] = dotsToSlashes(interfaces[i]);
        }
        return retVal;
    }

    private void doConstructor(String proxyName, ClassWriter cw) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L"+ dotsToSlashes(HotSwappable.Swappable.class)+";)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, proxyName, "swappable", encodedClassName(HotSwappable.Swappable.class));
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private void doMethod(String proxyName, ClassWriter cw, Class iface, Method meth) {
        String signature = "(" + encodedParameterNames(meth) + ")" + encodedClassName(meth.getReturnType());
        String[] exceptions = encodedExceptionNames(meth.getExceptionTypes());
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, meth.getName(), signature, null, exceptions);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, proxyName, "swappable", encodedClassName(HotSwappable.Swappable.class));
        mv.visitMethodInsn(INVOKEVIRTUAL, dotsToSlashes(HotSwappable.Swappable.class), "getInstance", "()Ljava/lang/Object;");
        mv.visitTypeInsn(CHECKCAST, dotsToSlashes(iface));
        Class[] types = meth.getParameterTypes();
        int ix = 1;
        for (Class type : types) {
            int load = whichLoad(type);
            mv.visitVarInsn(load, ix);
            ix = indexOf(ix, load);
        }
        mv.visitMethodInsn(INVOKEINTERFACE, dotsToSlashes(iface), meth.getName(), signature);
        mv.visitInsn(whichReturn(meth.getReturnType()));
        mv.visitMaxs(ix, ix);
        mv.visitEnd();
    }

    private int indexOf(int ix, int loadType) {
        if (loadType == LLOAD) {
            return ix + 2;
        } else if (loadType == DLOAD) {
            return ix + 2;
        } else if (loadType == ILOAD) {
            return ix + 1;
        } else if (loadType == ALOAD) {
            return ix + 1;
        } else if (loadType == FLOAD) {
            return ix + 1;
        }
        return 0;
    }

    private String[] encodedExceptionNames(Class[] exceptionTypes) {
        if (exceptionTypes.length == 0) {
            return null;
        }
        String[] retVal = new String[exceptionTypes.length];
        for (int i = 0; i < exceptionTypes.length; i++) {
            Class clazz = exceptionTypes[i];
            retVal[i] = dotsToSlashes(clazz);
        }
        return retVal;
    }

    private int whichReturn(Class clazz) {
        if (!clazz.isPrimitive()) {
            return ARETURN;
        } else if (clazz.isArray()) {
            return ARETURN;
        } else if (clazz == int.class) {
            return IRETURN;
        } else if (clazz == long.class) {
            return LRETURN;
        } else if (clazz == byte.class) {
            return IRETURN;
        } else if (clazz == float.class) {
            return FRETURN;
        } else if (clazz == double.class) {
            return DRETURN;
        } else if (clazz == char.class) {
            return IRETURN;
        } else if (clazz == short.class) {
            return IRETURN;
        } else if (clazz == boolean.class) {
            return IRETURN;
        } else if (clazz == void.class) {
            return RETURN;
        } else {
            return 0;
        }
    }

    private int whichLoad(Class clazz) {
        if (!clazz.isPrimitive()) {
            return ALOAD;
        } else if (clazz.isArray()) {
            return ALOAD;
        } else if (clazz == int.class) {
            return ILOAD;
        } else if (clazz == long.class) {
            return LLOAD;
        } else if (clazz == byte.class) {
            return ILOAD;
        } else if (clazz == float.class) {
            return FLOAD;
        } else if (clazz == double.class) {
            return DLOAD;
        } else if (clazz == char.class) {
            return ILOAD;
        } else if (clazz == short.class) {
            return ILOAD;
        } else if (clazz == boolean.class) {
            return ILOAD;
        } else {
            return 0;
        }
    }

    private String encodedClassName(Class clazz) {
        if (clazz.getName().startsWith("[")) {
            return dotsToSlashes(clazz);
        } else if (!clazz.isPrimitive()) {
            return "L" + dotsToSlashes(clazz) + ";";
        } else if (clazz == int.class) {
            return "I";
        } else if (clazz == long.class) {
            return "J";
        } else if (clazz == byte.class) {
            return "B";
        } else if (clazz == float.class) {
            return "F";
        } else if (clazz == double.class) {
            return "D";
        } else if (clazz == char.class) {
            return "C";
        } else if (clazz == short.class) {
            return "S";
        } else if (clazz == boolean.class) {
            return "Z";
        } else if (clazz == void.class) {
            return "V";
        } else {
            return null;
        }
    }

    private String encodedParameterNames(Method meth) {
        String retVal = "";
        for (Class type : meth.getParameterTypes()) {
            retVal += encodedClassName(type);
        }
        return retVal;
    }

    private String dotsToSlashes(Class type) {
        return type.getName().replace('.', '/');
    }

    private static class AsmClassLoader extends ClassLoader {

        public AsmClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

}