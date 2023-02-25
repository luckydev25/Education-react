package primal.util.text;

import org.bukkit.command.CommandSender;

/**
 * Represents a tag provider
 *
 * @author cyberpwn
 */
public interface ITagProvider
{
	/**
	 * Get the tag
	 *
	 * @param sender
	 *            the command sender who will see the tag
	 * @return the tag
	 */
    String getTag(CommandSender sender);

	/**
	 * Message the sender with the given tag
	 *
	 * @param sender
	 *            the sender
	 * @param msg
	 *            the message
	 */
    void msg(CommandSender sender, String msg);
}
