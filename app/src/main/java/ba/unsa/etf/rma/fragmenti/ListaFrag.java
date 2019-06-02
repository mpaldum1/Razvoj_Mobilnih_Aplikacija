package ba.unsa.etf.rma.fragmenti;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;

public class ListaFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Kvizovi";
    private static final String ARG_PARAM2 = "Kategorije";

    // TODO: Rename and change types of parameters


    private OnFragmentInteractionListener mListener;
    private ArrayList<String> listaImenaKategorija = new ArrayList<>();
    private ArrayList<Kviz> listaKvizova = new ArrayList<>();
    private ArrayList<Kategorija> listaKategorija = new ArrayList<>();
    private ListView lvListaKategorija;

    public ListaFrag() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ListaFrag newInstance(String param1, String param2) {
        ListaFrag fragment = new ListaFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            listaKvizova= getArguments().getParcelable(ARG_PARAM1);
            listaKategorija = getArguments().getParcelableArrayList(ARG_PARAM2);

        }

        Log.e("Cetvrti", "ListaFrag onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialize();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, listaImenaKategorija);
        lvListaKategorija.setAdapter(adapter);

        lvListaKategorija.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nazivTrenutneKategorije = (String) parent.getAdapter().getItem(position);        //provjeri ovo
                mListener.onInputA(nazivTrenutneKategorije);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onInputA(String nazivKategorije);
        void onFragmentInteraction(Uri uri);
    }

    private void initialize() {
        lvListaKategorija = getView().findViewById(R.id.listaKategorija);
    }

}
