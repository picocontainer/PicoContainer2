Nano Remoting is a component for NanoContainer that can make simple POJOs remotely accessible without the need to deal with the underlying transport API (RMI, etc.). These classes don't even have to extend java.io.Serializable or java.rmi.Remote!

With Nano Remoting you can control on a per-component level what components should be used by reference and what should be used by value.

== SERVER SIDE CONFIGURATION ==

The components should be registered in a PicoContainer on the server in the usual way. There are three things to be aware of:

1) Server side components that you want to use by reference on the client must be proxiable. That means, the proxy factory you use must be able to create a proxy for the real subject. The CGLIBProxyFactory lets you proxy concrete classes without the need for interfaces. The StandardProxyFactory requires [Interface-Implementation Separation]. There is no need at all to implement java.io.Serializable or java.net.Remote!

2) Server side components that you want to be able to use by reference on the client must be registered with a special ByRefKey. Example:

        // Configure server side components
        pico = new DefaultPicoContainer();
        thingKey = new ByRefKey("thing");
        thangKey = new ByRefKey("thang");
        pico.registerComponentImplementation(thingKey, Thing.class);
        pico.registerComponentImplementation(thangKey, Thang.class);
        pico.registerComponentImplementation(ArrayList.class);


	// Configure and bind the lookup service
	ProxyFactory proxyFactory = new CGLIBProxyFactory();
	// ProxyFactory proxyFactory = new StandardProxyFactory();
        NanoNamingImpl nanoNaming = new NanoNamingImpl(RegistryHelper.getRegistry(), pico, proxyFactory);
        nanoNaming.bind("nanonaming");
		
== CLIENT SIDE CONFIGURATION ==

Once the components are registered on the server like this it is really easy to look them up and start using them:

        naming = (NanoNaming) Naming.lookup("rmi://localhost:9877/nanonaming");
        Thing thing = (Thing) naming.lookup("thing");
        // This will give us a thang that is a reference to the one on the server.
        Thang thang = thing.getThang();

In most cases you will only need to do one lookup like this, and then you can reach other remote objects from the "root" object. -Either by reference or by value, depending on how they were registered on the server.

== HOW IT WORKS ==

*** Return values ***

If you now call a method on thing that returns an object, you will get another object that represents a reference to a remote object provided that:

a) The object returned by the component on the server is another component in the container (or in one of its parent containers)
b) The object returned by the component on the server was also registered with a ByRefKey.

If a) or b) doesn't hold true, you will get the object by value. This will of course require that the object is Serializable.

*** Parameters ***

If, on the client you call a method on a remote proxy, passing another remote proxy as method parameter(s), it/they will be unwrapped properly on the server side.
