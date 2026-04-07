package io.github.bakedlibs.dough.scheduling;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * A utility class that provides Folia-compatible scheduling methods.
 * Automatically detects whether the server is running Folia or Paper/Spigot
 * and uses the appropriate scheduler.
 * 
 * @author Dough Community
 */
public final class FoliaScheduler {

    private static final boolean IS_FOLIA;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        IS_FOLIA = folia;
    }

    private FoliaScheduler() {
        // Utility class
    }

    /**
     * Check if the server is running Folia.
     * 
     * @return true if running Folia, false otherwise
     */
    public static boolean isFolia() {
        return IS_FOLIA;
    }

    // ─────────────────────────────────────────────
    // Async Scheduling (works on both Folia and Paper)
    // ─────────────────────────────────────────────

    /**
     * Runs a task asynchronously.
     * 
     * @param plugin   The plugin
     * @param runnable The task to run
     */
    public static void runAsync(@Nonnull Plugin plugin, @Nonnull Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getAsyncScheduler().runNow(plugin, task -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }
    }

    /**
     * Runs a task asynchronously after a delay.
     * 
     * @param plugin   The plugin
     * @param runnable The task to run
     * @param delay    The delay in ticks
     */
    public static void runAsyncDelayed(@Nonnull Plugin plugin, @Nonnull Runnable runnable, long delay) {
        if (IS_FOLIA) {
            long delayMs = delay * 50L;
            Bukkit.getAsyncScheduler().runDelayed(plugin, task -> runnable.run(), delayMs, TimeUnit.MILLISECONDS);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
        }
    }

    /**
     * Runs a task asynchronously repeatedly.
     * 
     * @param plugin   The plugin
     * @param runnable The task to run
     * @param delay    The initial delay in ticks
     * @param period   The period between executions in ticks
     * @return A ScheduledTask that can be cancelled
     */
    @Nonnull
    public static ScheduledTask runAsyncRepeating(@Nonnull Plugin plugin, @Nonnull Runnable runnable, long delay, long period) {
        if (IS_FOLIA) {
            long delayMs = delay * 50L;
            long periodMs = period * 50L;
            io.papermc.paper.threadedregions.scheduler.ScheduledTask task = 
                Bukkit.getAsyncScheduler().runAtFixedRate(plugin, t -> runnable.run(), delayMs, periodMs, TimeUnit.MILLISECONDS);
            return new ScheduledTask(task);
        } else {
            int taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period).getTaskId();
            return new ScheduledTask(taskId);
        }
    }

    // ─────────────────────────────────────────────
    // Global/Sync Scheduling
    // ─────────────────────────────────────────────

    /**
     * Runs a task on the global region (Folia) or main thread (Paper).
     * 
     * @param plugin   The plugin
     * @param runnable The task to run
     */
    public static void runGlobal(@Nonnull Plugin plugin, @Nonnull Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().run(plugin, task -> runnable.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    /**
     * Runs a task on the global region (Folia) or main thread (Paper) after a delay.
     * 
     * @param plugin   The plugin
     * @param runnable The task to run
     * @param delay    The delay in ticks
     */
    public static void runGlobalDelayed(@Nonnull Plugin plugin, @Nonnull Runnable runnable, long delay) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), delay);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }

    /**
     * Runs a task on the global region (Folia) or main thread (Paper) repeatedly.
     * 
     * @param plugin   The plugin
     * @param runnable The task to run
     * @param delay    The initial delay in ticks
     * @param period   The period between executions in ticks
     * @return A ScheduledTask that can be cancelled
     */
    @Nonnull
    public static ScheduledTask runGlobalRepeating(@Nonnull Plugin plugin, @Nonnull Runnable runnable, long delay, long period) {
        if (IS_FOLIA) {
            io.papermc.paper.threadedregions.scheduler.ScheduledTask task = 
                Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> runnable.run(), delay, period);
            return new ScheduledTask(task);
        } else {
            int taskId = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period).getTaskId();
            return new ScheduledTask(taskId);
        }
    }

    // ─────────────────────────────────────────────
    // Location-based Scheduling (Region-safe for Folia)
    // ─────────────────────────────────────────────

    /**
     * Runs a task at a specific location (region-safe for Folia).
     * 
     * @param plugin   The plugin
     * @param location The location
     * @param runnable The task to run
     */
    public static void runAtLocation(@Nonnull Plugin plugin, @Nonnull Location location, @Nonnull Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getRegionScheduler().run(plugin, location, task -> runnable.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    /**
     * Runs a task at a specific location (region-safe for Folia) after a delay.
     * 
     * @param plugin   The plugin
     * @param location The location
     * @param runnable The task to run
     * @param delay    The delay in ticks
     */
    public static void runAtLocationDelayed(@Nonnull Plugin plugin, @Nonnull Location location, @Nonnull Runnable runnable, long delay) {
        if (IS_FOLIA) {
            Bukkit.getRegionScheduler().runDelayed(plugin, location, task -> runnable.run(), delay);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }

    // ─────────────────────────────────────────────
    // Entity-based Scheduling (Entity-safe for Folia)
    // ─────────────────────────────────────────────

    /**
     * Runs a task for a specific entity (entity-safe for Folia).
     * 
     * @param plugin   The plugin
     * @param entity   The entity
     * @param runnable The task to run
     * @param retired  The task to run if the entity is retired (Folia only, can be null)
     */
    public static void runAtEntity(@Nonnull Plugin plugin, @Nonnull Entity entity, @Nonnull Runnable runnable, @Nullable Runnable retired) {
        if (IS_FOLIA) {
            entity.getScheduler().run(plugin, task -> runnable.run(), retired);
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    /**
     * Runs a task for a specific entity (entity-safe for Folia) after a delay.
     * 
     * @param plugin   The plugin
     * @param entity   The entity
     * @param runnable The task to run
     * @param retired  The task to run if the entity is retired (Folia only, can be null)
     * @param delay    The delay in ticks
     */
    public static void runAtEntityDelayed(@Nonnull Plugin plugin, @Nonnull Entity entity, @Nonnull Runnable runnable, @Nullable Runnable retired, long delay) {
        if (IS_FOLIA) {
            entity.getScheduler().runDelayed(plugin, task -> runnable.run(), retired, delay);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }

    // ─────────────────────────────────────────────
    // ScheduledTask wrapper
    // ─────────────────────────────────────────────

    /**
     * A wrapper class for scheduled tasks that provides a unified cancellation interface.
     */
    public static class ScheduledTask {
        private final Object task;
        private final boolean isFoliaTask;

        ScheduledTask(io.papermc.paper.threadedregions.scheduler.ScheduledTask foliaTask) {
            this.task = foliaTask;
            this.isFoliaTask = true;
        }

        ScheduledTask(int bukkitTaskId) {
            this.task = bukkitTaskId;
            this.isFoliaTask = false;
        }

        /**
         * Cancels the scheduled task.
         */
        public void cancel() {
            if (isFoliaTask) {
                ((io.papermc.paper.threadedregions.scheduler.ScheduledTask) task).cancel();
            } else {
                Bukkit.getScheduler().cancelTask((Integer) task);
            }
        }

        /**
         * Checks if the task is cancelled.
         * 
         * @return true if cancelled
         */
        public boolean isCancelled() {
            if (isFoliaTask) {
                return ((io.papermc.paper.threadedregions.scheduler.ScheduledTask) task).isCancelled();
            } else {
                return !Bukkit.getScheduler().isQueued((Integer) task) && !Bukkit.getScheduler().isCurrentlyRunning((Integer) task);
            }
        }
    }
}
