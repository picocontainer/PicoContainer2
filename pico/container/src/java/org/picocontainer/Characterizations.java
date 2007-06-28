package org.picocontainer;

public final class Characterizations {

    private static final String _INJECTION = "injection";
    private static final String _CACHE = "cache";
    private static final String _NOJMX = "no-jmx";

    private static final String FALSE = "FALSE";
    private static final String TRUE = "TRUE";

    public static final ComponentCharacteristics CDI = new ComponentCharacteristics() {
        private static final String _CONSTRUCTOR = "constructor";

        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_INJECTION, _CONSTRUCTOR);
        }

        public boolean setAsProcessedIfSoCharacterized(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_INJECTION);
            boolean retVal = s != null && s.equals(_CONSTRUCTOR);
            if (retVal) {
                characteristics.removeProperty(_INJECTION);
            }
            return retVal;

        }
    };

    public static final ComponentCharacteristics SDI = new ComponentCharacteristics() {
        private static final String _SETTER = "setter";

        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_INJECTION, _SETTER);
        }

        public boolean setAsProcessedIfSoCharacterized(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_INJECTION);
            boolean retVal = s != null && s.equals(_SETTER);
            if (retVal) {
                characteristics.removeProperty(_INJECTION);
            }
            return retVal;

        }
    };

    public static final ComponentCharacteristics NOCACHE = new ComponentCharacteristics() {
        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_CACHE, FALSE);
        }

        public boolean setAsProcessedIfSoCharacterized(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_CACHE);
            boolean retVal = s != null && s.equals(FALSE);
            if (retVal) {
                characteristics.removeProperty(_CACHE);
            }
            return retVal;

        }
    };

    public static final ComponentCharacteristics CACHE = new ComponentCharacteristics() {
        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_CACHE, TRUE);
        }

        public boolean setAsProcessedIfSoCharacterized(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_CACHE);
            boolean retVal =  s != null && s.equals(TRUE);
            if (retVal) {
                characteristics.removeProperty(_CACHE);
            }
            return retVal;

        }
    };
    public static final ComponentCharacteristics NOJMX = new ComponentCharacteristics() {
        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_NOJMX, TRUE);
        }

        public boolean setAsProcessedIfSoCharacterized(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_NOJMX);
            boolean retVal = s != null && s.equals(TRUE);
            if (retVal) {
                characteristics.removeProperty(_NOJMX);
            }
            return retVal;

        }
    };
    public static final ComponentCharacteristics THREAD_SAFE = new ComponentCharacteristics() {
        private static final String _THREAD_SAFE = "thread-safe";

        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_THREAD_SAFE, TRUE);
        }

        public boolean setAsProcessedIfSoCharacterized(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_THREAD_SAFE);
            boolean retVal = s != null && s.equals(TRUE);
            if (retVal) {
                characteristics.removeProperty(_THREAD_SAFE);
            }
            return retVal;

        }
    };
    
    public static final ComponentCharacteristics SINGLE = CACHE;
    
    public static final ComponentCharacteristics HIDE = new ComponentCharacteristics() {
        private static final String _HIDE = "hide-implementations";

        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_HIDE, TRUE);
        }

        public boolean setAsProcessedIfSoCharacterized(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_HIDE);
            boolean retVal =  s != null && s.equals(TRUE);
            if (retVal) {
                characteristics.removeProperty(_HIDE);
            }
            return retVal;

        }
    };
}
