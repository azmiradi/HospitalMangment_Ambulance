package bee.read.abanob.prog.com.hospitalmangment.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import bee.read.abanob.prog.com.hospitalmangment.R;

public class AboutFragment extends Fragment {
    View view;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.about_fragment, container, false);
        initialization();
         return view;
    }

    private void initialization() {
    }
}
