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

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Random;
import java.util.concurrent.*;

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


	private void simLeftClick()
	{
		try
		{
			Robot leftClk = new Robot();
			leftClk.mousePress(InputEvent.BUTTON1_MASK);
			leftClk.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		catch (AWTException e)
		{
			e.printStackTrace();
		}
	}

	private static int randomDelay(int min, int max)
	{
		Random rand = new Random();
		int n = rand.nextInt(max) + 1;
		if (n < min)
		{
			n += min;
		}
		return n;
	}

	private void delayFirstClick()
	{
		final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.schedule(this::simLeftClick, randomDelay(config.randLow(), config.randHigh()), TimeUnit.MILLISECONDS);
		service.shutdown();
	}

	private void addSubscriptions()
	{
		eventBus.subscribe(GameTick.class, this, this::onGameTick);
	}


	private void onGameTick(GameTick event)
	{
		if(clickTog)
		{
			delayFirstClick();
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
