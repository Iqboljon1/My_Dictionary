package com.iraimjanov.dictionary

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.iraimjanov.dictionary.adapters.TypesRecyclerViewAdapter
import com.iraimjanov.dictionary.databinding.FragmentAddEditWordsBinding
import com.iraimjanov.dictionary.db.AppDatabase
import com.iraimjanov.dictionary.models.Types
import com.iraimjanov.dictionary.models.Words
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddEditWordsFragment : Fragment() {
    private lateinit var binding: FragmentAddEditWordsBinding
    private lateinit var appDataBase: AppDatabase
    private lateinit var arrayListTypes: ArrayList<Types>
    private lateinit var arrayListTypesString: ArrayList<String>
    private lateinit var arrayAdapterTypes: ArrayAdapter<String>
    private var positionType = 0
    private var time: String =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    private var uri: Uri? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddEditWordsBinding.inflate(layoutInflater)

        loadData()


        when (mode) {
            "add" -> {
                binding.tvAddEditWord.text = "Add word"
                binding.imageDone.setOnClickListener {
                    saveWord()
                }
            }
            "edit" -> {
                binding.tvAddEditWord.text = "Edit word"
                binding.edtWordName.setText(words.name)
                binding.edtWordDescription.setText(words.description)
                binding.spinnerTypes.setText(words.type)
                Glide.with(requireActivity()).load(words.image)
                    .apply(RequestOptions().placeholder(R.drawable.ic_add_photo)).centerCrop()
                    .into(binding.imageAddPhoto)
                if (words.image != "") {
                    val layoutParams: ViewGroup.LayoutParams = binding.imageAddPhoto.layoutParams
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    binding.imageAddPhoto.layoutParams = layoutParams
                    uri = Uri.fromFile(File(words.image.toString()))
                }
                binding.imageDone.setOnClickListener {
                    editWord()
                }
            }
        }

        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cardAddPhoto.setOnClickListener {
            getImageContent.launch("image/*")
        }

        binding.imageNoImage.setOnClickListener {
            imageDismiss()
        }

        binding.spinnerTypes.setOnItemClickListener { parent, view, position, id ->
            positionType = position
        }

        return binding.root
    }

    private fun editWord() {
        val name = binding.edtWordName.text.toString().trim()
        val description = binding.edtWordDescription.text.toString().trim()
        val type = binding.spinnerTypes.text.toString().trim()
        var image = ""

        if (name.isNotEmpty() && description.isNotEmpty() && type.isNotEmpty()) {

            image = if (uri != null) {
                val inputStream = requireActivity().contentResolver?.openInputStream(uri!!)
                val file = File(requireActivity().filesDir, "${time}.jpg")
                val fileOutputStream = FileOutputStream(file)
                inputStream?.copyTo(fileOutputStream)
                inputStream?.close()
                fileOutputStream.close()
                deleteImageFromFilesDir(words.image!!)
                file.absolutePath
            }else{
                deleteImageFromFilesDir(words.image!!)
                ""
            }
            appDataBase.myDao().updateWords(Words(words.id , name,
                description,
                image,
                arrayListTypes[positionType].id.toString() , words.like))
            clearEditTextAndImageView()
            Toast.makeText(requireActivity(), "Edited", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        } else {
            Toast.makeText(requireActivity(), "Row empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteImageFromFilesDir(patch: String) {
        if (requireActivity().filesDir.isDirectory) {
            val file = File(patch)
            for (i in requireActivity().filesDir.listFiles().indices) {
                if (requireActivity().filesDir.listFiles()[i].absolutePath == file.absolutePath) {
                    requireActivity().filesDir.listFiles()[i].delete()
                    break
                }
            }
        }
    }

    private fun imageDismiss() {
        binding.imageAddPhoto.setImageResource(R.drawable.ic_add_photo)
        val layoutParams: ViewGroup.LayoutParams = binding.imageAddPhoto.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        binding.imageAddPhoto.layoutParams = layoutParams
        uri = null
    }

    private fun saveWord() {
        val name = binding.edtWordName.text.toString().trim()
        val description = binding.edtWordDescription.text.toString().trim()
        val type = binding.spinnerTypes.text.toString().trim()
        var image = ""

        if (name.isNotEmpty() && description.isNotEmpty() && type.isNotEmpty()) {

            if (uri != null) {
                val inputStream = requireActivity().contentResolver?.openInputStream(uri!!)
                val file = File(requireActivity().filesDir, "${time}.jpg")
                val fileOutputStream = FileOutputStream(file)
                inputStream?.copyTo(fileOutputStream)
                inputStream?.close()
                fileOutputStream.close()
                image = file.absolutePath
            }
            appDataBase.myDao().addWords(Words(name,
                description,
                image,
                arrayListTypes[positionType].id.toString() , "false"))
            clearEditTextAndImageView()
            Toast.makeText(requireActivity(), "Saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireActivity(), "Row empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadData() {
        appDataBase = AppDatabase.getInstance(requireActivity())
        booleanAntiBagMainThread = true

        appDataBase.myDao().getAllTypes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (booleanAntiBagMainThread) {
                    arrayListTypesString = ArrayList()
                    arrayListTypes = ArrayList()
                    arrayListTypes = it as ArrayList<Types>
                    arrayListTypesString.clear()
                    for (i in arrayListTypes) {
                        arrayListTypesString.add(i.name!!)
                    }
                    arrayAdapterTypes =
                        ArrayAdapter(requireActivity(), R.layout.item_spinner, arrayListTypesString)
                    binding.spinnerTypes.setAdapter(arrayAdapterTypes)
                    if (words.type != null){
                        for (i in 0 until  arrayListTypesString.size) {
                            if (arrayListTypesString[i] == words.type){
                                positionType = i
                            }
                        }
                    }
                }
            }
    }

    private val getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it ?: return@registerForActivityResult
        Glide.with(requireActivity()).load(it).centerCrop().into(binding.imageAddPhoto)
        val layoutParams: ViewGroup.LayoutParams = binding.imageAddPhoto.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        binding.imageAddPhoto.layoutParams = layoutParams
        uri = it
    }

    private fun clearEditTextAndImageView() {
        binding.edtWordDescription.text!!.clear()
        binding.edtWordName.text!!.clear()
        binding.spinnerTypes.text!!.clear()
        binding.imageAddPhoto.setImageResource(R.drawable.ic_add_photo)
        time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        imageDismiss()
    }

    companion object {
        var mode = ""
        var booleanAntiBagMainThread = false
        var words = Words()
    }

    override fun onStop() {
        booleanAntiBagMainThread = false
        super.onStop()
    }

}
