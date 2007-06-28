/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.picometer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoMeterClassVisitor implements ClassVisitor {
    private final MethodVisitor codeVisitor;

    public PicoMeterClassVisitor(MethodVisitor codeVisitor) {
        this.codeVisitor = codeVisitor;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
    }

    public void visitAttribute(Attribute attribute) {
    }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }

    public MethodVisitor visitMethod(int i, String componentImplementationClassName, String componentImplementationClassName1, String signature, String[] strings) {
        return codeVisitor;
    }

    public void visitEnd() {
    }

    public void visitSource(String arg0, String arg1) {
    }

    public void visitOuterClass(String arg0, String arg1, String arg2) {
    }

    public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
        return null;
    }
}
