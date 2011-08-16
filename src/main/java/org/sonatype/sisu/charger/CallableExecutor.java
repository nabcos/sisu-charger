package org.sonatype.sisu.charger;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface CallableExecutor
{
    <T> Future<T> submit( Callable<T> task );
}
