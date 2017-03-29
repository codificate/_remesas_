package net.iantech.android.remitee;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.iantech.android.remitee.model.AuditTrail;
import net.iantech.android.remitee.model.Country;
import net.iantech.android.remitee.tasks.SaveAuditTrailTask;

import java.util.ArrayList;

/**
 * Created by Lucas on 8/1/2016.
 */
public class QuoteSelectTargetCountryFragment extends Fragment {
    private final String LOG_TAG = QuoteSelectTargetCountryFragment.class.getSimpleName();
    private ArrayAdapter<Country> adapter = null;

    private RemiteeApp app;

    public QuoteSelectTargetCountryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        app = ((RemiteeActivity) getActivity()).app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_quote_selecttargetcountry, container, false);
        if (rootView == null)
            return null;

        adapter = new CountriesAdapter(
                getActivity(),
                new ArrayList<Country>());

        ListView matches_listview = (ListView) rootView.findViewById(R.id.targetCountriesListView);
        if (matches_listview != null) {
            matches_listview.setAdapter(adapter);
            matches_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Country country = (Country) parent.getItemAtPosition(position);
                    if (country != null) {

                        if (app.currentTargetCountry == null || app.currentSourceCountry == null || app.currentTargetCountry.getId() != country.getId()) {
                            //Seteo pais de destino
                            app.currentTargetCountry = country;
                            app.currentTransaction.setTargetCountryCode(country.getCode());
                            app.currentTransaction.setTargetCountryId(country.getId());

                            //Guardo un auditTrail para registrar esta acción
                            AuditTrail auditTrail = new AuditTrail("Seleccion de Pais de Destino", app.deviceId, Build.MODEL, Build.MANUFACTURER, app.devicePhoneNumber, app.accountKitId, app.accountKitPhoneNumber, country.getName());
                            SaveAuditTrailTask saveAuditTrailTask = new SaveAuditTrailTask(getActivity());
                            saveAuditTrailTask.execute(auditTrail);

                            GoToNextFrame();
                        } else {
                            GoToNextFrame();
                        }
                    }
                }
            });
        }

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle("Selecciona el país destino");

        app.currentTargetCountry = null;

        loadCountries();
    }

    private void GoToNextFrame() {
        //Navego hacia el fragment del segundo paso
        getFragmentManager().beginTransaction().replace(R.id.activity_send, new QuoteAmountFragment()).addToBackStack("Seleccionar Pais de Destino").commit();
    }

    private void loadCountries() {

        ArrayList<Country> countries = new ArrayList<Country>();

        Resources resources = getResources();
        String[] country_names = resources.getStringArray(R.array.country_names);
        String[] country_codes = resources.getStringArray(R.array.country_codes);
        String[] country_currencySymbols = resources.getStringArray(R.array.country_currencySymbols);
        String[] country_currencyCodes = resources.getStringArray(R.array.country_currencyCodes);
        String[] country_currencyNames = resources.getStringArray(R.array.country_currencyNames);
        String[] country_flags = resources.getStringArray(R.array.country_flags);
        int[] country_phoneCodes = resources.getIntArray(R.array.country_phoneCodes);
        int[] country_ids = resources.getIntArray(R.array.country_ids);


        for (int i = 0; i < country_names.length; i++) {
            Country country = new Country(country_ids[i], country_names[i], country_codes[i], country_currencySymbols[i], country_currencyCodes[i], country_currencyNames[i], country_flags[i], country_phoneCodes[i], 0);

            //TODO: por ahora seteo argentina como Pais por default.
            if (i == 0) {
                app.currentSourceCountry = country;
                app.currentTransaction.setSourceCountryCode(app.currentSourceCountry.getCode());
            }
            countries.add(country);
        }

        adapter.clear();
        adapter.addAll(countries); //Honeycomb or above. Antes no existia el addAll
    }

    /*
    @Override
    public void onDestroy() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    */
}