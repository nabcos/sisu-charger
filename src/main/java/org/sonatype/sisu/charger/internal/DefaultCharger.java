package org.sonatype.sisu.charger.internal;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.charger.ChargeFuture;
import org.sonatype.sisu.charger.ChargeStrategy;
import org.sonatype.sisu.charger.Charger;
import org.sonatype.sisu.charger.ExceptionHandler;

import com.google.common.base.Preconditions;

@Singleton
@Named
public class DefaultCharger
    implements Charger
{
    private ExecutorService executorService;

    public DefaultCharger()
    {
        this( Executors.defaultThreadFactory() );
    }

    public DefaultCharger( final ThreadFactory threadFactory )
    {
        this.executorService = Executors.newCachedThreadPool( Preconditions.checkNotNull( threadFactory ) );
    }

    public DefaultCharger( final ExecutorService executorService )
    {
        this.executorService = Preconditions.checkNotNull( executorService );
    }

    public <E> ChargeFuture<E> submit( final List<Callable<E>> callables, final ChargeStrategy<E> strategy )
    {
        Preconditions.checkNotNull( callables );

        Charge<E> charge = getChargeInstance( strategy );

        for ( Callable<? extends E> callable : callables )
        {
            charge.addAmmo( callable, ( callable instanceof ExceptionHandler ) ? (ExceptionHandler) callable
                : NopExceptionHandler.NOOP );
        }

        return submit( charge );
    }

    public <E> ChargeFuture<E> submit( final List<Callable<E>> callables, final ExceptionHandler exceptionHandler,
                                       final ChargeStrategy<E> strategy )
    {
        Preconditions.checkNotNull( callables );

        Charge<E> charge = getChargeInstance( strategy );

        for ( Callable<? extends E> callable : callables )
        {
            charge.addAmmo( callable, exceptionHandler );
        }

        return submit( charge );
    }

    public <E> ChargeFuture<E> submit( Charge<E> charge )
    {
        Preconditions.checkNotNull( charge );

        charge.exec( executorService );

        return new DefaultChargeFuture<E>( charge );
    }

    public void shutdown()
    {
        executorService.shutdownNow();
    }

    // ==

    protected <E> Charge<E> getChargeInstance( final ChargeStrategy<E> strategy )
    {
        return new Charge<E>( strategy );
    }
}
