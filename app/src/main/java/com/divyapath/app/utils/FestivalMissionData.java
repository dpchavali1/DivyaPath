package com.divyapath.app.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FestivalMissionData {

    public static class MissionTask {
        public final String title;
        public final String titleHindi;
        public final String category; // CHANT, READ, SHOP, DONATE, CLEAN, COOK, WORSHIP
        public final String description;

        public MissionTask(String title, String titleHindi, String category, String description) {
            this.title = title;
            this.titleHindi = titleHindi;
            this.category = category;
            this.description = description;
        }
    }

    // Map<festivalName, List<List<MissionTask>>> — 7 days, each day has 3-4 tasks
    private static final Map<String, List<List<MissionTask>>> MISSIONS = new LinkedHashMap<>();

    static {
        // ===== DIWALI =====
        List<List<MissionTask>> diwali = new ArrayList<>();
        diwali.add(Arrays.asList(
                new MissionTask("Clean one room", "एक कमरे की सफाई करें", "CLEAN", "Start Diwali prep by deep cleaning one room today."),
                new MissionTask("Chant Lakshmi mantra 11 times", "लक्ष्मी मंत्र 11 बार जपें", "CHANT", "Om Shreem Mahalakshmiyei Namaha"),
                new MissionTask("Buy diyas and candles", "दीये और मोमबत्तियां खरीदें", "SHOP", "Stock up on earthen diyas and decorative candles.")
        ));
        diwali.add(Arrays.asList(
                new MissionTask("Clean another room", "एक और कमरे की सफाई करें", "CLEAN", "Continue deep cleaning."),
                new MissionTask("Read Lakshmi Chalisa", "लक्ष्मी चालीसा पढ़ें", "READ", "Read the Lakshmi Chalisa for prosperity."),
                new MissionTask("Buy rangoli colors", "रंगोली के रंग खरीदें", "SHOP", "Get vibrant colors for making rangoli.")
        ));
        diwali.add(Arrays.asList(
                new MissionTask("Make a rangoli design", "रंगोली बनाएं", "WORSHIP", "Create a beautiful rangoli at your entrance."),
                new MissionTask("Chant Ganesh mantra 21 times", "गणेश मंत्र 21 बार जपें", "CHANT", "Om Gan Ganapataye Namaha"),
                new MissionTask("Buy sweets for distribution", "बांटने के लिए मिठाई खरीदें", "SHOP", "Buy sweets to share with neighbors.")
        ));
        diwali.add(Arrays.asList(
                new MissionTask("Decorate with lights", "रोशनी से सजावट करें", "CLEAN", "Hang lights and torans on doors and windows."),
                new MissionTask("Read Vishnu Sahasranama", "विष्णु सहस्रनाम पढ़ें", "READ", "Recite for divine blessings."),
                new MissionTask("Donate to charity", "दान करें", "DONATE", "Give clothes, food, or money to the needy.")
        ));
        diwali.add(Arrays.asList(
                new MissionTask("Prepare puja thali", "पूजा थाली तैयार करें", "WORSHIP", "Arrange all items for Lakshmi Puja."),
                new MissionTask("Chant Lakshmi mantra 108 times", "लक्ष्मी मंत्र 108 बार जपें", "CHANT", "Full mala japa for prosperity."),
                new MissionTask("Buy new clothes", "नए कपड़े खरीदें", "SHOP", "Buy new clothes for the family.")
        ));
        diwali.add(Arrays.asList(
                new MissionTask("Perform Lakshmi Puja", "लक्ष्मी पूजा करें", "WORSHIP", "Complete Lakshmi-Ganesh puja in the evening."),
                new MissionTask("Light diyas in every room", "हर कमरे में दीये जलाएं", "WORSHIP", "Illuminate your home with earthen diyas."),
                new MissionTask("Share sweets with neighbors", "पड़ोसियों को मिठाई बांटें", "DONATE", "Spread joy by sharing sweets.")
        ));
        diwali.add(Arrays.asList(
                new MissionTask("Govardhan Puja", "गोवर्धन पूजा", "WORSHIP", "Perform Govardhan Puja and prepare annakut."),
                new MissionTask("Visit temple", "मंदिर जाएं", "WORSHIP", "Visit your local temple for darshan."),
                new MissionTask("Feed animals", "जानवरों को खिलाएं", "DONATE", "Offer food to cows and street animals.")
        ));
        MISSIONS.put("Diwali", diwali);

        // ===== NAVRATRI =====
        List<List<MissionTask>> navratri = new ArrayList<>();
        navratri.add(Arrays.asList(
                new MissionTask("Set up Navratri altar", "नवरात्रि वेदी सजाएं", "WORSHIP", "Clean and set up the puja space for 9 days."),
                new MissionTask("Chant Durga mantra 11 times", "दुर्गा मंत्र 11 बार जपें", "CHANT", "Om Dum Durgayei Namaha"),
                new MissionTask("Buy flowers and incense", "फूल और अगरबत्ती खरीदें", "SHOP", "Stock supplies for 9 days of worship.")
        ));
        navratri.add(Arrays.asList(
                new MissionTask("Wear red — Day of Maa Brahmacharini", "लाल पहनें — माँ ब्रह्मचारिणी दिवस", "WORSHIP", "Wear the color of the day and perform puja."),
                new MissionTask("Read Durga Chalisa", "दुर्गा चालीसा पढ़ें", "READ", "Read the Durga Chalisa with devotion."),
                new MissionTask("Observe fast", "व्रत रखें", "WORSHIP", "Keep a sattvic fast if possible.")
        ));
        navratri.add(Arrays.asList(
                new MissionTask("Offer chunari to Devi", "देवी को चुनरी अर्पित करें", "WORSHIP", "Offer a red chunari at the altar."),
                new MissionTask("Chant Durga Saptashati path", "दुर्गा सप्तशती पाठ करें", "CHANT", "Recite a chapter from Durga Saptashati."),
                new MissionTask("Donate food to poor", "गरीबों को भोजन दान करें", "DONATE", "Feed those in need as seva.")
        ));
        navratri.add(Arrays.asList(
                new MissionTask("Perform havan", "हवन करें", "WORSHIP", "Conduct a small havan at home if possible."),
                new MissionTask("Read about Nava Durga forms", "नव दुर्गा के बारे में पढ़ें", "READ", "Learn about the 9 forms of Goddess Durga."),
                new MissionTask("Buy prasad ingredients", "प्रसाद सामग्री खरीदें", "SHOP", "Buy halwa and puri ingredients for prasad.")
        ));
        navratri.add(Arrays.asList(
                new MissionTask("Visit Devi temple", "देवी मंदिर जाएं", "WORSHIP", "Visit a Durga or Devi temple for darshan."),
                new MissionTask("Chant Devi mantra 108 times", "देवी मंत्र 108 बार जपें", "CHANT", "Full mala japa of Durga mantra."),
                new MissionTask("Donate clothes", "कपड़े दान करें", "DONATE", "Give clothes to the underprivileged.")
        ));
        navratri.add(Arrays.asList(
                new MissionTask("Prepare special prasad", "विशेष प्रसाद तैयार करें", "COOK", "Cook halwa puri for Navratri offering."),
                new MissionTask("Read Devi Mahatmyam", "देवी माहात्म्य पढ़ें", "READ", "Read select chapters of Devi Mahatmyam."),
                new MissionTask("Light 9 diyas", "9 दीये जलाएं", "WORSHIP", "Light 9 diyas representing the 9 forms.")
        ));
        navratri.add(Arrays.asList(
                new MissionTask("Kanya Pujan", "कन्या पूजन", "WORSHIP", "Perform Kanya Pujan — worship young girls as Devi."),
                new MissionTask("Perform final Durga Aarti", "अंतिम दुर्गा आरती करें", "WORSHIP", "Complete the 9-day worship with grand aarti."),
                new MissionTask("Distribute prasad", "प्रसाद वितरण करें", "DONATE", "Share prasad with family, friends, and neighbors.")
        ));
        MISSIONS.put("Navratri", navratri);

        // ===== MAHA SHIVARATRI =====
        List<List<MissionTask>> shivaratri = new ArrayList<>();
        shivaratri.add(Arrays.asList(
                new MissionTask("Clean puja area", "पूजा स्थल की सफाई करें", "CLEAN", "Prepare a clean space for Shiva worship."),
                new MissionTask("Chant Om Namah Shivaya 11 times", "ॐ नमः शिवाय 11 बार जपें", "CHANT", "Begin the preparatory chanting."),
                new MissionTask("Buy bilva leaves and milk", "बिल्व पत्र और दूध खरीदें", "SHOP", "Gather essentials for Shiva abhishek.")
        ));
        shivaratri.add(Arrays.asList(
                new MissionTask("Read Shiva Chalisa", "शिव चालीसा पढ़ें", "READ", "Read the Shiva Chalisa with devotion."),
                new MissionTask("Chant Maha Mrityunjaya 21 times", "महा मृत्युंजय 21 बार जपें", "CHANT", "Practice the healing mantra."),
                new MissionTask("Buy dhatura and bhang", "धतूरा और भांग खरीदें", "SHOP", "Traditional offerings for Shiva.")
        ));
        shivaratri.add(Arrays.asList(
                new MissionTask("Visit Shiva temple", "शिव मंदिर जाएं", "WORSHIP", "Perform darshan at a Shiva temple."),
                new MissionTask("Read Shiva Purana stories", "शिव पुराण कथाएं पढ़ें", "READ", "Read about Shiva's divine exploits."),
                new MissionTask("Donate food", "भोजन दान करें", "DONATE", "Give food to the poor and needy.")
        ));
        shivaratri.add(Arrays.asList(
                new MissionTask("Chant Om Namah Shivaya 108 times", "ॐ नमः शिवाय 108 बार जपें", "CHANT", "Full mala japa for Shiva."),
                new MissionTask("Prepare abhishek items", "अभिषेक सामग्री तैयार करें", "WORSHIP", "Arrange milk, curd, honey, ghee, water for panchamrit."),
                new MissionTask("Read Rudrashtakam", "रुद्राष्टकम पढ़ें", "READ", "Recite this powerful Shiva stotra.")
        ));
        shivaratri.add(Arrays.asList(
                new MissionTask("Observe fast", "व्रत रखें", "WORSHIP", "Keep a strict fast or fruit-only diet."),
                new MissionTask("Perform Shiva Abhishek", "शिव अभिषेक करें", "WORSHIP", "Bathe the Shivling with panchamrit."),
                new MissionTask("Chant during all 4 prahars", "चारों प्रहर में जप करें", "CHANT", "Chant mantras in each of the 4 watches of the night.")
        ));
        shivaratri.add(Arrays.asList(
                new MissionTask("Night vigil (jagran)", "रात्रि जागरण", "WORSHIP", "Stay awake through the night in devotion."),
                new MissionTask("Read Shiva Tandava Stotram", "शिव तांडव स्तोत्र पढ़ें", "READ", "Recite this powerful hymn to Shiva."),
                new MissionTask("Offer bilva patra", "बिल्व पत्र अर्पित करें", "WORSHIP", "Offer 108 bilva leaves to Shiva.")
        ));
        shivaratri.add(Arrays.asList(
                new MissionTask("Morning aarti", "प्रातः आरती", "WORSHIP", "Perform the concluding aarti at dawn."),
                new MissionTask("Break fast with prasad", "प्रसाद से व्रत तोड़ें", "WORSHIP", "End your fast with blessed prasad."),
                new MissionTask("Share blessings", "आशीर्वाद बांटें", "DONATE", "Share prasad and blessings with all.")
        ));
        MISSIONS.put("Maha Shivaratri", shivaratri);

        // ===== GANESH CHATURTHI =====
        List<List<MissionTask>> ganeshChaturthi = new ArrayList<>();
        ganeshChaturthi.add(Arrays.asList(
                new MissionTask("Clean puja space", "पूजा स्थल साफ करें", "CLEAN", "Prepare the area for Ganesha's arrival."),
                new MissionTask("Chant Ganesh mantra 21 times", "गणेश मंत्र 21 बार जपें", "CHANT", "Om Gan Ganapataye Namaha"),
                new MissionTask("Buy eco-friendly Ganesha idol", "इको-फ्रेंडली गणेश मूर्ति खरीदें", "SHOP", "Choose a clay idol for eco-friendly celebration.")
        ));
        ganeshChaturthi.add(Arrays.asList(
                new MissionTask("Decorate the mandap", "मंडप सजाएं", "CLEAN", "Set up a beautiful pandal for Ganesha."),
                new MissionTask("Read Ganapati Atharvashirsha", "गणपति अथर्वशीर्ष पढ़ें", "READ", "Recite this sacred hymn."),
                new MissionTask("Buy modak ingredients", "मोदक सामग्री खरीदें", "SHOP", "Buy rice flour, coconut, and jaggery for modak.")
        ));
        ganeshChaturthi.add(Arrays.asList(
                new MissionTask("Prepare homemade modak", "घर पर मोदक बनाएं", "COOK", "Make Ganesha's favorite sweet at home."),
                new MissionTask("Chant Ganesh Chalisa", "गणेश चालीसा पढ़ें", "CHANT", "Recite with devotion."),
                new MissionTask("Buy durva grass and flowers", "दूर्वा घास और फूल खरीदें", "SHOP", "Essential offerings for Ganesha.")
        ));
        ganeshChaturthi.add(Arrays.asList(
                new MissionTask("Perform Ganesh Sthapana", "गणेश स्थापना करें", "WORSHIP", "Install the idol with proper rituals."),
                new MissionTask("Offer 21 durva blades", "21 दूर्वा अर्पित करें", "WORSHIP", "Offer 21 blades of durva grass."),
                new MissionTask("Donate to charity", "दान करें", "DONATE", "Give food or supplies to those in need.")
        ));
        ganeshChaturthi.add(Arrays.asList(
                new MissionTask("Daily Ganesh aarti", "प्रतिदिन गणेश आरती", "WORSHIP", "Perform morning and evening aarti."),
                new MissionTask("Read Ganesha stories", "गणेश कथाएं पढ़ें", "READ", "Read about Ganesha's divine exploits."),
                new MissionTask("Distribute prasad", "प्रसाद बांटें", "DONATE", "Share modak with neighbors.")
        ));
        ganeshChaturthi.add(Arrays.asList(
                new MissionTask("Chant Ganesh mantra 108 times", "गणेश मंत्र 108 बार जपें", "CHANT", "Full mala japa."),
                new MissionTask("Visit community pandal", "सार्वजनिक पंडाल जाएं", "WORSHIP", "Visit nearby Ganesh pandals for darshan."),
                new MissionTask("Feed the poor", "गरीबों को भोजन दें", "DONATE", "Organize or participate in community feeding.")
        ));
        ganeshChaturthi.add(Arrays.asList(
                new MissionTask("Perform Visarjan puja", "विसर्जन पूजा करें", "WORSHIP", "Bid farewell to Ganesha with full rituals."),
                new MissionTask("Eco-friendly immersion", "पर्यावरण-अनुकूल विसर्जन", "WORSHIP", "Immerse the idol in an eco-friendly way."),
                new MissionTask("Pray for Ganesha's return", "गणेश जी की वापसी की प्रार्थना करें", "CHANT", "Ganpati Bappa Morya, Pudhchya Varshi Lavkar Ya!")
        ));
        MISSIONS.put("Ganesh Chaturthi", ganeshChaturthi);

        // ===== HOLI =====
        List<List<MissionTask>> holi = new ArrayList<>();
        holi.add(Arrays.asList(
                new MissionTask("Read Prahlad-Holika story", "प्रह्लाद-होलिका कथा पढ़ें", "READ", "Understand the significance of Holi."),
                new MissionTask("Chant Vishnu mantra 11 times", "विष्णु मंत्र 11 बार जपें", "CHANT", "Om Namo Narayanaya"),
                new MissionTask("Buy organic colors", "जैविक रंग खरीदें", "SHOP", "Choose natural, skin-safe colors.")
        ));
        holi.add(Arrays.asList(
                new MissionTask("Buy gujiya ingredients", "गुजिया सामग्री खरीदें", "SHOP", "Buy khoya, dry fruits, and flour."),
                new MissionTask("Read Krishna Leela", "कृष्ण लीला पढ़ें", "READ", "Read about Krishna's playful Holi celebrations."),
                new MissionTask("Clean outdoor area", "बाहरी क्षेत्र की सफाई करें", "CLEAN", "Prepare spaces for Holi celebrations.")
        ));
        holi.add(Arrays.asList(
                new MissionTask("Make gujiya at home", "घर पर गुजिया बनाएं", "COOK", "Prepare the traditional Holi sweet."),
                new MissionTask("Chant Hare Krishna 108 times", "हरे कृष्ण 108 बार जपें", "CHANT", "Celebrate with divine chanting."),
                new MissionTask("Buy thandai ingredients", "ठंडाई सामग्री खरीदें", "SHOP", "Almonds, saffron, fennel for traditional drink.")
        ));
        holi.add(Arrays.asList(
                new MissionTask("Prepare wood for Holika Dahan", "होलिका दहन की लकड़ी तैयार करें", "WORSHIP", "Gather or arrange wood for the bonfire."),
                new MissionTask("Read Holi prayers", "होली प्रार्थना पढ़ें", "READ", "Read about the significance of Holika Dahan."),
                new MissionTask("Donate old clothes", "पुराने कपड़े दान करें", "DONATE", "Share what you can with others.")
        ));
        holi.add(Arrays.asList(
                new MissionTask("Attend Holika Dahan", "होलिका दहन में जाएं", "WORSHIP", "Participate in the community bonfire ritual."),
                new MissionTask("Offer coconut to fire", "अग्नि में नारियल अर्पित करें", "WORSHIP", "Offer coconut and grain to the sacred fire."),
                new MissionTask("Chant protection mantras", "रक्षा मंत्र जपें", "CHANT", "Chant for protection from negativity.")
        ));
        holi.add(Arrays.asList(
                new MissionTask("Play Holi with colors", "रंगों से होली खेलें", "WORSHIP", "Celebrate with family and friends."),
                new MissionTask("Make thandai", "ठंडाई बनाएं", "COOK", "Prepare traditional Holi drink."),
                new MissionTask("Share sweets with neighbors", "पड़ोसियों को मिठाई बांटें", "DONATE", "Spread love and sweetness.")
        ));
        holi.add(Arrays.asList(
                new MissionTask("Visit temple for darshan", "दर्शन के लिए मंदिर जाएं", "WORSHIP", "Thank the divine after celebrations."),
                new MissionTask("Forgive and reconcile", "क्षमा करें और मेल करें", "DONATE", "Holi is about new beginnings — forgive old grudges."),
                new MissionTask("Chant Vishnu mantra 11 times", "विष्णु मंत्र 11 बार जपें", "CHANT", "Close the festival with devotion.")
        ));
        MISSIONS.put("Holi", holi);

        // ===== JANMASHTAMI =====
        List<List<MissionTask>> janmashtami = new ArrayList<>();
        janmashtami.add(Arrays.asList(
                new MissionTask("Clean puja area", "पूजा स्थल साफ करें", "CLEAN", "Prepare for Krishna Janmashtami."),
                new MissionTask("Chant Hare Krishna 11 times", "हरे कृष्ण 11 बार जपें", "CHANT", "Begin the countdown with devotion."),
                new MissionTask("Buy decorations", "सजावट सामान खरीदें", "SHOP", "Flowers, jhula items for Krishna's cradle.")
        ));
        janmashtami.add(Arrays.asList(
                new MissionTask("Set up Krishna jhula", "कृष्ण झूला सजाएं", "WORSHIP", "Decorate a swing for baby Krishna."),
                new MissionTask("Read Bhagavad Gita Chapter 4", "भगवद्गीता अध्याय 4 पढ़ें", "READ", "Chapter on divine birth."),
                new MissionTask("Buy butter and mishri", "माखन और मिश्री खरीदें", "SHOP", "Krishna's favorite offerings.")
        ));
        janmashtami.add(Arrays.asList(
                new MissionTask("Learn Krishna bhajan", "कृष्ण भजन सीखें", "READ", "Learn a devotional song for the occasion."),
                new MissionTask("Chant Hare Krishna mala", "हरे कृष्ण माला जपें", "CHANT", "108 rounds of maha mantra."),
                new MissionTask("Donate milk to needy", "ज़रूरतमंदों को दूध दान करें", "DONATE", "Share Krishna's blessings.")
        ));
        janmashtami.add(Arrays.asList(
                new MissionTask("Decorate home with peacock feathers", "मोरपंख से घर सजाएं", "CLEAN", "Krishna's signature decoration."),
                new MissionTask("Read Krishna's birth story", "कृष्ण जन्म कथा पढ़ें", "READ", "Read about the divine birth in Mathura."),
                new MissionTask("Prepare panjiri/makhan mishri", "पंजीरी/माखन मिश्री तैयार करें", "COOK", "Make traditional Janmashtami prasad.")
        ));
        janmashtami.add(Arrays.asList(
                new MissionTask("Observe Janmashtami fast", "जन्माष्टमी व्रत रखें", "WORSHIP", "Fast until midnight."),
                new MissionTask("Perform midnight puja", "मध्यरात्रि पूजा करें", "WORSHIP", "Celebrate Krishna's birth at midnight."),
                new MissionTask("Rock the jhula at midnight", "मध्यरात्रि में झूला झुलाएं", "WORSHIP", "Welcome baby Krishna with joy.")
        ));
        janmashtami.add(Arrays.asList(
                new MissionTask("Break fast with prasad", "प्रसाद से व्रत तोड़ें", "WORSHIP", "End your fast with panjiri and fruits."),
                new MissionTask("Visit Krishna temple", "कृष्ण मंदिर जाएं", "WORSHIP", "Seek darshan at a Krishna temple."),
                new MissionTask("Distribute prasad", "प्रसाद बांटें", "DONATE", "Share blessed food with everyone.")
        ));
        janmashtami.add(Arrays.asList(
                new MissionTask("Dahi Handi celebration", "दही हांडी उत्सव", "WORSHIP", "Watch or participate in Dahi Handi."),
                new MissionTask("Chant Hare Krishna 108 times", "हरे कृष्ण 108 बार जपें", "CHANT", "Conclude the festival with devotion."),
                new MissionTask("Help community cleanup", "सामुदायिक सफाई में मदद करें", "DONATE", "Help clean up after celebrations.")
        ));
        MISSIONS.put("Janmashtami", janmashtami);
    }

    public static List<List<MissionTask>> getMissions(String festivalName) {
        // Try exact match first, then partial match
        if (MISSIONS.containsKey(festivalName)) return MISSIONS.get(festivalName);
        for (Map.Entry<String, List<List<MissionTask>>> entry : MISSIONS.entrySet()) {
            if (festivalName.toLowerCase().contains(entry.getKey().toLowerCase()) ||
                    entry.getKey().toLowerCase().contains(festivalName.toLowerCase())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static boolean hasMissions(String festivalName) {
        return getMissions(festivalName) != null;
    }

    public static List<String> getSupportedFestivals() {
        return new ArrayList<>(MISSIONS.keySet());
    }
}
