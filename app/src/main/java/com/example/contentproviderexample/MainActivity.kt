package com.example.contentproviderexample

import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

private  val TAG = "mainActivity"
private const val REQUEST_CODE_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {
//    private var readGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val hasReadContactPermission = ContextCompat.checkSelfPermission(this, READ_CONTACTS)
        Log.d(TAG, "oncreate: chck self persmission $hasReadContactPermission")

//        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "permission granded")
////            readGranted = true
//        } else {
//            Log.d(TAG, "reqiesting permission")
//            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
//        }


        if(hasReadContactPermission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "reqiesting permission")
            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
        }

        fab.setOnClickListener { view ->
            Log.d(TAG, "fabs on click: starts")
            if(ContextCompat.checkSelfPermission(this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    projection,null,null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

                val contacts = ArrayList<String>()
                cursor?.use {
                    while (it.moveToNext()) {
                        contacts.add(it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                    }
                }

                val adapter = ArrayAdapter<String>(this, R.layout.contact_details, R.id.name, contacts)
                contact_names.adapter = adapter
            } else {
                Snackbar.make(view, "please grand", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action") {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this, READ_CONTACTS)) {
                            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
                        } else {
                            // the user has permanately denied permission
                            Log.d(TAG, "lanching settings")
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", this.packageName, null)
                            intent.data = uri
                            this.startActivity(intent)
                        }
                        Log.d(TAG, "snackbar on end ")
                    }.show()
            }
            Log.d(TAG, "fab on click ends")
        }
        Log.d(TAG, "oncrete ends")
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        Log.d(TAG, "onrequest result run")
//        when (requestCode) {
//            REQUEST_CODE_READ_CONTACTS -> {
////                readGranted = if (grantResults.isNotEmpty() &&grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                if (grantResults.isNotEmpty() &&grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.d(TAG, "granded")
//                } else {
//                    Log.d(TAG, "denied")
//                }
//            }
//        }
//        Log.d(TAG, "ends")
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
