/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.struts2.example;

import static com.opensymphony.xwork2.Action.SUCCESS;

import java.math.BigDecimal;
import java.util.List;

public class GetQuote {

    final RecentQuotes quotes;
    final StockQuoteService service;
    private String ticker;
    private String amount;
    private BigDecimal quote;

    public GetQuote(RecentQuotes quotes, StockQuoteService service) {
        this.quotes = quotes;
        this.service = service;
    }

    public String execute() {
        if (ticker != null) {
            quote = service.getQuote(ticker);
            quotes.addQuote(ticker, quote);
        }
        return SUCCESS;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public List<Quote> getQuotes() {
        return quotes.getQuotes();
    }
}
