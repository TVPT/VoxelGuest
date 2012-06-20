package com.thevoxelbox.voxelguest;

import com.thevoxelbox.voxelguest.modules.BukkitEventWrapper;
import com.thevoxelbox.voxelguest.modules.MetaData;
import com.thevoxelbox.voxelguest.modules.Module;
import com.thevoxelbox.voxelguest.modules.ModuleEvent;
import com.thevoxelbox.voxelguest.modules.ModuleException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import org.bukkit.World;
import org.bukkit.event.block.SignChangeEvent;

/**
 *
 * @author thedeadlybutter
 */

@MetaData(name="SignLogger", description="Logs all signs")
public class SignLoggerModule extends Module {

    protected List<String> signLog = new ArrayList<String>();
    
    public SignLoggerModule() {
        super(SignLoggerModule.class.getAnnotation(MetaData.class));
    }
    
    @Override
    public void enable() throws ModuleException {
        
        try {
            
            File f = new File("plugins/VoxelGuest/signs/log.txt");

            
                if (!f.exists()) {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }

                Scanner scanner = new Scanner(f);
               
               while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    signLog.add(line);
               }
                
                
        } catch (IOException ex) {
           ex.printStackTrace();
        }
            
    }

    @Override
    public String getLoadMessage() {
        return "SignLogger Module enabled";
    }

    @Override
    public void disable() throws ModuleException {}
    
    @Override
    public void explorationToggle(boolean explorationSetting) {
        
    }
    
    
    @ModuleEvent(event=SignChangeEvent.class)
    public void signChangeEvent(BukkitEventWrapper wrapper) {
        
        SignChangeEvent event = (SignChangeEvent) wrapper.getEvent();
        
        String pname = event.getPlayer().getName();
        String lines[] = event.getLines();
        int xCord = event.getBlock().getX();
        int yCord = event.getBlock().getY();
        int zCord = event.getBlock().getZ();
        World world = event.getBlock().getWorld();
        
        String logString = getLogString(pname, lines, xCord, yCord, zCord, world);
        
        addSignLog(logString);
        
    }
    
    private void addSignLog(String logString){
        signLog.add(logString);
        
        try {
            
            File f = new File("plugins/VoxelGuest/signs/log.txt");
            PrintWriter pw;

            
                if (!f.exists()) {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }
                
                pw = new PrintWriter(new FileWriter(f, true));
                pw.append(logString + "\r\n");
                pw.close();

                
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    private String getLogString(String pname, String lines[], int xCord, int yCord, int zCord, World world){
        Calendar Current = Calendar.getInstance();
                int MO = Current.get(Calendar.MONTH);
                int D = Current.get(Calendar.DAY_OF_MONTH);
                int H = Current.get(Calendar.HOUR_OF_DAY);
                int M = Current.get(Calendar.MINUTE);
                int S = Current.get(Calendar.SECOND);
        return "[" + MO + "/" + D + "  " + H + ":" + M + ":" + S + "]" + " | " + pname + " | " + "' " + lines[0] + " ' " + lines[1] + " ' " + lines[2] + " ' " + lines[3] + " ' " + " | Location -> " + "X:" + xCord + " Y:" + yCord + " Z:" + zCord + " @ " + world.getName();        
    }

    
    
}

//String format: [time stamp]|Name|"Messages"|(X:# Y:# Z:# @ world)