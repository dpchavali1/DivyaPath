package com.divyapath.app.utils;

import com.divyapath.app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SevaData {

    public static final String CATEGORY_DAAN = "Daan";
    public static final String CATEGORY_PAROPKAR = "Paropkar";
    public static final String CATEGORY_BHAKTI = "Bhakti";
    public static final String CATEGORY_PARYAVARAN = "Paryavaran";

    public static class SevaItem {
        public final String title;
        public final String titleHindi;
        public final String category;
        public final String description;
        public final int iconRes;

        public SevaItem(String title, String titleHindi, String category, String description, int iconRes) {
            this.title = title;
            this.titleHindi = titleHindi;
            this.category = category;
            this.description = description;
            this.iconRes = iconRes;
        }
    }

    private static final List<SevaItem> ALL_SEVAS = new ArrayList<>();

    static {
        // DAAN (Charity) - 13 items
        ALL_SEVAS.add(new SevaItem("Feed a stray animal", "एक आवारा जानवर को खाना खिलाएं", CATEGORY_DAAN,
                "Leave food and water for street dogs, cats, or birds near your home.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Donate old clothes", "पुराने कपड़े दान करें", CATEGORY_DAAN,
                "Give gently used clothes to someone in need or a donation center.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Feed the hungry", "भूखे को भोजन कराएं", CATEGORY_DAAN,
                "Offer a meal to someone who cannot afford one today.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Donate to a temple", "मंदिर में दान करें", CATEGORY_DAAN,
                "Make a small offering to your local temple's anna-daan fund.", R.drawable.ic_kalash));
        ALL_SEVAS.add(new SevaItem("Give water to birds", "पक्षियों को पानी दें", CATEGORY_DAAN,
                "Place a bowl of water on your balcony or terrace for birds.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Donate books", "किताबें दान करें", CATEGORY_DAAN,
                "Give books to a child who cannot afford them.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Feed ants with sugar", "चींटियों को शक्कर खिलाएं", CATEGORY_DAAN,
                "Place some sugar or flour near an ant colony as an act of compassion.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Donate medicines", "दवाइयां दान करें", CATEGORY_DAAN,
                "Help someone who cannot afford their medicines.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Offer food at temple", "मंदिर में प्रसाद चढ़ाएं", CATEGORY_DAAN,
                "Prepare and offer prasad at your local temple for devotees.", R.drawable.ic_kalash));
        ALL_SEVAS.add(new SevaItem("Donate blankets", "कंबल दान करें", CATEGORY_DAAN,
                "Give a warm blanket to someone sleeping on the streets.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Feed cows", "गाय को चारा खिलाएं", CATEGORY_DAAN,
                "Offer fresh fodder or rotis to a cow at a gaushala.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Give grains to birds", "पक्षियों को दाना डालें", CATEGORY_DAAN,
                "Scatter grains on your rooftop or garden for birds.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Donate stationery", "स्टेशनरी दान करें", CATEGORY_DAAN,
                "Give pens, pencils, and notebooks to underprivileged children.", R.drawable.ic_om_symbol));

        // PAROPKAR (Helping Others) - 13 items
        ALL_SEVAS.add(new SevaItem("Help an elderly person", "बुजुर्गों की सेवा करें", CATEGORY_PAROPKAR,
                "Assist an elderly neighbor with groceries, chores, or just conversation.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Teach someone", "किसी को पढ़ाएं", CATEGORY_PAROPKAR,
                "Spend 30 minutes teaching a skill or subject to someone.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Visit the sick", "बीमार से मिलें", CATEGORY_PAROPKAR,
                "Visit someone who is unwell and offer comfort and support.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Smile at strangers", "अजनबियों को मुस्कुराहट दें", CATEGORY_PAROPKAR,
                "Greet at least 5 strangers with a genuine smile today.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Help with directions", "रास्ता बताएं", CATEGORY_PAROPKAR,
                "Help someone who looks lost find their way.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Listen with compassion", "सहानुभूति से सुनें", CATEGORY_PAROPKAR,
                "Give someone your full, undivided attention when they need to talk.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Forgive someone", "किसी को क्षमा करें", CATEGORY_PAROPKAR,
                "Let go of a grudge or resentment you've been holding.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Compliment someone", "किसी की तारीफ करें", CATEGORY_PAROPKAR,
                "Give a sincere, heartfelt compliment to brighten someone's day.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Help a colleague", "सहकर्मी की मदद करें", CATEGORY_PAROPKAR,
                "Offer to help a colleague with their work without being asked.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Call your parents", "माता-पिता को फोन करें", CATEGORY_PAROPKAR,
                "Spend time talking to your parents or grandparents today.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Write a thank you note", "धन्यवाद पत्र लिखें", CATEGORY_PAROPKAR,
                "Express gratitude to someone who has helped you recently.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Let someone go first", "पहले जाने दें", CATEGORY_PAROPKAR,
                "Practice patience by letting someone ahead of you in line.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Share your lunch", "अपना खाना बांटें", CATEGORY_PAROPKAR,
                "Share a portion of your meal with someone who needs it.", R.drawable.ic_om_symbol));

        // BHAKTI (Devotion) - 13 items
        ALL_SEVAS.add(new SevaItem("Chant 108 times", "108 बार जाप करें", CATEGORY_BHAKTI,
                "Chant your ishta-devta's name or mantra 108 times with a mala.", R.drawable.ic_mantra));
        ALL_SEVAS.add(new SevaItem("Read a sacred text", "पवित्र ग्रंथ पढ़ें", CATEGORY_BHAKTI,
                "Read one chapter from the Gita, Ramayana, or any scripture.", R.drawable.ic_chalisa));
        ALL_SEVAS.add(new SevaItem("Light a diya at sunset", "संध्या दीप जलाएं", CATEGORY_BHAKTI,
                "Light a ghee diya at your home mandir during sunset.", R.drawable.ic_aarti));
        ALL_SEVAS.add(new SevaItem("Sing a bhajan", "भजन गाएं", CATEGORY_BHAKTI,
                "Sing or listen to a devotional bhajan with full heart.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Meditate for 15 min", "15 मिनट ध्यान करें", CATEGORY_BHAKTI,
                "Sit in silence and meditate on the divine for 15 minutes.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Clean your mandir", "अपना मंदिर साफ करें", CATEGORY_BHAKTI,
                "Clean and arrange your home temple with love and devotion.", R.drawable.ic_kalash));
        ALL_SEVAS.add(new SevaItem("Offer flowers to God", "भगवान को फूल चढ़ाएं", CATEGORY_BHAKTI,
                "Offer fresh flowers at your home mandir or local temple.", R.drawable.ic_lotus));
        ALL_SEVAS.add(new SevaItem("Practice silence", "मौन व्रत रखें", CATEGORY_BHAKTI,
                "Observe silence for one hour and turn your attention inward.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Write God's name", "भगवान का नाम लिखें", CATEGORY_BHAKTI,
                "Write Ram or your ishta-devta's name 108 times in a notebook.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Do evening aarti", "संध्या आरती करें", CATEGORY_BHAKTI,
                "Perform a full evening aarti at your home mandir.", R.drawable.ic_aarti));
        ALL_SEVAS.add(new SevaItem("Listen to a katha", "कथा सुनें", CATEGORY_BHAKTI,
                "Listen to a spiritual discourse or katha for 20 minutes.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Recite a stotra", "स्तोत्र पढ़ें", CATEGORY_BHAKTI,
                "Recite a stotra or stuti dedicated to your deity.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Practice gratitude", "कृतज्ञता का अभ्यास करें", CATEGORY_BHAKTI,
                "Thank God for 5 specific blessings in your life today.", R.drawable.ic_om_symbol));

        // PARYAVARAN (Environment) - 13 items
        ALL_SEVAS.add(new SevaItem("Plant a tulsi", "तुलसी लगाएं", CATEGORY_PARYAVARAN,
                "Plant a tulsi (holy basil) sapling in a pot or garden.", R.drawable.ic_lotus));
        ALL_SEVAS.add(new SevaItem("Save water", "पानी बचाएं", CATEGORY_PARYAVARAN,
                "Be mindful of water usage — fix a leak or reduce shower time.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Pick up litter", "कचरा उठाएं", CATEGORY_PARYAVARAN,
                "Pick up at least 5 pieces of litter from your surroundings.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Use less plastic", "प्लास्टिक कम करें", CATEGORY_PARYAVARAN,
                "Avoid using single-use plastic today — carry your own bag.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Walk instead of driving", "गाड़ी छोड़ें, पैदल चलें", CATEGORY_PARYAVARAN,
                "Choose to walk or cycle for short distances today.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Water a plant", "पौधे को पानी दें", CATEGORY_PARYAVARAN,
                "Water your plants or a tree near your home with care.", R.drawable.ic_lotus));
        ALL_SEVAS.add(new SevaItem("Turn off unused lights", "अनावश्यक बत्ती बंद करें", CATEGORY_PARYAVARAN,
                "Be mindful of electricity — turn off lights when leaving a room.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Clean a public space", "सार्वजनिक स्थान साफ करें", CATEGORY_PARYAVARAN,
                "Spend 15 minutes cleaning an area around a temple or park.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Plant a tree", "पेड़ लगाएं", CATEGORY_PARYAVARAN,
                "Plant a tree sapling and nurture it as seva to Mother Earth.", R.drawable.ic_lotus));
        ALL_SEVAS.add(new SevaItem("Compost kitchen waste", "रसोई कचरे की खाद बनाएं", CATEGORY_PARYAVARAN,
                "Start composting your kitchen waste instead of throwing it away.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Reduce food waste", "भोजन बर्बाद न करें", CATEGORY_PARYAVARAN,
                "Cook only what you need and avoid wasting food today.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Use public transport", "सार्वजनिक वाहन का उपयोग करें", CATEGORY_PARYAVARAN,
                "Choose public transport over personal vehicle today.", R.drawable.ic_om_symbol));
        ALL_SEVAS.add(new SevaItem("Clean a water body", "जल स्रोत साफ करें", CATEGORY_PARYAVARAN,
                "Help clean a pond, river ghat, or water source near you.", R.drawable.ic_om_symbol));
    }

    public static SevaItem getTodaysSeva() {
        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return ALL_SEVAS.get(dayOfYear % ALL_SEVAS.size());
    }

    public static List<SevaItem> getAllSevas() {
        return new ArrayList<>(ALL_SEVAS);
    }

    public static List<SevaItem> getSevasByCategory(String category) {
        List<SevaItem> filtered = new ArrayList<>();
        for (SevaItem item : ALL_SEVAS) {
            if (item.category.equals(category)) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    public static int getTotalCount() {
        return ALL_SEVAS.size();
    }
}
