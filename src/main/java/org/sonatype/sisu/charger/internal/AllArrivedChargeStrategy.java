package org.sonatype.sisu.charger.internal;

import java.util.List;

/**
 * Strategy that will ensure all the payloads are here, are bailed out, or did fail.
 * 
 * @author cstamas
 * @param <E>
 */
public class AllArrivedChargeStrategy<E>
    extends AbstractChargeStrategy<E>
{
    @Override
    public boolean isDone( final Charge<E> charge, final ChargeWrapper<E> wrapper )
    {
        // done if all done, otherwise not
        List<ChargeWrapper<E>> ammoFutures = charge.getAmmoFutures();

        for ( ChargeWrapper<E> a : ammoFutures )
        {
            if ( !a.getFuture().isDone() )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<E> getResult( final Charge<E> charge )
        throws Exception
    {
        return getAllResults( charge );
    }
}
