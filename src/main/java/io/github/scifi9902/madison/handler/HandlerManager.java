package io.github.scifi9902.madison.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerManager {

    private final Map<Class<? extends IHandler>, IHandler> map = new HashMap<>();

    public <T> T getHandler(Class<T> tClass) {
        return (T) map.get(tClass);
    }

    public void registerHandler(IHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("Handler that you attempted to register is null");
        }

        map.put(handler.getClass(), handler);
        handler.load();
    }

    public Collection<IHandler> getHandlers() {
        return this.map.values();
    }

}
