package com.example.contactsapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.contactsapp.data.Contact
import com.example.contactsapp.data.ContactsRepository
import com.example.contactsapp.ui.ContactDetailsScreen
import com.example.contactsapp.ui.MyContactsScreen
import com.example.contactsapp.ui.theme.ContactsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContactsAppTheme {
                ContactsAppMain()
            }
        }
    }
}

@Composable
fun ContactsAppMain() {
    val context = LocalContext.current
    val activity = context as? Activity

    var isPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    var contactsList by remember { mutableStateOf<List<Contact>>(emptyList()) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var permissionDeniedCount by remember { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        isPermissionGranted = isGranted
        contactsList = ContactsRepository.getContacts(context, isGranted)
        if (isGranted) {
            Toast.makeText(context, "READ_CONTACTS Permission Granted!", Toast.LENGTH_SHORT).show()
        } else {
            permissionDeniedCount++
            val showRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.READ_CONTACTS)
            } ?: true

            if (!showRationale && permissionDeniedCount > 0) {
                Toast.makeText(
                    context,
                    "Permission permanently denied. Please enable Contacts in App Info settings.",
                    Toast.LENGTH_LONG,
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Permission Denied. Tap Grant to try again.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS,
        ) == PackageManager.PERMISSION_GRANTED
        isPermissionGranted = hasPermission
        contactsList = ContactsRepository.getContacts(context, hasPermission)
    }

    if (selectedContact != null) {
        BackHandler {
            selectedContact = null
        }
    }

    Crossfade(targetState = selectedContact, label = "ScreenTransition") { contact ->
        if (contact == null) {
            MyContactsScreen(
                contacts = contactsList,
                isPermissionGranted = isPermissionGranted,
                onRequestPermission = {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_CONTACTS,
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        isPermissionGranted = true
                        contactsList = ContactsRepository.getContacts(context, isPermissionGranted = true)
                        Toast.makeText(context, "Permission is already granted!", Toast.LENGTH_SHORT).show()
                    } else {
                        val showRationale = activity?.let {
                            ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.READ_CONTACTS)
                        } ?: true

                        if (!showRationale && permissionDeniedCount > 0) {
                            Toast.makeText(
                                context,
                                "Opening App Settings to enable Contacts permission...",
                                Toast.LENGTH_LONG,
                            ).show()
                            try {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null),
                                )
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Could not open settings", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                    }
                },
                onContactClick = { chosenContact ->
                    selectedContact = chosenContact
                },
            )
        } else {
            ContactDetailsScreen(
                contact = contact,
                onBackClick = {
                    selectedContact = null
                },
            )
        }
    }
}
