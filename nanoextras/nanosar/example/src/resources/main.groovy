
pico = new org.picocontainer.DefaultPicoContainer()

    pico.addComponent(new java.lang.String("glarch glurch"))
    /*
        note, that JNDI exposition shall be before (upstack?) caching - else
        each visitor invocation iwll create new instance
    */
    pico.addAdapter(
        new org.picocontainer.gems.jndi.JNDIExposed(
            new org.picocontainer.behaviors.Cached(
                new org.picocontainer.adapters.InstanceAdapter(
                    "exposedMap",
                    new java.util.HashMap()
                    /*,
                    new org.picocontainer.lifecycle.NullLifecycleStrategy(),
                    new org.picocontainer.monitors.NullComponentMonitor()
                    */
                    )
                ),
                "nano:/blurge/jndiExposedMap" 
            )        
        )

 
    pico.addAdapter(
        new org.picocontainer.gems.jndi.JNDIProvided(
            "java:/Mail"
        )
    )
    
    
    pico.addAdapter(
        new org.nanocontainer.nanosar.SimpleJMXExposed(
            new org.picocontainer.behaviors.Cached(
                 new org.picocontainer.injectors.ConstructorInjector(
                    "JMXExposed",
                    org.nanocontainer.nanosar.example.SimpleBean,
                    null,
                    new org.picocontainer.monitors.NullComponentMonitor(),
                    new org.picocontainer.lifecycle.NullLifecycleStrategy()
                )
            )
        )
    )

