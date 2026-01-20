package fr.infinitystudios.enderex.Utils;

import fr.infinitystudios.enderex.EnderEX;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.UnknownNullability;

import java.sql.SQLException;
import java.util.List;

public class MessagesUtils {

    private EnderEX plugin = EnderEX.getPlugin();

    private String prefix = plugin.getConfig().getString("prefix");

    private TextComponent cc(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    public void RegressionProtected(Player p){
        String temp = plugin.getConfig().getString("messages.regressionProtect");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void RegressionLost(Player p){
        String temp = plugin.getConfig().getString("messages.regressionLost");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void TransferNoEnderchest(CommandSender p){
        String temp = plugin.getConfig().getString("messages.transferNoEnderchest");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void TransferEnderchestNotEmpty(CommandSender p){
        String temp = plugin.getConfig().getString("messages.transferEnderchestNotEmpty");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void TransferSuccesful(CommandSender p, String targetname){
        String temp = plugin.getConfig().getString("messages.transferSuccesful");
        if(temp == null) return;
        if(temp.contains("%player%")){
            temp = temp.replace("%player%", targetname);
        }
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void ChestAlreadyOpened(Player p){
        String temp = plugin.getConfig().getString("messages.chestAlreadyOpened");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void EnderchestSaved(Player p){
        String temp = plugin.getConfig().getString("messages.enderchestSaved");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void NoPermission(Player p) {
        String temp = plugin.getConfig().getString("messages.noPermission");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void NoPermissionPhysical(Player p){
        String temp = plugin.getConfig().getString("messages.noPermissionPhysical");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void NoEnderchest(Player p) {
        String temp = plugin.getConfig().getString("messages.noEnderchest");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void ReloadConfirmed(CommandSender p){
        p.sendMessage(cc(prefix + " " + "&aConfig reloaded succesfully."));
    }

    public void DatabaseInternalError(@UnknownNullability CommandSender p){
        p.sendMessage(cc(prefix + " " + "&cAn nternal Database error occured!"));
    }

    public void NoEnderchestFound(Player p){
        String temp = plugin.getConfig().getString("messages.noEnderchestFound");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void NoUserWithThisIdFound(CommandSender p, int id){
        String temp = plugin.getConfig().getString("messages.noUserWithThisIdFound");
        if(temp == null) return;
        if(temp.contains("%id%")){
            temp = temp.replace("%id%", String.valueOf(id));
        }
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void NoUserWithThisNameFound(CommandSender p, String name){
        String temp = plugin.getConfig().getString("messages.noUserWithThisNameFound");
        if(temp == null) return;
        if(temp.contains("%name%")){
            temp = temp.replace("%name%", name);
        }
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void MultipleUserWithNameFound(CommandSender p, List<UserEntry> users){
        String temp = plugin.getConfig().getString("messages.multipleUserWithThisNameFound");
        if(temp == null) return;
        if(temp.contains("%name%")){
            temp = temp.replace("%name%", users.getFirst().name());
        }
        p.sendMessage(cc(prefix + " " + temp));

        for (UserEntry user : users) {
            p.sendMessage(cc(userInfoResolver(user)));
        }

        String temp2 = plugin.getConfig().getString("messages.useIdToResolve");
        if(temp2 == null) return;
        p.sendMessage(cc(temp2));
    }

    public void UserInfoSearchId(CommandSender p, UserEntry user){
        String temp = plugin.getConfig().getString("messages.userInfoSearchId");
        if(temp == null) return;
        if(temp.contains("%id%")){
            temp = temp.replace("%id%", String.valueOf(user.id()));
        }
        p.sendMessage(cc(prefix + " " + temp));
        p.sendMessage(cc(userInfoResolver(user)));
    }

    public void UserInfoSearchName(CommandSender p, List<UserEntry> users){
        String temp = plugin.getConfig().getString("messages.userInfoSearchName");
        if(temp == null) return;
        if(temp.contains("%name%")){
            temp = temp.replace("%name%", users.getFirst().name());
        }
        p.sendMessage(cc(prefix + " " + temp));

        for (UserEntry user : users) {
            p.sendMessage(cc(userInfoResolver(user)));
        }
    }

    public void InvalidPage(CommandSender p) {
        String temp = plugin.getConfig().getString("messages.invalidPage");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void UsermapList(CommandSender p, int page, List<UserEntry> users){
        String temp = plugin.getConfig().getString("messages.usermapList");
        if(temp == null) return;
        if(temp.contains("%page%")){
            temp = temp.replace("%page%", String.valueOf(page));
        }
        if(temp.contains("%maxpage%")){
            int maxpage = 0;
            try {
                maxpage = plugin.getDatabaseManager().getTotalPages();
            } catch (SQLException e){
                e.printStackTrace();
            }

            temp = temp.replace("%maxpage%", String.valueOf(maxpage));
        }

        p.sendMessage(cc(prefix + " " + temp));

        for (UserEntry user : users) {
            p.sendMessage(cc(userInfoResolver(user)));
        }
    }

    public void TransferPlayerNotOnline(CommandSender p){
        String temp = plugin.getConfig().getString("messages.transferPlayerNotOnline");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void TransferSimpleNotEnough(CommandSender p){
        String temp = plugin.getConfig().getString("messages.transferSimpleNotEnough");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void TransferAdvancedNotEnough(CommandSender p){
        String temp = plugin.getConfig().getString("messages.transferAdvancedNotEnough");
        if(temp == null) return;
        p.sendMessage(cc(prefix + " " + temp));
    }

    public void HelpCommandMessages(CommandSender p){
        List<String> helpMessage = plugin.getConfig().getStringList("messages.help");
        if(helpMessage.isEmpty()) return;

        for(String line : helpMessage){
            p.sendMessage(cc(line));
        }
    }

    private String userInfoResolver(UserEntry user){
        String temp = plugin.getConfig().getString("messages.userInfoEntry");
        if(temp == null) return null;

        if(temp.contains("%id%")){
            temp = temp.replace("%id%", String.valueOf(user.id()));
        }
        if(temp.contains("%name%")){
            temp = temp.replace("%name%", user.name());
        }
        if(temp.contains("%platform%")){
            switch (user.platform()) {
                case JAVA -> temp = temp.replace("%platform%", "&2JAVA");
                case BEDROCK -> temp = temp.replace("%platform%", "&3BEDROCK");
            }
        }
        if(temp.contains("%uuid%")){
            temp = temp.replace("%uuid%", String.valueOf(user.uuid()));
        }

        return temp;
    }
}
