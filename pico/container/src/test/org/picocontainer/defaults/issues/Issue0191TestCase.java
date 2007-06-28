package org.picocontainer.defaults.issues;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.DefaultPicoContainer;

public final class Issue0191TestCase extends TestCase {

    static int sharkCount = 0 ;
    static int codCount = 0 ;

    /*
      This bug as descripbed in the bug report, cannot be reproduced. Needs work.
    */
    public void testTheBug()
    {
        MutablePicoContainer pico = new DefaultPicoContainer( ) ;
        pico.addComponent(Shark.class);
        pico.addComponent(Cod.class);
        try {
            pico.addComponent(Bowl.class);
            Bowl bowl = pico.getComponent(Bowl.class);
            fail("Should have barfed here with UnsatisfiableDependenciesException");
            Fish[] fishes = bowl.getFishes( ) ;
            for( int i = 0 ; i < fishes.length ; i++ )
                System.out.println( "fish["+i+"]="+fishes[i] ) ;
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            // expected, well except that there is supposed to be a different bug here.
        }
    }


     class Bowl
    {
        private final Fish[] fishes;
        private final Cod[] cods;
        public Bowl(Fish[] fishes, Cod[] cods)
        {
            this.fishes = fishes;
            this.cods = cods;
        }
        public Fish[] getFishes()
        {
            return fishes;
        }
        public Cod[] getCods()
        {
            return cods;
        }

    }

    public interface Fish
    {
    }

    final class Cod implements Fish
    {
        final int instanceNum ;
        public Cod( ) { instanceNum = codCount++ ; }

        public String toString( ) {
            return "Cod #" + instanceNum ;
        }
    }

    final class Shark implements Fish
    {
        final int instanceNum ;
        public Shark( ) { instanceNum = sharkCount++ ; }

        public String toString( ) {
            return "Shark #" + instanceNum ;
        }
    }

}
