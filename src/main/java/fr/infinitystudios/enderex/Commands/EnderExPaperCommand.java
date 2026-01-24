package fr.infinitystudios.enderex.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.infinitystudios.enderex.Chests.EnderCache;
import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EnderExPaperCommand {

    private static EnderEX plugin = EnderEX.getPlugin();

    private static TextComponent cc(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("enderex")
                .executes(EnderExPaperCommand::base)

                .then(Commands.literal("open")
                        .executes(EnderExPaperCommand::base)
                )

                .then(Commands.literal("help")
                        .requires(src -> src.getSender().hasPermission("enderex.admin")
                                || src.getSender().isOp())
                        .executes(EnderExPaperCommand::help)
                )

                .then(Commands.literal("reload")
                        .requires(src -> src.getSender().hasPermission("enderex.admin")
                                || src.getSender().isOp())
                        .executes(EnderExPaperCommand::reload)
                )

                .then(Commands.literal("see")
                        .requires(src -> src.getSender().hasPermission("enderex.admin")
                        || src.getSender().isOp())

                        .then(Commands.argument("target", StringArgumentType.word())
                                .suggests(EnderExPaperCommand::nameOrIdSuggestions)
                                .executes(EnderExPaperCommand::see)
                        )
                )

                .then(Commands.literal("transfer")
                        .requires(src -> src.getSender().hasPermission("enderex.admin")
                                || src.getSender().isOp())

                        .then(Commands.argument("target", StringArgumentType.word())
                                .suggests(EnderExPaperCommand::nameOrIdSuggestions)
                                .executes(EnderExPaperCommand::transfer)
                        )
                )

                .then(Commands.literal("forcetransfer")
                        .requires(src -> src.getSender().hasPermission("enderex.admin")
                                || src.getSender().isOp())

                        .then(Commands.argument("target", StringArgumentType.word())
                                .suggests(EnderExPaperCommand::nameOrIdSuggestions)
                                .executes(EnderExPaperCommand::forcetransfer)
                        )
                )

                .then(Commands.literal("usermap")
                        .requires(src -> src.getSender().hasPermission("enderex.admin")
                                || src.getSender().isOp())

                        .then(Commands.literal("search")
                                .then(Commands.argument("target", StringArgumentType.word())
                                        .suggests(EnderExPaperCommand::nameOrIdSuggestions)
                                        .executes(EnderExPaperCommand::usermapsearch)
                                )
                        )
                        .then(Commands.literal("list")
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(EnderExPaperCommand::usermaplist)
                                )
                        )
                )
                .build();
    }


    //ENDEREX COMMAND TREE:
    //EnderEx (nothing else) > open EC
    //EnderEx open > open EC
    //
    //Everything below need admin perm or op
    //EnderEx help
    //EnderEx reload
    //
    // [NAME] is weird, it suggests online names,
    // but will also take offline/usermap name,
    // but will fail if usermap contains 2 id with the same name (platform difference),
    // this is to make sure you use the correct user account.
    // I will send a special message that "replicate" what the usermap search would do,
    // that means displaying both accounts ID with the infos.
    //
    // Suggestions for name/id will work weirdly: it will start with displaying [NAME/ID]
    // if the admin start with a number, and no online players start with it,
    // it will stop displaying the suggestion and just mark it green.
    // but if the admin starts with a letter; than it suggests
    // every online players that start with this prefix.
    // if it's a playername (3 char minimum, otherwise it is red), we never mark it red, because it could be a usermap name
    // and not an online playername.
    //
    //
    //EnderEx see [ID/NAME]
    //EnderEx transfer [ID/NAME]
    //EnderEx forcetransfer [ID/NAME]
    //EnderEx usermap search [ID/NAME]
    //EnderEx usermap list [PAGE]

    private static Boolean chestCommandOpenBool = plugin.getConfig().getBoolean("need_permission_for_command_opening");


    private static int base(CommandContext<CommandSourceStack> ctx){
        CommandSender sender = ctx.getSource().getSender();
        if(!(sender instanceof Player p)){
            plugin.getLogger().info("You can't open a chest from the console.");
            return Command.SINGLE_SUCCESS;
        }

        if(chestCommandOpenBool && !p.hasPermission("enderex.commandopen")){
            new MessagesUtils().NoPermission(p);
            return Command.SINGLE_SUCCESS;
        }

        InvUtils iu = new InvUtils();
        RegressionState regressionState = iu.regressionCheck(p);

        if(regressionState == RegressionState.PROTECTED){
            return Command.SINGLE_SUCCESS;
        }

        if(InvUtils.openedChests.containsKey(p.getUniqueId())){
            new MessagesUtils().ChestAlreadyOpened(p);
            return Command.SINGLE_SUCCESS;
        }

        Inventory chest = iu.CloneInventoryFromCache(p);
        if(chest != null){
            p.openInventory(chest);
            InvUtils.openedChests.put(p.getUniqueId(), p.getUniqueId());
        } else {
            new MessagesUtils().NoEnderchest(p);
        }

        return Command.SINGLE_SUCCESS;
    }
    private static int help(CommandContext<CommandSourceStack> ctx){
        CommandSender sender = ctx.getSource().getSender();

        new MessagesUtils().HelpCommandMessages(sender);

        return Command.SINGLE_SUCCESS;
    }
    private static int reload(CommandContext<CommandSourceStack> ctx){
        plugin.getConfigManager().loadAndUpdateConfig();
        new MessagesUtils().ReloadConfirmed(ctx.getSource().getSender());
        return Command.SINGLE_SUCCESS;
    }
    private static int see(CommandContext<CommandSourceStack> ctx){
        CommandSender sender = ctx.getSource().getSender();
        if(!(sender instanceof Player p)){
            plugin.getLogger().info("You can't open a chest from the console.");
            return Command.SINGLE_SUCCESS;
        }

        String target = StringArgumentType.getString(ctx, "target");

        ResolvedUser user = validateAndResolveTarget(sender, target, false);
        if (user == null) {
            return Command.SINGLE_SUCCESS; //erreur déjà envoyée
        }

        InvUtils iu = new InvUtils();

        Inventory adminchest = iu.GetChestInventoryAdmin(user.uuid());

        if(adminchest == null){
            new MessagesUtils().NoEnderchestFound(p);
            return Command.SINGLE_SUCCESS;
        }

        if(InvUtils.openedChests.containsKey(user.uuid())){
            new MessagesUtils().ChestAlreadyOpened(p);
            return Command.SINGLE_SUCCESS;
        }

        p.openInventory(adminchest);
        InvUtils.openedChests.put(user.uuid(), p.getUniqueId());

        return Command.SINGLE_SUCCESS;
    }
    private static int transfer(CommandContext<CommandSourceStack> ctx){
        CommandSender sender = ctx.getSource().getSender();

        String target = StringArgumentType.getString(ctx, "target");

        ResolvedUser user = validateAndResolveTarget(sender, target, false);
        if (user == null){
            return Command.SINGLE_SUCCESS;
        }

        if (!user.online()){
            new MessagesUtils().TransferPlayerNotOnline(sender);
            return Command.SINGLE_SUCCESS;
        }

        Player p = plugin.getServer().getPlayer(user.uuid());

        if(FileUtils.getPluginMode() == PluginMode.SIMPLE && FileUtils.getLevel(p) < 3){
            new MessagesUtils().TransferSimpleNotEnough(sender);
            return Command.SINGLE_SUCCESS;
        }
        else if(FileUtils.getPluginMode() == PluginMode.ADVANCED && FileUtils.getLevel(p) < 3){
            new MessagesUtils().TransferAdvancedNotEnough(sender);
            return  Command.SINGLE_SUCCESS;
        }

        InvUtils iu = new InvUtils();

        iu.TransferVanillaChestToEnderEx(sender, user.uuid(), false);

        return Command.SINGLE_SUCCESS;
    }
    private static int forcetransfer(CommandContext<CommandSourceStack> ctx){
        CommandSender sender = ctx.getSource().getSender();

        String target = StringArgumentType.getString(ctx, "target");

        ResolvedUser user = validateAndResolveTarget(sender, target, false);
        if (user == null){
            return Command.SINGLE_SUCCESS;
        }

        if (!user.online()){
            new MessagesUtils().TransferPlayerNotOnline(sender);
            return Command.SINGLE_SUCCESS;
        }

        Player p = plugin.getServer().getPlayer(user.uuid());

        if(FileUtils.getPluginMode() == PluginMode.SIMPLE && FileUtils.getLevel(p) < 3){
            new MessagesUtils().TransferSimpleNotEnough(sender);
            return Command.SINGLE_SUCCESS;
        }
        else if(FileUtils.getPluginMode() == PluginMode.ADVANCED && FileUtils.getLevel(p) < 3){
            new MessagesUtils().TransferAdvancedNotEnough(sender);
            return  Command.SINGLE_SUCCESS;
        }

        InvUtils iu = new InvUtils();

        iu.TransferVanillaChestToEnderEx(sender, user.uuid(), true);

        return Command.SINGLE_SUCCESS;
    }
    private static int usermapsearch(CommandContext<CommandSourceStack> ctx){
        CommandSender sender = ctx.getSource().getSender();

        String target = StringArgumentType.getString(ctx, "target");

        ResolvedUser user = validateAndResolveTarget(sender, target, true);

        return Command.SINGLE_SUCCESS;
    }
    private static int usermaplist(CommandContext<CommandSourceStack> ctx){
        CommandSender sender = ctx.getSource().getSender();
        int page = IntegerArgumentType.getInteger(ctx, "page");
        boolean pageValid = false;
        try{
            pageValid = plugin.getDatabaseManager().isPageValid(page);
        } catch (SQLException e){
            new MessagesUtils().DatabaseInternalError(sender);
        }


        if(!pageValid){
            new MessagesUtils().InvalidPage(sender);
            return Command.SINGLE_SUCCESS;
        }

        List<UserEntry> userEntries = new ArrayList<>();

        try {
            userEntries = plugin.getDatabaseManager().getUsersByPage(page);
        } catch (SQLException e){
            new MessagesUtils().DatabaseInternalError(sender);
        }

        if(userEntries == null){
            new MessagesUtils().DatabaseInternalError(sender);
            return  Command.SINGLE_SUCCESS;
        }

        new MessagesUtils().UsermapList(sender, page, userEntries);

        return  Command.SINGLE_SUCCESS;
    }



    private static CompletableFuture<Suggestions> nameOrIdSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {

        String input = builder.getRemaining();

        if (input.isEmpty()) {
            builder.suggest("[NAME/ID]");
            return builder.buildFuture();
        }

        // Case 1: starts with digit(s)
        if (Character.isDigit(input.charAt(0))) {

            boolean found = false;

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input.toLowerCase())) {
                    builder.suggest(player.getName());
                    found = true;
                }
            }

            if(!found) {
                builder.suggest(input);
            }

            // Important: allow raw number as valid input
            return builder.buildFuture();
        }

        // Case 2: starts with letter or empty
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(input.toLowerCase())) {
                builder.suggest(player.getName());
            }
        }

        return builder.buildFuture();
    }

    private static @Nullable ResolvedUser validateAndResolveTarget(CommandSender sender, String input, Boolean usermapSearch) {

        // 1️⃣ NUMERIC → ID
        if (input.matches("\\d+")) {
            int id = Integer.parseInt(input);

            UserEntry entry;

            try {
                entry = plugin.getDatabaseManager().getUserByID(id);
            } catch (SQLException ex){
                plugin.getLogger().warning("Database error!");
                ex.printStackTrace();
                new MessagesUtils().DatabaseInternalError(sender);
                return null;
            }


            if (entry == null) {
                new MessagesUtils().NoUserWithThisIdFound(sender, id);
                return null;
            }

            if(usermapSearch){
                new MessagesUtils().UserInfoSearchId(sender, entry);
                return null;
            }

            Player online = Bukkit.getPlayerExact(entry.name());


            return new ResolvedUser(
                    entry.id(),
                    entry.name(),
                    entry.uuid(),
                    entry.platform(),
                    online != null
            );
        }

        // 2️⃣ ONLINE PLAYER (exact, case-insensitive)
        Player online = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getName().equalsIgnoreCase(input))
                .findFirst()
                .orElse(null);

        if (online != null && usermapSearch == false) {

            Platform platform = plugin.playerPlatformResolver(online.getUniqueId());

            return new ResolvedUser(
                    -1,
                    online.getName(),
                    online.getUniqueId(),
                    platform,
                    true
            );
        }

        // 3️⃣ USERMAP NAME
        List<UserEntry> users;
        try {
            users = plugin.getDatabaseManager().getUsersByName(input);
        } catch (SQLException e) {
            plugin.getLogger().warning("Database error!");
            e.printStackTrace();
            return null;
        }

        if (users.isEmpty()) {
            new MessagesUtils().NoUserWithThisNameFound(sender, input);
            return null;
        }

        if(usermapSearch){
            new MessagesUtils().UserInfoSearchName(sender, users);
            return null;
        }

        if (users.size() > 1) {
            new MessagesUtils().MultipleUserWithNameFound(sender, users);
            return null;
        }

        UserEntry entry = users.getFirst();
        return new ResolvedUser(
                entry.id(),
                entry.name(),
                entry.uuid(),
                entry.platform(),
                false
        );
    }


}
