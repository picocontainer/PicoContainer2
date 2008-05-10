/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop;

import java.lang.reflect.Method;

/**
 * Produces pointcuts.
 *
 * @author Stephen Molitor
 */
public interface PointcutsFactory {

    /**
     * Returns a component pointcut that picks one component key.
     *
     * @param componentKey the component key to match against.
     * @return a <code>ComponentPointcut</code> that matches
     *         <code>componentKey</code>.
     */
    ComponentPointcut component(Object componentKey);

    /**
     * Returns a component pointcut that matches component keys with a regular
     * expression. The regular expression must be an <a
     * href="http://jakarta.apache.org/oro/index.html">ORO </a> Perl5 compatible
     * regular expression.
     *
     * @param regex the regular expression to match against.
     * @return a <code>ComponentPointcut</code> that matches the component key
     *         against <code>regex</code>.
     * @throws MalformedRegularExpressionException
     *          if the regular expression is
     *          invalid.
     */
    ComponentPointcut componentName(String regex) throws MalformedRegularExpressionException;

    /**
     * Returns a class pointcut that picks all classes.
     *
     * @return a <code>ClassPointcut</code> that matches all classes.
     */
    ClassPointcut allClasses();

    /**
     * Returns a class pointcut that picks all instances of a given type.
     *
     * @param type the base interface or class.
     * @return a <code>ClassPointcut</code> that matches instances of
     *         <code>type</code>.
     */
    ClassPointcut instancesOf(Class type);

    /**
     * Returns a class pointcut that matches class names with a regular
     * expression. The regular expression must be an <a
     * href="http://jakarta.apache.org/oro/index.html">ORO </a> Perl5 regular
     * expression.
     *
     * @param regex the regular expression to match against.
     * @return a <code>ClassPointcut</code> that matches the class name
     *         against <code>regex</code>.
     * @throws org.nanocontainer.aop.MalformedRegularExpressionException
     *          if the regular expression is
     *          invalid.
     */
    ClassPointcut className(String regex) throws MalformedRegularExpressionException;

    /**
     * Returns a class pointcut that picks one class.
     *
     * @param clazz the class to match against.
     * @return a <code>ClassPointcut</code> that matches <code>clazz</code>.
     */
    ClassPointcut oneClass(Class clazz);

    /**
     * Returns a class pointcut that picks all classes in a package. Note that
     * the <code>packageName</code> argument is not a regular expression; the
     * returned pointcut expects an exact match against the package name.
     *
     * @param packageName the package name to match against the package of the
     *                    candidate component's class.
     * @return a <code>ClassPointcut</code> that matches the class package
     *         with <code>packageName</code>.
     */
    ClassPointcut packageName(String packageName);

    /**
     * Returns a class pointcut that is the intersection of two class pointcuts.
     *
     * @param a the first <code>ClassPointcut</code>.
     * @param b the second <code>ClassPointcut</code>.
     * @return a <code>ClassPointcut</code> that is the intersection of
     *         <code>a</code> and <code>b</code>.
     */
    ClassPointcut intersection(ClassPointcut a, ClassPointcut b);

    /**
     * Returns a pointcut that is the union of two class pointcuts.
     *
     * @param a the first <code>ClassPointcut</code>.
     * @param b the second <code>ClassPointcut</code>.
     * @return a <code>ClassPointcut</code> that is the union of
     *         <code>a</code> and <code>b</code>.
     */
    ClassPointcut union(ClassPointcut a, ClassPointcut b);

    /**
     * Returns a class pointcut that inverts the original pointcut.
     *
     * @param classPointcut the pointcut to negate.
     * @return a <code>ClassPointcut</code> that inverts
     *         <code>classPointcut</code>.
     */
    ClassPointcut not(ClassPointcut classPointcut);

    /**
     * Returns a pointcut that matches all methods.
     *
     * @return a <code>MethodPointcut</code> that matches all methods.
     */
    MethodPointcut allMethods();

    /**
     * Returns a pointcut that matches get methods. Note that this does not
     * include 'is' methods.
     *
     * @return a <code>MethodPointcut</code> that matches get methods.
     */
    MethodPointcut getMethods();

    /**
     * Returns a pointcut that matches is methods.
     *
     * @return a <code>MethodPointcut</code> that matches is methods.
     */
    MethodPointcut isMethods();

    /**
     * Returns a method pointcut that matches set methods.
     *
     * @return a <code>MethodPointcut</code> that matches set methods.
     */
    MethodPointcut setMethods();

    /**
     * Returns a method pointcut that picks <code>equals</code>,
     * <code>hashCode</code>, and <code>toString</code>.
     *
     * @return a <code>MethodPointcut</code> that matches methods declared by
     *         <code>java.lang.Object</code>.
     */
    MethodPointcut objectMethods();

    /**
     * Returns a method pointcut that matches the method signatures with a
     * regular expression. Uses dynaop's signature pointcut. Method signatures
     * follow this pattern:
     * <p/>
     * <pre>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     *         ReturnType methodName(ArgumentType, ArgumentType, ...)
     *             throws ExceptionType, ExceptionType
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * </pre>
     * <p/>
     * Omits "java.lang." from classes in java.lang package. The regular
     * expression must be an <a
     * href="http://jakarta.apache.org/oro/index.html">ORO </a> Perl5 regular
     * expression.
     *
     * @param regexp the method signature regular expression.
     * @return a <code>MethodPointcut</code> that matches the method signature
     *         against a regular expression.
     */
    MethodPointcut signature(String regexp);

    /**
     * Returns a pointcut that matches one method.
     *
     * @param method the method to match against.
     * @return a <code>MethodPointcut</code> that matches one method.
     */
    MethodPointcut oneMethod(Method method);

    /**
     * Returns a method pointcut that picks a method if the given class pointcut
     * picks the method's return type.
     *
     * @param classPointcut the class pointcut to match against the method's
     *                      return type.
     * @return a <code>MethodPointcut</code> that matches
     *         <code>classPointcut</code> against the method's return type
     */
    MethodPointcut returnType(ClassPointcut classPointcut);

    /**
     * Returns a method pointcut that picks a method if the given class pointcut
     * picks the method's declaring class.
     *
     * @param classPointcut the class pointcut to match against the method's
     *                      declaring class.
     * @return a <code>MethodPointcut</code> that matches
     *         <code>classPointcut</code> against the method's declaring
     *         class.
     */
    MethodPointcut declaringClass(ClassPointcut classPointcut);

    /**
     * Picks methods that are members of the given class (even if the method was
     * declared in a super class of the given class).
     *
     * @param clazz the class that we will check to see if the method is a
     *              member of.
     * @return a <code>MethodPointcut</code> that will check to see if the
     *         method is a member of <code>clazz</code>.
     */
    MethodPointcut membersOf(Class clazz);

    /**
     * Returns a method pointcut that is the intersection of two other method
     * pointcuts.
     *
     * @param a the first method pointcut.
     * @param b the second method pointcut.
     * @return a <code>MethodPointcut</code> that is the intersection of
     *         <code>a</code> and <code>b</code>.
     */
    MethodPointcut intersection(MethodPointcut a, MethodPointcut b);

    /**
     * Returns a method pointcut that is the union of two other method
     * pointcuts.
     *
     * @param a the first method pointcut.
     * @param b the second method pointcut.
     * @return a <code>MethodPointcut</code> that is the union of
     *         <code>a</code> and <code>b</code>.
     */
    MethodPointcut union(MethodPointcut a, MethodPointcut b);

    /**
     * Creates a method pointcut that inverts the original pointcut.
     *
     * @param methodPointcut the pointcut to negate.
     * @return a new <code>MethodPointcut</code> that inverts
     *         <code>methodPointcut</code>.
     */
    MethodPointcut not(MethodPointcut methodPointcut);

}