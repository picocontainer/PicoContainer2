/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.struts2.example;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.math.BigDecimal;

public class RecentQuotes {

    private List<Quote> quotes = new ArrayList<Quote>();

    public synchronized int addQuote(String ticker, BigDecimal quote) {
        if (quotes.size() == 5) {
            quotes.remove(quotes.get(0));
        }
        quotes.add(new Quote(ticker, quote));
        return quotes.size();
    }

    public List<Quote> getQuotes() {
        return Collections.unmodifiableList(quotes);
    }
}
