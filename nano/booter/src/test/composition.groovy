import org.nanocontainer.reflection.ImplementationHidingNanoPicoContainer

pico = builder.container(parent:parent, class:ImplementationHidingNanoPicoContainer) {
    classPathElement(path:"test-comps/api.jar")
    classLoader {
        classPathElement(path:"test-comps/honeyimpl.jar")
        component(classNameKey:"org.nanocontainer.booter.Honey", class:"org.nanocontainer.booter.BeeHiveHoney")
    }
    classLoader {
        classPathElement(path:"test-comps/bearimpl.jar") {
             grant(new java.net.SocketPermission("yahoo.com:80", "connect"))
        }
        component(class:"org.nanocontainer.booter.BrownBear")
    }
}
