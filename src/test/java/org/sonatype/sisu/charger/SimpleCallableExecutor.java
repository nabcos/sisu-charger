package org.sonatype.sisu.charger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SimpleCallableExecutor
    implements CallableExecutor
{
    private final ExecutorService executorService;

    public SimpleCallableExecutor()
    {
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    public <T> Future<T> submit( Callable<T> task )
    {
        return executorService.submit( task );
    }
}
