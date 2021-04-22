package com.example.myservice

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.myservice.databinding.FragmentMainBinding


const val REQUUEST_CODE = 42

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    fun getContacts() {
        val myContext = context
        context?.let {
            val contentResolver: ContentResolver = it.contentResolver
            val cursorWithContacts: Cursor? = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )
            cursorWithContacts?.let {cursor ->
                for (i in 0..cursor.count) {
                    if(cursor.moveToPosition(i)) {
                        val name = cursor.getString(
                                cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        )


                        val s = name
                        if (name != null) {
                            addView(it, name)
                        }


                    }
                }
            }
            cursorWithContacts?.close()
        }
    }

    @SuppressLint("ResourceType")
    fun addView(context: Context, textToShow: String) {
        binding.layoutContactsContainer.addView(AppCompatTextView(context).apply {
            text = textToShow
            textSize = resources.getDimension(R.dimen.textview_dimension)
        })
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(it,
                    Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                            getContacts()
                        }

                shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS) -> {
                        AlertDialog.Builder(it)
                            .setTitle("Доступ к контактам")
                            .setMessage("Необходимо для блокирования спам-звонком, рекламы и т.д.")
                            .setPositiveButton("Разрешить доступ") {_, _, ->
                                requestPermission()
                            }
                            .setNegativeButton("Не разрешать") {
                                my_dialog, _ -> my_dialog.dismiss()
                            }
                } else -> {
                    requestPermission()
                }
            }
        }
    }

    fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            REQUUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts()
                } else {
                    context?.let {
                        AlertDialog.Builder(it)
                            .setTitle("Доступ к контактам")
                            .setMessage("Без доступа к контактам приложегние" +
                                    "не сможет работать!")
                            .setNegativeButton("Закрыть") {
                                my_dialog, _ -> my_dialog.dismiss()
                            }.create()
                            .show()
                    }
                }
            }
        }
    }
}