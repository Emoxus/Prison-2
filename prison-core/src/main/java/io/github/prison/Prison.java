/*
 *  Prison is a Minecraft plugin for the prison game mode.
 *  Copyright (C) 2016 The Prison Team
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.prison;

import com.google.common.eventbus.EventBus;

import java.io.File;

import io.github.prison.commands.CommandHandler;
import io.github.prison.commands.PluginCommand;
import io.github.prison.modules.ModuleManager;
import io.github.prison.selection.SelectionManager;

/**
 * Entry point for implementations. <p> An instance of Prison can be retrieved using the static
 * {@link Prison#getInstance()} method, however in order to use the core libraries, you must call
 * {@link Prison#init(Platform)} with a valid {@link Platform} implementation.
 *
 * @author SirFaizdat
 * @since 3.0
 */
public class Prison {

    // Singleton

    private static Prison instance = null;
    private Platform platform;

    // Fields
    private File dataFolder;
    private ModuleManager moduleManager;
    private CommandHandler commandHandler;
    private SelectionManager selectionManager;
    private ConfigurationLoader configurationLoader;
    private ConfigurationLoader messagesLoader;
    private EventBus eventBus;
    private Patrons patrons;

    /**
     * Gets the current instance of this class. <p> An instance will always be available, but you
     * must call the {@link Prison#init(Platform)} method before you perform any other action.
     *
     * @return an instance of Prison.
     */
    public static Prison getInstance() {
        if (instance == null) instance = new Prison();
        return instance;
    }

    // Public methods

    /**
     * Initializes prison-core. In the implementations, this should be called when the plugin is
     * enabled. After this is called, every getter in this class will return a value.
     */
    public void init(Platform platform) {
        long startTime = System.currentTimeMillis();
        platform.log("&7> &dENABLE START &7 <");

        this.platform = platform;
        platform.log("&7Using platform &3%s&7.", platform.getClass().getName());

        this.dataFolder = new File(platform.getPluginDirectory(), "Core");
        if (!this.dataFolder.exists()) this.dataFolder.mkdir();

        this.messagesLoader = new ConfigurationLoader(getDataFolder(), "messages.json", Messages.class, Messages.VERSION);
        this.messagesLoader.loadConfiguration();

        this.configurationLoader = new ConfigurationLoader(getDataFolder(), "config.json", Configuration.class, Configuration.VERSION);
        this.configurationLoader.loadConfiguration();

        this.eventBus = new EventBus();
        this.moduleManager = new ModuleManager();
        this.commandHandler = new CommandHandler();
        this.selectionManager = new SelectionManager();

        this.commandHandler.registerCommands(new PrisonCommand());

        patrons = new Patrons();
        getPlatform().getScheduler().runTaskTimer(() -> patrons.getPatrons(), 0, 3600);

        platform.log("&7Enabled &3Prison v%s&7.", platform.getPluginVersion());
        platform.log("&7> &dENABLE COMPLETE &5(%dms) &7<", (System.currentTimeMillis() - startTime));
    }

    /**
     * Finalizes and unregisters instances in prison-core. In the implementations, this should be
     * called when the plugin is disabled.
     */
    public void deinit() {
        moduleManager.unregisterAll();
    }

    // Getters

    /**
     * Returns the Platform in use, which contains methods for interacting with the Minecraft server
     * on whichever implementation is currently being used.
     *
     * @return the {@link Platform}.
     */
    public Platform getPlatform() {
        return platform;
    }

    /**
     * Returns the core data folder, which is located at "/plugins/Prison/Core". This contains the
     * core config.json and messages.json files, as well as other global data.
     *
     * @return the {@link File}.
     */
    public File getDataFolder() {
        return dataFolder;
    }

    /**
     * Returns the configuration class, which contains each configuration option in a public
     * variable. It is loaded and saved via a {@link ConfigurationLoader}.
     *
     * @return the {@link Configuration}.
     */
    public Configuration getConfig() {
        return (Configuration) configurationLoader.getConfig();
    }

    /**
     * Returns the messages class, which contains each localization option in a public variable. It
     * is loaded and saved via a {@link ConfigurationLoader}.
     *
     * @return the {@link Messages}.
     */
    public Messages getMessages() {
        return (Messages) messagesLoader.getConfig();
    }

    /**
     * Returns the event bus, where event listeners can be registered and events can be posted.
     *
     * @return The {@link EventBus}.
     */
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Returns the module manager, which stores instances of all registered {@link
     * io.github.prison.modules.Module}s and manages their state.
     *
     * @return The {@link ModuleManager}.
     */
    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    /**
     * Returns the command handler, where command methods can be registered using the {@link
     * CommandHandler#registerCommands(Object)} method.
     *
     * @return The {@link CommandHandler}.
     */
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    /**
     * Returns the selection manager, which stores each player's current selection using Prison's
     * selection wand.
     *
     * @return The {@link SelectionManager}.
     */
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    /**
     * This method is mainly for the use of the command library. It retrieves a list of commands
     * from the platform, and then queries the list for a command with a certain label.
     *
     * @param label The command's label.
     * @return The {@link PluginCommand}, or null if no command exists by that label.
     */
    public PluginCommand getCommand(String label) {
        for (PluginCommand command : platform.getCommands())
            if (command.getLabel().equalsIgnoreCase(label)) return command;
        return null;
    }

    public Patrons getPatrons() {
        return this.patrons;
    }
}
