package com.thevoxelbox.voxelguest.modules.greylist.stream;

import com.thevoxelbox.voxelguest.modules.greylist.GreylistConfiguration;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistDAO;
import com.thevoxelbox.voxelguest.modules.greylist.GreylistModule;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public final class StreamReader extends Thread
{
    private final Socket socket;
    private final GreylistModule module;

    public StreamReader(final Socket socket, final GreylistModule module)
    {
        this.module = module;
        this.socket = socket;
    }

    @Override
    public void run()
    {
        final List<String> list = this.readSocket();

        for (String name : list)
        {
            GreylistDAO.greylist(name);
        }
    }

    private synchronized List<String> readSocket()
    {
        try
        {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            List<String> list = new ArrayList<>();
            int numberOfGuests = in.readInt();

            for (int i = 0; i < numberOfGuests; i++)
            {
                String line = in.readUTF();
                String toAdd = interpretStreamInput(line);

                if (toAdd != null)
                {
                    if (!list.contains(toAdd))
                    {
                        list.add(toAdd);
                    }
                }

            }

            in.close();
            socket.close();
            return list;
        }
        catch (SocketException ex)
        {
            return Collections.emptyList();
        }
        catch (IOException ex)
        {
            return Collections.emptyList();
        }
    }

    private String interpretStreamInput(final String input)
    {
        String[] args = input.split("\\|");

        if (args[0].equals(((GreylistConfiguration) module.getConfiguration()).getStreamPasswordHash()))
        {
            return args[1];
        }

        return null;
    }
}
