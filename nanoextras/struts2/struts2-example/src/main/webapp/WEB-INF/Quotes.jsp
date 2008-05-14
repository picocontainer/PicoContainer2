<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <s:head/>
</head>
<body>
<h1>New Stock Quote</h1>

<s:form action="GetQuotes.action">
    <s:textfield label="Ticker" name="ticker" size="4"/>
    <s:submit label="Get Quote" name="getQuote"/>
</s:form>
<h1>Recent Stock Quotes</h1>
<table>
    <tr>
        <td><b>Ticker</b></td>
        <td><b>Quote</b></td>
    </tr>
    <s:iterator value="quotes" id="quote">
        <tr>
            <td>
                <s:property value="ticker"/>
            </td>
            <td>
                <s:property value="quote"/>
            </td>
        </tr>
    </s:iterator>
</table>
</body>
</html>