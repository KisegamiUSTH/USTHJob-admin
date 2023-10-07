package com.usth.jobadmin.auth.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usth.jobadmin.model.Sup
import com.usth.jobadmin.util.Constants.Companion.COLLECTION_PATH_SUP
import com.usth.jobadmin.util.Constants.Companion.SUP_IMAGE_STORAGE_PATH
import com.usth.jobadmin.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserDetailViewModel : ViewModel() {
    private var imageUri: Uri? = null
    private val mStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _userUploadStatus: MutableLiveData<Resource<String>> = MutableLiveData()
    val userUploadStatus: LiveData<Resource<String>> = _userUploadStatus

    fun setImageUri(imageUri: Uri?) {
        this.imageUri = imageUri
    }

    fun getImageUri(): Uri? {
        return this.imageUri
    }

    fun uploadUserDetail(imageUri: Uri, sup: Sup) {
        viewModelScope.launch(IO) {
            try {
                _userUploadStatus.postValue(Resource.loading())
                val user = mAuth.currentUser!!
                val userId = user.uid
                sup.uid = userId

                val profileBuilder = UserProfileChangeRequest.Builder()
                val profileUpdates = profileBuilder.setPhotoUri(imageUri).build()
                user.updateProfile(profileUpdates).await()

                val imagePath = SUP_IMAGE_STORAGE_PATH + userId
                val profileRef = mStorage.reference.child(imagePath)
                profileRef.putFile(imageUri).await()
                val imageUrl = profileRef.downloadUrl.await().toString()
                sup.imageUri = imageUrl

                val supRef = mFirestore.collection(COLLECTION_PATH_SUP).document(userId)
                supRef.set(sup).await()
                _userUploadStatus.postValue(Resource.success("Upload success."))
            } catch (error : Exception) {
                val errorMessage = error.message!!
                _userUploadStatus.postValue(Resource.error(errorMessage))
            }
        }
    }

}