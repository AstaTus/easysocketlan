package com.astatus.easysocketlansampleserver.fragment

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.astatus.easysocketlansampleserver.BR

import com.astatus.easysocketlansampleserver.R
import com.astatus.easysocketlansampleserver.adapter.GeneralListAdapter
import com.astatus.easysocketlansampleserver.databinding.FragmentClientListBinding
import com.astatus.easysocketlansampleserver.entity.ClientEntity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_client_list.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ClientListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ClientListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClientListFragment() : Fragment() {

    private lateinit var binding: FragmentClientListBinding;

    private lateinit var clientListAdapter: GeneralListAdapter<ClientEntity>

    private lateinit var listener: IClientListFragmentListener

    private var clientItemHandler: ClientItemHandler = ClientItemHandler()

    private lateinit var clients: ArrayList<ClientEntity>

    interface IClientListFragmentListener{

        fun changeMessageFragment(client: ClientEntity)
    }

    inner class ClientItemHandler{
        public fun onMessageMoreClick(view: View) {

                var entity = view.getTag() as ClientEntity
                listener.changeMessageFragment(entity)

        }
    }


    public fun setParam(clients: ArrayList<ClientEntity>, listener: IClientListFragmentListener){
        this.listener = listener
        this.clients = clients
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate<FragmentClientListBinding>(layoutInflater, R.layout.fragment_client_list, container, false)

        clientListAdapter = GeneralListAdapter<ClientEntity>(R.layout.widget_client_item, BR.client, clients,
                BR.handler, clientItemHandler)

        var layoutManager = LinearLayoutManager(activity);
        binding.recyclerView.setLayoutManager(layoutManager)
        binding.recyclerView.setHasFixedSize(true);

        binding.recyclerView.adapter = clientListAdapter


        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }


    public fun updateList(){
        clientListAdapter.notifyDataSetChanged()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }



}// Required empty public constructor
