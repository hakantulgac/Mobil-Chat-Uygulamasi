package com.example.qmessenger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qmessenger.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ChatRecyclerAdapter
    private var chats = arrayListOf<Chat>()
    lateinit var room: String
    private lateinit var chars: String
    private lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chars = "/*|_a.b, cç:def;gğ-hıi?j:k=)l90m&%noöp+^qr('sşt£uüv#w12x34y56z78<>[]"
        room = ""
        firestore = Firebase.firestore
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatRecyclerAdapter()
        adapter.chats = chats
        binding.chatRecycler.adapter = adapter
        binding.chatRecycler.layoutManager= LinearLayoutManager(requireContext())

        binding.joinButton.setOnClickListener {
            binding.chatText.setText("")
            room = binding.roomText.text.toString()

            firestore.collection("Chat").whereEqualTo("room",room).addSnapshotListener { value, error ->
                if(value!!.documents.size != 0){
                    binding.roomText.visibility = View.INVISIBLE
                    binding.createButton.visibility = View.INVISIBLE
                    binding.joinButton.visibility = View.INVISIBLE
                    binding.chatRecycler.visibility = View.VISIBLE
                    binding.sendButton.visibility = View.VISIBLE
                    binding.chatText.visibility = View.VISIBLE
                    binding.backButton.visibility = View.INVISIBLE
                    binding.backButton2.visibility = View.VISIBLE
                    getMessages(room)
                }
            }

        }
        binding.backButton.setOnClickListener {
            auth.signOut()
            val action = ChatFragmentDirections.actionChatFragmentToLoginFrangment()
            findNavController().navigate(action)
        }

        binding.backButton2.setOnClickListener {
            binding.roomText.setText("")
            binding.backButton2.visibility = View.INVISIBLE
            binding.backButton.visibility = View.VISIBLE
            binding.roomText.visibility = View.VISIBLE
            binding.createButton.visibility = View.VISIBLE
            binding.joinButton.visibility = View.VISIBLE
            binding.chatRecycler.visibility = View.INVISIBLE
            binding.sendButton.visibility = View.INVISIBLE
            binding.chatText.visibility = View.INVISIBLE
        }
        binding.createButton.setOnClickListener {
            binding.chatText.setText("")
            room = binding.roomText.text.toString()
            val dataMap = HashMap<String, Any>()
            val date = FieldValue.serverTimestamp()
            firestore.collection("Chat").whereEqualTo("room",room).addSnapshotListener { value, error ->
                if(value!!.documents.size == 0){
                    var msg = room + " nolu odaya hoş geldiniz"
                    dataMap.put("room", room)
                    dataMap.put("text", encryption(msg))
                    dataMap.put("user", "")
                    dataMap.put("date", date)
                    firestore.collection("Chat").add(dataMap).addOnSuccessListener {
                        binding.chatText.setText("")
                    }.addOnFailureListener {
                        binding.chatText.setText("")
                    }
                    getMessages(room)
                    binding.roomText.visibility = View.INVISIBLE
                    binding.createButton.visibility = View.INVISIBLE
                    binding.joinButton.visibility = View.INVISIBLE
                    binding.chatRecycler.visibility = View.VISIBLE
                    binding.sendButton.visibility = View.VISIBLE
                    binding.chatText.visibility = View.VISIBLE
                    binding.backButton.visibility = View.INVISIBLE
                    binding.backButton2.visibility = View.VISIBLE

                }
            }
        }

        binding.sendButton.setOnClickListener {

            if(auth.currentUser!=null) {
                val chatText = binding.chatText.text.toString()
                val user = auth.currentUser!!.email.toString()
                val date = FieldValue.serverTimestamp()
                val dataMap = HashMap<String, Any>()
                dataMap.put("room", room)
                dataMap.put("text", encryption(chatText))
                dataMap.put("user", user!!)
                dataMap.put("date", date)
                firestore.collection("Chat").add(dataMap).addOnSuccessListener {
                    binding.chatText.setText("")
                }.addOnFailureListener {
                    binding.chatText.setText("")
                }
                getMessages(room)

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getMessages(_room:String){
        firestore.collection("Chat").whereEqualTo("room",_room).orderBy("date",Query.Direction.ASCENDING).addSnapshotListener { value, error ->
            if(error!=null){
            }else{
                if(value!=null){
                    if(value.isEmpty){
                        Toast.makeText(requireContext(),"Mesaj Yok",Toast.LENGTH_LONG).show()
                    }else{
                        chats.clear()
                        for (document in value!!.documents){
                            val text = document.get("text") as String
                            val user = document.get("user") as String
                            val chat = Chat(user,decryption(text))
                            chats.add(chat)
                        }


                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
    fun encryption(msg:String): String{
        var result = ""
        var counter = 0
        for(a in msg){
            counter  = 0
            for(z in chars){
                if(a==z){
                    result += chars[counter+4]
                }
                counter++
            }
        }
        return result
    }
    fun decryption(msg:String): String{
        var result = ""
        var counter = 0
        for(a in msg){
            counter  = 0
            for(z in chars){
                if(a==z){
                    result += chars[counter-4]
                }
                counter++
            }
        }
        return result
    }

}