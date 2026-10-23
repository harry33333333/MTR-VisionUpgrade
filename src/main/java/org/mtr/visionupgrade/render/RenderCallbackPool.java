package org.mtr.visionupgrade.render;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.mapper.GraphicsHolder;

import java.util.ArrayDeque;
import java.util.function.BiConsumer;

public final class RenderCallbackPool {
	public static final RenderCallbackPool INSTANCE = new RenderCallbackPool();

	/**
	 * Maximum number of pooled lists retained.
	 */
	private static final int MAX_LISTS = 128;

	/**
	 * Target capacity to retain per list. Lists larger than this will be trimmed.
	 */
	private static final int TARGET_MAX_LIST_CAPACITY = 1024;

	/**
	 * Hard maximum capacity beyond which a list is discarded rather than retained in the pool.
	 */
	private static final int HARD_MAX_LIST_CAPACITY = 8192;

	/**
	 * Maximum cumulative capacity across all pooled lists.
	 */
	private static final int MAX_TOTAL_CAPACITY = 65536;

	private final ArrayDeque<ObjectArrayList<BiConsumer<GraphicsHolder, Vector3d>>> lists = new ArrayDeque<>();
	private int retainedCapacity;

	private RenderCallbackPool() {
	}

	public synchronized ObjectArrayList<BiConsumer<GraphicsHolder, Vector3d>> acquire() {
		final ObjectArrayList<BiConsumer<GraphicsHolder, Vector3d>> list = lists.pollFirst();
		if (list == null) {
			return new ObjectArrayList<>();
		}
		retainedCapacity -= capacity(list);
		return list;
	}

	public synchronized void recycle(ObjectArrayList<BiConsumer<GraphicsHolder, Vector3d>> list) {
		if (list == null) {
			return;
		}
		// Null out callback references so lambdas/buffers/entities can be collected by Young GC immediately
		list.clear();

		int capacity = capacity(list);
		if (capacity > HARD_MAX_LIST_CAPACITY) {
			// Discard abnormally huge lists from extreme stress situations
			return;
		}

		if (capacity > TARGET_MAX_LIST_CAPACITY) {
			// High watermark trimming: shrink array back to sensible size
			list.trim(TARGET_MAX_LIST_CAPACITY);
			capacity = capacity(list);
		}

		if (capacity > 0 && retainedCapacity + capacity <= MAX_TOTAL_CAPACITY && lists.size() < MAX_LISTS) {
			lists.addLast(list);
			retainedCapacity += capacity;
		}
	}

	public synchronized void trim(int targetMaxTotalCapacity) {
		while (retainedCapacity > targetMaxTotalCapacity && !lists.isEmpty()) {
			final ObjectArrayList<BiConsumer<GraphicsHolder, Vector3d>> removed = lists.pollLast();
			if (removed != null) {
				retainedCapacity -= capacity(removed);
			}
		}
	}

	public synchronized void clear() {
		for (ObjectArrayList<BiConsumer<GraphicsHolder, Vector3d>> list : lists) {
			list.clear();
			list.trim(0);
		}
		lists.clear();
		retainedCapacity = 0;
	}

	public synchronized int getPoolSize() {
		return lists.size();
	}

	public synchronized int getRetainedCapacity() {
		return retainedCapacity;
	}

	private static int capacity(ObjectArrayList<?> list) {
		return list.elements().length;
	}
}
