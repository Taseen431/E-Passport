package com.e_passport.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.e_passport.R;
import com.e_passport.activities.ApplyActivity;
import com.e_passport.activities.StatusActivity;
import com.e_passport.adapters.CardAdapter;
import com.e_passport.ui.CenteredGridLayoutManager;
import com.e_passport.utilities.AppUtilities;
import com.e_passport.models.Card;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends Fragment implements CardAdapter.OnCardItemClickListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<Card> cardList;

    public static HomePage newInstance() {
        return new HomePage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.home_page, container, false);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        swipeRefreshLayout.setRefreshing(true);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        refreshData();

        return rootView;
    }

    private void refreshData() {
        cardList = new ArrayList<>();

        try {
            InputStream inputStream = getActivity().getAssets().open("data.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemObj = jsonArray.getJSONObject(i);
                String id = itemObj.getString("id");
                String title = itemObj.getString("title");
                String img = itemObj.getString("img");
                cardList.add(new Card(getContext(), id, title, img));
            }

        } catch (IOException | JSONException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        CardAdapter adapter = new CardAdapter(requireContext(), cardList);
        adapter.setOnCardItemClickListener(this);
        recyclerView.setLayoutManager(new CenteredGridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCardItemClick(int position) {
        Card clickedItem = cardList.get(position);
        if (clickedItem.getId().equals("apply_online")) {
            startActivity(new Intent(getContext(), ApplyActivity.class));
        } else if (clickedItem.getId().equals("about_e_passport")) {
            AppUtilities.openYoutube(getContext(), "https://www.youtube.com/watch?v=BsIwHjpTOw0");
        } else if (clickedItem.getId().equals("5_steps")) {
            AppUtilities.openDefaultBrowser(getContext(), "https://www.epassport.gov.bd/instructions/five-step-to-your-epassport");
        } else if (clickedItem.getId().equals("urgent_applications")) {
            AppUtilities.openDefaultBrowser(getContext(), "https://www.epassport.gov.bd/instructions/urgent-applications");
        } else if (clickedItem.getId().equals("check_status")) {
            startActivity(new Intent(getContext(), StatusActivity.class));
        } else if (clickedItem.getId().equals("fees")) {
            AppUtilities.openDefaultBrowser(getContext(), "https://www.epassport.gov.bd/instructions/passport-fees");
        } else if (clickedItem.getId().equals("instructions")) {
            AppUtilities.openDefaultBrowser(getContext(), "https://www.epassport.gov.bd/instructions/instructions");
        }
    }

}
