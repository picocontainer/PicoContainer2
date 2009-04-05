package org.picocontainer.web.sample.ajaxemail;

public interface PersistenceManagerWrapper {
    void makePersistent(Object persistent);

    void beginTransaction();

    void commitTransaction();

    Query newQuery(Class<?> clazz, String query);

    void deletePersistent(Object persistent);

}
