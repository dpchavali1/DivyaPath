package com.divyapath.app.ui.store;

public class Product {
    private final String name;
    private final String imageUrl;
    private final String price;
    private final String originalPrice;
    private final float rating;
    private final int reviewCount;
    private final String amazonAsin;
    private final String category;

    public Product(String name, String imageUrl, String price, String originalPrice,
                   float rating, int reviewCount, String amazonAsin, String category) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.originalPrice = originalPrice;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.amazonAsin = amazonAsin;
        this.category = category;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getOriginalPrice() { return originalPrice; }
    public float getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public String getAmazonAsin() { return amazonAsin; }
    public String getCategory() { return category; }

    /**
     * Returns the product image URL. Uses Amazon's direct product image CDN
     * which serves product images from ASIN.
     */
    public String getImageUrl() {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return imageUrl;
        }
        // Amazon direct product image URL from ASIN
        return "https://m.media-amazon.com/images/P/" + amazonAsin + ".01._SCLZZZZZZZ_SL500_.jpg";
    }

    public String getAmazonUrl() {
        return "https://www.amazon.in/dp/" + amazonAsin + "?tag=syncflowin-21";
    }
}
