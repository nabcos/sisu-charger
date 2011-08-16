package org.sonatype.sisu.charger.internal;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.charger.ChargeFuture;
import org.sonatype.sisu.charger.ChargeStrategy;
import org.sonatype.sisu.charger.Charger;
import org.sonatype.sisu.charger.ExceptionHandler;
import org.sonatype.sisu.charger.CallableExecutor;

import com.google.common.base.Preconditions;

@Singleton
@Named
public class DefaultCharger
    implements Charger
{
    public <E> ChargeFuture<E> submit( final List<Callable<E>> callables, final ChargeStrategy<E> strategy,
                                       final CallableExecutor executorServiceProvider )
    {
        Preconditions.checkNotNull( callables );

        Charge<E> charge = getChargeInstance( strategy );

        for ( Callable<? extends E> callable : callables )
        {
            charge.addAmmo( callable, ( callable instanceof ExceptionHandler ) ? (ExceptionHandler) callable
                : NopExceptionHandler.NOOP );
        }

        return submit( charge, executorServiceProvider );
    }

    public <E> ChargeFuture<E> submit( final List<Callable<E>> callables, final ExceptionHandler exceptionHandler,
                                       final ChargeStrategy<E> strategy,
                                       final CallableExecutor executorServiceProvider )
    {
        Preconditions.checkNotNull( callables );

        Charge<E> charge = getChargeInstance( strategy );

        for ( Callable<? extends E> callable : callables )
        {
            charge.addAmmo( callable, exceptionHandler );
        }

        return submit( charge, executorServiceProvider );
    }

    public <E> ChargeFuture<E> submit( Charge<E> charge, final CallableExecutor callableExecutor )
    {
        Preconditions.checkNotNull( charge );

        charge.exec( callableExecutor );

        return new DefaultChargeFuture<E>( charge );
    }

    // ==

    protected <E> Charge<E> getChargeInstance( final ChargeStrategy<E> strategy )
    {
        return new Charge<E>( strategy );
    }
}
