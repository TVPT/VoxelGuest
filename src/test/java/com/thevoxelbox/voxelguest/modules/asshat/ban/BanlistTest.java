package com.thevoxelbox.voxelguest.modules.asshat.ban;

import com.thevoxelbox.voxelguest.persistence.Persistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Monofraps
 */
public class BanlistTest
{
    private static final String TEST_BAN_PLAYER = "PeanutSmurf";
    private static final String TEST_BAN_REASON = "Coz you suck!";

    @Before
    public void setUp() throws Exception
    {
        Persistence.getInstance().initialize(File.createTempFile("voxelguest_test_db", ".db"));
        assertTrue(Banlist.ban(TEST_BAN_PLAYER, TEST_BAN_REASON));
    }

    @After
    public void tearDown() throws Exception
    {
        Persistence.getInstance().shutdown();
    }

    @Test
    public void testExceptionOnDoubleBan() throws Exception
    {
        assertFalse(Banlist.ban(TEST_BAN_PLAYER, TEST_BAN_REASON));
    }

    @Test
    public void testFalseOnDoubleUnban() throws Exception
    {
        assertTrue(Banlist.unban(TEST_BAN_PLAYER));
        assertFalse(Banlist.unban(TEST_BAN_PLAYER));
    }

    @Test
    public void testBan() throws Exception
    {
        assertTrue(Banlist.isPlayerBanned(TEST_BAN_PLAYER));
    }

    @Test
    public void testBanreason() throws Exception
    {
        assertEquals(Banlist.getPlayerBanreason(TEST_BAN_PLAYER), TEST_BAN_REASON);
    }

    @Test
    public void testUnban() throws Exception
    {
        assertTrue(Banlist.unban(TEST_BAN_PLAYER));
        assertFalse(Banlist.isPlayerBanned(TEST_BAN_PLAYER));
    }
}
