package org.sonatype.sisu.charger.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.sonatype.sisu.charger.ChargeStrategy;

public abstract class AbstractChargeStrategy<E>
    implements ChargeStrategy<E>
{
    protected E getFutureResult( final ChargeWrapper<E> wrapper )
        throws Exception
    {
        try
        {
            return wrapper.getFuture().get();
        }
        catch ( ExecutionException e )
        {
            if ( e.getCause() instanceof InterruptedException )
            {
                // we bailed out, just ignore it then
            }
            else if ( e.getCause() instanceof Exception )
            {
                final Exception cause = (Exception) e.getCause();

                if ( !wrapper.handle( cause ) )
                {
                    throw cause;
                }
            }
            else
            {
                throw new RuntimeException( e.getCause() );
            }
        }
        catch ( CancellationException e )
        {
            // we ignore this
        }
        catch ( InterruptedException e )
        {
            // we ignore this
        }

        return null;
    }

    protected List<E> getAllResults( final Charge<E> charge )
        throws Exception
    {
        final List<ChargeWrapper<E>> ammo = charge.getAmmoFutures();

        final ArrayList<E> result = new ArrayList<E>( ammo.size() );

        for ( ChargeWrapper<E> f : ammo )
        {
            E e = getFutureResult( f );

            if ( e != null )
            {
                result.add( e );
            }
        }

        return result;
    }

    // ==

    @Override
    public abstract boolean isDone( Charge<E> charge, ChargeWrapper<E> wrapper );

    @Override
    public abstract List<E> getResult( Charge<E> charge )
        throws Exception;

}
