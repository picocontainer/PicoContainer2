pico = builder.container(parent: parent) {
	if(assemblyScope instanceof javax.servlet.ServletContext) {
		component(key: 'org.picocontainer.web.sample.dao.CheeseDao', class: 'org.picocontainer.web.sample.dao.simple.MemoryCheeseDao')
		component(key: 'org.picocontainer.web.sample.service.CheeseService', class: 'org.picocontainer.web.sample.service.defaults.DefaultCheeseService')
	} else if (assemblyScope instanceof javax.servlet.ServletRequest) {
		component(key: 'cheeseBean', class: 'org.picocontainer.web.samples.jsf.ListCheeseController')
		component(key: 'addCheeseBean', class: 'org.picocontainer.web.samples.jsf.AddCheeseController')
	}
}