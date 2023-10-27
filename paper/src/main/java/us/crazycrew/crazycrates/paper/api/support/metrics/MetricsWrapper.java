package us.crazycrew.crazycrates.paper.api.support.metrics;

import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class MetricsWrapper {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private Metrics metrics;

    public void start() {
        if (this.metrics != null) {
            if (this.plugin.isLogging()) this.plugin.getLogger().warning("Metrics is already enabled.");
            return;
        }

        this.metrics = new Metrics(this.plugin, 4514);

        this.plugin.getCrateManager().getCrates().forEach(crate -> {
            CrateType crateType = crate.getCrateType();

            SimplePie chart = new SimplePie("crate_types", crateType::getName);

            this.metrics.addCustomChart(chart);
        });

        if (this.plugin.isLogging()) this.plugin.getLogger().fine("Metrics has been enabled.");
    }

    public void stop() {
        if (this.metrics == null) {
            if (this.plugin.isLogging()) this.plugin.getLogger().warning("Metrics isn't enabled so we do nothing.");
            return;
        }

        this.metrics.shutdown();
        this.metrics = null;

        if (this.plugin.isLogging()) this.plugin.getLogger().fine("Metrics has been turned off.");
    }
}