/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.struts2.example;

import java.math.BigDecimal;
import java.math.MathContext;

public class StockQuoteService {

    public BigDecimal getQuote(String ticker) {
        return BigDecimal.valueOf(Math.random()).movePointRight(2).round(new MathContext(4));
    }
}
