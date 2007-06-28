/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/

package org.nanocontainer.persistence.hibernate.annotations;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Just a pojo to make hibernate happy.
 * 
 * @author Konstantin Pribluda
 * @author Michael Rimov
 */
@Entity
public class Pojo implements Serializable {

    /**
     * Serialization UID
     */
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue
    private Integer id;
    

    @Column(name="Foo", length=34)
    private String foo;

    public Pojo() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

}
