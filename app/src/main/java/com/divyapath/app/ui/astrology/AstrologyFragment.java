package com.divyapath.app.ui.astrology;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divyapath.app.R;
import com.divyapath.app.databinding.FragmentAstrologyBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AstrologyFragment extends Fragment {

    private FragmentAstrologyBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAstrologyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbarAstrology.setNavigationOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v).popBackStack());

        String today = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
        binding.tvAstrologyDate.setText(today);

        List<Rashi> rashis = loadRashis();
        RashiAdapter adapter = new RashiAdapter(rashis);
        binding.rvRashis.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rvRashis.setAdapter(adapter);

        binding.btnAstrotalk.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://astrotalk.com/?ref=divyapath"))));
    }

    private List<Rashi> loadRashis() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        List<Rashi> rashis = new ArrayList<>();

        rashis.add(new Rashi("Mesh", "मेष", "♈", getPrediction(0, dayOfWeek)));
        rashis.add(new Rashi("Vrishabh", "वृषभ", "♉", getPrediction(1, dayOfWeek)));
        rashis.add(new Rashi("Mithun", "मिथुन", "♊", getPrediction(2, dayOfWeek)));
        rashis.add(new Rashi("Kark", "कर्क", "♋", getPrediction(3, dayOfWeek)));
        rashis.add(new Rashi("Singh", "सिंह", "♌", getPrediction(4, dayOfWeek)));
        rashis.add(new Rashi("Kanya", "कन्या", "♍", getPrediction(5, dayOfWeek)));
        rashis.add(new Rashi("Tula", "तुला", "♎", getPrediction(6, dayOfWeek)));
        rashis.add(new Rashi("Vrishchik", "वृश्चिक", "♏", getPrediction(7, dayOfWeek)));
        rashis.add(new Rashi("Dhanu", "धनु", "♐", getPrediction(8, dayOfWeek)));
        rashis.add(new Rashi("Makar", "मकर", "♑", getPrediction(9, dayOfWeek)));
        rashis.add(new Rashi("Kumbh", "कुंभ", "♒", getPrediction(10, dayOfWeek)));
        rashis.add(new Rashi("Meen", "मीन", "♓", getPrediction(11, dayOfWeek)));

        return rashis;
    }

    private String getPrediction(int rashiIndex, int dayOfWeek) {
        String[][] predictions = {
            // Mon, Tue, Wed, Thu, Fri, Sat, Sun for each rashi
            {"Aaj dhan labh hoga", "Kaam mein safalta milegi", "Parivaar se khushi", "Swasthya ka dhyan rakhein", "Naya avsar milega", "Yatra ki sambhavna", "Manoranjan ka din"},
            {"Vyapar mein unnati", "Mitra se milap hoga", "Aarthik labh hoga", "Kisi se vivad ho sakta hai", "Shubh samachar milega", "Aalas se bachein", "Pooja paath karein"},
            {"Padhai mein safalta", "Samaj mein maan badhega", "Kisi ki madad milegi", "Swasthya achha rahega", "Dhairya rakhein", "Parivaar ke saath samay bitayein", "Kaam ki safalta"},
            {"Ghar mein shanti rahegi", "Naye rishte banenge", "Kaam mein deri ho sakti hai", "Maan samman badhega", "Dhan ki praapti", "Swasthya par dhyan dein", "Khushi ka din"},
            {"Neta jaise gun dikhenge", "Safalta aapke kadam chumegi", "Kuch aasthik karya karein", "Sabr rakhein", "Aarthik sthiti sudhare", "Parivaar mein khushi", "Naya karya shuru karein"},
            {"Bouddhik karya mein safalta", "Swasthya ka dhyan rakhein", "Vyapar mein labh", "Mitra se sahayta", "Yatra ho sakti hai", "Aalas chhod karya karein", "Dharmik karya karein"},
            {"Samanvay banaye rakhein", "Rishton mein madhurta", "Dhan labh hoga", "Karya mein safalta", "Samajik karya karein", "Anushashan rakhein", "Shubh avsar"},
            {"Gupt labh hoga", "Mano bal badhega", "Kaam mein deri par safalta", "Swasthya ka dhyan", "Parivaar se pyar", "Anusandhan karein", "Naye avsar"},
            {"Bhagya ka saath milega", "Yatra shubh hogi", "Padhai mein man lagega", "Dharm karya karein", "Dhan ki praapti", "Swasthya uttam", "Khel mein safalta"},
            {"Mehnat rang layegi", "Vyavsayik unnati", "Samaj mein izzat", "Sabr rakhein", "Sthir rahein", "Bade karya mein safalta", "Parivaar ke saath"},
            {"Naye vichar aayenge", "Mitra ka saath milega", "Takneeki karya mein labh", "Samajik seva karein", "Kuch naya seekhein", "Dhan ki chinta na karein", "Aatm chintan karein"},
            {"Adhyatmik unnati", "Kalpana shakti badhegi", "Swasthya achha", "Maan samman badega", "Yatra sambhav", "Santosh rakhein", "Pooja paath shubh"}
        };

        int dayIndex = (dayOfWeek + 5) % 7; // Convert Calendar.DAY_OF_WEEK to 0-6 Mon-Sun
        return predictions[rashiIndex % 12][dayIndex % 7];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Inner classes
    static class Rashi {
        final String name, nameHindi, symbol, prediction;
        Rashi(String name, String nameHindi, String symbol, String prediction) {
            this.name = name; this.nameHindi = nameHindi;
            this.symbol = symbol; this.prediction = prediction;
        }
    }

    static class RashiAdapter extends RecyclerView.Adapter<RashiAdapter.VH> {
        private final List<Rashi> rashis;
        private int expandedPos = -1;

        RashiAdapter(List<Rashi> rashis) { this.rashis = rashis; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rashi, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Rashi rashi = rashis.get(position);
            holder.tvSymbol.setText(rashi.symbol);
            holder.tvName.setText(rashi.name);
            holder.tvNameHindi.setText(rashi.nameHindi);
            holder.tvPrediction.setText(rashi.prediction);
            holder.tvPrediction.setVisibility(expandedPos == position ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                int prev = expandedPos;
                expandedPos = expandedPos == position ? -1 : position;
                if (prev >= 0) notifyItemChanged(prev);
                notifyItemChanged(position);
            });
        }

        @Override public int getItemCount() { return rashis.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final TextView tvSymbol, tvName, tvNameHindi, tvPrediction;
            VH(@NonNull View itemView) {
                super(itemView);
                tvSymbol = itemView.findViewById(R.id.tv_rashi_symbol);
                tvName = itemView.findViewById(R.id.tv_rashi_name);
                tvNameHindi = itemView.findViewById(R.id.tv_rashi_name_hindi);
                tvPrediction = itemView.findViewById(R.id.tv_rashi_prediction);
            }
        }
    }
}
