package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InformacijeFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InformacijeFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InformacijeFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Kviz";
    private static final String ARG_PARAM2 = "Pitanja";

    // TODO: Rename and change types of parameters
    private Kviz paramTrenutniKviz;
    private ArrayList<Pitanje> paramPitanjaKviza;


    private OnFragmentInteractionListener mListener;
    private TextView infNazivKviza;
    private TextView infBrojTacnihPitanja;
    private TextView infBrojPreostalihPitanja;
    private TextView infProcenatTacni;
    private Button btnKraj;

    public InformacijeFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InformacijeFrag.
     */
    public static InformacijeFrag newInstance(String param1, String param2) {

        InformacijeFrag fragment = new InformacijeFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

                                                                                   //pripremamo pocetno stanje
        if (getArguments() != null) {
            paramTrenutniKviz = getArguments().getParcelable(ARG_PARAM1);
            paramPitanjaKviza = getArguments().getParcelableArrayList(ARG_PARAM2);
            paramTrenutniKviz.setPitanja(paramPitanjaKviza);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_informacije, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialize();
        infNazivKviza.setText(paramTrenutniKviz.getNaziv());                                    // pocetne informacije
        infBrojTacnihPitanja.setText("0");
        infBrojPreostalihPitanja.setText((Integer.toString(paramTrenutniKviz.getPitanja().size() - 1)));
        infProcenatTacni.setText("0.0%");

        btnKraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
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
        void onInputA(CharSequence input);
        void onFragmentInteraction(Uri uri);
    }

    private void initialize() {

        infNazivKviza = Objects.requireNonNull(getView()).findViewById(R.id.infNazivKviza);
        infBrojTacnihPitanja = getView().findViewById(R.id.infBrojTacnihPitanja);
        infBrojPreostalihPitanja = getView().findViewById(R.id.infBrojPreostalihPitanja);
        infProcenatTacni = getView().findViewById(R.id.infProcenatTacni);
        btnKraj = getView().findViewById(R.id.btnKraj);

    }

    public void setEditTexts(String brojTacnihOdgovora, String brojPreostalih, String procenatTacnih){
        infBrojTacnihPitanja.setText(brojTacnihOdgovora);
        infBrojPreostalihPitanja.setText(brojPreostalih);
        String pomocni = procenatTacnih + "%";
        infProcenatTacni.setText(pomocni);
    }
}
