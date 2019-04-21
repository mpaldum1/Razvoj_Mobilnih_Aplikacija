package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PitanjeFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PitanjeFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PitanjeFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Kviz";
    private static final String ARG_PARAM2 = "Pitanja";


    // TODO: Rename and change types of parameters
    private String paramTekstPitanja;
    private ArrayList<Pitanje> listaPitanja = new ArrayList<>();
    private Pitanje trenutnoPitanje;
    private ArrayList<String> listaOdgovora = new ArrayList<>();
    private Kviz trenutniKviz;

    private TextView tekstPitanja;
    private ListView odgovoriPitanja;

    private ArrayAdapter<String> adapter;
    private Integer brojTacnihOdgovora = 0;
    private Double procenatTacnihOdgovora = 0.0;
    private Integer brojPitanja = 0;

    private int positionTacni = -1;
    private int positionTrenutnog = -1;

    private OnFragmentInteractionListener mListener;

    public PitanjeFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PitanjeFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static PitanjeFrag newInstance(Kviz trenutniKviz, ArrayList<Pitanje> listaPitanja) {
        PitanjeFrag fragment = new PitanjeFrag();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, trenutniKviz);
        args.putParcelableArrayList(ARG_PARAM2, listaPitanja);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            trenutniKviz = getArguments().getParcelable(ARG_PARAM1);
            listaPitanja = getArguments().getParcelableArrayList(ARG_PARAM2);
            listaPitanja.remove(listaPitanja.size() - 1);           //brisemo dodaj pitanje
            trenutniKviz.setPitanja(listaPitanja);
            Collections.shuffle(listaPitanja);
            trenutnoPitanje = listaPitanja.get(0);
            listaOdgovora = trenutnoPitanje.dajRandomOdgovore();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pitanje, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialize();
        tekstPitanja.setText(trenutnoPitanje.getNaziv());
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listaOdgovora) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);

                if (position == positionTacni && positionTacni != positionTrenutnog) {
                    text.setBackgroundResource(R.color.zelena);
                } else if (position == positionTacni) {
                    text.setBackgroundResource(R.color.zelena);
                } else {
                    text.setBackgroundResource(android.R.drawable.edit_text);
                }
                if (position != positionTacni && position == positionTrenutnog) {
                    text.setBackgroundResource(R.color.crvena);
                }

                return view;
            }

        };
        odgovoriPitanja.setAdapter(adapter);

        odgovoriPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                positionTacni = 0;
                for (String trenutni : listaOdgovora) {
                    if (trenutni.equals(trenutnoPitanje.getTacan())) {
                        break;
                    }
                    positionTacni++;
                }
                positionTrenutnog = position;
                adapter.notifyDataSetChanged();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myTask(position);
                    }
                }, 2000);
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
        // TODO: Update argument type and name
        void onInputB(String brojTacnihOdgvoora, String presotaloPitanja, String procenatTacnihOdgovora);

        void onFragmentInteraction(Uri uri);
    }

    private void initialize() {
        tekstPitanja = Objects.requireNonNull(getView()).findViewById(R.id.tekstPitanja);
        odgovoriPitanja = getView().findViewById(R.id.odgovoriPitanja);

    }

    private void myTask(int position) {

        if (listaOdgovora.get(position).toString().equals(trenutnoPitanje.getTacan())) {
            brojTacnihOdgovora++;
        }
        int moreToGo = trenutniKviz.getPitanja().size() - brojPitanja - 1;
        procenatTacnihOdgovora = 100 * (brojTacnihOdgovora.doubleValue()) / (brojPitanja + 1);
        procenatTacnihOdgovora = Math.round(procenatTacnihOdgovora * 10) / 10.;
        if (mListener != null) {
            mListener.onInputB(brojTacnihOdgovora.toString(), Integer.toString(moreToGo), procenatTacnihOdgovora.toString());
        }
        if (brojPitanja < trenutniKviz.getPitanja().size() - 1) {
            brojPitanja++;
            trenutnoPitanje = listaPitanja.get(brojPitanja);
            listaOdgovora.removeAll(listaOdgovora);
            listaOdgovora.addAll(trenutnoPitanje.getOdgovori());
            tekstPitanja.setText(trenutnoPitanje.getNaziv());
        } else {
            tekstPitanja.setText("Kviz je završen!");
            listaOdgovora.clear();
        }
        positionTrenutnog = -1;
        positionTacni = -1;                                                                         // restartujemo boje
        adapter.notifyDataSetChanged();

    }
}
