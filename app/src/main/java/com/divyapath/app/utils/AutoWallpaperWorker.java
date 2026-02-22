package com.divyapath.app.utils;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoWallpaperWorker extends Worker {

    public AutoWallpaperWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            PreferenceManager pm = new PreferenceManager(getApplicationContext());
            String category = pm.getAutoWallpaperCategory();

            List<String> urls = getWallpaperUrls(category);
            if (urls.isEmpty()) return Result.failure();

            String url = urls.get(new Random().nextInt(urls.size()));

            Bitmap bitmap = Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(url)
                    .submit()
                    .get();

            WallpaperManager.getInstance(getApplicationContext()).setBitmap(bitmap);
            return Result.success();
        } catch (Exception e) {
            return Result.retry();
        }
    }

    public static List<String> getWallpaperUrls(String category) {
        List<String> urls = new ArrayList<>();
        List<String[]> all = getAllWallpapers();
        for (String[] entry : all) {
            if ("All".equals(category) || entry[1].equals(category)) {
                urls.add(entry[2]);
            }
        }
        return urls;
    }

    public static List<String[]> getAllWallpapers() {
        List<String[]> items = new ArrayList<>();
        // Format: {name, category, imageUrl}
        // All images from Unsplash (free, no auth required, direct CDN URLs)

        // Ganesh
        items.add(new String[]{"Ganesh Chaturthi Idol", "Ganesh",
                "https://images.unsplash.com/photo-1567591414240-e9c1e59f3e06?w=1080&q=80"});
        items.add(new String[]{"Ganesha Idol", "Ganesh",
                "https://images.unsplash.com/photo-1607604760190-ec9ccc12156e?w=1080&q=80"});
        items.add(new String[]{"Dagdushet Ganpati", "Ganesh",
                "https://images.unsplash.com/photo-1598090216740-eb040d8c3f82?w=1080&q=80"});
        items.add(new String[]{"Ganesha Display", "Ganesh",
                "https://images.unsplash.com/photo-1599379126660-d10b7e1831c8?w=1080&q=80"});
        items.add(new String[]{"Ganesh Festival", "Ganesh",
                "https://images.unsplash.com/photo-1563777389189-5e3564e5961c?w=1080&q=80"});

        // Shiva
        items.add(new String[]{"Adiyogi Shiva", "Shiva",
                "https://images.unsplash.com/photo-1653282767171-c889318ce51c?w=1080&q=80"});
        items.add(new String[]{"Shiva Meditation", "Shiva",
                "https://images.unsplash.com/photo-1652714145473-eea2563d9e68?w=1080&q=80"});
        items.add(new String[]{"Shiva Frame", "Shiva",
                "https://images.unsplash.com/photo-1590659948963-caafdecdfe64?w=1080&q=80"});
        items.add(new String[]{"Shiva Aazhimala", "Shiva",
                "https://images.unsplash.com/photo-1614836978715-21d79e427450?w=1080&q=80"});
        items.add(new String[]{"Shiva Statue", "Shiva",
                "https://images.unsplash.com/photo-1671471043465-25855822d55b?w=1080&q=80"});

        // Krishna
        items.add(new String[]{"Laddoo Gopal", "Krishna",
                "https://images.unsplash.com/photo-1653282766911-40082a8ec989?w=1080&q=80"});
        items.add(new String[]{"Krishna Gold", "Krishna",
                "https://images.unsplash.com/photo-1597237698944-f17195950882?w=1080&q=80"});
        items.add(new String[]{"Radha Krishna", "Krishna",
                "https://images.unsplash.com/photo-1590228947699-5f1fa1d86458?w=1080&q=80"});
        items.add(new String[]{"Krishna Statue", "Krishna",
                "https://images.unsplash.com/photo-1740750047365-3cd46bced114?w=1080&q=80"});
        items.add(new String[]{"Hindu Deities", "Krishna",
                "https://images.unsplash.com/photo-1527221579996-0de6d1ae2069?w=1080&q=80"});

        // Hanuman
        items.add(new String[]{"Hanuman Gold", "Hanuman",
                "https://images.unsplash.com/photo-1583089892943-e02e5b017b6a?w=1080&q=80"});
        items.add(new String[]{"Hanuman Crown", "Hanuman",
                "https://images.unsplash.com/photo-1730191567375-e82ce67160df?w=1080&q=80"});
        items.add(new String[]{"Hanuman Large", "Hanuman",
                "https://images.unsplash.com/photo-1707833684948-11bd776ffdef?w=1080&q=80"});
        items.add(new String[]{"Hanuman Staff", "Hanuman",
                "https://images.unsplash.com/photo-1686582557983-0df22fd25187?w=1080&q=80"});
        items.add(new String[]{"Hanuman Classic", "Hanuman",
                "https://images.unsplash.com/photo-1564984069790-2d0767de5856?w=1080&q=80"});

        // Durga
        items.add(new String[]{"Maa Durga Worship", "Durga",
                "https://plus.unsplash.com/premium_photo-1675578713697-8fe7f744cc75?w=1080&q=80"});
        items.add(new String[]{"Durga Idol Mumbai", "Durga",
                "https://images.unsplash.com/photo-1600867161364-67e000733952?w=1080&q=80"});
        items.add(new String[]{"Durga Multi-Armed", "Durga",
                "https://images.unsplash.com/photo-1760679674585-0ff853563c5f?w=1080&q=80"});
        items.add(new String[]{"Durga Ornate", "Durga",
                "https://images.unsplash.com/photo-1760295336466-18f2b1a12b14?w=1080&q=80"});
        items.add(new String[]{"Durga on Horse", "Durga",
                "https://images.unsplash.com/photo-1675611940263-5185e78e7b71?w=1080&q=80"});

        // Lakshmi
        items.add(new String[]{"Lakshmi Display", "Lakshmi",
                "https://images.unsplash.com/photo-1760679674298-f63844b9b36c?w=1080&q=80"});
        items.add(new String[]{"Durga Puja Thakur Bari", "Lakshmi",
                "https://images.unsplash.com/photo-1760344654214-97e2ceda7c4e?w=1080&q=80"});
        items.add(new String[]{"Lakshmi Seated", "Lakshmi",
                "https://images.unsplash.com/photo-1760295336635-819496770098?w=1080&q=80"});
        items.add(new String[]{"Deity on Throne", "Lakshmi",
                "https://plus.unsplash.com/premium_photo-1691030925369-c3edf9abcc0a?w=1080&q=80"});
        items.add(new String[]{"Lakshmi Devi Worship", "Lakshmi",
                "https://plus.unsplash.com/premium_photo-1674898515725-52fc2fcf52d3?w=1080&q=80"});

        // Vishnu
        items.add(new String[]{"Vishnu Shamlaji Temple", "Vishnu",
                "https://images.unsplash.com/photo-1651455035957-7113d7762a58?w=1080&q=80"});
        items.add(new String[]{"Vishnu Worship", "Vishnu",
                "https://images.unsplash.com/photo-1732965595974-063d833339de?w=1080&q=80"});
        items.add(new String[]{"Vishnu Deity Idol", "Vishnu",
                "https://plus.unsplash.com/premium_photo-1675597044994-1b04f8b3fec8?w=1080&q=80"});

        // Saraswati
        items.add(new String[]{"Saraswati Adorned", "Saraswati",
                "https://images.unsplash.com/photo-1741273573197-0b9e8ac1c033?w=1080&q=80"});
        items.add(new String[]{"Saraswati Radiant", "Saraswati",
                "https://images.unsplash.com/photo-1741273573807-21177923ead3?w=1080&q=80"});
        items.add(new String[]{"Saraswati Divine Wisdom", "Saraswati",
                "https://images.unsplash.com/photo-1741273574071-04ecb1a973bb?w=1080&q=80"});
        items.add(new String[]{"Saraswati Murti", "Saraswati",
                "https://images.unsplash.com/photo-1741273574468-faa2d4f9440e?w=1080&q=80"});

        // Ram
        items.add(new String[]{"Ram Darbar", "Ram",
                "https://images.unsplash.com/photo-1678593628844-6ea49dee8ce3?w=1080&q=80"});
        items.add(new String[]{"Ram Temple Deities", "Ram",
                "https://images.unsplash.com/photo-1678595150185-ef823ad412a3?w=1080&q=80"});
        items.add(new String[]{"Ram Lalla Ayodhya", "Ram",
                "https://images.unsplash.com/photo-1769634126808-c0f006eb254e?w=1080&q=80"});
        items.add(new String[]{"Ram Sita Laxman", "Ram",
                "https://images.unsplash.com/photo-1652521711266-67ec5768bfc2?w=1080&q=80"});
        items.add(new String[]{"Ram Lalla Idol", "Ram",
                "https://images.unsplash.com/photo-1759592370423-69ba372939e9?w=1080&q=80"});

        // Temples
        items.add(new String[]{"Akshardam Mandir", "Temples",
                "https://images.unsplash.com/photo-1729372982385-eab49b673f4c?w=1080&q=80"});
        items.add(new String[]{"Mandir Elephants", "Temples",
                "https://images.unsplash.com/photo-1729372982394-38cc518a1256?w=1080&q=80"});
        items.add(new String[]{"Temple Arches", "Temples",
                "https://images.unsplash.com/photo-1729372982406-2b0412ac6c3f?w=1080&q=80"});
        items.add(new String[]{"Ayodhya Temple", "Temples",
                "https://images.unsplash.com/photo-1652059468424-249066e3a98f?w=1080&q=80"});
        items.add(new String[]{"Ayodhya Courtyard", "Temples",
                "https://images.unsplash.com/photo-1652059468417-3b44ff25afdf?w=1080&q=80"});

        return items;
    }
}
