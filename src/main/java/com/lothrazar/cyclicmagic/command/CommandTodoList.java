package com.lothrazar.cyclicmagic.command;

import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilChat;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandTodoList extends BaseCommand implements ICommand {
	public static final String name = "todo";
	public CommandTodoList(String n, boolean op) {

		super(n, op);
	}

	private static final String	MODE_ADD		= "add";
	private static final String	MODE_REMOVE	= "delete";
	private static final String	MODE_SET		= "set";
	private static final String	MODE_GET		= "get";
	private static final String	NBT_KEY			= Const.MODID + "_todo";

	public static boolean				PERSIST_DEATH;

	@Override
	public String getCommandUsage(ICommandSender s) {
		return "/" + getCommandName() + " <" + MODE_GET + "|" + MODE_SET + "|" + MODE_ADD + "|" + MODE_REMOVE + "> <text>";
	}

	public static String getTodoForPlayer(EntityPlayer player) {
		String todoCurrent = player.getEntityData().getString(NBT_KEY);

		if (todoCurrent == null)
			todoCurrent = "";

		return todoCurrent;
	}

	public static void setTodoForPlayer(EntityPlayer player, String todoCurrent) {
		player.getEntityData().setString(NBT_KEY, todoCurrent);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) {
		EntityPlayer player = (EntityPlayer) icommandsender;

		String todoCurrent = getTodoForPlayer(player);

		// is the first argument empty
		if (args == null || args.length == 0 || args[0] == null || args[0].isEmpty()) {
			// player.addChatMessage(new
			// ChatComponentTranslation(getCommandUsage(icommandsender)));

			UtilChat.addChatMessage(player, getCommandUsage(icommandsender));

			return;
		}

		String message = "";

		if (args[0].equals(MODE_GET)) {
			// just display current in chat
			UtilChat.addChatMessage(player, getTodoForPlayer(player));
		}
		else if (args[0].equals(MODE_REMOVE)) {
			todoCurrent = "";
			args[0] = "";// remove the plus sign
		}
		else if (args[0].equals(MODE_ADD)) {
			for (int i = 1; i < args.length; i++) {
				message += " " + args[i];
			}

			if (todoCurrent.isEmpty())
				todoCurrent = message;
			else
				todoCurrent += " " + message;// so append
		}
		else if (args[0].equals(MODE_SET)) {
			// they just did /todo blah blah
			for (int i = 1; i < args.length; i++) {
				message += " " + args[i];
			}

			todoCurrent = message;// so replace
		}

		setTodoForPlayer(player, todoCurrent);
	}
}
