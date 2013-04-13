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

    private Banlist banlist = null;

    @Before
    public void setUp() throws Exception
    {
        Persistence.getInstance().initialize(File.createTempFile("voxelguest_test_db", ".db"));
        banlist = new Banlist();
        assertTrue(banlist.ban(TEST_BAN_PLAYER, TEST_BAN_REASON));
    }

    @After
    public void tearDown() throws Exception
    {
        banlist = null;
        Persistence.getInstance().shutdown();
    }

    @Test
    public void testExceptionOnDoubleBan() throws Exception
    {
        assertFalse(banlist.ban(TEST_BAN_PLAYER, TEST_BAN_REASON));
    }

    @Test
    public void testFalseOnDoubleUnban() throws Exception
    {
        assertTrue(banlist.unban(TEST_BAN_PLAYER));
        assertFalse(banlist.unban(TEST_BAN_PLAYER));
    }

    @Test
    public void testBan() throws Exception
    {
        assertTrue(banlist.isPlayerBanned(TEST_BAN_PLAYER));
    }

    @Test
    public void testBanreason() throws Exception
    {
        assertEquals(banlist.whyIsPlayerBanned(TEST_BAN_PLAYER), TEST_BAN_REASON);
    }

    @Test
    public void testUnban() throws Exception
    {
        assertTrue(banlist.unban(TEST_BAN_PLAYER));
        assertFalse(banlist.isPlayerBanned(TEST_BAN_PLAYER));
    }
}
