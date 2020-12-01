package UserCommands;

import Command.Command;
import Management.BotInfo;
import lombok.Getter;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;

public class Github extends Command {
    @Getter
    public static String help = "Returns the repo of the bot";

    public Github(DiscordApi api) {
        super(api);
        api.addMessageCreateListener(event -> {
            inviteBot(api, super.getChannel(), super.getMessage(), super.getMessageAuthor());
        });
    }

    private void inviteBot(DiscordApi api, TextChannel channel, Message message, MessageAuthor messageAuthor) {
        if (!onCommand(api, channel, message, messageAuthor)) {
            return;
        }
        channel.sendMessage("Here is the repo! : " + BotInfo.getBotRepo());
    }
}
