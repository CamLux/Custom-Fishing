/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customfishing.api.mechanic.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.utils.format.NodeRole;
import net.momirealms.customfishing.api.BukkitCustomFishingPlugin;
import net.momirealms.customfishing.api.mechanic.block.BlockConfig;
import net.momirealms.customfishing.api.mechanic.config.function.*;
import net.momirealms.customfishing.api.mechanic.context.Context;
import net.momirealms.customfishing.api.mechanic.effect.Effect;
import net.momirealms.customfishing.api.mechanic.effect.EffectModifier;
import net.momirealms.customfishing.api.mechanic.effect.LootBaseEffect;
import net.momirealms.customfishing.api.mechanic.entity.EntityConfig;
import net.momirealms.customfishing.api.mechanic.event.EventCarrier;
import net.momirealms.customfishing.api.mechanic.hook.HookConfig;
import net.momirealms.customfishing.api.mechanic.loot.Loot;
import net.momirealms.customfishing.api.mechanic.loot.operation.WeightOperation;
import net.momirealms.customfishing.api.mechanic.requirement.Requirement;
import net.momirealms.customfishing.api.mechanic.totem.TotemConfig;
import net.momirealms.customfishing.common.config.ConfigLoader;
import net.momirealms.customfishing.common.config.node.Node;
import net.momirealms.customfishing.common.item.Item;
import net.momirealms.customfishing.common.plugin.feature.Reloadable;
import net.momirealms.customfishing.common.util.Pair;
import net.momirealms.customfishing.common.util.TriConsumer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ConfigManager implements ConfigLoader, Reloadable {

    private static ConfigManager instance;
    protected final BukkitCustomFishingPlugin plugin;
    protected final HashMap<String, Node<ConfigParserFunction>> entityFormatFunctions = new HashMap<>();
    protected final HashMap<String, Node<ConfigParserFunction>> blockFormatFunctions = new HashMap<>();
    protected final HashMap<String, Node<ConfigParserFunction>> totemFormatFunctions = new HashMap<>();
    protected final HashMap<String, Node<ConfigParserFunction>> hookFormatFunctions = new HashMap<>();
    protected final HashMap<String, Node<ConfigParserFunction>> eventFormatFunctions = new HashMap<>();
    protected final HashMap<String, Node<ConfigParserFunction>> baseEffectFormatFunctions = new HashMap<>();
    protected final HashMap<String, Node<ConfigParserFunction>> effectModifierFormatFunctions = new HashMap<>();
    protected final HashMap<String, Node<ConfigParserFunction>> itemFormatFunctions = new HashMap<>();
    protected final HashMap<String, Node<ConfigParserFunction>> lootFormatFunctions = new HashMap<>();
    protected int placeholderLimit;
    protected boolean redisRanking;
    protected String serverGroup;
    protected String[] itemDetectOrder = new String[0];
    protected String[] blockDetectOrder = new String[0];
    protected int dataSaveInterval;
    protected boolean logDataSaving;
    protected boolean lockData;
    protected boolean metrics;
    protected boolean checkUpdate;
    protected boolean debug;
    protected boolean overrideVanillaWaitTime;
    protected int waterMinTime;
    protected int waterMaxTime;
    protected int finalWaterMaxTime;
    protected int finalWaterMinTime;
    protected boolean enableLavaFishing;
    protected int lavaMinTime;
    protected int lavaMaxTime;
    protected int finalLavaMaxTime;
    protected int finalLavaMinTime;
    protected boolean enableVoidFishing;
    protected int voidMinTime;
    protected int voidMaxTime;
    protected int finalVoidMaxTime;
    protected int finalVoidMinTime;
    protected int multipleLootSpawnDelay;
    protected boolean restrictedSizeRange;
    protected List<String> durabilityLore;
    protected boolean allowMultipleTotemType;
    protected boolean allowSameTotemType;
    protected EventPriority eventPriority;
    protected Requirement<Player>[] mechanicRequirements;
    protected Requirement<Player>[] skipGameRequirements;
    protected Requirement<Player>[] autoFishingRequirements;
    protected boolean enableBag;
    protected boolean baitAnimation;
    protected boolean antiAutoFishingMod;
    protected List<TriConsumer<Effect, Context<Player>, Integer>> globalEffects;

    protected ConfigManager(BukkitCustomFishingPlugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static boolean debug() {
        return instance.debug;
    }

    public static int placeholderLimit() {
        return instance.placeholderLimit;
    }

    public static boolean redisRanking() {
        return instance.redisRanking;
    }

    public static String serverGroup() {
        return instance.serverGroup;
    }

    public static String[] itemDetectOrder() {
        return instance.itemDetectOrder;
    }

    public static String[] blockDetectOrder() {
        return instance.blockDetectOrder;
    }

    public static int dataSaveInterval() {
        return instance.dataSaveInterval;
    }

    public static boolean logDataSaving() {
        return instance.logDataSaving;
    }

    public static boolean lockData() {
        return instance.lockData;
    }

    public static boolean metrics() {
        return instance.metrics;
    }

    public static boolean checkUpdate() {
        return instance.checkUpdate;
    }

    public static boolean overrideVanillaWaitTime() {
        return instance.overrideVanillaWaitTime;
    }

    public static int waterMinTime() {
        return instance.waterMinTime;
    }

    public static int waterMaxTime() {
        return instance.waterMaxTime;
    }

    public static int finalWaterMinTime() {
        return instance.finalWaterMinTime;
    }

    public static int finalWaterMaxTime() {
        return instance.finalWaterMaxTime;
    }

    public static boolean enableLavaFishing() {
        return instance.enableLavaFishing;
    }

    public static int lavaMinTime() {
        return instance.lavaMinTime;
    }

    public static int lavaMaxTime() {
        return instance.lavaMaxTime;
    }

    public static int finalLavaMinTime() {
        return instance.finalLavaMinTime;
    }

    public static int finalLavaMaxTime() {
        return instance.finalLavaMaxTime;
    }

    public static boolean enableVoidFishing() {
        return instance.enableVoidFishing;
    }

    public static int voidMinTime() {
        return instance.voidMinTime;
    }

    public static int voidMaxTime() {
        return instance.voidMaxTime;
    }

    public static int finalVoidMinTime() {
        return instance.finalVoidMinTime;
    }

    public static int finalVoidMaxTime() {
        return instance.finalVoidMaxTime;
    }

    public static int multipleLootSpawnDelay() {
        return instance.multipleLootSpawnDelay;
    }

    public static boolean restrictedSizeRange() {
        return instance.restrictedSizeRange;
    }

    public static boolean allowMultipleTotemType() {
        return instance.allowMultipleTotemType;
    }

    public static boolean allowSameTotemType() {
        return instance.allowSameTotemType;
    }

    public static boolean enableBag() {
        return instance.enableBag;
    }

    public static boolean baitAnimation() {
        return instance.baitAnimation;
    }

    public static boolean antiAutoFishingMod() {
        return instance.antiAutoFishingMod;
    }

    public static List<String> durabilityLore() {
        return instance.durabilityLore;
    }

    public static EventPriority eventPriority() {
        return instance.eventPriority;
    }

    public static Requirement<Player>[] mechanicRequirements() {
        return instance.mechanicRequirements;
    }

    public static Requirement<Player>[] autoFishingRequirements() {
        return instance.autoFishingRequirements;
    }

    public static Requirement<Player>[] skipGameRequirements() {
        return instance.skipGameRequirements;
    }

    public static List<TriConsumer<Effect, Context<Player>, Integer>> globalEffects() {
        return instance.globalEffects;
    }

    public void registerHookParser(Function<Object, Consumer<HookConfig.Builder>> function, String... nodes) {
        registerNodeFunction(nodes, new HookParserFunction(function), hookFormatFunctions);
    }

    public void registerTotemParser(Function<Object, Consumer<TotemConfig.Builder>> function, String... nodes) {
        registerNodeFunction(nodes, new TotemParserFunction(function), totemFormatFunctions);
    }

    public void registerLootParser(Function<Object, Consumer<Loot.Builder>> function, String... nodes) {
        registerNodeFunction(nodes, new LootParserFunction(function), lootFormatFunctions);
    }

    public void registerItemParser(Function<Object, BiConsumer<Item<ItemStack>, Context<Player>>> function, int priority, String... nodes) {
        registerNodeFunction(nodes, new ItemParserFunction(priority, function), itemFormatFunctions);
    }

    public void registerEffectModifierParser(Function<Object, Consumer<EffectModifier.Builder>> function, String... nodes) {
        registerNodeFunction(nodes, new EffectModifierParserFunction(function), effectModifierFormatFunctions);
    }

    public void registerEntityParser(Function<Object, Consumer<EntityConfig.Builder>> function, String... nodes) {
        registerNodeFunction(nodes, new EntityParserFunction(function), entityFormatFunctions);
    }

    public void registerBlockParser(Function<Object, Consumer<BlockConfig.Builder>> function, String... nodes) {
        registerNodeFunction(nodes, new BlockParserFunction(function), blockFormatFunctions);
    }

    public void registerEventParser(Function<Object, Consumer<EventCarrier.Builder>> function, String... nodes) {
        registerNodeFunction(nodes, new EventParserFunction(function), eventFormatFunctions);
    }

    public void registerBaseEffectParser(Function<Object, Consumer<LootBaseEffect.Builder>> function, String... nodes) {
        registerNodeFunction(nodes, new BaseEffectParserFunction(function), baseEffectFormatFunctions);
    }

    public void unregisterNodeFunction(Map<String, Node<ConfigParserFunction>> functionMap, String... nodes) {
        for (int i = 0; i < nodes.length; i++) {
            if (functionMap.containsKey(nodes[i])) {
                Node<ConfigParserFunction> functionNode = functionMap.get(nodes[i]);
                if (i != nodes.length - 1) {
                   if (functionNode.nodeValue() != null) {
                       return;
                   } else {
                       functionMap = functionNode.getChildTree();
                   }
                } else {
                    if (functionNode.nodeValue() != null) {
                        functionMap.remove(nodes[i]);
                    }
                }
            }
        }
    }

    public void registerNodeFunction(String[] nodes, ConfigParserFunction configParserFunction, Map<String, Node<ConfigParserFunction>> functionMap) {
        for (int i = 0; i < nodes.length; i++) {
            if (functionMap.containsKey(nodes[i])) {
                Node<ConfigParserFunction> functionNode = functionMap.get(nodes[i]);
                if (functionNode.nodeValue() != null) {
                    throw new IllegalArgumentException("Format function '" + nodes[i] + "' already exists");
                }
                functionMap = functionNode.getChildTree();
            } else {
                if (i != nodes.length - 1) {
                    Node<ConfigParserFunction> newNode = new Node<>();
                    functionMap.put(nodes[i], newNode);
                    functionMap = newNode.getChildTree();
                } else {
                    functionMap.put(nodes[i], new Node<>(configParserFunction));
                }
            }
        }
    }

    protected Path resolveConfig(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
        filePath = filePath.replace('\\', '/');
        Path configFile = plugin.getConfigDirectory().resolve(filePath);
        // if the config doesn't exist, create it based on the template in the resources dir
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(configFile.getParent());
            } catch (IOException e) {
                // ignore
            }
            try (InputStream is = plugin.getResourceStream(filePath)) {
                if (is == null) {
                    throw new IllegalArgumentException("The embedded resource '" + filePath + "' cannot be found");
                }
                Files.copy(is, configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return configFile;
    }

    @Override
    public YamlDocument loadConfig(String filePath) {
        return loadConfig(filePath, '.');
    }

    @Override
    public YamlDocument loadConfig(String filePath, char routeSeparator) {
        try (InputStream inputStream = new FileInputStream(resolveConfig(filePath).toFile())) {
            return YamlDocument.create(
                    inputStream,
                    plugin.getResourceStream(filePath),
                    GeneralSettings.builder().setRouteSeparator(routeSeparator).build(),
                    LoaderSettings
                            .builder()
                            .setAutoUpdate(true)
                            .build(),
                    DumperSettings.builder()
                            .setScalarFormatter((tag, value, role, def) -> {
                                if (role == NodeRole.KEY) {
                                    return ScalarStyle.PLAIN;
                                } else {
                                    return tag == Tag.STR ? ScalarStyle.DOUBLE_QUOTED : ScalarStyle.PLAIN;
                                }
                            })
                            .build(),
                    UpdaterSettings
                            .builder()
                            .setVersioning(new BasicVersioning("config-version"))
                            .build()
            );
        } catch (IOException e) {
            plugin.getPluginLogger().severe("Failed to load config " + filePath, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public YamlDocument loadData(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return YamlDocument.create(inputStream);
        } catch (IOException e) {
            plugin.getPluginLogger().severe("Failed to load config " + file, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public YamlDocument loadData(File file, char routeSeparator) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return YamlDocument.create(inputStream, GeneralSettings.builder()
                    .setRouteSeparator(routeSeparator)
                    .build());
        } catch (IOException e) {
            plugin.getPluginLogger().severe("Failed to load config " + file, e);
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, Node<ConfigParserFunction>> getBlockFormatFunctions() {
        return blockFormatFunctions;
    }

    public HashMap<String, Node<ConfigParserFunction>> getEntityFormatFunctions() {
        return entityFormatFunctions;
    }

    public HashMap<String, Node<ConfigParserFunction>> getTotemFormatFunctions() {
        return totemFormatFunctions;
    }

    public HashMap<String, Node<ConfigParserFunction>> getHookFormatFunctions() {
        return hookFormatFunctions;
    }

    public HashMap<String, Node<ConfigParserFunction>> getEventFormatFunctions() {
        return eventFormatFunctions;
    }

    public HashMap<String, Node<ConfigParserFunction>> getBaseEffectFormatFunctions() {
        return baseEffectFormatFunctions;
    }

    public HashMap<String, Node<ConfigParserFunction>> getEffectModifierFormatFunctions() {
        return effectModifierFormatFunctions;
    }

    public HashMap<String, Node<ConfigParserFunction>> getItemFormatFunctions() {
        return itemFormatFunctions;
    }

    public HashMap<String, Node<ConfigParserFunction>> getLootFormatFunctions() {
        return lootFormatFunctions;
    }

    public abstract List<Pair<String, WeightOperation>> parseWeightOperation(List<String> ops, Function<String, Boolean> validator);

    public abstract List<Pair<String, WeightOperation>> parseGroupWeightOperation(List<String> gops);

    @Deprecated
    public Map<String, Node<ConfigParserFunction>> getDefaultFormatFunctions() {
        return getItemFormatFunctions();
    }
}
