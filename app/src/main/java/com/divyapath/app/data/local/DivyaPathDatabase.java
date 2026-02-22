package com.divyapath.app.data.local;
import android.content.Context; import android.database.Cursor;
import androidx.annotation.NonNull; import androidx.room.*; import androidx.sqlite.db.SupportSQLiteDatabase;
import com.divyapath.app.data.local.dao.*; import com.divyapath.app.data.local.entity.*;
import androidx.room.migration.Migration;
import java.util.concurrent.ExecutorService; import java.util.concurrent.Executors;
@Database(entities={DeityEntity.class,AartiEntity.class,ChalisaEntity.class,MantraEntity.class,FestivalEntity.class,BookmarkEntity.class,TempleEntity.class,BhajanEntity.class,StotraEntity.class,ShraddhaEntity.class}, version=9, exportSchema=false)
public abstract class DivyaPathDatabase extends RoomDatabase {
    public abstract DeityDao deityDao(); public abstract AartiDao aartiDao(); public abstract ChalisaDao chalisaDao();
    public abstract MantraDao mantraDao(); public abstract FestivalDao festivalDao(); public abstract BookmarkDao bookmarkDao();
    public abstract TempleDao templeDao();
    public abstract BhajanDao bhajanDao(); public abstract StotraDao stotraDao();
    public abstract ShraddhaDao shraddhaDao();
    private static volatile DivyaPathDatabase INSTANCE;
    private static volatile boolean seedQueued = false;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);
    public static DivyaPathDatabase getDatabase(final Context context) {
        if (INSTANCE==null) { synchronized(DivyaPathDatabase.class) { if (INSTANCE==null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DivyaPathDatabase.class, "divyapath_database").fallbackToDestructiveMigrationFrom(1,2,3,4).addMigrations(MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9).addCallback(sCallback).build();
        }}} return INSTANCE;
    }
    private static boolean isTableEmpty(SupportSQLiteDatabase db, String table) {
        Cursor c = db.query("SELECT COUNT(*) FROM " + table);
        try { return !c.moveToFirst() || c.getLong(0) == 0; }
        finally { c.close(); }
    }
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `temples` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `nameHindi` TEXT, `location` TEXT, `youtubeUrl` TEXT, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `timings` TEXT, `imageUrl` TEXT, `description` TEXT, `hasLiveDarshan` INTEGER NOT NULL)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `bhajans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `deityId` INTEGER NOT NULL, `title` TEXT, `titleHindi` TEXT, `lyricsHindi` TEXT, `lyricsEnglish` TEXT, `audioUrl` TEXT, `duration` INTEGER NOT NULL, `category` TEXT, `language` TEXT, FOREIGN KEY(`deityId`) REFERENCES `deities`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_bhajans_deityId` ON `bhajans` (`deityId`)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `stotras` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `deityId` INTEGER NOT NULL, `title` TEXT, `titleHindi` TEXT, `textSanskrit` TEXT, `textHindi` TEXT, `textEnglish` TEXT, `audioUrl` TEXT, `duration` INTEGER NOT NULL, `verseCount` INTEGER NOT NULL, FOREIGN KEY(`deityId`) REFERENCES `deities`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_stotras_deityId` ON `stotras` (`deityId`)");
        }
    };
    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `shraddha` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT, `relationship` TEXT, " +
                    "`tithiIndex` INTEGER NOT NULL DEFAULT 0, " +
                    "`lunarMonth` INTEGER NOT NULL DEFAULT 1, " +
                    "`isAnnual` INTEGER NOT NULL DEFAULT 1, " +
                    "`mantraId` INTEGER NOT NULL DEFAULT 0, " +
                    "`notes` TEXT, `createdAt` INTEGER NOT NULL DEFAULT 0)");
        }
    };
    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add audio source columns to aartis table
            database.execSQL("ALTER TABLE aartis ADD COLUMN archiveOrgUrl TEXT");
            database.execSQL("ALTER TABLE aartis ADD COLUMN iskconUrl TEXT");
            database.execSQL("ALTER TABLE aartis ADD COLUMN localAssetName TEXT");
            database.execSQL("ALTER TABLE aartis ADD COLUMN audioSource TEXT");
            database.execSQL("ALTER TABLE aartis ADD COLUMN isCached INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE aartis ADD COLUMN cachedFilePath TEXT");

            // Add audio source columns to chalisas table
            database.execSQL("ALTER TABLE chalisas ADD COLUMN archiveOrgUrl TEXT");
            database.execSQL("ALTER TABLE chalisas ADD COLUMN iskconUrl TEXT");
            database.execSQL("ALTER TABLE chalisas ADD COLUMN localAssetName TEXT");
            database.execSQL("ALTER TABLE chalisas ADD COLUMN audioSource TEXT");
            database.execSQL("ALTER TABLE chalisas ADD COLUMN isCached INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE chalisas ADD COLUMN cachedFilePath TEXT");

            // Backfill localAssetName from existing audioUrl
            database.execSQL("UPDATE aartis SET localAssetName = audioUrl WHERE audioUrl IS NOT NULL AND audioUrl NOT LIKE 'http%'");
            database.execSQL("UPDATE chalisas SET localAssetName = audioUrl WHERE audioUrl IS NOT NULL AND audioUrl NOT LIKE 'http%'");
        }
    };
    static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add audio source columns to bhajans table
            database.execSQL("ALTER TABLE bhajans ADD COLUMN archiveOrgUrl TEXT");
            database.execSQL("ALTER TABLE bhajans ADD COLUMN iskconUrl TEXT");
            database.execSQL("ALTER TABLE bhajans ADD COLUMN localAssetName TEXT");
            database.execSQL("ALTER TABLE bhajans ADD COLUMN audioSource TEXT");
            database.execSQL("ALTER TABLE bhajans ADD COLUMN isCached INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE bhajans ADD COLUMN cachedFilePath TEXT");

            // Add audio source columns to mantras table
            database.execSQL("ALTER TABLE mantras ADD COLUMN archiveOrgUrl TEXT");
            database.execSQL("ALTER TABLE mantras ADD COLUMN iskconUrl TEXT");
            database.execSQL("ALTER TABLE mantras ADD COLUMN localAssetName TEXT");
            database.execSQL("ALTER TABLE mantras ADD COLUMN audioSource TEXT");
            database.execSQL("ALTER TABLE mantras ADD COLUMN isCached INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE mantras ADD COLUMN cachedFilePath TEXT");

            // Add audio source columns to stotras table
            database.execSQL("ALTER TABLE stotras ADD COLUMN archiveOrgUrl TEXT");
            database.execSQL("ALTER TABLE stotras ADD COLUMN iskconUrl TEXT");
            database.execSQL("ALTER TABLE stotras ADD COLUMN localAssetName TEXT");
            database.execSQL("ALTER TABLE stotras ADD COLUMN audioSource TEXT");
            database.execSQL("ALTER TABLE stotras ADD COLUMN isCached INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE stotras ADD COLUMN cachedFilePath TEXT");

            // Backfill localAssetName from existing audioUrl
            database.execSQL("UPDATE bhajans SET localAssetName = audioUrl WHERE audioUrl IS NOT NULL AND audioUrl NOT LIKE 'http%'");
            database.execSQL("UPDATE mantras SET localAssetName = audioUrl WHERE audioUrl IS NOT NULL AND audioUrl NOT LIKE 'http%'");
            database.execSQL("UPDATE stotras SET localAssetName = audioUrl WHERE audioUrl IS NOT NULL AND audioUrl NOT LIKE 'http%'");
        }
    };
    private static final Callback sCallback = new Callback() {
        @Override public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            seedQueued = true;
            databaseWriteExecutor.execute(() -> { if(INSTANCE!=null) DatabaseSeeder.seedDatabase(INSTANCE); });
        }
        @Override public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
            super.onDestructiveMigration(db);
            seedQueued = true;
            databaseWriteExecutor.execute(() -> { if(INSTANCE!=null) DatabaseSeeder.seedDatabase(INSTANCE); });
        }
        @Override public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // Always deduplicate on every app launch to clean up any existing duplicates.
            try {
                db.execSQL("DELETE FROM deities WHERE id NOT IN (SELECT MIN(id) FROM deities GROUP BY name)");
                db.execSQL("DELETE FROM aartis WHERE id NOT IN (SELECT MIN(id) FROM aartis GROUP BY title)");
                db.execSQL("DELETE FROM chalisas WHERE id NOT IN (SELECT MIN(id) FROM chalisas GROUP BY title)");
                db.execSQL("DELETE FROM mantras WHERE id NOT IN (SELECT MIN(id) FROM mantras GROUP BY title)");
                db.execSQL("DELETE FROM bhajans WHERE id NOT IN (SELECT MIN(id) FROM bhajans GROUP BY title)");
                db.execSQL("DELETE FROM stotras WHERE id NOT IN (SELECT MIN(id) FROM stotras GROUP BY title)");
                db.execSQL("DELETE FROM temples WHERE id NOT IN (SELECT MIN(id) FROM temples GROUP BY name)");
            } catch (Exception ignored) {}
            // Skip if onCreate/onDestructiveMigration already queued a seed
            if (seedQueued) { seedQueued = false; return; }
            // For existing installs: backfill audio URLs
            databaseWriteExecutor.execute(() -> {
                if (INSTANCE != null) {
                    if (INSTANCE.deityDao().getCount() == 0) {
                        DatabaseSeeder.seedDatabase(INSTANCE);
                    } else {
                        DatabaseSeeder.backfillAllAudioUrls(INSTANCE);
                    }
                }
            });
        }
    };
}
