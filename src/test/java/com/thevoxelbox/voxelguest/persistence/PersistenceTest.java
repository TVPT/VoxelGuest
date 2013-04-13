package com.thevoxelbox.voxelguest.persistence;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * @author Monofraps
 */
public class PersistenceTest
{
    private void initDb() throws Exception
    {
        Persistence persistence = Persistence.getInstance();
        assertNotNull(persistence);
        assertSame(persistence, Persistence.getInstance());

        persistence.initialize(File.createTempFile("voxelguest_tset_db", ".db"));
    }

    private void shutdownDb() throws Exception
    {
        Persistence.getInstance().shutdown();
    }

    @Test(expected = IllegalStateException.class)
    public void persistenceSystemThrowsWhenSaveIsCalledUninitialized()
    {
        Persistence persistence = Persistence.getInstance();
        assertNotNull(persistence);

        persistence.save(new DummyDataModel());
    }

    @Test(expected = IllegalStateException.class)
    public void persistenceSystemThrowsWhenSaveAllIsCalledUninitialized()
    {
        Persistence persistence = Persistence.getInstance();
        assertNotNull(persistence);

        persistence.saveAll(new ArrayList<DummyDataModel>()
        {{
                add(new DummyDataModel());
                add(new DummyDataModel());
            }});
    }

    @Test(expected = IllegalStateException.class)
    public void persistenceSystemThrowsWhenLoadAllIsCalledUninitialized()
    {
        Persistence persistence = Persistence.getInstance();
        assertNotNull(persistence);

        persistence.loadAll(DummyDataModel.class);
    }

    @Test
    public void persistenceSystemCanStoreAndLoadData() throws Exception
    {
        initDb();

        Persistence persistence = Persistence.getInstance();

        DummyDataModel originalData = new DummyDataModel();
        originalData.setData("Hello World!");
        persistence.save(originalData);

        final List<DummyDataModel> objects = persistence.loadAll(DummyDataModel.class);
        assertEquals(objects.size(), 1);
        final DummyDataModel dataFromDb = objects.get(0);

        assertEquals(originalData.getData(), dataFromDb.getData());

        shutdownDb();
    }

    @Test
    public void persistenceSystemCanStoreAndDeleteData() throws Exception
    {
        initDb();

        Persistence persistence = Persistence.getInstance();

        // save
        DummyDataModel originalData = new DummyDataModel();
        originalData.setData("Hello World!");
        persistence.save(originalData);

        // load back
        List<DummyDataModel> objects = persistence.loadAll(DummyDataModel.class);
        assertEquals(objects.size(), 1);
        final DummyDataModel dataFromDb = objects.get(0);

        assertEquals(originalData.getData(), dataFromDb.getData());

        // delete
        persistence.delete(dataFromDb);

        // read back to make sure entry is deleted
        objects.clear();
        objects = persistence.loadAll(DummyDataModel.class);
        assertEquals(objects.size(), 0);

        shutdownDb();
    }

    @DatabaseTable(tableName = "dummy")
    private static class DummyDataModel
    {
        @DatabaseField(generatedId = true)
        private int id;
        @DatabaseField
        private String data;

        private DummyDataModel()
        {
        }

        public String getData()
        {
            return data;
        }

        public void setData(final String data)
        {
            this.data = data;
        }
    }


}
