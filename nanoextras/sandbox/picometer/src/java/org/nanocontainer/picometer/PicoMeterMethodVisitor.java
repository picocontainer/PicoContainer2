/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/

// The class is in this package only to be able to access package private members from Label.
package org.nanocontainer.picometer;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.util.Collection;

/**
 * Visits code and records instantiations of new objects.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1570 $
 */
public class PicoMeterMethodVisitor implements MethodVisitor {
    private final Collection instantiations;
    private final PicoMeterClass picoMeterClass;

    private String lastType;
    private int currentLine = -1;

    public PicoMeterMethodVisitor(Collection instantiations, PicoMeterClass picoMeterClass) {
        this.instantiations = instantiations;
        this.picoMeterClass = picoMeterClass;
    }

    public void visitTypeInsn(int opcode, String desc) {
        if (Opcodes.NEW == opcode) {
            lastType = desc;
        } else {
            lastType = null;
        }
    }


    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        boolean isNew = (Opcodes.INVOKESPECIAL == opcode) && owner.equals(lastType) && "<init>".equals(name);
        if (isNew) {
            String className = owner.replace('/', '.');
            final Instantiation instantiation = new Instantiation(className, picoMeterClass);
            try {
                instantiation.setBytecodeLine(currentLine);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            instantiations.add(instantiation);
        }
    }

    public void visitLabel(Label label) {
    }

    public void visitLineNumber(int line, Label start) {
        currentLine = line;
    }

    public void visitAttribute(Attribute attribute) {
    }

    public void visitInsn(int opcode) {
    }

    public void visitIntInsn(int opcode, int operand) {
    }

    public void visitVarInsn(int opcode, int var) {
    }

    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    }

    public void visitJumpInsn(int opcode, Label label) {
    }

    public void visitLdcInsn(Object cst) {
    }

    public void visitIincInsn(int var, int increment) {
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label labels[]) {
    }

    public void visitLookupSwitchInsn(Label dflt, int keys[], Label labels[]) {
    }

    public void visitMultiANewArrayInsn(String desc, int dims) {
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
    }

    public void visitMaxs(int maxStack, int maxLocals) {
    }

    public void visitLocalVariable(String name, String desc, String sig, Label start, Label end, int index) {
    }

    public AnnotationVisitor visitAnnotationDefault() {
        return null;
    }

    public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
        return null;
    }

    public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1, boolean arg2) {
        return null;
    }

    public void visitCode() {
    }

    public void visitEnd() {
    }
}
