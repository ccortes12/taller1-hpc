package cl.ucn.disc.hpc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The main Class.
 *
 * @author ccort
 */
@Slf4j
public class Main {

    /**
     * The starting point
     * @param args to use
     */
    public static void main(String[] args) throws InterruptedException {

        log.debug("Starting the program...");

        final long start = 2;
        final long end = 5000000;

        final int N = 10;
        int acum = 0;

        List<Long> times = new ArrayList<>();

        for(int i = 0; i<N; i++){
            long time = runInMillis(start, end);
            log.debug("N: {} -> Time: {} ms.",i+1,time);
            times.add(time);
        }

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

        log.debug("Average time is {} ms.",average);

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
     * Time to find the numbers of prime in an interval
     * @param start
     * @param end
     * @return the time in millis
     */
    private static long runInMillis(long start, long end){

        //Stopwatch
        StopWatch sw = StopWatch.createStarted();

        long counter = 0;

        for(long n = start; n <= end; n++){
            if (isPrime(n)){
                counter++;
            }
        }

        log.debug("Existen {} numeros primos entre {} y {}",counter,start,end);

        return sw.getTime(TimeUnit.MILLISECONDS);
    }

}
