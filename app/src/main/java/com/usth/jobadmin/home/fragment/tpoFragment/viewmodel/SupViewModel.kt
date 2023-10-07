package com.usth.jobadmin.home.fragment.supFragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usth.jobadmin.model.Sup
import com.usth.jobadmin.util.Constants.Companion.COLLECTION_PATH_ROLE
import com.usth.jobadmin.util.Constants.Companion.COLLECTION_PATH_SUP
import com.usth.jobadmin.util.Constants.Companion.SUP_IMAGE_STORAGE_PATH
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SupViewModel : ViewModel() {

    private val mFirestore : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mStorage : StorageReference by lazy { FirebaseStorage.getInstance().reference }
    private var supListener : ListenerRegistration? = null
    private val _supList : MutableLiveData<List<Sup>> = MutableLiveData(emptyList())
    val supList : LiveData<List<Sup>> = _supList

    fun fetchSup(){
        val supRef = mFirestore.collection(COLLECTION_PATH_SUP)
        supListener = supRef.addSnapshotListener { value, error ->
            if (error != null){
                return@addSnapshotListener
            }
            val supDocs = value?.documents!!
            val supList = supDocs.map {
                it.toObject(Sup::class.java)!!
            }
            _supList.postValue(supList)
        }
    }

    fun deleteSup(sup: Sup){
        viewModelScope.launch(IO) {
            val supId = sup.uid
            val supImagePath = "$SUP_IMAGE_STORAGE_PATH/$supId"
            mFirestore.collection(COLLECTION_PATH_SUP).document(supId).delete().await()
            mFirestore.collection(COLLECTION_PATH_ROLE).document(supId).delete().await()
            mStorage.child(supImagePath).delete().await()
        }
    }

    override fun onCleared() {
        supListener?.remove()
        super.onCleared()
    }
}