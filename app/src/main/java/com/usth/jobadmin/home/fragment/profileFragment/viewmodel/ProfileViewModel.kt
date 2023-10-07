package com.usth.jobadmin.home.fragment.profileFragment.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usth.jobadmin.model.Sup
import com.usth.jobadmin.util.Constants
import com.usth.jobadmin.util.Constants.Companion.COLLECTION_PATH_SUP
import com.usth.jobadmin.util.Constants.Companion.SUP_IMAGE_STORAGE_PATH
import com.usth.jobadmin.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "ProfileViewModel"

class ProfileViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var imageUri: Uri? = null

    fun setImageUri(imageUri: Uri) {
        this.imageUri = imageUri
    }

    fun getImageUri(): Uri? {
        return this.imageUri
    }

    private val _currentUser: MutableLiveData<Sup> = MutableLiveData(Sup())
    val currentUser: LiveData<Sup> = _currentUser

    private val _updateStatus : MutableLiveData<UiState> = MutableLiveData(UiState.LOADING)
    val updateStatus : LiveData<UiState> = _updateStatus

    private val _deleteStatus : MutableLiveData<UiState> = MutableLiveData(UiState.LOADING)
    val deleteStatus : LiveData<UiState> = _deleteStatus

    fun fetchUser(uid: String) {
        viewModelScope.launch {
            val userRef = mFirestore.collection(COLLECTION_PATH_SUP).document(uid).get().await()
            val user = userRef.toObject(Sup::class.java)!!
            Log.d(TAG, "User : $user")
            _currentUser.postValue(user)
        }
    }

    fun updateUser(sup: Sup) {
       try {
           viewModelScope.launch(IO) {
               _updateStatus.postValue(UiState.LOADING)
               if (!sup.imageUri.startsWith("https://firebasestorage.googleapis.com/")) {
                   val editUserRef =
                       mStorage.getReference(SUP_IMAGE_STORAGE_PATH).child(sup.uid)
                   editUserRef.putFile(Uri.parse(sup.imageUri)).await()
                   sup.imageUri = editUserRef.downloadUrl.await().toString()
               }

               val currentUser = mAuth.currentUser!!
               if (
                   sup.username != currentUser.displayName ||
                   sup.email != currentUser.email ||
                   sup.imageUri != currentUser.photoUrl.toString()
               ) {
                   val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(sup.username)
                       .setPhotoUri(Uri.parse(sup.imageUri)).build()
                   currentUser.updateProfile(profileUpdate).await()
                   currentUser.updateEmail(sup.email).await()
               }

               val editUserRef = mFirestore.collection(COLLECTION_PATH_SUP).document(sup.uid)
               editUserRef.set(sup).await()

               _updateStatus.postValue(UiState.SUCCESS)
           }
       } catch (e : Exception){
           Log.d(TAG, "Error : ${e.message}")
           _updateStatus.postValue(UiState.FAILURE)
       }
    }

    fun deleteAccount(sup: Sup){
        viewModelScope.launch(IO) {
            try {
                _deleteStatus.postValue(UiState.LOADING)
                val supId = sup.uid
                val supImagePath = "$SUP_IMAGE_STORAGE_PATH/$supId"
                mAuth.currentUser?.delete()?.await()
                mFirestore.collection(COLLECTION_PATH_SUP).document(supId).delete().await()
                mFirestore.collection(Constants.COLLECTION_PATH_ROLE).document(supId).delete().await()
                mStorage.reference.child(supImagePath).delete().await()
                _deleteStatus.postValue(UiState.SUCCESS)
            } catch (error : Exception){
                Log.d(TAG, "deleteAccount Error: ${error.message}")
                _deleteStatus.postValue(UiState.FAILURE)
            }

        }
    }

}