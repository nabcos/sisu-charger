package org.sonatype.sisu.charger;

import java.util.List;
import java.util.concurrent.Callable;

import org.sonatype.sisu.charger.internal.Charge;

/**
 * Simple component to submit Charge instances for execution.
 * 
 * @author cstamas
 */
public interface Charger
{
    /**
     * Handy method to quickly assemble and execute a charge of work, with passed in Callables using passed in Strategy.
     * 
     * @param callables
     * @param strategy
     * @param executorServiceProvider
     * @return
     */
    <E> ChargeFuture<E> submit( List<Callable<E>> callables, ChargeStrategy<E> strategy,
                                CallableExecutor executorServiceProvider );

    /**
     * Handy method to quickly assemble and execute a charge of work, sharing one instance (!) of ExceptionHandler, with
     * passed in Callables using passed in Strategy.
     * 
     * @param callables
     * @param exceptionHandler
     * @param strategy
     * @param executorServiceProvider
     * @return
     */
    <E> ChargeFuture<E> submit( List<Callable<E>> callables, ExceptionHandler exceptionHandler,
                                ChargeStrategy<E> strategy, CallableExecutor executorServiceProvider );

    /**
     * If you crufted manually a Charge instance, just toss it here to start it's execution.
     * 
     * @param charge
     * @param executorServiceProvider
     * @return
     */
    <E> ChargeFuture<E> submit( Charge<E> charge, CallableExecutor executorServiceProvider );
}
