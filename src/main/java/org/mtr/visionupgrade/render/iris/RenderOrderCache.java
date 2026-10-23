package org.mtr.visionupgrade.render.iris;

import java.util.*;
import java.util.function.Supplier;

public final class RenderOrderCache {
	private static final ThreadLocal<RenderOrderCache> ACTIVE = new ThreadLocal<>();
	private static final Set<RenderOrderCache> INSTANCES = Collections.newSetFromMap(new WeakHashMap<>());
	private final GraphResultCache<List<?>> orders;
	private final GraphResultCache<Object> feedbackEdges;

	public RenderOrderCache() {
		this(65536);
	}

	public RenderOrderCache(int maxSize) {
		orders = new GraphResultCache<>(maxSize, 8, maxSize * 4);
		feedbackEdges = new GraphResultCache<>(maxSize, 64, maxSize * 2);
		synchronized (INSTANCES) {
			INSTANCES.add(this);
		}
	}

	private static final java.util.concurrent.ExecutorService ASYNC_SOLVER = java.util.concurrent.Executors.newSingleThreadExecutor(r -> {
		final Thread thread = new Thread(r, "MTR-Iris-Async-Solver");
		thread.setDaemon(true);
		thread.setPriority(Thread.NORM_PRIORITY - 1);
		return thread;
	});

	private volatile List<?> lastKnownOrder;
	private final java.util.concurrent.atomic.AtomicBoolean isSolving = new java.util.concurrent.atomic.AtomicBoolean(false);

	public void clear() {
		orders.clear();
		feedbackEdges.clear();
		lastKnownOrder = null;
	}

	public static void clearAll() {
		ACTIVE.remove();
		synchronized (INSTANCES) {
			for (RenderOrderCache cache : INSTANCES) {
				cache.clear();
			}
		}
	}

	public List<?> getOrCompute(Collection<?> graphs, Supplier<List<?>> compute) {
		// 1. Check if cached order exists (fast path, cache hit)
		final List<?> cached = orders.find(graphs, null, null);
		if (cached != null) {
			lastKnownOrder = cached;
			return new ArrayList<>(cached);
		}

		// 2. Cache miss: if we have a previous frame's valid order, reuse it immediately and solve asynchronously!
		if (lastKnownOrder != null) {
			if (isSolving.compareAndSet(false, true)) {
				ASYNC_SOLVER.execute(() -> {
					try {
						final List<?> computed = computeWithActive(compute);
						if (computed != null) {
							lastKnownOrder = computed;
							orders.getOrCompute(graphs, null, null, () -> computed);
						}
					} catch (Throwable ignored) {
					} finally {
						isSolving.set(false);
					}
				});
			}
			return new ArrayList<>(lastKnownOrder);
		}

		// 3. First frame bootstrap: compute synchronously once
		final List<?> result = computeWithActive(compute);
		lastKnownOrder = result;
		return new ArrayList<>(orders.getOrCompute(graphs, null, null, () -> result));
	}

	private List<?> computeWithActive(Supplier<List<?>> compute) {
		final RenderOrderCache previous = ACTIVE.get();
		ACTIVE.set(this);
		try {
			return new ArrayList<>(compute.get());
		} finally {
			if (previous == null) {
				ACTIVE.remove();
			} else {
				ACTIVE.set(previous);
			}
		}
	}

	public static Object getFeedbackEdges(Object provider, Object graph, Object weights, Supplier<Object> compute) {
		final RenderOrderCache active = ACTIVE.get();
		if (active == null || !(weights instanceof Graph)) {
			return compute.get();
		}
		return active.feedbackEdges.getOrCompute(Collections.singletonList(graph), provider, (Graph) weights, compute);
	}

	public static boolean isActive() {
		return ACTIVE.get() != null;
	}

	public interface Graph {
		Map<Object, ? extends Map<Object, Integer>> mtr$getVertices();
	}
}
