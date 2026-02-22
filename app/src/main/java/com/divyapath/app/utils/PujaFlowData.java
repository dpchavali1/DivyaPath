package com.divyapath.app.utils;

import com.divyapath.app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PujaFlowData {

    public static final String TIER_QUICK = "quick";       // 5 min
    public static final String TIER_STANDARD = "standard";  // 15 min
    public static final String TIER_FULL = "full";          // 30+ min

    public static class PujaStep {
        public final String title;
        public final String titleHindi;
        public final String instruction;
        public final String instructionHindi;
        public final List<String> itemsNeeded;
        public final int audioCueResId;

        public PujaStep(String title, String titleHindi, String instruction,
                        String instructionHindi, List<String> itemsNeeded, int audioCueResId) {
            this.title = title;
            this.titleHindi = titleHindi;
            this.instruction = instruction;
            this.instructionHindi = instructionHindi;
            this.itemsNeeded = itemsNeeded != null ? itemsNeeded : Collections.emptyList();
            this.audioCueResId = audioCueResId;
        }
    }

    private static final Map<String, Map<String, List<PujaStep>>> ALL_FLOWS = new LinkedHashMap<>();

    static {
        // ===== LORD GANESHA =====
        Map<String, List<PujaStep>> ganesha = new LinkedHashMap<>();

        ganesha.put(TIER_QUICK, Arrays.asList(
                new PujaStep("Light Diya", "दीया जलाएं",
                        "Light a ghee diya and place it near the idol or image of Lord Ganesha.",
                        "घी का दीया जलाकर गणेश जी की मूर्ति के पास रखें।",
                        Arrays.asList("Ghee diya", "Matchbox"), R.raw.bell_tone),
                new PujaStep("Offer Durva Grass", "दूर्वा अर्पित करें",
                        "Place 3 blades of durva grass at the feet of Ganesha.",
                        "गणेश जी के चरणों में 3 दूर्वा की पत्तियां चढ़ाएं।",
                        Arrays.asList("Durva grass"), 0),
                new PujaStep("Chant Mantra", "मंत्र जाप",
                        "Chant 'Om Gan Ganapataye Namaha' 11 times.",
                        "'ॐ गं गणपतये नमः' 11 बार जपें।",
                        null, R.raw.om_tone),
                new PujaStep("Offer Modak", "मोदक अर्पित करें",
                        "Offer modak or any sweet as prasad.",
                        "मोदक या कोई मिठाई प्रसाद के रूप में अर्पित करें।",
                        Arrays.asList("Modak or sweet"), R.raw.chime_tone)
        ));

        ganesha.put(TIER_STANDARD, Arrays.asList(
                new PujaStep("Dhyan (Meditation)", "ध्यान",
                        "Close your eyes and meditate on Lord Ganesha for a moment.",
                        "आंखें बंद करें और गणेश जी का ध्यान करें।",
                        null, R.raw.om_tone),
                new PujaStep("Avahan (Invocation)", "आवाहन",
                        "Ring the bell and invite Lord Ganesha to accept your worship.",
                        "घंटी बजाएं और गणेश जी को पूजा स्वीकार करने का आमंत्रण दें।",
                        Arrays.asList("Bell"), R.raw.bell_tone),
                new PujaStep("Snaan (Bathing)", "स्नान",
                        "Sprinkle water on the idol while chanting mantras.",
                        "मंत्र जपते हुए मूर्ति पर जल छिड़कें।",
                        Arrays.asList("Water", "Small spoon"), 0),
                new PujaStep("Vastra (Clothing)", "वस्त्र",
                        "Offer a red cloth or drape to the deity.",
                        "देवता को लाल वस्त्र अर्पित करें।",
                        Arrays.asList("Red cloth"), 0),
                new PujaStep("Chandan & Kumkum", "चंदन और कुमकुम",
                        "Apply sandalwood paste and kumkum tilak on the idol.",
                        "मूर्ति पर चंदन और कुमकुम का तिलक लगाएं।",
                        Arrays.asList("Sandalwood paste", "Kumkum"), 0),
                new PujaStep("Durva & Flowers", "दूर्वा और पुष्प",
                        "Offer durva grass and red flowers.",
                        "दूर्वा घास और लाल फूल अर्पित करें।",
                        Arrays.asList("Durva grass", "Red flowers"), 0),
                new PujaStep("Dhoop & Diya", "धूप और दीपक",
                        "Light incense and a ghee diya. Wave in circular motion.",
                        "अगरबत्ती और घी का दीया जलाएं। गोलाकार में घुमाएं।",
                        Arrays.asList("Incense sticks", "Ghee diya"), R.raw.bell_tone),
                new PujaStep("Naivedya (Offering)", "नैवेद्य",
                        "Offer modak, fruits, and coconut.",
                        "मोदक, फल और नारियल अर्पित करें।",
                        Arrays.asList("Modak", "Fruits", "Coconut"), R.raw.chime_tone),
                new PujaStep("Aarti", "आरती",
                        "Perform Ganesha aarti — 'Jai Ganesh Jai Ganesh Deva'.",
                        "'जय गणेश जय गणेश देवा' आरती करें।",
                        Arrays.asList("Aarti plate"), R.raw.bell_tone),
                new PujaStep("Prarthana (Prayer)", "प्रार्थना",
                        "Fold hands and pray to Lord Ganesha for blessings.",
                        "हाथ जोड़कर गणेश जी से आशीर्वाद की प्रार्थना करें।",
                        null, R.raw.om_tone)
        ));

        ganesha.put(TIER_FULL, new ArrayList<>(ganesha.get(TIER_STANDARD)));
        List<PujaStep> ganeshaFull = ganesha.get(TIER_FULL);
        ganeshaFull.add(6, new PujaStep("Sindoor", "सिंदूर",
                "Apply sindoor (vermilion) to the idol.",
                "मूर्ति पर सिंदूर लगाएं।",
                Arrays.asList("Sindoor"), 0));
        ganeshaFull.add(new PujaStep("Atharvashirsha", "अथर्वशीर्ष",
                "Recite Ganapati Atharvashirsha for complete blessing.",
                "संपूर्ण आशीर्वाद के लिए गणपति अथर्वशीर्ष का पाठ करें।",
                null, R.raw.om_tone));
        ganeshaFull.add(new PujaStep("Pradakshina", "प्रदक्षिणा",
                "Circumambulate the idol or turn around yourself 3 times.",
                "मूर्ति की 3 बार परिक्रमा करें या स्वयं 3 बार घूमें।",
                null, R.raw.chime_tone));

        ALL_FLOWS.put("Lord Ganesha", ganesha);

        // ===== LORD SHIVA =====
        Map<String, List<PujaStep>> shiva = new LinkedHashMap<>();

        shiva.put(TIER_QUICK, Arrays.asList(
                new PujaStep("Light Diya", "दीया जलाएं",
                        "Light a ghee or oil diya near the Shivling.",
                        "शिवलिंग के पास घी या तेल का दीया जलाएं।",
                        Arrays.asList("Diya", "Matchbox"), R.raw.bell_tone),
                new PujaStep("Abhishek", "अभिषेक",
                        "Pour water or milk over the Shivling.",
                        "शिवलिंग पर जल या दूध चढ़ाएं।",
                        Arrays.asList("Water or milk"), 0),
                new PujaStep("Offer Bilva Patra", "बिल्व पत्र अर्पित करें",
                        "Place bilva (bel) leaves on the Shivling.",
                        "शिवलिंग पर बिल्व पत्र चढ़ाएं।",
                        Arrays.asList("Bilva leaves"), 0),
                new PujaStep("Chant Om Namah Shivaya", "ॐ नमः शिवाय जप",
                        "Chant 'Om Namah Shivaya' 11 times.",
                        "'ॐ नमः शिवाय' 11 बार जपें।",
                        null, R.raw.om_tone)
        ));

        shiva.put(TIER_STANDARD, Arrays.asList(
                new PujaStep("Dhyan", "ध्यान",
                        "Meditate on Lord Shiva in his peaceful form.",
                        "भगवान शिव के शांत रूप का ध्यान करें।",
                        null, R.raw.om_tone),
                new PujaStep("Avahan", "आवाहन",
                        "Ring bell and invoke Lord Shiva.",
                        "घंटी बजाएं और भगवान शिव का आवाहन करें।",
                        Arrays.asList("Bell"), R.raw.bell_tone),
                new PujaStep("Panchamrit Abhishek", "पंचामृत अभिषेक",
                        "Bathe the Shivling with milk, curd, honey, ghee, and sugar water.",
                        "शिवलिंग को दूध, दही, शहद, घी और शक्कर के पानी से स्नान कराएं।",
                        Arrays.asList("Milk", "Curd", "Honey", "Ghee", "Sugar"), 0),
                new PujaStep("Water Abhishek", "जल अभिषेक",
                        "Pour clean water to wash the Shivling after panchamrit.",
                        "पंचामृत के बाद शिवलिंग को स्वच्छ जल से धोएं।",
                        Arrays.asList("Water"), 0),
                new PujaStep("Chandan & Vibhuti", "चंदन और विभूति",
                        "Apply sandalwood paste and sacred ash on the Shivling.",
                        "शिवलिंग पर चंदन और भस्म लगाएं।",
                        Arrays.asList("Sandalwood paste", "Vibhuti"), 0),
                new PujaStep("Bilva Patra & Flowers", "बिल्व पत्र और पुष्प",
                        "Offer bilva leaves and white flowers.",
                        "बिल्व पत्र और सफेद फूल अर्पित करें।",
                        Arrays.asList("Bilva leaves", "White flowers"), 0),
                new PujaStep("Dhoop & Diya", "धूप और दीपक",
                        "Light incense and diya. Perform aarti motion.",
                        "अगरबत्ती और दीपक जलाएं। आरती करें।",
                        Arrays.asList("Incense", "Diya"), R.raw.bell_tone),
                new PujaStep("Naivedya", "नैवेद्य",
                        "Offer fruits and bhasma prasad.",
                        "फल और भस्म प्रसाद अर्पित करें।",
                        Arrays.asList("Fruits"), R.raw.chime_tone),
                new PujaStep("Shiva Aarti", "शिव आरती",
                        "Perform aarti — 'Om Jai Shiv Omkara'.",
                        "'ॐ जय शिव ओमकारा' आरती करें।",
                        Arrays.asList("Aarti plate"), R.raw.bell_tone),
                new PujaStep("Prarthana", "प्रार्थना",
                        "Pray to Lord Shiva for inner peace and strength.",
                        "भगवान शिव से शांति और शक्ति की प्रार्थना करें।",
                        null, R.raw.om_tone)
        ));

        shiva.put(TIER_FULL, new ArrayList<>(shiva.get(TIER_STANDARD)));
        shiva.get(TIER_FULL).add(new PujaStep("Rudra Mantra", "रुद्र मंत्र",
                "Chant the Maha Mrityunjaya mantra 108 times.",
                "महा मृत्युंजय मंत्र 108 बार जपें।",
                Arrays.asList("Rudraksha mala"), R.raw.om_tone));
        shiva.get(TIER_FULL).add(new PujaStep("Pradakshina", "प्रदक्षिणा",
                "Do not circumambulate the Shivling fully — go half way and return.",
                "शिवलिंग की आधी परिक्रमा करें और वापस आएं।",
                null, R.raw.chime_tone));

        ALL_FLOWS.put("Lord Shiva", shiva);

        // ===== LORD VISHNU =====
        Map<String, List<PujaStep>> vishnu = new LinkedHashMap<>();

        vishnu.put(TIER_QUICK, Arrays.asList(
                new PujaStep("Light Diya", "दीया जलाएं",
                        "Light a ghee diya near Lord Vishnu's image.",
                        "भगवान विष्णु की छवि के पास घी का दीया जलाएं।",
                        Arrays.asList("Ghee diya"), R.raw.bell_tone),
                new PujaStep("Offer Tulsi", "तुलसी अर्पित करें",
                        "Place tulsi leaves at the feet of Vishnu.",
                        "विष्णु जी के चरणों में तुलसी के पत्ते रखें।",
                        Arrays.asList("Tulsi leaves"), 0),
                new PujaStep("Chant Vishnu Mantra", "विष्णु मंत्र जप",
                        "Chant 'Om Namo Narayanaya' 11 times.",
                        "'ॐ नमो नारायणाय' 11 बार जपें।",
                        null, R.raw.om_tone),
                new PujaStep("Offer Prasad", "प्रसाद अर्पित करें",
                        "Offer any sattvic food as prasad.",
                        "कोई सात्विक भोजन प्रसाद के रूप में अर्पित करें।",
                        Arrays.asList("Prasad"), R.raw.chime_tone)
        ));

        vishnu.put(TIER_STANDARD, Arrays.asList(
                new PujaStep("Dhyan", "ध्यान",
                        "Meditate on the four-armed form of Lord Vishnu.",
                        "भगवान विष्णु के चतुर्भुज स्वरूप का ध्यान करें।",
                        null, R.raw.om_tone),
                new PujaStep("Avahan", "आवाहन",
                        "Ring bell and invoke Lord Vishnu.",
                        "घंटी बजाएं और भगवान विष्णु का आवाहन करें।",
                        Arrays.asList("Bell"), R.raw.bell_tone),
                new PujaStep("Snaan", "स्नान",
                        "Sprinkle Ganga water on the idol.",
                        "मूर्ति पर गंगाजल छिड़कें।",
                        Arrays.asList("Ganga water"), 0),
                new PujaStep("Vastra", "वस्त्र",
                        "Offer yellow cloth to Vishnu.",
                        "विष्णु जी को पीला वस्त्र अर्पित करें।",
                        Arrays.asList("Yellow cloth"), 0),
                new PujaStep("Chandan & Kumkum", "चंदन और कुमकुम",
                        "Apply sandalwood paste and tilak.",
                        "चंदन और तिलक लगाएं।",
                        Arrays.asList("Sandalwood paste", "Kumkum"), 0),
                new PujaStep("Tulsi & Flowers", "तुलसी और पुष्प",
                        "Offer tulsi leaves and yellow flowers.",
                        "तुलसी के पत्ते और पीले फूल अर्पित करें।",
                        Arrays.asList("Tulsi leaves", "Yellow flowers"), 0),
                new PujaStep("Dhoop & Diya", "धूप और दीपक",
                        "Light incense and ghee diya.",
                        "अगरबत्ती और घी का दीपक जलाएं।",
                        Arrays.asList("Incense", "Ghee diya"), R.raw.bell_tone),
                new PujaStep("Naivedya", "नैवेद्य",
                        "Offer fruits, tulsi water, and sweets.",
                        "फल, तुलसी जल और मिठाई अर्पित करें।",
                        Arrays.asList("Fruits", "Sweets"), R.raw.chime_tone),
                new PujaStep("Vishnu Aarti", "विष्णु आरती",
                        "Perform aarti — 'Om Jai Jagdish Hare'.",
                        "'ॐ जय जगदीश हरे' आरती करें।",
                        Arrays.asList("Aarti plate"), R.raw.bell_tone),
                new PujaStep("Prarthana", "प्रार्थना",
                        "Pray for protection and well-being.",
                        "रक्षा और कल्याण की प्रार्थना करें।",
                        null, R.raw.om_tone)
        ));

        vishnu.put(TIER_FULL, new ArrayList<>(vishnu.get(TIER_STANDARD)));
        vishnu.get(TIER_FULL).add(new PujaStep("Vishnu Sahasranama", "विष्णु सहस्रनाम",
                "Recite Vishnu Sahasranama for divine grace.",
                "दिव्य कृपा के लिए विष्णु सहस्रनाम का पाठ करें।",
                null, R.raw.om_tone));

        ALL_FLOWS.put("Lord Vishnu", vishnu);

        // ===== LORD HANUMAN =====
        Map<String, List<PujaStep>> hanuman = new LinkedHashMap<>();

        hanuman.put(TIER_QUICK, Arrays.asList(
                new PujaStep("Light Diya", "दीया जलाएं",
                        "Light a mustard oil diya near Hanuman ji.",
                        "हनुमान जी के पास सरसों के तेल का दीया जलाएं।",
                        Arrays.asList("Mustard oil diya"), R.raw.bell_tone),
                new PujaStep("Offer Sindoor", "सिंदूर अर्पित करें",
                        "Apply sindoor to Hanuman ji's image.",
                        "हनुमान जी की मूर्ति पर सिंदूर लगाएं।",
                        Arrays.asList("Sindoor"), 0),
                new PujaStep("Recite Hanuman Chalisa", "हनुमान चालीसा पाठ",
                        "Recite the Hanuman Chalisa with devotion.",
                        "भक्तिपूर्वक हनुमान चालीसा का पाठ करें।",
                        null, R.raw.om_tone),
                new PujaStep("Offer Prasad", "प्रसाद अर्पित करें",
                        "Offer boondi laddoo or jaggery.",
                        "बूंदी के लड्डू या गुड़ अर्पित करें।",
                        Arrays.asList("Boondi laddoo"), R.raw.chime_tone)
        ));

        hanuman.put(TIER_STANDARD, Arrays.asList(
                new PujaStep("Dhyan", "ध्यान",
                        "Meditate on Hanuman ji's mighty form.",
                        "हनुमान जी के वीर रूप का ध्यान करें।",
                        null, R.raw.om_tone),
                new PujaStep("Avahan", "आवाहन",
                        "Ring bell and invoke Hanuman ji.",
                        "घंटी बजाएं और हनुमान जी का आवाहन करें।",
                        Arrays.asList("Bell"), R.raw.bell_tone),
                new PujaStep("Abhishek", "अभिषेक",
                        "Sprinkle water on the idol.",
                        "मूर्ति पर जल छिड़कें।",
                        Arrays.asList("Water"), 0),
                new PujaStep("Sindoor & Chandan", "सिंदूर और चंदन",
                        "Apply sindoor and sandalwood paste.",
                        "सिंदूर और चंदन लगाएं।",
                        Arrays.asList("Sindoor", "Sandalwood paste"), 0),
                new PujaStep("Offer Flowers", "पुष्प अर्पित करें",
                        "Offer red flowers and marigold garland.",
                        "लाल फूल और गेंदे की माला अर्पित करें।",
                        Arrays.asList("Red flowers", "Marigold garland"), 0),
                new PujaStep("Dhoop & Diya", "धूप और दीपक",
                        "Light incense and mustard oil diya.",
                        "अगरबत्ती और सरसों के तेल का दीपक जलाएं।",
                        Arrays.asList("Incense", "Mustard oil diya"), R.raw.bell_tone),
                new PujaStep("Hanuman Chalisa", "हनुमान चालीसा",
                        "Recite the complete Hanuman Chalisa.",
                        "संपूर्ण हनुमान चालीसा का पाठ करें।",
                        null, R.raw.om_tone),
                new PujaStep("Naivedya", "नैवेद्य",
                        "Offer boondi laddoo, banana, and jaggery.",
                        "बूंदी के लड्डू, केला और गुड़ अर्पित करें।",
                        Arrays.asList("Boondi laddoo", "Banana", "Jaggery"), R.raw.chime_tone),
                new PujaStep("Hanuman Aarti", "हनुमान आरती",
                        "Perform aarti — 'Aarti Kije Hanuman Lala Ki'.",
                        "'आरती कीजे हनुमान लला की' आरती करें।",
                        Arrays.asList("Aarti plate"), R.raw.bell_tone),
                new PujaStep("Prarthana", "प्रार्थना",
                        "Pray for courage and strength.",
                        "साहस और शक्ति की प्रार्थना करें।",
                        null, R.raw.om_tone)
        ));

        hanuman.put(TIER_FULL, new ArrayList<>(hanuman.get(TIER_STANDARD)));
        hanuman.get(TIER_FULL).add(7, new PujaStep("Bajrang Baan", "बजरंग बाण",
                "Recite Bajrang Baan for protection from evil.",
                "बुरी शक्तियों से रक्षा के लिए बजरंग बाण का पाठ करें।",
                null, R.raw.om_tone));
        hanuman.get(TIER_FULL).add(new PujaStep("Pradakshina", "प्रदक्षिणा",
                "Circumambulate the idol 3 times.",
                "मूर्ति की 3 बार परिक्रमा करें।",
                null, R.raw.chime_tone));

        ALL_FLOWS.put("Lord Hanuman", hanuman);

        // ===== GODDESS LAKSHMI =====
        Map<String, List<PujaStep>> lakshmi = new LinkedHashMap<>();

        lakshmi.put(TIER_QUICK, Arrays.asList(
                new PujaStep("Light Diya", "दीया जलाएं",
                        "Light a ghee diya and place near Lakshmi ji.",
                        "घी का दीया जलाकर लक्ष्मी जी के पास रखें।",
                        Arrays.asList("Ghee diya"), R.raw.bell_tone),
                new PujaStep("Offer Lotus/Flowers", "कमल/पुष्प अर्पित करें",
                        "Offer lotus or white flowers.",
                        "कमल या सफेद फूल अर्पित करें।",
                        Arrays.asList("Lotus or white flowers"), 0),
                new PujaStep("Chant Lakshmi Mantra", "लक्ष्मी मंत्र जप",
                        "Chant 'Om Shreem Mahalakshmiyei Namaha' 11 times.",
                        "'ॐ श्रीं महालक्ष्म्यै नमः' 11 बार जपें।",
                        null, R.raw.om_tone),
                new PujaStep("Offer Prasad", "प्रसाद अर्पित करें",
                        "Offer kheer or any milk-based sweet.",
                        "खीर या कोई दूध से बनी मिठाई अर्पित करें।",
                        Arrays.asList("Kheer"), R.raw.chime_tone)
        ));

        lakshmi.put(TIER_STANDARD, Arrays.asList(
                new PujaStep("Dhyan", "ध्यान",
                        "Meditate on Goddess Lakshmi seated on lotus.",
                        "कमल पर विराजमान लक्ष्मी जी का ध्यान करें।",
                        null, R.raw.om_tone),
                new PujaStep("Avahan", "आवाहन",
                        "Ring bell and invoke Goddess Lakshmi.",
                        "घंटी बजाएं और लक्ष्मी जी का आवाहन करें।",
                        Arrays.asList("Bell"), R.raw.bell_tone),
                new PujaStep("Snaan", "स्नान",
                        "Sprinkle Ganga water on the idol.",
                        "मूर्ति पर गंगाजल छिड़कें।",
                        Arrays.asList("Ganga water"), 0),
                new PujaStep("Vastra", "वस्त्र",
                        "Offer red or pink cloth.",
                        "लाल या गुलाबी वस्त्र अर्पित करें।",
                        Arrays.asList("Red cloth"), 0),
                new PujaStep("Shringar", "श्रृंगार",
                        "Apply kumkum, haldi, and offer bangles.",
                        "कुमकुम, हल्दी लगाएं और चूड़ियां अर्पित करें।",
                        Arrays.asList("Kumkum", "Haldi", "Bangles"), 0),
                new PujaStep("Lotus & Flowers", "कमल और पुष्प",
                        "Offer lotus flowers and marigold garland.",
                        "कमल के फूल और गेंदे की माला अर्पित करें।",
                        Arrays.asList("Lotus", "Marigold garland"), 0),
                new PujaStep("Dhoop & Diya", "धूप और दीपक",
                        "Light incense and ghee diya.",
                        "अगरबत्ती और घी का दीपक जलाएं।",
                        Arrays.asList("Incense", "Ghee diya"), R.raw.bell_tone),
                new PujaStep("Naivedya", "नैवेद्य",
                        "Offer kheer, fruits, and dry fruits.",
                        "खीर, फल और मेवे अर्पित करें।",
                        Arrays.asList("Kheer", "Fruits", "Dry fruits"), R.raw.chime_tone),
                new PujaStep("Lakshmi Aarti", "लक्ष्मी आरती",
                        "Perform aarti — 'Om Jai Lakshmi Mata'.",
                        "'ॐ जय लक्ष्मी माता' आरती करें।",
                        Arrays.asList("Aarti plate"), R.raw.bell_tone),
                new PujaStep("Prarthana", "प्रार्थना",
                        "Pray for prosperity and well-being.",
                        "समृद्धि और कल्याण की प्रार्थना करें।",
                        null, R.raw.om_tone)
        ));

        lakshmi.put(TIER_FULL, new ArrayList<>(lakshmi.get(TIER_STANDARD)));
        lakshmi.get(TIER_FULL).add(new PujaStep("Lakshmi Stotram", "लक्ष्मी स्तोत्र",
                "Recite Sri Lakshmi Stotram.",
                "श्री लक्ष्मी स्तोत्र का पाठ करें।",
                null, R.raw.om_tone));

        ALL_FLOWS.put("Goddess Lakshmi", lakshmi);

        // ===== GODDESS DURGA =====
        Map<String, List<PujaStep>> durga = new LinkedHashMap<>();

        durga.put(TIER_QUICK, Arrays.asList(
                new PujaStep("Light Diya", "दीया जलाएं",
                        "Light a ghee diya near Durga Maa's image.",
                        "दुर्गा माँ की छवि के पास घी का दीया जलाएं।",
                        Arrays.asList("Ghee diya"), R.raw.bell_tone),
                new PujaStep("Offer Red Flowers", "लाल पुष्प अर्पित करें",
                        "Offer red hibiscus or red roses.",
                        "लाल गुड़हल या लाल गुलाब अर्पित करें।",
                        Arrays.asList("Red flowers"), 0),
                new PujaStep("Chant Durga Mantra", "दुर्गा मंत्र जप",
                        "Chant 'Om Dum Durgayei Namaha' 11 times.",
                        "'ॐ दुं दुर्गायै नमः' 11 बार जपें।",
                        null, R.raw.om_tone),
                new PujaStep("Offer Prasad", "प्रसाद अर्पित करें",
                        "Offer halwa or coconut.",
                        "हलवा या नारियल अर्पित करें।",
                        Arrays.asList("Halwa or coconut"), R.raw.chime_tone)
        ));

        durga.put(TIER_STANDARD, Arrays.asList(
                new PujaStep("Dhyan", "ध्यान",
                        "Meditate on Durga Maa's powerful form.",
                        "दुर्गा माँ के शक्तिशाली रूप का ध्यान करें।",
                        null, R.raw.om_tone),
                new PujaStep("Avahan", "आवाहन",
                        "Ring bell and invoke Durga Maa.",
                        "घंटी बजाएं और दुर्गा माँ का आवाहन करें।",
                        Arrays.asList("Bell"), R.raw.bell_tone),
                new PujaStep("Snaan", "स्नान",
                        "Sprinkle holy water on the idol.",
                        "मूर्ति पर पवित्र जल छिड़कें।",
                        Arrays.asList("Holy water"), 0),
                new PujaStep("Vastra & Shringar", "वस्त्र और श्रृंगार",
                        "Offer red chunari and kumkum.",
                        "लाल चुनरी और कुमकुम अर्पित करें।",
                        Arrays.asList("Red chunari", "Kumkum"), 0),
                new PujaStep("Red Flowers", "लाल पुष्प",
                        "Offer red flowers and garland.",
                        "लाल फूल और माला अर्पित करें।",
                        Arrays.asList("Red flowers", "Garland"), 0),
                new PujaStep("Dhoop & Diya", "धूप और दीपक",
                        "Light incense and akhand jyoti.",
                        "अगरबत्ती और अखंड ज्योति जलाएं।",
                        Arrays.asList("Incense", "Diya"), R.raw.bell_tone),
                new PujaStep("Naivedya", "नैवेद्य",
                        "Offer halwa, puri, and fruits.",
                        "हलवा, पूरी और फल अर्पित करें।",
                        Arrays.asList("Halwa", "Puri", "Fruits"), R.raw.chime_tone),
                new PujaStep("Durga Aarti", "दुर्गा आरती",
                        "Perform Durga aarti — 'Jai Ambe Gauri'.",
                        "'जय अम्बे गौरी' आरती करें।",
                        Arrays.asList("Aarti plate"), R.raw.bell_tone),
                new PujaStep("Prarthana", "प्रार्थना",
                        "Pray for strength and protection.",
                        "शक्ति और रक्षा की प्रार्थना करें।",
                        null, R.raw.om_tone)
        ));

        durga.put(TIER_FULL, new ArrayList<>(durga.get(TIER_STANDARD)));
        durga.get(TIER_FULL).add(7, new PujaStep("Durga Saptashati", "दुर्गा सप्तशती",
                "Recite select chapters from Durga Saptashati.",
                "दुर्गा सप्तशती के चुनिंदा अध्यायों का पाठ करें।",
                null, R.raw.om_tone));

        ALL_FLOWS.put("Goddess Durga", durga);

        // ===== LORD KRISHNA =====
        Map<String, List<PujaStep>> krishna = new LinkedHashMap<>();

        krishna.put(TIER_QUICK, Arrays.asList(
                new PujaStep("Light Diya", "दीया जलाएं",
                        "Light a ghee diya near Lord Krishna's image.",
                        "भगवान कृष्ण की छवि के पास घी का दीया जलाएं।",
                        Arrays.asList("Ghee diya"), R.raw.bell_tone),
                new PujaStep("Offer Tulsi & Butter", "तुलसी और माखन अर्पित करें",
                        "Offer tulsi leaves and a small portion of butter.",
                        "तुलसी के पत्ते और थोड़ा माखन अर्पित करें।",
                        Arrays.asList("Tulsi leaves", "Butter"), 0),
                new PujaStep("Chant Krishna Mantra", "कृष्ण मंत्र जप",
                        "Chant 'Hare Krishna Hare Krishna' 11 times.",
                        "'हरे कृष्ण हरे कृष्ण' 11 बार जपें।",
                        null, R.raw.om_tone),
                new PujaStep("Offer Prasad", "प्रसाद अर्पित करें",
                        "Offer peda or any milk sweet.",
                        "पेड़ा या कोई दूध की मिठाई अर्पित करें।",
                        Arrays.asList("Peda"), R.raw.chime_tone)
        ));

        krishna.put(TIER_STANDARD, Arrays.asList(
                new PujaStep("Dhyan", "ध्यान",
                        "Meditate on Lord Krishna playing the flute.",
                        "बांसुरी बजाते भगवान कृष्ण का ध्यान करें।",
                        null, R.raw.om_tone),
                new PujaStep("Avahan", "आवाहन",
                        "Ring bell and invoke Lord Krishna.",
                        "घंटी बजाएं और भगवान कृष्ण का आवाहन करें।",
                        Arrays.asList("Bell"), R.raw.bell_tone),
                new PujaStep("Snaan", "स्नान",
                        "Bathe the idol with Panchamrit.",
                        "पंचामृत से मूर्ति को स्नान कराएं।",
                        Arrays.asList("Panchamrit"), 0),
                new PujaStep("Vastra", "वस्त्र",
                        "Offer yellow or peacock-colored cloth.",
                        "पीला या मोरपंखी रंग का वस्त्र अर्पित करें।",
                        Arrays.asList("Yellow cloth"), 0),
                new PujaStep("Tulsi & Flowers", "तुलसी और पुष्प",
                        "Offer tulsi leaves, marigold, and peacock feather.",
                        "तुलसी, गेंदे के फूल और मोरपंख अर्पित करें।",
                        Arrays.asList("Tulsi", "Marigold", "Peacock feather"), 0),
                new PujaStep("Dhoop & Diya", "धूप और दीपक",
                        "Light incense and ghee diya.",
                        "अगरबत्ती और घी का दीपक जलाएं।",
                        Arrays.asList("Incense", "Ghee diya"), R.raw.bell_tone),
                new PujaStep("Naivedya", "नैवेद्य",
                        "Offer butter, mishri, peda, and fruits.",
                        "माखन, मिश्री, पेड़ा और फल अर्पित करें।",
                        Arrays.asList("Butter", "Mishri", "Peda", "Fruits"), R.raw.chime_tone),
                new PujaStep("Krishna Aarti", "कृष्ण आरती",
                        "Perform aarti — 'Aarti Kunj Bihari Ki'.",
                        "'आरती कुंज बिहारी की' आरती करें।",
                        Arrays.asList("Aarti plate"), R.raw.bell_tone),
                new PujaStep("Prarthana", "प्रार्थना",
                        "Pray for divine love and guidance.",
                        "दिव्य प्रेम और मार्गदर्शन की प्रार्थना करें।",
                        null, R.raw.om_tone)
        ));

        krishna.put(TIER_FULL, new ArrayList<>(krishna.get(TIER_STANDARD)));
        krishna.get(TIER_FULL).add(new PujaStep("Gita Shloka", "गीता श्लोक",
                "Recite select shlokas from Bhagavad Gita Chapter 12.",
                "भगवद्गीता अध्याय 12 के चुनिंदा श्लोकों का पाठ करें।",
                null, R.raw.om_tone));

        ALL_FLOWS.put("Lord Krishna", krishna);
    }

    public static List<String> getDeityNames() {
        return new ArrayList<>(ALL_FLOWS.keySet());
    }

    public static List<PujaStep> getSteps(String deityName, String tier) {
        Map<String, List<PujaStep>> deityFlows = ALL_FLOWS.get(deityName);
        if (deityFlows == null) return Collections.emptyList();
        List<PujaStep> steps = deityFlows.get(tier);
        return steps != null ? steps : Collections.emptyList();
    }

    public static String getTierDisplayName(String tier) {
        switch (tier) {
            case TIER_QUICK: return "Quick Sandhya (5 min)";
            case TIER_STANDARD: return "Standard Puja (15 min)";
            case TIER_FULL: return "Full Puja (30+ min)";
            default: return tier;
        }
    }

    public static String getTierDescription(String tier) {
        switch (tier) {
            case TIER_QUICK: return "A brief daily worship with essential steps — diya, offering, and mantra.";
            case TIER_STANDARD: return "Complete worship with invocation, abhishek, offerings, and aarti.";
            case TIER_FULL: return "Elaborate puja with extended mantras, stotras, and full rituals.";
            default: return "";
        }
    }
}
