package com.webshopping.stats;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

public final class SiteStats {
    private static final AtomicLong startupTimeMillis = new AtomicLong(System.currentTimeMillis());
    private static final AtomicLong onlineVisitorCount = new AtomicLong(0L);
    private static final AtomicLong totalVisitorCount = new AtomicLong(0L);
    private static final AtomicLong totalPageViewCount = new AtomicLong(0L);

    private SiteStats() {
    }

    public static void onStartup() {
        startupTimeMillis.set(System.currentTimeMillis());
        onlineVisitorCount.set(0L);
        totalVisitorCount.set(0L);
        totalPageViewCount.set(0L);
    }

    public static void onSessionCreated() {
        onlineVisitorCount.incrementAndGet();
        totalVisitorCount.incrementAndGet();
    }

    public static void onSessionDestroyed() {
        while (true) {
            long current = onlineVisitorCount.get();
            if (current <= 0) {
                return;
            }
            if (onlineVisitorCount.compareAndSet(current, current - 1)) {
                return;
            }
        }
    }

    public static void onRequest(HttpServletRequest req) {
        if (req == null) {
            return;
        }
        String method = req.getMethod();
        if (!"GET".equalsIgnoreCase(method)) {
            return;
        }
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri == null ? "" : uri;
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        if (shouldCountPageView(path)) {
            totalPageViewCount.incrementAndGet();
        }
    }

    public static long getStartupTimeMillis() {
        return startupTimeMillis.get();
    }

    public static long getOnlineVisitorCount() {
        return onlineVisitorCount.get();
    }

    public static long getTotalVisitorCount() {
        return totalVisitorCount.get();
    }

    public static long getTotalPageViewCount() {
        return totalPageViewCount.get();
    }

    private static boolean shouldCountPageView(String path) {
        if (path == null || path.trim().isEmpty() || "/".equals(path.trim())) {
            return true;
        }
        String normalized = path.trim().toLowerCase(Locale.ROOT);

        if (normalized.endsWith(".css") || normalized.endsWith(".js") || normalized.endsWith(".png")
                || normalized.endsWith(".jpg") || normalized.endsWith(".jpeg") || normalized.endsWith(".gif")
                || normalized.endsWith(".webp") || normalized.endsWith(".ico") || normalized.endsWith(".svg")
                || normalized.endsWith(".woff") || normalized.endsWith(".woff2") || normalized.endsWith(".ttf")
                || normalized.endsWith(".map")) {
            return false;
        }
        return true;
    }
}
