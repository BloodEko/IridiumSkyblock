package com.iridium.iridiumteams.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.PermissionType;
import com.iridium.iridiumteams.Rank;
import com.iridium.iridiumteams.database.IridiumUser;
import com.iridium.iridiumteams.database.Team;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class PromoteCommand<T extends Team, U extends IridiumUser<T>> extends Command<T, U> {
    public PromoteCommand(List<String> args, String description, String syntax, String permission) {
        super(args, description, syntax, permission);
    }

    @Override
    public void execute(U user, T team, String[] args, IridiumTeams<T, U> iridiumTeams) {
        Player player = user.getPlayer();
        if (args.length != 1) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", iridiumTeams.getConfiguration().prefix)));
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
        U targetUser = iridiumTeams.getUserManager().getUser(targetPlayer);

        if (targetUser.getTeamID() != team.getId()) {
            player.sendMessage(StringUtils.color(iridiumTeams.getMessages().userNotInYourTeam.replace("%prefix%", iridiumTeams.getConfiguration().prefix)));
            return;
        }

        int nextRank = targetUser.getUserRank() + 1;

        if (!iridiumTeams.getUserRanks().containsKey(nextRank)
        || (nextRank >= user.getUserRank() && user.getUserRank() != Rank.OWNER.getId() && !user.isBypassing())
        || !iridiumTeams.getTeamManager().getTeamPermission(team, user, PermissionType.PROMOTE)
        || targetUser.getUserRank() == Rank.OWNER.getId()) {
            player.sendMessage(StringUtils.color(iridiumTeams.getMessages().cannotPromoteUser.replace("%prefix%", iridiumTeams.getConfiguration().prefix)));
            return;
        }

        targetUser.setUserRank(nextRank);

        for (U member : iridiumTeams.getTeamManager().getTeamMembers(team)) {
            Player teamMember = Bukkit.getPlayer(member.getUuid());
            if (teamMember != null) {
                if (teamMember.equals(player)) {
                    teamMember.sendMessage(StringUtils.color(iridiumTeams.getMessages().promotedPlayer
                            .replace("%player%", targetUser.getName())
                            .replace("%rank%", iridiumTeams.getUserRanks().get(nextRank).name)
                            .replace("%prefix%", iridiumTeams.getConfiguration().prefix)
                    ));
                } else {
                    teamMember.sendMessage(StringUtils.color(iridiumTeams.getMessages().userPromotedPlayer
                            .replace("%promoter%", player.getName())
                            .replace("%player%", targetUser.getName())
                            .replace("%rank%", iridiumTeams.getUserRanks().get(nextRank).name)
                            .replace("%prefix%", iridiumTeams.getConfiguration().prefix)
                    ));
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, IridiumTeams<T, U> iridiumTeams) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

}
