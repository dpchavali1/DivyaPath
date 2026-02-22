package com.divyapath.app.data.local;

import androidx.sqlite.db.SupportSQLiteDatabase;

import com.divyapath.app.data.local.entity.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseSeeder {

    /**
     * Backfill Archive.org audio URLs for existing users who already have data.
     * Called on every app open to ensure all content has audio URLs populated.
     */
    public static void backfillAllAudioUrls(DivyaPathDatabase db) {
        // Insert Shani Dev aarti for existing users who don't have it yet
        if (db.aartiDao().getIdByTitle("Aarti Shani Dev Ki") == 0) {
            AartiEntity shaniAarti = new AartiEntity();
            shaniAarti.setDeityId(6);
            shaniAarti.setTitle("Aarti Shani Dev Ki");
            shaniAarti.setTitleHindi("आरती शनि देव की");
            shaniAarti.setLyricsHindi(
                    "जय जय श्री शनिदेव, भक्तन हितकारी ।\n" +
                    "सूर्य पुत्र प्रभु छाया, महतारी ॥\n" +
                    "जय जय श्री शनिदेव ॥\n\n" +
                    "श्याम अंग वक्र दृष्टि, चतुर्भुजा धारी ।\n" +
                    "नीलाम्बर धारण किये, कृत अम्बर भारी ॥\n" +
                    "जय जय श्री शनिदेव ॥\n\n" +
                    "किरीट मुकुट शीश, दिव्य छवि श्रेणी ।\n" +
                    "तनिक मंद हंसत, किंचित भृकुटि भेणी ॥\n" +
                    "जय जय श्री शनिदेव ॥\n\n" +
                    "कर में गदा त्रिशूल, धनुष वाण धारी ।\n" +
                    "पलना करत सदा, भक्तन भय हारी ॥\n" +
                    "जय जय श्री शनिदेव ॥\n\n" +
                    "पंचदेव में देव, सुनत जय जयकारी ।\n" +
                    "शनिदेव की आरती, जो कोई गावे ।\n" +
                    "कहत शनिश्चर देव, मनवांछित फल पावे ॥\n" +
                    "जय जय श्री शनिदेव ॥"
            );
            shaniAarti.setLyricsEnglish(
                    "Jai Jai Shri Shanidev, Bhaktan Hitkari\n" +
                    "Surya Putra Prabhu Chhaya, Mahtari\n" +
                    "Jai Jai Shri Shanidev\n\n" +
                    "Shyam Ang Vakra Drishti, Chaturbhuja Dhari\n" +
                    "Nilambar Dharan Kiye, Krit Ambar Bhari\n" +
                    "Jai Jai Shri Shanidev\n\n" +
                    "Panch Dev Mein Dev, Sunat Jai Jaikari\n" +
                    "Shanidev Ki Aarti, Jo Koi Gave\n" +
                    "Kahat Shanishchar Dev, Manvanchhit Phal Pave\n" +
                    "Jai Jai Shri Shanidev"
            );
            shaniAarti.setDuration(280);
            shaniAarti.setAudioUrl("raw:aarti_shani_dev_ki");
            db.aartiDao().insert(shaniAarti);
        }

        backfillAartiAudioUrls(db);
        backfillArchiveOrgUrls(db);
        backfillTempleDarshanUrls(db);
    }

    public static void seedDatabase(DivyaPathDatabase db) {
        deduplicateDeities(db);
        deduplicateAartis(db);
        seedDeities(db);
        seedAartis(db);
        seedChalisas(db);
        seedMantras(db);
        seedFestivals(db);
        seedTemples(db);
        seedBhajans(db);
        seedStotras(db);
        // Always backfill Archive.org URLs for all content types
        backfillArchiveOrgUrls(db);
    }

    /**
     * Remove duplicate deity entries caused by the race condition between
     * onDestructiveMigration and onOpen both queuing seedDatabase.
     * Keeps the entry with the lowest ID for each name.
     */
    private static void deduplicateDeities(DivyaPathDatabase db) {
        try {
            db.getOpenHelper().getWritableDatabase().execSQL(
                    "DELETE FROM deities WHERE id NOT IN " +
                    "(SELECT MIN(id) FROM deities GROUP BY name)");
        } catch (Exception ignored) {}
    }

    /**
     * Remove duplicate aarti entries. Keeps the entry with the lowest ID for each title.
     */
    private static void deduplicateAartis(DivyaPathDatabase db) {
        try {
            db.getOpenHelper().getWritableDatabase().execSQL(
                    "DELETE FROM aartis WHERE id NOT IN " +
                    "(SELECT MIN(id) FROM aartis GROUP BY title)");
        } catch (Exception ignored) {}
    }

    private static void seedDeities(DivyaPathDatabase db) {
        if (db.deityDao().getCount() > 0) return;

        List<DeityEntity> deities = new ArrayList<>();
        deities.add(new DeityEntity("Lord Shiva", "भगवान शिव",
                "deity_shiva", "The destroyer and transformer among the Hindu trinity",
                Calendar.MONDAY, "#4A148C"));
        deities.add(new DeityEntity("Lord Hanuman", "हनुमान जी",
                "deity_hanuman", "The devoted servant of Lord Rama, symbol of strength",
                Calendar.TUESDAY, "#E65100"));
        deities.add(new DeityEntity("Lord Ganesha", "श्री गणेश",
                "deity_ganesha", "The remover of obstacles, lord of beginnings",
                Calendar.WEDNESDAY, "#FF6B00"));
        deities.add(new DeityEntity("Lord Vishnu", "भगवान विष्णु",
                "deity_vishnu", "The preserver and protector of the universe",
                Calendar.THURSDAY, "#1565C0"));
        deities.add(new DeityEntity("Goddess Lakshmi", "माँ लक्ष्मी",
                "deity_lakshmi", "The goddess of wealth, fortune and prosperity",
                Calendar.FRIDAY, "#C62828"));
        deities.add(new DeityEntity("Lord Shani Dev", "शनि देव",
                "deity_shani", "The lord of justice, associated with planet Saturn",
                Calendar.SATURDAY, "#37474F"));
        deities.add(new DeityEntity("Lord Surya", "सूर्य देव",
                "deity_surya", "The Sun God, source of light and life",
                Calendar.SUNDAY, "#FF8F00"));
        deities.add(new DeityEntity("Goddess Durga", "माँ दुर्गा",
                "deity_durga", "The warrior goddess, protector of the righteous",
                -1, "#AD1457"));
        deities.add(new DeityEntity("Lord Krishna", "श्री कृष्ण",
                "deity_krishna", "The divine cowherd, speaker of the Bhagavad Gita",
                -1, "#1A237E"));
        deities.add(new DeityEntity("Goddess Saraswati", "माँ सरस्वती",
                "deity_saraswati", "The goddess of knowledge, music and arts",
                -1, "#FFFFFF"));
        deities.add(new DeityEntity("Lord Rama", "श्री राम",
                "deity_rama", "The ideal king, embodiment of dharma",
                -1, "#2E7D32"));

        db.deityDao().insertAll(deities);
    }

    private static void seedAartis(DivyaPathDatabase db) {
        if (db.aartiDao().getCount() > 0) {
            backfillAartiAudioUrls(db);
            backfillArchiveOrgUrls(db);
            return;
        }

        List<AartiEntity> aartis = new ArrayList<>();

        // Ganesh Aarti
        AartiEntity ganeshAarti = new AartiEntity();
        ganeshAarti.setDeityId(3);
        ganeshAarti.setTitle("Jai Ganesh Deva");
        ganeshAarti.setTitleHindi("जय गणेश देवा");
        ganeshAarti.setLyricsHindi(
                "जय गणेश जय गणेश जय गणेश देवा ।\n" +
                "माता जाकी पार्वती पिता महादेवा ॥\n\n" +
                "एकदन्त दयावन्त चार भुजा धारी ।\n" +
                "माथे पर तिलक सोहे मूसे की सवारी ॥\n" +
                "जय गणेश जय गणेश जय गणेश देवा ॥\n\n" +
                "पान चढ़े फूल चढ़े और चढ़े मेवा ।\n" +
                "लड्डुअन का भोग लगे सन्त करें सेवा ॥\n" +
                "जय गणेश जय गणेश जय गणेश देवा ॥\n\n" +
                "अन्धन को आँख देत कोढ़िन को काया ।\n" +
                "बाँझन को पुत्र देत निर्धन को माया ॥\n" +
                "जय गणेश जय गणेश जय गणेश देवा ॥\n\n" +
                "सूर श्याम शरण आए सफल कीजे सेवा ।\n" +
                "माता जाकी पार्वती पिता महादेवा ॥\n" +
                "जय गणेश जय गणेश जय गणेश देवा ॥"
        );
        ganeshAarti.setLyricsEnglish(
                "Jai Ganesh Jai Ganesh Jai Ganesh Deva\n" +
                "Mata Jaki Parvati Pita Mahadeva\n\n" +
                "Ek Dant Dayavant Char Bhuja Dhari\n" +
                "Mathe Par Tilak Sohe Muse Ki Savari\n" +
                "Jai Ganesh Jai Ganesh Jai Ganesh Deva\n\n" +
                "Paan Chadhe Phool Chadhe Aur Chadhe Meva\n" +
                "Ladduan Ka Bhog Lage Sant Karein Seva\n" +
                "Jai Ganesh Jai Ganesh Jai Ganesh Deva\n\n" +
                "Andhan Ko Ankh Det Kodhin Ko Kaya\n" +
                "Banjhan Ko Putra Det Nirdhan Ko Maya\n" +
                "Jai Ganesh Jai Ganesh Jai Ganesh Deva\n\n" +
                "Sur Shyam Sharan Aaye Saphal Kije Seva\n" +
                "Mata Jaki Parvati Pita Mahadeva\n" +
                "Jai Ganesh Jai Ganesh Jai Ganesh Deva"
        );
        ganeshAarti.setDuration(300);
        ganeshAarti.setAudioUrl("raw:aarti_jai_ganesh_deva");

        aartis.add(ganeshAarti);

        // Lakshmi Aarti
        AartiEntity lakshmiAarti = new AartiEntity();
        lakshmiAarti.setDeityId(5);
        lakshmiAarti.setTitle("Om Jai Lakshmi Mata");
        lakshmiAarti.setTitleHindi("ॐ जय लक्ष्मी माता");
        lakshmiAarti.setLyricsHindi(
                "ॐ जय लक्ष्मी माता, मैया जय लक्ष्मी माता ।\n" +
                "तुमको निशदिन सेवत, हरि विष्णु विधाता ॥\n" +
                "ॐ जय लक्ष्मी माता ॥\n\n" +
                "उमा रमा ब्रह्माणी, तुम ही जग माता ।\n" +
                "सूर्य चन्द्रमा ध्यावत, नारद ऋषि गाता ॥\n" +
                "ॐ जय लक्ष्मी माता ॥\n\n" +
                "दुर्गा रूप निरंजनी, सुख सम्पत्ति दाता ।\n" +
                "जो कोई तुमको ध्यावत, ऋद्धि सिद्धि धन पाता ॥\n" +
                "ॐ जय लक्ष्मी माता ॥\n\n" +
                "तुम पाताल निवासिनी, तुम ही शुभ दाता ।\n" +
                "कर्म प्रभाव प्रकाशिनी, भवनिधि की त्राता ॥\n" +
                "ॐ जय लक्ष्मी माता ॥\n\n" +
                "जिस घर में तुम रहतीं, सब सद्गुण आता ।\n" +
                "सब सम्भव हो जाता, मन नहीं घबराता ॥\n" +
                "ॐ जय लक्ष्मी माता ॥\n\n" +
                "तुम बिन यज्ञ न होते, वस्त्र न कोई पाता ।\n" +
                "खान पान का वैभव, सब तुमसे आता ॥\n" +
                "ॐ जय लक्ष्मी माता ॥\n\n" +
                "शुभ गुण मन्दिर सुन्दर, क्षीरोदधि जाता ।\n" +
                "रत्न चतुर्दश तुम बिन, कोई नहीं पाता ॥\n" +
                "ॐ जय लक्ष्मी माता ॥\n\n" +
                "महालक्ष्मी जी की आरती, जो कोई जन गाता ।\n" +
                "उर आनन्द समाता, पाप उतर जाता ॥\n" +
                "ॐ जय लक्ष्मी माता ॥"
        );
        lakshmiAarti.setLyricsEnglish(
                "Om Jai Lakshmi Mata, Maiya Jai Lakshmi Mata\n" +
                "Tumko Nishdin Sevat, Hari Vishnu Vidhata\n" +
                "Om Jai Lakshmi Mata\n\n" +
                "Uma Rama Brahmani, Tum Hi Jag Mata\n" +
                "Surya Chandrama Dhyavat, Narad Rishi Gata\n" +
                "Om Jai Lakshmi Mata"
        );
        lakshmiAarti.setDuration(360);
        lakshmiAarti.setAudioUrl("raw:aarti_om_jai_lakshmi_mata");

        aartis.add(lakshmiAarti);

        // Shiv Aarti
        AartiEntity shivAarti = new AartiEntity();
        shivAarti.setDeityId(1);
        shivAarti.setTitle("Om Jai Shiv Omkara");
        shivAarti.setTitleHindi("ॐ जय शिव ओंकारा");
        shivAarti.setLyricsHindi(
                "ॐ जय शिव ओंकारा, स्वामी जय शिव ओंकारा ।\n" +
                "ब्रह्मा विष्णु सदाशिव, अर्द्धांगी धारा ॥\n" +
                "ॐ जय शिव ओंकारा ॥\n\n" +
                "एकानन चतुरानन पंचानन राजे ।\n" +
                "हंसासन गरुड़ासन वृषवाहन साजे ॥\n" +
                "ॐ जय शिव ओंकारा ॥\n\n" +
                "दो भुज चार चतुर्भुज दशभुज अति सोहे ।\n" +
                "तीनों रूप निरखता त्रिभुवन जन मोहे ॥\n" +
                "ॐ जय शिव ओंकारा ॥\n\n" +
                "अक्षमाला वनमाला मुण्डमाला धारी ।\n" +
                "त्रिपुरारी कंसारी कर माला धारी ॥\n" +
                "ॐ जय शिव ओंकारा ॥"
        );
        shivAarti.setLyricsEnglish(
                "Om Jai Shiv Omkara, Swami Jai Shiv Omkara\n" +
                "Brahma Vishnu Sadashiv, Ardhangi Dhara\n" +
                "Om Jai Shiv Omkara"
        );
        shivAarti.setDuration(320);
        shivAarti.setAudioUrl("raw:aarti_om_jai_shiv_omkara");

        aartis.add(shivAarti);

        // Hanuman Aarti
        AartiEntity hanumanAarti = new AartiEntity();
        hanumanAarti.setDeityId(2);
        hanumanAarti.setTitle("Aarti Keeje Hanuman Lala Ki");
        hanumanAarti.setTitleHindi("आरती कीजै हनुमान लला की");
        hanumanAarti.setLyricsHindi(
                "आरती कीजै हनुमान लला की ।\n" +
                "दुष्ट दलन रघुनाथ कला की ॥\n\n" +
                "जाके बल से गिरवर काँपे ।\n" +
                "रोग दोष जाके निकट न झाँके ॥\n" +
                "आरती कीजै हनुमान लला की ॥\n\n" +
                "अंजनि पुत्र महा बलदाई ।\n" +
                "सन्तन के प्रभु सदा सहाई ॥\n" +
                "आरती कीजै हनुमान लला की ॥\n\n" +
                "दे बीरा रघुनाथ पठाए ।\n" +
                "लंका जलाइ सिया सुधि लाए ॥\n" +
                "आरती कीजै हनुमान लला की ॥\n\n" +
                "लंका सो कोट समुद्र सी खाई ।\n" +
                "जात पवनसुत बार न लाई ॥\n" +
                "आरती कीजै हनुमान लला की ॥\n\n" +
                "लंका जलाइ असुर सब मारे ।\n" +
                "सियाराम जी के काज सँवारे ॥\n" +
                "आरती कीजै हनुमान लला की ॥\n\n" +
                "लक्ष्मण मूर्छित पड़े सकारे ।\n" +
                "आणि सजीवन प्राण उबारे ॥\n" +
                "आरती कीजै हनुमान लला की ॥\n\n" +
                "पैठि पताल तोरि जम कारे ।\n" +
                "अहिरावण की भुजा उखारे ॥\n" +
                "आरती कीजै हनुमान लला की ॥\n\n" +
                "बाएँ भुजा असुर दल मारे ।\n" +
                "दाहिने भुजा सन्तजन तारे ॥\n" +
                "आरती कीजै हनुमान लला की ॥\n\n" +
                "सुर नर मुनि आरती उतारे ।\n" +
                "जै जै जै हनुमान उचारे ॥\n" +
                "आरती कीजै हनुमान लला की ॥"
        );
        hanumanAarti.setLyricsEnglish(
                "Aarti Keejai Hanuman Lala Ki\n" +
                "Dusht Dalan Raghunath Kala Ki"
        );
        hanumanAarti.setDuration(280);
        hanumanAarti.setAudioUrl("raw:aarti_hanuman_lala_ki");
        aartis.add(hanumanAarti);

        // Krishna Aarti
        AartiEntity krishnaAarti = new AartiEntity();
        krishnaAarti.setDeityId(9);
        krishnaAarti.setTitle("Aarti Kunj Bihari Ki");
        krishnaAarti.setTitleHindi("आरती कुंजबिहारी की");
        krishnaAarti.setLyricsHindi(
                "आरती कुंजबिहारी की ।\n" +
                "श्री गिरिधर कृष्ण मुरारी की ॥\n\n" +
                "गले में बैजंती माला, बजावे मुरली मधुर बाला ।\n" +
                "श्रवन में कुण्डल झलकाला, नन्द के आनन्द नन्दलाला ॥\n" +
                "गगन सम अंग कान्ति काली, राधिका चमक रही आली ।\n" +
                "लतन में ठाढ़े बनमाली, भ्रमर सी अलक, कस्तूरी तिलक लाली ॥\n" +
                "आरती कुंजबिहारी की ॥"
        );
        krishnaAarti.setLyricsEnglish(
                "Aarti Kunj Bihari Ki\n" +
                "Shri Giridhar Krishna Murari Ki"
        );
        krishnaAarti.setDuration(290);
        krishnaAarti.setAudioUrl("raw:aarti_kunj_bihari_ki");
        aartis.add(krishnaAarti);

        // Durga Aarti
        AartiEntity durgaAarti = new AartiEntity();
        durgaAarti.setDeityId(8);
        durgaAarti.setTitle("Jai Ambe Gauri");
        durgaAarti.setTitleHindi("जय अम्बे गौरी");
        durgaAarti.setLyricsHindi(
                "जय अम्बे गौरी, मैया जय श्यामा गौरी ।\n" +
                "तुमको निशदिन ध्यावत, हरि ब्रह्मा शिवरी ॥\n" +
                "जय अम्बे गौरी ॥\n\n" +
                "मांग सिन्दूर विराजत, टीको मृगमद को ।\n" +
                "उज्ज्वल से दो नैना, चन्द्रवदन नीको ॥\n" +
                "जय अम्बे गौरी ॥\n\n" +
                "कनक समान कलेवर, रक्ताम्बर राजे ।\n" +
                "रक्तपुष्प गल माला, कण्ठन पर साजे ॥\n" +
                "जय अम्बे गौरी ॥"
        );
        durgaAarti.setLyricsEnglish(
                "Jai Ambe Gauri, Maiya Jai Shyama Gauri\n" +
                "Tumko Nishdin Dhyavat, Hari Brahma Shivri"
        );
        durgaAarti.setDuration(310);
        durgaAarti.setAudioUrl("raw:aarti_jai_ambe_gauri");
        aartis.add(durgaAarti);

        // Saraswati Aarti
        AartiEntity saraswatiAarti = new AartiEntity();
        saraswatiAarti.setDeityId(10);
        saraswatiAarti.setTitle("Jai Saraswati Mata");
        saraswatiAarti.setTitleHindi("जय सरस्वती माता");
        saraswatiAarti.setLyricsHindi(
                "जय सरस्वती माता, मैया जय सरस्वती माता ।\n" +
                "सदगुण वैभव शालिनी, त्रिभुवन विख्याता ॥\n" +
                "जय सरस्वती माता ॥\n\n" +
                "चन्द्र वदनि पद्मासिनी, ध्यान धरूँ तेरा ।\n" +
                "कृपा दृष्टि निज भक्तन पर सदा रहे तेरा ॥\n" +
                "जय सरस्वती माता ॥"
        );
        saraswatiAarti.setLyricsEnglish(
                "Jai Saraswati Mata, Maiya Jai Saraswati Mata\n" +
                "Sadgun Vaibhav Shalini, Tribhuvan Vikhyata"
        );
        saraswatiAarti.setDuration(270);
        saraswatiAarti.setAudioUrl("raw:aarti_jai_saraswati_mata");
        aartis.add(saraswatiAarti);

        // Vishnu Aarti
        AartiEntity vishnuAarti = new AartiEntity();
        vishnuAarti.setDeityId(4);
        vishnuAarti.setTitle("Om Jai Jagdish Hare");
        vishnuAarti.setTitleHindi("ॐ जय जगदीश हरे");
        vishnuAarti.setLyricsHindi(
                "ॐ जय जगदीश हरे, स्वामी जय जगदीश हरे ।\n" +
                "भक्त जनों के संकट, दास जनों के संकट,\n" +
                "क्षण में दूर करे ॥\n" +
                "ॐ जय जगदीश हरे ॥\n\n" +
                "जो ध्यावे फल पावे, दुख बिनसे मन का,\n" +
                "स्वामी दुख बिनसे मन का ।\n" +
                "सुख सम्पत्ति घर आवे, सुख सम्पत्ति घर आवे,\n" +
                "कष्ट मिटे तन का ॥\n" +
                "ॐ जय जगदीश हरे ॥"
        );
        vishnuAarti.setLyricsEnglish(
                "Om Jai Jagdish Hare, Swami Jai Jagdish Hare\n" +
                "Bhakt Janon Ke Sankat, Das Janon Ke Sankat\n" +
                "Kshan Mein Door Kare"
        );
        vishnuAarti.setDuration(330);
        vishnuAarti.setAudioUrl("raw:aarti_om_jai_jagdish_hare");
        aartis.add(vishnuAarti);

        // Surya Aarti
        AartiEntity suryaAarti = new AartiEntity();
        suryaAarti.setDeityId(7);
        suryaAarti.setTitle("Aarti Surya Dev Ki");
        suryaAarti.setTitleHindi("आरती सूर्य देव की");
        suryaAarti.setLyricsHindi(
                "जय जय जय रवि देव, जय जय जय रवि देव ।\n" +
                "करत सदा जगमें हरि, कर सेवा करत सेव ॥\n" +
                "जय जय जय रवि देव ॥"
        );
        suryaAarti.setLyricsEnglish("Jai Jai Jai Ravi Dev");
        suryaAarti.setDuration(200);
        suryaAarti.setAudioUrl("raw:aarti_jai_ravi_dev");
        aartis.add(suryaAarti);

        // Shani Aarti
        AartiEntity shaniAarti = new AartiEntity();
        shaniAarti.setDeityId(6);
        shaniAarti.setTitle("Aarti Shani Dev Ki");
        shaniAarti.setTitleHindi("आरती शनि देव की");
        shaniAarti.setLyricsHindi(
                "जय जय श्री शनिदेव, भक्तन हितकारी ।\n" +
                "सूर्य पुत्र प्रभु छाया, महतारी ॥\n" +
                "जय जय श्री शनिदेव ॥\n\n" +
                "श्याम अंग वक्र दृष्टि, चतुर्भुजा धारी ।\n" +
                "नीलाम्बर धारण किये, कृत अम्बर भारी ॥\n" +
                "जय जय श्री शनिदेव ॥\n\n" +
                "किरीट मुकुट शीश, दिव्य छवि श्रेणी ।\n" +
                "तनिक मंद हंसत, किंचित भृकुटि भेणी ॥\n" +
                "जय जय श्री शनिदेव ॥\n\n" +
                "कर में गदा त्रिशूल, धनुष वाण धारी ।\n" +
                "पलना करत सदा, भक्तन भय हारी ॥\n" +
                "जय जय श्री शनिदेव ॥\n\n" +
                "पंचदेव में देव, सुनत जय जयकारी ।\n" +
                "शनिदेव की आरती, जो कोई गावे ।\n" +
                "कहत शनिश्चर देव, मनवांछित फल पावे ॥\n" +
                "जय जय श्री शनिदेव ॥"
        );
        shaniAarti.setLyricsEnglish(
                "Jai Jai Shri Shanidev, Bhaktan Hitkari\n" +
                "Surya Putra Prabhu Chhaya, Mahtari\n" +
                "Jai Jai Shri Shanidev\n\n" +
                "Shyam Ang Vakra Drishti, Chaturbhuja Dhari\n" +
                "Nilambar Dharan Kiye, Krit Ambar Bhari\n" +
                "Jai Jai Shri Shanidev\n\n" +
                "Panch Dev Mein Dev, Sunat Jai Jaikari\n" +
                "Shanidev Ki Aarti, Jo Koi Gave\n" +
                "Kahat Shanishchar Dev, Manvanchhit Phal Pave\n" +
                "Jai Jai Shri Shanidev"
        );
        shaniAarti.setDuration(280);
        shaniAarti.setAudioUrl("raw:aarti_shani_dev_ki");
        aartis.add(shaniAarti);

        // Rama Aarti
        AartiEntity ramaAarti = new AartiEntity();
        ramaAarti.setDeityId(11);
        ramaAarti.setTitle("Aarti Shri Ram Ji Ki");
        ramaAarti.setTitleHindi("आरती श्री रामचन्द्र जी की");
        ramaAarti.setLyricsHindi(
                "आरती श्री रामचन्द्र जी की ।\n" +
                "कीर्ति निशान दशरथ नन्दन की ॥\n\n" +
                "कोसल्या के नन्दन ।\n" +
                "देवतन के रखवारे ।\n" +
                "राम राम गावत सब जन ॥\n" +
                "आरती श्री रामचन्द्र जी की ॥"
        );
        ramaAarti.setLyricsEnglish("Aarti Shri Ramchandra Ji Ki");
        ramaAarti.setDuration(250);
        ramaAarti.setAudioUrl("raw:aarti_shri_ram_ji_ki");
        aartis.add(ramaAarti);

        db.aartiDao().insertAll(aartis);
        backfillAartiAudioUrls(db);
        backfillArchiveOrgUrls(db);
    }

    private static void backfillAartiAudioUrls(DivyaPathDatabase db) {
        db.aartiDao().updateAudioUrlByTitle("Jai Ganesh Deva", "raw:aarti_jai_ganesh_deva");
        db.aartiDao().updateAudioUrlByTitle("Om Jai Lakshmi Mata", "raw:aarti_om_jai_lakshmi_mata");
        db.aartiDao().updateAudioUrlByTitle("Om Jai Shiv Omkara", "raw:aarti_om_jai_shiv_omkara");
        db.aartiDao().updateAudioUrlByTitle("Aarti Keeje Hanuman Lala Ki", "raw:aarti_hanuman_lala_ki");
        db.aartiDao().updateAudioUrlByTitle("Aarti Kunj Bihari Ki", "raw:aarti_kunj_bihari_ki");
        db.aartiDao().updateAudioUrlByTitle("Jai Ambe Gauri", "raw:aarti_jai_ambe_gauri");
        db.aartiDao().updateAudioUrlByTitle("Jai Saraswati Mata", "raw:aarti_jai_saraswati_mata");
        db.aartiDao().updateAudioUrlByTitle("Om Jai Jagdish Hare", "raw:aarti_om_jai_jagdish_hare");
        db.aartiDao().updateAudioUrlByTitle("Aarti Surya Dev Ki", "raw:aarti_jai_ravi_dev");
        db.aartiDao().updateAudioUrlByTitle("Aarti Shani Dev Ki", "raw:aarti_shani_dev_ki");
        db.aartiDao().updateAudioUrlByTitle("Aarti Shri Ram Ji Ki", "raw:aarti_shri_ram_ji_ki");
    }

    /**
     * Backfill verified Archive.org direct MP3 streaming URLs for all content types.
     * This runs for existing users too (not just on fresh seed).
     *
     * Archive.org collections used:
     *   Aartis:   https://archive.org/details/PretrajSarkarKiAarti
     *   Chalisas: https://archive.org/details/godchalisa
     *   Mantras:  https://archive.org/details/HinduSlokasAndMantras
     *             https://archive.org/details/MantrasFromVedasSlokas
     *             https://archive.org/details/MantraVidya
     *   Bhajans:  https://archive.org/details/HindiBhajans-anoopJalotaJi
     *             https://archive.org/details/BhakthiSongs
     *             https://archive.org/details/MantrasFromVedasSlokas
     *   Stotras:  https://archive.org/details/ShivTandavStotram
     *             https://archive.org/details/MahishasuraMardiniStotram
     *             https://archive.org/details/VishnuSahasranamaStotra
     *             https://archive.org/details/SriLalithaSahasranamam
     *             https://archive.org/details/AdityaHridayamMantra
     *             https://archive.org/details/VariousSuktam
     */
    private static final String GH_AUDIO = "https://github.com/dpchavali1/DivyaPath/releases/download/v1.0-audio/";

    private static void backfillArchiveOrgUrls(DivyaPathDatabase db) {
        // ── Aartis ──
        db.aartiDao().updateArchiveOrgUrlByTitle("Jai Ganesh Deva",
                GH_AUDIO + "jai_ganesh_deva.mp3");
        db.aartiDao().updateArchiveOrgUrlByTitle("Om Jai Shiv Omkara",
                GH_AUDIO + "om_jai_shiv_omkara.mp3");
        db.aartiDao().updateArchiveOrgUrlByTitle("Om Jai Jagdish Hare",
                GH_AUDIO + "om_jai_jagdish_hare.mp3");
        db.aartiDao().updateArchiveOrgUrlByTitle("Om Jai Lakshmi Mata",
                GH_AUDIO + "om_jai_lakshmi_mata.mp3");
        db.aartiDao().updateArchiveOrgUrlByTitle("Jai Ambe Gauri",
                GH_AUDIO + "jai_ambe_gauri.mp3");
        db.aartiDao().updateArchiveOrgUrlByTitle("Aarti Keeje Hanuman Lala Ki",
                GH_AUDIO + "aarti_hanuman_lala_ki.mp3");
        db.aartiDao().updateArchiveOrgUrlByTitle("Aarti Kunj Bihari Ki",
                GH_AUDIO + "aarti_kunj_bihari_ki.mp3");
        db.aartiDao().updateArchiveOrgUrlByTitle("Jai Saraswati Mata",
                GH_AUDIO + "jai_saraswati_mata.mp3");
        db.aartiDao().updateArchiveOrgUrlByTitle("Aarti Surya Dev Ki",
                GH_AUDIO + "aarti_surya_dev_ki.mp3");
        db.aartiDao().updateArchiveOrgUrlByTitle("Aarti Shani Dev Ki",
                GH_AUDIO + "aarti_shani_dev_ki.mp3");
        db.aartiDao().updateArchiveOrgUrlByTitle("Aarti Shri Ram Ji Ki",
                GH_AUDIO + "aarti_shri_ram_ji_ki.mp3");

        // ── Chalisas ──
        db.chalisaDao().updateArchiveOrgUrlByTitle("Hanuman Chalisa",
                GH_AUDIO + "hanuman_chalisa.mp3");
        db.chalisaDao().updateArchiveOrgUrlByTitle("Ganesh Chalisa",
                GH_AUDIO + "ganesh_chalisa.mp3");
        db.chalisaDao().updateArchiveOrgUrlByTitle("Durga Chalisa",
                GH_AUDIO + "durga_chalisa.mp3");
        db.chalisaDao().updateArchiveOrgUrlByTitle("Shiv Chalisa",
                GH_AUDIO + "shiv_chalisa.mp3");
        db.chalisaDao().updateArchiveOrgUrlByTitle("Lakshmi Chalisa",
                GH_AUDIO + "lakshmi_chalisa.mp3");
        db.chalisaDao().updateArchiveOrgUrlByTitle("Saraswati Chalisa",
                GH_AUDIO + "saraswati_chalisa.mp3");
        db.chalisaDao().updateArchiveOrgUrlByTitle("Ram Chalisa",
                GH_AUDIO + "ram_chalisa.mp3");
        db.chalisaDao().updateArchiveOrgUrlByTitle("Shani Chalisa",
                GH_AUDIO + "shani_chalisa.mp3");
        db.chalisaDao().updateArchiveOrgUrlByTitle("Surya Chalisa",
                GH_AUDIO + "surya_chalisa.mp3");
        db.chalisaDao().updateArchiveOrgUrlByTitle("Vishnu Chalisa",
                GH_AUDIO + "vishnu_chalisa.mp3");

        // ── Mantras ──
        db.mantraDao().updateArchiveOrgUrlByTitle("Ganesh Beej Mantra",
                GH_AUDIO + "ganesh_beej_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Vakratunda Mahakaya",
                GH_AUDIO + "vakratunda_mahakaya.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Om Namah Shivaya",
                GH_AUDIO + "om_namah_shivaya.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Mahamrityunjaya Mantra",
                GH_AUDIO + "mahamrityunjaya_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Gayatri Mantra",
                GH_AUDIO + "gayatri_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Shanti Mantra",
                GH_AUDIO + "shanti_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Lakshmi Beej Mantra",
                GH_AUDIO + "lakshmi_beej_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Saraswati Beej Mantra",
                GH_AUDIO + "saraswati_beej_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Surya Mantra",
                GH_AUDIO + "surya_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Hare Krishna Mahamantra",
                GH_AUDIO + "hare_krishna_mahamantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Durga Beej Mantra",
                GH_AUDIO + "durga_beej_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Shani Mantra",
                GH_AUDIO + "shani_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Chandra Mantra",
                GH_AUDIO + "chandra_beej_Mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Mangal Mantra",
                GH_AUDIO + "mangal_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Guru Brihaspati Mantra",
                GH_AUDIO + "guru_brihaspati_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Rahu Mantra",
                GH_AUDIO + "rahu_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Ketu Mantra",
                GH_AUDIO + "ketu_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Budh Mantra",
                GH_AUDIO + "budh_mantra.mp3");
        db.mantraDao().updateArchiveOrgUrlByTitle("Shri Ram Jai Ram Mantra",
                GH_AUDIO + "shri_ram_jai_ram.mp3");

        // ── Bhajans ──
        db.bhajanDao().updateArchiveOrgUrlByTitle("Achyutam Keshavam",
                GH_AUDIO + "achyutam_keshavam.mp3");
        db.bhajanDao().updateArchiveOrgUrlByTitle("Hare Krishna Hare Rama",
                GH_AUDIO + "hare_krishna_hare_rama.mp3");
        db.bhajanDao().updateArchiveOrgUrlByTitle("Ya Devi Sarvabhuteshu",
                GH_AUDIO + "ya_devi_sarvabhuteshu.mp3");
        db.bhajanDao().updateArchiveOrgUrlByTitle("Shri Ramchandra Kripalu",
                GH_AUDIO + "shri_ramchandra_kripalu.mp3");
        db.bhajanDao().updateArchiveOrgUrlByTitle("Ram Siya Ram",
                GH_AUDIO + "ram_siya_ram.mp3");
        db.bhajanDao().updateArchiveOrgUrlByTitle("Govind Bolo Hari Gopal Bolo",
                GH_AUDIO + "govind_bolo_hari_gopal.mp3");
        db.bhajanDao().updateArchiveOrgUrlByTitle("Jai Ambe Gauri",
                GH_AUDIO + "jai_ambe_gauri_bhajan.mp3");
        db.bhajanDao().updateArchiveOrgUrlByTitle("Bam Bam Bhole",
                GH_AUDIO + "bam_bam_bhole.mp3");
        db.bhajanDao().updateArchiveOrgUrlByTitle("Dukh Mein Sumiran Sab Karein",
                GH_AUDIO + "dukh_mein_sumiran.mp3");
        db.bhajanDao().updateArchiveOrgUrlByTitle("Mere To Giridhar Gopal",
                GH_AUDIO + "mere_to_giridhar_gopal.mp3");

        // ── Stotras ──
        db.stotraDao().updateArchiveOrgUrlByTitle("Shiv Tandav Stotram",
                GH_AUDIO + "shiv_tandav_stotram.mp3");
        db.stotraDao().updateArchiveOrgUrlByTitle("Mahishasura Mardini Stotram",
                GH_AUDIO + "mahishasura_mardini.mp3");
        db.stotraDao().updateArchiveOrgUrlByTitle("Vishnu Sahasranama",
                GH_AUDIO + "vishnu_sahasranama.mp3");
        db.stotraDao().updateArchiveOrgUrlByTitle("Lalita Sahasranama",
                GH_AUDIO + "lalita_sahasranama.mp3");
        db.stotraDao().updateArchiveOrgUrlByTitle("Aditya Hridayam",
                GH_AUDIO + "aditya_hridayam.mp3");
        db.stotraDao().updateArchiveOrgUrlByTitle("Purusha Suktam",
                GH_AUDIO + "purusha_suktam.mp3");
        db.stotraDao().updateArchiveOrgUrlByTitle("Sri Suktam",
                GH_AUDIO + "sri_suktam.mp3");

        // Backfill localAssetName for aartis
        db.aartiDao().updateLocalAssetNameByTitle("Jai Ganesh Deva", "raw:aarti_jai_ganesh_deva");
        db.aartiDao().updateLocalAssetNameByTitle("Om Jai Lakshmi Mata", "raw:aarti_om_jai_lakshmi_mata");
        db.aartiDao().updateLocalAssetNameByTitle("Om Jai Shiv Omkara", "raw:aarti_om_jai_shiv_omkara");
        db.aartiDao().updateLocalAssetNameByTitle("Aarti Keeje Hanuman Lala Ki", "raw:aarti_hanuman_lala_ki");
        db.aartiDao().updateLocalAssetNameByTitle("Aarti Kunj Bihari Ki", "raw:aarti_kunj_bihari_ki");
        db.aartiDao().updateLocalAssetNameByTitle("Jai Ambe Gauri", "raw:aarti_jai_ambe_gauri");
        db.aartiDao().updateLocalAssetNameByTitle("Jai Saraswati Mata", "raw:aarti_jai_saraswati_mata");
        db.aartiDao().updateLocalAssetNameByTitle("Om Jai Jagdish Hare", "raw:aarti_om_jai_jagdish_hare");
        db.aartiDao().updateLocalAssetNameByTitle("Aarti Surya Dev Ki", "raw:aarti_jai_ravi_dev");
        db.aartiDao().updateLocalAssetNameByTitle("Aarti Shani Dev Ki", "raw:aarti_shani_dev_ki");
        db.aartiDao().updateLocalAssetNameByTitle("Aarti Shri Ram Ji Ki", "raw:aarti_shri_ram_ji_ki");
    }

    private static void seedChalisas(DivyaPathDatabase db) {
        if (db.chalisaDao().getCount() > 0) return;

        List<ChalisaEntity> chalisas = new ArrayList<>();

        // Hanuman Chalisa
        ChalisaEntity hanumanChalisa = new ChalisaEntity();
        hanumanChalisa.setDeityId(2);
        hanumanChalisa.setTitle("Hanuman Chalisa");
        hanumanChalisa.setTitleHindi("हनुमान चालीसा");
        hanumanChalisa.setContent(
                "॥ दोहा ॥\n\n" +
                "श्रीगुरु चरन सरोज रज, निज मनु मुकुरु सुधारि ।\n" +
                "बरनउँ रघुबर बिमल जसु, जो दायकु फल चारि ॥\n\n" +
                "बुद्धिहीन तनु जानिके, सुमिरौं पवन-कुमार ।\n" +
                "बल बुद्धि विद्या देहु मोहिं, हरहु कलेस विकार ॥\n\n" +
                "॥ चौपाई ॥\n\n" +
                "जय हनुमान ज्ञान गुन सागर ।\n" +
                "जय कपीस तिहुँ लोक उजागर ॥ 1 ॥\n\n" +
                "राम दूत अतुलित बल धामा ।\n" +
                "अंजनि-पुत्र पवनसुत नामा ॥ 2 ॥\n\n" +
                "महाबीर बिक्रम बजरंगी ।\n" +
                "कुमति निवार सुमति के संगी ॥ 3 ॥\n\n" +
                "कंचन बरन बिराज सुबेसा ।\n" +
                "कानन कुण्डल कुञ्चित केसा ॥ 4 ॥\n\n" +
                "हाथ बज्र और ध्वजा बिराजे ।\n" +
                "काँधे मूँज जनेऊ साजे ॥ 5 ॥\n\n" +
                "शंकर सुवन केसरीनन्दन ।\n" +
                "तेज प्रताप महा जग बन्दन ॥ 6 ॥\n\n" +
                "विद्यावान गुनी अति चातुर ।\n" +
                "राम काज करिबे को आतुर ॥ 7 ॥\n\n" +
                "प्रभु चरित्र सुनिबे को रसिया ।\n" +
                "राम लखन सीता मन बसिया ॥ 8 ॥\n\n" +
                "सूक्ष्म रूप धरि सियहिं दिखावा ।\n" +
                "बिकट रूप धरि लंक जरावा ॥ 9 ॥\n\n" +
                "भीम रूप धरि असुर सँहारे ।\n" +
                "रामचन्द्र के काज सँवारे ॥ 10 ॥\n\n" +
                "लाय सजीवन लखन जियाये ।\n" +
                "श्रीरघुबीर हरषि उर लाये ॥ 11 ॥\n\n" +
                "रघुपति कीन्हीं बहुत बड़ाई ।\n" +
                "तुम मम प्रिय भरतहि सम भाई ॥ 12 ॥\n\n" +
                "सहस बदन तुम्हरो जस गावैं ।\n" +
                "अस कहि श्रीपति कण्ठ लगावैं ॥ 13 ॥\n\n" +
                "सनकादिक ब्रह्मादि मुनीसा ।\n" +
                "नारद सारद सहित अहीसा ॥ 14 ॥\n\n" +
                "जम कुबेर दिगपाल जहाँ ते ।\n" +
                "कबि कोबिद कहि सके कहाँ ते ॥ 15 ॥\n\n" +
                "तुम उपकार सुग्रीवहिं कीन्हा ।\n" +
                "राम मिलाय राज पद दीन्हा ॥ 16 ॥\n\n" +
                "तुम्हरो मन्त्र बिभीषन माना ।\n" +
                "लंकेश्वर भए सब जग जाना ॥ 17 ॥\n\n" +
                "जुग सहस्र जोजन पर भानू ।\n" +
                "लील्यो ताहि मधुर फल जानू ॥ 18 ॥\n\n" +
                "प्रभु मुद्रिका मेलि मुख माहीं ।\n" +
                "जलधि लाँघि गये अचरज नाहीं ॥ 19 ॥\n\n" +
                "दुर्गम काज जगत के जेते ।\n" +
                "सुगम अनुग्रह तुम्हरे तेते ॥ 20 ॥\n\n" +
                "राम दुआरे तुम रखवारे ।\n" +
                "होत न आज्ञा बिनु पैसारे ॥ 21 ॥\n\n" +
                "सब सुख लहै तुम्हारी सरना ।\n" +
                "तुम रक्षक काहू को डरना ॥ 22 ॥\n\n" +
                "आपन तेज सम्हारो आपै ।\n" +
                "तीनों लोक हाँक तें काँपै ॥ 23 ॥\n\n" +
                "भूत पिसाच निकट नहिं आवै ।\n" +
                "महाबीर जब नाम सुनावै ॥ 24 ॥\n\n" +
                "नासै रोग हरे सब पीरा ।\n" +
                "जपत निरन्तर हनुमत बीरा ॥ 25 ॥\n\n" +
                "संकट तें हनुमान छुड़ावै ।\n" +
                "मन क्रम बचन ध्यान जो लावै ॥ 26 ॥\n\n" +
                "सब पर राम तपस्वी राजा ।\n" +
                "तिन के काज सकल तुम साजा ॥ 27 ॥\n\n" +
                "और मनोरथ जो कोई लावै ।\n" +
                "सोइ अमित जीवन फल पावै ॥ 28 ॥\n\n" +
                "चारों जुग परताप तुम्हारा ।\n" +
                "है परसिद्ध जगत उजियारा ॥ 29 ॥\n\n" +
                "साधु सन्त के तुम रखवारे ।\n" +
                "असुर निकन्दन राम दुलारे ॥ 30 ॥\n\n" +
                "अष्ट सिद्धि नौ निधि के दाता ।\n" +
                "अस बर दीन जानकी माता ॥ 31 ॥\n\n" +
                "राम रसायन तुम्हरे पासा ।\n" +
                "सदा रहो रघुपति के दासा ॥ 32 ॥\n\n" +
                "तुम्हरे भजन राम को पावै ।\n" +
                "जनम जनम के दुख बिसरावै ॥ 33 ॥\n\n" +
                "अन्तकाल रघुबर पुर जाई ।\n" +
                "जहाँ जन्म हरि-भक्त कहाई ॥ 34 ॥\n\n" +
                "और देवता चित्त न धरई ।\n" +
                "हनुमत सेइ सर्ब सुख करई ॥ 35 ॥\n\n" +
                "संकट कटै मिटै सब पीरा ।\n" +
                "जो सुमिरै हनुमत बलबीरा ॥ 36 ॥\n\n" +
                "जै जै जै हनुमान गोसाईं ।\n" +
                "कृपा करहु गुरुदेव की नाईं ॥ 37 ॥\n\n" +
                "जो सत बार पाठ कर कोई ।\n" +
                "छूटहि बन्दि महा सुख होई ॥ 38 ॥\n\n" +
                "जो यह पढ़ै हनुमान चालीसा ।\n" +
                "होय सिद्धि साखी गौरीसा ॥ 39 ॥\n\n" +
                "तुलसीदास सदा हरि चेरा ।\n" +
                "कीजै नाथ हृदय महँ डेरा ॥ 40 ॥\n\n" +
                "॥ दोहा ॥\n\n" +
                "पवनतनय संकट हरन, मंगल मूरति रूप ।\n" +
                "राम लखन सीता सहित, हृदय बसहु सुर भूप ॥"
        );
        hanumanChalisa.setContentEnglish(
                "Doha:\nWith the dust of Guru's lotus feet, I clean the mirror of my mind\n" +
                "And then narrate the sacred glory of Sri Ramchandra, the supreme among Raghus\n\n" +
                "Knowing myself to be ignorant, I urge you O Hanuman\n" +
                "Grant me strength, intelligence and knowledge, removing all sorrows\n\n" +
                "Chaupai 1: Victory to Hanuman, ocean of wisdom and virtue\n" +
                "Victory to the lord of monkeys, illuminator of the three worlds"
        );
        hanumanChalisa.setTotalVerses(40);
        chalisas.add(hanumanChalisa);

        // Ganesh Chalisa
        ChalisaEntity ganeshChalisa = new ChalisaEntity();
        ganeshChalisa.setDeityId(3);
        ganeshChalisa.setTitle("Ganesh Chalisa");
        ganeshChalisa.setTitleHindi("गणेश चालीसा");
        ganeshChalisa.setContent(
                "॥ दोहा ॥\n\n" +
                "जय गणपति सदगुन सदन, कविवर बदन कृपाल ।\n" +
                "विघ्न हरण मंगल करण, जय जय गिरिजालाल ॥\n\n" +
                "॥ चौपाई ॥\n\n" +
                "जय जय जय गणपति गणराजू ।\n" +
                "मंगल भरण करण शुभ काजू ॥ 1 ॥\n\n" +
                "जय गजबदन सदन सुखदाता ।\n" +
                "विश्व विनायक बुद्धि विधाता ॥ 2 ॥\n\n" +
                "वक्र तुण्ड शुचि शुण्ड सुहावन ।\n" +
                "तिलक त्रिपुण्ड भाल मन भावन ॥ 3 ॥\n\n" +
                "राजत मणि मुक्तन उर माला ।\n" +
                "स्वर्ण मुकुट शिर नयन विशाला ॥ 4 ॥"
        );
        ganeshChalisa.setContentEnglish("Doha: Victory to Ganapati, abode of virtues...");
        ganeshChalisa.setTotalVerses(40);
        chalisas.add(ganeshChalisa);

        // Durga Chalisa
        ChalisaEntity durgaChalisa = new ChalisaEntity();
        durgaChalisa.setDeityId(8);
        durgaChalisa.setTitle("Durga Chalisa");
        durgaChalisa.setTitleHindi("दुर्गा चालीसा");
        durgaChalisa.setContent(
                "॥ दोहा ॥\n\n" +
                "नमो नमो दुर्गे सुख करनी ।\n" +
                "नमो नमो अम्बे दुख हरनी ॥\n\n" +
                "॥ चौपाई ॥\n\n" +
                "निरंकार है ज्योति तुम्हारी ।\n" +
                "तिहूँ लोक फैली उजियारी ॥ 1 ॥\n\n" +
                "शशि ललाट मुख महाविशाला ।\n" +
                "नेत्र लाल भृकुटी विकराला ॥ 2 ॥"
        );
        durgaChalisa.setContentEnglish("Doha: Salutations to Durga, bestower of happiness...");
        durgaChalisa.setTotalVerses(40);
        chalisas.add(durgaChalisa);

        // Shiv Chalisa
        ChalisaEntity shivChalisa = new ChalisaEntity();
        shivChalisa.setDeityId(1);
        shivChalisa.setTitle("Shiv Chalisa");
        shivChalisa.setTitleHindi("शिव चालीसा");
        shivChalisa.setContent(
                "॥ दोहा ॥\n\n" +
                "जय गिरिजा पति दीनदयाला ।\n" +
                "सदा करत सन्तन प्रतिपाला ॥\n\n" +
                "॥ चौपाई ॥\n\n" +
                "चन्द्र अर्ध ममथा शिर राजै ।\n" +
                "काढ़त पिनाक मनो गज गाजै ॥ 1 ॥"
        );
        shivChalisa.setContentEnglish("Doha: Victory to the lord of Girija...");
        shivChalisa.setTotalVerses(40);
        chalisas.add(shivChalisa);

        // Lakshmi Chalisa
        ChalisaEntity lakshmiChalisa = new ChalisaEntity();
        lakshmiChalisa.setDeityId(5);
        lakshmiChalisa.setTitle("Lakshmi Chalisa");
        lakshmiChalisa.setTitleHindi("लक्ष्मी चालीसा");
        lakshmiChalisa.setContent(
                "॥ दोहा ॥\n\n" +
                "मातु लक्ष्मी करि कृपा, करो हृदय में वास ।\n" +
                "मनोकामना सिद्ध करि, पूरण करो आस ॥\n\n" +
                "॥ चौपाई ॥\n\n" +
                "सिन्धु सुता मैं सुमिरों तोही ।\n" +
                "कृपा करो जगदम्बा मोही ॥ 1 ॥"
        );
        lakshmiChalisa.setContentEnglish("Doha: Mother Lakshmi, have mercy...");
        lakshmiChalisa.setTotalVerses(40);
        chalisas.add(lakshmiChalisa);

        // Saraswati Chalisa
        ChalisaEntity saraswatiChalisa = new ChalisaEntity();
        saraswatiChalisa.setDeityId(10);
        saraswatiChalisa.setTitle("Saraswati Chalisa");
        saraswatiChalisa.setTitleHindi("सरस्वती चालीसा");
        saraswatiChalisa.setContent(
                "॥ दोहा ॥\n\n" +
                "श्री सरस्वती मैय्या जिनके, भवन में सदा निवास ।\n" +
                "उनके मन में ज्ञान का, प्रकाश कभी न हो उदास ॥\n\n" +
                "॥ चौपाई ॥\n\n" +
                "जय जय श्री सरस्वती भवानी ।\n" +
                "जय जय जय गुणवंती ज्ञानी ॥ 1 ॥"
        );
        saraswatiChalisa.setContentEnglish("Doha: Shri Saraswati Maiya...");
        saraswatiChalisa.setTotalVerses(40);
        chalisas.add(saraswatiChalisa);

        // Ram Chalisa
        ChalisaEntity ramChalisa = new ChalisaEntity();
        ramChalisa.setDeityId(11);
        ramChalisa.setTitle("Ram Chalisa");
        ramChalisa.setTitleHindi("राम चालीसा");
        ramChalisa.setContent(
                "॥ दोहा ॥\n\n" +
                "श्री रामचन्द्र कृपालु भजु मन हरण भवभय दारुणम् ।\n" +
                "नवकंज लोचन कंज मुख कर कंज पद कंजारुणम् ॥\n\n" +
                "॥ चौपाई ॥\n\n" +
                "राम नाम सुन्दर हैं दोऊ ।\n" +
                "बरन बिलोक बिचारहिं कोऊ ॥ 1 ॥\n\n" +
                "अपतु अजामिल गज गनिकाऊ ।\n" +
                "भए मुकुत हरि नाम प्रभाऊ ॥ 2 ॥\n\n" +
                "सुनि समुझहिं जन मुदित मन मजहिं ।\n" +
                "राम सनेह सराहन लाजहिं ॥ 3 ॥\n\n" +
                "एहि महँ रघुपति नाम उदारा ।\n" +
                "अति पावन पुरान श्रुति सारा ॥ 4 ॥"
        );
        ramChalisa.setContentEnglish("Doha: Worship the gracious Shri Ramchandra who removes the fears of worldly existence...");
        ramChalisa.setTotalVerses(40);
        chalisas.add(ramChalisa);

        // Shani Chalisa
        ChalisaEntity shaniChalisa = new ChalisaEntity();
        shaniChalisa.setDeityId(6);
        shaniChalisa.setTitle("Shani Chalisa");
        shaniChalisa.setTitleHindi("शनि चालीसा");
        shaniChalisa.setContent(
                "॥ दोहा ॥\n\n" +
                "जय जय श्री शनिदेव प्रभु, सुनहु विनय महाराज ।\n" +
                "करहु कृपा हे रवि तनय, राखहु जन की लाज ॥\n\n" +
                "॥ चौपाई ॥\n\n" +
                "जय जय जय शनिदेव दयाला ।\n" +
                "करत सदा भक्तन प्रतिपाला ॥ 1 ॥\n\n" +
                "चारि भुजा तनु श्याम विराजै ।\n" +
                "माथे रतन मुकुट छबि छाजै ॥ 2 ॥\n\n" +
                "परम विशाल मनोहर भाला ।\n" +
                "नील वस्त्र तनु उपर डाला ॥ 3 ॥\n\n" +
                "हाथ गदा त्रिशूल कुठारा ।\n" +
                "कटि में सोहत तीर कटारा ॥ 4 ॥"
        );
        shaniChalisa.setContentEnglish("Doha: Victory to Shri Shani Dev, hear my humble prayer O great lord...");
        shaniChalisa.setTotalVerses(40);
        chalisas.add(shaniChalisa);

        // Surya Chalisa
        ChalisaEntity suryaChalisa = new ChalisaEntity();
        suryaChalisa.setDeityId(7);
        suryaChalisa.setTitle("Surya Chalisa");
        suryaChalisa.setTitleHindi("सूर्य चालीसा");
        suryaChalisa.setContent(
                "॥ दोहा ॥\n\n" +
                "कनक बदन कुण्डल मकर, मुकुट रत्न खचित ।\n" +
                "ध्यान धरत ही सूर्य के, मन होवत पुनीत ॥\n\n" +
                "॥ चौपाई ॥\n\n" +
                "जय जय जय रवि देव दयाला ।\n" +
                "करत सदा जन प्रतिपाला ॥ 1 ॥\n\n" +
                "सकल सृष्टि तुम तेज पसारा ।\n" +
                "हरत तिमिर अन्धकार अपारा ॥ 2 ॥\n\n" +
                "किरण सप्त तव शोभा धारी ।\n" +
                "विश्व विमल किय तेज तुम्हारी ॥ 3 ॥\n\n" +
                "सकल लोक तुम्हीं से प्रकाशा ।\n" +
                "तव बिना जगत अन्धियारा ॥ 4 ॥"
        );
        suryaChalisa.setContentEnglish("Doha: Golden-faced, adorned with earrings and gem-studded crown, meditating on Surya purifies the mind...");
        suryaChalisa.setTotalVerses(40);
        chalisas.add(suryaChalisa);

        // Vishnu Chalisa
        ChalisaEntity vishnuChalisa = new ChalisaEntity();
        vishnuChalisa.setDeityId(4);
        vishnuChalisa.setTitle("Vishnu Chalisa");
        vishnuChalisa.setTitleHindi("विष्णु चालीसा");
        vishnuChalisa.setContent(
                "॥ दोहा ॥\n\n" +
                "नमो विष्णु भगवान को, जिनके निर्मल नाम ।\n" +
                "सदा ध्यान जो ध्यावहीं, उनके सफल सब काम ॥\n\n" +
                "॥ चौपाई ॥\n\n" +
                "जय जय श्री हरि विष्णु भवानी ।\n" +
                "जय जय पालनकर्ता ज्ञानी ॥ 1 ॥\n\n" +
                "शंख चक्र गदा पद्म विराजै ।\n" +
                "पीताम्बर शिर मुकुट विराजै ॥ 2 ॥\n\n" +
                "लक्ष्मी संग सदा विराजत ।\n" +
                "क्षीर सागर में सुख पावत ॥ 3 ॥\n\n" +
                "शेषनाग शय्या अति शोभा ।\n" +
                "त्रिभुवन दरश करत सब लोभा ॥ 4 ॥"
        );
        vishnuChalisa.setContentEnglish("Doha: Salutations to Lord Vishnu, whose name is pure, those who meditate on him always succeed...");
        vishnuChalisa.setTotalVerses(40);
        chalisas.add(vishnuChalisa);

        db.chalisaDao().insertAll(chalisas);
    }

    private static void seedMantras(DivyaPathDatabase db) {
        if (db.mantraDao().getCount() > 0) return;

        List<MantraEntity> mantras = new ArrayList<>();

        // Ganesh Mantras
        MantraEntity m1 = new MantraEntity();
        m1.setDeityId(3);
        m1.setTitle("Ganesh Beej Mantra");
        m1.setSanskrit("ॐ गं गणपतये नमः");
        m1.setHindiMeaning("ॐ गं बीज मंत्र से गणपति को नमस्कार है।");
        m1.setEnglishTransliteration("Om Gam Ganapataye Namaha");
        m1.setBenefits("Removes obstacles, brings success in new ventures, enhances wisdom and intellect.");
        m1.setCategory("ganesh");
        mantras.add(m1);

        MantraEntity m2 = new MantraEntity();
        m2.setDeityId(3);
        m2.setTitle("Vakratunda Mahakaya");
        m2.setSanskrit("वक्रतुण्ड महाकाय सूर्यकोटि समप्रभ ।\nनिर्विघ्नं कुरु मे देव सर्वकार्येषु सर्वदा ॥");
        m2.setHindiMeaning("हे विशाल शरीर वाले, करोड़ों सूर्यों के समान तेजस्वी, मेरे सभी कार्यों में सदा विघ्न दूर कीजिए।");
        m2.setEnglishTransliteration("Vakratunda Mahakaya Suryakoti Samaprabha\nNirvighnam Kuru Me Deva Sarvakaryeshu Sarvada");
        m2.setBenefits("Invoked before starting any new task. Removes all obstacles and ensures success.");
        m2.setCategory("ganesh");
        mantras.add(m2);

        // Shiva Mantras
        MantraEntity m3 = new MantraEntity();
        m3.setDeityId(1);
        m3.setTitle("Om Namah Shivaya");
        m3.setSanskrit("ॐ नमः शिवाय");
        m3.setHindiMeaning("भगवान शिव को नमस्कार। यह पंचाक्षर मंत्र शिव की आराधना का सबसे शक्तिशाली मंत्र है।");
        m3.setEnglishTransliteration("Om Namah Shivaya");
        m3.setBenefits("The most powerful Shiva mantra. Brings inner peace, destroys negative karma, and leads to spiritual awakening.");
        m3.setCategory("shiva");
        mantras.add(m3);

        MantraEntity m4 = new MantraEntity();
        m4.setDeityId(1);
        m4.setTitle("Mahamrityunjaya Mantra");
        m4.setSanskrit("ॐ त्र्यम्बकं यजामहे सुगन्धिं पुष्टिवर्धनम् ।\nउर्वारुकमिव बन्धनान्मृत्योर्मुक्षीय मामृतात् ॥");
        m4.setHindiMeaning("हम तीन नेत्रों वाले भगवान शिव की पूजा करते हैं जो सुगंधित हैं और पोषण करते हैं। जैसे ककड़ी बेल से अलग हो जाती है, वैसे ही हमें मृत्यु से मुक्ति मिले।");
        m4.setEnglishTransliteration("Om Tryambakam Yajamahe Sugandhim Pushti-Vardhanam\nUrvarukamiva Bandhanan Mrityor Mukshiya Maamritat");
        m4.setBenefits("Protects from untimely death, heals diseases, brings longevity and spiritual liberation.");
        m4.setCategory("shiva");
        mantras.add(m4);

        // Vedic Mantras
        MantraEntity m5 = new MantraEntity();
        m5.setDeityId(7);
        m5.setTitle("Gayatri Mantra");
        m5.setSanskrit("ॐ भूर्भुवः स्वः\nतत्सवितुर्वरेण्यं\nभर्गो देवस्य धीमहि\nधियो यो नः प्रचोदयात् ॥");
        m5.setHindiMeaning("उस प्राणस्वरूप, दुःखनाशक, सुखस्वरूप, श्रेष्ठ, तेजस्वी, पापनाशक, देवस्वरूप परमात्मा को हम अन्तःकरण में धारण करते हैं। वह परमात्मा हमारी बुद्धि को सन्मार्ग में प्रेरित करें।");
        m5.setEnglishTransliteration("Om Bhur Bhuvah Svah\nTat Savitur Varenyam\nBhargo Devasya Dhimahi\nDhiyo Yo Nah Prachodayat");
        m5.setBenefits("The most sacred Vedic mantra. Enhances intelligence, removes ignorance, purifies the mind and brings spiritual illumination.");
        m5.setCategory("vedic");
        mantras.add(m5);

        MantraEntity m6 = new MantraEntity();
        m6.setDeityId(4);
        m6.setTitle("Shanti Mantra");
        m6.setSanskrit("ॐ सर्वे भवन्तु सुखिनः\nसर्वे सन्तु निरामयाः ।\nसर्वे भद्राणि पश्यन्तु\nमा कश्चिद्दुःखभाग्भवेत् ॥");
        m6.setHindiMeaning("सभी सुखी हों, सभी रोगमुक्त हों, सभी को शुभ दर्शन हों, किसी को कोई दुःख न हो।");
        m6.setEnglishTransliteration("Om Sarve Bhavantu Sukhinah\nSarve Santu Niramayah\nSarve Bhadrani Pashyantu\nMa Kashchid Duhkha Bhag Bhavet");
        m6.setBenefits("A universal peace prayer. Brings harmony, compassion, and well-being for all beings.");
        m6.setCategory("vedic");
        mantras.add(m6);

        // Beej Mantras
        MantraEntity m7 = new MantraEntity();
        m7.setDeityId(5);
        m7.setTitle("Lakshmi Beej Mantra");
        m7.setSanskrit("ॐ श्रीं महालक्ष्म्यै नमः");
        m7.setHindiMeaning("श्रीं बीज मंत्र से महालक्ष्मी को नमस्कार है।");
        m7.setEnglishTransliteration("Om Shreem Mahalakshmyai Namaha");
        m7.setBenefits("Attracts wealth, prosperity, abundance and the blessings of Goddess Lakshmi.");
        m7.setCategory("beej");
        mantras.add(m7);

        MantraEntity m8 = new MantraEntity();
        m8.setDeityId(10);
        m8.setTitle("Saraswati Beej Mantra");
        m8.setSanskrit("ॐ ऐं सरस्वत्यै नमः");
        m8.setHindiMeaning("ऐं बीज मंत्र से सरस्वती को नमस्कार है।");
        m8.setEnglishTransliteration("Om Aim Saraswatyai Namaha");
        m8.setBenefits("Enhances knowledge, memory, creativity and artistic abilities.");
        m8.setCategory("beej");
        mantras.add(m8);

        // Navgraha Mantras
        MantraEntity m9 = new MantraEntity();
        m9.setDeityId(7);
        m9.setTitle("Surya Mantra");
        m9.setSanskrit("ॐ ह्रां ह्रीं ह्रौं सः सूर्याय नमः");
        m9.setHindiMeaning("सूर्य देव के बीज मंत्र से सूर्य को नमस्कार है।");
        m9.setEnglishTransliteration("Om Hraam Hreem Hraum Sah Suryaya Namaha");
        m9.setBenefits("Strengthens Sun in horoscope, brings fame, authority, health and leadership qualities.");
        m9.setCategory("navgraha");
        mantras.add(m9);

        MantraEntity m10 = new MantraEntity();
        m10.setDeityId(6);
        m10.setTitle("Shani Mantra");
        m10.setSanskrit("ॐ प्रां प्रीं प्रौं सः शनैश्चराय नमः");
        m10.setHindiMeaning("शनि देव के बीज मंत्र से शनैश्चर को नमस्कार है।");
        m10.setEnglishTransliteration("Om Praam Preem Praum Sah Shanaischaraya Namaha");
        m10.setBenefits("Pacifies the effects of Saturn, removes obstacles caused by Shani Dasha, brings discipline.");
        m10.setCategory("navgraha");
        mantras.add(m10);

        // Chandra (Moon) Mantra
        MantraEntity m11 = new MantraEntity();
        m11.setDeityId(1);
        m11.setTitle("Chandra Mantra");
        m11.setSanskrit("ॐ श्रां श्रीं श्रौं सः चन्द्रमसे नमः");
        m11.setHindiMeaning("चन्द्र देव के बीज मंत्र से चन्द्रमा को नमस्कार है।");
        m11.setEnglishTransliteration("Om Shraam Shreem Shraum Sah Chandramase Namaha");
        m11.setBenefits("Strengthens Moon in horoscope, brings mental peace, emotional stability and good health.");
        m11.setCategory("navgraha");
        mantras.add(m11);

        // Mangal (Mars) Mantra
        MantraEntity m12 = new MantraEntity();
        m12.setDeityId(1);
        m12.setTitle("Mangal Mantra");
        m12.setSanskrit("ॐ क्रां क्रीं क्रौं सः भौमाय नमः");
        m12.setHindiMeaning("मंगल ग्रह के बीज मंत्र से भौम (मंगल) को नमस्कार है।");
        m12.setEnglishTransliteration("Om Kraam Kreem Kraum Sah Bhaumaya Namaha");
        m12.setBenefits("Strengthens Mars in horoscope, brings courage, vitality, property gains and removes Mangal Dosha.");
        m12.setCategory("navgraha");
        mantras.add(m12);

        // Budh (Mercury) Mantra
        MantraEntity m13 = new MantraEntity();
        m13.setDeityId(1);
        m13.setTitle("Budh Mantra");
        m13.setSanskrit("ॐ ब्रां ब्रीं ब्रौं सः बुधाय नमः");
        m13.setHindiMeaning("बुध ग्रह के बीज मंत्र से बुध को नमस्कार है।");
        m13.setEnglishTransliteration("Om Braam Breem Braum Sah Budhaya Namaha");
        m13.setBenefits("Strengthens Mercury in horoscope, enhances communication skills, intelligence and business acumen.");
        m13.setCategory("navgraha");
        mantras.add(m13);

        // Guru (Jupiter/Brihaspati) Mantra
        MantraEntity m14 = new MantraEntity();
        m14.setDeityId(1);
        m14.setTitle("Guru Brihaspati Mantra");
        m14.setSanskrit("ॐ ग्रां ग्रीं ग्रौं सः गुरवे नमः");
        m14.setHindiMeaning("बृहस्पति ग्रह के बीज मंत्र से गुरु को नमस्कार है।");
        m14.setEnglishTransliteration("Om Graam Greem Graum Sah Gurave Namaha");
        m14.setBenefits("Strengthens Jupiter in horoscope, brings wisdom, prosperity, good fortune and spiritual growth.");
        m14.setCategory("navgraha");
        mantras.add(m14);

        // Shukra (Venus) Mantra
        MantraEntity m15 = new MantraEntity();
        m15.setDeityId(1);
        m15.setTitle("Shukra Mantra");
        m15.setSanskrit("ॐ द्रां द्रीं द्रौं सः शुक्राय नमः");
        m15.setHindiMeaning("शुक्र ग्रह के बीज मंत्र से शुक्र को नमस्कार है।");
        m15.setEnglishTransliteration("Om Draam Dreem Draum Sah Shukraya Namaha");
        m15.setBenefits("Strengthens Venus in horoscope, brings love, beauty, artistic talents and material comforts.");
        m15.setCategory("navgraha");
        mantras.add(m15);

        // Rahu Mantra
        MantraEntity m16 = new MantraEntity();
        m16.setDeityId(1);
        m16.setTitle("Rahu Mantra");
        m16.setSanskrit("ॐ भ्रां भ्रीं भ्रौं सः राहवे नमः");
        m16.setHindiMeaning("राहु ग्रह के बीज मंत्र से राहु को नमस्कार है।");
        m16.setEnglishTransliteration("Om Bhraam Bhreem Bhraum Sah Rahave Namaha");
        m16.setBenefits("Pacifies Rahu, removes confusion and illusions, protects from sudden misfortunes and hidden enemies.");
        m16.setCategory("navgraha");
        mantras.add(m16);

        // Ketu Mantra
        MantraEntity m17 = new MantraEntity();
        m17.setDeityId(1);
        m17.setTitle("Ketu Mantra");
        m17.setSanskrit("ॐ स्रां स्रीं स्रौं सः केतवे नमः");
        m17.setHindiMeaning("केतु ग्रह के बीज मंत्र से केतु को नमस्कार है।");
        m17.setEnglishTransliteration("Om Sraam Sreem Sraum Sah Ketave Namaha");
        m17.setBenefits("Pacifies Ketu, enhances spiritual liberation, intuition and removes past life karmic obstacles.");
        m17.setCategory("navgraha");
        mantras.add(m17);

        // Hare Krishna Mahamantra
        MantraEntity m18 = new MantraEntity();
        m18.setDeityId(9);
        m18.setTitle("Hare Krishna Mahamantra");
        m18.setSanskrit("हरे कृष्ण हरे कृष्ण कृष्ण कृष्ण हरे हरे ।\nहरे राम हरे राम राम राम हरे हरे ॥");
        m18.setHindiMeaning("भगवान कृष्ण और राम के नामों का महामंत्र। यह कलियुग में सबसे प्रभावशाली मंत्र माना जाता है।");
        m18.setEnglishTransliteration("Hare Krishna Hare Krishna Krishna Krishna Hare Hare\nHare Rama Hare Rama Rama Rama Hare Hare");
        m18.setBenefits("The supreme mantra for Kali Yuga. Brings divine love, spiritual awakening, inner joy and liberation from the cycle of birth and death.");
        m18.setCategory("krishna");
        mantras.add(m18);

        // Durga Beej Mantra
        MantraEntity m19 = new MantraEntity();
        m19.setDeityId(8);
        m19.setTitle("Durga Beej Mantra");
        m19.setSanskrit("ॐ दुं दुर्गायै नमः");
        m19.setHindiMeaning("दुं बीज मंत्र से माँ दुर्गा को नमस्कार है।");
        m19.setEnglishTransliteration("Om Dum Durgayai Namaha");
        m19.setBenefits("Invokes the protective power of Goddess Durga. Destroys evil forces, removes fear and grants courage and strength.");
        m19.setCategory("beej");
        mantras.add(m19);

        // Ram Mantra
        MantraEntity m20 = new MantraEntity();
        m20.setDeityId(11);
        m20.setTitle("Shri Ram Jai Ram Mantra");
        m20.setSanskrit("श्री राम जय राम जय जय राम");
        m20.setHindiMeaning("श्री राम की जय हो, जय जय राम। भगवान राम का यह सरल और शक्तिशाली मंत्र है।");
        m20.setEnglishTransliteration("Shri Ram Jai Ram Jai Jai Ram");
        m20.setBenefits("A powerful mantra of Lord Rama. Brings mental peace, removes negativity, instills righteousness and grants divine protection.");
        m20.setCategory("rama");
        mantras.add(m20);

        db.mantraDao().insertAll(mantras);
    }

    private static void seedFestivals(DivyaPathDatabase db) {
        if (db.festivalDao().getCount() > 0) return;

        List<FestivalEntity> festivals = new ArrayList<>();

        FestivalEntity f1 = new FestivalEntity();
        f1.setName("Makar Sankranti"); f1.setNameHindi("मकर संक्रांति");
        f1.setDate("2026-01-14"); f1.setDescription("Harvest festival marking the sun's transition into Capricorn.");
        festivals.add(f1);

        FestivalEntity f2 = new FestivalEntity();
        f2.setName("Basant Panchami"); f2.setNameHindi("बसंत पंचमी");
        f2.setDate("2026-02-01"); f2.setDescription("Festival of Goddess Saraswati, marking the arrival of spring.");
        festivals.add(f2);

        FestivalEntity f3 = new FestivalEntity();
        f3.setName("Maha Shivaratri"); f3.setNameHindi("महा शिवरात्रि");
        f3.setDate("2026-02-15"); f3.setDescription("The great night of Lord Shiva, celebrated with fasting and night vigil.");
        festivals.add(f3);

        FestivalEntity f4 = new FestivalEntity();
        f4.setName("Holi"); f4.setNameHindi("होली");
        f4.setDate("2026-03-17"); f4.setDescription("Festival of colors celebrating the victory of good over evil.");
        festivals.add(f4);

        FestivalEntity f5 = new FestivalEntity();
        f5.setName("Ram Navami"); f5.setNameHindi("राम नवमी");
        f5.setDate("2026-03-28"); f5.setDescription("Celebration of Lord Rama's birth.");
        festivals.add(f5);

        FestivalEntity f6 = new FestivalEntity();
        f6.setName("Hanuman Jayanti"); f6.setNameHindi("हनुमान जयंती");
        f6.setDate("2026-04-06"); f6.setDescription("Birth anniversary of Lord Hanuman.");
        festivals.add(f6);

        FestivalEntity f7 = new FestivalEntity();
        f7.setName("Raksha Bandhan"); f7.setNameHindi("रक्षा बंधन");
        f7.setDate("2026-08-12"); f7.setDescription("Festival celebrating the bond between brothers and sisters.");
        festivals.add(f7);

        FestivalEntity f8 = new FestivalEntity();
        f8.setName("Janmashtami"); f8.setNameHindi("जन्माष्टमी");
        f8.setDate("2026-08-22"); f8.setDescription("Birth anniversary of Lord Krishna.");
        festivals.add(f8);

        FestivalEntity f9 = new FestivalEntity();
        f9.setName("Ganesh Chaturthi"); f9.setNameHindi("गणेश चतुर्थी");
        f9.setDate("2026-09-07"); f9.setDescription("Festival celebrating the birth of Lord Ganesha.");
        festivals.add(f9);

        FestivalEntity f10 = new FestivalEntity();
        f10.setName("Navratri"); f10.setNameHindi("नवरात्रि");
        f10.setDate("2026-10-08"); f10.setDescription("Nine nights of Goddess Durga worship.");
        festivals.add(f10);

        FestivalEntity f11 = new FestivalEntity();
        f11.setName("Dussehra"); f11.setNameHindi("दशहरा");
        f11.setDate("2026-10-17"); f11.setDescription("Victory of Lord Rama over Ravana, triumph of good over evil.");
        festivals.add(f11);

        FestivalEntity f12 = new FestivalEntity();
        f12.setName("Diwali"); f12.setNameHindi("दीपावली");
        f12.setDate("2026-11-05"); f12.setDescription("Festival of lights celebrating Lord Rama's return to Ayodhya.");
        festivals.add(f12);

        FestivalEntity f13 = new FestivalEntity();
        f13.setName("Dhanteras"); f13.setNameHindi("धनतेरस");
        f13.setDate("2026-11-03"); f13.setDescription("Festival of wealth, first day of Diwali celebrations, dedicated to Lord Dhanvantari.");
        festivals.add(f13);

        FestivalEntity f14 = new FestivalEntity();
        f14.setName("Govardhan Puja"); f14.setNameHindi("गोवर्धन पूजा");
        f14.setDate("2026-11-06"); f14.setDescription("Celebrates Lord Krishna lifting Govardhan Hill to protect villagers from Indra's wrath.");
        festivals.add(f14);

        FestivalEntity f15 = new FestivalEntity();
        f15.setName("Bhai Dooj"); f15.setNameHindi("भाई दूज");
        f15.setDate("2026-11-07"); f15.setDescription("Festival celebrating the bond between brothers and sisters, sisters pray for their brothers' well-being.");
        festivals.add(f15);

        FestivalEntity f16 = new FestivalEntity();
        f16.setName("Chhath Puja"); f16.setNameHindi("छठ पूजा");
        f16.setDate("2026-11-08"); f16.setDescription("Ancient Hindu festival dedicated to Lord Surya and Chhathi Maiya, celebrated with fasting and offering prayers to the Sun.");
        festivals.add(f16);

        FestivalEntity f17 = new FestivalEntity();
        f17.setName("Guru Purnima"); f17.setNameHindi("गुरु पूर्णिमा");
        f17.setDate("2026-07-11"); f17.setDescription("Festival dedicated to spiritual and academic teachers, celebrated on the full moon day of Ashadha month.");
        festivals.add(f17);

        FestivalEntity f18 = new FestivalEntity();
        f18.setName("Dev Uthani Ekadashi"); f18.setNameHindi("देवउठनी एकादशी");
        f18.setDate("2026-11-17"); f18.setDescription("The day Lord Vishnu awakens from his cosmic sleep, marking the end of Chaturmas and beginning of the wedding season.");
        festivals.add(f18);

        FestivalEntity f19 = new FestivalEntity();
        f19.setName("Tulsi Vivah"); f19.setNameHindi("तुलसी विवाह");
        f19.setDate("2026-11-18"); f19.setDescription("Ceremonial marriage of the Tulsi plant to Lord Vishnu or his avatar Shaligram, marking the beginning of the Hindu wedding season.");
        festivals.add(f19);

        FestivalEntity f20 = new FestivalEntity();
        f20.setName("Karva Chauth"); f20.setNameHindi("करवा चौथ");
        f20.setDate("2026-10-26"); f20.setDescription("Festival where married women fast from sunrise to moonrise for the longevity and well-being of their husbands.");
        festivals.add(f20);

        db.festivalDao().insertAll(festivals);
    }

    private static void backfillTempleDarshanUrls(DivyaPathDatabase db) {
        try {
            SupportSQLiteDatabase sdb = db.getOpenHelper().getWritableDatabase();
            // Always update to latest URLs (overwrite any old/broken URLs)
            sdb.execSQL("UPDATE temples SET youtubeUrl = 'https://www.youtube.com/@svbcttd/live' WHERE name LIKE '%Tirupati%'");
            sdb.execSQL("UPDATE temples SET youtubeUrl = 'https://www.youtube.com/@SaiBabaLiveDarshan/live' WHERE name LIKE '%Shirdi%'");
            sdb.execSQL("UPDATE temples SET youtubeUrl = 'https://www.youtube.com/@shrikashivishwanath/live' WHERE name LIKE '%Kashi%'");
            sdb.execSQL("UPDATE temples SET youtubeUrl = 'https://www.youtube.com/@mhoneshraddha/live' WHERE name LIKE '%Vaishno%'");
            sdb.execSQL("UPDATE temples SET youtubeUrl = 'https://www.youtube.com/@soaboratemple/live' WHERE name LIKE '%Somnath%'");
            sdb.execSQL("UPDATE temples SET youtubeUrl = 'https://www.youtube.com/@ShreeSiddhivinayak/live' WHERE name LIKE '%Siddhivinayak%'");
            sdb.execSQL("UPDATE temples SET youtubeUrl = 'https://www.youtube.com/@sgpcsriamritsar/live' WHERE name LIKE '%Golden%'");
        } catch (Exception ignored) {}
    }

    private static void seedTemples(DivyaPathDatabase db) {
        if (db.templeDao().getCount() > 0) return;

        List<TempleEntity> temples = new ArrayList<>();

        TempleEntity t1 = new TempleEntity();
        t1.setName("Tirumala Tirupati");
        t1.setNameHindi("తిరుమల");
        t1.setLocation("Tirupati, Andhra Pradesh");
        t1.setYoutubeUrl("https://www.youtube.com/@svbcttd/live");
        t1.setLatitude(13.6833);
        t1.setLongitude(79.3471);
        t1.setTimings("2:30 AM - 1:00 AM");
        t1.setImageUrl("");
        t1.setDescription("One of the most visited holy places in the world, dedicated to Lord Venkateswara. The temple sits atop the seven hills of Tirumala.");
        t1.setHasLiveDarshan(true);
        temples.add(t1);

        TempleEntity t2 = new TempleEntity();
        t2.setName("Shirdi Sai Baba");
        t2.setNameHindi("शिर्डी");
        t2.setLocation("Shirdi, Maharashtra");
        t2.setYoutubeUrl("https://www.youtube.com/@SaiBabaLiveDarshan/live");
        t2.setLatitude(19.7667);
        t2.setLongitude(74.4833);
        t2.setTimings("4:00 AM - 10:30 PM");
        t2.setImageUrl("");
        t2.setDescription("The sacred shrine of Sai Baba of Shirdi, a revered saint who preached love, forgiveness, and devotion to God.");
        t2.setHasLiveDarshan(true);
        temples.add(t2);

        TempleEntity t3 = new TempleEntity();
        t3.setName("Kashi Vishwanath");
        t3.setNameHindi("काशी विश्वनाथ");
        t3.setLocation("Varanasi, Uttar Pradesh");
        t3.setYoutubeUrl("https://www.youtube.com/@shrikashivishwanath/live");
        t3.setLatitude(25.3109);
        t3.setLongitude(83.0107);
        t3.setTimings("2:30 AM - 11:00 PM");
        t3.setImageUrl("");
        t3.setDescription("One of the twelve Jyotirlingas dedicated to Lord Shiva, located on the western bank of the holy river Ganga in Varanasi.");
        t3.setHasLiveDarshan(true);
        temples.add(t3);

        TempleEntity t4 = new TempleEntity();
        t4.setName("Vaishno Devi");
        t4.setNameHindi("वैष्णो देवी");
        t4.setLocation("Katra, Jammu & Kashmir");
        t4.setYoutubeUrl("https://www.youtube.com/@mhoneshraddha/live");
        t4.setLatitude(33.0308);
        t4.setLongitude(74.9491);
        t4.setTimings("5:00 AM - 12:00 PM");
        t4.setImageUrl("");
        t4.setDescription("A sacred Hindu temple dedicated to Goddess Vaishno Devi, nestled in the Trikuta Mountains. Pilgrims trek through scenic mountain paths to reach the holy cave shrine.");
        t4.setHasLiveDarshan(true);
        temples.add(t4);

        TempleEntity t5 = new TempleEntity();
        t5.setName("Somnath");
        t5.setNameHindi("सोमनाथ");
        t5.setLocation("Somnath, Gujarat");
        t5.setYoutubeUrl("https://www.youtube.com/@soaboratemple/live");
        t5.setLatitude(20.8880);
        t5.setLongitude(70.4012);
        t5.setTimings("6:00 AM - 9:30 PM");
        t5.setImageUrl("");
        t5.setDescription("The first among the twelve Jyotirlingas of Lord Shiva, located on the western coast of Gujarat. The temple has been rebuilt multiple times throughout history.");
        t5.setHasLiveDarshan(true);
        temples.add(t5);

        TempleEntity t6 = new TempleEntity();
        t6.setName("Meenakshi Temple");
        t6.setNameHindi("मीनाक्षी मंदिर");
        t6.setLocation("Madurai, Tamil Nadu");
        t6.setYoutubeUrl("");
        t6.setLatitude(9.9195);
        t6.setLongitude(78.1193);
        t6.setTimings("5:00 AM - 12:30 PM, 4:00 PM - 10:00 PM");
        t6.setImageUrl("");
        t6.setDescription("A historic Hindu temple dedicated to Goddess Meenakshi and Lord Sundareshwar, known for its stunning Dravidian architecture and towering gopurams.");
        t6.setHasLiveDarshan(false);
        temples.add(t6);

        TempleEntity t7 = new TempleEntity();
        t7.setName("Siddhivinayak");
        t7.setNameHindi("सिद्धिविनायक");
        t7.setLocation("Mumbai, Maharashtra");
        t7.setYoutubeUrl("https://www.youtube.com/@ShreeSiddhivinayak/live");
        t7.setLatitude(19.0170);
        t7.setLongitude(72.8302);
        t7.setTimings("5:30 AM - 10:05 PM");
        t7.setImageUrl("");
        t7.setDescription("A renowned Hindu temple dedicated to Lord Ganesha, located in Prabhadevi, Mumbai. It is one of the most visited temples in the city.");
        t7.setHasLiveDarshan(true);
        temples.add(t7);

        TempleEntity t8 = new TempleEntity();
        t8.setName("ISKCON Vrindavan");
        t8.setNameHindi("इस्कॉन वृंदावन");
        t8.setLocation("Vrindavan, Uttar Pradesh");
        t8.setYoutubeUrl("");
        t8.setLatitude(27.5530);
        t8.setLongitude(77.6915);
        t8.setTimings("4:30 AM - 8:30 PM");
        t8.setImageUrl("");
        t8.setDescription("The Krishna Balaram Mandir in Vrindavan, a major ISKCON temple dedicated to Lord Krishna and Balaram in the holy land of Vrindavan.");
        t8.setHasLiveDarshan(false);
        temples.add(t8);

        TempleEntity t9 = new TempleEntity();
        t9.setName("Golden Temple");
        t9.setNameHindi("स्वर्ण मंदिर");
        t9.setLocation("Amritsar, Punjab");
        t9.setYoutubeUrl("https://www.youtube.com/@sgpcsriamritsar/live");
        t9.setLatitude(31.6200);
        t9.setLongitude(74.8765);
        t9.setTimings("2:00 AM - 10:00 PM");
        t9.setImageUrl("");
        t9.setDescription("Sri Harmandir Sahib, the holiest Gurdwara and spiritual center of Sikhism. The temple is covered in gold leaf and surrounded by the sacred Amrit Sarovar.");
        t9.setHasLiveDarshan(true);
        temples.add(t9);

        TempleEntity t10 = new TempleEntity();
        t10.setName("Jagannath Puri");
        t10.setNameHindi("जगन्नाथ पुरी");
        t10.setLocation("Puri, Odisha");
        t10.setYoutubeUrl("");
        t10.setLatitude(19.8048);
        t10.setLongitude(85.8180);
        t10.setTimings("5:00 AM - 11:00 PM");
        t10.setImageUrl("");
        t10.setDescription("One of the Char Dham pilgrimage sites, dedicated to Lord Jagannath. Famous for the annual Rath Yatra festival.");
        t10.setHasLiveDarshan(false);
        temples.add(t10);

        db.templeDao().insertAll(temples);
    }

    private static void seedBhajans(DivyaPathDatabase db) {
        if (db.bhajanDao().getCount() > 0) return;

        List<BhajanEntity> bhajans = new ArrayList<>();

        // Krishna Bhajans
        BhajanEntity b1 = new BhajanEntity();
        b1.setDeityId(9);
        b1.setTitle("Achyutam Keshavam");
        b1.setTitleHindi("अच्युतम् केशवम्");
        b1.setLyricsHindi("अच्युतम् केशवम् रामनारायणम्\nकृष्णदामोदरम् वासुदेवम् हरिम्\nश्रीधरम् माधवम् गोपिकावल्लभम्\nजानकीनायकम् रामचन्द्रम् भजे");
        b1.setLyricsEnglish("Achyutam Keshavam Rama Narayanam\nKrishna Damodaram Vasudevam Harim\nShridharam Madhavam Gopika Vallabham\nJanaki Nayakam Ramachandram Bhaje");
        b1.setCategory("krishna");
        b1.setLanguage("Hindi");
        b1.setDuration(300);
        bhajans.add(b1);

        BhajanEntity b2 = new BhajanEntity();
        b2.setDeityId(9);
        b2.setTitle("Hare Krishna Hare Rama");
        b2.setTitleHindi("हरे कृष्ण हरे राम");
        b2.setLyricsHindi("हरे कृष्ण हरे कृष्ण कृष्ण कृष्ण हरे हरे\nहरे राम हरे राम राम राम हरे हरे\nहरि बोल हरि बोल हरि हरि बोल\nमुकुंद माधव गोविंद बोल");
        b2.setLyricsEnglish("Hare Krishna Hare Krishna Krishna Krishna Hare Hare\nHare Rama Hare Rama Rama Rama Hare Hare\nHari Bol Hari Bol Hari Hari Bol\nMukund Madhav Govind Bol");
        b2.setCategory("krishna");
        b2.setLanguage("Hindi");
        b2.setDuration(300);
        bhajans.add(b2);

        BhajanEntity b3 = new BhajanEntity();
        b3.setDeityId(9);
        b3.setTitle("Govind Bolo Hari Gopal Bolo");
        b3.setTitleHindi("गोविन्द बोलो हरि गोपाल बोलो");
        b3.setLyricsHindi("गोविन्द बोलो हरि गोपाल बोलो\nराधा रमण हरि गोविन्द बोलो\nगोविन्द हरि हरि गोपाल हरि हरि\nराधा रमण हरि गोविन्द बोलो");
        b3.setLyricsEnglish("Govind Bolo Hari Gopal Bolo\nRadha Raman Hari Govind Bolo\nGovind Hari Hari Gopal Hari Hari\nRadha Raman Hari Govind Bolo");
        b3.setCategory("krishna");
        b3.setLanguage("Hindi");
        b3.setDuration(300);
        bhajans.add(b3);

        // Devi Bhajans
        BhajanEntity b4 = new BhajanEntity();
        b4.setDeityId(8);
        b4.setTitle("Jai Ambe Gauri");
        b4.setTitleHindi("जय अम्बे गौरी");
        b4.setLyricsHindi("जय अम्बे गौरी मैया जय श्यामा गौरी\nतुमको निशदिन ध्यावत हरि ब्रह्मा शिवरी\nमांग सिन्दूर विराजत टीको मृगमद को\nउज्ज्वल से दो नैना चन्द्रवदन नीको");
        b4.setLyricsEnglish("Jai Ambe Gauri Maiya Jai Shyama Gauri\nTumko Nishdin Dhyavat Hari Brahma Shivri\nMaang Sindoor Virajat Teeko Mrigmad Ko\nUjjwal Se Do Naina Chandravadan Neeko");
        b4.setCategory("devi");
        b4.setLanguage("Hindi");
        b4.setDuration(300);
        bhajans.add(b4);

        BhajanEntity b5 = new BhajanEntity();
        b5.setDeityId(8);
        b5.setTitle("Ya Devi Sarvabhuteshu");
        b5.setTitleHindi("या देवी सर्वभूतेषु");
        b5.setLyricsHindi("या देवी सर्वभूतेषु माँ दुर्गा रूपेण संस्थिता\nनमस्तस्यै नमस्तस्यै नमस्तस्यै नमो नमः\nया देवी सर्वभूतेषु शक्ति रूपेण संस्थिता\nनमस्तस्यै नमस्तस्यै नमस्तस्यै नमो नमः");
        b5.setLyricsEnglish("Ya Devi Sarvabhuteshu Maa Durga Rupena Samsthita\nNamastasyai Namastasyai Namastasyai Namo Namah\nYa Devi Sarvabhuteshu Shakti Rupena Samsthita\nNamastasyai Namastasyai Namastasyai Namo Namah");
        b5.setCategory("devi");
        b5.setLanguage("Hindi");
        b5.setDuration(300);
        bhajans.add(b5);

        // Shiva Bhajans
        BhajanEntity b6 = new BhajanEntity();
        b6.setDeityId(1);
        b6.setTitle("Bam Bam Bhole");
        b6.setTitleHindi("बम बम भोले");
        b6.setLyricsHindi("बम बम भोले बम बम भोले\nबम बम भोले बम बम भोले\nडमरू वाले बम बम भोले\nत्रिशूल वाले बम बम भोले");
        b6.setLyricsEnglish("Bam Bam Bhole Bam Bam Bhole\nBam Bam Bhole Bam Bam Bhole\nDamru Wale Bam Bam Bhole\nTrishul Wale Bam Bam Bhole");
        b6.setCategory("shiva");
        b6.setLanguage("Hindi");
        b6.setDuration(300);
        bhajans.add(b6);

        BhajanEntity b7 = new BhajanEntity();
        b7.setDeityId(1);
        b7.setTitle("Shiv Shankar Ko Jisne Puja");
        b7.setTitleHindi("शिव शंकर को जिसने पूजा");
        b7.setLyricsHindi("शिव शंकर को जिसने पूजा\nउसका ही उद्धार हुआ\nजिसने शंकर जी का ध्यान किया\nउसकी दुनिया से पहचान हुई");
        b7.setLyricsEnglish("Shiv Shankar Ko Jisne Puja\nUska Hi Uddhaar Hua\nJisne Shankar Ji Ka Dhyan Kiya\nUski Duniya Se Pehchaan Hui");
        b7.setCategory("shiva");
        b7.setLanguage("Hindi");
        b7.setDuration(300);
        bhajans.add(b7);

        // Ram Bhajans
        BhajanEntity b8 = new BhajanEntity();
        b8.setDeityId(11);
        b8.setTitle("Ram Siya Ram");
        b8.setTitleHindi("राम सिया राम");
        b8.setLyricsHindi("राम सिया राम सिया राम जय जय राम\nराम सिया राम सिया राम जय जय राम\nबोलो राम राम राम सीता राम राम राम\nबोलो राम राम राम सीता राम राम राम");
        b8.setLyricsEnglish("Ram Siya Ram Siya Ram Jai Jai Ram\nRam Siya Ram Siya Ram Jai Jai Ram\nBolo Ram Ram Ram Sita Ram Ram Ram\nBolo Ram Ram Ram Sita Ram Ram Ram");
        b8.setCategory("rama");
        b8.setLanguage("Hindi");
        b8.setDuration(300);
        bhajans.add(b8);

        BhajanEntity b9 = new BhajanEntity();
        b9.setDeityId(11);
        b9.setTitle("Shri Ramchandra Kripalu");
        b9.setTitleHindi("श्री रामचन्द्र कृपालु");
        b9.setLyricsHindi("श्री रामचन्द्र कृपालु भजु मन\nहरण भवभय दारुणम्\nनवकंज लोचन कंज मुख\nकर कंज पद कंजारुणम्");
        b9.setLyricsEnglish("Shri Ramchandra Kripalu Bhaju Man\nHaran Bhav Bhay Daarunam\nNav Kanj Lochan Kanj Mukh\nKar Kanj Pad Kanjarunam");
        b9.setCategory("rama");
        b9.setLanguage("Hindi");
        b9.setDuration(300);
        bhajans.add(b9);

        // Kabir Dohe
        BhajanEntity b10 = new BhajanEntity();
        b10.setDeityId(1);
        b10.setTitle("Dukh Mein Sumiran Sab Karein");
        b10.setTitleHindi("दुख में सुमिरन सब करें");
        b10.setLyricsHindi("दुख में सुमिरन सब करें सुख में करे न कोय\nजो सुख में सुमिरन करे दुख काहे को होय\nबुरा जो देखन मैं चला बुरा न मिलिया कोय\nजो दिल खोजा आपना मुझसे बुरा न कोय");
        b10.setLyricsEnglish("Dukh Mein Sumiran Sab Karein Sukh Mein Kare Na Koye\nJo Sukh Mein Sumiran Kare Dukh Kahe Ko Hoye\nBura Jo Dekhan Main Chala Bura Na Miliya Koye\nJo Dil Khoja Aapna Mujhse Bura Na Koye");
        b10.setCategory("kabir");
        b10.setLanguage("Hindi");
        b10.setDuration(300);
        bhajans.add(b10);

        BhajanEntity b11 = new BhajanEntity();
        b11.setDeityId(1);
        b11.setTitle("Bura Jo Dekhan Main Chala");
        b11.setTitleHindi("बुरा जो देखन मैं चला");
        b11.setLyricsHindi("बुरा जो देखन मैं चला बुरा न मिलिया कोय\nजो दिल खोजा आपना मुझसे बुरा न कोय\nपोथी पढ़ पढ़ जग मुआ पंडित भया न कोय\nढाई अक्षर प्रेम का पढ़े सो पंडित होय");
        b11.setLyricsEnglish("Bura Jo Dekhan Main Chala Bura Na Miliya Koye\nJo Dil Khoja Aapna Mujhse Bura Na Koye\nPothi Padh Padh Jag Mua Pandit Bhaya Na Koye\nDhai Akshar Prem Ka Padhe So Pandit Hoye");
        b11.setCategory("kabir");
        b11.setLanguage("Hindi");
        b11.setDuration(300);
        bhajans.add(b11);

        // Mirabai
        BhajanEntity b12 = new BhajanEntity();
        b12.setDeityId(9);
        b12.setTitle("Mere To Giridhar Gopal");
        b12.setTitleHindi("मेरे तो गिरिधर गोपाल");
        b12.setLyricsHindi("मेरे तो गिरिधर गोपाल दूसरो न कोई\nजाके सिर मोर मुकुट मेरो पति सोई\nतात मात भ्रात बन्धु आपनो न कोई\nछाँड़ि दई कुल की कानि कहा करिहै कोई");
        b12.setLyricsEnglish("Mere To Giridhar Gopal Doosro Na Koi\nJake Sir Mor Mukut Mero Pati Soi\nTaat Maat Bhrat Bandhu Aapno Na Koi\nChhandi Dai Kul Ki Kaani Kaha Karihai Koi");
        b12.setCategory("mirabai");
        b12.setLanguage("Hindi");
        b12.setDuration(300);
        bhajans.add(b12);

        db.bhajanDao().insertAll(bhajans);
    }

    private static void seedStotras(DivyaPathDatabase db) {
        if (db.stotraDao().getCount() > 0) return;

        List<StotraEntity> stotras = new ArrayList<>();

        StotraEntity s1 = new StotraEntity();
        s1.setDeityId(1);
        s1.setTitle("Shiv Tandav Stotram");
        s1.setTitleHindi("शिव तांडव स्तोत्रम्");
        s1.setTextSanskrit("जटाटवीगलज्जलप्रवाहपावितस्थले\nगलेऽवलम्ब्य लम्बितां भुजङ्गतुङ्गमालिकाम् ।\nडमड्डमड्डमड्डमन्निनादवड्डमर्वयं\nचकार चण्डताण्डवं तनोतु नः शिवः शिवम् ॥१॥\n\nजटाकटाहसम्भ्रमभ्रमन्निलिम्पनिर्झरी-\nविलोलवीचिवल्लरीविराजमानमूर्धनि ।\nधगद्धगद्धगज्ज्वलल्ललाटपट्टपावके\nकिशोरचन्द्रशेखरे रतिः प्रतिक्षणं मम ॥२॥");
        s1.setTextHindi("जटाओं के घने जंगल से बहती गंगा की धारा से पवित्र हुए स्थान पर, गले में लंबे सर्प की माला लटकाए, डमरू की ध्वनि करते हुए शिव ने प्रचंड तांडव किया। वे शिव हमारा कल्याण करें।");
        s1.setTextEnglish("Jatatavee galajjala pravaha pavitasthale\nGalevlamby lambitam bhujangatungamalikam\nDamad damad damaddama ninnadavaddamarvayam\nChakara chandtandavam tanotu nah shivah shivam");
        s1.setVerseCount(17);
        s1.setDuration(600);
        stotras.add(s1);

        StotraEntity s2 = new StotraEntity();
        s2.setDeityId(8);
        s2.setTitle("Mahishasura Mardini Stotram");
        s2.setTitleHindi("महिषासुरमर्दिनी स्तोत्रम्");
        s2.setTextSanskrit("अयि गिरिनन्दिनि नन्दितमेदिनि विश्वविनोदिनि नन्दनुते\nगिरिवरविन्ध्यशिरोऽधिनिवासिनि विष्णुविलासिनि जिष्णुनुते ।\nभगवति हे शितिकण्ठकुटुम्बिनि भूरिकुटुम्बिनि भूरिकृते\nजय जय हे महिषासुरमर्दिनि रम्यकपर्दिनि शैलसुते ॥१॥\n\nसुरवरवर्षिणि दुर्धरधर्षिणि दुर्मुखमर्षिणि हर्षरते\nत्रिभुवनपोषिणि शङ्करतोषिणि किल्बिषमोषिणि घोषरते ।\nदनुजनिरोषिणि दितिसुतरोषिणि दुर्मदशोषिणि सिन्धुसुते\nजय जय हे महिषासुरमर्दिनि रम्यकपर्दिनि शैलसुते ॥२॥");
        s2.setTextHindi("हे पर्वत की पुत्री, पृथ्वी को आनंदित करने वाली, विश्व को प्रसन्न करने वाली, विन्ध्य पर्वत पर निवास करने वाली, विष्णु की शक्ति, शिव की पत्नी, हे महिषासुरमर्दिनी, जय हो जय हो।");
        s2.setTextEnglish("Ayi Girinandini Nanditamedini Vishwavinodini Nandanute\nGirivaravindhya Shirodhinivasini Vishnuvilasini Jishnunute\nBhagavati He Shitikanthakutumbini Bhurikutumbini Bhurikrite\nJaya Jaya He Mahishasuramardini Ramyakapardini Shailasute");
        s2.setVerseCount(21);
        s2.setDuration(720);
        stotras.add(s2);

        StotraEntity s3 = new StotraEntity();
        s3.setDeityId(4);
        s3.setTitle("Vishnu Sahasranama");
        s3.setTitleHindi("विष्णु सहस्रनाम");
        s3.setTextSanskrit("विश्वं विष्णुर्वषट्कारो भूतभव्यभवत्प्रभुः ।\nभूतकृद्भूतभृद्भावो भूतात्मा भूतभावनः ॥१॥\n\nपूतात्मा परमात्मा च मुक्तानां परमा गतिः ।\nअव्ययः पुरुषः साक्षी क्षेत्रज्ञोऽक्षर एव च ॥२॥\n\nयोगो योगविदां नेता प्रधानपुरुषेश्वरः ।\nनारसिंहवपुः श्रीमान् केशवः पुरुषोत्तमः ॥३॥");
        s3.setTextHindi("जो सम्पूर्ण विश्व हैं, जो सर्वव्यापी विष्णु हैं, भूत-भविष्य-वर्तमान के स्वामी, सभी प्राणियों के रचयिता और पालनकर्ता, शुद्ध आत्मा, परमात्मा और मुक्त जीवों की परम गति हैं।");
        s3.setTextEnglish("Vishwam Vishnur Vashatkaro Bhutabhavya Bhavatprabhuh\nBhutakrid Bhutabhrid Bhavo Bhutatma Bhutabhavanah\nPutatma Paramatma Cha Muktanam Parama Gatih\nAvyayah Purushah Sakshi Kshetrajno Akshara Eva Cha");
        s3.setVerseCount(108);
        s3.setDuration(1800);
        stotras.add(s3);

        StotraEntity s4 = new StotraEntity();
        s4.setDeityId(5);
        s4.setTitle("Lalita Sahasranama");
        s4.setTitleHindi("ललिता सहस्रनाम");
        s4.setTextSanskrit("श्रीमाता श्रीमहाराज्ञी श्रीमत्सिंहासनेश्वरी ।\nचिदग्निकुण्डसम्भूता देवकार्यसमुद्यता ॥१॥\n\nउद्यद्भानुसहस्राभा चतुर्बाहुसमन्विता ।\nरागस्वरूपपाशाढ्या क्रोधाकाराङ्कुशोज्ज्वला ॥२॥\n\nमनोरूपेक्षुकोदण्डा पञ्चतन्मात्रसायका ।\nनिजारुणप्रभापूरमज्जद्ब्रह्माण्डमण्डला ॥३॥");
        s4.setTextHindi("श्री माता, महारानी, सिंहासन की स्वामिनी, चित्-अग्नि-कुंड से प्रकट हुईं, देवताओं के कार्य के लिए उद्यत, हजारों सूर्यों के समान तेजस्वी, चार भुजाओं वाली देवी।");
        s4.setTextEnglish("Shrimata Shrimaharajni Shrimat Simhasaneshwari\nChidagni Kunda Sambhuta Devakarya Samudyata\nUdyad Bhanu Sahasrabha Chaturbahu Samanvita\nRagaswarupa Pashadhya Krodhakaranku Shojjvala");
        s4.setVerseCount(108);
        s4.setDuration(1800);
        stotras.add(s4);

        StotraEntity s5 = new StotraEntity();
        s5.setDeityId(7);
        s5.setTitle("Aditya Hridayam");
        s5.setTitleHindi("आदित्य हृदयम्");
        s5.setTextSanskrit("ततो युद्धपरिश्रान्तं समरे चिन्तया स्थितम् ।\nरावणं चाग्रतो दृष्ट्वा युद्धाय समुपस्थितम् ॥१॥\n\nदैवतैश्च समागम्य द्रष्टुमभ्यागतो रणम् ।\nउपागम्याब्रवीद्रामम् अगस्त्यो भगवान् ऋषिः ॥२॥\n\nराम राम महाबाहो शृणु गुह्यं सनातनम् ।\nयेन सर्वानरीन् वत्स समरे विजयिष्यसि ॥३॥");
        s5.setTextHindi("तब युद्ध से थके हुए और चिंतित श्री राम को देखकर, तथा सामने रावण को युद्ध के लिए तैयार देखकर, देवताओं के साथ आए भगवान अगस्त्य ऋषि ने राम से कहा - हे महाबाहु राम, यह सनातन गोपनीय मंत्र सुनो।");
        s5.setTextEnglish("Tato Yuddha Parishrantam Samare Chintaya Sthitam\nRavanam Chagrato Drishtva Yuddhaya Samupasthitam\nDaivataischa Samagamya Drashtum Abhyagato Ranam\nUpagamya Abravid Ramam Agastyo Bhagavan Rishiph");
        s5.setVerseCount(31);
        s5.setDuration(900);
        stotras.add(s5);

        StotraEntity s6 = new StotraEntity();
        s6.setDeityId(4);
        s6.setTitle("Purusha Suktam");
        s6.setTitleHindi("पुरुष सूक्तम्");
        s6.setTextSanskrit("सहस्रशीर्षा पुरुषः सहस्राक्षः सहस्रपात् ।\nस भूमिं विश्वतो वृत्वात्यतिष्ठद्दशाङ्गुलम् ॥१॥\n\nपुरुष एवेदं सर्वं यद्भूतं यच्च भव्यम् ।\nउतामृतत्वस्येशानो यदन्नेनातिरोहति ॥२॥\n\nएतावानस्य महिमातो ज्यायांश्च पूरुषः ।\nपादोऽस्य विश्वा भूतानि त्रिपादस्यामृतं दिवि ॥३॥");
        s6.setTextHindi("पुरुष (परमात्मा) के सहस्र शीर्ष, सहस्र नेत्र और सहस्र पाद हैं। वे सम्पूर्ण पृथ्वी को व्याप्त करके भी दस अंगुल ऊपर शेष रहते हैं। जो कुछ भूत और भविष्य है, वह सब पुरुष ही हैं।");
        s6.setTextEnglish("Sahasra Shirsha Purushah Sahasrakshah Sahasrapat\nSa Bhumim Vishvato Vritva Atyatishthad Dashangulam\nPurusha Evedam Sarvam Yad Bhutam Yachcha Bhavyam\nUtamritatvasya Ishano Yad Annena Atirohati");
        s6.setVerseCount(16);
        s6.setDuration(600);
        stotras.add(s6);

        StotraEntity s7 = new StotraEntity();
        s7.setDeityId(5);
        s7.setTitle("Sri Suktam");
        s7.setTitleHindi("श्री सूक्तम्");
        s7.setTextSanskrit("हिरण्यवर्णां हरिणीं सुवर्णरजतस्रजाम् ।\nचन्द्रां हिरण्मयीं लक्ष्मीं जातवेदो म आवह ॥१॥\n\nतां म आवह जातवेदो लक्ष्मीमनपगामिनीम् ।\nयस्यां हिरण्यं विन्देयं गामश्वं पुरुषानहम् ॥२॥\n\nअश्वपूर्वां रथमध्यां हस्तिनादप्रबोधिनीम् ।\nश्रियं देवीमुपह्वये श्रीर्मा देवीर्जुषताम् ॥३॥");
        s7.setTextHindi("स्वर्ण वर्ण वाली, सोने-चाँदी के आभूषणों से सुशोभित, चन्द्रमा के समान कान्तिमती, स्वर्णमयी लक्ष्मी को हे जातवेद अग्नि, मेरे पास लाइए। वह लक्ष्मी जो कभी न जाने वाली हैं।");
        s7.setTextEnglish("Hiranyavarnam Harinim Suvarnarajatasrajam\nChandram Hiranmayim Lakshmim Jatavedo Ma Avaha\nTam Ma Avaha Jatavedo Lakshmimanapagaminim\nYasyam Hiranyam Vindeyam Gamashvam Purushaanaham");
        s7.setVerseCount(16);
        s7.setDuration(600);
        stotras.add(s7);

        db.stotraDao().insertAll(stotras);
    }
}
