
pico = new org.picocontainer.DefaultPicoContainer()

    pico.addComponent(new java.lang.String("glarch glurch"))
    /*
        note, that JNDIO exposition shall be before (upstack?) cachong - else
        each visitor invocation iwll create new instance
    */
    pico.addAdapter(
        new org.picocontainer.gems.jndi.JNDIExposed(
            new org.picocontainer.behaviors.Cached(
                new org.picocontainer.adapters.InstanceAdapter(
                    "exposedMap",
                    new java.util.HashMap(),
                    new org.picocontainer.lifecycle.NullLifecycleStrategy(),
                    new org.picocontainer.monitors.NullComponentMonitor()
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

