pico = builder.container(parent: parent) {
	if(assemblyScope instanceof javax.servlet.ServletContext) {
		component(key: 'org.nanocontainer.nanowar.sample.dao.CheeseDao', class: 'org.nanocontainer.nanowar.sample.dao.simple.MemoryCheeseDao')
		component(key: 'org.nanocontainer.nanowar.sample.service.CheeseService', class: 'org.nanocontainer.nanowar.sample.service.defaults.DefaultCheeseService')
	} else if (assemblyScope instanceof javax.servlet.ServletRequest) {
		component(key: 'cheeseBean', class: 'org.nanocontainer.nanowar.samples.jsf.ListCheeseController')
		component(key: 'addCheeseBean', class: 'org.nanocontainer.nanowar.samples.jsf.AddCheeseController')
	}
}