package org.picocontainer.web.sample.ajaxemail;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: Mar 30, 2009
 * Time: 10:05:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IMailbox {
    Message read(long msgId);

    void delete(long msgId);

    Message[] messages();
}
