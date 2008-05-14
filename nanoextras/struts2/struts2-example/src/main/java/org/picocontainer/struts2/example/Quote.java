package org.picocontainer.struts2.example;

import java.math.BigDecimal;

public class Quote {
    private final String ticker;
    private final BigDecimal quote;

    public Quote(String ticker, BigDecimal quote) {
        this.ticker = ticker;
        this.quote = quote;
    }
    public String getTicker() {
    return ticker;
}

    public BigDecimal getQuote() {
        return quote;
    }
}
