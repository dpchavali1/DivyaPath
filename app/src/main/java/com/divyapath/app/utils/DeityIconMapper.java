package com.divyapath.app.utils;

import com.divyapath.app.R;

/**
 * Utility class that maps deity IDs and imageUrl strings to their corresponding
 * vector drawable resources. Used across all adapters to show deity-specific icons
 * instead of the generic Om symbol.
 */
public final class DeityIconMapper {

    private DeityIconMapper() {
        // Utility class
    }

    /**
     * Maps a deity's database ID to the corresponding drawable resource.
     * IDs match those assigned in DatabaseSeeder.
     *
     * @param deityId the deity's database primary key (1-11)
     * @return drawable resource ID, or ic_om_symbol as fallback
     */
    public static int getIconForDeityId(int deityId) {
        switch (deityId) {
            case 1: return R.drawable.ic_deity_shiva;
            case 2: return R.drawable.ic_deity_hanuman;
            case 3: return R.drawable.ic_deity_ganesha;
            case 4: return R.drawable.ic_deity_vishnu;
            case 5: return R.drawable.ic_deity_lakshmi;
            case 6: return R.drawable.ic_deity_shani;
            case 7: return R.drawable.ic_deity_surya;
            case 8: return R.drawable.ic_deity_durga;
            case 9: return R.drawable.ic_deity_krishna;
            case 10: return R.drawable.ic_deity_saraswati;
            case 11: return R.drawable.ic_deity_rama;
            default: return R.drawable.ic_om_symbol;
        }
    }

    /**
     * Maps a deity's imageUrl string to the corresponding drawable resource.
     * imageUrl values are stored in DeityEntity.imageUrl field.
     *
     * @param imageUrl the deity's image URL string (e.g., "deity_shiva")
     * @return drawable resource ID, or ic_om_symbol as fallback
     */
    public static int getIconForImageUrl(String imageUrl) {
        if (imageUrl == null) return R.drawable.ic_om_symbol;
        switch (imageUrl) {
            case "deity_shiva": return R.drawable.ic_deity_shiva;
            case "deity_hanuman": return R.drawable.ic_deity_hanuman;
            case "deity_ganesha": return R.drawable.ic_deity_ganesha;
            case "deity_vishnu": return R.drawable.ic_deity_vishnu;
            case "deity_lakshmi": return R.drawable.ic_deity_lakshmi;
            case "deity_shani": return R.drawable.ic_deity_shani;
            case "deity_surya": return R.drawable.ic_deity_surya;
            case "deity_durga": return R.drawable.ic_deity_durga;
            case "deity_krishna": return R.drawable.ic_deity_krishna;
            case "deity_saraswati": return R.drawable.ic_deity_saraswati;
            case "deity_rama": return R.drawable.ic_deity_rama;
            default: return R.drawable.ic_om_symbol;
        }
    }
}
