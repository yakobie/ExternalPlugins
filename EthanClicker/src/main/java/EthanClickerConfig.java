import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("example")
public interface EthanClickerConfig extends Config
{
	@ConfigItem(
			position = 1,
			keyName = "toggle",
			name = "Toggle Clicker",
			description = "Drops Items in config above."
	)
	default Keybind toggle()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "randLow",
			name = "Minimum Delay",
			description = "wow can u read",
			position = 3
	)
	default int randLow()
	{
		return 70;
	}

	@ConfigItem(
			keyName = "randLower",
			name = "Maximum Delay",
			description = "wow can u read",
			position = 4
	)
	default int randHigh()
	{
		return 80;
	}
}
