package com.example.contactsapp.data

data class Contact(
    val id: String = "",
    val name: String,
    val phoneNumber: String,
    val email: String? = null,
    val address: String? = null
)

val sampleContacts = listOf(
    Contact(
        id = "sample_1",
        name = "Rahul Kumar",
        phoneNumber = "9876543210",
        email = "rahul.kumar@email.com",
        address = "123 Main Street, City"
    ),
    Contact(
        id = "sample_2",
        name = "Anjali Sharma",
        phoneNumber = "9123456780",
        email = "anjali.sharma@email.com",
        address = "456 Park Avenue, City"
    ),
    Contact(
        id = "sample_3",
        name = "Aman Patel",
        phoneNumber = "9988776655",
        email = "aman.patel@email.com",
        address = "789 Lake View, City"
    ),
    Contact(
        id = "sample_4",
        name = "Priya Singh",
        phoneNumber = "9898989898",
        email = "priya.singh@email.com",
        address = "321 Hill Road, City"
    ),
    Contact(
        id = "sample_5",
        name = "Sahil Khan",
        phoneNumber = "9012345678",
        email = "sahil.khan@email.com",
        address = "654 Station Road, City"
    ),
    Contact(
        id = "sample_6",
        name = "Neha Gupta",
        phoneNumber = "9345678901",
        email = "neha.gupta@email.com",
        address = "987 Market Street, City"
    ),
    Contact(
        id = "sample_7",
        name = "Vikram Mehta",
        phoneNumber = "8765432109",
        email = "vikram.mehta@email.com",
        address = "147 Grand Trunk, City"
    ),
    Contact(
        id = "sample_8",
        name = "Karan Verma",
        phoneNumber = "9156784321",
        email = "karan.verma@email.com",
        address = "258 Ring Road, City"
    )
)
