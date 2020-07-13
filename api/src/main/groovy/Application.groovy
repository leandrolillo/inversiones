package inversiones

import io.micronaut.runtime.Micronaut
import groovy.transform.CompileStatic

@CompileStatic
class Application {
    static void main(String[] args) {
        //Micronaut.run(Application)
        Micronaut.build(args)
                .packages("cotizaciones.domain", "inversiones.domain")
                .mainClass(Application.class)
                .start()
    }
}
