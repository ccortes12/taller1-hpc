package cl.ucn.disc.hpc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class to find primal numbers
 */
@Slf4j
public final class Parallel {

    /**
     * The counter of primal numbers
     */
    private static final AtomicInteger counter = new AtomicInteger(0);

    /**
     * The main
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        log.debug("Program initialized...");

        final long start = 2;
        final long end = 5000000;
        final int cores = 4;

        List<Long> times = new ArrayList<>();

        for(int i=1;  i <= cores; i++){

            //Reset the counter
            counter.set(0);

            long time = findPrimes(start,end,i);

            log.debug("N° cores: {} -> Time: {} ms.",i,time);
            times.add(time);
        }
        /*
        long min = Collections.min(times);
        log.debug("The min value is {} ms.",min);
        times.remove(min);

        long max = Collections.max(times);
        log.debug("The max value is {} ms.",max);
        times.remove(max);

        double average = times
                .stream()
                .mapToLong(n -> n)
                .average()
                .getAsDouble();

        log.debug("Average time is {} ms.",average);*/

    }

    /**
     * Detect if a number is prime
     * @param n
     * @return
     */
    public static boolean isPrime(final long n) {

        //No prime
        if (n <= 0) {
            throw new IllegalArgumentException("Error in n: can't process negative numbers");
        }

        //1 isn't prime
        if (n == 1) {
            return false;
        }

        //Any number %2 -> false
        if(n%2 == 0){
            return false;
        }

        //Testing the primality
        for(long i = 3 ; (i*i) <= n; i+=2){
            //n is divisible by i
            if(n%i == 0){
                return false;
            }
        }

        return true;
    }

    /**
     * Find the primes with cores
     * @param ini
     * @param end
     * @param cores
     * @return
     */
    public static long findPrimes(long ini, long end, int cores) throws InterruptedException {

        final StopWatch sw = StopWatch.createStarted();

        //The Executor of Threads
        final ExecutorService executor = Executors.newFixedThreadPool(cores);

        for(long i = ini; i <= end; i++){

            //The number of test
            final long n = i;

            //Exe the callable
            executor.submit(() -> {

                if(isPrime(n)){
                    counter.incrementAndGet();
                }

            });


        }

        executor.shutdown();

        int maxTime = 5;
        if(executor.awaitTermination(5, TimeUnit.MINUTES)){
            log.info("Founded {} primes in {} ms.",counter, sw.getTime(TimeUnit.MILLISECONDS));
            return sw.getTime(TimeUnit.MILLISECONDS);
        }

        throw new RuntimeException("The program doesn't work!!");

    }
}
