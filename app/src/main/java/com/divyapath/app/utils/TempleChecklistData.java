package com.divyapath.app.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TempleChecklistData {

    public static class TempleChecklist {
        public final List<String> itemsToCarry;
        public final String dressCode;
        public final String bestTimings;
        public final List<String> nearbyTemples;

        public TempleChecklist(List<String> itemsToCarry, String dressCode,
                               String bestTimings, List<String> nearbyTemples) {
            this.itemsToCarry = itemsToCarry;
            this.dressCode = dressCode;
            this.bestTimings = bestTimings;
            this.nearbyTemples = nearbyTemples;
        }
    }

    private static final Map<String, TempleChecklist> CHECKLISTS = new HashMap<>();

    static {
        CHECKLISTS.put("Kashi Vishwanath", new TempleChecklist(
                Arrays.asList("Flowers", "Bel Patra", "Milk", "Water for Abhishek", "Dhoop/Agarbatti", "Prasad"),
                "Traditional attire recommended. Men: Dhoti/Kurta. Women: Saree/Salwar.",
                "Early morning (4-6 AM) for Mangla Aarti. Evening for Ganga Aarti.",
                Arrays.asList("Annapurna Temple", "Kaal Bhairav", "Sankat Mochan", "Tulsi Manas Mandir")
        ));

        CHECKLISTS.put("Tirupati Balaji", new TempleChecklist(
                Arrays.asList("ID Proof", "Laddu Booking Receipt", "Small bag", "Prasad items"),
                "Traditional attire mandatory. No jeans/shorts. Men: Dhoti/Shirt. Women: Saree/Churidar.",
                "Book darshan online. VIP darshan 2-3 hours. Free darshan 6-10 hours.",
                Arrays.asList("Padmavathi Temple", "Govindaraja Swamy", "Sri Kapileswara Swamy", "Srinivasa Mangapuram")
        ));

        CHECKLISTS.put("Somnath", new TempleChecklist(
                Arrays.asList("Flowers", "Coconut", "Prasad", "Camera (outside only)"),
                "Decent traditional attire. Remove footwear before entering.",
                "Early morning for Sunrise view. Evening for Light and Sound show.",
                Arrays.asList("Bhalka Tirth", "Triveni Sangam", "Panch Pandav Gufa", "Junagadh Temples")
        ));

        CHECKLISTS.put("Jagannath Puri", new TempleChecklist(
                Arrays.asList("Flowers", "Coconut", "Offering items", "ID Proof"),
                "Traditional attire required. Non-Hindus not permitted inside.",
                "Morning 5 AM for Mangala Aarti. Afternoon for Madhyahna Dhupa.",
                Arrays.asList("Gundicha Temple", "Lingaraj Temple", "Konark Sun Temple", "Chilika Lake")
        ));

        CHECKLISTS.put("Kedarnath", new TempleChecklist(
                Arrays.asList("Warm clothes", "Rain gear", "Trekking shoes", "Water bottle", "Energy bars", "First aid kit", "ID Proof"),
                "Warm comfortable clothes for the trek. Traditional attire for darshan.",
                "Open May-November. Start trek early morning (4-5 AM). Temple opens 4 AM.",
                Arrays.asList("Badrinath", "Tungnath", "Rudranath", "Madhyamaheshwar")
        ));

        CHECKLISTS.put("Badrinath", new TempleChecklist(
                Arrays.asList("Warm clothes", "ID Proof", "Prasad items", "Water bottle", "Medicines"),
                "Warm traditional attire. Layer up as weather changes quickly.",
                "Open May-November. Morning 4:30 AM Abhishek. Best darshan 7-9 AM.",
                Arrays.asList("Mana Village", "Vasudhara Falls", "Tapt Kund", "Kedarnath")
        ));

        CHECKLISTS.put("Dwarka", new TempleChecklist(
                Arrays.asList("Flowers", "Coconut", "Prasad", "Comfortable footwear"),
                "Traditional attire. Remove footwear at the entrance.",
                "Morning 6-7 AM or evening 7-8 PM for best darshan.",
                Arrays.asList("Nageshwar Jyotirlinga", "Bet Dwarka", "Rukmini Temple", "Gopi Talav")
        ));

        CHECKLISTS.put("Rameswaram", new TempleChecklist(
                Arrays.asList("Extra clothes (for holy bath)", "Towel", "Coconut", "Flowers", "ID Proof"),
                "Traditional attire. Carry change of clothes for 22 Theerthams bath.",
                "Early morning 5 AM for Sparsha Darshan. Complete Theerthams before darshan.",
                Arrays.asList("Gandhamadhana Parvatam", "Pamban Bridge", "Dhanushkodi", "Villundi Tirtham")
        ));

        CHECKLISTS.put("Vaishno Devi", new TempleChecklist(
                Arrays.asList("Trekking shoes", "Warm clothes", "Water bottle", "Yatra Parchi", "ID Proof", "Torch", "Rain gear"),
                "Comfortable clothes for 13 km trek. Traditional attire for darshan.",
                "Start trek from Katra by midnight for early morning darshan. Book pony/helicopter in advance.",
                Arrays.asList("Bhairavnath Temple", "Ardhkuwari", "Shiv Khori", "Patnitop")
        ));

        CHECKLISTS.put("Meenakshi Temple", new TempleChecklist(
                Arrays.asList("Flowers", "Coconut", "Camphor", "Offering items"),
                "Traditional attire. Men: Dhoti/Veshti. Women: Saree.",
                "Morning 5 AM or evening 5 PM. Hall of Thousand Pillars visit recommended.",
                Arrays.asList("Thiruparankundram Murugan", "Alagar Kovil", "Gandhi Memorial", "Koodal Azhagar")
        ));

        CHECKLISTS.put("Golden Temple", new TempleChecklist(
                Arrays.asList("Head covering (mandatory)", "Comfortable shoes (footwear room available)", "Donation (optional)"),
                "Cover head mandatory. Any decent attire. Remove shoes before entering.",
                "Early morning 3-5 AM for Prakash ceremony. Evening for Rehras Sahib.",
                Arrays.asList("Jallianwala Bagh", "Wagah Border", "Durgiana Temple", "Partition Museum")
        ));

        CHECKLISTS.put("Siddhivinayak", new TempleChecklist(
                Arrays.asList("Flowers", "Modak", "Coconut", "Prasad"),
                "Decent attire. No restrictions on traditional/modern.",
                "Tuesday morning for best darshan. Book online pass to skip queue.",
                Arrays.asList("Mahalaxmi Temple", "Haji Ali", "Mount Mary Church", "Babulnath Temple")
        ));
    }

    public static TempleChecklist getChecklist(String templeName) {
        // Try exact match first
        TempleChecklist checklist = CHECKLISTS.get(templeName);
        if (checklist != null) return checklist;

        // Try partial match
        for (Map.Entry<String, TempleChecklist> entry : CHECKLISTS.entrySet()) {
            if (templeName.contains(entry.getKey()) || entry.getKey().contains(templeName)) {
                return entry.getValue();
            }
        }

        // Default checklist
        return new TempleChecklist(
                Arrays.asList("Flowers", "Coconut", "Prasad", "ID Proof", "Comfortable footwear"),
                "Traditional attire recommended. Remove footwear before entering.",
                "Morning or evening hours recommended for darshan.",
                Arrays.asList()
        );
    }

    public static boolean hasChecklist(String templeName) {
        if (CHECKLISTS.containsKey(templeName)) return true;
        for (String key : CHECKLISTS.keySet()) {
            if (templeName.contains(key) || key.contains(templeName)) return true;
        }
        return false;
    }
}
