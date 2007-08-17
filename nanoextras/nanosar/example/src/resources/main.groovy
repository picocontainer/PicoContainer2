
pico = new org.picocontainer.DefaultPicoContainer()

    pico.addComponent(new java.lang.String("glarch glurch"))

    pico.addAdapter(
        new org.picocontainer.behaviors.Cached(
            new org.picocontainer.gems.jndi.JNDIExposed(
                new org.picocontainer.adapters.InstanceAdapter(
                    "exposedMap",
                    new java.lang.String("foo bar"),
                    new org.picocontainer.lifecycle.NullLifecycleStrategy(),
                    new org.picocontainer.monitors.NullComponentMonitor()
                ),
                "nano:/blurge/jndiExposedMap" 
            )
        )
    )

 
    pico.addAdapter(
        new org.picocontainer.gems.jndi.JNDIProvided(
            "java:/DefaultDS"
        )
    )

