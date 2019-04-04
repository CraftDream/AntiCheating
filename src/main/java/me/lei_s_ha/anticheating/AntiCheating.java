package me.lei_s_ha.anticheating;

import com.google.common.base.Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class AntiCheating extends JavaPlugin implements Listener {
    private String qz = "§eAntiCheating§b >> §r";
    private static List<String> AntiCheatModCode = Arrays.asList( //反作弊的代码
            "&3 &9 &2 &0 &0 &2 ",
            // CJB Fly 防御CJB作弊飞行
            "&3 &9 &2 &0 &0 &1 ",
            // CJB Radar 防御CJB雷达
            "&3 &9 &2 &0 &0 &3 ",
            // Rei"s Minimap 防御Rei小地图
            "&0&0&1&e&f",
            "&0&0&2&3&4&5&6&7&e&f",
            // Zan MiniMap 防御Zan小地图
            "&3 &6 &3 &6 &3 &6 &e",
            "&3 &6 &3 &6 &3 &6 &d",
            // Automap 防御AutoMap
            "&0&0&1&f&e",
            "&0&0&2&f&e",
            "&0&0&3&4&5&6&7&8&f&e",
            // SmartMove 防御灵活动作作弊
            "&0&1&0&1&2&f&f",
            "&0&1&3&4&f&f",
            "&0&1&5&f&f",
            "&0&1&6&f&f",
            "&0&1&7&f&f",
            "&0&1&8&9&a&b&f&f",
            // Zombe 防御Zombe秒破坏和飞行等作弊
            "&f &f &2 &0 &4 &8 ",
            "&f &f &4 &0 &9 &6 ",
            "&f &f &1 &0 &2 &4 ",
            // Schematica 防御Schematica模组
            // 该模组可以盗取服务器里的任何建筑
            "&0&2&0&0&e&f",
            "&0&2&1&0&e&f",
            "&0&2&1&1&e&f");
    private String username = null;
    private static HashMap<Player, Float> yawMap = new HashMap<>();
    private static HashMap<Player, Float> ptichMap  = new HashMap<>();
    private static HashMap<Player, Integer> valueMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getConsoleSender().sendMessage(qz + "§6插件已加载!");
    }

    @EventHandler(ignoreCancelled = true)
    public void CheckDropNoBugInfItem(PlayerDropItemEvent e) {  //影分身BUG
        if (e.getPlayer() == null || !e.getPlayer().isOnline() || !e.getPlayer().isValid()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)  //防爆
    public void Explode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)  //防止方块被火烧
    public void BurnEvent(BlockBurnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFish(PlayerFishEvent e) {  //钓鱼机
        Entity b = e.getHook();
        if (b.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.TRIPWIRE || b.getLocation().getBlock().getRelative(BlockFace.UP).getType().toString().contains("_PRESSURE_PLATE")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {  //钓鱼视角判断
        if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            return;
        }
        Player player = event.getPlayer();
        boolean boo = false;
        Location loc = player.getLocation().clone();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        if (yawMap.containsKey(player) && yaw == yawMap.get(player)) {
            boo = true;
        }
        if (ptichMap.containsKey(player) && pitch == ptichMap.get(player)) {
            boo = true;
        }
        if (boo) {
            event.getCaught().remove();
            event.setExpToDrop(0);
            if (valueMap.containsKey(player)) {
                valueMap.put(player, valueMap.get(player) + 1);
            }
            else {
                valueMap.put(player, 1);
            }
        }
        yawMap.put(player, yaw);
        ptichMap.put(player, pitch);
    }

    @EventHandler
    public void onFireSpread(BlockSpreadEvent e) {  //防止火蔓延
        if (e.getSource().getType() == Material.FIRE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMoveItem(EntityDamageByEntityEvent e) {  //展示框、画、盔甲架保护、等一切实体保护

        if (e.getDamager().isOp()) {
            return;
        }
        //杀戮光环
        if (e.getDamager() instanceof Player) {
            Player player = (Player)e.getDamager();
            if (player.getInventory().getItemInMainHand().getType().toString().contains("_SWORD")||player.getInventory().getItemInOffHand().getType().toString().contains("_SWORD")) {
                final Location damager2 = e.getDamager().getLocation();
                final Location entity2 = e.getEntity().getLocation();
                if (damager2.distance(entity2) > 3.0) {
                    e.setCancelled(true);
                }
            }
        }
        if(e.getDamager().getWorld().getName().equals("spawn")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e){  //树叶腐烂
        if(e.getBlock().getWorld().getName().equals("spawn")) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)  //龙蛋移动
    public void ban(BlockFromToEvent event) {
        Material material = event.getBlock().getType();
        if(material == Material.DRAGON_EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)  //玩家加入事件判断作弊
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().isOp()) {
            this.username = e.getPlayer().getName();
            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    AsyncSendTask();
                }
            }, 20L);
        }
    }

    private void AsyncSendTask() {  //判断作弊
        if (Strings.isNullOrEmpty(this.username)) {
            return;
        }
        String name = this.username;
        this.username = null;
        Player p = Bukkit.getPlayer(name);
        if (p == null) {
            return;
        }
        for (String code : AntiCheatModCode) {
            if (!p.isOnline()) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', code));
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)  //附魔效率卡方块
    public void onBlockDamage(BlockDamageEvent event){
        if (event.getPlayer().isOnGround()) {
            return;
        }
        if (event.getPlayer().isFlying()) {
            return;
        }
        Block block = event.getBlock();
        event.getPlayer().sendBlockChange(block.getLocation(), block.getType(), block.getData());
    }

    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent e){  //判断是否玩家捡起掉落物
        if (e.getEntity() instanceof Player){
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onJump(PlayerInteractEvent event){  //防止玩家踩田
        if ((event.getAction() == Action.PHYSICAL) && (event.getClickedBlock().getType() == Material.SOIL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onModJumpty(EntityInteractEvent event){  //防止实体踩田
        if ((event.getEntityType() != EntityType.PLAYER) && (event.getBlock().getType() == Material.SOIL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)  //防止实体破坏方块
    public void onEntityBlockChangeEvent(EntityChangeBlockEvent e) {
        if ((e.getEntity() instanceof Wither) || (e.getEntity() instanceof EnderDragon) || (e.getEntity() instanceof Enderman) || (e.getEntity() instanceof Ghast) || (e.getEntity() instanceof Blaze)) {
            if(e.isCancelled()) {
                e.setCancelled(true);
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)  //限制告示牌
    public void onsignchange(SignChangeEvent e){
        for (int i = 0; i < 4; i++) {
            if (e.getLine(i).matches("^[a-zA-Z0-9_]*$")){
                if (e.getLine(i).length() > 20){
                    e.setCancelled(true);
                }
            }else if (e.getLine(i).length() > 50){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void eggThrow(PlayerBedEnterEvent event) {  //禁止睡觉
        event.setCancelled(true);
        event.getPlayer().sendMessage(qz + "§6服务器禁止睡觉!");

    }

    @EventHandler
    public void lightningEntity(final EntityDamageByEntityEvent e) {  //防止地上的掉落物被炸没
        if (e.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
            if(e.getEntity() instanceof Item) {
                e.setCancelled(true);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage(qz + "§6插件已卸载!");
    }
}