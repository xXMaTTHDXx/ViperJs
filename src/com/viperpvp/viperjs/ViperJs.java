package com.viperpvp.viperjs;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.viperpvp.viperjs.backend.req.ViperEventExecutor;
import com.viperpvp.viperjs.backend.script.ScriptInstance;
import com.viperpvp.viperjs.commands.ScriptCommand;
import com.viperpvp.viperjs.database.MongoDatabaseManager;
import com.viperpvp.viperjs.database.ServerUtil;
import com.viperpvp.viperjs.listeners.PlayerJoin;
import com.viperpvp.viperjs.listeners.PlayerQuit;
import jdk.internal.dynalink.beans.StaticClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Matt on 15/05/2016.
 */
public class ViperJs extends JavaPlugin {

    private static ViperJs instance;

    private ScriptEngine nashorn = null;
    private Invocable invocable;
    private List<ViperEventExecutor> eventExecutors = new ArrayList<>();
    private Compilable compilable;

    private List<ScriptInstance> allScripts = new ArrayList<>();
    private final Map<String, String> eventNames = Maps.newHashMap();
    private ChatColor colorClass = null;


    private static Set<Class<? extends Event>> bukkitEvents = ImmutableSet.of(
            AsyncPlayerChatEvent.class,
            AsyncPlayerPreLoginEvent.class,
            BlockBreakEvent.class,
            BlockBurnEvent.class,
            BlockCanBuildEvent.class,
            BlockDamageEvent.class,
            BlockDispenseEvent.class,
            BlockExpEvent.class,
            BlockFormEvent.class,
            BlockFadeEvent.class,
            BlockFromToEvent.class,
            BlockGrowEvent.class,
            BlockIgniteEvent.class,
            BlockPhysicsEvent.class,
            BlockPistonExtendEvent.class,
            BlockPistonRetractEvent.class,
            BlockPlaceEvent.class,
            BlockRedstoneEvent.class,
            BlockSpreadEvent.class,
            BrewEvent.class,
            ChunkLoadEvent.class,
            ChunkPopulateEvent.class,
            ChunkUnloadEvent.class,
            CraftItemEvent.class,
            CreatureSpawnEvent.class,
            CreeperPowerEvent.class,
            EnchantItemEvent.class,
            EntityBlockFormEvent.class,
            EntityBreakDoorEvent.class,
            EntityChangeBlockEvent.class,
            EntityCombustByBlockEvent.class,
            EntityCombustByEntityEvent.class,
            EntityCombustEvent.class,
            EntityCreatePortalEvent.class,
            EntityDamageByBlockEvent.class,
            EntityDamageByEntityEvent.class,
            EntityDamageEvent.class,
            EntityDeathEvent.class,
            EntityExplodeEvent.class,
            EntityInteractEvent.class,
            EntityPortalEnterEvent.class,
            EntityPortalEvent.class,
            EntityPortalExitEvent.class,
            EntityRegainHealthEvent.class,
            EntityShootBowEvent.class,
            EntityTameEvent.class,
            EntityTargetEvent.class,
            EntityTargetLivingEntityEvent.class,
            EntityTeleportEvent.class,
            EntityUnleashEvent.class,
            ExpBottleEvent.class,
            ExplosionPrimeEvent.class,
            FoodLevelChangeEvent.class,
            FurnaceBurnEvent.class,
            FurnaceExtractEvent.class,
            FurnaceSmeltEvent.class,
            HangingBreakByEntityEvent.class,
            HangingBreakEvent.class,
            HangingPlaceEvent.class,
            HorseJumpEvent.class,
            InventoryClickEvent.class,
            InventoryCloseEvent.class,
            InventoryCreativeEvent.class,
            InventoryDragEvent.class,
            InventoryInteractEvent.class,
            InventoryMoveItemEvent.class,
            InventoryOpenEvent.class,
            InventoryPickupItemEvent.class,
            ItemDespawnEvent.class,
            ItemSpawnEvent.class,
            LeavesDecayEvent.class,
            LightningStrikeEvent.class,
            MapInitializeEvent.class,
            NotePlayEvent.class,
            PigZapEvent.class,
            PlayerAchievementAwardedEvent.class,
            PlayerAnimationEvent.class,
            PlayerBedEnterEvent.class,
            PlayerBedLeaveEvent.class,
            PlayerBucketEmptyEvent.class,
            PlayerBucketFillEvent.class,
            PlayerChangedWorldEvent.class,
            PlayerChannelEvent.class,
            PlayerChatEvent.class,
            PlayerChatTabCompleteEvent.class,
            PlayerCommandPreprocessEvent.class,
            PlayerDeathEvent.class,
            PlayerDropItemEvent.class,
            PlayerEditBookEvent.class,
            PlayerEggThrowEvent.class,
            PlayerExpChangeEvent.class,
            PlayerFishEvent.class,
            PlayerGameModeChangeEvent.class,
            PlayerInteractAtEntityEvent.class,
            PlayerInteractEntityEvent.class,
            PlayerInteractEvent.class,
            PlayerInventoryEvent.class,
            PlayerItemBreakEvent.class,
            PlayerItemConsumeEvent.class,
            PlayerItemHeldEvent.class,
            PlayerJoinEvent.class,
            PlayerKickEvent.class,
            PlayerLeashEntityEvent.class,
            PlayerLevelChangeEvent.class,
            PlayerLoginEvent.class,
            PlayerMoveEvent.class,
            PlayerPickupItemEvent.class,
            PlayerPortalEvent.class,
            PlayerPreLoginEvent.class,
            PlayerQuitEvent.class,
            PlayerRegisterChannelEvent.class,
            PlayerRespawnEvent.class,
            PlayerShearEntityEvent.class,
            PlayerStatisticIncrementEvent.class,
            PlayerTeleportEvent.class,
            PlayerToggleFlightEvent.class,
            PlayerToggleSneakEvent.class,
            PlayerToggleSprintEvent.class,
            PlayerUnleashEntityEvent.class,
            PlayerUnregisterChannelEvent.class,
            PlayerVelocityEvent.class,
            PluginDisableEvent.class,
            PluginEnableEvent.class,
            PortalCreateEvent.class,
            PotionSplashEvent.class,
            PrepareItemCraftEvent.class,
            PrepareItemEnchantEvent.class,
            ProjectileHitEvent.class,
            ProjectileLaunchEvent.class,
            RemoteServerCommandEvent.class,
            ServerCommandEvent.class,
            ServerListPingEvent.class,
            ServiceRegisterEvent.class,
            ServiceUnregisterEvent.class,
            SheepDyeWoolEvent.class,
            SheepRegrowWoolEvent.class,
            SignChangeEvent.class,
            SlimeSplitEvent.class,
            SpawnChangeEvent.class,
            StructureGrowEvent.class,
            ThunderChangeEvent.class,
            VehicleBlockCollisionEvent.class,
            VehicleCreateEvent.class,
            VehicleDamageEvent.class,
            VehicleDestroyEvent.class,
            VehicleEnterEvent.class,
            VehicleEntityCollisionEvent.class,
            VehicleExitEvent.class,
            VehicleMoveEvent.class,
            VehicleUpdateEvent.class,
            WeatherChangeEvent.class,
            WorldInitEvent.class,
            WorldLoadEvent.class,
            WorldSaveEvent.class,
            WorldUnloadEvent.class
    );

    private final String REMOTE_URL = "https://github.com/xXMaTTHDXx/Js.git";

    private Bindings bindings;

    private int loadedScripts = 0;
    public static MongoDatabaseManager manager;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("viperjs").setExecutor(new ScriptCommand());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        /**
         Nashorn stuff
         */
        nashorn = new ScriptEngineManager().getEngineByName("nashorn");
        invocable = (Invocable) nashorn;

        compilable = (Compilable) nashorn;

        getConfig().options().copyDefaults(true);
        saveConfig();

        bukkitEvents.forEach(clazz -> {
            eventNames.put(clazz.getSimpleName(), clazz.getCanonicalName());
        });

        manager = new MongoDatabaseManager();
        manager.connect("localhost", 27017);

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerJoin(), this);
        pm.registerEvents(new PlayerQuit(), this);

        try {
            init();
        } catch (FileNotFoundException | ScriptException e) {
            e.printStackTrace();
        }
    }

    public void unloadScripts() {
        for (ScriptInstance instance : allScripts) {
            instance.disableScriptFile();
        }
        allScripts.clear();
    }

    public void handleDir(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {

                if (dir.getName().equalsIgnoreCase(getConfig().getString("servername")) || dir.getPath().contains("loads") || dir.getPath().contains("patches") || dir.getName().equalsIgnoreCase("Js") || dir.getName().equalsIgnoreCase("bin" ) || dir.getName().equalsIgnoreCase("servers")) {
                    handleDir(file);
                } else {
                    System.out.println("Skipping directory: " + dir.getName() + " as it is not needed for this server!");
                }
            } else {
                if (file.getName().endsWith(".js")) {
                    if (!toEvaluate.contains(file)) {
                        toEvaluate.add(file);
                    }
                }
            }
        }
    }

    private List<File> toEvaluate = new ArrayList<>();

    public void pullAll() {
        unloadScripts();

        loadedScripts = 0;
        File scriptDir = new File(getDataFolder(), "Js");

        handleDir(scriptDir);

        for (File file : toEvaluate) {
            if (!file.getName().endsWith(".js")) {
                System.out.println("Skipping file: " + file.getName() + " as it is not a JavaScript file!");
            } else {
                try {
                    CompiledScript script = compilable.compile(new FileReader(file));
                    ScriptInstance instance = new ScriptInstance(script);
                    Bindings bindings = nashorn.createBindings();
                    bindings.put("viper", this);
                    bindings.put("server", getServer());
                    bindings.put("events", eventNames);
                    bindings.put("manager", instance.getScriptManager());
                    bindings.put("exports", instance.getJsonVariables());
                    bindings.put("db", manager);
                    bindings.put("serverutil", new ServerUtil());
                    addGlobalBindings(bindings);
                    script.eval(bindings);
                    allScripts.add(instance);
                } catch (ScriptException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            loadedScripts++;
        }
        System.out.println("Scripts loaded: " + loadedScripts + "!");
        toEvaluate.clear();
    }

    public void init() throws FileNotFoundException, ScriptException {
        pullAll();
    }

    public Object invokeLibFunction(String functionName, Object... args) {
        try {
            return invocable.invokeFunction(functionName, args);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public StaticClass toNashornClass(Class<?> clazz) {
        StaticClass o = null;
        try {
            Class<?> cl = Class.forName("jdk.internal.dynalink.beans.StaticClass"); //Remove reference to internal
            Constructor<?> constructor = cl.getDeclaredConstructor(Class.class);

            constructor.setAccessible(true);
            o = (StaticClass) constructor.newInstance(clazz);

            return o;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return o;
    }

    public void addGlobalBindings(Bindings b) {
        b.put("ChatColor", toNashornClass(ChatColor.class));
        b.put("Material", toNashornClass(Material.class));
        b.put("ItemStack", toNashornClass(ItemStack.class));
        b.put("InventoryType", toNashornClass(InventoryType.class));
        b.put("System", System.class);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static ViperJs get() {
        return instance;
    }
}
