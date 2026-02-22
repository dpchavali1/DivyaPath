package com.divyapath.app.ui.puja;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.divyapath.app.utils.PujaFlowData;

import java.util.List;

public class PujaFlowViewModel extends ViewModel {

    private String deityName;
    private String tier;
    private List<PujaFlowData.PujaStep> steps;

    private final MutableLiveData<Integer> currentStepIndex = new MutableLiveData<>(0);
    private final MutableLiveData<PujaFlowData.PujaStep> currentStep = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isComplete = new MutableLiveData<>(false);

    public void init(String deityName, String tier) {
        this.deityName = deityName;
        this.tier = tier;
        this.steps = PujaFlowData.getSteps(deityName, tier);
        if (!steps.isEmpty()) {
            currentStepIndex.setValue(0);
            currentStep.setValue(steps.get(0));
        }
    }

    public void nextStep() {
        Integer idx = currentStepIndex.getValue();
        if (idx == null || steps == null) return;
        if (idx < steps.size() - 1) {
            int next = idx + 1;
            currentStepIndex.setValue(next);
            currentStep.setValue(steps.get(next));
        } else {
            isComplete.setValue(true);
        }
    }

    public void previousStep() {
        Integer idx = currentStepIndex.getValue();
        if (idx == null || idx <= 0) return;
        int prev = idx - 1;
        currentStepIndex.setValue(prev);
        currentStep.setValue(steps.get(prev));
    }

    public int getTotalSteps() {
        return steps != null ? steps.size() : 0;
    }

    public String getDeityName() { return deityName; }
    public String getTier() { return tier; }
    public LiveData<Integer> getCurrentStepIndex() { return currentStepIndex; }
    public LiveData<PujaFlowData.PujaStep> getCurrentStep() { return currentStep; }
    public LiveData<Boolean> getIsComplete() { return isComplete; }
}
