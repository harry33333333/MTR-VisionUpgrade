package org.mtr.visionupgrade;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mtr.visionupgrade.render.iris.RenderOrderCache;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class IrisRenderOrderCacheTest {

	private static class MockGraph implements RenderOrderCache.Graph {
		private final Map<Object, Map<Object, Integer>> vertices = new HashMap<>();

		public MockGraph(Object... nodes) {
			for (Object node : nodes) {
				vertices.put(node, new HashMap<>());
			}
		}

		public void addEdge(Object from, Object to, int weight) {
			vertices.computeIfAbsent(from, k -> new HashMap<>()).put(to, weight);
		}

		@Override
		public Map<Object, ? extends Map<Object, Integer>> mtr$getVertices() {
			return vertices;
		}
	}

	@Test
	public void testRenderOrderCacheHitAndAsyncFallback() throws InterruptedException {
		RenderOrderCache cache = new RenderOrderCache();

		MockGraph graphA = new MockGraph("node1", "node2");
		graphA.addEdge("node1", "node2", 1);

		AtomicInteger computeCalls = new AtomicInteger(0);

		// First compute (synchronous bootstrap)
		List<?> order1 = cache.getOrCompute(Collections.singletonList(graphA), () -> {
			computeCalls.incrementAndGet();
			return Arrays.asList("node1", "node2");
		});

		Assertions.assertEquals(1, computeCalls.get());
		Assertions.assertEquals(Arrays.asList("node1", "node2"), order1);

		// Second call with same graph (cache hit - no recompute)
		List<?> order2 = cache.getOrCompute(Collections.singletonList(graphA), () -> {
			computeCalls.incrementAndGet();
			return Arrays.asList("node1", "node2");
		});

		Assertions.assertEquals(1, computeCalls.get(), "Cache hit must not call compute");
		Assertions.assertEquals(order1, order2);

		// Third call with new graph (cache miss, but has lastKnownOrder -> returns lastKnownOrder immediately without blocking!)
		MockGraph graphB = new MockGraph("node3", "node4");
		graphB.addEdge("node3", "node4", 2);

		CountDownLatch latch = new CountDownLatch(1);
		List<?> immediateOrder = cache.getOrCompute(Collections.singletonList(graphB), () -> {
			computeCalls.incrementAndGet();
			latch.countDown();
			return Arrays.asList("node3", "node4");
		});

		// Immediate order returned must be previous frame's order (double buffering!)
		Assertions.assertEquals(Arrays.asList("node1", "node2"), immediateOrder);

		// Wait for async background computation to complete
		boolean completed = latch.await(2, TimeUnit.SECONDS);
		Assertions.assertTrue(completed, "Async solver should finish computation");

		// Sleep briefly for volatile cache update
		Thread.sleep(50);

		// Subsequent call after async finish should hit the new cached order
		List<?> updatedOrder = cache.getOrCompute(Collections.singletonList(graphB), () -> {
			computeCalls.incrementAndGet();
			return Arrays.asList("node3", "node4");
		});
		Assertions.assertEquals(Arrays.asList("node3", "node4"), updatedOrder);
	}
}
