/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.aop.defaults;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.picocontainer.aop.ComponentPointcut;
import org.picocontainer.aop.MalformedRegularExpressionException;

/**
 * Component pointcut that matches the component name against a regular
 * expression.
 *
 * @author Stephen Molitor
 */
public class NameMatchesComponentPointcut implements ComponentPointcut {

    private final Pattern pattern;
    private final Perl5Matcher matcher = new Perl5Matcher();

    /**
     * Creates a new <code>NameMatchesComponentPointcut</code> that will match
     * the component key against the given regular expression. The regular
     * expression must be an <a
     * href="http://jakarta.apache.org/oro/index.html">ORO </a> Perl5 regular
     * expression.
     *
     * @param regex the regular expression to match against the component name.
     * @throws org.picocontainer.aop.MalformedRegularExpressionException
     *          if the regular expression is
     *          invalid.
     */
    public NameMatchesComponentPointcut(String regex) throws MalformedRegularExpressionException {
        Perl5Compiler compiler = new Perl5Compiler();
        try {
            pattern = compiler.compile(regex);
        } catch (MalformedPatternException e) {
            throw new MalformedRegularExpressionException("malformed component name regular expression", e);
        }
    }

    /**
     * Tests to see if the component key's toString() value matches the regular expression passed
     * to the constructor.
     *
     * @param componentKey the component key to match against.
     * @return true if the regular expression passed to the constructor matches
     *         against <code>componentKey</code>, else false.
     */
    public boolean picks(Object componentKey) {
        String componentName = componentKey.toString();
        return matcher.contains(componentName, pattern);
    }

}