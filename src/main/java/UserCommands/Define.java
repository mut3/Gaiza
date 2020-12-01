package UserCommands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import Command.Command;
import lombok.Getter;
import lombok.SneakyThrows;
import org.checkerframework.checker.units.qual.A;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Management.BotInfo;
import Management.Keywords;

public class Define extends Command {
	@Getter
	public static String help = "Search urban dictionary. Usage [prefix]define [term]";
	private String command = "define";
	
	public Define(DiscordApi api) {
		super(api);
		api.addMessageCreateListener(e -> {
			urbanSearch(api, super.getChannel(), super.getMessage(), super.getMessageAuthor(), super.getArgs());
		});
	}

	@SneakyThrows
	private void urbanSearch(DiscordApi api, TextChannel channel, Message message, MessageAuthor messageAuthor, List<String> args) {
		if (!onCommand(api, channel, message, messageAuthor, args)) {
			return;
		}
		if (args.size() == 0) {
			channel.sendMessage("Please enter a search term");
		}

		StringBuilder term = new StringBuilder();
		for (String s : args) {
			term.append(s + " ");
		}
		Elements css = Jsoup.connect("https://www.urbandictionary.com/define.php?term=" + term.toString())
				.followRedirects(true)
				.ignoreHttpErrors(true)
				.userAgent("Mozilla/5.0")
				.get()
				.select("div.meaning");

		if (css.isEmpty()) {
			channel.sendMessage("Could not find term");
			return;
		}

		List<String> definition = new ArrayList<>();
		definition.add(css.get(0).text());
		definition.add(css.parents().select("div.example").get(0).text());
		definition.add(css.parents().select("div.contributor").get(0).text());

		channel.sendMessage(buildEmbed(term.toString(), messageAuthor, definition)).exceptionally(e -> {
			channel.sendMessage(definition.get(0));
			channel.sendMessage("Credit: Contributed " + definition.get(2));
			return null;
		});
	}

	private EmbedBuilder buildEmbed(String message, MessageAuthor messageAuthor, List<String> def) {
		EmbedBuilder embed = new EmbedBuilder()
				.setTitle(message.substring(0, 1).toUpperCase() + message.substring(1))
				.setColor(Color.magenta)
				.addField("Definition", def.get(0))
				.addField("Example:", def.get(1))
				.addField("Credit:", "Contributed " + def.get(2))
				.setTimestampToNow()
				.setFooter(BotInfo.getBotName(), BotInfo.getBotImage());
		return embed;
	}
}