package com.example.contactsapp.data

import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.util.Log
import androidx.core.content.ContextCompat

object ContactsRepository {

    fun getContacts(context: Context, isPermissionGranted: Boolean): List<Contact> {
        val hasPermission = isPermissionGranted || ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return sampleContacts
        }

        val realContactsList = mutableListOf<Contact>()

        try {
            val contentResolver = context.contentResolver

            // 1. Fetch Emails mapped by CONTACT_ID
            val emailMap = mutableMapOf<String, String>()
            try {
                val emailCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Email.ADDRESS
                    ),
                    null,
                    null,
                    null
                )
                emailCursor?.use { cursor ->
                    val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
                    val emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                    while (cursor.moveToNext()) {
                        if (idIndex != -1 && emailIndex != -1) {
                            val contactId = cursor.getString(idIndex)
                            val email = cursor.getString(emailIndex)
                            if (!contactId.isNullOrEmpty() && !email.isNullOrEmpty() && !emailMap.containsKey(contactId)) {
                                emailMap[contactId] = email
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ContactsRepository", "Error reading emails: ${e.message}")
            }

            // 2. Fetch Addresses mapped by CONTACT_ID
            val addressMap = mutableMapOf<String, String>()
            try {
                val addressCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID,
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS
                    ),
                    null,
                    null,
                    null
                )
                addressCursor?.use { cursor ->
                    val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID)
                    val addressIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)
                    while (cursor.moveToNext()) {
                        if (idIndex != -1 && addressIndex != -1) {
                            val contactId = cursor.getString(idIndex)
                            val address = cursor.getString(addressIndex)
                            if (!contactId.isNullOrEmpty() && !address.isNullOrEmpty() && !addressMap.containsKey(contactId)) {
                                addressMap[contactId] = address
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ContactsRepository", "Error reading addresses: ${e.message}")
            }

            // 3. Fetch Phone Contacts
            val phoneCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            phoneCursor?.use { cursor ->
                val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                val addedIds = mutableSetOf<String>()

                while (cursor.moveToNext()) {
                    val id = if (idIndex != -1) cursor.getString(idIndex) ?: "" else ""
                    val name = if (nameIndex != -1) cursor.getString(nameIndex) ?: "Unknown" else "Unknown"
                    val number = if (numberIndex != -1) cursor.getString(numberIndex) ?: "" else ""

                    if (number.isNotBlank() && (id.isBlank() || !addedIds.contains(id))) {
                        if (id.isNotBlank()) addedIds.add(id)

                        val email = emailMap[id]
                        val address = addressMap[id]

                        realContactsList.add(
                            Contact(
                                id = id,
                                name = name,
                                phoneNumber = number,
                                email = email,
                                address = address
                            )
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("ContactsRepository", "Permission denied reading contacts: ${e.message}")
            return sampleContacts
        } catch (e: Exception) {
            Log.e("ContactsRepository", "Error reading contacts: ${e.message}")
        }

        return realContactsList
    }
}
