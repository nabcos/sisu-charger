package org.sonatype.sisu.charger.internal;

import org.sonatype.sisu.charger.ExceptionHandler;

/**
 * A NOP ExceptionHandler implementation, used by Charger internals (to use this exception handler when there is not a
 * supplied one).
 * 
 * @author cstamas
 */
public class NopExceptionHandler
    implements ExceptionHandler
{
    public static final ExceptionHandler NOOP = new NopExceptionHandler();

    private NopExceptionHandler()
    {
    }

    @Override
    public boolean handle( Exception ex )
    {
        // nothing to handle
        return false;
    }
}
