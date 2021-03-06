package me.Cutiemango.MangoQuest.book;

import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestBookPage
{

	public QuestBookPage()
	{
		page = new TextComponent("");
		textleft = new TextComponent("");
	}
	
	public QuestBookPage(TextComponent text)
	{
		page = text;
		textleft = new TextComponent("");
	}

	private TextComponent page;
	private TextComponent textleft;
	private int lineUsed = 1;
	
	public void endNormally()
	{
		if (textleft.toPlainText() != "")
		{
			page.addExtra(textleft);
			textleft = new TextComponent("");
		}
	}

	public void changeLine()
	{
		endNormally();
		page.addExtra("\n");
		lineUsed+=1;
	}

	public QuestBookPage add(String s)
	{
		if (s == "\n")
		{
			changeLine();
			return this;
		}
		s = textleft.toPlainText() + s;
		TextAlignment align = new TextAlignment(s, lineUsed);
		if (align.calculateCharSize(s) >= TextAlignment.MAXIUM_CHAR_PER_LINE)
			page.addExtra(align.getResult());
		textleft = new TextComponent(QuestChatManager.translateColor(align.getLeft()));
		lineUsed = align.lineUsed();
		return this;
	}

	public QuestBookPage add(TextComponent t)
	{		
		t.addExtra(textleft);
		String s = t.toPlainText();
		TextAlignment align = new TextAlignment(s, lineUsed);
		if (align.calculateCharSize(s) >= TextAlignment.MAXIUM_CHAR_PER_LINE)
			page.addExtra(align.getResult());
		textleft = new TextComponent(QuestChatManager.translateColor(align.getLeft()));
		lineUsed = align.lineUsed();
		return this;
	}

	public QuestBookPage add(InteractiveText it)
	{
		String s = it.get().toPlainText();
		// 做一點標記
		// @： 這本書原本剩下來還沒加進去的字串 與 新加進來的字串之間
		// #： 加進字串後的終點標記
		s = textleft.toPlainText() + "@" + s + "#";
		// 丟進書本整理器
		TextAlignment align = new TextAlignment(s, lineUsed);
		// 如果整行字超過了一行最大字數
		if (align.calculateCharSize(s) >= TextAlignment.MAXIUM_CHAR_PER_LINE)
		{
			// 如果整理後的字串(結果)裡面有@標記的話
			if (align.getResult().contains("@"))
			{
				// 將其分開，先把沒有互動的字串加進書裡
				String[] firstsplit = align.getResult().split("@");
				page.addExtra(firstsplit[0]);
				// 檢查剩下來的字串
				for (int i = 1; i < firstsplit.length; i++)
				{
					// 如果剩下來的字串含有終止符號
					if (firstsplit[i].contains("#"))
					{
						// 將其再分開
						String[] secondsplit = firstsplit[i].split("#");
						page.addExtra(it.toggleAlignText(secondsplit[0]));
						page.addExtra(secondsplit[1]);
					}
					// 沒有的話就直接將其調整成互動字串，加進書本
					else
						page.addExtra(it.toggleAlignText(firstsplit[1]));
				}
			}
			// 沒有的話就直接加入
			else
				page.addExtra(align.getResult());
		}
		// 如果是剩下的字串含有@標記
		if (align.getLeft().contains("@"))
		{
			// 超極端狀況
			if (align.getLeft().startsWith("@") && align.getLeft().contains("#"))
			{
				String[] split = align.getLeft().split("#");
				textleft.addExtra(it.toggleAlignText(split[0].replace("@", "")));
				if (split.length > 1)
					textleft.addExtra(split[1]);
			}
			else
			{
				String[] firstsplit = align.getLeft().split("@");
				if (align.getLeft().contains("#"))
				{
					String[] secondsplit = firstsplit[1].split("#");
					textleft.addExtra(it.toggleAlignText(secondsplit[0]));
					if (secondsplit.length > 1)
						textleft.addExtra(secondsplit[1]);
				}
			}
		}
		else if (align.getLeft().contains("#") && !align.getLeft().contains("@"))
		{
			String[] split = align.getLeft().split("#");
			textleft.addExtra(it.toggleAlignText(split[0]));
		}
		lineUsed = align.lineUsed();
		return this;
	}

	public TextComponent getOriginalPage()
	{
		return page;
	}
	
	public QuestBookPage duplicate()
	{
		return new QuestBookPage((TextComponent)page.duplicate());
	}
	
	public TextComponent getTextleft()
	{
		return textleft;
	}
	
	public boolean pageOutOfBounds()
	{
		return lineUsed >= TextAlignment.MAXIUM_LINE_PER_PAGE;
	}
}
