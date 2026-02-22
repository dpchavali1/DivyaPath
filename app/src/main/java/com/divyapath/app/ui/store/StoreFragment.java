package com.divyapath.app.ui.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.divyapath.app.R;
import com.divyapath.app.databinding.FragmentStoreBinding;

import java.util.ArrayList;
import java.util.List;

public class StoreFragment extends Fragment {

    private FragmentStoreBinding binding;
    private ProductAdapter adapter;
    private List<Product> allProducts;
    private String currentCategory = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStoreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbarStore.setNavigationOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v).popBackStack());

        allProducts = loadProducts();
        adapter = new ProductAdapter();
        binding.rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvProducts.setAdapter(adapter);
        adapter.setProducts(allProducts);

        setupCategoryFilter();
    }

    private void setupCategoryFilter() {
        binding.chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);

            if (id == R.id.chip_all) currentCategory = "All";
            else if (id == R.id.chip_incense) currentCategory = "Incense";
            else if (id == R.id.chip_diyas) currentCategory = "Diyas";
            else if (id == R.id.chip_idols) currentCategory = "Idols";
            else if (id == R.id.chip_puja) currentCategory = "Puja Thali";
            else if (id == R.id.chip_books) currentCategory = "Books";
            else if (id == R.id.chip_rudraksha) currentCategory = "Rudraksha";
            else if (id == R.id.chip_camphor) currentCategory = "Camphor";

            filterProducts();
        });
    }

    private void filterProducts() {
        if ("All".equals(currentCategory)) {
            adapter.setProducts(allProducts);
        } else {
            List<Product> filtered = new ArrayList<>();
            for (Product p : allProducts) {
                if (currentCategory.equals(p.getCategory())) {
                    filtered.add(p);
                }
            }
            adapter.setProducts(filtered);
        }
    }

    private List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();

        // Incense
        products.add(new Product("Cycle Pure Agarbatti Three in One", "https://m.media-amazon.com/images/I/71wJ6jMxkeL._SL1500_.jpg", "₹199", "₹299", 4.4f, 12500, "B00Q8KMAGI", "Incense"));
        products.add(new Product("Zed Black Manthan Dhoop Sticks", "https://m.media-amazon.com/images/I/61A+9VPq1CL.jpg", "₹249", "₹399", 4.3f, 8900, "B0926ZZ2HG", "Incense"));
        products.add(new Product("Hem Precious Assorted Incense Sticks", "https://m.media-amazon.com/images/I/61uIccGIpHL._SL1000_.jpg", "₹150", "₹220", 4.2f, 5600, "B00MGNRAWK", "Incense"));
        products.add(new Product("Cycle Pure Woods Natural Agarbatti", "https://m.media-amazon.com/images/I/815+8GVQw3L._SL1500_.jpg", "₹175", "₹250", 4.3f, 3200, "B074KCWMFB", "Incense"));

        // Diyas
        products.add(new Product("Borosil Akhand Diya Medium Brass", "https://m.media-amazon.com/images/I/61tFDSCoNGL._SL1000_.jpg", "₹699", "₹1299", 4.5f, 7800, "B00EZMNH9A", "Diyas"));
        products.add(new Product("LED Flameless Tealight Diya Set of 12", "https://m.media-amazon.com/images/I/61GWvbaakVL._SL1500_.jpg", "₹199", "₹399", 4.3f, 4500, "B09KGQBXPF", "Diyas"));
        products.add(new Product("Borosil Akhand Diya Large Brass", "https://m.media-amazon.com/images/I/61AG6SgUWtL._SL1000_.jpg", "₹899", "₹1499", 4.6f, 2300, "B00EZMNHHW", "Diyas"));
        products.add(new Product("Terracotta Decorative Akhand Diya", "https://m.media-amazon.com/images/I/810LveIQuqL._SL1500_.jpg", "₹349", "₹599", 4.0f, 6100, "B01M7SYMTC", "Diyas"));

        // Idols
        products.add(new Product("Brass Large Ganesh Idol Murti", "https://m.media-amazon.com/images/I/71r+46zauRL._SL1121_.jpg", "₹799", "₹1299", 4.7f, 9200, "B013LTDRPG", "Idols"));
        products.add(new Product("Brass Goddess Lakshmi Idol 6 Inch", "https://m.media-amazon.com/images/I/811Nf8Dko6L._SL1500_.jpg", "₹699", "₹1199", 4.4f, 3800, "B08G1R85NF", "Idols"));
        products.add(new Product("Brass Hanuman Idol 9.5 Inch", "https://m.media-amazon.com/images/I/81ugO2iR3lL._SL1500_.jpg", "₹1299", "₹1999", 4.6f, 2100, "B07MJ32XGG", "Idols"));
        products.add(new Product("Brass Radha Krishna Statue Pair", "https://m.media-amazon.com/images/I/91UMJZzWpHL._SL1500_.jpg", "₹2999", "₹4999", 4.8f, 1500, "B01CNMQG0K", "Idols"));

        // Puja Thali
        products.add(new Product("German Silver Pooja Thali Set 11 Items", "https://m.media-amazon.com/images/I/61jEjukjGZL._SL1080_.jpg", "₹699", "₹1199", 4.5f, 5400, "B0BW4T4JV9", "Puja Thali"));
        products.add(new Product("Brass Puja Thali Set Complete", "https://m.media-amazon.com/images/I/61dGEqKWm7L._SL1000_.jpg", "₹1499", "₹2199", 4.6f, 3200, "B00EZMNMJK", "Puja Thali"));
        products.add(new Product("German Silver Pooja Thali 10 Items", "https://m.media-amazon.com/images/I/71YzFFhmoDL._SL1080_.jpg", "₹599", "₹999", 4.2f, 7600, "B07J37KQZW", "Puja Thali"));
        products.add(new Product("Pure Copper Kalash for Puja", "https://m.media-amazon.com/images/I/61yrISNCEuL._SL1500_.jpg", "₹549", "₹899", 4.4f, 2800, "B0GDFM234H", "Puja Thali"));

        // Books
        products.add(new Product("Shrimad Bhagwat Gita Gita Press", "https://m.media-amazon.com/images/I/310OuT3+zRL.jpg", "₹35", "₹75", 4.8f, 25000, "B06XXTZHRN", "Books"));
        products.add(new Product("Hanuman Chalisa Gita Press", "https://m.media-amazon.com/images/I/81Yokrz107L._SL1136_.jpg", "₹25", "₹50", 4.5f, 11000, "B07TJXMP4B", "Books"));
        products.add(new Product("Sunderkand with Hanuman Chalisa", "https://m.media-amazon.com/images/I/A17ObNxMRJL._SL1500_.jpg", "₹49", "₹99", 4.6f, 8500, "B092RG8BPZ", "Books"));
        products.add(new Product("Ramcharitmanas Gita Press", "https://m.media-amazon.com/images/I/81R404zE3lL._SL1500_.jpg", "₹495", "₹700", 4.7f, 6200, "B09BD6P7M3", "Books"));

        // Rudraksha
        products.add(new Product("5 Mukhi Rudraksha Mala 108 Beads", "https://m.media-amazon.com/images/I/61QzUMGN0+S._SL1142_.jpg", "₹399", "₹799", 4.3f, 4100, "B078HT76H7", "Rudraksha"));
        products.add(new Product("Original Nepali 5 Mukhi Rudraksha", "https://m.media-amazon.com/images/I/81yZYYCRObL._SL1500_.jpg", "₹415", "₹799", 4.2f, 3500, "B00I6Z66DG", "Rudraksha"));
        products.add(new Product("5 Mukhi Rudraksha Bracelet Certified", "https://m.media-amazon.com/images/I/710OrnYuk-L._SL1500_.jpg", "₹449", "₹799", 4.4f, 5800, "B0C3M5L1X3", "Rudraksha"));
        products.add(new Product("Rudraksha Mala Original Certified", "https://m.media-amazon.com/images/I/51Dfskp3IUL.jpg", "₹599", "₹1199", 4.3f, 890, "B0CJTTPJWW", "Rudraksha"));

        // Camphor
        products.add(new Product("Mangalam Camphor Tablet 100g Pure", "https://m.media-amazon.com/images/I/61ny0s3+UfL._SL1000_.jpg", "₹195", "₹299", 4.5f, 15000, "B0725N7D7D", "Camphor"));
        products.add(new Product("Mangalam Bhimseni Camphor 100g", "https://m.media-amazon.com/images/I/71GnKrgPmeL._SL1500_.jpg", "₹280", "₹399", 4.4f, 8700, "B07ZJSTTHM", "Camphor"));
        products.add(new Product("Cycle Om Shanthi Pure Camphor", "https://m.media-amazon.com/images/I/51xrX+Lu-SL._SL1000_.jpg", "₹199", "₹349", 4.3f, 6300, "B09ZPNG422", "Camphor"));
        products.add(new Product("Mangalam Camphor Diffuser Wooden", "https://m.media-amazon.com/images/I/61ELjjfMMdL._SL1080_.jpg", "₹499", "₹799", 4.6f, 2100, "B0C71BMZ82", "Camphor"));

        return products;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
