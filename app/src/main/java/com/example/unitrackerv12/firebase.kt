package com.example.unitrackerv12


import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.auth.User
import com.google.type.DateTime
import com.squareup.okhttp.internal.DiskLruCache

import java.time.LocalDateTime

// date: Dec 9 2021

val db: FirebaseFirestore = FirebaseFirestore.getInstance()
var auth: FirebaseAuth = FirebaseAuth.getInstance()

val TAG:String = "FIREBASEDEBUG"


/*
 * Position of a user at certain time
 */

data class Position(
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val time: Timestamp = Timestamp.now()
)

data class User(
    val userid: String,
    val name: String,
    val lastPosition: Position? = null
)

class UserManager
{
    companion object{
        @JvmField
        val collection: CollectionReference = db.collection("users")

        /*
        @JvmStatic fun getCurrentUser(): User?
        {
            var user: FirebaseUser = auth.currentUser

            if(user != null)
                return User(user)
            return null
        }

         */

        @JvmStatic fun init(userid: String, email: String)
                /*
                 * Init a document of an user with ID: userid (Note: Use this function when the user has been created (sign up))
                 */
        {
            var document = UserManager.collection.document(userid)
            //var trackedGroups = listOf<String>()
            //var belongGroups = listOf<String>()
            //lastPosition = null
            //var positions = mapOf<String, Position>()

            document.set(
                mapOf(
                    "name" to null,
                    "email" to email,
                    "lastPosition" to null,
                )
            )
                .addOnCompleteListener{
                    Log.d(TAG, "New user document was created!")
                }
                .addOnFailureListener {  e -> Log.w(TAG, "Error while creating user document", e)}
        }

        @JvmStatic fun remove(user: FirebaseUser)
                /*
                 * Remove a user from users firebase collection
                 */
        {
            UserManager.collection.document(user.uid)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "User was successfully deleted") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }

        @JvmStatic fun leftGroup(user: FirebaseUser, groupid: String)
        {

        }

        @JvmStatic fun positions(user: FirebaseUser): List<Position>
                /*
                 * Return all the positions of a user
                 */
        {
            var userPositions: List<Position> = listOf<Position>()

            // more stuff

            return userPositions
        }

        @JvmStatic fun addPosition(user: FirebaseUser?, position: Position)
                /*
                 * Add a position of a user
                 */
        {
            if(user != null)
            {

                UserManager.collection.document(user.uid).update("lastPosition", position)
                UserManager.collection.document(user.uid).collection("positions").add(position)
                    .addOnSuccessListener { Log.d(TAG, "New position successfully added!") }
                    .addOnFailureListener {  e -> Log.w(TAG, "Error adding position document", e)}
            }
        }
        @JvmStatic fun lastPosition(user: FirebaseUser?): Position?
                /*
                 * Return the last position of a user
                 */
        {
            var position: Position? = null
            if(user != null)
            {
                UserManager.collection.document(user.uid).get()
                    .addOnSuccessListener{ doc ->
                        var data = doc.data?.get("lastPosition")
                        Log.d(TAG, "Data: ${data}")
                    }
            }

            return position
        }

        @JvmStatic fun lastPosition(userid: String): Position?
                /*
                 * Return the last position of a user
                 */
        {
            var position: Position? = null
            UserManager.collection.document(userid).get()
                .addOnSuccessListener{ doc ->
                    var data = doc.data?.get("lastPosition")
                    Log.d(TAG, "Data: ${data}")
                }
            return position
        }

        @JvmStatic fun get(userid: String): User?
        {
            var user: User? = null
            UserManager.collection.document(userid).get()
                .addOnSuccessListener{ doc ->
                    var data = doc.data?.get("lastPosition")
                    Log.d(TAG, "Data: ${data}")
                    // user = SOMETING
                }

            return user
        }
    }
}

data class Group(
    var groupid: String,
    var name: String,
    var admins: List<String>? = null,
    var users: List<String>? = null
)

class GroupManager
/*
 * Group to tracking several users
 */ {

    companion object{
        @JvmField
        val collection: CollectionReference = db.collection("groups")

        @JvmStatic fun create(name: String): Group?
                /*
                 * Create a group (document) to DB
                 */
        {
            var userid: String? = auth.currentUser?.uid
            var admins: List<String> = listOf(userid!!)
            var users: List<String> = listOf()
            var group: Group? = null
            GroupManager.collection.add(
                mapOf(
                    "name" to name,
                    "admins" to admins,
                    "users" to users
                )
            )
                .addOnSuccessListener { doc ->
                    var groupid: String = doc.id
                    group = Group(groupid, name, admins, users)
                }

            return group
        }

        @JvmStatic fun isAdmin(groupid: String, userid: String): Boolean
                /*
                 * Check if a user is an administrator of the group
                 */
        {
            var belong: Boolean = false

            GroupManager.collection.document(groupid)
                .get()
                .addOnSuccessListener { doc ->
                    var admins = doc.data!!.get("admins")
                    Log.d(TAG, "admins: ${admins}")
                    /*
                    if (userid in admins)
                    {
                        belong = true
                    }
                     */
                }

            return belong
        }

        @JvmStatic fun isUser(groupid: String, userid: String): Boolean
                /*
                 * Check if a user belong to the a group
                 */
        {
            var belong: Boolean = false

            GroupManager.collection.document(groupid)
                .get()
                .addOnSuccessListener { doc ->
                    var rawadmins = doc.data!!.get("admins")
                    Log.d(TAG, "admins: ${rawadmins}")
                    //admins = doc.data.get('admins')
                }

            return belong
        }

        @JvmStatic fun users(groupid: String): List<String>
                /*
                 * Return the tracked users of a group
                 */
        {

            var users: List<String> = listOf()
            // SOME STUFF

            return users
        }

        @JvmStatic fun addAdmin(groupid: String, userid: String)
                /*
                 * Add an user to admins
                 */
        {
            var userid: String = auth.currentUser!!.uid
            if(GroupManager.isAdmin(groupid, userid)) {
                GroupManager.collection.document(groupid).update("admins", FieldValue.arrayUnion(userid))
            }
        }

        @JvmStatic fun addUser(groupid: String, userid: String)
                /*
                 * Add an user to users
                 */
        {
            var userid: String = auth.currentUser!!.uid
            if(GroupManager.isAdmin(groupid, userid)) {
                GroupManager.collection.document(groupid).update("users", FieldValue.arrayUnion(userid))
            }
        }

        @JvmStatic fun remove(groupid: String)
                /*
                 * Remove a group (document) from groups firebase collection
                 */
        {
            GroupManager.collection.document(groupid)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Group was successfully deleted") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }

        @JvmStatic fun lastPositions(groupid: String): Map<User, Position>
                /*
                 * Return the last position of all the users of this group
                 */
        {
            var last_positions: Map<User, Position> = mapOf<User, Position>()

            var userid: String = auth.currentUser!!.uid

            if (GroupManager.isAdmin(groupid, userid))
            {
                /*
                for(userid in group.users!!)
                {
                    var user: User? = UserManager.get(userid)
                    if(user != null)
                    {
                        //last_positions[user] = user.lastPosition
                    }
                }

                 */
            }

            return last_positions
        }
    }
}
