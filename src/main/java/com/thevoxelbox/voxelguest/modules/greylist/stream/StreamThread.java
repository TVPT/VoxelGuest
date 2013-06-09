package com.thevoxelbox.voxelguest.modules.greylist.stream;

import com.thevoxelbox.voxelguest.modules.greylist.GreylistConfiguration;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistModule;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class StreamThread extends Thread
{
    private final GreylistModule module;
    private ServerSocket serverSocket;
    private List<StreamReader> readers = new ArrayList<>();

    public StreamThread(final GreylistModule module)
    {
        this.module = module;

        try
        {
            this.serverSocket = new ServerSocket(((GreylistConfiguration) module.getConfiguration()).getStreamPort());
        }
        catch (IOException ex)
        {
            Bukkit.getLogger().severe("Failed to start greylist stream. - Unable to create socket.");
            ex.printStackTrace();
        }
    }

    public void killProcesses()
    {
        for (StreamReader reader : readers)
        {
            if (reader != null && reader.isAlive())
            {
                reader.interrupt();
            }
        }
        try
        {
            serverSocket.close();
        }
        catch (IOException | NullPointerException ex)
        {
            ex.printStackTrace();
        }
        this.interrupt();
    }

    @Override
    public void run()
    {
        if (serverSocket == null)
        {
            return;
        }

        try
        {
            while (true)
            {
                StreamReader reader = new StreamReader(serverSocket.accept(), module);
                readers.add(reader);
                reader.start();
            }

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
