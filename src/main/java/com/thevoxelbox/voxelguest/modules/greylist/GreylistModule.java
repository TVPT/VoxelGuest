package com.thevoxelbox.voxelguest.modules.greylist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.common.base.Preconditions;
import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationGetter;
import com.thevoxelbox.voxelguest.configuration.annotations.ConfigurationSetter;
import com.thevoxelbox.voxelguest.modules.GuestModule;
import com.thevoxelbox.voxelguest.modules.greylist.command.GreylistCommandExecutor;
import com.thevoxelbox.voxelguest.modules.greylist.command.UngreylistCommandExecutor;
import com.thevoxelbox.voxelguest.modules.greylist.listener.GreylistListener;
import com.thevoxelbox.voxelguest.modules.greylist.model.Greylistee;
import com.thevoxelbox.voxelguest.persistence.Persistence;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.hibernate.criterion.Restrictions;

/**
 * @author MikeMatrix
 */
public class GreylistModule extends GuestModule
{
    private GreylistListener greylistListener;
    private GreylistCommandExecutor greylistCommandExecutor;
    private UngreylistCommandExecutor ungreylistCommandExecutor;
    private boolean explorationMode = false;
    private String notGreylistedKickMessage = "You are not greylisted.";

    public GreylistModule()
    {
        setName("Greylist Module");
        greylistListener = new GreylistListener(this);
        greylistCommandExecutor = new GreylistCommandExecutor(this);
        ungreylistCommandExecutor = new UngreylistCommandExecutor(this);

	    Persistence.getInstance().registerPersistentClass(Greylistee.class);
    }

    @Override
    public Object getConfiguration()
    {
        return this;
    }

    @Override
    public void onEnable()
    {

        super.onEnable();
    }

    @Override
    public HashSet<Listener> getListeners()
    {
        final HashSet<Listener> listeners = new HashSet<>();
        listeners.add(greylistListener);
        return listeners;
    }

    @Override
    public HashMap<String, CommandExecutor> getCommandMappings()
    {
        HashMap<String, CommandExecutor> commandMapping = new HashMap<>();
        commandMapping.put("greylist", greylistCommandExecutor);
        commandMapping.put("ungreylist", ungreylistCommandExecutor);
        return commandMapping;
    }

    @ConfigurationGetter("exploration-mode")
    public boolean isExplorationMode()
    {
        return explorationMode;
    }

    @ConfigurationSetter("exploration-mode")
    public void setExplorationMode(final boolean explorationMode)
    {
        this.explorationMode = explorationMode;
    }

    @ConfigurationGetter("not-greylisted-kick-message")
    public String getNotGreylistedKickMessage()
    {
        return notGreylistedKickMessage;
    }

    @ConfigurationSetter("not-greylisted-kick-message")
    public void setNotGreylistedKickMessage(final String notGreylistedKickMessage)
    {
        this.notGreylistedKickMessage = notGreylistedKickMessage;
    }

    public final boolean isOnPersistentGreylist(String name)
    {
        final List<Object> greylistees = Persistence.getInstance().loadAll(Greylistee.class, Restrictions.like("name", name));

        for (Object greylisteeObject : greylistees)
        {
            if (greylisteeObject instanceof Greylistee)
            {
                if (((Greylistee) greylisteeObject).getName().equalsIgnoreCase(name))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void greylist(final String name)
    {
        final List<Object> greylistees = Persistence.getInstance().loadAll(Greylistee.class, Restrictions.like("name", name));
        for (Object greylisteeObject : greylistees)
        {
            Preconditions.checkState(greylisteeObject instanceof Greylistee);

            Greylistee greylistee = (Greylistee) greylisteeObject;
            if (greylistee.getName().equalsIgnoreCase(name))
            {
                return;
            }
        }
        Persistence.getInstance().save(new Greylistee(name));
    }

    public void ungreylist(final String name)
    {
        final List<Object> greylistees = Persistence.getInstance().loadAll(Greylistee.class, Restrictions.like("name", name));
        for (Object greylisteeObject : greylistees)
        {
            Preconditions.checkState(greylisteeObject instanceof Greylistee);

            Greylistee greylistee = (Greylistee) greylisteeObject;
            if (greylistee.getName().equalsIgnoreCase(name))
            {
                Persistence.getInstance().delete(greylistee);
            }
        }
    }
}
