#!/bin/bash
# ============================================================
# DivyaPath Wallpaper Generator using Gemini API
# ============================================================
# Usage: ./generate_wallpapers.sh <GEMINI_API_KEY>
#
# Generates 66 HD wallpapers (11 deities × 3 styles × 2 images)
# Output: wallpapers/<deity>/<style>/ directory
#
# Models tried (in order of preference):
#   - gemini-2.0-flash-exp-image-generation (free tier, rate limited)
#   - gemini-2.5-flash-image (free tier, rate limited)
#   - gemini-3-pro-image-preview (free tier, rate limited)
#
# Rate limits: Free tier allows ~10 requests/minute.
# The script waits 15 seconds between requests to avoid 429 errors.
# If you have billing enabled, use imagen-4.0-generate-001 instead.
# ============================================================

set -euo pipefail

API_KEY="${1:?Usage: $0 <GEMINI_API_KEY>}"
MODEL="gemini-2.0-flash-exp-image-generation"
BASE_URL="https://generativelanguage.googleapis.com/v1beta/models/${MODEL}:generateContent?key=${API_KEY}"
OUTPUT_DIR="wallpapers"
DELAY=15  # seconds between requests

# 11 Deities
declare -a DEITIES=(
    "Ganesha"
    "Shiva"
    "Krishna"
    "Hanuman"
    "Lakshmi"
    "Durga"
    "Ram"
    "Vishnu"
    "Saraswati"
    "Shani"
    "Surya"
)

# 3 Style categories with prompt templates
# {deity} will be replaced with the deity name
declare -A STYLE_PROMPTS
STYLE_PROMPTS["divine"]="Generate a majestic, photorealistic HD wallpaper of Lord {deity} in divine glory. Rich golden aura, ethereal light rays, ornate traditional jewelry and clothing, sacred symbols, lotus flowers, heavenly clouds. Vibrant colors with deep saffron, gold, and royal blue tones. Portrait orientation 1080x1920, ultra detailed, spiritual atmosphere."
STYLE_PROMPTS["cute"]="Generate an adorable cute baby/chibi style illustration of Lord {deity} as a sweet, chubby little child deity. Big sparkling eyes, rosy cheeks, tiny divine ornaments, sitting on a fluffy cloud or lotus. Soft pastel colors with warm golden highlights. Kawaii art style, heartwarming and playful. Portrait orientation 1080x1920, high quality illustration."
STYLE_PROMPTS["artistic"]="Generate a stunning artistic wallpaper of Lord {deity} in bold modern Indian art style. Blend of traditional and contemporary aesthetics - geometric patterns, mandala backgrounds, vibrant watercolor splashes, intricate paisley motifs. Rich jewel tones: deep purple, emerald green, ruby red, gold leaf accents. Portrait orientation 1080x1920, gallery-quality artwork."

# Custom deity descriptions to help Gemini
declare -A DEITY_DESC
DEITY_DESC["Ganesha"]="elephant-headed Hindu god of wisdom, with trunk, large ears, holding modak sweet, mouse vahana"
DEITY_DESC["Shiva"]="blue-throated lord with third eye, crescent moon, Ganga flowing from matted hair, trident, serpent necklace, tiger skin"
DEITY_DESC["Krishna"]="dark blue-skinned lord playing flute, peacock feather crown, yellow dhoti, standing in tribhanga pose"
DEITY_DESC["Hanuman"]="mighty monkey god, orange/vermillion colored, carrying mountain, mace (gada), devotee of Lord Ram"
DEITY_DESC["Lakshmi"]="golden-skinned goddess of wealth, four arms, sitting on lotus, gold coins flowing, red sari, elephants"
DEITY_DESC["Durga"]="fierce warrior goddess riding lion/tiger, multiple arms holding weapons, red sari, killing Mahishasura demon"
DEITY_DESC["Ram"]="noble prince with bow and arrows, blue-skinned, golden crown, royal bearing, dharmic warrior"
DEITY_DESC["Vishnu"]="four-armed lord reclining on Shesha serpent, holding conch, discus, mace, lotus, blue skin"
DEITY_DESC["Saraswati"]="goddess of knowledge playing veena instrument, white sari, swan vehicle, sitting on lotus, books"
DEITY_DESC["Shani"]="dark-complexioned god of justice riding crow, holding sword, stern expression, blue/black robes, Saturn symbol"
DEITY_DESC["Surya"]="radiant sun god in golden chariot pulled by seven horses, brilliant light, golden armor, lotus hands"

# Counters
total=0
success=0
failed=0

echo "============================================="
echo "DivyaPath Wallpaper Generator"
echo "Model: ${MODEL}"
echo "Output: ${OUTPUT_DIR}/"
echo "Deities: ${#DEITIES[@]} × 3 styles × 2 = $((${#DEITIES[@]} * 3 * 2)) images"
echo "============================================="

for deity in "${DEITIES[@]}"; do
    for style in divine cute artistic; do
        # Create output directory
        dir="${OUTPUT_DIR}/${deity,,}/${style}"
        mkdir -p "$dir"

        # Build prompt with deity description
        prompt="${STYLE_PROMPTS[$style]}"
        prompt="${prompt//\{deity\}/${deity}}"
        prompt="${prompt} The deity is: ${DEITY_DESC[$deity]}"

        for i in 1 2; do
            total=$((total + 1))
            output_file="${dir}/${deity,,}_${style}_${i}.png"

            # Skip if already generated
            if [[ -f "$output_file" ]]; then
                echo "[SKIP] ${output_file} already exists"
                success=$((success + 1))
                continue
            fi

            echo -n "[${total}/66] Generating ${deity} ${style} #${i}..."

            # Vary the prompt slightly for the second image
            if [[ $i -eq 2 ]]; then
                final_prompt="${prompt} Use a different composition, angle, and color palette than the first variation."
            else
                final_prompt="${prompt}"
            fi

            # Make API request
            response=$(curl -s -X POST "$BASE_URL" \
                -H "Content-Type: application/json" \
                -d "{
                    \"contents\": [{\"parts\": [{\"text\": $(echo "$final_prompt" | python3 -c 'import sys,json; print(json.dumps(sys.stdin.read().strip()))')}]}],
                    \"generationConfig\": {\"responseModalities\": [\"TEXT\", \"IMAGE\"]}
                }" 2>&1)

            # Check for errors
            if echo "$response" | python3 -c "
import sys, json, base64
data = json.load(sys.stdin)
if 'error' in data:
    print(f'ERROR: {data[\"error\"][\"message\"][:100]}')
    sys.exit(1)
for part in data.get('candidates', [{}])[0].get('content', {}).get('parts', []):
    if 'inlineData' in part:
        img_data = base64.b64decode(part['inlineData']['data'])
        with open('$output_file', 'wb') as f:
            f.write(img_data)
        print(f'OK ({len(img_data)} bytes)')
        sys.exit(0)
print('ERROR: No image in response')
sys.exit(1)
" 2>/dev/null; then
                success=$((success + 1))
            else
                failed=$((failed + 1))
                echo " FAILED"
            fi

            # Rate limit delay
            if [[ $total -lt 66 ]]; then
                echo "  (waiting ${DELAY}s for rate limit...)"
                sleep "$DELAY"
            fi
        done
    done
done

echo ""
echo "============================================="
echo "Generation Complete!"
echo "Total: ${total} | Success: ${success} | Failed: ${failed}"
echo "Output directory: ${OUTPUT_DIR}/"
echo "============================================="
echo ""
echo "Next steps:"
echo "1. Review generated images in ${OUTPUT_DIR}/"
echo "2. Upload to a CDN or cloud storage (e.g., Firebase Storage, Cloudflare R2)"
echo "3. Update AutoWallpaperWorker.java with the URLs"
