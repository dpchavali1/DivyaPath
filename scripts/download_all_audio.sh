#!/bin/bash
# ============================================================
# Download all DivyaPath audio files to local backup
# ============================================================
set -euo pipefail

BASE="/Users/dchavali/GitHub/devpath/DivyaPath/audio_backup"
mkdir -p "$BASE"/{aartis,chalisas,mantras,bhajans,stotras}

total=0
success=0
failed=0

download() {
    local dir="$1"
    local name="$2"
    local url="$3"
    local ext="${url##*.}"
    # Clean extension (remove query params)
    ext="${ext%%\?*}"
    [[ "$ext" != "mp3" ]] && ext="mp3"
    local file="$BASE/$dir/$name.$ext"

    total=$((total + 1))

    if [[ -f "$file" ]]; then
        echo "[SKIP] $name (already exists)"
        success=$((success + 1))
        return
    fi

    echo -n "[$total] Downloading $name..."
    if curl -sL -o "$file" --max-time 120 "$url" 2>/dev/null; then
        # Verify it's actually audio
        type=$(file -b --mime-type "$file" 2>/dev/null || echo "unknown")
        size=$(stat -f%z "$file" 2>/dev/null || stat -c%s "$file" 2>/dev/null || echo "?")
        if [[ "$type" == audio/* ]] || [[ "$size" -gt 10000 ]]; then
            echo " OK ($(( size / 1024 ))KB)"
            success=$((success + 1))
        else
            echo " FAILED (not audio: $type, ${size}B)"
            rm -f "$file"
            failed=$((failed + 1))
        fi
    else
        echo " FAILED (download error)"
        rm -f "$file"
        failed=$((failed + 1))
    fi
}

echo "============================================="
echo "DivyaPath Audio Backup"
echo "Output: $BASE/"
echo "============================================="

# ── Aartis (11) ──
download "aartis" "jai_ganesh_deva" \
    "https://archive.org/download/PretrajSarkarKiAarti/Jai-Ganesh-Deva.mp3"
download "aartis" "om_jai_shiv_omkara" \
    "https://archive.org/download/PretrajSarkarKiAarti/Jai-Shiv-Omkara.mp3"
download "aartis" "om_jai_jagdish_hare" \
    "https://archive.org/download/PretrajSarkarKiAarti/Om-Jai-Jagdish-Hare-Bijender-Chauhan.mp3"
download "aartis" "om_jai_lakshmi_mata" \
    "https://archive.org/download/PretrajSarkarKiAarti/Jai-Lakshmi-Mata.mp3"
download "aartis" "jai_ambe_gauri" \
    "https://archive.org/download/PretrajSarkarKiAarti/Jai%20Ambe%20Gauri-f.mp3"
download "aartis" "aarti_hanuman_lala_ki" \
    "https://archive.org/download/PretrajSarkarKiAarti/Aarti-Kije-Hanuman-Lala-Ki.mp3"
download "aartis" "aarti_kunj_bihari_ki" \
    "https://archive.org/download/PretrajSarkarKiAarti/Kunj-Vihari-Ki.mp3"
download "aartis" "jai_saraswati_mata" \
    "https://archive.org/download/PretrajSarkarKiAarti/Saraswati-Mata-Aarti.mp3"
download "aartis" "aarti_surya_dev_ki" \
    "https://archive.org/download/PretrajSarkarKiAarti/Surya%20Dev%20Ji%20Ki%20Aarti.mp3"
download "aartis" "aarti_shani_dev_ki" \
    "https://archive.org/download/PretrajSarkarKiAarti/Shanidev%20Aarti-Bijender-Chauhan.mp3"
download "aartis" "aarti_shri_ram_ji_ki" \
    "https://archive.org/download/PretrajSarkarKiAarti/Ram%20Ji%20Ki.mp3"

# ── Chalisas (10) ──
download "chalisas" "hanuman_chalisa" \
    "https://archive.org/download/godchalisa/Hanuman%20Chalisa%20akchay.mp3"
download "chalisas" "ganesh_chalisa" \
    "https://archive.org/download/godchalisa/Ganesh%20Chalisa%20Sikha.mp3"
download "chalisas" "durga_chalisa" \
    "https://archive.org/download/godchalisa/Durga%20chalisa.mp3"
download "chalisas" "shiv_chalisa" \
    "https://archive.org/download/godchalisa/Shiv%20Chalisa%20Ravindra%20Jain.mp3"
download "chalisas" "lakshmi_chalisa" \
    "https://archive.org/download/godchalisa/Lakshmi%20Ji%20Chalisa.mp3"
download "chalisas" "saraswati_chalisa" \
    "https://archive.org/download/godchalisa/Saraswati%20Mata%20Chalisa%20female.mp3"
download "chalisas" "ram_chalisa" \
    "https://archive.org/download/godchalisa/Ram%20Chalisa.mp3"
download "chalisas" "shani_chalisa" \
    "https://archive.org/download/godchalisa/Shanidev%20Chalisa%20Ravindra%20Jain.mp3"
download "chalisas" "surya_chalisa" \
    "https://archive.org/download/godchalisa/Surya%20Chalisa.mp3"
download "chalisas" "vishnu_chalisa" \
    "https://archive.org/download/godchalisa/Vishnu%20Chalisa.mp3"

# ── Mantras (18) ──
download "mantras" "ganesh_beej_mantra" \
    "https://archive.org/download/MantrasFromVedasSlokas/Ganesh%20Mantra.mp3"
download "mantras" "vakratunda_mahakaya" \
    "https://archive.org/download/ShlokaAndBhaktigeetGanesh-MeenaTapaswi/Vakratunda_Mahakaya.mp3"
download "mantras" "om_namah_shivaya" \
    "https://archive.org/download/HinduSlokasAndMantras/Om%20Namah%20Shivaya.mp3"
download "mantras" "mahamrityunjaya_mantra" \
    "https://archive.org/download/MantrasFromVedasSlokas/Shiva%20Mahamrityunjaya%20Mantra.mp3"
download "mantras" "gayatri_mantra" \
    "https://archive.org/download/MantrasFromVedasSlokas/Gayatri%20Mantra.mp3"
download "mantras" "shanti_mantra" \
    "https://cs1.mp3.pm/listen/242468706/Y0pkNGlpWWt5TStRcFZ2U09ma1BTa1pUUXRseFEzTjlaaGErQkhxRitiai9sMnN2NUJkVXEwNXF5MWZLQVFzSE9Xc1hhWUsyVDlCMTVyNEs0Nm1NL21MSmsvVzNrTFJMcG8veHE3SUJmRlhvQ0U0aTVMWk5BNjEzejJQVmVTbG4/Sanskrit_-_OM_SARVE_BHAVANTU_SUKHINAH_SARVE_SANTU_NIRAMAYAH_SARVE_BHADRANI_PASHYANTU_MA_KASCHID_DUKH_(mp3.pm).mp3"
download "mantras" "lakshmi_beej_mantra" \
    "https://cs1.mp3.pm/listen/171567378/Y0pkNGlpWWt5TStRcFZ2U09ma1BTa1pUUXRseFEzTjlaaGErQkhxRitiaUxOYXpUVGxEcTZKRjdzSjlQejJBQmpUWEhkcGZQdk9wdUk0SnFxYnBQVDJTS0h3R2ZJZ1ZLcW9WMHVUdDYwRTVmamNkVWhualJrZjhzSTJmcUZtQ2I/Pooja_-_Maa_Lakshmi_Beej_Mantra_(mp3.pm).mp3"
download "mantras" "saraswati_beej_mantra" \
    "https://cs1.mp3.pm/listen/220498140/Y0pkNGlpWWt5TStRcFZ2U09ma1BTa1pUUXRseFEzTjlaaGErQkhxRitiZ2RQRm1JK1R5NVpWemZXUUVET3JpYmp1bGF4eXM4SzlaK0tuZmtNKzRlRy9RQkppbGZ4Tm5ZeVZCTXpXNG1GR05XM25ScHRQcHFKdzRrSmV1OHZXSEs/TaTTu_-_Saraswati_Beej_Manta_108_Times_(mp3.pm).mp3"
download "mantras" "surya_mantra" \
    "https://cs1.mp3.pm/listen/146668419/Y0pkNGlpWWt5TStRcFZ2U09ma1BTa1pUUXRseFEzTjlaaGErQkhxRitialRuSnd6ZzhsWW9vZGFJSERFZmZmSEluUUU4eko1L01XOTNHd1YwYUZUT0lGbXA1YnJQYXYrNHVnWDdCN1RCUDRNSGpPRktqbXNCNXhLOThtU2Y5dHc/Bidzha_mantra_Sure_-_Om_hraam_hreem_hraum_sah_suryaaya_namah_(mp3.pm).mp3"
download "mantras" "hare_krishna_mahamantra" \
    "https://archive.org/download/HinduSlokasAndMantras/Hare%20Krishna%20Maha%20Mantra.mp3"
download "mantras" "durga_beej_mantra" \
    "https://archive.org/download/MantrasFromVedasSlokas/Durga%20Mantra%20%28Om%20Dum%20Durgayei%20Namaha%29.mp3"
download "mantras" "shani_mantra" \
    "https://cs1.mp3.pm/listen/235934759/Y0pkNGlpWWt5TStRcFZ2U09ma1BTa1pUUXRseFEzTjlaaGErQkhxRitiaERsbmJLMU9OT244ampyREZhNUpldTNDYWErMGdmT0xFVXh4dVo2NkdsRnJTZkFPbU9iZS9Qc3FQQ0cya0locVB2ZGhQcHVDM2xBOWR1dE9ONkZGQk8/Arushi_Bajpai_-_Shani_Mantra_(mp3.pm).mp3"
download "mantras" "chandra_mantra" \
    "https://cs1.mp3.pm/listen/246502509/Y0pkNGlpWWt5TStRcFZ2U09ma1BTa1pUUXRseFEzTjlaaGErQkhxRitiaXNBLzVRRW1PcGJtOFFKc1draTRwWWhGeFRxM3gzL0lwdlFDTWNzUDF5ZzNQZDM3TFY0Qm5USURJUWlCME5zb1JtdHZVRDUyS1ZpZVVwZWFjdXVJVSs/Sonea_Madhv_-_Chandra_Beej_Mantra_Om_Shraam_Shreem_Shraum_Sah_Chandramase_Namah_(mp3.pm).mp3"
download "mantras" "mangal_mantra" \
    "https://archive.org/download/mangal-beej-mantra-108-times/mangal-beej-mantra-108-times.mp3"
download "mantras" "guru_brihaspati_mantra" \
    "https://archive.org/download/BrihaspateAtana/5.%20Brihaspate%20%28Atana%29.mp3"
download "mantras" "rahu_mantra" \
    "https://archive.org/download/rahu-beej-mantra/rahu-beej-mantra.mp3"
download "mantras" "ketu_mantra" \
    "https://archive.org/download/ketu-beej/ketu-beej.mp3"
download "mantras" "budh_mantra" \
    "https://cs1.mp3.pm/listen/196028637/Y0pkNGlpWWt5TStRcFZ2U09ma1BTa1pUUXRseFEzTjlaaGErQkhxRitiaDZOV0hmV0MzNmRMdmNkdFJuMThHTEJOcDhpcmp1WWM2NUwvNFg3cUJ2MllwMmRjY1AvMm00eHdGeTRoNDFQWTBLNjRUY3UrQlI2akhtdDBEMTFhbnE/Dinesh_Arjuna_-_Om_Bram_Breem_Broum_Sah_Budhaya_Namah_Budh_Beej_Mantra_108_Times_in_5_Times_(mp3.pm).mp3"
download "mantras" "shri_ram_jai_ram" \
    "https://archive.org/download/RamMantraChantingSriRamJaiRam128k/Ram_Mantra_Chanting_%7C_Sri_Ram_Jai_Ram%28128k%29.mp3"

# ── Bhajans (10) ──
download "bhajans" "achyutam_keshavam" \
    "https://archive.org/download/MantrasFromVedasSlokas/Achyutam%20Keshavam.mp3"
download "bhajans" "hare_krishna_hare_rama" \
    "https://archive.org/download/HindiBhajans-anoopJalotaJi/HareRamaHareKrishna.mp3"
download "bhajans" "ya_devi_sarvabhuteshu" \
    "https://archive.org/download/BhakthiSongs/Ya%20devi%20sarvabhuteshu.mp3"
download "bhajans" "shri_ramchandra_kripalu" \
    "https://archive.org/download/HindiBhajans-anoopJalotaJi/ShriRamchandraKripaluBhajMan.mp3"
download "bhajans" "ram_siya_ram" \
    "https://archive.org/download/ram-siya-ram-sachet-tandon-128-kbps/Ram%20Siya%20Ram%20-%20Sachet%20Tandon%20128%20Kbps.mp3"
download "bhajans" "govind_bolo_hari_gopal" \
    "https://archive.org/download/09.-govind-bolo-s-wadekar-20.55/09.Govind%20Bolo%20%28S%20Wadekar%29%2020.55.mp3"
download "bhajans" "jai_ambe_gauri_bhajan" \
    "https://archive.org/download/jai_ambe_gauri_aarti/jai_ambe_gauri_aarti.mp3"
download "bhajans" "bam_bam_bhole" \
    "https://archive.org/download/BAMBAMBHOLESwarRamjiKhadRaKrishnaGurungWww.lovenepal.net/BAM%20BAM%20BHOLE%20swar%20ramji%20khad%20ra%20krishna%20gurung%20www.lovenepal.net.mp3"
download "bhajans" "dukh_mein_sumiran" \
    "https://archive.org/download/dukh-me-sumiran-sab-kare/Dukh%20me%20sumiran%20sab%20kare.mp3"
download "bhajans" "mere_to_giridhar_gopal" \
    "https://archive.org/download/Meera-soundtrack/01%20Mere%20To%20Giridhar%20Gopal.mp3"

# ── Stotras (7) ──
download "stotras" "shiv_tandav_stotram" \
    "https://archive.org/download/shiv-tandav-shiv-tandav-stotram/Shiv%20Tandav%20-%20Shiv%20Tandav%20Stotram.mp3"
download "stotras" "mahishasura_mardini" \
    "https://archive.org/download/MahishasuraMardiniStotram/Mahishasura%20Mardini%20Stotram.mp3"
download "stotras" "vishnu_sahasranama" \
    "https://archive.org/download/VishnuSahasranamam_MSS/Vishnu%20Sahasranamam.mp3"
download "stotras" "lalita_sahasranama" \
    "https://archive.org/download/SriLalithaSahasranamam/Sri%20Lalitha%20Sahasranamam.mp3"
download "stotras" "aditya_hridayam" \
    "https://archive.org/download/AdityaHridayamMantra/Aditya%20Hrudayam.mp3"
download "stotras" "purusha_suktam" \
    "https://archive.org/download/VariousSuktam/006-purushasuktam.mp3"
download "stotras" "sri_suktam" \
    "https://archive.org/download/VariousSuktam/007-srisuktam.mp3"

echo ""
echo "============================================="
echo "Download Complete!"
echo "Total: ${total} | Success: ${success} | Failed: ${failed}"
echo "============================================="
du -sh "$BASE"
echo ""
du -sh "$BASE"/*/
