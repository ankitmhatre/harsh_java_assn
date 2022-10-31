public class Simulation {

    /**
     * 
     * Starts the simulation.
     * 
     * @param args are ignored
     * 
     */

    public static void main(String[] args) {

        // how many processes to simulate

        int range = 4;

        // how many rounds so simulate

        int length = 10;

        // the random sequence deciding in which round which process can

        // execute one step

        // you may use this notation to enter a specific sequence:

        // int[] sequence = { 0, 0, 3, 2, 4 };

        int[] sequence = randomSequence(range, length);

        Bus[] buses = new Bus[2];

        for (int i = 0; i < 2; i++) {

            Bus bus = new Bus(range);

            buses[i] = bus;

            Lock[] locks;

            if (i == 0) {

                System.out.println("TAS");

                locks = tas(bus);

            }

            else {

                System.out.println("TTAS");

                locks = ttas(bus);

            }

            print(locks, bus.getCaches());

            for (int j = 0; j < length; j++) {

                System.out.println("[" + sequence[j] + "]");

                locks[sequence[j]].step();

                print(locks, bus.getCaches());

            }

            System.out.println();

            System.out.println();

        }

        for (int i = 0; i < 2; i++) {

            if (i == 0) {

                System.out.println("TAS");

            }

            else {

                System.out.println("TTAS");

            }

            buses[i].printStatistics();

            System.out.println();

        }

    }

    /**
     * 
     * Prints the status of locks and caches.
     * 
     * @param locks  all the locks
     * 
     * @param caches all the caches
     * 
     */

    public static void print(Lock[] locks, Cache[] caches) {

        System.out.println("-----");

        for (Cache cache : caches) {

            System.out.print(cache.toString());

            System.out.print("\t");

        }

        System.out.println();

        for (Lock lock : locks) {

            System.out.print(lock.toString());

            System.out.print("\t");

        }

        System.out.println();

        System.out.println("-----");

    }

    /**
     * 
     * A sequence of random values. The values are in the range 0 (incl.) to
     * 
     * <code>range</code> (excl.).
     * 
     * @param range  how many different random values to use
     * 
     * @param length the length of the sequence
     * 
     * @return the new sequence
     * 
     */

    public static int[] randomSequence(int range, int length) {

        int[] result = new int[length];

        for (int i = 0; i < length; i++) {

            result[i] = (int) (range * Math.random());

        }

        return result;

    }

    /**
     * 
     * Creates a process performing TestAndSet for each {@link Cache} of
     * 
     * <code>bus</code>.
     * 
     * @param bus the computer
     * 
     * @return the processes
     * 
     */

    public static Lock[] tas(Bus bus) {

        Cache[] caches = bus.getCaches();

        Lock[] result = new Lock[caches.length];

        for (int i = 0; i < result.length; i++) {

            result[i] = new TASLock(caches[i]);

        }

        return result;

    }

    /**
     * 
     * Creates a process performing TestAndTestAndSet for each {@link Cache} of
     * 
     * <code>bus</code>.
     * 
     * @param bus the computer
     * 
     * @return the processes
     * 
     */

    public static Lock[] ttas(Bus bus) {

        Cache[] caches = bus.getCaches();

        Lock[] result = new Lock[caches.length];

        for (int i = 0; i < result.length; i++) {

            result[i] = new TTASLock(caches[i]);

        }

        return result;

    }

    /**
     * 
     * Represents a process that wants to acquire a lock.
     * 
     */

    public interface Lock {

        /**
         * 
         * Executes one step in order to acquire the lock. Should the lock
         * 
         * already be acquire, release it.
         * 
         */

        public void step();

    }

    /**
     * 
     * A process performing TestAndSet.
     * 
     */

    public static class TASLock implements Lock {

        /** the shared variable */

        private Cache state;

        /** 0: trying to acquire the lock, 1: lock is acquired */

        private int simulationState = 0;

        public TASLock(Cache cache) {

            this.state = cache;

        }

        @Override

        public String toString() {

            if (simulationState == 0)

                return "acquiring ";

            else

                return "locked ";

        }

        public void step() {

            switch (simulationState) {

                case 0: // lock

                    if (!atomicGetAndSet(state)) {

                        // locked

                        simulationState = 1;

                    }

                    break;

                case 1: // unlock

                    atomicSet(state, false);

                    simulationState = 0;

                    break;

            }

        }

    }

    /**
     * 
     * A process performing TestAndTestAndSet.
     * 
     */

    public static class TTASLock implements Lock {

        /** The shared variable */

        private Cache state;

        /**
         * 0: test whether lock is acquired, 1: try acquire the lock, 2: lock acquired
         */

        private int simulationState = 0;

        public TTASLock(Cache cache) {

            this.state = cache;

        }

        @Override

        public String toString() {

            switch (simulationState) {

                case 0:
                    return "testing ";

                case 1:
                    return "acquiring ";

                case 2:
                    return "locked ";

                default:
                    return null; // never happens

            }

        }

        public void step() {

            switch (simulationState) {

                case 0: // test lock

                    if (!atomicGet(state)) {

                        // the lock is free

                        simulationState = 1;

                    }

                    break;

                case 1: // lock

                    if (!atomicGetAndSet(state)) {

                        // locked

                        simulationState = 2;

                    }

                    else {

                        simulationState = 0;

                    }

                    break;

                case 2: // unlock

                    atomicSet(state, false);

                    simulationState = 0;

                    break;

            }

        }

    }

    /**
     * 
     * Atomically sets the value of <code>state</code> to <code>true</code>
     * 
     * and returns the previous value.<br>
     * 
     * In the context of this application successfully changing the value from
     * 
     * <code>false</code> to <code>true</code> means the lock got acquired.
     * 
     * @param state the variable whose value will be changed atomically.
     * 
     * @return the old value
     * 
     */

    public static boolean atomicGetAndSet(Cache state) {

        int value = state.read();

        state.write(1);

        return value == 1;

    }

    /**
     * 
     * Gets the value of the variable <code>state</code>.
     * 
     * @param state the variable
     * 
     * @return the value
     * 
     */

    public static boolean atomicGet(Cache state) {

        int value = state.read();

        return value == 1;

    }

    /**
     * 
     * Sets the value of variable <code>state</code> without checking
     * 
     * the current value.
     * 
     * @param state the variable
     * 
     * @param value the new value
     * 
     */

    public static void atomicSet(Cache state, boolean value) {

        state.write(value ? 1 : 0);

    }

    /**
     * 
     * The state of a cache line.
     * 
     */

    public static enum State {

        /** The line is not used at all */

        INVALID,

        /** The line can be read, but not written */

        VALID,

        /** The line has been modified and not yet written back to main memory */

        DIRTY

    }

    /**
     * 
     * Main memory of an application.
     * 
     */

    private static class Memory {

        /**
         * 
         * The value of the main memory (in reality this would be several GBs,
         * 
         * not just 32 bit).
         * 
         */

        private int value;

        public void set(int value) {

            this.value = value;

        }

        public int get() {

            return value;

        }

    }

    /**
     * 
     * The cache of a processor.
     * 
     */

    private static class Cache {

        /**
         * 
         * One cache line. In reality a cache has a size of about 1 MB (depending
         * 
         * on the architecture), and a cache line could have a size of 128 KB.
         * 
         */

        private int value = 0;

        /** The state of the one cache line of this cache */

        private State state = State.INVALID;

        /** Access to the bus */

        private Bus bus;

        /** unique identifier for this cache */

        private int id;

        public Cache(Bus bus, int id) {

            this.bus = bus;

            this.id = id;

        }

        @Override

        public String toString() {

            String tail = null;

            switch (state) {

                case DIRTY:

                    tail = "dirty ";

                    break;

                case INVALID:

                    tail = "invalid";

                    break;

                case VALID:

                    tail = "valid ";

                    break;

            }

            return value + " - " + tail;

        }

        public int getId() {

            return id;

        }

        /**
         * 
         * Reads the single cache line of this cache. Loads the line from
         * 
         * main memory if necessary.
         * 
         * @return the cache line
         * 
         */

        public int read() {

            switch (state) {

                case DIRTY:

                case VALID:

                    return value;

                case INVALID:

                    bus.requestLoad(this);

                    state = State.VALID;

                    return value;

                default:

                    return -1; // that never happens

            }

        }

        /**
         * 
         * Writes the single cache line of this cache. Invalidates other
         * 
         * caches if necessary.
         * 
         * @param value the new value
         * 
         */

        public void write(int value) {

            switch (state) {

                case DIRTY:

                    this.value = value;

                    break;

                case VALID:

                case INVALID:

                    bus.invalidateOthers(this);

                    state = State.DIRTY;

                    this.value = value;

                    break;

            }

        }

        public void set(int value) {

            this.value = value;

        }

        public int get() {

            return value;

        }

        public void invalidate() {

            state = State.INVALID;

        }

        public void requestLoad() {

            if (state == State.DIRTY) {

                bus.set(this);

                state = State.VALID;

            }

        }

    }

    /**
     * 
     * The bus connecting caches and main memory.
     * 
     */

    private static class Bus {

        private Memory memory;

        private Cache[] caches;

        private int countMessagesOnBus = 0;

        private int countMemoryToCache = 0;

        private int countCacheToMemory = 0;

        public Bus(int size) {

            memory = new Memory();

            caches = new Cache[size];

            for (int i = 0; i < size; i++) {

                caches[i] = new Cache(this, i);

            }

        }

        public void printStatistics() {

            System.out.println("#messages on bus = " + countMessagesOnBus);

            System.out.println("#memory to cache = " + countMemoryToCache);

            System.out.println("#cache to memory = " + countCacheToMemory);

        }

        public void set(Cache caller) {

            System.out.println("cache(" + caller.getId() + ") -> memory: " + caller.get());

            countCacheToMemory++;

            memory.set(caller.get());

        }

        public void requestLoad(Cache caller) {

            System.out.println(caller.getId() + ": request load");

            countMessagesOnBus++;

            for (Cache cache : caches) {

                if (cache != caller) {

                    cache.requestLoad();

                }

            }

            System.out.println("memory -> cache(" + caller.getId() + "): " + memory.get());

            countMemoryToCache++;

            caller.set(memory.get());

        }

        /**
         * 
         * Invalidates all the caches except <code>caller</code>.
         * 
         * @param caller the one protected cache
         * 
         */

        public void invalidateOthers(Cache caller) {

            System.out.println(caller.getId() + ": invalidate others");

            countMessagesOnBus++;

            for (Cache cache : caches) {

                if (cache != caller) {

                    cache.invalidate();

                }

            }

        }

        public Cache[] getCaches() {

            return caches;

        }

    }

}