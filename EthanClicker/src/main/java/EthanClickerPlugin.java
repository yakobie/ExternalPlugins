import com.google.common.eventbus.Subscribe;
import com.google.inject.Binder;
import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.SoundEffectID;
import net.runelite.api.SoundEffectVolume;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.flexo.Flexo;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.Sound;
import net.runelite.client.input.KeyManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.util.HotkeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@PluginDescriptor(
	name = "EthanClicker",
	description = "im ethan bradberry",
	type = PluginType.EXTERNAL
)
public class EthanClickerPlugin extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(EthanClickerPlugin.class);

	@Inject
	private Client client;
	@Inject
	private ConfigManager configManager;
	@Inject
	private EthanClickerConfig config;
	@Inject
	private KeyManager keyManager;
	@Inject
	private MenuManager menuManager;
	@Inject
	private ItemManager itemManager;

	@Inject
	private EventBus eventBus;

	private Flexo flexo;
	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
	private ThreadPoolExecutor executorService = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
			new ThreadPoolExecutor.DiscardPolicy());

	private void addSubscriptions()
	{
		eventBus.subscribe(GameStateChanged.class, this, this::onGameStateChanged);
		eventBus.subscribe(GameTick.class, this, this::onGameTick);
	}


	private void onGameTick(GameTick event)
	{
		if(clickTog)
		{
			executorService.submit(() ->
			{
				flexo.delay(getMillis());
				flexo.mousePressAndRelease(1);
			});
		}
	}

	private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle())
	{
		@Override
		public void hotkeyPressed()
		{
			clickTog = !clickTog;
			//hotkey prressed AYY lets go

		}
	};

	private boolean loggedIn = false;
	private boolean clickTog = false;


	@Override
	protected void startUp() throws Exception
	{
		addSubscriptions();
		Flexo.client = client;
		keyManager.registerKeyListener(toggle);
		executorService.submit(() ->
		{
			flexo = null;
			try
			{
				flexo = new Flexo();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});	}

	@Override
	protected void shutDown() throws Exception
	{
		keyManager.unregisterKeyListener(toggle);
		flexo = null;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			loggedIn = true;
		}
	}

	@Override
	public void configure(Binder binder)
	{

	}

	@Provides
	EthanClickerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EthanClickerConfig.class);
	}

	private int getMillis()
	{
		return (int) (Math.random() * config.randLow() + config.randHigh());
	}

}
