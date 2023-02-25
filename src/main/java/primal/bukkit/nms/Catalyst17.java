package primal.bukkit.nms;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatBaseComponent;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.player.EnumChatVisibility;
import net.minecraft.world.level.NextTickListEntry;
import net.minecraft.world.level.TickListServer;
import net.minecraft.world.level.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import primal.bukkit.plugin.PrimalPlugin;
import primal.bukkit.sched.J;
import primal.bukkit.world.MaterialBlock;
import primal.lang.collection.GList;
import primal.lang.collection.GSet;
import primal.util.text.C;

import java.lang.reflect.Field;
import java.util.*;

public class Catalyst17 extends CatalystPacketListener implements CatalystHost
{
    private final Map<Player, PlayerSettings> playerSettings = new HashMap<>();

    @Override
    public void sendAdvancement(Player p, FrameType type, ItemStack is, String text)
    {
        AdvancementHolder17 a = new AdvancementHolder17(UUID.randomUUID().toString());
        a.withToast(true);
        a.withDescription("?");
        a.withFrame(type);
        a.withAnnouncement(false);
        a.withTitle(text);
        a.withTrigger("minecraft:impossible");
        a.withIcon(is.getData());
        a.withBackground("minecraft:textures/blocks/bedrock.png");
        a.loadAdvancement();
        a.sendPlayer(p);
        J.s(() -> a.delete(p), 5);
    }

    @Override
    public Object packetChunkUnload(int x, int z)
    {
        return new PacketPlayOutUnloadChunk(x, z);
    }

    @Override
    public Object packetChunkFullSend(Chunk chunk)
    {
        return new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle());
    }

    @Override
    public Object packetBlockChange(Location block, int blockId, byte blockData)
    {
        return new PacketPlayOutBlockChange(toBlockPos(block), Block.getByCombinedId(blockId << 4 | (blockData & 15)));
    }

    @Override
    public Object packetBlockAction(Location block, int action, int param, int blocktype)
    {
       return null;
    }

    @Override
    public Object packetAnimation(int eid, int animation)
    {
        return null;
    }

    @Override
    public Object packetBlockBreakAnimation(int eid, Location location, byte damage)
    {
        return new PacketPlayOutBlockBreakAnimation(eid, toBlockPos(location), damage);
    }

    @Override
    public Object packetGameState(int mode, float value)
    {
        switch(mode)
        {
            case 0:

            case 1:
                return new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.c, value);
            case 2:
                return new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.b, value);
            case 3:
                return new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.d, value);
            case 4:

            case 5:

            case 6:
                return new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.g, value);
            case 7:

            case 8:

            case 9:

            case 10:
                return new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.k, value);
            default:
                return null;
        }
    }

    @Override
    public Object packetTitleMessage(String title)
    {
        return new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}"));
    }

    @Override
    public Object packetSubtitleMessage(String subtitle)
    {
        return new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}"));
    }

    @Override
    public Object packetActionBarMessage(String subtitle)
    {
        return new net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}"));
    }

    @Override
    public Object packetResetTitle()
    {
        return new net.minecraft.network.protocol.game.ClientboundClearTitlesPacket(true);
    }

    @Override
    public Object packetClearTitle()
    {
        return new net.minecraft.network.protocol.game.ClientboundClearTitlesPacket(false);
    }

    @Override
    public Object packetTimes(int in, int stay, int out)
    {
        return new net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket(in, stay, out);
    }

    private BlockPosition toBlockPos(Location location)
    {
        return new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public String getServerVersion()
    {
        return "1_17_R1";
    }

    @Override
    public String getVersion()
    {
        return "1.17";
    }

    @Override
    public void start()
    {
        openListener();
        Bukkit.getPluginManager().registerEvents(this, PrimalPlugin.instance);
    }

    @Override
    public void stop()
    {
        closeListener();
        HandlerList.unregisterAll(this);
    }

    @Override
    public void onOpened()
    {
        addGlobalIncomingListener(new PacketHandler<Object>()
        {
            @Override
            public Object onPacket(Player player, Object packet)
            {
                if(packet instanceof PacketPlayInSettings)
                {
                    PacketPlayInSettings s = (PacketPlayInSettings) packet;
                    playerSettings.put(player, new PlayerSettings(new V(s).get("locale"), new V(s).get("viewDistance"), ChatMode.values()[((EnumChatVisibility) new V(s).get("c")).ordinal()], new V(s).get("d"), new V(s).get("e"), new V(s).get("f").equals(EnumMainHand.b)));
                }

                return packet;
            }
        });
    }

    @Override
    public void sendPacket(Player p, Object o)
    {
        ((CraftPlayer) p).getHandle().b.sendPacket((Packet<?>) o);
    }

    @Override
    public void sendRangedPacket(double radius, Location l, Object o)
    {
        for(Player i : l.getWorld().getPlayers())
        {
            if(canSee(l, i) && l.distanceSquared(i.getLocation()) <= radius * radius)
            {
                sendPacket(i, o);
            }
        }
    }

    @Override
    public void sendGlobalPacket(World w, Object o)
    {
        for(Player i : w.getPlayers())
        {
            sendPacket(i, o);
        }
    }

    @Override
    public void sendUniversalPacket(Object o)
    {
        for(Player i : Bukkit.getOnlinePlayers())
        {
            sendPacket(i, o);
        }
    }

    @Override
    public void sendViewDistancedPacket(Chunk c, Object o)
    {
        for(Player i : getObservers(c))
        {
            sendPacket(i, o);
        }
    }

    @Override
    public boolean canSee(Chunk c, Player p)
    {
        return isWithin(p.getLocation().getChunk(), c, getViewDistance(p));
    }

    @Override
    public boolean canSee(Location l, Player p)
    {
        return canSee(l.getChunk(), p);
    }

    @Override
    public int getViewDistance(Player p)
    {
        try
        {
            return getSettings(p).getViewDistance();
        }

        catch(Throwable e)
        {

        }

        return Bukkit.getServer().getViewDistance();
    }

    public boolean isWithin(Chunk center, Chunk check, int viewDistance)
    {
        return Math.abs(center.getX() - check.getX()) <= viewDistance && Math.abs(center.getZ() - check.getZ()) <= viewDistance;
    }

    @Override
    public List<Player> getObservers(Chunk c)
    {
        List<Player> p = new ArrayList<>();

        for(Player i : c.getWorld().getPlayers())
        {
            if(canSee(c, i))
            {
                p.add(i);
            }
        }

        return p;
    }

    @Override
    public List<Player> getObservers(Location l)
    {
        return getObservers(l.getChunk());
    }

    @EventHandler
    public void on(PlayerQuitEvent e)
    {
        playerSettings.remove(e.getPlayer());
    }

    @Override
    public PlayerSettings getSettings(Player p)
    {
        return playerSettings.get(p);
    }

    @Override
    public ShadowChunk shadowCopy(Chunk at)
    {
        return new ShadowChunk14(at);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Object> getTickList(World world)
    {
        try
        {
            Field f = WorldServer.class.getDeclaredField("F");
            Field ff = TickListServer.class.getDeclaredField("d");
            f.setAccessible(true);
            ff.setAccessible(true);
            TickListServer<?> l = (TickListServer<?>) f.get(((CraftWorld) world).getHandle());
            return (Set<Object>) ff.get(l);
        }

        catch(Throwable e)
        {
            e.printStackTrace();
        }

        return new GSet<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Object> getTickListFluid(World world)
    {
        try
        {
            Field f = WorldServer.class.getDeclaredField("L");
            Field ff = TickListServer.class.getDeclaredField("d");
            f.setAccessible(true);
            ff.setAccessible(true);
            TickListServer<?> l = (TickListServer<?>) f.get(((CraftWorld) world).getHandle());
            return (Set<Object>) ff.get(l);
        }

        catch(Throwable e)
        {
            e.printStackTrace();
        }

        return new GSet<>();
    }

    @Override
    public org.bukkit.block.Block getBlock(World world, Object tickListEntry)
    {
        BlockPosition pos = ((NextTickListEntry<?>) tickListEntry).a;
        return world.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public Object packetTabHeaderFooter(String h, String f)
    {
        PacketPlayOutPlayerListHeaderFooter p = new PacketPlayOutPlayerListHeaderFooter(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + h + "\"}"), IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + f + "\"}"));

        return p;
    }

    @Override
    public void scroll(Player sender, int previous)
    {
        sendPacket(sender, new PacketPlayOutHeldItemSlot(previous));
    }

    @Override
    public int getAction(Object packetIn)
    {
        return ((PacketPlayInEntityAction) packetIn).c().ordinal();
    }

    @Override
    public Vector getDirection(Object packet)
    {
        float yaw = 0;
        float pitch = 0;

        try
        {
            Field a = PacketPlayInFlying.class.getDeclaredField("yaw");
            Field b = PacketPlayInFlying.class.getDeclaredField("pitch");
            a.setAccessible(true);
            b.setAccessible(true);
            yaw = (float) a.get(packet);
            pitch = (float) b.get(packet);
        }

        catch(Exception e)
        {

        }

        double pitchRadians = Math.toRadians(-pitch);
        double yawRadians = Math.toRadians(-yaw);
        double sinPitch = Math.sin(pitchRadians);
        double cosPitch = Math.cos(pitchRadians);
        double sinYaw = Math.sin(yawRadians);
        double cosYaw = Math.cos(yawRadians);
        Vector v = new Vector(-cosPitch * sinYaw, sinPitch, -cosPitch * cosYaw);
        return new Vector(-v.getX(), v.getY(), -v.getZ());
    }

    @Override
    public void spawnFallingBlock(int eid, UUID id, Location l, Player player, MaterialBlock mb)
    {
    }

    @Override
    public void removeEntity(int eid, Player p)
    {
        PacketPlayOutEntityDestroy d = new PacketPlayOutEntityDestroy(eid);
        sendPacket(p, d);
    }

    @Override
    public void moveEntityRelative(int eid, Player p, double x, double y, double z, boolean onGround)
    {
    }

    @Override
    public void teleportEntity(int eid, Player p, Location l, boolean onGround)
    {

    }

    @Override
    public void spawnArmorStand(int eid, UUID id, Location l, int data, Player player)
    {

    }

    private IChatBaseComponent s(String s)
    {
        return IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + s + "\"}");
    }

    @Override
    public void sendTeam(Player p, String id, String name, String prefix, String suffix, C color, int mode)
    {

    }

    @Override
    public void addTeam(Player p, String id, String name, String prefix, String suffix, C color)
    {
        sendTeam(p, id, name, prefix, suffix, color, 0);
    }

    @Override
    public void updateTeam(Player p, String id, String name, String prefix, String suffix, C color)
    {
        sendTeam(p, id, name, prefix, suffix, color, 2);
    }

    @Override
    public void removeTeam(Player p, String id)
    {
        sendTeam(p, id, "", "", "", C.WHITE, 1);
    }

    @Override
    public void addToTeam(Player p, String id, String... entities)
    {

    }

    @Override
    public void removeFromTeam(Player p, String id, String... entities)
    {

    }

    @Override
    public void displayScoreboard(Player p, int slot, String id)
    {

    }

    @Override
    public void displayScoreboard(Player p, C slot, String id)
    {
        displayScoreboard(p, 3 + slot.getMeta(), id);
    }

    @Override
    public void sendNewObjective(Player p, String id, String name)
    {

    }

    @Override
    public void sendDeleteObjective(Player p, String id)
    {

    }

    @Override
    public void sendEditObjective(Player p, String id, String name)
    {

    }

    @Override
    public void sendScoreUpdate(Player p, String name, String objective, int score)
    {

    }

    @Override
    public void sendScoreRemove(Player p, String name, String objective)
    {

    }

    @Override
    public void sendRemoveGlowingColorMetaEntity(Player p, UUID glowing)
    {
        String c = teamCache.get(p.getUniqueId() + "-" + glowing);

        if(c != null)
        {
            teamCache.remove(p.getUniqueId() + "-" + glowing);
            removeFromTeam(p, c, glowing.toString());
            removeTeam(p, c);
        }
    }

    @Override
    public void sendRemoveGlowingColorMetaPlayer(Player p, UUID glowing, String name)
    {
        String c = teamCache.get(p.getUniqueId() + "-" + glowing);

        if(c != null)
        {
            teamCache.remove(p.getUniqueId() + "-" + glowing);
            removeFromTeam(p, c, name);
            removeTeam(p, c);
        }
    }

    @Override
    public void sendGlowingColorMeta(Player p, Entity glowing, C color)
    {
        if(glowing instanceof Player)
        {
            sendGlowingColorMetaName(p, p.getName(), color);
        }

        else
        {
            sendGlowingColorMetaEntity(p, glowing.getUniqueId(), color);
        }
    }

    @Override
    public void sendGlowingColorMetaEntity(Player p, UUID euid, C color)
    {
        sendGlowingColorMetaName(p, euid.toString(), color);
    }

    @Override
    public void sendGlowingColorMetaName(Player p, String euid, C color)
    {
        String c = teamCache.get(p.getUniqueId() + "-" + euid);

        if(c != null)
        {
            updateTeam(p, c, c, color.toString(), C.RESET.toString(), color);
            sendEditObjective(p, c, c);
        }

        else
        {
            c = "v" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 15);
            teamCache.put(p.getUniqueId() + "-" + euid, c);

            addTeam(p, c, c, color.toString(), C.RESET.toString(), color);
            updateTeam(p, c, c, color.toString(), C.RESET.toString(), color);

            addToTeam(p, c, euid);
        }
    }

    @Override
    public void sendRemoveGlowingColorMeta(Player p, Entity glowing)
    {
        String c = teamCache.get(p.getUniqueId() + "-" + glowing.getUniqueId());

        if(c != null)
        {
            teamCache.remove(p.getUniqueId() + "-" + glowing.getUniqueId());
            removeFromTeam(p, c, glowing instanceof Player ? glowing.getName() : glowing.getUniqueId().toString());
            removeTeam(p, c);
        }
    }

    @Override
    public void updatePassengers(Player p, int vehicle, int... passengers)
    {

    }

    @Override
    public void sendEntityMetadata(Player p, int eid, Object... objects)
    {

    }

    @Override
    public void sendEntityMetadata(Player p, int eid, List<Object> objects)
    {
        sendEntityMetadata(p, eid, objects.toArray(new Object[0]));
    }

    @Override
    public Object getMetaEntityRemainingAir(int airTicksLeft)
    {
return null;
    }

    @Override
    public Object getMetaEntityCustomName(String name)
    {
        return null;
    }

    @Override
    public Object getMetaEntityProperties(boolean onFire, boolean crouched, boolean sprinting, boolean swimming, boolean invisible, boolean glowing, boolean flyingElytra)
    {
      return null;
    }

    @Override
    public Object getMetaEntityGravity(boolean gravity)
    {
return null;
    }

    @Override
    public Object getMetaEntitySilenced(boolean silenced)
    {
        return null;
    }

    @Override
    public Object getMetaEntityCustomNameVisible(boolean visible)
    {
        return null;
    }

    @Override
    public Object getMetaArmorStandProperties(boolean isSmall, boolean hasArms, boolean noBasePlate, boolean marker)
    {
        return null;
    }

    @Override
    public void sendItemStack(Player p, ItemStack is, int slot)
    {

    }
}