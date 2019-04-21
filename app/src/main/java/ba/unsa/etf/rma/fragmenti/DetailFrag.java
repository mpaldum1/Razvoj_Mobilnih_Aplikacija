package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Objects;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.GridViewAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Kvizovi";
    private static final String ARG_PARAM2 = "Kategorija";

    // TODO: Rename and change types of parameter;

    private OnFragmentInteractionListener mListener;
    private ArrayList<Kviz> listaKvizova = new ArrayList<>();
    private ArrayList<Kategorija> listaKategorija;
    private Kategorija trenutnaKategorija;
    private ArrayList<Kviz> filterListaKvizova = new ArrayList<>();

    private GridView griedView;
    private GridViewAdapter adapter;

    private ListView lvListaKategorija;
    private String imeKategorije = "";


    public DetailFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFrag newInstance(String param1, String param2) {
        DetailFrag fragment = new DetailFrag();
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
            listaKvizova = getArguments().getParcelableArrayList(ARG_PARAM1);
            listaKategorija = getArguments().getParcelableArrayList(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        griedView = Objects.requireNonNull(getView()).findViewById(R.id.gridKvizovi);
        adapter = new GridViewAdapter(filterListaKvizova,getContext());
        griedView.setAdapter(adapter);

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void filtrirajIPrikazi(String imeKategorije) {

        filterListaKvizova.clear();
        for (Kviz trenutni : listaKvizova) {
            if (trenutni.getKategorija().getNaziv().equals(imeKategorije)) {
                filterListaKvizova.add(trenutni);
            }
        }

        adapter.notifyDataSetChanged();

    }
}
