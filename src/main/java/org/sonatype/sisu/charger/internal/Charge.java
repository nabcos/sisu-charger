package org.sonatype.sisu.charger.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.sonatype.sisu.charger.CallableExecutor;
import org.sonatype.sisu.charger.ChargeStrategy;
import org.sonatype.sisu.charger.ExceptionHandler;

import com.google.common.base.Preconditions;

/**
 * Amount of parallel workload.
 * 
 * @author cstamas
 * @param <E>
 */
public class Charge<E>
{
    private final List<ChargeWrapper<E>> ammunition;

    private final ChargeStrategy<E> strategy;

    private volatile boolean done;

    public Charge( final ChargeStrategy<E> strategy )
    {
        this.strategy = Preconditions.checkNotNull( strategy );

        this.ammunition = new ArrayList<ChargeWrapper<E>>();
    }

    public void addAmmo( final Callable<? extends E> callable, final ExceptionHandler exceptionHandler )
    {
        ammunition.add( new ChargeWrapper<E>( this, callable, exceptionHandler ) );
    }

    public List<ChargeWrapper<E>> getAmmoFutures()
    {
        return ammunition;
    }

    public synchronized void exec( final CallableExecutor runner )
    {
        for ( ChargeWrapper<E> ammo : ammunition )
        {
            ammo.setFuture( runner.submit( ammo ) );
        }
    }

    public boolean cancel( final boolean mayInterruptIfRunning )
    {
        if ( isDone() )
        {
            return false;
        }
        else
        {
            for ( ChargeWrapper<E> wrapper : ammunition )
            {
                wrapper.getFuture().cancel( mayInterruptIfRunning );
            }

            return true;
        }
    }

    public boolean isDone()
    {
        return done;
    }

    public List<E> getResult()
        throws Exception
    {
        return strategy.getResult( this );
    }

    public synchronized void checkIsDone( final ChargeWrapper<E> wrapper )
    {
        if ( strategy.isDone( this, wrapper ) )
        {
            done = true;
        }
    }
}
