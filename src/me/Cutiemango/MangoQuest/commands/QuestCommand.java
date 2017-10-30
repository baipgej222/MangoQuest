package me.Cutiemango.MangoQuest.commands;

import org.bukkit.entity.Player;
import com.nisovin.shopkeepers.Shopkeeper;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestRewardManager;
import me.Cutiemango.MangoQuest.manager.PluginHooker;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.RewardCache;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class QuestCommand
{

	// Command: /mq quest args[1] args[2] args[3]
	public static void execute(Player sender, String args[])
	{
		if (args.length == 1)
		{
			sendHelp(sender);
			return;
		}
		else
			if (args.length >= 2)
			{
				switch (args[1])
				{
					case "list":
						QuestGUIManager.openJourney(sender);
						return;
					case "help":
						sendHelp(sender);
						return;
					case "trade":
						PluginHooker im = Main.instance.pluginHooker;
						if (im.hasCitizensEnabled())
						{
							NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[2]));
							if (npc == null || npc.getEntity().getLocation().distance(sender.getLocation()) > 20)
								return;
							Shopkeeper s = Main.instance.pluginHooker.getShopkeepers().getShopkeeperByEntity(npc.getEntity());
							if (s == null)
								return;
							else
							{
								sender.closeInventory();
								s.openTradingWindow(sender);
								return;
							}
						}
						return;
					case "reward":
						if (args.length >= 3)
						{
							switch (args[2])
							{
								case "select":
									if (QuestRewardManager.hasRewardCache(sender))
										return;
									if (args.length == 4)
									{
										Quest target = QuestUtil.getQuest(args[3]);
										if (target == null)
										{
											QuestChatManager.error(sender, I18n.locMsg("CommandInfo.QuestNotFound"));
											return;
										}
										QuestRewardManager.registerCache(sender, target);
									}
									return;
								case "add":
									if (!QuestRewardManager.hasRewardCache(sender))
										return;
									if (args.length == 4)
									{
										RewardCache cache = QuestRewardManager.getRewardCache(sender);
										cache.addChoice(Integer.parseInt(args[3]));
									}
									return;
								case "remove":
									if (!QuestRewardManager.hasRewardCache(sender))
										return;
									if (args.length == 4)
									{
										RewardCache cache = QuestRewardManager.getRewardCache(sender);
										cache.removeChoice(Integer.parseInt(args[3]));
									}
									return;
							}
						}
						return;
				}
				
				if (args.length < 3 || QuestStorage.Quests.get(args[2]) == null)
				{
					QuestChatManager.error(sender, I18n.locMsg("CommandInfo.QuestNotFound"));
					return;
				}
				Quest quest = QuestStorage.Quests.get(args[2]);
				QuestPlayerData qd = QuestUtil.getData(sender);
				switch (args[1])
				{
					case "view":
						if (!QuestUtil.getData(sender).isCurrentlyDoing(quest))
							QuestGUIManager.openGUI(sender, new QuestProgress(quest, sender));
						else
							QuestGUIManager.openGUI(sender, qd.getProgress(quest));
						return;
					case "take":
						qd.takeQuest(quest, true);
						return;
					case "cquit":
						if (!quest.isQuitable())
						{
							QuestChatManager.error(sender, I18n.locMsg("QuestQuitMsg.Denied"));
							return;
						}
						qd.quitQuest(quest);
						QuestGUIManager.openJourney(sender);
						return;
					case "quit":
						if (!quest.isQuitable())
						{
							QuestChatManager.error(sender, I18n.locMsg("QuestQuitMsg.Denied"));
							return;
						}
						QuestGUIManager.openQuitGUI(sender, quest);
						return;
					default:
						sendHelp(sender);
						return;
				}
			}
	}

	private static void sendHelp(Player p)
	{
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.Title"));
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.List"));
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.View"));
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.Take"));
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.Quit"));
	}

}
